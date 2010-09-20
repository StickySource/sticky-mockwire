package net.stickycode.mockwire;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MethodBlessingTest {

	@Inject
	private Autowirable injected;

	@Bless
  public Autowirable factory() {
    return new Autowirable();
  }

	@Inject
	IsolatedTestManifest context;

	@Before
	public void setup() {
		Mockwire.isolate(this);
	}

	@Test
	public void atBless() {
	  assertThat(context.hasRegisteredType(Autowirable.class)).isTrue();
	  assertThat(injected).isNotNull();
	}
}
