package net.stickycode.mockwire.configured;

import net.stickycode.configuration.ConfigurationValue;
import net.stickycode.configuration.ResolvedConfiguration;

final class ResolvedConfigurations
    implements ResolvedConfiguration {

  private ConfigurationValue value;

  @Override
  public boolean hasValue() {
    return value != null;
  }

  @Override
  public String getValue() {
    return value.get();
  }

  @Override
  public void add(ConfigurationValue value) {
    this.value = value;
  }
}