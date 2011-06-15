package net.stickycode.mockwire;

import net.stickycode.exception.PermanentException;

@SuppressWarnings("serial")
public class InvalidConfigurationException
    extends PermanentException {

  public InvalidConfigurationException(Class<?> type, String s) {
    super("Failed to create configuration for {} with '{}' expected something like blah=value",
        type.getSimpleName(), s);
  }

}
