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


public abstract class AnnotatedFieldProcessor
    implements FieldProcessor {

  private Class<? extends Annotation> annotationClass;

  public AnnotatedFieldProcessor(Class<? extends Annotation> annotation) {
    this.annotationClass = annotation;
  }

  @Override
  public boolean canProcess(Field field) {
    if (!field.isAnnotationPresent(annotationClass))
      return false;

    if (!field.getType().isMemberClass())
      return true;

    if (Modifier.isStatic(field.getType().getModifiers()))
      return true;

    throw new CanNotBlessNonStaticInnerClassException(
        "@Bless'd field '{}' on test '{}' has non static inner class '{}' as type. Add static modifier to it so it can be blessed.\n" +
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
        new Object[] {field.getName(), field.getType().getName()});
  }

}
