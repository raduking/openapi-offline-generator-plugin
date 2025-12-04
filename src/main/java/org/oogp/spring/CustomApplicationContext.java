package org.oogp.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.morphix.lang.JavaObjects;
import org.oogp.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * A custom implementation of Spring's ApplicationContext interface that provides basic bean management and dependency
 * injection capabilities.
 *
 * <p>
 * This implementation maintains beans in memory using two internal maps: one indexed by class type and another by bean
 * name. It supports basic bean operations such as registration, retrieval by name or type, and type-based queries.
 * </p>
 *
 * <p>
 * Key features:
 * </p>
 * <ul>
 * <li>Bean registration by instance, name, or metadata</li>
 * <li>Bean retrieval by name or type with type safety</li>
 * <li>Support for annotation-based bean queries</li>
 * <li>Integration with a custom bean factory for dependency injection</li>
 * <li>Partial implementation of Spring's ApplicationContext interface</li>
 * </ul>
 *
 * <p>
 * Note: Many ApplicationContext methods are not implemented and will throw UnsupportedOperationException. This
 * implementation is designed for specific use cases where a lightweight, custom bean container is needed.
 * </p>
 *
 * <p>
 * The context automatically registers itself and its associated bean factory as beans during construction.
 * </p>
 *
 * @see ApplicationContext
 * @see CustomBeanFactory
 * @see BeanMetadata
 *
 * @author Radu Sebastian LAZIN
 */
public class CustomApplicationContext implements ApplicationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomApplicationContext.class);

	private final Map<Class<?>, List<BeanMetadata>> classMap = new HashMap<>();
	private final Map<String, BeanMetadata> nameMap = new HashMap<>();

	private final Instant startupDate = Instant.now();

	private final ClassLoader classLoader;

	private final CustomBeanFactory customBeanFactory;

	/**
	 * Constructs a new CustomApplicationContext with the specified class loader.
	 *
	 * @param classLoader the class loader to use for loading classes and resources
	 */
	public CustomApplicationContext(final ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.customBeanFactory = new CustomBeanFactory(this);
		addBean(this);
		addBean(customBeanFactory);
	}

	/**
	 * Registers a bean with the context using the provided BeanMetadata.
	 *
	 * @param beanMetadata the metadata of the bean to register
	 */
	public void addBean(final BeanMetadata beanMetadata) {
		if (nameMap.containsKey(beanMetadata.beanName())) {
			throw new IllegalArgumentException("Bean with name " + beanMetadata.beanName() + " is already registered");
		}
		nameMap.put(beanMetadata.beanName(), beanMetadata);
		classMap.computeIfAbsent(beanMetadata.beanType(), _ -> new ArrayList<>()).add(beanMetadata);
	}

	/**
	 * Registers a bean with the context using its instance. The bean name is derived from the class's simple name.
	 *
	 * @param beanInstance the instance of the bean to register
	 */
	public void addBean(final Object beanInstance) {
		Class<?> beanType = beanInstance.getClass();
		addBean(beanType.getSimpleName(), beanInstance);
	}

	/**
	 * Registers a bean with the context using the provided name and instance.
	 *
	 * @param beanName the name of the bean to register
	 * @param beanInstance the instance of the bean to register
	 */
	public void addBean(final String beanName, final Object beanInstance) {
		BeanMetadata beanMetadata = BeanMetadata.of(beanName, beanInstance);
		addBean(beanMetadata);
	}

	/**
	 * Returns the custom bean factory associated with this application context.
	 *
	 * @return the custom bean factory
	 */
	public CustomBeanFactory getCustomBeanFactory() {
		return customBeanFactory;
	}

	@Override
	public Environment getEnvironment() {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean containsBeanDefinition(final String beanName) {
		return nameMap.containsKey(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return nameMap.size();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return nameMap.keySet().toArray(String[]::new);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType, final boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType, final boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getBeanNamesForType(final ResolvableType type) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getBeanNamesForType(final ResolvableType type, final boolean includeNonSingletons, final boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getBeanNamesForType(final Class<?> type) {
		List<String> result = new ArrayList<>();
		for (Map.Entry<Class<?>, List<BeanMetadata>> entry : classMap.entrySet()) {
			if (type.isAssignableFrom(entry.getKey())) {
				for (BeanMetadata beanMetadata : entry.getValue()) {
					result.add(beanMetadata.beanName());
				}
			}
		}
		return result.toArray(String[]::new);
	}

	@Override
	public String[] getBeanNamesForType(final Class<?> type, final boolean includeNonSingletons, final boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException {
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<Class<?>, List<BeanMetadata>> entry : classMap.entrySet()) {
			if (type.isAssignableFrom(entry.getKey())) {
				for (BeanMetadata beanMetadata : entry.getValue()) {
					result.put(beanMetadata.beanName(), beanMetadata.beanInstance());
				}
			}
		}
		return JavaObjects.cast(result);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit)
			throws BeansException {
		return getBeansOfType(type);
	}

	@Override
	public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> annotationType) {
		return Classes.unsupportedOperation();
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) throws BeansException {
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, BeanMetadata> entry : nameMap.entrySet()) {
			BeanMetadata beanMetadata = entry.getValue();
			if (beanMetadata.beanType().getAnnotation(annotationType) != null) {
				result.put(entry.getKey(), beanMetadata.beanInstance());
			}
		}
		return result;
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType, final boolean allowFactoryBeanInit)
			throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <A extends Annotation> Set<A> findAllAnnotationsOnBean(final String beanName, final Class<A> annotationType,
			final boolean allowFactoryBeanInit)
			throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object getBean(final String name) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
		BeanMetadata beanMetadata = nameMap.get(name);
		if (null == beanMetadata) {
			throw new NoSuchBeanDefinitionException("No bean available for name: " + name);
		}
		Class<?> beanType = beanMetadata.beanType();
		if (!requiredType.isAssignableFrom(beanType)) {
			throw new NoSuchBeanDefinitionException("Bean '" + name + "' is not of type: " + requiredType + ", actual type: " + beanType);
		}
		return JavaObjects.cast(beanMetadata.beanInstance());
	}

	@Override
	public Object getBean(final String name, final Object... args) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> T getBean(final Class<T> requiredType) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> T getBean(final Class<T> requiredType, final Object... args) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean containsBean(final String name) {
		return nameMap.containsKey(name);
	}

	@Override
	public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean isTypeMatch(final String name, final ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean isTypeMatch(final String name, final Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	@Nullable
	public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
		BeanMetadata beanMetadata = nameMap.get(name);
		if (null == beanMetadata) {
			throw new NoSuchBeanDefinitionException("No bean available for name: " + name);
		}
		return beanMetadata.beanType();
	}

	@Override
	@Nullable
	public Class<?> getType(final String name, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getAliases(final String name) {
		return Classes.unsupportedOperation();
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		return null;
	}

	@Override
	public boolean containsLocalBean(final String name) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException {
		LOGGER.debug("code: {}, args: {}, locale: {}", code, args, locale);
		return Classes.unsupportedOperation();
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void publishEvent(final Object event) {
		Classes.unsupportedOperation();
	}

	@Override
	public Resource[] getResources(final String locationPattern) throws IOException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Resource getResource(final String location) {
		return Classes.unsupportedOperation();
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public String getId() {
		return Classes.unsupportedOperation();
	}

	@Override
	public String getApplicationName() {
		return "Application name: " + getClass().getName();
	}

	@Override
	public String getDisplayName() {
		return "Display name: " + getClass().getName();
	}

	@Override
	public long getStartupDate() {
		return startupDate.toEpochMilli();
	}

	@Override
	public ApplicationContext getParent() {
		// no parent
		return null;
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return getCustomBeanFactory();
	}
}
