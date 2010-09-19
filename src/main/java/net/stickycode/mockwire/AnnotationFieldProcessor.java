package net.stickycode.mockwire;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class AnnotationFieldProcessor
    implements FieldProcessor {

  private Class<? extends Annotation> annotationClass;

  public AnnotationFieldProcessor(Class<? extends Annotation> annotation) {
    this.annotationClass = annotation;
  }

  @Override
  public void processField(Field field) {
  }

  @Override
  public boolean canProcess(Field field) {
    return field.isAnnotationPresent(annotationClass);
  }

}
