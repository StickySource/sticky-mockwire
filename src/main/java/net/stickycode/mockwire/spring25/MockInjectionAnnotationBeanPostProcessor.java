package net.stickycode.mockwire.spring25;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

import net.stickycode.mockwire.Mock;


public class MockInjectionAnnotationBeanPostProcessor
    extends AutowiredAnnotationBeanPostProcessor {

  @Override
  protected Class<? extends Annotation> getAutowiredAnnotationType() {
    return Mock.class;
  }

}
