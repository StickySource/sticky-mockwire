package net.stickycode.mockwire.binder;

import net.stickycode.mockwire.IsolatedTestManifest;

public class TestManifestFactoryLoader {

  private static ContextManifestFactory SINGLETON;

  public static IsolatedTestManifest load() {
    if (SINGLETON != null)
      return SINGLETON.create();

    try {
      Class<?> klass = Class.forName(ContextManifestFactory.class.getName() + "Binder");
      if (ContextManifestFactory.class.isAssignableFrom(klass)) {
        SINGLETON = (ContextManifestFactory) klass.newInstance();
        return SINGLETON.create();
      }

      throw new IllegalStateException("Class " + klass.getName() + " should implement " + ContextManifestFactory.class.getName());
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
  static void preset(ContextManifestFactory manifest) {
    SINGLETON = manifest;
  }

}
