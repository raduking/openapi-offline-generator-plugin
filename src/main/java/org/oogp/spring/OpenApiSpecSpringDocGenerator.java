package org.oogp.spring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apiphany.json.JsonBuilder;
import org.apiphany.lang.collections.Lists;
import org.apiphany.lang.collections.Maps;
import org.morphix.reflection.InstanceCreator;
import org.morphix.reflection.Methods;
import org.oogp.Classes;
import org.oogp.GeneratorProperties;
import org.oogp.JavaEnvironment;
import org.oogp.SwaggerAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.converters.AdditionalModelsConverter;
import org.springdoc.core.converters.FileSupportConverter;
import org.springdoc.core.converters.PolymorphicModelConverter;
import org.springdoc.core.converters.PropertyCustomizingConverter;
import org.springdoc.core.converters.ResponseSupportConverter;
import org.springdoc.core.converters.SchemaPropertyDeprecatingConverter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.discoverer.SpringDocParameterNameDiscoverer;
import org.springdoc.core.extractor.MethodParameterPojoExtractor;
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
import org.springdoc.core.utils.SchemaUtils;
import org.springdoc.core.utils.SpringDocUtils;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.core.util.Yaml31;
import io.swagger.v3.oas.integration.GenericOpenApiContextBuilder;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Utility class responsible for generating an OpenAPI specification (YAML or JSON) from compiled Spring controller
 * classes. This class uses a minimal Spring functionality implementation to generate what a Spring application
 * generates when {@code spring-doc} is configured.
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
 *     org.oogp.spring.OpenApiSpecSpringDocGenerator \
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
	 * Annotations that define if a class should take part in the Open API generation.
	 */
	private static final Set<Class<? extends Annotation>> REQUEST_HANDLER_ANNOTATIONS = Set.of(
			RestController.class,
			RequestMapping.class,
			Controller.class);

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
	public static void main(final String[] args) {
		if (args.length < 2) {
			LOGGER.error("Usage: OpenApiSpecGenerator <packagesToScan> <outputFile>");
			System.exit(1);
		}
		try {
			GeneratorProperties properties = Classes.convertFromStringArray(args, GeneratorProperties.class);
			properties.applyDefaults(null);
			generate(properties);
		} catch (Exception e) {
			LOGGER.error("Error generating Open API", e);
		}
	}

	/**
	 * Generates an OpenAPI specification file by scanning the given base packages for annotated REST controllers.
	 * <p>
	 * The generator supports both YAML and JSON output formats, depending on the file extension provided.
	 *
	 * @param properties the generator properties
	 * @throws IOException when an I/O error occurs
	 */
	public static void generate(final GeneratorProperties properties) throws IOException {
		System.setProperty(JsonBuilder.Property.INDENT_OUTPUT, "true");
		LOGGER.info("Generator properties: {}", properties);

		Set<String> packages = Arrays.stream(properties.getPackagesToScan().split(","))
				.map(String::trim)
				.filter(p -> !p.isEmpty())
				.collect(Collectors.toSet());

		Path projectClassesDir = JavaEnvironment.detectProjectOutputDirectory();
		LOGGER.info("Using classes directory: {}", projectClassesDir.toAbsolutePath());

		Set<Class<?>> requestHandlerClasses = Classes.findWithAnyAnnotation(packages, projectClassesDir, REQUEST_HANDLER_ANNOTATIONS);
		ClassLoader projectClassLoader = Thread.currentThread().getContextClassLoader();
		CustomApplicationContext context = new CustomApplicationContext(projectClassLoader);
		for (Class<?> requestHandlerClass : requestHandlerClasses) {
			Object controller = InstanceCreator.getInstance().newInstance(requestHandlerClass);
			String beanName = requestHandlerClass.getSimpleName();
			context.addBean(controller);

			RequestMappingHandlerMapping handlerMapping = createHandlerMapping(controller, context, properties);
			context.addBean(beanName + "HandlerMapping", handlerMapping);
		}

		String outputFile = properties.getOutputFile();

		SpringDocOpenApiResource openApiResource = buildSpringDocOpenApiResource(outputFile, context);
		OpenAPI openAPI = openApiResource.getOpenApi(null, Locale.ENGLISH);

		List<GeneratorProperties.Server> configuredServers = Lists.safe(properties.getServers());
		openAPI.setServers(configuredServers.stream()
				.map(srv -> new Server().url(srv.getUrl()))
				.toList());
		if (properties.isOAuth2Enabled()) {
			configureOAuth2(openAPI, properties.getOauth2());
		}
		addExtensions(openAPI, properties.getExtensions());

		File out = new File(outputFile);
		out.getParentFile().mkdirs();

		boolean isOpenapi31 = openApiResource.getSpringDocConfigProperties().isOpenapi31();
		ObjectMapper mapper = switch (outputFile) {
			case String s when s.endsWith(".json") -> isOpenapi31 ? Json31.mapper() : Json.mapper();
			case String s when (s.endsWith(".yaml") || s.endsWith(".yml")) -> isOpenapi31 ? Yaml31.mapper() : Yaml.mapper();
			default -> throw new UnsupportedOperationException("Unsupported output type: " + outputFile);
		};

		try (FileWriter writer = new FileWriter(out, StandardCharsets.UTF_8)) {
			mapper.writerWithDefaultPrettyPrinter().writeValue(writer, openAPI);
		}

		LOGGER.info("Generated OpenAPI spec at {}", out.getAbsolutePath());

		Path outputFilePath = Path.of(outputFile);
		String generatedContent = Files.readString(outputFilePath);
		LOGGER.info("Generated:\n{}", generatedContent);
	}

	private static RequestMappingHandlerMapping createHandlerMapping(final Object controller, final ApplicationContext context,
			GeneratorProperties properties) {
		RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
		handlerMapping.setApplicationContext(context);
		handlerMapping.afterPropertiesSet();
		registerControllerMethods(handlerMapping, controller, properties.getSchemaForObjectClass());
		return handlerMapping;
	}

	private static void registerControllerMethods(final RequestMappingHandlerMapping handlerMapping, final Object controller,
			String schemaForObjectClass) {
		for (Method method : Methods.Complete.getAllDeclaredInHierarchy(controller.getClass(), Classes.mutableSetOf(Object.class))) {
			RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
			SwaggerAnnotations.overrideAnnotations(method, schemaForObjectClass);
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

	private static SpringDocOpenApiResource buildSpringDocOpenApiResource(final String outputFile, final CustomApplicationContext context) {
		SpringDocConfigProperties springDocConfigProperties = new SpringDocConfigProperties();
		String jsonSpringDocConfigProperties = JsonBuilder.toJson(springDocConfigProperties);
		LOGGER.info("Spring Doc Config properties: {}", jsonSpringDocConfigProperties);

		SpringDocUtils.getConfig().initExtraSchemas();
		ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider(springDocConfigProperties);
		registerModelConverters(springDocConfigProperties, objectMapperProvider);

		DelegatingMessageSource messageSource = new DelegatingMessageSource();
		PropertyResolverUtils propertyResolverUtils = new PropertyResolverUtils(
				context.getCustomBeanFactory(),
				messageSource,
				springDocConfigProperties);

		SecurityService securityService = new SecurityService(propertyResolverUtils);
		SpringDocJavadocProvider springDocJavadocProvider = new SpringDocJavadocProvider();

		OpenAPIService openAPIService = new OpenAPIService(
				Optional.empty(),
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
				Optional.of(Set.of(normalizeOperationIds())),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());

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
				new MethodParameterPojoExtractor(new SchemaUtils(Optional.empty())));

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
				Optional.of(springWebMvcProvider),
				objectMapperProvider);

		return new SpringDocOpenApiResource(
				outputFile,
				propertyResolverUtils,
				openAPIService,
				requestService,
				responseService,
				operationService,
				springDocCustomizers,
				springDocProviders);
	}

	private static void registerModelConverters(final SpringDocConfigProperties springDocConfigProperties,
			final ObjectMapperProvider objectMapperProvider) {
		ModelConverters modelConverters = ModelConverters.getInstance(springDocConfigProperties.isOpenapi31());

		modelConverters.addConverter(new AdditionalModelsConverter(objectMapperProvider));
		modelConverters.addConverter(new FileSupportConverter(objectMapperProvider));
		modelConverters.addConverter(new ResponseSupportConverter(objectMapperProvider));
		modelConverters.addConverter(new SchemaPropertyDeprecatingConverter());
		modelConverters.addConverter(new PolymorphicModelConverter(objectMapperProvider));
		modelConverters.addConverter(new PropertyCustomizingConverter(Optional.empty()));
	}

	private static OpenApiCustomizer normalizeOperationIds() {
		return openApi -> openApi.getPaths().forEach((_, item) -> {
			for (PathItem.HttpMethod method : item.readOperationsMap().keySet()) {
				Operation operation = item.readOperationsMap().get(method);
				String id = operation.getOperationId();
				if (id != null && id.startsWith("_")) {
					String normalizedId = id.substring(1);
					LOGGER.info("Normalizing operationId '{}' to '{}'", id, normalizedId);
					operation.setOperationId(normalizedId);
				}
			}
		});
	}

	private static void configureOAuth2(final OpenAPI openAPI, final GeneratorProperties.OAuth2 oauth2) {
		OAuthFlows flows = new OAuthFlows()
				.implicit(new OAuthFlow()
						.authorizationUrl(oauth2.getAuthorizationUrl())
						.scopes(new Scopes()));
		SecurityScheme oAuth2Scheme = new SecurityScheme()
				.type(SecurityScheme.Type.OAUTH2)
				.flows(flows);
		openAPI.schemaRequirement("OAuth2", oAuth2Scheme);
		openAPI.addSecurityItem(new SecurityRequirement().addList("OAuth2"));
	}

	private static void addExtensions(final OpenAPI openAPI, final Map<String, String> extensions) {
		Maps.safe(extensions).forEach(openAPI::addExtension);
	}
}
