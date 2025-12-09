package org.oogp;

import java.lang.reflect.Method;

import org.apiphany.lang.Strings;
import org.morphix.reflection.Annotations;
import org.morphix.reflection.Constructors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Utility methods for overriding Swagger annotations.
 *
 * @author Radu Sebastian LAZIN
 */
public interface SwaggerAnnotations {

	/**
	 * Name space class for attribute constants.
	 *
	 * @author Radu Sebastian LAZIN
	 */
	class AttributeName {

		/**
		 * The 'type' attribute name.
		 */
		public static final String TYPE = "type";

		/**
		 * Hide constructor.
		 */
		private AttributeName() {
			throw Constructors.unsupportedOperationException();
		}
	}

	/**
	 * Overrides the 'type' attribute of schemas within the Operation annotation of a method.
	 *
	 * @param method the method containing the Operation annotation
	 * @param schemaForObjectClass the new type value to set for Object class schemas
	 */
	static void overrideAll(final Method method, final String schemaForObjectClass) {
		Operation operation = method.getAnnotation(Operation.class);
		if (null == operation) {
			return;
		}
		for (ApiResponse apiResponse : operation.responses()) {
			for (Content content : apiResponse.content()) {
				Schema schema = content.schema();
				if (null == schema) {
					continue;
				}
				if (Object.class.equals(schema.implementation()) && Strings.isEmpty(schema.type())) {
					Annotations.overrideValue(schema, AttributeName.TYPE, schemaForObjectClass);
				}
			}
		}
	}
}
