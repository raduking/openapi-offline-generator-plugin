package org.oogp.offsetdatetime.api;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	@Operation(
		operationId = "getStringAsObject",
		tags = { "Object" },
		parameters = {
				@Parameter(
					name = "dateTime",
					description = "Input date time",
					schema = @Schema(
						implementation = OffsetDateTime.class,
						pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}$",
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
		value = "string",
		produces = { MediaType.APPLICATION_JSON_VALUE })
	default ResponseEntity<Object> getStringAsObject(@SuppressWarnings("unused") final OffsetDateTime dateTime) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
