package org.oogp;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springdoc.webmvc.api.OpenApiResource;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * Custom SpringDoc OpenApiResource.
 *
 * @author Radu Sebastian LAZIN
 */
public class SpringDocOpenApiResource extends OpenApiResource {

	/**
	 * Constructor.
	 *
	 * @param groupName the group name
	 * @param propertyResolverUtils the property resolver utils
	 * @param openAPIService the OpenAPI service
	 * @param requestBuilder the request builder
	 * @param responseBuilder the response builder
	 * @param operationService the operation service
	 * @param springDocCustomizers the SpringDoc customizers
	 * @param springDocProviders the SpringDoc providers
	 */
	protected SpringDocOpenApiResource( // NOSONAR we need to pass all these dependencies
			final String groupName,
			final PropertyResolverUtils propertyResolverUtils,
			final OpenAPIService openAPIService,
			final AbstractRequestService requestBuilder,
			final GenericResponseService responseBuilder,
			final OperationService operationService,
			final SpringDocCustomizers springDocCustomizers,
			final SpringDocProviders springDocProviders) {
		super(groupName,
				() -> openAPIService,
				requestBuilder,
				responseBuilder,
				operationService,
				propertyResolverUtils.getSpringDocConfigProperties(),
				springDocProviders,
				springDocCustomizers);
	}

	@Override
	protected String getServerUrl(final HttpServletRequest request, final String apiDocsUrl) {
		return null;
	}

	@Override
	public OpenAPI getOpenApi(final String serverBaseUrl, final Locale locale) {
		return super.getOpenApi(serverBaseUrl, locale);
	}

	/**
	 * Returns the SpringDoc configuration properties.
	 *
	 * @return the springDocConfigProperties
	 */
	public SpringDocConfigProperties getSpringDocConfigProperties() {
		return springDocConfigProperties;
	}
}
