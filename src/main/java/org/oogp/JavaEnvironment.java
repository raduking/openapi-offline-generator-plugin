package org.oogp;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.apiphany.lang.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility interface for Java version and environment information.
 *
 * @author Radu Sebastian LAZIN
 */
public interface JavaEnvironment {

	/**
	 * The logger.
	 */
	Logger LOGGER = LoggerFactory.getLogger(Classes.class);

	/**
	 * Detects the project's build output directory containing compiled classes.
	 * <p>
	 * This method attempts to locate the directory where compiled class files are stored by reading the
	 * 'project.build.outputDirectory' system property, which is typically set by build tools like Maven.
	 *
	 * @return a Path object representing the project's build output directory
	 * @throws IllegalStateException if the 'project.build.outputDirectory' system property is not set or is empty,
	 *     indicating the classes directory could not be detected
	 */
	public static Path detectProjectOutputDirectory() {
		String buildOutput = System.getProperty("project.build.outputDirectory");
		if (Strings.isNotEmpty(buildOutput)) {
			return Path.of(buildOutput);
		}
		LOGGER.error("Missing 'project.build.outputDirectory' property");
		throw new IllegalStateException("Could not detect project classes directory");
	}

	/**
	 * Provides detailed Java environment information using the provided message consumer.
	 *
	 * @param messageConsumer the consumer to handle java information messages
	 */
	static void info(final Consumer<String> messageConsumer) {
		List<String> properties = List.of(
				"java.version",
				"java.runtime.version",
				"java.vm.version",
				"java.vm.name",
				"java.vm.vendor",
				"java.vendor",
				"java.home",
				"java.class.version",
				"os.name",
				"os.version");

		messageConsumer.accept("Java Environment Information:");
		for (String property : properties) {
			String message = String.format("%s: %s", property, System.getProperty(property));
			messageConsumer.accept(message);
		}
	}

	/**
	 * Gets the path to the Java executable.
	 *
	 * @return the Java executable path as a String
	 */
	static String getJavaExecutablePath() {
		String javaHome = System.getProperty("java.home");
		if (javaHome == null || javaHome.isEmpty()) {
			javaHome = "java";
		}
		return javaHome + File.separator + "bin" + File.separator + "java";
	}
}
