/**
 * 
 */
package com.philips.ps.prs.telemetrydata.model;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 320073421
 *
 */

@Getter
@Setter
/**
 * POJO for the request object.
 */
public class TelemetryRequest {
	
	private boolean scripted_upsert;
	
	private JsonObject upsert;
	
	private TelemetryScript script;

}
