package net.stickycode.mockwire;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public abstract class AnnotationMethodProcessor
    implements MethodProcessor {

  private Class<? extends Annotation> annotation;

  public AnnotationMethodProcessor(Class<? extends Annotation> annotation) {
    this.annotation = annotation;
  }

  public Object invoke(Object target, Method method, ParameterSource parameterSource) {
    return getValue(target, method, getParameters(method, parameterSource));
  }

  private Object[] getParameters(Method method, ParameterSource parameterSource) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length == 0)
      return new Object[0];

    Object[] parameters = new Object[parameterTypes.length];
    for (int i = 0; i < parameters.length; i++) {
      parameters[i] = parameterSource.getBeanOfType(parameterTypes[i]);
    }
    return parameters;
  }

  private static Object getValue(Object t, Method method, Object[] parameters) {
    try {
      return method.invoke(t, parameters);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean canProcess(Method method) {
    return method.isAnnotationPresent(annotation);
  }

  @Override
  public void sort(List<Method> methods) {
    Collections.sort(methods, new MethodDependencyComparator());
  }

}
