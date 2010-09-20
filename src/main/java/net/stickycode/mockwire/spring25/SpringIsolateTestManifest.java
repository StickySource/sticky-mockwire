package net.stickycode.mockwire.spring25;

import java.beans.Introspector;

import org.mockito.internal.util.Decamelizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import net.stickycode.mockwire.IsolatedTestManifest;
import net.stickycode.mockwire.MissingBeanException;

public class SpringIsolateTestManifest
    extends GenericApplicationContext
    implements IsolatedTestManifest {

  private Logger log = LoggerFactory.getLogger(getClass());



  public SpringIsolateTestManifest() {
    super();

    registerPostProcessor(new InjectAnnotationBeanPostProcessor());
    registerPostProcessor(new MockInjectionAnnotationBeanPostProcessor());
    registerPostProcessor(new BlessInjectionAnnotationBeanPostProcessor());

    getBeanFactory().registerSingleton(
        Introspector.decapitalize(getClass().getSimpleName()),
        this);
  }

  private void registerPostProcessor(AutowiredAnnotationBeanPostProcessor autowiredProcessor) {
    autowiredProcessor.setBeanFactory(getDefaultListableBeanFactory());
    getBeanFactory().addBeanPostProcessor(autowiredProcessor);
  }

  public SpringIsolateTestManifest(ApplicationContext parent) {
    super(parent);
  }

  public SpringIsolateTestManifest(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
    super(beanFactory, parent);
  }

  public SpringIsolateTestManifest(DefaultListableBeanFactory beanFactory) {
    super(beanFactory);
  }

  @Override
  public boolean hasRegisteredType(Class<?> type) {
    return getBeanNamesForType(type).length > 0;
  }

  @Override
  public void autowire(Object testInstance) {
    try {
      getAutowireCapableBeanFactory().autowireBean(testInstance);
    }
    catch (BeansException e) {
      Throwable cause = e.getMostSpecificCause();
      if (cause instanceof NoSuchBeanDefinitionException) {
        NoSuchBeanDefinitionException n = (NoSuchBeanDefinitionException)cause;
        throw new MissingBeanException("Missing {} of type {}", n.getBeanName() == null ? "bean" : n.getBeanName(), n.getBeanType().getName());
      }
    }
  }

  @Override
  public void registerBean(String beanName, Object bean) {
    log.info("registering bean {} of type {}", beanName, bean.getClass().getName());
    getBeanFactory().initializeBean(bean, beanName);
    getBeanFactory().registerSingleton(beanName, bean);
    // beans that get pushed straight into the context need to be attached to destructive bean post processors
    getDefaultListableBeanFactory().registerDisposableBean(
        beanName, new DisposableBeanAdapter(bean, beanName, this));
  }

  @Override
  public void registerType(String beanName, Class<?> type) {
    log.info("registering {}  as type {}", beanName, type.getName());
    GenericBeanDefinition bd = new GenericBeanDefinition();
    bd.setBeanClass(type);
    getDefaultListableBeanFactory().registerBeanDefinition(beanName, bd);
  }

}
