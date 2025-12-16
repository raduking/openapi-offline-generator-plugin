package org.oogp;

/**
 * Exception class for generator-related errors.
 *
 * @author Radu Sebastian LAZIN
 */
public class GeneratorException extends RuntimeException {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4336559629781875288L;

	/**
	 * Constructs a new {@link GeneratorException} with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public GeneratorException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link GeneratorException} with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause the cause of the exception
	 */
	public GeneratorException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
