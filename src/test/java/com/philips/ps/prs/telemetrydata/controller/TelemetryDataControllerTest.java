package com.philips.ps.prs.telemetrydata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.philips.ps.prs.common.audit.core.AuditHelper;
import com.philips.ps.prs.telemetrydata.exceptionhandler.TelemetryDataException;
import com.philips.ps.prs.telemetrydata.model.TelemetryData;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.service.TelemetryDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.ArrayList;
import java.util.List;


@SpringJUnitConfig(TelemetryDataControllerTest.config.class)
class TelemetryDataControllerTest {

    @TestConfiguration
    static class config {
        @Bean
        public TelemetryDataController telemetryDataController() {
            return new TelemetryDataController();
        }
    }

    @Autowired
    TelemetryDataController telemetryDataController;

    @MockBean
    TelemetryDataService telemetryDataService;

    @MockBean
    private AuditHelper auditHelper;

    @MockBean
    private HttpServletRequest httpRequest;

    @Test
    void saveTelemetryDataSuccessTest() throws JsonProcessingException {
        doNothing().when(telemetryDataService).upsertTelemetryData(Mockito.any(TelemetryDataRequest.class));
        ResponseEntity response = telemetryDataController.saveTelemetryData(getTelemetryDataRequest(), httpRequest);
        verify(telemetryDataService, times(1)).upsertTelemetryData(Mockito.any(TelemetryDataRequest.class));
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void saveTelemetryDataExceptionTest() {
        doThrow(TelemetryDataException.class).when(telemetryDataService)
                .upsertTelemetryData(Mockito.any(TelemetryDataRequest.class));
        assertThrows(TelemetryDataException.class, () -> {
            telemetryDataController.saveTelemetryData(getTelemetryDataRequest(), httpRequest);
        });
    }

    @Test
    void fetchTelemetryDataSuccessTest() {
        when(telemetryDataService.getTelemetryData("ACV_Test", "ABC")).thenReturn(getTelemetryDataRequest());
        ResponseEntity<TelemetryDataRequest> response = telemetryDataController.fetchTelemetryData("ACV_Test", "ABC",
                httpRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getTelemetryDataRequest().getData().size(), response.getBody().getData().size());
        verify(telemetryDataService, times(1)).getTelemetryData("ACV_Test", "ABC");
    }

    private TelemetryDataRequest getTelemetryDataRequest() {
        TelemetryDataRequest telemetryDataRequest = new TelemetryDataRequest();
        telemetryDataRequest.setNameTag("ACV_Test");
        telemetryDataRequest.setProductType("ABC");
        List<TelemetryData> telemetryDataList = new ArrayList<>();
        TelemetryData data1 = new TelemetryData();
        data1.setName("name1");
        data1.setValue("value1");
        data1.setTimestamp("timestamp");
        telemetryDataList.add(data1);
        telemetryDataRequest.setData(telemetryDataList);
        return telemetryDataRequest;
    }

}
