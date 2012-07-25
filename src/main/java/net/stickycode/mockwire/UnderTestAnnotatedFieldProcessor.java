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

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.stickycode.mockwire.configured.MockwireConfigurationSource;
import net.stickycode.reflector.AnnotatedFieldProcessor;
import net.stickycode.reflector.Methods;
import net.stickycode.stereotype.configured.Configured;

public class UnderTestAnnotatedFieldProcessor
    extends AnnotatedFieldProcessor {

  private static Class<? extends Annotation>[] subjects = AnnotationFinder.load("mockwire", "subject");

  public static interface MockwireConfigurationSourceProvider {

    MockwireConfigurationSource getConfigurationSource();
  }

  private final IsolatedTestManifest manifest;

  private final MockwireConfigurationSourceProvider configurationSource;

  public UnderTestAnnotatedFieldProcessor(IsolatedTestManifest manifest, MockwireConfigurationSourceProvider configurationSource) {
    super(subjects);
    this.manifest = manifest;
    this.configurationSource = configurationSource;
  }

  @Override
  public void processField(Object target, Field field) {
    manifest.registerType(field.getName(), field.getType());
    processConfiguration(field);
  }

  private void processConfiguration(Field field) {
    for (Class<? extends Annotation> subject : subjects) {
      Annotation a = field.getAnnotation(subject);
      if (a != null) {
        String[] config = Methods.invoke(a, a.annotationType(), "value");
        processConfiguration(field.getType(), config);
      }
    }
  }

  private void processConfiguration(Class<?> type, String[] value) {
    for (String s : value) {
      int index = s.indexOf('=');
      if (index < 1)
        throw new InvalidConfigurationException(type, s);

      String fieldName = s.substring(0, index);
      verifyField(type, fieldName, s);
      String key = Introspector.decapitalize(type.getSimpleName()) + "." + fieldName;
      addValue(key, s.substring(index + 1));
    }
  }

  private void verifyField(Class<?> type, String fieldName, String s) {
    try {
      Field f = type.getDeclaredField(fieldName);
      if (!f.isAnnotationPresent(Configured.class))
        throw new ConfiguredFieldNotFoundForConfigurationException(type, fieldName, s);
    }
    catch (SecurityException e) {
      throw new RuntimeException(e);
    }
    catch (NoSuchFieldException e) {
      throw new ConfiguredFieldNotFoundForConfigurationException(type, fieldName, s);
    }
  }

  private void addValue(String key, String value) {
    configurationSource.getConfigurationSource().addValue(key, value);
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
