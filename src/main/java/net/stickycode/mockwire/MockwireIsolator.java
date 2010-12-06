package net.stickycode.mockwire;

import net.stickycode.configured.ConfigurationSystem;
import net.stickycode.configured.source.EnvironmentConfigurationSource;
import net.stickycode.configured.source.SystemPropertiesConfigurationSource;
import net.stickycode.configured.spring25.ConfiguredBeanPostProcessor;
import net.stickycode.mockwire.MockwireConfigured.Priority;
import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.configured.MockwireConfigurationSource;
import net.stickycode.mockwire.junit4.MockwireContext;
import net.stickycode.mockwire.spring30.ConfigurationSystemWasScannedOrDefinedAlready;
import net.stickycode.reflector.Reflector;

public class MockwireIsolator
    implements MockwireContext {

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("MockwireIsolator v" + version + " see http://stickycode.net/mockwire");
  }

  @SuppressWarnings("unused")
  private Class<?> testClass;

  public MockwireIsolator(Class<?> testClass) {
    this.testClass = testClass;
  }

  public void initialiseTestInstance(Object testInstance) {
    // TODO this should call create on the factory!
    // XXX move this call outside the isolator to allow for testing
    Mocker mocker = MockerFactoryLoader.load();
    // TODO this should call create on the factory!
    // XXX move this call outside the isolator to allow for testing
    IsolatedTestManifest manifest = TestManifestFactoryLoader.load();

    configure(manifest, testInstance);
    process(manifest, mocker, testInstance);
    manifest.autowire(testInstance);
  }

  private void configure(IsolatedTestManifest manifest, Object testInstance) {
    Class<?> testClass = testInstance.getClass();
    MockwireConfigured configured = testClass.getAnnotation(MockwireConfigured.class);
    if (configured == null)
      return;

    if (manifest.hasRegisteredType(ConfigurationSystem.class))
      throw new ConfigurationSystemWasScannedOrDefinedAlready(testInstance);

    ConfigurationSystem system = new ConfigurationSystem();
    manifest.registerBean("mockwireConfigurationSystem", system, ConfigurationSystem.class);
    manifest.registerType("configuredBeanPostProcessor", ConfiguredBeanPostProcessor.class);

    if (configured.useSystemProperties() == Priority.First)
      system.add(new SystemPropertiesConfigurationSource());

    MockwireConfigurationSource source = new MockwireConfigurationSource();
    source.add(testClass, configured.value());
    system.add(source);

    if (configured.useSystemProperties() == Priority.Fallback)
      system.add(new SystemPropertiesConfigurationSource());

    if (configured.useEnvironmentProperties())
      system.add(new EnvironmentConfigurationSource());
  }

  private IsolatedTestManifest process(final IsolatedTestManifest manifest, final Mocker mocker, Object testInstance) {
    new Reflector()
        .forEachField(
            new MockAnnotatedFieldProcessor(manifest, mocker, Controlled.class, Mock.class),
            new BlessAnnotatedFieldProcessor(manifest, UnderTest.class, Bless.class))
        .forEachMethod(
            new BlessAnnotatedMethodProcessor(manifest, UnderTest.class, Bless.class))
        .process(testInstance);

    return manifest;
  }

}
