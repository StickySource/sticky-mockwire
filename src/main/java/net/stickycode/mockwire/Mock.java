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
