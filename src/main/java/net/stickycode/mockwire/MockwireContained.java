package net.stickycode.mockwire;

import java.util.LinkedList;
import java.util.List;

import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.junit4.MockwireContext;
import net.stickycode.reflector.Reflector;

public class MockwireContained
    implements MockwireContext {

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("MockwireContainment v" + version + " see http://stickycode.net/mockwire");
  }
  private final Class<?> testClass;
  private final String[] scanRoots;

  public MockwireContained(Class<?> testClass, MockwireContainment containment) {
    this.testClass = testClass;
    this.scanRoots = deriveContainmentRoots(testClass, containment);
  }

  public MockwireContained(Class<?> testClass) {
    this.testClass = testClass;
    MockwireContainment containment = testClass.getAnnotation(MockwireContainment.class);
    this.scanRoots = deriveContainmentRoots(testClass, containment);
  }

  private String[] deriveContainmentRoots(Class<?> testClass2, MockwireContainment containment) {
    String packageAsPath = packageToPath(testClass.getPackage());
    if (containment == null || containment.value().length == 0)
      return new String[] { packageAsPath };

    List<String> paths = new LinkedList<String>();
    for (String path : containment.value()) {
      if (!path.startsWith("/"))
        throw new ScanRootsShouldStartWithSlashException(path);

      paths.add(path);
    }

    paths.add(packageAsPath);
    return paths.toArray(new String[paths.size()]);
  }

  private String packageToPath(Package p) {
    return "/" + p.getName().replace('.', '/');
  }

  @Override
  public void initialiseTestInstance(Object testInstance) {
    Mocker mocker = MockerFactoryLoader.load();
    IsolatedTestManifest manifest = TestManifestFactoryLoader.load();

    manifest.scanPackages(scanRoots);

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
