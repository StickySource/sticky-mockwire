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

import net.stickycode.mockwire.mockito.MockitoMocker;
import net.stickycode.mockwire.spring25.SpringIsolatedTestManifest;


public class Mockwire {

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("Using Mockwire v" + version + " see http://stickycode.net/mockwire");
  }

  public static void isolate(Object testInstance) {

    if (testInstance == null)
      throw new CodingException("You passed null when a test instance was expected");

    IsolatedTestManifest manifest = getManifest(testInstance);
    manifest.autowire(testInstance);
  }

  private static IsolatedTestManifest getManifest(Object testInstance) {
    final IsolatedTestManifest manifest = new SpringIsolatedTestManifest();
    final Mocker mocker = new MockitoMocker();
    new Reflector()
      .forEachField(
        new AnnotatedFieldProcessor(Mock.class) {
          @Override
          public void processField(Object target, Field field) {
            manifest.registerBean(field.getName(), mocker.mock(field.getType()));
          }
        },
        new AnnotatedFieldProcessor(Bless.class) {
          @Override
          public void processField(Object target, Field field) {
            manifest.registerType(field.getName(), field.getType());
          }
        })
      .forEachMethod(new AnnotatedMethodProcessor(Bless.class) {
        @Override
        public void processMethod(Object target, Method method) {
          manifest.registerBean(method.getName(), invoke(target, method, manifest));
        }
      })
       .process(testInstance);

    return manifest;
  }

}
