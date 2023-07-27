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
package com.philips.ps.prs.telemetrydata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.service.TelemetryDataService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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



@Profile("euwest")
@RequestMapping(TelemetryDataConstants.BASE_URL)
@RestController
/**
 * Enables IoT devices to publish their telemetry data to PRS platform.
 */
public class TelemetryDataController {
    private static final Logger LOGGER = Logger.getLogger(TelemetryDataController.class);
    @Autowired
    private TelemetryDataService telemetryService;

    /**
     * 
     * @param telemetryDataRequest
     * @param httpRequest
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, headers = TelemetryDataConstants.API_VERSION)
    @PreAuthorize(TelemetryDataConstants.VALIDATE_ACCESS_CREATE_OPERATION)
    public ResponseEntity<Object> saveTelemetryData(@RequestBody @Valid TelemetryDataRequest telemetryDataRequest,
            HttpServletRequest httpRequest) throws JsonProcessingException {
        LOGGER.info("Saving telemetry data for nametag " + Encode.forJava(new ObjectMapper().writeValueAsString(telemetryDataRequest)));
        telemetryService.upsertTelemetryData(telemetryDataRequest);
        LOGGER.info("Successfully saved details in elastic search for "
                + Encode.forJava(telemetryDataRequest.getNameTag()));
        return new ResponseEntity<>(HttpStatus.CREATED);
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
    public ResponseEntity<TelemetryDataRequest> fetchTelemetryData(
            @PathVariable(TelemetryDataConstants.NAMETAG) String nameTag,
            @PathVariable(TelemetryDataConstants.PRODUCT_TYPE) String productType,
            HttpServletRequest httpRequest) {
        LOGGER.info("Retrieving telemetry data for nametag " + Encode.forJava(nameTag));
        final TelemetryDataRequest telemetryDataResponse = telemetryService.getTelemetryData(nameTag, productType);
        LOGGER.info("Successfully retrieved telemetry data for nametag " + Encode.forJava(nameTag));
        return new ResponseEntity<>(telemetryDataResponse, HttpStatus.OK);
    }
}
