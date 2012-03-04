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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import net.stickycode.reflector.AnnotatedMethodProcessor;

class UncontrolledAnnotatedMethodProcessor
    extends AnnotatedMethodProcessor {

  private final IsolatedTestManifest manifest;

  private static Class<? extends Annotation>[] factoryMarkers = AnnotationFinder.load("mockwire", "factory");

  UncontrolledAnnotatedMethodProcessor(IsolatedTestManifest manifest) {
    super(factoryMarkers);
    this.manifest = manifest;
  }

  @Override
  public void processMethod(Object target, Method method) {
    manifest.registerBean(method.getName(), invoke(target, method, manifest), method.getReturnType());
  }

  @Override
  public boolean canProcess(Method method) {
    if (!super.canProcess(method))
      return false;

    if (method.getReturnType().getName().equals("void"))
      throw new VoidMethodsCannotBeUsedAsFactoriesForCodeUnderTestException(method);

    return true;
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
    }
    catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sort(List<Method> methods) {
    Collections.sort(methods, new MethodDependencyComparator());
  }

}
