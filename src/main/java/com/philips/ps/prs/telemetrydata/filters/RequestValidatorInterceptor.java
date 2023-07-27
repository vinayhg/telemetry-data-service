/**
* (C) Koninklijke Philips Electronics N.V. 2020
*
* All rights are reserved. Reproduction or transmission in whole or in part,
* in  any form or by any means, electronic, mechanical or otherwise, is
* prohibited without the prior written permission of the copyright owner.
* 
* @author Saumya Mahajan
* Filename   : RequestValidatorInterceptor.java
* 
*/
package com.philips.ps.prs.telemetrydata.filters;

import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

@Component("requestValidatorInterceptorLocal")
@Primary
/**
 * @description Holds thread context with context id
 */
public class RequestValidatorInterceptor implements HandlerInterceptor {
    @Value("${displayName}")
    private String displayName;

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        ThreadContext.put(TelemetryDataConstants.TRANSACTION_ID_KEY, UUID.randomUUID().toString());
        ThreadContext.put(TelemetryDataConstants.CUSTOM_LOG_EVENT_KEY, TelemetryDataConstants.CUSTOM_LOG_EVENT_VALUE);
        ThreadContext.put(TelemetryDataConstants.SERVICE_NAME_KEY, Encode.forJava(displayName));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        ThreadContext.clearAll();
    }
}
