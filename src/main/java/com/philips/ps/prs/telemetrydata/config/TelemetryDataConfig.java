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

import com.philips.ps.prs.telemetrydata.model.Elastic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * The config class.
 */
@Configuration
@ConfigurationProperties(prefix = "config")
public class TelemetryDataConfig {
    @Value("${ps.prs.telemetrydata.scope.post}")
    private String writeScope;
    @Value("${ps.prs.telemetrydata.scope.get}")
    private String readScope;
    @Value("${config.elastic.url}")
    private String elasticUrl;
    @Value("${config.elastic.index-name}")
    private String elasticIndexName;
    @Value("${config.elastic.type-name}")
    private String typeName;
    @Value("${config.elastic.upsert-script-name}")
    private String upsertScriptName;

    /**
     * Scope map.
     *
     * @return the map
     */
    @Bean
    public Map<String, String> scopeMap() {
        final Map<String, String> scopeMap = new HashMap<>();
        scopeMap.put("post", writeScope);
        scopeMap.put("get", readScope);
        return scopeMap;
    }

    @Bean
    public Elastic getElasticDetails() {
        Elastic elastic = new Elastic();
        elastic.setElasticUrl(elasticUrl);
        elastic.setIndexName(elasticIndexName);
        elastic.setTypeName(typeName);
        elastic.setUpsertScriptName(upsertScriptName);
        return elastic;
    }
}
