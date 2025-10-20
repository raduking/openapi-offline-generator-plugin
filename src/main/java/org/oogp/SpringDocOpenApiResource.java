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

public class SpringDocOpenApiResource extends OpenApiResource {

	private final SpringDocConfigProperties springDocConfigProperties;

	protected SpringDocOpenApiResource(
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
		this.springDocConfigProperties = propertyResolverUtils.getSpringDocConfigProperties();
	}

	@Override
	protected String getServerUrl(final HttpServletRequest request, final String apiDocsUrl) {
		return null;
	}

	@Override
	public OpenAPI getOpenApi(final String serverBaseUrl, final Locale locale) {
		return super.getOpenApi(serverBaseUrl, locale);
	}

	public SpringDocConfigProperties getSpringDocConfigProperties() {
		return springDocConfigProperties;
	}
}
