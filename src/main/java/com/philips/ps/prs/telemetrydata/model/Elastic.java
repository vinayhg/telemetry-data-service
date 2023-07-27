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

import lombok.Data;

@Data
/**
 * 
 * @author 320086435
 *
 */
public class Elastic {

        private volatile String elasticUrl;
        private volatile String elasticPort;
        private volatile String elasticProtocol;
        private volatile String indexName;
        private volatile String typeName;
        private String upsertScriptName;

}
