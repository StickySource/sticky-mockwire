package net.stickycode.mockwire;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MockwireTest {

	@Mock
	private Mockable m;

	@Bless
	private Autowirable a;

	@Bless
	private AutowirableWithDependencies d;

	@Inject
	private AutowirableWithDependencies injected;

	@Inject
	IsolatedTestManifest context;

	@Before
	public void setup() {
		Mockwire.isolate(this);
		assertThat(d).isNull();
		assertThat(m).isNull();
		assertThat(a).isNull();
	}

	@Test
	public void objectsAreAutowired() {
		assertThat(injected).isNotNull();
		assertThat(injected.getMock()).isNotNull();
		assertThat(injected.getAutowirable()).isNotNull();
	}

	@Test(expected = CodingException.class)
	public void nullParameterIsIllegal() {
		Mockwire.isolate(null);
	}

}
