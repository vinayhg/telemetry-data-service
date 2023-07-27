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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
/**
 * POJO for the params object.
 */
public class TelemetryDataRequest {

    @NotBlank(message = "NameTag is Required")
    private String nameTag;

    @NotBlank(message = "ProductType is Required")
    private String productType;
    
    @JsonInclude(JsonInclude.Include.NON_NULL) 
    private String timestamp;

    @NotEmpty
    @Valid
    private List<TelemetryData> data;

}
