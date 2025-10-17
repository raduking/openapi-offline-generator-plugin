package org.oogp;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springdoc.webmvc.api.OpenApiResource;

import io.swagger.v3.oas.models.OpenAPI;

public class SpringDocOpenApiResource extends OpenApiResource {

	protected SpringDocOpenApiResource(
			String groupName,
			PropertyResolverUtils propertyResolverUtils,
			OpenAPIService openAPIService,
			AbstractRequestService requestBuilder,
			GenericResponseService responseBuilder,
			OperationService operationService,
			SpringDocCustomizers springDocCustomizers,
			SpringDocProviders springDocProviders) {
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
	protected String getServerUrl(HttpServletRequest request, String apiDocsUrl) {
		return null;
	}

	@Override
	public OpenAPI getOpenApi(String serverBaseUrl, Locale locale) {
		return super.getOpenApi(serverBaseUrl, locale);
	}

}
