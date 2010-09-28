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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.stickycode.mockwire.reflector.AnnotatedFieldProcessor;

class BlessAnnotatedFieldProcessor
    extends AnnotatedFieldProcessor {

  private final IsolatedTestManifest manifest;

  BlessAnnotatedFieldProcessor(Class<? extends Annotation> annotation, IsolatedTestManifest manifest) {
    super(annotation);
    this.manifest = manifest;
  }

  @Override
  public void processField(Object target, Field field) {
    manifest.registerType(field.getName(), field.getType());
  }

  @Override
  public boolean canProcess(Field field) {
    if (!super.canProcess(field))
      return false;

    if (field.getType().isInterface())
      throw cannotBlessInterfacesException(field);

    if (!field.getType().isMemberClass())
      return true;

    if (Modifier.isStatic(field.getType().getModifiers()))
      return true;

    throw cannotBlessNonStaticTypesException(field);
  }

  private UnblessabledTypeException cannotBlessInterfacesException(Field field) {
    return new UnblessabledTypeException(
        "@Bless'd field '{}' on test '{}' is not instantiable with type '{}'. Blessing is used to identify the code you wish to test did you mean @Mock instead?",
        new Object[] { field.getName(), field.getType().getName() });
  }

  private UnblessabledTypeException cannotBlessNonStaticTypesException(Field field) {
    return new UnblessabledTypeException(
        "@Bless'd field '{}' on test '{}' has non static inner class '{}' as type. Add static modifier to it so it can be blessed.\n"
            +
            "For example\n" +
            "public class SomeTest {\n" +
            "  private class InnerType {\n" +
            "  }\n" +
            "}\n" +
            "Should look more like\n" +
            "public class SomeTest {\n" +
            "  private static class InnerType {\n" +
            "  }\n" +
            "}\n",
        new Object[] { field.getName(), field.getType().getName() });
  }
}
