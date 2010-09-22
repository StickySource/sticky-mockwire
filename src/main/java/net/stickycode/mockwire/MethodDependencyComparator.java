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

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * This comparator orders method in terms of return types being dependencies of other method signatures.
 *
 * For example
 *
 * <pre>
 * public A bean1() {
 * }
 *
 * public B bean2(A a) {
 * }
 * </pre>
 *
 * would result in order of bean1 before bean2 as bean1 returns a parameter of bean2.
 */
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
