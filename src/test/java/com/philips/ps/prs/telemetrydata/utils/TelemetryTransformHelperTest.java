package com.philips.ps.prs.telemetrydata.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import com.philips.ps.prs.telemetrydata.model.TelemetryData;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.model.TelemetryRequest;
import com.philips.ps.prs.telemetrydata.model.TelemetryScript;

@SpringJUnitConfig(TelemetryTransformHelperTest.TelemetryTransformHelperTestConfiguration.class)
@SuppressWarnings("unchecked")
class TelemetryTransformHelperTest {
    @TestConfiguration
    static class TelemetryTransformHelperTestConfiguration {
        @Bean
        public TelemetryTransformHelper telemetryTransformHelper() {
            return new TelemetryTransformHelper();
        }
    }

    @Autowired
    TelemetryTransformHelper transformHelper;

    TelemetryRequest createTestTelemetryDataRequest() {

        TelemetryRequest telemetryRequest = new TelemetryRequest();
        TelemetryScript telemetryScript = new TelemetryScript();
        telemetryScript.setId("test-script");
        TelemetryDataRequest telemetryDataRequest = new TelemetryDataRequest();
        telemetryDataRequest.setTimestamp(Instant.now().toString());

        telemetryRequest.setScript(telemetryScript);
        telemetryRequest.setScripted_upsert(true);

        List<TelemetryData> dataList = new ArrayList<TelemetryData>();
        TelemetryData data = new TelemetryData();
        data.setName("telemetryProperty");
        data.setValue("1000");
        data.setTimestamp("2020-12-10T13:45:00.000Z");
        dataList.add(data);
        telemetryDataRequest.setData(dataList);
        telemetryScript.setParams(telemetryDataRequest);
        return telemetryRequest;
    }

    @Test
    void testTransformTelemetryData() {
        TelemetryRequest telemetryRequest = createTestTelemetryDataRequest();
        String jsonString = transformHelper.transform(telemetryRequest);
        assertNotNull(jsonString);
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonString);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.has("scripted_upsert"));
        assertNotNull(jsonObject.get("script"));
    }

    @Test
    void testTransformTelemetryDocument() {
        Map<String, Object> telemetryDocument = new HashMap();
        telemetryDocument.put(TelemetryDataConstants.FIELD_PRODUCTTYPE, "testProductType");
        telemetryDocument.put(TelemetryDataConstants.FIELD_NAMETAG, "testNameTag");
        List<HashMap> dataMaps = new ArrayList<HashMap>();
        HashMap dataMap = new HashMap();
        dataMap.put(TelemetryDataConstants.FIELD_NAME, "telemetryProperty");
        dataMap.put(TelemetryDataConstants.FIELD_VALUE, "1000");
        dataMap.put(TelemetryDataConstants.FIELD_TIMESTAMP, "2020-12-10T13:45:00.000Z");
        dataMaps.add(dataMap);
        telemetryDocument.put(TelemetryDataConstants.FIELD_DATA, dataMaps);
        TelemetryDataRequest telemetryDataRequest = transformHelper.transform(telemetryDocument);
        assertNotNull(telemetryDataRequest);
        assertEquals("testProductType", telemetryDataRequest.getProductType());
        assertEquals("testNameTag", telemetryDataRequest.getNameTag());
        assertEquals("telemetryProperty", telemetryDataRequest.getData().get(0).getName());
        assertEquals("1000", telemetryDataRequest.getData().get(0).getValue());
        assertEquals("2020-12-10T13:45:00.000Z", telemetryDataRequest.getData().get(0).getTimestamp());
    }
}