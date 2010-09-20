package net.stickycode.mockwire;

import java.lang.reflect.Method;
import java.util.Comparator;


public class MethodDependencyComparator
    implements Comparator<Method> {

  public int compare(Method f, Method l) {
    if (hasNoDependencies(f))
      return -1;

    if (hasNoDependencies(l))
      return +1;

    if (isDependendentOn(f, l))
      return +1;

    if (isDependendentOn(l, f))
      return -1;

    return 0;
  }

  private boolean isDependendentOn(Method parameters, Method type) {
    return isDependendentOn(parameters.getParameterTypes(), type.getReturnType());
  }

  boolean isDependendentOn(Class<?>[] parameters, Class<?> type) {
    return parametersContainReturnType(parameters, type);
  }

  private boolean hasNoDependencies(Method m) {
    return hasNoDependencies(m.getParameterTypes());
  }

  boolean hasNoDependencies(Class<?>[] parameters) {
    return parameters.length == 0;
  }

  boolean parametersContainReturnType(Class<?>[] parameterTypes, Class<?> returnType) {
    for (Class<?> c : parameterTypes) {
      if (c.isAssignableFrom(returnType))
        return true;

    }
    return false;
  }

}
