package net.stickycode.mockwire.spring25;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.support.GenericApplicationContext;


public class DisposableBeanAdapter
    implements DisposableBean {

  private final GenericApplicationContext context;
  private final Object bean;
  private final String beanName;

  public DisposableBeanAdapter(Object bean, String beanName, GenericApplicationContext context) {
    super();
    this.context = context;
    this.bean = bean;
    this.beanName = beanName;
  }

  @Override
  public void destroy() throws Exception {
    @SuppressWarnings("unchecked")
    List<BeanPostProcessor> beanPostProcessors = context.getDefaultListableBeanFactory().getBeanPostProcessors();
    for (BeanPostProcessor p : beanPostProcessors) {
      if (p instanceof DestructionAwareBeanPostProcessor)
        ((DestructionAwareBeanPostProcessor)p).postProcessBeforeDestruction(bean, beanName);
    }
  }

}
