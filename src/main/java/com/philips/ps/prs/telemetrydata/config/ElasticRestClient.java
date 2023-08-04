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
package com.philips.ps.prs.telemetrydata.config;

import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.common.core.messages.BaseErrorMessages;
import com.philips.ps.prs.common.vault.core.VaultHelper;
import com.philips.ps.prs.common.vault.model.VaultSensitiveData;

import org.apache.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

/**
 * Provides details to the rest client to connect to elastic search
 */
@Component
@Profile("${spring.profiles.active}")
public class ElasticRestClient {
    private static final Logger LOGGER = Logger.getLogger(ElasticRestClient.class);
    @Value("${prs.vault.elasticCredentialsVaultPath}")
    private String elasticCredentialsVaultPath;
    @Autowired
    private VaultHelper vaultHelper;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public String elasticCredentials() {
        VaultSensitiveData data = vaultHelper.retrieve(elasticCredentialsVaultPath);
        if (data == null) {
            LOGGER.info("Vault returned empty or null. VaultPath :" + Encode.forJava(elasticCredentialsVaultPath));
            throw new InternalException(String.format("Vault path %s returned empty or null",
                                            elasticCredentialsVaultPath), BaseErrorMessages.COD_ERROR_INTERNALSERVER);
        }
        String userName = data.getKey();
        String passWord = data.getValue();
        final String kibanaCredentials = userName.concat(":").concat(passWord);
        return "Basic " + Base64.getEncoder().encodeToString(kibanaCredentials.getBytes());
    }
}
