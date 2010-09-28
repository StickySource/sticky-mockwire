package net.stickycode.mockwire;

import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.junit4.MockwireContext;
import net.stickycode.mockwire.reflector.Reflector;

public class MockwireIsolator
    implements MockwireContext {

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("MockwireIsolator v" + version + " see http://stickycode.net/mockwire");
  }

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
            new MockAnnotatedFieldProcessor(Mock.class, manifest, mocker),
            new BlessAnnotatedFieldProcessor(Bless.class, manifest))
        .forEachMethod(
            new BlessAnnotatedMethodProcessor(Bless.class, manifest))
        .process(testInstance);

    return manifest;
  }

}
