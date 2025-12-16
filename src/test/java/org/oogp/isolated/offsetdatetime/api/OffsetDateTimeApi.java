package org.oogp.isolated.offsetdatetime.api;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Object", description = "the data")
@RequestMapping("/api/test/offsetdatetime/")
public interface OffsetDateTimeApi {

	/**
	 * Flexible pattern to match ISO 8601.
	 * <p>
	 * Examples of valid formats:
	 * <ul>
	 * <li>2023-08-15T14:30:00+02:00</li>
	 * <li>2023-08-15T14:30+02:00</li>
	 * <li>2023-08-15T14:30:45-02:00</li>
	 * <li>2023-08-15T14:30Z</li>
	 * </ul>
	 */
	String ISO_8601_DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?([+-]\\d{2}:\\d{2}|Z)$";

	@Operation(
		operationId = "getObjectWithOffsetDateTime",
		tags = { "Object" },
		parameters = {
				@Parameter(
					name = "dateTime",
					description = "Input date time",
					schema = @Schema(
						implementation = OffsetDateTime.class,
						pattern = ISO_8601_DATE_TIME_PATTERN,
						example = "2019-12-19T23:59:00+01:00"))
		},
		responses = {
				@ApiResponse(
					responseCode = "200",
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
					})
		})
	@GetMapping(
		value = "with-offsetdatetime",
		produces = { MediaType.APPLICATION_JSON_VALUE })
	default ResponseEntity<Object> getObjectWithOffsetDateTime(@SuppressWarnings("unused") final OffsetDateTime dateTime) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Operation(
		operationId = "getObjectWithStringTime",
		tags = { "Object" },
		parameters = {
				@Parameter(
					name = "dateTime",
					description = "Input date time",
					schema = @Schema(
						implementation = String.class,
						pattern = ISO_8601_DATE_TIME_PATTERN,
						example = "2019-12-19T23:59:00+01:00"))
		},
		responses = {
				@ApiResponse(
					responseCode = "200",
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
					})
		})
	@GetMapping(
		value = "with-stringtime",
		produces = { MediaType.APPLICATION_JSON_VALUE })
	default ResponseEntity<Object> getObjectWithStringTime(
			@SuppressWarnings("unused") @Validated @Pattern(
				regexp = ISO_8601_DATE_TIME_PATTERN,
				message = "Invalid date-time format. Expected: ISO 8601") final String dateTime) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
