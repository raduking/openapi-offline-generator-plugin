package org.oogp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;

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
 * <p>
 * <b>Example usage:</b>
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
public class OpenApiSpecSpringGenerator {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiSpecSpringGenerator.class);

	/**
	 * The path separator.
	 */
	private static final String PATH_SEPARATOR = "/";

	/**
	 * Hide constructor.
	 */
	private OpenApiSpecSpringGenerator() {
		// empty
	}

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
	 * @throws IOException when an I/O error occurs
	 */
	public static void generate(String packagesToScan, String outputFile) throws IOException {
		Set<String> packages = Arrays.stream(packagesToScan.split(","))
				.map(String::trim)
				.filter(p -> !p.isEmpty())
				.collect(Collectors.toSet());

		Path projectClassesDir = Classes.detectDirectory();
		LOGGER.info("Using classes directory: {}", projectClassesDir.toAbsolutePath());

		Set<Class<?>> controllers = new HashSet<>();
		for (String pkg : packages) {
			LOGGER.info("Scanning package: {}", pkg);
			Set<Class<?>> classes = Classes.findInPackage(pkg, projectClassesDir);
			for (Class<?> cls : classes) {
				if (null != cls.getAnnotation(RestController.class) || null != cls.getAnnotation(RequestMapping.class)) {
					controllers.add(cls);
				}
			}
		}

		OpenAPI openAPI = new OpenAPI();
		Paths paths = new Paths();

		for (Class<?> controller : controllers) {
			processController(openAPI, paths, controller);
		}

		openAPI.setPaths(paths);

		File out = new File(outputFile);
		out.getParentFile().mkdirs();

		ObjectMapper mapper = switch (outputFile) {
			case String s when s.endsWith(".json") -> Json.mapper();
			case String s when (s.endsWith(".yaml") || s.endsWith(".yml")) -> Yaml.mapper();
			default -> throw new UnsupportedOperationException("Unsupported output type: " + outputFile);
		};

		try (FileWriter writer = new FileWriter(out, StandardCharsets.UTF_8)) {
			mapper.writerWithDefaultPrettyPrinter().writeValue(writer, openAPI);
		}

		LOGGER.info("Generated OpenAPI spec at {}", out.getAbsolutePath());
	}

	private static void processController(OpenAPI openAPI, Paths paths, Class<?> controller) {
		String basePath = "";
		RequestMapping classMapping = controller.getAnnotation(RequestMapping.class);
		if (classMapping != null && classMapping.value().length > 0) {
			basePath = classMapping.value()[0];
		}

		Tag tag = new Tag().name(controller.getSimpleName());
		openAPI.addTagsItem(tag);

		for (var method : controller.getDeclaredMethods()) {
			String httpMethod = null;
			String path = "";
			if (method.isAnnotationPresent(GetMapping.class)) {
				httpMethod = "get";
				path = firstValue(method.getAnnotation(GetMapping.class).value());
			} else if (method.isAnnotationPresent(PostMapping.class)) {
				httpMethod = "post";
				path = firstValue(method.getAnnotation(PostMapping.class).value());
			} else if (method.isAnnotationPresent(PutMapping.class)) {
				httpMethod = "put";
				path = firstValue(method.getAnnotation(PutMapping.class).value());
			} else if (method.isAnnotationPresent(DeleteMapping.class)) {
				httpMethod = "delete";
				path = firstValue(method.getAnnotation(DeleteMapping.class).value());
			} else if (method.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping rm = method.getAnnotation(RequestMapping.class);
				if (rm.method().length > 0) {
					httpMethod = rm.method()[0].name().toLowerCase();
				}
				path = firstValue(rm.value());
			}

			if (httpMethod != null) {
				String fullPath = (basePath + PATH_SEPARATOR + path).replaceAll("//+", PATH_SEPARATOR);
				Operation op = new Operation()
						.operationId(method.getName())
						.addTagsItem(controller.getSimpleName());

				ApiResponses responses = new ApiResponses()
						.addApiResponse("200", new ApiResponse().description("OK"));
				op.setResponses(responses);

				PathItem pathItem = paths.computeIfAbsent(fullPath, k -> new PathItem());
				pathItem.operation(PathItem.HttpMethod.valueOf(httpMethod.toUpperCase()), op);
			}
		}
	}

	private static String firstValue(String[] arr) {
		return (arr != null && arr.length > 0) ? arr[0] : "";
	}
}
