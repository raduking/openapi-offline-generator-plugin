package org.oogp;

import java.util.Map;

import org.morphix.lang.Enums;

/**
 * Represents the standard project type.
 *
 * @author Radu Sebastian LAZIN
 */
public enum ProjectType {

	/**
	 * The Spring project type.
	 */
	SPRING("spring"),

	/**
	 * The Jakarta project type.
	 */
	JAKARTA("jakarta");

	/**
	 * The name map for easy from string implementation.
	 */
	private static final Map<String, ProjectType> NAME_MAP = Enums.buildNameMap(values());

	/**
	 * The {@link String} value.
	 */
	private final String value;

	/**
	 * Constructs an {@link ProjectType} with the specified string value.
	 *
	 * @param value string value
	 */
	ProjectType(final String value) {
		this.value = value;
	}

	/**
	 * Returns the string value.
	 *
	 * @return the string value
	 */
	public String value() {
		return value;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return value();
	}

	/**
	 * Returns a {@link ProjectType} enum from a {@link String}.
	 *
	 * @param method HTTP method as string
	 * @return an HTTP method enum
	 */
	public static ProjectType fromString(final String method) {
		return Enums.fromString(method, NAME_MAP, values());
	}

}
