package net.stickycode.mockwire;

import java.lang.annotation.Annotation;

import javax.inject.Inject;


public class AnnotationMatcher {

  private Class<?>[] annotations;

  public AnnotationMatcher(Class<?>... annotations) {
    for (Class<?> c : annotations) {
      if (!c.isAnnotation())
        throw new CodingException("The given class {} is not an annotation", c);
    }
    this.annotations = annotations;
  }

}
