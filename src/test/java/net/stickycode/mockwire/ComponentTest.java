package net.stickycode.mockwire;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ComponentTest {

	@Bless
	private Autowirable autowirable;

	@Inject
	private Autowirable injected;

	@Inject
	IsolatedTestManifest context;

	@Before
	public void setup() {
		Mockwire.isolate(this);
	}

	@Test
	public void atBless() {
	  assertThat(context.hasRegisteredType(Autowirable.class)).isTrue();
	  assertThat(autowirable).isNull();
	  assertThat(injected).isNotNull();
	}
}
