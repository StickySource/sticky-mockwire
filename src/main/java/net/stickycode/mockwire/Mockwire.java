package net.stickycode.mockwire;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.stickycode.mockwire.mockito.MockitoMocker;
import net.stickycode.mockwire.spring25.SpringIsolateTestManifest;


public class Mockwire {

  public static void isolate(Object testInstance) {
    if (testInstance == null)
      throw new CodingException("You passed null when a test instance was expected");

    IsolatedTestManifest manifest = getManifest(testInstance);
    manifest.autowire(testInstance);
  }

  private static IsolatedTestManifest getManifest(Object testInstance) {
    final IsolatedTestManifest manifest = new SpringIsolateTestManifest();
    final Mocker mocker = new MockitoMocker();
    new Reflector()
      .forEachField(
        new AnnotationFieldProcessor(Mock.class) {
          @Override
          public void processField(Object target, Field field) {
            manifest.registerBean(field.getName(), mocker.mock(field.getType()));
          }
        },
        new AnnotationFieldProcessor(Bless.class) {
          @Override
          public void processField(Object target, Field field) {
            manifest.registerType(field.getName(), field.getType());
          }
        })
      .forEachMethod(new AnnotationMethodProcessor(Bless.class) {
        @Override
        public void processMethod(Object target, Method method) {
          manifest.registerBean(method.getName(), invoke(target, method, manifest));
        }
      })
       .process(testInstance);

    return manifest;
  }

}
