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

/**
 * The custom exception handling class.
 */
public class TelemetryDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final SeverityEnum severity;

    private final CodeEnum code;

    private final ReasonEnum reason;

    private final String reasonText;

    private final Throwable thrownException;

    public TelemetryDataException(String reasonText, SeverityEnum severity, CodeEnum code, ReasonEnum reason) {
        super();
        this.severity = severity;
        this.code = code;
        this.reason = reason;
        this.reasonText = reasonText;
        thrownException = null;
    }

    public TelemetryDataException(String reasonText, SeverityEnum severity, CodeEnum code, ReasonEnum reason,
            Throwable thrownException) {
        super();
        this.severity = severity;
        this.code = code;
        this.reason = reason;
        this.reasonText = reasonText;
        this.thrownException = thrownException;
    }

    public ReasonEnum getReason() {
        return reason;
    }

    public CodeEnum getCode() {
        return code;
    }

    public SeverityEnum getSeverity() {
        return severity;
    }

    public String getReasonText() {
        return reasonText;
    }

    public Throwable getThrownException() {
        return thrownException;
    }

}
