package org.oogp;

import org.apiphany.json.JsonBuilder;
import org.morphix.reflection.Constructors;
import org.oogp.jakarta.OpenApiSpecJakartaGenerator;
import org.oogp.spring.OpenApiSpecSpringDocGenerator;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Main class for OpenAPI generation used in the CLI mode.
 *
 * @author Radu Sebastian LAZIN
 */
public class OpenApiGenerator {

	/**
	 * Main method.
	 *
	 * @param args the command line arguments
	 * @throws Exception in case of errors
	 */
	static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Expected path to properties JSON");
			System.exit(2);
		}

		Path propertiesPath = Path.of(args[0]);
		String json = Files.readString(propertiesPath);
		GeneratorProperties properties = JsonBuilder.fromJson(json, GeneratorProperties.class);
		properties.applyDefaults(null, null);

		switch (ProjectType.fromString(properties.getProjectType())) {
			case JAKARTA -> OpenApiSpecJakartaGenerator.generate(properties);
			case SPRING -> OpenApiSpecSpringDocGenerator.generate(properties);
			default -> throw new RuntimeException("Unknown project type: " + properties.getProjectType());
		}
	}

	/**
	 * Hide constructor.
	 */
	private OpenApiGenerator() {
		throw Constructors.unsupportedOperationException();
	}
}
