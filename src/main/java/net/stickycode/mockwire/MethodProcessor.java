package net.stickycode.mockwire;

import java.lang.reflect.Method;
import java.util.List;


public interface MethodProcessor {

  void processMethod(Object target, Method method);

  boolean canProcess(Method method);

  void sort(List<Method> methods);

}
