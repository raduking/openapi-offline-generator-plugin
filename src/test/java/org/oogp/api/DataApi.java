package org.oogp.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
