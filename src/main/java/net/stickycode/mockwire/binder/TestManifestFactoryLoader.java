package net.stickycode.mockwire.binder;

import net.stickycode.mockwire.IsolatedTestManifest;
import net.stickycode.mockwire.binder.TestManifestFactory;

public class TestManifestFactoryLoader {

  public static IsolatedTestManifest load() {
    try {
      Class<?> klass = Class.forName(TestManifestFactory.class.getName() + "Binder");
      if (TestManifestFactory.class.isAssignableFrom(klass))
        return ((TestManifestFactory) klass.newInstance()).create();

      throw new IllegalStateException("Class " + klass.getName() + " should implement " + TestManifestFactory.class.getName());
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException("You must have an Mocker implmentation to run Mockwire", e);
    }
    catch (InstantiationException e) {
      throw new IllegalStateException("You must have an Mocker implmentation to run Mockwire", e);
    }
    catch (IllegalAccessException e) {
      throw new IllegalStateException("You must have an Mocker implmentation to run Mockwire", e);
    }
  }

}
