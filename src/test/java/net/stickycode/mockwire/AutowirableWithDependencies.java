package net.stickycode.mockwire;

import javax.inject.Inject;

public class AutowirableWithDependencies {

  @Inject
  private Mockable mock;

  @Inject
  private Autowirable autowirable;

  public Mockable getMock() {
    return mock;
  }

  public Autowirable getAutowirable() {
    return autowirable;
  }

}
