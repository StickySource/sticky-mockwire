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
import java.lang.reflect.Method;

import net.stickycode.mockwire.reflector.AnnotatedMethodProcessor;

class BlessAnnotatedMethodProcessor
    extends AnnotatedMethodProcessor {

  private final IsolatedTestManifest manifest;

  BlessAnnotatedMethodProcessor(Class<? extends Annotation> annotation, IsolatedTestManifest manifest) {
    super(annotation);
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
      throw cannotBlessVoidMethodException(method);

    return true;
  }

  private CannotBlessVoidMethodException cannotBlessVoidMethodException(Method method) {
    return new CannotBlessVoidMethodException("Method {} on test {} is void so you can't bless it as a bean factory method",
        method.getName(), method.getDeclaringClass().getName());
  }
}