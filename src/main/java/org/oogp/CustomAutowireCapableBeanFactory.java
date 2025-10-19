package org.oogp;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

public class CustomAutowireCapableBeanFactory implements AutowireCapableBeanFactory {

	private final ApplicationContext context;

	public CustomAutowireCapableBeanFactory(final ApplicationContext context) {
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
		return Classes.unsupportedOperation();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
		return Classes.unsupportedOperation();
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
	public Class<?> getType(final String name) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	@Override
	public Class<?> getType(final String name, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	@Override
	public String[] getAliases(final String name) {
		return context.getAliases(name);
	}

	@Override
	public <T> T createBean(final Class<T> beanClass) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void autowireBean(final Object existingBean) throws BeansException {
		Classes.unsupportedOperation();
	}

	@Override
	public Object configureBean(final Object existingBean, final String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object createBean(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object autowire(final Class<?> beanClass, final int autowireMode, final boolean dependencyCheck) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void autowireBeanProperties(final Object existingBean, final int autowireMode, final boolean dependencyCheck) throws BeansException {
		Classes.unsupportedOperation();
	}

	@Override
	public void applyBeanPropertyValues(final Object existingBean, final String beanName) throws BeansException {
		Classes.unsupportedOperation();
	}

	@Override
	public Object initializeBean(final Object existingBean, final String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(final Object existingBean, final String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object applyBeanPostProcessorsAfterInitialization(final Object existingBean, final String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void destroyBean(final Object existingBean) {
		Classes.unsupportedOperation();
	}

	@Override
	public <T> NamedBeanHolder<T> resolveNamedBean(final Class<T> requiredType) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object resolveBeanByName(final String name, final DependencyDescriptor descriptor) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object resolveDependency(final DependencyDescriptor descriptor, final String requestingBeanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object resolveDependency(final DependencyDescriptor descriptor, final String requestingBeanName, final Set<String> autowiredBeanNames,
			final TypeConverter typeConverter) throws BeansException {
		return Classes.unsupportedOperation();
	}

}
