package org.oogp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apiphany.lang.Strings;
import org.morphix.reflection.Constructors;
import org.morphix.reflection.Fields;
import org.morphix.reflection.Methods;
import org.morphix.reflection.ReflectionException;

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
	 * Overrides a specific attribute value of an annotation instance present on a method.
	 *
	 * @param <A> the type of the annotation
	 *
	 * @param method the method containing the annotation
	 * @param annotationClass the class of the annotation to override
	 * @param attribute the attribute name to override
	 * @param value the new value to set
	 * @return the original annotation instance
	 */
	@SuppressWarnings("unchecked")
	static <A extends Annotation> A overrideValue(final Method method, final Class<A> annotationClass, final String attribute, final String value) {
		A annotation = method.getAnnotation(annotationClass);
		if (null == annotation) {
			return annotation;
		}

		Map<String, Object> values = new HashMap<>();
		for (Method m : annotationClass.getDeclaredMethods()) {
			Object val = Methods.IgnoreAccess.invoke(m, annotation);
			values.put(m.getName(), val);
		}
		values.put(attribute, value);

		InvocationHandler proxyHandler = (_, m, args) -> {
			if (values.containsKey(m.getName())) {
				return values.get(m.getName());
			}
			return m.invoke(annotation, args);
		};

		Class<?>[] interfaces = new Class[] { annotationClass };
		A proxy = (A) Proxy.newProxyInstance(annotationClass.getClassLoader(), interfaces, proxyHandler);

		// inject proxy into the method's annotations
		Field annotationsField = Fields.getOneDeclaredInHierarchy(Method.class, "declaredAnnotations");
		Map<Class<? extends Annotation>, Annotation> declared = Fields.IgnoreAccess.get(method, annotationsField);

		declared.put(annotationClass, proxy);

		return annotation;
	}

	/**
	 * Overrides a specific attribute value of an annotation instance.
	 *
	 * @param <A> the type of the annotation
	 *
	 * @param annotation the annotation instance
	 * @param attribute the attribute name to override
	 * @param value the new value to set
	 */
	static <A extends Annotation> void overrideValue(final A annotation, final String attribute, final Object value) {
		InvocationHandler handler = Proxy.getInvocationHandler(annotation);
		try {
			Field memberValuesField = Fields.getOneDeclared(handler, "memberValues");
			Map<String, Object> memberValues = Fields.IgnoreAccess.get(handler, memberValuesField);
			memberValues.put(attribute, value);
		} catch (Exception e) {
			throw new ReflectionException("Failed to override annotation value", e);
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
					overrideValue(schema, AttributeName.TYPE, schemaForObjectClass);
				}
			}
		}
	}
}
