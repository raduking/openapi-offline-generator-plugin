package org.oogp.object.controller;

import org.oogp.object.api.ObjectApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ObjectController implements ObjectApi {

	private final String name;

	public ObjectController(final String name) {
		this.name = name;
	}

	@Override
	public ResponseEntity<Object> getStringAsObject() {
		return ObjectApi.super.getStringAsObject();
	}

	public String getName() {
		return name;
	}
}
