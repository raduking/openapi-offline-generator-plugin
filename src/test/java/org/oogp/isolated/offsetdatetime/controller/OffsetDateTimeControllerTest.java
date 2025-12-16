package org.oogp.isolated.offsetdatetime.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OffsetDateTimeController.class)
class OffsetDateTimeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Provides a stream of valid ISO 8601 offset date-time strings for parameterized tests.
	 *
	 * @return a stream of valid offset date-time strings
	 */
	private static Stream<Arguments> provideValidOffsetDateTimes() {
		return Stream.of(
				Arguments.of("2019-12-19T23:59:00+01:00", 2019, 12, 19, 23, 59, 0, "+01:00"),
				Arguments.of("2019-12-19T23:59+01:00", 2019, 12, 19, 23, 59, 0, "+01:00"),
				Arguments.of("2021-06-15T12:30:45-05:00", 2021, 6, 15, 12, 30, 45, "-05:00"),
				Arguments.of("2020-01-01T00:00:00Z", 2020, 1, 1, 0, 0, 0, "Z"));
	}

	/**
	 * Provides a stream of invalid ISO 8601 offset date-time strings for parameterized tests.
	 *
	 * @return a stream of invalid offset date-time strings
	 */
	private static Stream<Arguments> provideInvalidOffsetDateTimes() {
		return Stream.of(
				Arguments.of("2019-12-19T23:59:00+0100"),
				Arguments.of("2019-12-19T23:59+0100"),
				Arguments.of("2019-12-19 23:59:00"));
	}

	@ParameterizedTest
	@MethodSource("provideValidOffsetDateTimes")
	void shouldReturnOkOnValidOffsetDateTimeWhenCallingWithOffsetDateTime(
			final String validDateTime,
			final int year,
			final int month,
			final int day,
			final int hour,
			final int minute,
			final int second,
			final String offset) throws Exception {
		OffsetDateTime parsedDateTime = OffsetDateTime.parse(validDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		String formattedDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(parsedDateTime);

		mockMvc.perform(get("/api/test/offsetdatetime/with-offsetdatetime")
				.param("dateTime", validDateTime))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dateTime").value(formattedDateTime))
				.andExpect(jsonPath("$.year").value(year))
				.andExpect(jsonPath("$.month").value(month))
				.andExpect(jsonPath("$.day").value(day))
				.andExpect(jsonPath("$.hour").value(hour))
				.andExpect(jsonPath("$.minute").value(minute))
				.andExpect(jsonPath("$.second").value(second))
				.andExpect(jsonPath("$.offset").value(offset));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidOffsetDateTimes")
	void shouldReturnBadRequestOnInvalidOffsetDateTimeWhenCallingWithOffsetDateTime(final String invalidDateTime) throws Exception {
		mockMvc.perform(get("/api/test/offsetdatetime/with-offsetdatetime")
				.param("dateTime", invalidDateTime))
				.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@MethodSource("provideValidOffsetDateTimes")
	void shouldReturnOkOnValidOffsetDateTimeWhenCallingWithStringTime(
			final String validDateTime,
			final int year,
			final int month,
			final int day,
			final int hour,
			final int minute,
			final int second,
			final String offset) throws Exception {
		OffsetDateTime parsedDateTime = OffsetDateTime.parse(validDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		String formattedDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(parsedDateTime);

		mockMvc.perform(get("/api/test/offsetdatetime/with-stringtime")
				.param("dateTime", validDateTime))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.dateTime").value(formattedDateTime))
				.andExpect(jsonPath("$.year").value(year))
				.andExpect(jsonPath("$.month").value(month))
				.andExpect(jsonPath("$.day").value(day))
				.andExpect(jsonPath("$.hour").value(hour))
				.andExpect(jsonPath("$.minute").value(minute))
				.andExpect(jsonPath("$.second").value(second))
				.andExpect(jsonPath("$.offset").value(offset));
	}

	@ParameterizedTest
	@MethodSource("provideInvalidOffsetDateTimes")
	void shouldReturnBadRequestOnInvalidOffsetDateTimeWhenCallingWithStringTime(final String invalidDateTime) throws Exception {
		mockMvc.perform(get("/api/test/offsetdatetime/with-stringtime")
				.param("dateTime", invalidDateTime))
				.andExpect(status().isBadRequest());
	}
}
