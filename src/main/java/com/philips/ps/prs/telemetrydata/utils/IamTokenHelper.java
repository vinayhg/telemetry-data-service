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
package com.philips.ps.prs.telemetrydata.utils;

import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.common.core.messages.BaseErrorMessages;
import com.philips.ps.prs.common.vault.core.VaultHelper;
import com.philips.ps.prs.common.vault.model.VaultSensitiveData;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
/**
 * Helps generating token.
 */
public class IamTokenHelper {

    @Value("${iamDetails.euwest.url}")
    private String iamEuWestUrl;

    @Value("${iamDetails.euwest.apiVersionKey}")
    private String iamApiVersionKey;

    @Value("${iamDetails.euwest.apiVersionValue}")
    private String iamApiVersionValue;

    @Value("${prs.vault.iamServiceGlobalClientCredentialsPath}")
    private String vaultIamServiceCredentialsPath;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VaultHelper vaultHelper;

    private static final String IAM_REQUEST_BODY = "grant_type=client_credentials";
    private static final Logger LOGGER = Logger.getLogger(IamTokenHelper.class);

    /**
     * 
     * @return gets token for eu-west.
     */
    public String getAccessTokenEuWest() {
        try {
            final VaultSensitiveData vaultSensitiveData = vaultHelper.retrieve(vaultIamServiceCredentialsPath);
            checkEmptyVaultData(vaultSensitiveData);
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set(iamApiVersionKey, iamApiVersionValue);
            headers.setBasicAuth(vaultSensitiveData.getKey(), vaultSensitiveData.getValue());
            final HttpEntity<String> request = new HttpEntity<>(IAM_REQUEST_BODY, headers);
            return restTemplate.postForObject(iamEuWestUrl.concat("/authorize/oauth2/token"), request, Map.class).get(TelemetryDataConstants.ACCESS_TOKEN)
                    .toString();
        } catch (RestClientException exception) {
            LOGGER.error("Exception occured while generating IAM access token." + exception.getLocalizedMessage());
            throw new InternalException(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
                    BaseErrorMessages.COD_ERROR_INTERNALSERVER, exception);
        }
    }

    public void checkEmptyVaultData(VaultSensitiveData vaultSensitiveData) {
        if (ObjectUtils.isEmpty(vaultSensitiveData)) {
            LOGGER.error("Vault Sensitive Data is empty or null, Couldn't hit the IAM Service.");
        }
    }
}
