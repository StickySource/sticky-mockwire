package net.stickycode.mockwire.direct;

import org.junit.Before;

import net.stickycode.mockwire.Mockwire;
import net.stickycode.mockwire.AbstractIsolationTest;


public class StaticMockwireIsolationTest
    extends AbstractIsolationTest {

  @Before
  public void setup() {
    Mockwire.isolate(this);
    super.setup();
  }
}
