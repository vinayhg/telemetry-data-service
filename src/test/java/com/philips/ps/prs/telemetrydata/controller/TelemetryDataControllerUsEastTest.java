package com.philips.ps.prs.telemetrydata.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.telemetrydata.controller.useast.UsEastTelemetryDataController;
import com.philips.ps.prs.telemetrydata.model.TelemetryData;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.utils.IamTokenHelper;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@SpringJUnitConfig(TelemetryDataControllerUsEastTest.config.class)
public class TelemetryDataControllerUsEastTest {
    @TestConfiguration
    static class config {
        @Bean
        public com.philips.ps.prs.telemetrydata.controller.useast.UsEastTelemetryDataController usEastTelemetryDataController() {
            return new com.philips.ps.prs.telemetrydata.controller.useast.UsEastTelemetryDataController();
        }
    }

    @Autowired
    private UsEastTelemetryDataController usEastTelemetryDataController;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private IamTokenHelper iamTokenHelper;
    @MockBean
    HttpServletRequest httpRequest;

    @Test
    public void saveTelemetryDataTest() {
        ReflectionTestUtils.setField(usEastTelemetryDataController, "TelemetryDataEuWestUrl", "url");
        TelemetryDataRequest telemetryDataRequest = getTelemetryDataRequest();
        usEastTelemetryDataController.saveTelemetryData(telemetryDataRequest, httpRequest);
    }

    @Test
    public void saveTelemetryDataTestThrowException() {
        ResponseEntity<TelemetryDataRequest> responseEntity = new ResponseEntity<TelemetryDataRequest>(
                                        HttpStatus.CREATED);
        when( restTemplate.postForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any(),
                                        ArgumentMatchers.eq(Object.class))).thenThrow(new RuntimeException(""));
        Exception exception = assertThrows(InternalException.class, () -> {
            usEastTelemetryDataController.saveTelemetryData(getTelemetryDataRequest(), httpRequest);
        });
    }


    @Test
    public void fetchTelemetryDataTest() {
        ReflectionTestUtils.setField(usEastTelemetryDataController, "TelemetryDataEuWestUrl", "url");
        HttpHeaders header = new HttpHeaders();
        ResponseEntity<TelemetryDataRequest> responseEntity = new ResponseEntity<TelemetryDataRequest>(HttpStatus.CREATED);
        when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
                                        ArgumentMatchers.any(), ArgumentMatchers.<Class<TelemetryDataRequest>>any()))
                                                                        .thenReturn(responseEntity);
        usEastTelemetryDataController.fetchTelemetryData("", "", httpRequest);
    }
    @Test
    public void fetchTelemetryDataTestFail() {
        assertThrows(NullPointerException.class, ()->usEastTelemetryDataController.fetchTelemetryData("", "", httpRequest));
    }

    @Test
    public void fetchTelemetryDataTestThrowException() {
        when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
                                        ArgumentMatchers.any(), ArgumentMatchers.<Class<TelemetryDataRequest>>any()))
                                                                        .thenThrow(new RuntimeException(""));
        Exception exception = assertThrows(InternalException.class, () -> {
            usEastTelemetryDataController.fetchTelemetryData("", "", httpRequest);
        });

    }

    public TelemetryDataRequest getTelemetryDataRequest() {
        TelemetryDataRequest telemetryDataRequest = new TelemetryDataRequest();
        telemetryDataRequest.setNameTag("");
        telemetryDataRequest.setProductType("");
        TelemetryData telemetryData = new TelemetryData();
        telemetryData.setName("");
        telemetryData.setTimestamp("");
        telemetryData.setValue("");
        List<TelemetryData> list = new ArrayList<TelemetryData>();
        list.add(telemetryData);
        telemetryDataRequest.setData(list);
        return telemetryDataRequest;
    }
    public String JSON() {
        return "{\r\n" + "  \"_index\" : \"telemetry-dev-test2\",\r\n" + "  \"_type\" : \"_doc\",\r\n"
                                        + "  \"_id\" : \"test2\",\r\n" + "  \"_version\" : 1,\r\n"
                                        + "  \"_seq_no\" : 0,\r\n" + "  \"_primary_term\" : 1,\r\n"
                                        + "  \"found\" : true,\r\n" + "  \"_source\" : {\r\n"
                                        + "    \"nameTag\" : \"test2\",\r\n" + "    \"productType\" : \"test2\",\r\n"
                                        + "    \"data\" : [\r\n" + "      {\r\n"
                                        + "        \"name\" : \"RiseInHelium2\",\r\n"
                                        + "        \"value\" : \"test2\",\r\n"
                                        + "        \"timestamp\" : \"05-05-2022 03:36:45\"\r\n" + "      }\r\n"
                                        + "    ],\r\n" + "    \"RiseInHelium2\" : \"test2\",\r\n"
                                        + "    \"timestamp\" : \"2022-05-24T05:53:13.705258400Z\"\r\n" + "  }\r\n"
                                        + "}\r\n" + "";
    }
}
