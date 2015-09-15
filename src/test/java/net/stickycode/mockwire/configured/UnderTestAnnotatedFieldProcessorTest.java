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

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import mockit.Mocked;
import mockit.Verifications;
import net.stickycode.bootstrap.StickyBootstrap;
import net.stickycode.mockwire.ConfiguredFieldNotFoundForConfigurationException;
import net.stickycode.mockwire.InvalidConfigurationException;
import net.stickycode.mockwire.MockwireConfigured;
import net.stickycode.mockwire.MockwireMetadata;
import net.stickycode.mockwire.UnderTest;
import net.stickycode.mockwire.UnderTestAnnotatedFieldProcessor;
import net.stickycode.stereotype.configured.Configured;

@MockwireConfigured
public class UnderTestAnnotatedFieldProcessorTest {

  @Mocked
  MockwireConfigurationSource source;

  @Mocked
  StickyBootstrap bootstrap;

  MockwireMetadata metadata = new MockwireMetadata(getClass());

  @UnderTest("")
  String emptyStringExcepts;

  @Test(expected = InvalidConfigurationException.class)
  public void emptyStringExcepts() {
    process();
  }

  @UnderTest("value")
  String noEqualsExcepts;

  @Test(expected = InvalidConfigurationException.class)
  public void noEqualsExcepts() {
    process();
  }

  @UnderTest("=value")
  String noKeyExcepts;

  @Test(expected = InvalidConfigurationException.class)
  public void noKeyExcepts() {
    process();
  }

  @UnderTest("field=something")
  String missingField;

  @Test(expected = ConfiguredFieldNotFoundForConfigurationException.class)
  public void missingField() {
    process();
  }

  @UnderTest("offset=0")
  String fieldNotConfigured;

  @Test(expected = ConfiguredFieldNotFoundForConfigurationException.class)
  public void fieldNotConfigured() {
    process();
  }

  public static class ConfiguredSomething {
    @Configured
    Boolean something;
  }

  @UnderTest("something=value")
  ConfiguredSomething keyValue;

  @Test
  public void keyValue() {
    process();

    new Verifications() {
      {
        source.addValue("configuredSomething.something", "value");
      }
    };
  }

  @Rule
  public TestName name = new TestName();

  public void process() {
    Field f = getField(name.getMethodName());
    underTestProcessor().processField(this, f);
  }

  private UnderTestAnnotatedFieldProcessor underTestProcessor() {
    return new UnderTestAnnotatedFieldProcessor(bootstrap, metadata);
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
