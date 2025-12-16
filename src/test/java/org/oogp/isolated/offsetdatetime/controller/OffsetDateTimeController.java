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
	public ResponseEntity<Object> getStringAsObject(final OffsetDateTime dateTime) {
		LOGGER.info("Received OffsetDateTime: {}", dateTime);

		// IMPORTANT: Format the OffsetDateTime back to ISO 8601 string for consistent output not dependent on toString()
		String formattedDateTime = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		// return the parsed dateTime for testing
		ObjectNode response = objectMapper.createObjectNode();
		response.put("dateTime", formattedDateTime);
		response.put("year", dateTime.getYear());
		response.put("month", dateTime.getMonthValue());
		response.put("day", dateTime.getDayOfMonth());
		response.put("hour", dateTime.getHour());
		response.put("minute", dateTime.getMinute());
		response.put("second", dateTime.getSecond());
		response.put("offset", dateTime.getOffset().toString());

		return ResponseEntity.ok(response);
	}

}
