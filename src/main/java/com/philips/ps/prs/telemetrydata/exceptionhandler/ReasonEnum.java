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
 * The ReasonEnum
 */
public enum ReasonEnum {

    INTERNAL_SERVER_ERROR("MSG_ERROR_INTERNALSERVER"),

    UNSUPPORTED_MEDIATYPE("MSG_INVALID_MEDIATYPE"),

    BAD_REQUEST("MSG_INVALID_REQUESTFORMAT"),

    INVALID_METHOD("MSG_INVALID_METHOD"),

    UNROUTABLE_REQUEST("MSG_UNROUTABLE_REQUEST"),

    UNPROCESSABLE_ENTITY("MSG_INVALID_REQUEST");

    private String reason;

    ReasonEnum(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return reason;
    }
}
