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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class UnderTestAnnotatedFieldProcessorTest {

  @UnderTest
  String underTest;

  public static class StaticMember {
  }

  @UnderTest
  StaticMember staticMember;

  String other;

  @Controlled
  String controlled;

  @Test
  public void annotationDetection() {
    assertThat(canProcess("underTest")).isTrue();
    assertThat(canProcess("controlled")).isFalse();
    assertThat(canProcess("other")).isFalse();
    assertThat(canProcess("staticMember")).isTrue();
  }

  public interface Interface {
  }

  @UnderTest
  Interface iface;

  @Test(expected = InterfacesCannotBePutUnderTestException.class)
  public void interfacesError() {
    canProcess("iface");
  }

  public class NonStaticMember {
  }

  @UnderTest
  NonStaticMember nonStatic;

  @Test(expected = NonStaticMemberTypesCannotBePutUnderTestException.class)
  public void nonStaticMemberTypesNotInstantiable() {
    canProcess("nonStatic");
  }

  private boolean canProcess(String name) {
    Field f = getField(name);
    return underTestProcessor().canProcess(f);
  }

  private UnderTestAnnotatedFieldProcessor underTestProcessor() {
    return new UnderTestAnnotatedFieldProcessor(null, null);
  }

  private Field getField(String name) {
    try {
      return getClass().getDeclaredField(name);
    }
    catch (SecurityException e) {
      throw new RuntimeException(e);
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

}
