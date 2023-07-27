package com.philips.ps.prs.telemetrydata.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import com.philips.ps.prs.telemetrydata.model.TelemetryData;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.model.TelemetryRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper Class performs various required operations.
 */
public class TelemetryTransformHelper {
    public TelemetryTransformHelper() {
    }

    /**
     * Decorates Telemetry Data by promoteing data fields to root level. Also adds
     * timestamp field with current DateTime
     * 
     * @param telemetryDataRequest
     * @return String
     */
    public static String transform(TelemetryRequest telemetryRequest) {
        final Gson gson = new Gson();
        final String jsonString = gson.toJson(telemetryRequest);
        final JsonObject jsonObject = (JsonObject) JsonParser.parseString(jsonString);
        return gson.toJson(jsonObject);
    }

    /**
     * 
     * @param telemetryDocument
     * @return
     */
    public static TelemetryDataRequest transform(Map<String, Object> telemetryDocument) {
        final TelemetryDataRequest telemetryDataRequest = new TelemetryDataRequest();
        telemetryDataRequest.setProductType(telemetryDocument.get(TelemetryDataConstants.FIELD_PRODUCTTYPE).toString());
        telemetryDataRequest.setNameTag(telemetryDocument.get(TelemetryDataConstants.FIELD_NAMETAG).toString());
        final List<TelemetryData> dataList = new ArrayList<>();
        // TODO:ArrayList is a raw type.
        @SuppressWarnings({ "rawtypes", "unchecked" })
        List<HashMap<String, Object>> dataMaps = (ArrayList) telemetryDocument.get(TelemetryDataConstants.FIELD_DATA);
        for (HashMap<String, Object> dataMap : dataMaps) {
            final TelemetryData data = new TelemetryData();
            data.setName(dataMap.get(TelemetryDataConstants.FIELD_NAME).toString());
            data.setValue(dataMap.get(TelemetryDataConstants.FIELD_VALUE).toString());
            data.setTimestamp(dataMap.get(TelemetryDataConstants.FIELD_TIMESTAMP).toString());
            dataList.add(data);
        }
        telemetryDataRequest.setData(dataList);
        return telemetryDataRequest;
    }
}
