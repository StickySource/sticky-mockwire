package net.stickycode.mockwire.binder;

import net.stickycode.mockwire.Mocker;
import net.stickycode.mockwire.binder.MockerFactory;


public class MockerFactoryLoader {

  public static Mocker load() {
    try {
      Class<?> klass = Class.forName(MockerFactory.class.getName() + "Binder");
      if (MockerFactory.class.isAssignableFrom(klass))
        return ((MockerFactory)klass.newInstance()).create();

      throw new IllegalStateException("Class " + klass.getName() + " should implement " + MockerFactory.class.getName());
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
