package org.oogp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Utility class responsible for generating an OpenAPI specification (YAML or JSON) from compiled Spring controller
 * classes.
 * <p>
 * This class can be used both:
 * <ul>
 * <li>As a standalone command-line utility (via {@link #main(String[])})</li>
 * <li>Or invoked programmatically (e.g., from a custom Maven Mojo)</li>
 * </ul>
 * <p>
 * It uses {@link SwaggerConfiguration} and {@link GenericOpenApiContextBuilder} from the <em>swagger-core</em> library
 * to introspect annotated Spring controllers and build a valid {@link OpenAPI} model representation.
 *
 * <h3>Example usage:</h3>
 *
 * <pre>{@code
 * java -cp target/classes:<dependencies> \
 *     org.oogp.OpenApiSpecGenerator \
 *     "com.example.app.controller" \
 *     "target/generated/openapi.yaml"
 * }</pre>
 *
 * The output format is automatically inferred from the file extension:
 * <ul>
 * <li><code>.json</code> → JSON</li>
 * <li><code>.yaml</code> / <code>.yml</code> → YAML</li>
 * </ul>
 *
 * @author Radu Sebastian LAZIN
 */
public class OpenApiSpecGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiSpecGenerator.class);

	/**
	 * Entry point for CLI execution.
	 * <p>
	 * Expects two arguments:
	 * <ol>
	 * <li>The base package(s) to scan, comma-separated (e.g. {@code com.example.controller,com.example.api})</li>
	 * <li>The output file path (e.g. {@code target/generated/openapi.yaml})</li>
	 * </ol>
	 *
	 * @param args command-line arguments:
	 *     <ul>
	 *     <li>{@code args[0]} → base packages to scan</li>
	 *     <li>{@code args[1]} → output file path</li>
	 *     </ul>
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			LOGGER.error("Usage: OpenApiSpecGenerator <packagesToScan> <outputFile>");
			System.exit(1);
		}
		try {
			generate(args[0], args[1]);
		} catch (Exception e) {
			LOGGER.error("Error generating Open API", e);
		}
	}

	/**
	 * Generates an OpenAPI specification file by scanning the given base packages for annotated REST controllers.
	 * <p>
	 * The generator supports both YAML and JSON output formats, depending on the file extension provided.
	 *
	 * @param packagesToScan comma-separated list of base packages
	 * @param outputFile output YAML or JSON file path
	 * @throws OpenApiConfigurationException when Open API builder fails
	 * @throws IOException when an I/O error occurs
	 */
	public static void generate(String packagesToScan, String outputFile) throws OpenApiConfigurationException, IOException {
		Set<String> packages = Arrays.stream(packagesToScan.split(","))
				.map(String::trim)
				.filter(p -> !p.isEmpty())
				.collect(Collectors.toSet());

		SwaggerConfiguration config = new SwaggerConfiguration()
				.resourcePackages(packages)
				.prettyPrint(true);

		OpenAPI openAPI = new GenericOpenApiContextBuilder<>()
				.openApiConfiguration(config)
				.buildContext(true)
				.read();

		File out = new File(outputFile);
		out.getParentFile().mkdirs();

		ObjectMapper mapper = switch (outputFile) {
			case String s when s.endsWith(".json") -> Json.mapper();
			case String s when (s.endsWith(".yaml") || s.endsWith(".yml")) -> Yaml.mapper();
			default -> throw new UnsupportedOperationException("Unsupported output type: " + outputFile);
		};

		try (FileWriter writer = new FileWriter(out)) {
			mapper.writerWithDefaultPrettyPrinter().writeValue(writer, openAPI);
		}

		LOGGER.info("Generated OpenAPI spec at {}", out.getAbsolutePath());
	}
}
