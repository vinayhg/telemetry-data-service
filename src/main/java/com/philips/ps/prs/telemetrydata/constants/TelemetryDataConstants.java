/**
* (C) Koninklijke Philips Electronics N.V. 2021
*
* All rights are reserved. Reproduction or transmission in whole or in part,
* in  any form or by any means, electronic, mechanical or otherwise, is
* prohibited without the prior written permission of the copyright owner.
* 
* @author Saumya Mahajan
* 
*/
package com.philips.ps.prs.telemetrydata.constants;

/**
 * The Class TelemetryDataConstants declare all constants used in the
 * application.
 */
public class TelemetryDataConstants {

    private TelemetryDataConstants() {
        throw new IllegalStateException("Error initializing TelemetryDataConstants");
    }

    public static final String BASE_URL = "telemetry";
    public static final String UPSERT_PATH = "_update";
    public static final String GET_URL = "/{nameTag}/{productType}";
    public static final String API_VERSION = "API-Version=1";
    public static final String VALIDATE_ACCESS_CREATE_OPERATION = "validateAccess('post')";
    public static final String VALIDATE_ACCESS_READ_OPERATION = "validateAccess('get')";
    public static final String SERVICE_NAME = "telemetry-data-service";
    public static final String COMPONENT_NAME = "telemetry-data-service";
    public static final String DICOM_CODE_APPLICATION_ACTIVITY = "11001";
    public static final String DICOM_CODE_APPLICATION_START = "11002";
    public static final String DICOM_CODE_APPLICATION = "11003";
    public static final String ACCESS = "ACCESS";
    public static final String TRANSACTION_ID_KEY = "transactionId";
    public static final String CUSTOM_LOG_EVENT_KEY = "customLogEvent";
    public static final String CUSTOM_LOG_EVENT_VALUE = "CustomLogEvent";
    public static final String SERVICE_NAME_KEY = "serviceName";
    public static final String NAMETAG = "nameTag";
    public static final String PRODUCT_TYPE = "productType";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String API_VERSION_KEY = "API-Version";
    public static final String API_VERSION_VALUE = "1";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String UTC = "UTC";

    public static final String FIELD_NAMETAG = "nameTag";
    public static final String FIELD_PRODUCTTYPE = "productType";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String AUTHORIZATION = "Authorization";
    public static final String SOURCE = "_source";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String INDEX_NOT_FOUND = "index_not_found_exception";
}
