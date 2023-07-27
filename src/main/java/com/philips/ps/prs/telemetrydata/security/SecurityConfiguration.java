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

import com.philips.ps.prs.common.auth.introspection.TokenAuthenticationConverter;
import com.philips.ps.prs.common.auth.introspection.TokenIntrospector;
import com.philips.ps.prs.common.core.security.filter.CustomSecurityFilter;
import com.philips.ps.prs.common.core.security.handlers.GenericAuthenticationEntryPoint;
import com.philips.ps.prs.common.core.security.handlers.GenericAuthenticationResponseHandler;
import com.philips.ps.prs.telemetrydata.exceptionhandler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Configures Security settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${ps.prs.retrieveEventpathSaveTelemetryData}")
    private String retrieveEventpathSaveTelemetryData;

    @Value("${ps.prs.retrieveEventpathGetTelemetryData}")
    private String retrieveEventpathGetTelemetryData;

    /** The environment. */
    private Environment environment;

    /** The exception handler. */
    private GlobalExceptionHandler exceptionHandler;

    /** The authentication response handler. */
    private PublisherAuthenticationResponseHandler authenticationResponseHandler;

    /** The Constant ANTPATTERN_SUFFIX. */
    private static final String ANTPATTERN_SUFFIX = "*//**";

    /** The introspect token services. */
    private TokenIntrospector introspector;
    private TokenAuthenticationConverter authenticationConverter;
    /** The authentication entry point. */
    private final GenericAuthenticationEntryPoint authenticationEntryPoint = new GenericAuthenticationEntryPoint();
    /** The event publisher. */
    private final GenericAuthenticationResponseHandler eventPublisher = new GenericAuthenticationResponseHandler();
    /** The custom security filter. */
    private CustomSecurityFilter customSecurityFilter;
    @Value("${hsdp.iam.introspect.url}")
    private String introspectionUri;
    @Value("${hsdp.iam.introspect.serviceclient.id}")
    private String clientId;
    @Value("${hsdp.iam.introspect.serviceclient.secret}")
    private String clientSecret;
    private static final Logger logger = Logger.getLogger(SecurityConfiguration.class);

    public SecurityConfiguration(Environment environment, GlobalExceptionHandler exceptionHandler,
                                 PublisherAuthenticationResponseHandler authenticationResponseHandler,
                                 CustomSecurityFilter customSecurityFilter, TokenIntrospector introspector,
                                 TokenAuthenticationConverter authenticationConverter) {
        this.environment = environment;
        this.exceptionHandler = exceptionHandler;
        this.authenticationResponseHandler = authenticationResponseHandler;
        this.customSecurityFilter = customSecurityFilter;
        this.introspector = introspector;
        this.authenticationConverter = authenticationConverter;

    }

    /**
     * Registration.
     *
     * @param customSecurityFilter the custom security filter
     * @return the filter registration bean
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FilterRegistrationBean registration(CustomSecurityFilter customSecurityFilter) {
        final FilterRegistrationBean registration = new FilterRegistrationBean(customSecurityFilter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Configure.
     *
     * @param http the http
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        authenticationEntryPoint.setExceptionHandler(exceptionHandler);

        eventPublisher.setAuthenticationResponseHandler(authenticationResponseHandler);

        // config for security. Defines which paths receive authentication and what
        // authorities.
            http.cors().and().authorizeHttpRequests(auth -> auth.requestMatchers(new OAuthRequestedMatcher())
                            .authenticated()
                            .requestMatchers(HttpMethod.POST,
                                    String.format("%s%s", retrieveEventpathSaveTelemetryData, ANTPATTERN_SUFFIX))
                            .authenticated()
                            .requestMatchers(HttpMethod.GET,
                                    String.format("%s%s", retrieveEventpathGetTelemetryData, ANTPATTERN_SUFFIX))
                            .authenticated())
                    .exceptionHandling().and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and().formLogin().disable()
                    .logout().disable().headers().httpStrictTransportSecurity().includeSubDomains(true)
                    .maxAgeInSeconds(31536000).and().and().oauth2ResourceServer(oauth -> oauth.opaqueToken((opaque) -> opaque.introspector(introspector)
                                    .authenticationConverter(authenticationConverter))
                            .authenticationEntryPoint(authenticationEntryPoint));

            if (environment.getProperty("security.require-ssl", Boolean.class, Boolean.FALSE)) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
            http.addFilterBefore(customSecurityFilter, WebAsyncManagerIntegrationFilter.class);
            return http.build();
    }

    /**
     * class to match OAuth request.
     */
    private static class OAuthRequestedMatcher implements RequestMatcher {

        /**
         * Matches.
         *
         * @param request the request
         * @return true, if successful
         */
        public boolean matches(HttpServletRequest request) {
            final String auth = request.getHeader("Authorization");
            // Determine if the client request contained an OAuth Authorization token
            final boolean haveOauth2Token = (auth != null) && auth.startsWith("Bearer");
            final boolean haveAccessToken = request.getParameter("access_token") != null;
            return haveOauth2Token || haveAccessToken;
        }
    }

}
