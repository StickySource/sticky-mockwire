package net.stickycode.mockwire.configured;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.stickycode.configuration.ConfigurationKey;
import net.stickycode.configuration.ResolvedConfiguration;

final class PlainKey
    implements ConfigurationKey {

  private List<String> key;

  public PlainKey(String... components) {
    this.key = Arrays.asList(components);
  }

  @Override
  public List<String> join(String delimeter) {
    return Collections.singletonList(String.join(delimeter, key));
  }

  @Override
  public void apply(ResolvedConfiguration resolution) {
  }
}
