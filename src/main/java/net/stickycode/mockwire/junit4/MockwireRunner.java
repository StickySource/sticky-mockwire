package net.stickycode.mockwire.junit4;

import net.stickycode.mockwire.Controlled;
import net.stickycode.mockwire.MockwireContext;
import net.stickycode.mockwire.UnderTest;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A jUnit runner to make your test classes and code behave like it would when run live in a di context ala Mockwire.
 * 
 * The default context used for Dependency Injection is a manifest defined by the test class itself. It will only contain
 * {@link UnderTest code under test} and {@link Controlled controlled} classes in the actual test class or its super types.
 * 
 * <pre>
 * package net.stickycode.example;
 * 
 * &#064;RunWith(MockwireRunner.class)
 * public class ContainedTest {
 * 
 *   &#064;UnderTest
 *   SomeConcreteClass field;
 * 
 *   &#064;Inject
 *   SomeConcreteClass injectedInstanceOfBlessesClass;
 * 
 *   &#064;Inject
 *   IsolateTestContext context;
 * 
 *   &#064;Test
 *   public void testManifestHasCodeUnderTest() {
 *     assertThat(context.hasRegisteredType(ConcreteClass.class)).isTrue();
 *   }
 * }
 * 
 * </pre>
 */
public class MockwireRunner
    extends BlockJUnit4ClassRunner {

  private final class MockwireTestMethodLifecycleStatement
      extends Statement {

    private final Statement statement;

    private final Object test;

    private MockwireTestMethodLifecycleStatement(Statement statement, Object test) {
      this.statement = statement;
      this.test = test;
    }

    @Override
    public void evaluate() throws Throwable {
      try {
        mockwire.initialiseTestInstance(test);
      }
      catch (Throwable t) {
        throw new AssertionError(t);
      }
      statement.evaluate();
    }
  }

  private final class MockwireContextLifecycleStatement
      extends Statement {

    private final Statement wrappedStatement;

    private MockwireContextLifecycleStatement(Statement classBlock) {
      this.wrappedStatement = classBlock;
    }

    @Override
    public void evaluate() throws Throwable {
      try {
        mockwire.startup();
      }
      catch (Throwable t) {
        throw new AssertionError(t);
      }
      try {
        wrappedStatement.evaluate();
      }
      finally {
        try {
          mockwire.shutdown();
        }
        catch (Throwable t) {
          throw new AssertionError(t);
        }
      }
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
  protected Statement methodBlock(FrameworkMethod method) {
    mockwire.setTestName(method.getName());
    // TODO put the method name into configuration as test.name
    if (!mockwire.isolateLifecycles())
      return super.methodBlock(method);

    return new MockwireContextLifecycleStatement(super.methodBlock(method));
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, final Object test) {
    return new MockwireTestMethodLifecycleStatement(super.methodInvoker(method, test), test);
  }
}
