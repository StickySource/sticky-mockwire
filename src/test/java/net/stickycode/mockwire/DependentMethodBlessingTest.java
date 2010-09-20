package net.stickycode.mockwire;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class DependentMethodBlessingTest {

  public class AutowirableWithAutowirable {
    AutowirableWithMockable autowirableWithMockable;

    public AutowirableWithAutowirable(AutowirableWithMockable autowirable) {
      this.autowirableWithMockable = autowirable;
    }
  }

  public class AutowirableWithMockable {
    Mockable mockable;

    public AutowirableWithMockable(Mockable mockable) {
      this.mockable = mockable;
    }
  }

  @Mock
  Mockable mockable;

	@Inject
	AutowirableWithMockable injected;

	@Inject
	AutowirableWithAutowirable nested;

	@Bless
  public AutowirableWithMockable factory(Mockable mockable) {
    return new AutowirableWithMockable(mockable);
  }

	@Bless
	public AutowirableWithAutowirable dependency(AutowirableWithMockable autowirable) {
	  return new AutowirableWithAutowirable(autowirable);
	}

	@Inject
	IsolatedTestManifest context;

	@Before
	public void setup() {
		Mockwire.isolate(this);
	}

	@Test
	public void atBless() {
	  assertThat(context.hasRegisteredType(AutowirableWithMockable.class)).isTrue();
	  assertThat(injected).isNotNull();
	  assertThat(injected.mockable).isNotNull();
	  assertThat(nested).isNotNull();
	  assertThat(context.hasRegisteredType(AutowirableWithAutowirable.class)).isTrue();
	  assertThat(nested.autowirableWithMockable).isNotNull();
	}
}
