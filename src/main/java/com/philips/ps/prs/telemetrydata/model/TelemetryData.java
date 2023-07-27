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
package com.philips.ps.prs.telemetrydata.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
/**
 * POJO for the request object.
 */
public class TelemetryData {

    @NotBlank(message = "Name is Required")
    private String name;

    @NotBlank(message = "Value is Required")
    private String value;

    @NotBlank(message = "Timestamp is Required")
    private String timestamp;

}
