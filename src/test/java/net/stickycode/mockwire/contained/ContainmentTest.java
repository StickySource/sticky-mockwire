package net.stickycode.mockwire.contained;

import javax.inject.Inject;

import net.stickycode.mockwire.MockwireContainment;
import net.stickycode.mockwire.junit4.MockwireRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockwireRunner.class)
@MockwireContainment
public class ContainmentTest {

  @Inject
  ScannedComponent scanned;

  @Test
  public void check() {
    assertThat(scanned).isNotNull();
  }

}
