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

import net.stickycode.reflector.AnnotatedFieldProcessor;

class UnderTestAnnotatedFieldProcessor
    extends AnnotatedFieldProcessor {

  private final IsolatedTestManifest manifest;

  UnderTestAnnotatedFieldProcessor(IsolatedTestManifest manifest, Class<? extends Annotation>... annotation) {
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
      throw new InterfacesCannotBePutUnderTestException(field);

    if (!field.getType().isMemberClass())
      return true;

    if (Modifier.isStatic(field.getType().getModifiers()))
      return true;

    throw new NonStaticMemberTypesCannotBePutUnderTestException(field);
  }

}
