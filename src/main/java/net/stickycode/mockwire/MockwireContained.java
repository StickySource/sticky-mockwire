package net.stickycode.mockwire;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.junit4.MockwireContext;


public class MockwireContained implements MockwireContext {
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
    if (containment == null || containment.value().length == 0)
      return new String[] {(packageToPath(testClass.getPackage()))};

    for (String path : containment.value())
      if (!path.startsWith("/"))
        throw new CodingException("The scan root '{}' for containment must start with /", path);

    return containment.value();
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
        new AnnotatedFieldProcessor(Mock.class) {
          @Override
          public void processField(Object target, Field field) {
            manifest.registerBean(field.getName(), mocker.mock(field.getType()), field.getType());
          }
        },
        new AnnotatedFieldProcessor(Bless.class) {
          @Override
          public void processField(Object target, Field field) {
            manifest.registerType(field.getName(), field.getType());
          }
        })
      .forEachMethod(new AnnotatedMethodProcessor(Bless.class) {
        @Override
        public void processMethod(Object target, Method method) {
          manifest.registerBean(method.getName(), invoke(target, method, manifest), method.getReturnType());
        }
      })
       .process(testInstance);

    return manifest;
  }

}
