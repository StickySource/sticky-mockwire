/**
 * Copyright (c) 2010 RedEngine Ltd, http://www.redengine.co.nz. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package net.stickycode.mockwire;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
