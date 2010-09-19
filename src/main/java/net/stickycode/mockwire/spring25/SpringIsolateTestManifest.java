package net.stickycode.mockwire.spring25;

import java.beans.Introspector;

import org.mockito.internal.util.Decamelizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import net.stickycode.mockwire.IsolatedTestManifest;

public class SpringIsolateTestManifest
    extends GenericApplicationContext
    implements IsolatedTestManifest {

  private Logger log = LoggerFactory.getLogger(getClass());



  public SpringIsolateTestManifest() {
    super();

    AutowiredAnnotationBeanPostProcessor autowiredProcessor = new InjectAnnotationBeanPostProcessor();
    autowiredProcessor.setBeanFactory(getDefaultListableBeanFactory());
    getBeanFactory().addBeanPostProcessor(autowiredProcessor);

    getBeanFactory().registerSingleton(
        Introspector.decapitalize(getClass().getSimpleName()),
        this);
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
    getAutowireCapableBeanFactory().autowireBean(testInstance);
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
