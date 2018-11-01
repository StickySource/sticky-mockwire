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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.stickycode.configuration.ConfigurationValue;
import net.stickycode.configuration.ResolvedConfiguration;
import net.stickycode.mockwire.ClasspathResourceNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class MockwireConfigurationSourceTest {

  private class PrivateMemberClass {

  }

  @Mock
  ResolvedConfiguration mock;

  @Test
  public void stringProperties() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();

    s.add(getClass(), "a=b");

    s.apply(new PlainKey("a"), mock);

    verify(mock).add(any(ConfigurationValue.class));
  }

  @Test
  public void fileProperties() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), "configured.properties");
    s.apply(new PlainKey("ainfile"), mock);
    verify(mock).add(any(ConfigurationValue.class));
  }

  @Test(expected = ClasspathResourceNotFoundException.class)
  public void fileNotFound() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), "whereisit.properties");
  }

  @Test
  public void filePropertiesFromMemberClass() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(new PrivateMemberClass().getClass(), "configured.properties");
    s.apply(new PlainKey("ainfile"), mock);

    verify(mock).add(any(ConfigurationValue.class));
  }

  @Test
  public void filePropertiesFromRootOfClasspath() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(new PrivateMemberClass().getClass(), "root.properties");
    s.apply(new PlainKey("root"), mock);

    verify(mock).add(any(ConfigurationValue.class));
  }

  @Test
  public void filePropertiesFromOnePackageUpInClasspath() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(new PrivateMemberClass().getClass(), "oneup.properties");
    s.apply(new PlainKey("oneup"), mock);

    verify(mock).add(any(ConfigurationValue.class));
  }

  @Test
  public void filePropertiesFromManyFiles() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), new String[] { "configured.properties", "configured2.properties" });

    s.apply(new PlainKey("ainfile"), mock);

    s.apply(new PlainKey("ainfile2"), mock);

    verify(mock, times(2)).add(any(ConfigurationValue.class));
  }

}
