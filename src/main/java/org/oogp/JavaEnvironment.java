package org.oogp;

import java.util.List;
import java.util.function.Consumer;

/**
 * Utility interface for Java version and environment information.
 *
 * @author Radu Sebastian LAZIN
 */
public interface JavaEnvironment {

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
}
