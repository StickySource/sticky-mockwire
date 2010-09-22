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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field as defining a mocked bean in an isolated test context.
 *
 * In the following example <code>AnInterface</code> will be <code>Mock</code>ed into a singleton in the isolate test context created
 * by <code>Mockwire.isolate()</code>.
 *
 * <pre>
 *  public class MockwireTest {
 *
 *  &#064;Mock
 *  AnInterface field;
 *
 *  &#064;Inject
 *  IsolateTestContext context;
 *
 *  @Before
 *  public void setup() {
 *  	Mockwire.isolate(this);
 *  }
 *
 *  @Test
 *  public void testBless() {
 *    assertThat(context.getBeanNamesForType(AnInterface)).hasSize(1);
 *  }
 *
 * </pre>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Mock {

}
