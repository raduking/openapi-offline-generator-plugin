package org.oogp;

import java.util.Set;

public record BeanMetadata(
		String beanName,
		Object beanInstance,
		Class<?> beanType,
		Set<String> aliases,
		String scope) {

	public BeanMetadata {
		aliases = aliases != null ? aliases : Set.of();
	}

	public static BeanMetadata of(String beanName, Object beanInstance, Class<?> beanType) {
		return new BeanMetadata(beanName, beanInstance, beanType, null, null);
	}

	public static BeanMetadata of(String beanName, Object beanInstance) {
		return of(beanName, beanInstance, beanInstance.getClass());
	}
}
