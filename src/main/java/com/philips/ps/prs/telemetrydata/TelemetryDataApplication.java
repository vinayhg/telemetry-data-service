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
package com.philips.ps.prs.telemetrydata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * The main class TelemetryDataApplication.
 */
@ComponentScan("com.philips.ps.prs")
@SpringBootApplication
public class TelemetryDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemetryDataApplication.class, args);
    }
}
