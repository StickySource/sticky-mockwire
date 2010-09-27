package net.stickycode.mockwire;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.junit4.MockwireContext;


public class MockwireIsolator implements MockwireContext {

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
