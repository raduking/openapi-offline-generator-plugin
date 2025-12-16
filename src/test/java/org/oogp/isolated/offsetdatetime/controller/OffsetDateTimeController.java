package org.oogp.isolated.offsetdatetime.controller;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.oogp.isolated.offsetdatetime.api.OffsetDateTimeApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class OffsetDateTimeController implements OffsetDateTimeApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(OffsetDateTimeController.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public ResponseEntity<Object> getObjectWithOffsetDateTime(final OffsetDateTime dateTime) {
		LOGGER.info("Received OffsetDateTime: {}", dateTime);

		ObjectNode response = createResponseNode(dateTime);

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Object> getObjectWithStringTime(final String dateTime) {
		LOGGER.info("Received dateTime string: {}", dateTime);
		OffsetDateTime parsedDateTime = OffsetDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		ObjectNode response = createResponseNode(parsedDateTime);

		return ResponseEntity.ok(response);
	}

	/**
	 * Returns a JSON object node with the details of the OffsetDateTime for testing purposes.
	 *
	 * @param dateTime the OffsetDateTime to extract details from
	 * @return an ObjectNode containing the dateTime details
	 */
	private ObjectNode createResponseNode(final OffsetDateTime dateTime) {
		// IMPORTANT: Format the OffsetDateTime back to ISO 8601 string for consistent output not dependent on toString()
		String formattedDateTime = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		ObjectNode response = objectMapper.createObjectNode();
		response.put("dateTime", formattedDateTime);
		response.put("year", dateTime.getYear());
		response.put("month", dateTime.getMonthValue());
		response.put("day", dateTime.getDayOfMonth());
		response.put("hour", dateTime.getHour());
		response.put("minute", dateTime.getMinute());
		response.put("second", dateTime.getSecond());
		response.put("offset", dateTime.getOffset().toString());
		return response;
	}
}
