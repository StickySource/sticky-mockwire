package net.stickycode.mockwire.junit4;

/**
 * A context that can be used to initialise a test instance, and manage the lifecycle of an injection system
 */
public interface MockwireContext {

  /**
   * Inject the given test instance to prepare it for test methods being invoked
   */
  void initialiseTestInstance(Object testInstance);

}
