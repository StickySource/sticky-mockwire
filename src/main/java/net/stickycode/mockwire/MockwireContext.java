package net.stickycode.mockwire;

import java.util.LinkedList;
import java.util.List;

import net.stickycode.configured.ConfigurationSource;
import net.stickycode.configured.ConfigurationSystem;
import net.stickycode.configured.source.EnvironmentConfigurationSource;
import net.stickycode.configured.source.SystemPropertiesConfigurationSource;
import net.stickycode.mockwire.MockwireConfigured.Priority;
import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.configured.MockwireConfigurationSource;
import net.stickycode.reflector.Reflector;

public class MockwireContext {

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("Mockwire v" + version + " see http://www.stickycode.net/mockwire.html");
  }

  private final Class<?> testClass;
  private final String[] scanRoots;
  private final List<ConfigurationSource> configurationSources;

  public MockwireContext(Class<?> testClass) {
    this.testClass = testClass;
    this.scanRoots = deriveContainmentRoots(testClass);
    this.configurationSources = deriveConfigurationSources(testClass);
  }

  private String[] deriveContainmentRoots(Class<?> testClass) {
    MockwireContainment containment = testClass.getAnnotation(MockwireContainment.class);
    if (containment == null)
      return null;

    String packageAsPath = packageToPath(testClass.getPackage());
    if (containment == null || containment.value().length == 0)
      return new String[] { packageAsPath };

    List<String> paths = new LinkedList<String>();
    for (String path : containment.value()) {
      if (!path.startsWith("/"))
        throw new ScanRootsShouldStartWithSlashException(path);

      paths.add(path);
    }

    paths.add(packageAsPath);
    return paths.toArray(new String[paths.size()]);
  }

  private String packageToPath(Package p) {
    return "/" + p.getName().replace('.', '/');
  }

  public void initialiseTestInstance(Object testInstance) {
    // XXX move this call outside the isolator to allow for testing
    Mocker mocker = MockerFactoryLoader.load();
    // XXX move this call outside the isolator to allow for testing
    IsolatedTestManifest manifest = TestManifestFactoryLoader.load();

    if (scanRoots != null)
      manifest.scanPackages(scanRoots);

    if (configurationSources != null)
      configure(manifest, testInstance);

    process(manifest, mocker, testInstance);
    manifest.autowire(testInstance);
  }

  private void configure(IsolatedTestManifest manifest, Object testInstance) {
    if (manifest.hasRegisteredType(ConfigurationSystem.class))
      throw new ConfigurationSystemWasScannedOrDefinedAlready(testInstance);

    ConfigurationSystem system = new ConfigurationSystem();
    for (ConfigurationSource s : configurationSources) {
      system.add(s);
    }

    manifest.registerConfigurationSystem("configurationSystem", system, system.getClass());
  }

  private List<ConfigurationSource> deriveConfigurationSources(Class<?> testClass) {
    MockwireConfigured configured = testClass.getAnnotation(MockwireConfigured.class);
    if (configured == null)
      return null;

    LinkedList<ConfigurationSource> sources = new LinkedList<ConfigurationSource>();
    if (configured.useSystemProperties() == Priority.First)
      sources.add(new SystemPropertiesConfigurationSource());

    MockwireConfigurationSource source = new MockwireConfigurationSource();
    source.add(testClass, configured.value());
    sources.add(source);

    if (configured.useSystemProperties() == Priority.Fallback)
      sources.add(new SystemPropertiesConfigurationSource());

    if (configured.useEnvironmentProperties())
      sources.add(new EnvironmentConfigurationSource());

    return sources;
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
