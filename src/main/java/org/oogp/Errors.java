package org.oogp;

import org.morphix.reflection.Constructors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Errors utility class.
 *
 * @author Radu Sebastian LAZIN
 */
public class Errors {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Conversions.class);

	/**
	 * Hide constructor.
	 */
	private Errors() {
		throw Constructors.unsupportedOperationException();
	}

	/**
	 * Throws an UnsupportedOperationException and logs the error. This utility method is used to indicate that a particular
	 * operation is not supported or not yet implemented.
	 *
	 * @param <T> the return type (never actually returned due to exception)
	 *
	 * @return never returns as it always throws an exception
	 * @throws UnsupportedOperationException always thrown to indicate unsupported operation
	 */
	public static <T> T unsupportedOperation() {
		var e = new UnsupportedOperationException();
		LOGGER.error("Error", e);
		throw e;
	}
}
