package org.oogp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apiphany.json.JsonBuilder;
import org.morphix.reflection.InstanceCreator;
import org.morphix.reflection.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.discoverer.SpringDocParameterNameDiscoverer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.SpringDocJavadocProvider;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.GenericParameterService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.service.RequestBodyService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
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
public class OpenApiSpecSpringDocGenerator {

	/**
	 * The logger used by this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiSpecSpringDocGenerator.class);

	/**
	 * Hide constructor.
	 */
	private OpenApiSpecSpringDocGenerator() {
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
					LOGGER.info("Found controller: {}", cls);
				}
			}
		}

		ClassLoader projectLoader = Thread.currentThread().getContextClassLoader();
		CustomApplicationContext context = new CustomApplicationContext(projectLoader);
		for (Class<?> controllerClass : controllers) {
			Object controller = InstanceCreator.getInstance().newInstance(controllerClass);
			String beanName = controllerClass.getSimpleName();
			context.addBean(controller);

			RequestMappingHandlerMapping handlerMapping = createHandlerMapping(context, controller);
			context.addBean(beanName + "HandlerMapping", handlerMapping);
		}
		context.addBean(context);

		OpenAPI openAPI = new OpenAPI();

		SpringDocConfigProperties springDocConfigProperties = new SpringDocConfigProperties();
		springDocConfigProperties.setOverrideWithGenericResponse(false);

		System.setProperty(JsonBuilder.Property.INDENT_OUTPUT, "true");
		String properties = JsonBuilder.toJson(springDocConfigProperties);
		LOGGER.info("Spring Doc Config properties: {}", properties);

		PropertyResolverUtils propertyResolverUtils = new PropertyResolverUtils(
				null,
				context,
				springDocConfigProperties);

		SecurityService securityService = new SecurityService(propertyResolverUtils);
		SpringDocJavadocProvider springDocJavadocProvider = new SpringDocJavadocProvider();

		OpenAPIService openAPIService = new OpenAPIService(
				Optional.of(openAPI),
				securityService,
				springDocConfigProperties,
				propertyResolverUtils,
				Optional.empty(),
				Optional.empty(),
				Optional.of(springDocJavadocProvider));

		openAPIService.setApplicationContext(context);

		SpringWebMvcProvider springWebMvcProvider = new SpringWebMvcProvider();
		springWebMvcProvider.setApplicationContext(context);

		SpringDocCustomizers springDocCustomizers = new SpringDocCustomizers(
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

		ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider(springDocConfigProperties);

		GenericParameterService genericParameterService = new GenericParameterService(
				propertyResolverUtils,
				Optional.empty(),
				objectMapperProvider,
				Optional.of(springDocJavadocProvider));

		RequestBodyService requestBodyService = new RequestBodyService(
				genericParameterService,
				propertyResolverUtils);

		RequestService requestService = new RequestService(
				genericParameterService,
				requestBodyService,
				springDocCustomizers,
				new SpringDocParameterNameDiscoverer(),
				null);

		OperationService operationService = new OperationService(
				genericParameterService,
				requestBodyService,
				securityService,
				propertyResolverUtils);

		GenericResponseService responseService = new GenericResponseService(
				operationService,
				springDocConfigProperties,
				propertyResolverUtils);

		SpringDocProviders springDocProviders = new SpringDocProviders(
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.ofNullable(springWebMvcProvider),
				objectMapperProvider);

		SpringDocOpenApiResource openApiResource = new SpringDocOpenApiResource(
				outputFile,
				propertyResolverUtils,
				openAPIService,
				requestService,
				responseService,
				operationService,
				springDocCustomizers,
				springDocProviders);

		openAPI = openApiResource.getOpenApi(null, Locale.ENGLISH);

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

	private static RequestMappingHandlerMapping createHandlerMapping(ApplicationContext context, Object controller) {
		RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
		handlerMapping.setApplicationContext(context);
		handlerMapping.afterPropertiesSet();
		registerControllerMethods(handlerMapping, controller);
		return handlerMapping;
	}

	private static void registerControllerMethods(RequestMappingHandlerMapping handlerMapping, Object controller) {
		for (Method method : Methods.getAllDeclaredInHierarchy(controller.getClass())) {
			RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
			if (methodMapping != null) {
				RequestMappingInfo mappingInfo = RequestMappingInfo
						.paths(methodMapping.value())
						.methods(methodMapping.method())
						.params(methodMapping.params())
						.headers(methodMapping.headers())
						.consumes(methodMapping.consumes())
						.produces(methodMapping.produces())
						.build();

				handlerMapping.registerMapping(mappingInfo, controller, method);
			}
		}
	}
}
