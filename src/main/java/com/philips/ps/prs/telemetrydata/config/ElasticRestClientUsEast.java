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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Provides details to the rest client to connect to elastic search
 */
@Component
@Profile("useast")
public class ElasticRestClientUsEast {
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
