package org.oogp.offsetdatetime.controller;

import org.oogp.offsetdatetime.api.OffsetDateTimeApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class OffsetDateTimeController implements OffsetDateTimeApi {

	@Override
	public ResponseEntity<Object> getStringAsObject(OffsetDateTime dateTime) {
		return OffsetDateTimeApi.super.getStringAsObject(dateTime);
	}

}
