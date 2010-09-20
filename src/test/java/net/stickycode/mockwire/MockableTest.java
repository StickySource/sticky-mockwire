package net.stickycode.mockwire;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.when;

import static org.fest.assertions.Assertions.assertThat;

public class MockableTest {

	@Mock
	private Mockable mockable;

	@Inject
	private Mockable injected;

	@Inject
	IsolatedTestManifest context;

	@Before
	public void setup() {
		Mockwire.isolate(this);
	}

	@Test
	public void atMock() {
	  assertThat(context.hasRegisteredType(Mockable.class)).isTrue();
	  assertThat(mockable).isNotNull();
	  assertThat(injected).isNotNull();
	}

	public void verifyMock() {
		when(injected.callme()).thenReturn(true);
		assertThat(injected.callme()).isEqualTo(true);
	}
}
