package com.philips.ps.prs.telemetrydata.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.telemetrydata.config.ElasticRestClient;
import com.philips.ps.prs.telemetrydata.config.TelemetryDataConfig;
import com.philips.ps.prs.telemetrydata.model.Elastic;
import com.philips.ps.prs.telemetrydata.model.TelemetryData;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.service.impl.TelemetryDataServiceImpl;
import com.philips.ps.prs.telemetrydata.utils.TelemetryTransformHelper;

@SpringJUnitConfig(TelemetryDataServiceImplTest.config.class)
class TelemetryDataServiceImplTest {
    @TestConfiguration
    static class config {
        @Bean
        public TelemetryDataService telemetryDataService() {
            return new TelemetryDataServiceImpl();
        }
    }

    @Autowired
    private TelemetryDataServiceImpl telemetryDataServiceImpl;
    @MockBean
    private TelemetryDataConfig telemetryDataConfig;
    @MockBean
    private ElasticRestClient elasticRestClient;
    @MockBean
    private TelemetryTransformHelper transformHelper;
    @MockBean
    RestTemplate restTemplate;

    public Elastic getElasticDetails() {
        Elastic elastic = new Elastic();
        elastic.setElasticPort("");
        elastic.setElasticProtocol("");
        elastic.setElasticUrl("");
        elastic.setIndexName("");
        elastic.setTypeName("");
        return elastic;
    }

    @Test
    public void getTelemetryDataTests() {
        when(telemetryDataConfig.getElasticDetails()).thenReturn(getElasticDetails());
        when(elasticRestClient.elasticCredentials()).thenReturn("");
        HttpHeaders header = new HttpHeaders();
        ResponseEntity<String> responseEntity = new ResponseEntity<>(JSON(), header, HttpStatus.CREATED);
        when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntity);
        telemetryDataServiceImpl.getTelemetryData("", "");
    }

    @Test
    public void getTelemetryDataTestsFail() {
        when(telemetryDataConfig.getElasticDetails()).thenReturn(getElasticDetails());
        when(elasticRestClient.elasticCredentials()).thenReturn("");
        HttpHeaders header = new HttpHeaders();
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", header, HttpStatus.CREATED);
        when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntity);
        assertThrows(InternalException.class, () -> telemetryDataServiceImpl.getTelemetryData("", ""));
    }

    @Test
    public void getTelemetryDataTestsFailStatus() {
        HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);
        when(telemetryDataConfig.getElasticDetails()).thenReturn(getElasticDetails());
        when(elasticRestClient.elasticCredentials()).thenReturn("");
        HttpHeaders header = new HttpHeaders();
        when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any()))
                .thenThrow(httpClientErrorException);
        assertThrows(InternalException.class, () -> telemetryDataServiceImpl.getTelemetryData("", ""));
    }

    @Test
    public void insertTelemetryDataTests() {
        TelemetryDataRequest telemetryDataRequest = getTelemetryDataRequest();
        when(telemetryDataConfig.getElasticDetails()).thenReturn(getElasticDetails());
        when(elasticRestClient.elasticCredentials()).thenReturn("");
        HttpHeaders header = new HttpHeaders();
        ResponseEntity<String> responseEntity = new ResponseEntity<>(JSON(), header, HttpStatus.CREATED);
        when(restTemplate.exchange(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any()))
                .thenReturn(responseEntity);
        telemetryDataServiceImpl.upsertTelemetryData(telemetryDataRequest);
    }

    @Test
    public void insertTelemetryDataTestsFail() {
        TelemetryDataRequest telemetryDataRequest = getTelemetryDataRequest();
        when(telemetryDataConfig.getElasticDetails()).thenReturn(getElasticDetails());
        when(elasticRestClient.elasticCredentials()).thenReturn("");
        HttpHeaders header = new HttpHeaders();
        when(restTemplate.postForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any(),
                ArgumentMatchers.<Class<String>>any()))
                .thenThrow(new RuntimeException("index_not_found_exception"));
        assertThrows(InternalException.class, () -> telemetryDataServiceImpl.upsertTelemetryData(telemetryDataRequest));
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