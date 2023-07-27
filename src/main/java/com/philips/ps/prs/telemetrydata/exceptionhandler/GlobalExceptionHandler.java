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
package com.philips.ps.prs.telemetrydata.exceptionhandler;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import com.philips.ps.prs.common.core.dto.outcome.OperationOutcome;
import com.philips.ps.prs.common.core.exception.BaseApplicationException;
import com.philips.ps.prs.common.core.logging.JsonHelper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Custom Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
                                implements com.philips.ps.prs.common.core.security.ExceptionHandler {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class);
    /** The message converter. */
    private final HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();


    /**
     * Handle exception internal.
     *
     * @param ex      the ex
     * @param body    the body
     * @param headers the headers
     * @param statusCode  the statusCode
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Object response = body;
        if (!(body instanceof OperationOutcome)) {
            response = OperationOutcomeFactory.getExceptionOutcome(ex);
        }
        LOGGER.error(JsonHelper.serialize(response));
        return new ResponseEntity<>(response, new HttpHeaders(), statusCode);
    }

    /**
     * Handle expected exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler({ BaseApplicationException.class })
    protected ResponseEntity<Object> handleExpectedException(BaseApplicationException ex, WebRequest request) {
        HttpStatus status = resolveAnnotatedResponseStatus(ex);
        OperationOutcome body = OperationOutcomeFactory.getBaseApplicationExceptionOutcome(ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    /**
     * Handle all other exception.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler({ Throwable.class })
    public ResponseEntity<Object> handleAllOtherException(Exception ex, WebRequest request) {
        if (ex instanceof BaseApplicationException) {
            return handleExpectedException((BaseApplicationException) ex, request);
        }
        HttpStatus status = resolveAnnotatedResponseStatus(ex);
        OperationOutcome body = OperationOutcomeFactory.getUnexpectedErrorOutcome(ex, String.valueOf(status.value()));
        return handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    /**
     * Handle security exception.
     *
     * @param bEx      the b ex
     * @param response the response
     */
    // This is called from the Spring Security Exception handlers
    @Override
    @SuppressWarnings({ "unchecked" })
    public void handleSecurityException(BaseApplicationException bEx, jakarta.servlet.http.HttpServletResponse response) {
        try {
            OperationOutcome error = OperationOutcomeFactory.getBaseApplicationExceptionOutcome(bEx);
            ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
            outputMessage.setStatusCode(GlobalExceptionHandler.resolveAnnotatedResponseStatus(bEx));
            httpMessageConverter.write(error, null, outputMessage);
            LOGGER.error(JsonHelper.serialize(error));
        } catch (Exception ex) {
            LOGGER.error(convertStackTraceToString(ex));
        }
    }

    /**
     * Handle no handler found exception.
     *
     * @param ex      the ex
     * @param headers the headers
     * @param status  the status
     * @param request the request
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Object response = OperationOutcomeFactory.getExceptionOutcome(ex);
        LOGGER.error(JsonHelper.serialize(response));
        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    /**
     * Resolve annotated response status.
     *
     * @param exception the exception
     * @return the http status
     */
    public static HttpStatus resolveAnnotatedResponseStatus(Exception exception) {
        ResponseStatus annotation = findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Convert stack trace to string.
     *
     * @param ex the ex
     * @return the string
     */
    private String convertStackTraceToString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String sStackTrace = sw.toString();
        String exception = sStackTrace.replaceAll("[\t\n\r\"]", " ");
        return exception;
    }
}
