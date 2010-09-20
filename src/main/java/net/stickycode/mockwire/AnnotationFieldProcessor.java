package net.stickycode.mockwire;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public abstract class AnnotationFieldProcessor
    implements FieldProcessor {

  private Class<? extends Annotation> annotationClass;

  public AnnotationFieldProcessor(Class<? extends Annotation> annotation) {
    this.annotationClass = annotation;
  }


  @Override
  public boolean canProcess(Field field) {
    return field.isAnnotationPresent(annotationClass);
  }

}
