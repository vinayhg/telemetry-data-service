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
 * The CodeEnum
 */
public enum CodeEnum {

    INVALID("invalid"),

    PROCESSING("processing"),

    INFORMATIONAL("informational"),

    TIMEOUT("timeout");

    private String code;

    CodeEnum(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

}
