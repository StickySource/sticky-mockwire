package net.stickycode.mockwire;

import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.junit4.MockwireContext;
import net.stickycode.reflector.Reflector;

public class MockwireIsolator
    implements MockwireContext {

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("MockwireIsolator v" + version + " see http://stickycode.net/mockwire");
  }

  @SuppressWarnings("unused")
  private Class<?> testClass;

  public MockwireIsolator(Class<?> testClass) {
    this.testClass = testClass;
  }

  public void initialiseTestInstance(Object testInstance) {
    // TODO this should call create on the factory!
    Mocker mocker = MockerFactoryLoader.load();
    // TODO this should call create on the factory!
    IsolatedTestManifest manifest = TestManifestFactoryLoader.load();

    process(manifest, mocker, testInstance);
    manifest.autowire(testInstance);
  }

  private IsolatedTestManifest process(final IsolatedTestManifest manifest, final Mocker mocker, Object testInstance) {
    new Reflector()
        .forEachField(
            new MockAnnotatedFieldProcessor(manifest, mocker, Controlled.class, Mock.class),
            new BlessAnnotatedFieldProcessor(manifest, UnderTest.class, Bless.class))
        .forEachMethod(
            new BlessAnnotatedMethodProcessor(manifest, UnderTest.class, Bless.class))
        .process(testInstance);

    return manifest;
  }

}
