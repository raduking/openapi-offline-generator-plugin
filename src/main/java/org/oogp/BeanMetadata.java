package org.oogp;

import java.util.Set;

/**
 * Metadata record for beans registered with a {@link CustomApplicationContext}.
 *
 * @param beanName the bean name
 * @param beanInstance the bean instance
 * @param beanType the bean type
 * @param aliases the bean aliases
 * @param scope the scope of the bean in the context
 *
 * @author Radu Sebastian LAZIN
 */
public record BeanMetadata(
		String beanName,
		Object beanInstance,
		Class<?> beanType,
		Set<String> aliases,
		String scope) {

	public BeanMetadata {
		aliases = aliases != null ? aliases : Set.of();
	}

	public static BeanMetadata of(final String beanName, final Object beanInstance, final Class<?> beanType) {
		return new BeanMetadata(beanName, beanInstance, beanType, null, null);
	}

	public static BeanMetadata of(final String beanName, final Object beanInstance) {
		return of(beanName, beanInstance, beanInstance.getClass());
	}
}
