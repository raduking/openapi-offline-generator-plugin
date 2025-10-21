package org.oogp;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Data", description = "the data")
public interface DataApi {

	@RequestMapping(
		method = RequestMethod.POST,
		value = "/api/test/data/elements",
		produces = { "application/json" })
	default ResponseEntity<List<String>> _getElements(@RequestParam(required = false) final Integer count) {
		return getElements(count);
	}

	@SuppressWarnings("unused")
	default ResponseEntity<List<String>> getElements(@RequestParam(required = false) final Integer count) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
	}
}
