package org.oogp.isolated.object.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Object", description = "the data")
@RequestMapping("/api/test/object/")
public interface ObjectApi {

	@Operation(
		operationId = "getStringAsObject",
		tags = { "Object" },
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
	default ResponseEntity<Object> getStringAsObject() {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
