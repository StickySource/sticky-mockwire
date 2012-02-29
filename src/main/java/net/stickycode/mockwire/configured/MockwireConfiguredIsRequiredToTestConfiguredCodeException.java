package net.stickycode.mockwire.configured;

import net.stickycode.exception.PermanentException;


@SuppressWarnings("serial")
public class MockwireConfiguredIsRequiredToTestConfiguredCodeException
    extends PermanentException {

  
  public MockwireConfiguredIsRequiredToTestConfiguredCodeException() {
    super("You need to add @MockwireConfigured to the test in order for objects in the test context to be configured");
  }
}
