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

	public CustomAutowireCapableBeanFactory(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Object getBean(String name) throws BeansException {
		return context.getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return context.getBean(name, requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		return context.getBean(name, args);
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return context.getBean(requiredType);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return context.getBean(requiredType, args);
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
		return context.containsBean(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return context.isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return context.isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		return context.isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return context.isTypeMatch(name, typeToMatch);
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	@Override
	public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
		return context.getType(name);
	}

	@Override
	public String[] getAliases(String name) {
		return context.getAliases(name);
	}

	@Override
	public <T> T createBean(Class<T> beanClass) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void autowireBean(Object existingBean) throws BeansException {
		Classes.unsupportedOperation();
	}

	@Override
	public Object configureBean(Object existingBean, String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
		Classes.unsupportedOperation();
	}

	@Override
	public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
		Classes.unsupportedOperation();
	}

	@Override
	public Object initializeBean(Object existingBean, String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public void destroyBean(Object existingBean) {
		Classes.unsupportedOperation();
	}

	@Override
	public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
		return Classes.unsupportedOperation();
	}

	@Override
	public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames,
			TypeConverter typeConverter) throws BeansException {
		return Classes.unsupportedOperation();
	}

}
