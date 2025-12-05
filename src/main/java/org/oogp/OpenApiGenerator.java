package org.oogp;

import com.fasterxml.jackson.databind.ObjectMapper;
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

		ObjectMapper mapper = new ObjectMapper();
		Path propertiesPath = Path.of(args[0]);
		String json = Files.readString(propertiesPath);
		GeneratorProperties props = mapper.readValue(json, GeneratorProperties.class);
		props.applyDefaults(null, null);

		switch (ProjectType.fromString(props.getProjectType())) {
			case JAKARTA -> OpenApiSpecJakartaGenerator.generate(props);
			case SPRING -> OpenApiSpecSpringDocGenerator.generate(props);
			default -> throw new RuntimeException("Unknown project type: " + props.getProjectType());
		}
	}

	/**
	 * Hide constructor.
	 */
	private OpenApiGenerator() {
		throw Constructors.unsupportedOperationException();
	}
}
