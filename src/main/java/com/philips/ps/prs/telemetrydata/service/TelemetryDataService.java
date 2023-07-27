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
package com.philips.ps.prs.telemetrydata.service;

import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.model.TelemetryRequest;

/**
 * Connects to elastic search and performs various required operations.
 */
public interface TelemetryDataService {

    /**
     * Insert telemetry data.
     *
     * @param telemetryDataRequest the telemetry request
     */
    void upsertTelemetryData(TelemetryDataRequest telemetryDataRequest);

    /**
     * Gets the telemetry data.
     *
     * @param nameTag the name tag
     * @return the telemetry data
     */
    TelemetryDataRequest getTelemetryData(String nameTag, String productType);

}