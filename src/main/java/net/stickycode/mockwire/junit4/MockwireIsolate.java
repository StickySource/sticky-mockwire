package net.stickycode.mockwire.junit4;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import net.stickycode.mockwire.Mockwire;

public class MockwireIsolate extends BlockJUnit4ClassRunner {

  public MockwireIsolate(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected Object createTest() throws Exception {
    Object test = super.createTest();
    Mockwire.isolate(test);
    return test;
  }

}
