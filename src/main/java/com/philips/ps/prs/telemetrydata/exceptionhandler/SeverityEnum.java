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
 * The SeverityEnum
 */
public enum SeverityEnum {
    FATAL("fatal"),

    WARNING("warning"),

    INFORMATION("information"),

    ERROR("error");

    private String severity;

    SeverityEnum(String severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return severity;
    }
}
