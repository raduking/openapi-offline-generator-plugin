package org.oogp.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Data", description = "the data")
@RequestMapping("/api/test/data/")
public interface DataApi {

	@PostMapping(
		value = "elements",
		produces = { MediaType.APPLICATION_JSON_VALUE })
	@SuppressWarnings("unused")
	default ResponseEntity<List<String>> getElements(@RequestParam(required = false) final Integer count) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Operation(
		operationId = "getObjectAsString",
		tags = { "Data" },
		responses = {
				@ApiResponse(
					responseCode = "200",
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
					}),
				@ApiResponse(
					responseCode = "500",
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
					})
		})
	@GetMapping(
		value = "object-string",
		produces = { MediaType.APPLICATION_JSON_VALUE })
	default ResponseEntity<Object> getObjectAsString() {
		// this will generate schema with type: string because the @Schema implementation is defined as Object.class but
		// spring-doc infers string because it cannot determine a structured schema
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}

	@Operation(
		operationId = "getStringAsObject",
		tags = { "Data" },
		responses = {
				@ApiResponse(
					responseCode = "200",
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))
					}),
				@ApiResponse(
					responseCode = "500",
					content = {
							@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
					})
		})
	@GetMapping(
		value = "object-object",
		produces = { MediaType.APPLICATION_JSON_VALUE })
	default ResponseEntity<Object> getObjectAsObject() {
		// this will generate schema with type: string because the @Schema implementation is defined as Object.class but
		// spring-doc infers string because it cannot determine a structured schema
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
