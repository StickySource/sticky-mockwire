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

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.stickycode.mockwire.junit4.MockwireRunner;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockwireRunner.class)
public class DependentMethodBlessingTest {

  public class AutowirableWithAutowirable {
    AutowirableWithMockable autowirableWithMockable;

    public AutowirableWithAutowirable(AutowirableWithMockable autowirable) {
      this.autowirableWithMockable = autowirable;
    }
  }

  public class AutowirableWithMockable {
    Mockable mockable;

    public AutowirableWithMockable(Mockable mockable) {
      this.mockable = mockable;
    }
  }

  @Mock
  Mockable mockable;

	@Inject
	AutowirableWithMockable injected;

	@Inject
	AutowirableWithAutowirable nested;

	@Bless
  public AutowirableWithMockable factory(Mockable mockable) {
    return new AutowirableWithMockable(mockable);
  }

	@Bless
	public AutowirableWithAutowirable dependency(AutowirableWithMockable autowirable) {
	  return new AutowirableWithAutowirable(autowirable);
	}

	@Inject
	IsolatedTestManifest context;

	@Test
	public void atBless() {
	  assertThat(context.hasRegisteredType(AutowirableWithMockable.class)).isTrue();
	  assertThat(injected).isNotNull();
	  assertThat(injected.mockable).isNotNull();
	  assertThat(nested).isNotNull();
	  assertThat(context.hasRegisteredType(AutowirableWithAutowirable.class)).isTrue();
	  assertThat(nested.autowirableWithMockable).isNotNull();
	}
}
