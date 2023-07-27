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
package com.philips.ps.prs.telemetrydata.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.philips.ps.prs.common.core.exception.extended.InternalException;
import com.philips.ps.prs.common.core.messages.BaseErrorMessages;
import com.philips.ps.prs.telemetrydata.config.ElasticRestClient;
import com.philips.ps.prs.telemetrydata.config.TelemetryDataConfig;
import com.philips.ps.prs.telemetrydata.constants.TelemetryDataConstants;
import com.philips.ps.prs.telemetrydata.exceptionhandler.TelemetryDataException;
import com.philips.ps.prs.telemetrydata.model.TelemetryDataRequest;
import com.philips.ps.prs.telemetrydata.model.TelemetryRequest;
import com.philips.ps.prs.telemetrydata.model.TelemetryScript;
import com.philips.ps.prs.telemetrydata.service.TelemetryDataService;
import com.philips.ps.prs.telemetrydata.utils.TelemetryTransformHelper;

import org.apache.log4j.Logger;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Map;

@Profile("euwest")
@Service
/**
 * Connects to elastic search and performs various required operations.
 */
public class TelemetryDataServiceImpl implements TelemetryDataService {
	@Autowired
	private TelemetryDataConfig applicationProperties;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ElasticRestClient elasticRestClient;
	private static final Logger LOGGER = Logger.getLogger(TelemetryDataServiceImpl.class);

	/**
	 * 
	 * @param productType
	 * @return
	 */
	private String getIndexName(String productType) {
		LOGGER.info("Fetching the index name for the productType : " + Encode.forJava(productType));
		String indexInLower = applicationProperties.getElasticDetails().getIndexName().toLowerCase();
		return String.format("%s-%s", indexInLower, productType.trim().toLowerCase());
	}

	/**
	 * 
	 * @param telemetryDataRequest // Get existing Telemetry Document if any //
	 *                             Create Doc // Update Doc
	 * 
	 */
	@Override
	public void upsertTelemetryData(TelemetryDataRequest telemetryDataRequest) throws TelemetryDataException {
		final String telemetryRequest = buildTelemetryUpsertRequestBody(telemetryDataRequest);
		final String indexName = getIndexName(telemetryDataRequest.getProductType());

		try {
			final String upsertEndpoint = applicationProperties.getElasticDetails().getElasticUrl().concat("/").concat(indexName)
					.concat("/").concat(TelemetryDataConstants.UPSERT_PATH).concat("/")
					.concat(telemetryDataRequest.getNameTag());
			LOGGER.debug(telemetryRequest);
			final HttpHeaders headers = getHttpHeaders();
			final HttpEntity<String> httpEntity = new HttpEntity<>(telemetryRequest, headers);
			restTemplate.postForEntity(Encode.forJava(upsertEndpoint), httpEntity, String.class);
			LOGGER.info(String.format("Successfully indexed telemetry document '%s' into '%s'",
					Encode.forJava(telemetryDataRequest.getNameTag().trim()), Encode.forJava(indexName)));
		} catch (Exception e) {
			LOGGER.error(String.format("An error occurred while indexing telemetry document - %s into %s. %s",
					Encode.forJava(telemetryDataRequest.getNameTag()), Encode.forJava(indexName), e.getMessage()));
			throw new InternalException(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
					BaseErrorMessages.COD_ERROR_INTERNALSERVER);
		}
	}

	private String buildTelemetryUpsertRequestBody(TelemetryDataRequest telemetryDataRequest) {
		final TelemetryRequest telemetryRequest = new TelemetryRequest();
		final TelemetryScript telemetryScript = new TelemetryScript();
		telemetryScript.setId(applicationProperties.getElasticDetails().getUpsertScriptName());
		telemetryDataRequest.setTimestamp(Instant.now().toString());
		telemetryScript.setParams(telemetryDataRequest);
		telemetryRequest.setScript(telemetryScript);
		telemetryRequest.setScripted_upsert(true);
		telemetryRequest.setUpsert(new JsonObject());
		final String jsonString = TelemetryTransformHelper.transform(telemetryRequest);
		return jsonString;

	}

	/**
	 * 
	 * @param indexName
	 * @param nameTag
	 * @return fetches the telemetry document from elastic search.
	 */
	private Map<String, Object> getTelemetryDocument(String indexName, String nameTag) {
		try {
			final String fetchDocumentUrl = applicationProperties.getElasticDetails().getElasticUrl() + "/" + indexName
					+ "/" + applicationProperties.getElasticDetails().getTypeName() + "/" + nameTag;
			final HttpHeaders headers = getHttpHeaders();
			final HttpEntity<String> entity = new HttpEntity<>(headers);
			final ResponseEntity<String> response = restTemplate.exchange(fetchDocumentUrl, HttpMethod.GET, entity,
					String.class);
			final ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
			});
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
				LOGGER.error(
						String.format("An Elastic exception occurred while fetching the telemetry document (%s). %s",
								Encode.forJava(nameTag), ex.getMessage()));
				throw new InternalException(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
						BaseErrorMessages.COD_ERROR_INTERNALSERVER, ex);
			}
		} catch (Exception exception) {
			LOGGER.error(String.format("An Elastic exception occurred while fetching the telemetry document (%s). %s",
					Encode.forJava(nameTag), exception.getMessage()));
			throw new InternalException(BaseErrorMessages.MSG_ERROR_INTERNALSERVER,
					BaseErrorMessages.COD_ERROR_INTERNALSERVER, exception);
		}
		return null;
	}

	/**
	 * Gets the telemetry data.
	 *
	 * @param nameTag the name tag
	 * @return the telemetry data Get existing Telemetry Document if any
	 */
	@Override
	public TelemetryDataRequest getTelemetryData(String nameTag, String productType) {
		final String indexName = getIndexName(productType);
		final Map<String, Object> telemetryDoc = getTelemetryDocument(indexName, nameTag.trim());
		if (telemetryDoc == null) {
			LOGGER.info(
					String.format("No document found with id '%s' in '%s' index", Encode.forJava(nameTag), indexName));
			return null;
		} else {
			LOGGER.info(String.format("Found document with id '%s' in '%s' index", Encode.forJava(nameTag), indexName));
		}
		return TelemetryTransformHelper.transform(extractSourceMessage(telemetryDoc));
	}

	/**
	 * 
	 * @return prepares headers
	 */
	private HttpHeaders getHttpHeaders() {
		final HttpHeaders headers = new HttpHeaders();
		headers.set(TelemetryDataConstants.CONTENT_TYPE, TelemetryDataConstants.APPLICATION_JSON);
		headers.set(TelemetryDataConstants.AUTHORIZATION, elasticRestClient.elasticCredentials());
		return headers;
	}

	/**
	 * 
	 * @param telemetryDoc
	 * @return extract the response JSON from source in document.
	 */
	// TODO:The expression of type Map needs unchecked conversion to conform to
	// Map<String,Object>
	@SuppressWarnings("unchecked")
	private Map<String, Object> extractSourceMessage(Map<String, Object> telemetryDoc) {
		Map<String, Object> map = null;
		for (Map.Entry<String, Object> entry : telemetryDoc.entrySet()) {
			if (entry.getKey().equals(TelemetryDataConstants.SOURCE)) {
				final ObjectMapper mapper = new ObjectMapper();
				map = mapper.convertValue(entry.getValue(), Map.class);
			}
		}
		return map;
	}
}
