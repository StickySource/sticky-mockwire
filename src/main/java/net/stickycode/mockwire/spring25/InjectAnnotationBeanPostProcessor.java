package net.stickycode.mockwire.spring25;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;


public class InjectAnnotationBeanPostProcessor
    extends AutowiredAnnotationBeanPostProcessor {

  @Override
  protected Class<? extends Annotation> getAutowiredAnnotationType() {
    return Inject.class;
  }

}
