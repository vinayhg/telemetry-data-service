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
package com.philips.ps.prs.telemetrydata.controller.useast;

import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.common.core.messages.BaseErrorMessages;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.utils.IamTokenHelper;

import org.apache.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;



/**
 * Enables IoT devices to publish their telemetry data to PRS platform.
 */
@Profile("useast")
@RequestMapping(TelemetryDataConstants.BASE_URL)
@RestController
public class UsEastTelemetryDataController {
    private static final Logger LOGGER = Logger.getLogger(UsEastTelemetryDataController.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IamTokenHelper iamTokenHelper;

    @Value("${TelemetryDataEuWestUrl}")
    private String TelemetryDataEuWestUrl;

    /**
     *
     * @param telemetryDataRequest
     * @param httpRequest
     * @return
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, headers = TelemetryDataConstants.API_VERSION)
    @PreAuthorize(TelemetryDataConstants.VALIDATE_ACCESS_CREATE_OPERATION)
    public ResponseEntity<Object> saveTelemetryData(@RequestBody @Valid TelemetryDataRequest telemetryDataRequest,
                                    HttpServletRequest httpRequest) {
        LOGGER.info("Saving telemetry data for nametag " + Encode.forJava(telemetryDataRequest.getNameTag()));
        ResponseEntity<Object> response = null;
        try {
            final String requestUrl = TelemetryDataEuWestUrl.concat("/telemetry");
            LOGGER.info("Invoking Telemetry data service: " + requestUrl);
            response = restTemplate.postForEntity(requestUrl,
                                            new HttpEntity<>(telemetryDataRequest, setHeaders()), Object.class);
        } catch (Exception e) {
            LOGGER.error("Error calling POST telemetry url in eu-west : " + e.getLocalizedMessage());

            throw new InternalException(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
                                            BaseErrorMessages.COD_ERROR_INTERNALSERVER);
        }
        LOGGER.info("Successfully saved details in elastic search for "
                                        + Encode.forJava(telemetryDataRequest.getNameTag()));
        return response;
    }

    /**
     *
     * @param nameTag
     * @param productType
     * @param httpRequest
     * @return
     */

    @GetMapping(path = TelemetryDataConstants.GET_URL, produces = MediaType.APPLICATION_JSON_VALUE, headers = TelemetryDataConstants.API_VERSION)
    @PreAuthorize(TelemetryDataConstants.VALIDATE_ACCESS_READ_OPERATION)
    // TODO:The constructor HttpEntity(MultiValueMap) belongs to the raw type
    // HttpEntity
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ResponseEntity<TelemetryDataRequest> fetchTelemetryData(
                                    @PathVariable(TelemetryDataConstants.NAMETAG) String nameTag,
                                    @PathVariable(TelemetryDataConstants.PRODUCT_TYPE) String productType,
                                    HttpServletRequest httpRequest) {
        LOGGER.info("Retrieving telemetry data for nametag " + Encode.forJava(nameTag));
        ResponseEntity<TelemetryDataRequest> response = null;
        try {
            final UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("https")
                                            .host(TelemetryDataEuWestUrl.concat("/telemetry")).path("/" + nameTag + "/" + productType).build();
            response = restTemplate.exchange(Encode.forJava(uriComponents.toString()), HttpMethod.GET,
                                            new HttpEntity(setHeaders()), TelemetryDataRequest.class);
        } catch (Exception e) {
            LOGGER.error("Error calling GET telemetry url in eu-west : " + e.getLocalizedMessage());
            throw new InternalException(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
                                            BaseErrorMessages.COD_ERROR_INTERNALSERVER);
        }
        LOGGER.info("Successfully retrieved telemetry data for nametag " + Encode.forJava(nameTag));
        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    private HttpHeaders setHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(TelemetryDataConstants.API_VERSION_KEY, TelemetryDataConstants.API_VERSION_VALUE);
        headers.setBearerAuth(iamTokenHelper.getAccessTokenEuWest());
        return headers;
    }
}
