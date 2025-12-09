package org.oogp.spring;

import java.beans.PropertyEditor;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.oogp.Errors;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * A custom bean factory implementation that acts as a wrapper around an existing Spring ApplicationContext. This class
 * implements both AutowireCapableBeanFactory and ConfigurableBeanFactory interfaces to provide a bridge between an
 * ApplicationContext and components that require these specific factory interfaces.
 *
 * <p>
 * The factory delegates most bean retrieval operations to the underlying ApplicationContext, while throwing
 * UnsupportedOperationException for advanced factory operations like bean creation, autowiring, and configuration
 * management that are not supported by this wrapper implementation.
 * </p>
 *
 * <p>
 * This implementation is primarily designed for scenarios where you need to expose an ApplicationContext as a
 * BeanFactory interface without full factory capabilities, such as in plugin architectures or integration scenarios
 * where only basic bean lookup functionality is required.
 * </p>
 *
 * <b>Supported Operations:</b>
 * <ul>
 * <li>Bean retrieval by name and/or type</li>
 * <li>Bean existence checks</li>
 * <li>Bean scope queries (singleton/prototype)</li>
 * <li>Type matching operations</li>
 * <li>Bean type resolution</li>
 * <li>Alias resolution</li>
 * <li>Basic embedded value resolution (pass-through)</li>
 * <li>Basic bean expression resolution (pass-through)</li>
 * </ul>
 *
 * <b>Unsupported Operations:</b>
 * <ul>
 * <li>Bean creation and instantiation</li>
 * <li>Dependency injection and autowiring</li>
 * <li>Bean lifecycle management</li>
 * <li>Singleton registration and management</li>
 * <li>Bean post-processing</li>
 * <li>Scope management</li>
 * <li>Factory configuration</li>
 * </ul>
 *
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory
 * @see org.springframework.context.ApplicationContext
 *
 * @author Radu Sebastian LAZIN
 */
public class CustomBeanFactory implements AutowireCapableBeanFactory, ConfigurableBeanFactory {

	private final ApplicationContext context;

	/**
	 * Constructs a new CustomBeanFactory that wraps the given ApplicationContext.
	 *
	 * @param context the ApplicationContext to wrap
	 */
	public CustomBeanFactory(final ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Object getBean(final String name) throws BeansException {
		return context.getBean(name);
	}

	@Override
	public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException {
		return context.getBean(name, requiredType);
	}

	@Override
	public Object getBean(final String name, final Object... args) throws BeansException {
		return context.getBean(name, args);
	}

	@Override
	public <T> T getBean(final Class<T> requiredType) throws BeansException {
		return context.getBean(requiredType);
	}

	@Override
	public <T> T getBean(final Class<T> requiredType, final Object... args) throws BeansException {
		return context.getBean(requiredType, args);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
		return Errors.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
		return Errors.unsupportedOperation();
	}

	@Override
	public boolean containsBean(final String name) {
		return context.containsBean(name);
	}

	@Override
	public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException {
		return context.isSingleton(name);
	}

	@Override
	public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException {
		return context.isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(final String name, final ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		return context.isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(final String name, final Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return context.isTypeMatch(name, typeToMatch);
	}

	@Override
	@Nullable
	public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	@Override
	@Nullable
	public Class<?> getType(final String name, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	@Override
	public String[] getAliases(final String name) {
		return context.getAliases(name);
	}

	@Override
	public <T> T createBean(final Class<T> beanClass) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public void autowireBean(final Object existingBean) throws BeansException {
		Errors.unsupportedOperation();
	}

	@Override
	public Object configureBean(final Object existingBean, final String beanName) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object createBean(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object autowire(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public void autowireBeanProperties(final Object existingBean, final int autowireMode, final boolean dependencyCheck) throws BeansException {
		Errors.unsupportedOperation();
	}

	@Override
	public void applyBeanPropertyValues(final Object existingBean, final String beanName) throws BeansException {
		Errors.unsupportedOperation();
	}

	@Override
	public Object initializeBean(final Object existingBean, final String beanName) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(final Object existingBean, final String beanName) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object applyBeanPostProcessorsAfterInitialization(final Object existingBean, final String beanName) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public void destroyBean(final Object existingBean) {
		Errors.unsupportedOperation();
	}

	@Override
	public <T> NamedBeanHolder<T> resolveNamedBean(final Class<T> requiredType) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object resolveBeanByName(final String name, final DependencyDescriptor descriptor) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object resolveDependency(final DependencyDescriptor descriptor, final String requestingBeanName) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object resolveDependency(final DependencyDescriptor descriptor, final String requestingBeanName, final Set<String> autowiredBeanNames,
			final TypeConverter typeConverter) throws BeansException {
		return Errors.unsupportedOperation();
	}

	@Override
	public BeanFactory getParentBeanFactory() {
		// no parent
		return null;
	}

	@Override
	public boolean containsLocalBean(final String name) {
		return Errors.unsupportedOperation();
	}

	@Override
	public void registerSingleton(final String beanName, final Object singletonObject) {
		Errors.unsupportedOperation();
	}

	@Override
	public void addSingletonCallback(final String beanName, final Consumer<Object> singletonConsumer) {
		Errors.unsupportedOperation();
	}

	@Override
	public Object getSingleton(final String beanName) {
		return Errors.unsupportedOperation();
	}

	@Override
	public boolean containsSingleton(final String beanName) {
		return Errors.unsupportedOperation();
	}

	@Override
	public String[] getSingletonNames() {
		return Errors.unsupportedOperation();
	}

	@Override
	public int getSingletonCount() {
		return Errors.unsupportedOperation();
	}

	@Override
	public Object getSingletonMutex() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setParentBeanFactory(final BeanFactory parentBeanFactory) throws IllegalStateException {
		Errors.unsupportedOperation();
	}

	@Override
	public void setBeanClassLoader(final ClassLoader beanClassLoader) {
		Errors.unsupportedOperation();
	}

	@Override
	public ClassLoader getBeanClassLoader() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setTempClassLoader(final ClassLoader tempClassLoader) {
		Errors.unsupportedOperation();
	}

	@Override
	public ClassLoader getTempClassLoader() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setCacheBeanMetadata(final boolean cacheBeanMetadata) {
		Errors.unsupportedOperation();
	}

	@Override
	public boolean isCacheBeanMetadata() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setBeanExpressionResolver(final BeanExpressionResolver resolver) {
		Errors.unsupportedOperation();
	}

	@Override
	public BeanExpressionResolver getBeanExpressionResolver() {
		// TODO: see if we need it
		return (value, _) -> value;
	}

	@Override
	public void setBootstrapExecutor(final Executor executor) {
		Errors.unsupportedOperation();
	}

	@Override
	public Executor getBootstrapExecutor() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setConversionService(final ConversionService conversionService) {
		Errors.unsupportedOperation();
	}

	@Override
	public ConversionService getConversionService() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void addPropertyEditorRegistrar(final PropertyEditorRegistrar registrar) {
		Errors.unsupportedOperation();
	}

	@Override
	public void registerCustomEditor(final Class<?> requiredType, final Class<? extends PropertyEditor> propertyEditorClass) {
		Errors.unsupportedOperation();
	}

	@Override
	public void copyRegisteredEditorsTo(final PropertyEditorRegistry registry) {
		Errors.unsupportedOperation();
	}

	@Override
	public void setTypeConverter(final TypeConverter typeConverter) {
		Errors.unsupportedOperation();
	}

	@Override
	public TypeConverter getTypeConverter() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void addEmbeddedValueResolver(final StringValueResolver valueResolver) {
		Errors.unsupportedOperation();
	}

	@Override
	public boolean hasEmbeddedValueResolver() {
		return Errors.unsupportedOperation();
	}

	@Override
	public String resolveEmbeddedValue(final String value) {
		// TODO: maybe resolve properties with: PropertySourcesPlaceholderConfigurer for ${...} and
		// StandardBeanExpressionResolver for #{...}
		return value;
	}

	@Override
	public void addBeanPostProcessor(final BeanPostProcessor beanPostProcessor) {
		Errors.unsupportedOperation();
	}

	@Override
	public int getBeanPostProcessorCount() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void registerScope(final String scopeName, final Scope scope) {
		Errors.unsupportedOperation();
	}

	@Override
	public String[] getRegisteredScopeNames() {
		return Errors.unsupportedOperation();
	}

	@Override
	public Scope getRegisteredScope(final String scopeName) {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setApplicationStartup(final ApplicationStartup applicationStartup) {
		Errors.unsupportedOperation();
	}

	@Override
	public ApplicationStartup getApplicationStartup() {
		return Errors.unsupportedOperation();
	}

	@Override
	public void copyConfigurationFrom(final ConfigurableBeanFactory otherFactory) {
		Errors.unsupportedOperation();
	}

	@Override
	public void registerAlias(final String beanName, final String alias) throws BeanDefinitionStoreException {
		Errors.unsupportedOperation();
	}

	@Override
	public void resolveAliases(final StringValueResolver valueResolver) {
		Errors.unsupportedOperation();
	}

	@Override
	public BeanDefinition getMergedBeanDefinition(final String beanName) throws NoSuchBeanDefinitionException {
		return Errors.unsupportedOperation();
	}

	@Override
	public boolean isFactoryBean(final String name) throws NoSuchBeanDefinitionException {
		return Errors.unsupportedOperation();
	}

	@Override
	public void setCurrentlyInCreation(final String beanName, final boolean inCreation) {
		Errors.unsupportedOperation();
	}

	@Override
	public boolean isCurrentlyInCreation(final String beanName) {
		return Errors.unsupportedOperation();
	}

	@Override
	public void registerDependentBean(final String beanName, final String dependentBeanName) {
		Errors.unsupportedOperation();
	}

	@Override
	public String[] getDependentBeans(final String beanName) {
		return Errors.unsupportedOperation();
	}

	@Override
	public String[] getDependenciesForBean(final String beanName) {
		return Errors.unsupportedOperation();
	}

	@Override
	public void destroyBean(final String beanName, final Object beanInstance) {
		Errors.unsupportedOperation();
	}

	@Override
	public void destroyScopedBean(final String beanName) {
		Errors.unsupportedOperation();
	}

	@Override
	public void destroySingletons() {
		Errors.unsupportedOperation();
	}
}
