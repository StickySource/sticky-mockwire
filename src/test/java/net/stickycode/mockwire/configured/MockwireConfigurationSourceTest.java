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
package net.stickycode.mockwire.configured;

import org.junit.Test;

import net.stickycode.mockwire.ClasspathResourceNotFoundException;
import net.stickycode.mockwire.configured.MockwireConfigurationSource;

import static org.fest.assertions.Assertions.assertThat;



public class MockwireConfigurationSourceTest {

  private class PrivateMemberClass {

  }

  @Test
  public void stringProperties() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();

    s.add(getClass(), "a=b");
    assertThat(s.hasValue("a")).isTrue();
    assertThat(s.getValue("a")).isEqualTo("b");

    s.add(getClass(), "c=d");
    assertThat(s.hasValue("c")).isTrue();
    assertThat(s.getValue("c")).isEqualTo("d");
  }

  @Test
  public void fileProperties() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), "configured.properties");
    assertThat(s.hasValue("ainfile")).isTrue();
    assertThat(s.getValue("ainfile")).isEqualTo("binfile");
  }

  @Test(expected=ClasspathResourceNotFoundException.class)
  public void fileNotFound() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), "whereisit.properties");
  }

  @Test
  public void filePropertiesFromMemberClass() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(new PrivateMemberClass().getClass(), "configured.properties");
    assertThat(s.hasValue("ainfile")).isTrue();
    assertThat(s.getValue("ainfile")).isEqualTo("binfile");
  }

  @Test
  public void filePropertiesFromManyClasses() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), new String[]{"configured.properties", "configured2.properties"});
    assertThat(s.hasValue("ainfile")).isTrue();
    assertThat(s.getValue("ainfile")).isEqualTo("binfile");
    assertThat(s.hasValue("ainfile2")).isTrue();
    assertThat(s.getValue("ainfile2")).isEqualTo("binfile2");
  }

}
