package org.oogp.myservice.controller;

import java.util.List;

import org.oogp.myservice.api.DataApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController implements DataApi {

	private final String name;

	public DataController(final String name) {
		this.name = name;
	}

	@Override
	public ResponseEntity<List<String>> getElements(@RequestParam(required = false) final Integer count) {
		List<String> result = List.of("one", "two", "three");
		return ResponseEntity.ok(result);
	}

	public String getName() {
		return name;
	}
}
