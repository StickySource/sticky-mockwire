package net.stickycode.mockwire;

import net.stickycode.exception.PermanentException;

@SuppressWarnings("serial")
public class ConfiguredFieldNotFoundForConfigurationException
    extends PermanentException {

  public ConfiguredFieldNotFoundForConfigurationException(Class<?> type, String fieldName, String s) {
    super("Configuration {} could not be applied to {} as there is no field called {}",
        new Object[] {s, type.getSimpleName(), fieldName});
  }

}
