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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import net.stickycode.mockwire.ConfiguredFieldNotFoundForConfigurationException;
import net.stickycode.mockwire.InvalidConfigurationException;
import net.stickycode.mockwire.IsolatedTestManifest;
import net.stickycode.mockwire.UnderTest;
import net.stickycode.mockwire.UnderTestAnnotatedFieldProcessor;
import net.stickycode.mockwire.UnderTestAnnotatedFieldProcessor.MockwireConfigurationSourceProvider;
import net.stickycode.stereotype.configured.Configured;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class UnderTestAnnotatedFieldProcessorTest {

  MockwireConfigurationSource source = mock(MockwireConfigurationSource.class);
  MockwireConfigurationSourceProvider sourceProvider = new MockwireConfigurationSourceProvider() {
    @Override
    public MockwireConfigurationSource getConfigurationSource() {
      return source;
    }
  };
  IsolatedTestManifest manifest = mock(IsolatedTestManifest.class);
  
  @UnderTest("")
  String emptyStringExcepts;
  
  @Test(expected=InvalidConfigurationException.class)
  public void emptyStringExcepts() {
    process();
  }
  
  @UnderTest("value")
  String noEqualsExcepts;
  
  @Test(expected=InvalidConfigurationException.class)
  public void noEqualsExcepts() {
    process();
  }
  
  @UnderTest("=value")
  String noKeyExcepts;
  
  @Test(expected=InvalidConfigurationException.class)
  public void noKeyExcepts() {
    process();
  }
  
  @UnderTest("field=something")
  String missingField;
  
  @Test(expected=ConfiguredFieldNotFoundForConfigurationException.class)
  public void missingField() {
    process();
  }
  @UnderTest("offset=0")
  String fieldNotConfigured;
  
  @Test(expected=ConfiguredFieldNotFoundForConfigurationException.class)
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
    verify(source).addValue(eq("configuredSomething.something"), eq("value"));
  }
  
  @Rule
  public TestName name = new TestName();
  
  public void process() {
    Field f = getField(name.getMethodName());
    underTestProcessor().processField(this, f);
  }

  private UnderTestAnnotatedFieldProcessor underTestProcessor() {
    return new UnderTestAnnotatedFieldProcessor(manifest, sourceProvider);
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
