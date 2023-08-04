package com.philips.ps.prs.telemetrydata.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.common.vault.core.VaultHelper;
import com.philips.ps.prs.common.vault.model.VaultSensitiveData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringJUnitConfig(IamHelperTests.IAMHelperTestConfiguration.class)
public class IamHelperTests {
    @TestConfiguration
    static class IAMHelperTestConfiguration {
        @Bean
        public IamTokenHelper iamHelper() {
            return new IamTokenHelper();
        }
    }

    @Autowired
    private IamTokenHelper iamHelper;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    VaultHelper vaultHelper;
    @MockBean
    VaultSensitiveData vaultSensitiveData;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(iamHelper, "vaultIamServiceCredentialsPath", "url");
        ReflectionTestUtils.setField(iamHelper, "iamglobalRegionUrl", "url");
        ReflectionTestUtils.setField(iamHelper, "iamApiVersionKey", "key");
        ReflectionTestUtils.setField(iamHelper, "iamApiVersionValue", "val");
    }

    @Test
    public void getAccessTokenSuccess() {
        HashMap<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "alpha_protocol");
        when(vaultHelper.retrieve(ArgumentMatchers.anyString())).thenReturn(vaultSensitiveData);
        when(vaultSensitiveData.getKey()).thenReturn("key");
        when(vaultSensitiveData.getValue()).thenReturn("value");
        when(restTemplate.postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpEntity.class),
                                        ArgumentMatchers.eq(Map.class))).thenReturn(tokenResponse);
        assertEquals("alpha_protocol", iamHelper.getAccessToken());
    }

    @Test
    public void getAccessTokenFail() {
        HashMap<String, String> tokenResponse = new HashMap<>();
        when(vaultHelper.retrieve(ArgumentMatchers.anyString())).thenReturn(vaultSensitiveData);
        when(vaultSensitiveData.getKey()).thenReturn("key");
        when(vaultSensitiveData.getValue()).thenReturn("value");
        tokenResponse.put("access_token", "alpha_protocol");
        when(restTemplate.postForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpEntity.class),
                                        ArgumentMatchers.eq(Map.class))).thenThrow(RestClientException.class);
        assertThrows(InternalException.class, () -> iamHelper.getAccessToken());
    }
}
