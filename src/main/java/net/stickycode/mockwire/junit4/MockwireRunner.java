package net.stickycode.mockwire.junit4;

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import net.stickycode.mockwire.Controlled;
import net.stickycode.mockwire.MockwireContext;
import net.stickycode.mockwire.UnderTest;

/**
 * A jUnit runner to make your test classes and code behave like it would when run live in a di context ala Mockwire.
 *
 * The default context used for Dependency Injection is a manifest defined by the test class itself. It will only contain
 * {@link UnderTest code under test} and {@link Controlled controlled} classes in the actual test class or its super types.
 *
 * <pre>
 * package net.stickycode.example;
 *
 *  &#064;RunWith(MockwireRunner.class)
 *  public class ContainedTest {
 *
 *  &#064;UnderTest
 *  SomeConcreteClass field;
 *
 *  &#064;Inject
 *  SomeConcreteClass injectedInstanceOfBlessesClass;
 *
 *  &#064;Inject
 *  IsolateTestContext context;
 *
 *  &#064;Test
 *  public void testManifestHasCodeUnderTest() {
 *    assertThat(context.hasRegisteredType(ConcreteClass.class)).isTrue();
 *  }
 * }
 * </pre>
 */
public class MockwireRunner extends BlockJUnit4ClassRunner {

  private final class MockwireContextLifecycleStatement
      extends Statement {

    private final Statement wrappedStatement;

    private MockwireContextLifecycleStatement(Statement classBlock) {
      this.wrappedStatement = classBlock;
    }

    @Override
    public void evaluate() throws Throwable {
      mockwire.startup();
      wrappedStatement.evaluate();
      mockwire.shutdown();
    }
  }

  private final MockwireContext mockwire;

  public MockwireRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
    mockwire = new MockwireContext(testClass);
  }

  @Override
  protected Statement classBlock(RunNotifier notifier) {
    if (mockwire.isolateLifecycles())
      return super.classBlock(notifier);

    return new MockwireContextLifecycleStatement(super.classBlock(notifier));
  }

  @Override
  protected List<MethodRule> rules(Object test) {
    if (!mockwire.isolateLifecycles())
      return super.rules(test);

    List<MethodRule> rules = super.rules(test);
    rules.add(new MethodRule() {

      @Override
      public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new MockwireContextLifecycleStatement(base);
      }
    });
    return rules;
  }



  @Override
  protected Statement methodInvoker(FrameworkMethod method, final Object test) {
    final Statement statement = super.methodInvoker(method, test);
    return new Statement() {

      @Override
      public void evaluate() throws Throwable {
        mockwire.initialiseTestInstance(test);
        statement.evaluate();
      }
    };
  }
}
