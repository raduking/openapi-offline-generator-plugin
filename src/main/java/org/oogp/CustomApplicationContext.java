package org.oogp;

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

public class CustomApplicationContext implements ApplicationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomApplicationContext.class);

	private Map<Class<?>, List<BeanMetadata>> classMap = new HashMap<>();
	private Map<String, BeanMetadata> nameMap = new HashMap<>();

	private Instant startupDate = Instant.now();

	private final ClassLoader classLoader;

	private AutowireCapableBeanFactory autowireCapableBeanFactory;

	public CustomApplicationContext(ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.autowireCapableBeanFactory = new CustomAutowireCapableBeanFactory(this);
	}

	public void addBean(BeanMetadata beanMetadata) {
		nameMap.put(beanMetadata.beanName(), beanMetadata);
		classMap.computeIfAbsent(beanMetadata.beanType(), k -> new ArrayList<>()).add(beanMetadata);
	}

	public void addBean(Object beanInstance) {
		Class<?> beanType = beanInstance.getClass();
		addBean(beanType.getSimpleName(), beanInstance);
	}

	public void addBean(String beanName, Object beanInstance) {
		BeanMetadata beanMetadata = BeanMetadata.of(beanName, beanInstance);
		addBean(beanMetadata);
	}

	@Override
	public Environment getEnvironment() {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return nameMap.containsKey(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return nameMap.size();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return nameMap.keySet().stream().toArray(String[]::new);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		List<String> result = new ArrayList<>();
		for (Map.Entry<Class<?>, List<BeanMetadata>> entry : classMap.entrySet()) {
			if (type.isAssignableFrom(entry.getKey())) {
				for (BeanMetadata beanMetadata : entry.getValue()) {
					result.add(beanMetadata.beanName());
				}
			}
		}
		return result.stream().toArray(String[]::new);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
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
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
		return getBeansOfType(type);
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		return Classes.unsupportedOperation();
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
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
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit)
			throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <A extends Annotation> Set<A> findAllAnnotationsOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit)
			throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object getBean(String name) throws BeansException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
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
	public Object getBean(String name, Object... args) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean containsBean(String name) {
		return nameMap.containsKey(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		BeanMetadata beanMetadata = nameMap.get(name);
		if (null == beanMetadata) {
			throw new NoSuchBeanDefinitionException("No bean available for name: " + name);
		}
		return beanMetadata.beanType();
	}

	@Override
	public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
		return Classes.unsupportedOperation();
	}

	@Override
	public String[] getAliases(String name) {
		return Classes.unsupportedOperation();
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		return null;
	}

	@Override
	public boolean containsLocalBean(String name) {
		return Classes.unsupportedOperation();
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		var e = new UnsupportedOperationException();
		LOGGER.error("Error", e);
		throw e;
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return Classes.unsupportedOperation();
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void publishEvent(Object event) {
		Classes.unsupportedOperation();
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Resource getResource(String location) {
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
		return autowireCapableBeanFactory;
	}

}
