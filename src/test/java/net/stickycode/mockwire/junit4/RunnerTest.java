package net.stickycode.mockwire.junit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.stickycode.mockwire.Autowirable;
import net.stickycode.mockwire.Bless;
import net.stickycode.mockwire.Mock;
import net.stickycode.mockwire.Mockable;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockwireIsolate.class)
public class RunnerTest {

  @Mock
  Mockable mockable;

  @Bless
  Autowirable autowirable;

  @Test
  public void test() {
    assertThat(mockable).isNotNull();
    assertThat(autowirable).isNull();
  }
}
