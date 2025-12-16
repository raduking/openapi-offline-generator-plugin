package org.oogp.isolated.offsetdatetime.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OffsetDateTimeController.class)
class OffsetDateTimeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnOkOnValidOffsetDateTime() throws Exception {
		// Valid ISO 8601 offset date-time format
		String validDateTime = "2019-12-19T23:59:00+01:00";

		mockMvc.perform(get("/api/test/offsetdatetime/string")
				.param("dateTime", validDateTime))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dateTime").value("2019-12-19T23:59:00+01:00"))
				.andExpect(jsonPath("$.year").value(2019))
				.andExpect(jsonPath("$.month").value(12))
				.andExpect(jsonPath("$.day").value(19))
				.andExpect(jsonPath("$.hour").value(23))
				.andExpect(jsonPath("$.minute").value(59))
				.andExpect(jsonPath("$.second").value(0))
				.andExpect(jsonPath("$.offset").value("+01:00"));
	}

	@Test
	void shouldReturnBadRequestOnInvalidOffsetDateTime() throws Exception {
		// Invalid format
		String invalidDateTime = "2019-12-19 23:59:00";

		mockMvc.perform(get("/api/test/offsetdatetime/string")
				.param("dateTime", invalidDateTime))
				.andExpect(status().isBadRequest());
	}
}
