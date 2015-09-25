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

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import mockit.Mocked;
import mockit.Verifications;
import net.stickycode.configuration.ConfigurationKey;
import net.stickycode.configuration.ConfigurationValue;
import net.stickycode.configuration.ResolvedConfiguration;
import net.stickycode.mockwire.ClasspathResourceNotFoundException;

public class MockwireConfigurationSourceTest {

  private final class PlainKey
      implements ConfigurationKey {

    private String key;

    public PlainKey(String key) {
      this.key = key;
    }

    @Override
    public List<String> join(String delimeter) {
      return Collections.singletonList(key);
    }

    @Override
    public void apply(ResolvedConfiguration resolution) {
    }
  }

  private class PrivateMemberClass {

  }

  @Mocked
  ResolvedConfiguration mock;

  @Test
  public void stringProperties() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();

    s.add(getClass(), "a=b");

    s.apply(new PlainKey("a"), mock);

    new Verifications() {
      {
        mock.add(withInstanceOf(ConfigurationValue.class));
      }
    };
  }

  @Test
  public void fileProperties() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), "configured.properties");
    s.apply(new PlainKey("ainfile"), mock);
    new Verifications() {
      {
        mock.add(withInstanceOf(ConfigurationValue.class));
      }
    };

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

    new Verifications() {
      {
        mock.add(withInstanceOf(ConfigurationValue.class));
      }
    };
  }

  @Test
  public void filePropertiesFromRootOfClasspath() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(new PrivateMemberClass().getClass(), "root.properties");
    s.apply(new PlainKey("root"), mock);

    new Verifications() {
      {
        mock.add(withInstanceOf(ConfigurationValue.class));
      }
    };
  }

  @Test
  public void filePropertiesFromOnePackageUpInClasspath() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(new PrivateMemberClass().getClass(), "oneup.properties");
    s.apply(new PlainKey("oneup"), mock);

    new Verifications() {
      {
        mock.add(withInstanceOf(ConfigurationValue.class));
      }
    };
  }

  @Test
  public void filePropertiesFromManyFiles() {
    MockwireConfigurationSource s = new MockwireConfigurationSource();
    s.add(getClass(), new String[] { "configured.properties", "configured2.properties" });

    s.apply(new PlainKey("ainfile"), mock);

    s.apply(new PlainKey("ainfile2"), mock);

    new Verifications() {
      {
        mock.add(withInstanceOf(ConfigurationValue.class));
      }
    };
  }

}
