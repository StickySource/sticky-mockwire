package net.stickycode.mockwire;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a field as defining a bean in an isolated test context.
 *
 * In the following example <code>ConcreteClass</code> will be blessed into a singleton in the isolate test context created
 * by <code>Mockwire.isolate()</code>.
 *
 * <pre>
 *  public class MockwireTest {
 *
 *  &#064;Bless
 *  ConcreteClass field;
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
 *    assertThat(context.getBeanNamesForType(ConcreteClass.class)).hasSize(1);
 *  }
 *
 * </pre>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Bless {

}
