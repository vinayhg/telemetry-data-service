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
package com.philips.ps.prs.telemetrydata.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.dhp.audit.model.EventActionCode;
import com.philips.dhp.audit.model.EventOutcomeIndicator;
import com.philips.ps.prs.common.audit.core.AuditHelper;
import com.philips.ps.prs.common.auth.dto.IntrospectResponse;
import com.philips.ps.prs.common.core.security.AuthenticationResponseHandler;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;



/**
 * class to handle authentication response.
 */
@Component
public class PublisherAuthenticationResponseHandler implements AuthenticationResponseHandler {

    /** The audit helper. */
    @Autowired
    private AuditHelper auditHelper;

    /** The http request. */
    @Autowired
    private HttpServletRequest httpRequest;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LogManager.getLogger(PublisherAuthenticationResponseHandler.class);


    /**
     * Publish.
     *
     * @param authentication       the authentication
     * @param authenticationResult the authentication result
     * @param exception            the exception
     */
    @Override
    public void publish(Authentication authentication, boolean authenticationResult,
            AuthenticationException exception) {
        try {
            if (authenticationResult) {
                final BearerTokenAuthentication oauth = (BearerTokenAuthentication) authentication;
                String response = ((OAuth2AccessToken) oauth.getCredentials()).getTokenValue();
                final IntrospectResponse introspectResponse = MAPPER.readValue(response, IntrospectResponse.class);
                if (introspectResponse.isNewToken()) {
                    // replacing the service account character (@) with (.)
                    final String serviceAccount = oauth.getPrincipal().toString().replace('@', '.');

                    auditHelper.audit(httpRequest.getRemoteAddr().replaceAll(":", "-"), EventActionCode.Read,
                            serviceAccount, TelemetryDataConstants.SERVICE_NAME, TelemetryDataConstants.COMPONENT_NAME,
                            null, TelemetryDataConstants.ACCESS, "", TelemetryDataConstants.DICOM_CODE_APPLICATION,
                            EventOutcomeIndicator.NominalSuccess);
                }
            } else {

                auditHelper.audit(httpRequest.getRemoteAddr().replaceAll(":", "-"), EventActionCode.Read,
                        "Invalid Access Token", TelemetryDataConstants.SERVICE_NAME, TelemetryDataConstants.COMPONENT_NAME,
                        null, TelemetryDataConstants.ACCESS, "", TelemetryDataConstants.DICOM_CODE_APPLICATION,
                        EventOutcomeIndicator.NominalSuccess);
            }
        }catch (JsonProcessingException ex) {
            LOGGER.error("Error publishing token response" + ex.getLocalizedMessage());
        }

    }
}