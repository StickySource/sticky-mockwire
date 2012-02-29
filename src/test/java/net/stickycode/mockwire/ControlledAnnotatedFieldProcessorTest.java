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

public class ControlledAnnotatedFieldProcessorTest {

  @UnderTest
  String underTest;

  public static class StaticMember {
  }

  @Controlled
  StaticMember staticMember;

  String unmarked;

  @Controlled
  String controlled;

  public interface Interface {
  }

  @Controlled
  Interface iface;

  public class NonStaticMember {
  }

  @Controlled
  NonStaticMember nonStaticMember;

  @Test
  public void annotationDetection() {
    assertThat(canProcess("controlled")).isTrue();

    assertThat(canProcess("underTest")).isFalse();
    assertThat(canProcess("unmarked")).isFalse();

    assertThat(canProcess("iface")).isTrue();
    assertThat(canProcess("staticMember")).isTrue();
    assertThat(canProcess("nonStaticMember")).isTrue();
  }

  private boolean canProcess(String name) {
    Field f = getField(name);
    return underTestProcessor().canProcess(f);
  }

  private ControlledAnnotatedFieldProcessor underTestProcessor() {
    return new ControlledAnnotatedFieldProcessor(null, null);
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
