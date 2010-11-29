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

import net.stickycode.exception.NullParameterException;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public abstract class AbstractIsolationTest {

	@Mock
	private Mockable m;

	@UnderTest
	private Autowirable a;

	@UnderTest
	private AutowirableWithDependencies d;

	@Inject
	private AutowirableWithDependencies injected;

	@Inject
	IsolatedTestManifest context;

	@Before
	public void setup() {
		assertThat(d).isNotNull();
		assertThat(m).isNotNull();
		assertThat(a).isNotNull();
	}

	@Test
	public void objectsAreAutowired() {
		assertThat(injected).isNotNull();
		assertThat(injected.getMock()).isNotNull();
		assertThat(injected.getAutowirable()).isNotNull();
	}

	@Test(expected = NullParameterException.class)
	public void nullParameterIsIllegal() {
		Mockwire.isolate(null);
	}

}
