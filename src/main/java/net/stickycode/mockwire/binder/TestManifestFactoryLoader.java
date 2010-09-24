package net.stickycode.mockwire.binder;

import net.stickycode.mockwire.IsolatedTestManifest;
import net.stickycode.mockwire.binder.TestManifestFactory;

public class TestManifestFactoryLoader {

  private static TestManifestFactory SINGLETON;

  public static IsolatedTestManifest load() {
    if (SINGLETON != null)
      return SINGLETON.create();

    try {
      Class<?> klass = Class.forName(TestManifestFactory.class.getName() + "Binder");
      if (TestManifestFactory.class.isAssignableFrom(klass)) {
        SINGLETON = (TestManifestFactory) klass.newInstance();
        return SINGLETON.create();
      }

      throw new IllegalStateException("Class " + klass.getName() + " should implement " + TestManifestFactory.class.getName());
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException("You must have an TestManifestFactory implmentation to run Mockwire", e);
    }
    catch (InstantiationException e) {
      throw new IllegalStateException("You must have an TestManifestFactory implmentation to run Mockwire", e);
    }
    catch (IllegalAccessException e) {
      throw new IllegalStateException("You must have an TestManifestFactory implmentation to run Mockwire", e);
    }
  }

  /**
   * Used for highjacking the actual context implementation used;
   */
  static void preset(TestManifestFactory manifest) {
    SINGLETON = manifest;
  }

}
