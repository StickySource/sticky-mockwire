package net.stickycode.mockwire;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Reflector {

  private List<FieldProcessor> fieldProcessors = new LinkedList<FieldProcessor>();
  private List<MethodProcessor> methodProcessors = new LinkedList<MethodProcessor>();

  public Reflector() {
  }

  public Reflector forEachField(FieldProcessor... processors) {
    for (FieldProcessor fieldProcessor : processors) {
      fieldProcessors.add(fieldProcessor);
    }
    return this;
  }

  public void process(Object target) {
    List<Method> methods = new ArrayList<Method>();

    Class<?> type = target.getClass();
    while (type != Object.class) {
      processFields(target, type);
      collectMethods(methods, target, type);
      type = type.getSuperclass();
    }

    processMethods(methods, target);
  }

  private void processMethods(List<Method> methods, Object target) {
    for (MethodProcessor processor : methodProcessors)
      processor.sort(methods);

    for (Method method : methods)
      for (MethodProcessor processor : methodProcessors)
        processor.processMethod(target, method);
  }

  private void collectMethods(List<Method> methods, Object target, Class<?> type) {
    for (Method method : type.getDeclaredMethods())
      if (canProcess(method))
        methods.add(method);
  }

  private boolean canProcess(Method method) {
    for (MethodProcessor processor : methodProcessors)
      if (processor.canProcess(method))
        return true;

    return false;
  }

  private void processFields(Object target, Class<?> type) {
    Field[] fields = type.getDeclaredFields();
    for (Field field : fields)
      for (FieldProcessor processor : fieldProcessors)
        if (processor.canProcess(field))
          processor.processField(target, field);

  }

  public Reflector forEachMethod(MethodProcessor... processors) {
    for (MethodProcessor methodProcessor : processors) {
      methodProcessors.add(methodProcessor);
    }
    return this;
  }

}
