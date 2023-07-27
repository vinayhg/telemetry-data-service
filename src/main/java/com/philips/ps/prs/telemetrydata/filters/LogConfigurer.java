/**
* (C) Koninklijke Philips Electronics N.V. 2020
*
* All rights are reserved. Reproduction or transmission in whole or in part,
* in  any form or by any means, electronic, mechanical or otherwise, is
* prohibited without the prior written permission of the copyright owner.
* 
* @author Saumya Mahajan
* Filename   : LogConfigurer.java
* 
*/
package com.philips.ps.prs.telemetrydata.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/**
 * Configuration to enable logging
 */
public class LogConfigurer implements WebMvcConfigurer {

    private static final String PATH_PATTERNS = "/**";

    @Autowired
    @Qualifier("requestValidatorInterceptorLocal")
    private RequestValidatorInterceptor requestValidatorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestValidatorInterceptor).addPathPatterns(PATH_PATTERNS);
    }
}
