package net.stickycode.mockwire;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

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

  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("Mockwire v" + version + " see http://www.stickycode.net/mockwire.html");
  }

  private final Class<?> testClass;
  private final String[] scanRoots;
  private final List<ConfigurationSource> configurationSources;
  private Mocker mocker;
  private IsolatedTestManifest manifest;

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
    log.debug("initialising test {}", testInstance);
    process(manifest, mocker, testInstance);
    manifest.prepareTest(testInstance);
  }

  private void configure() {
    if (manifest.hasRegisteredType(ConfigurationSystem.class))
      throw new ConfigurationSystemWasScannedOrDefinedAlready(testClass);

    ConfigurationSystem system = new ConfigurationSystem();
    for (ConfigurationSource s : configurationSources) {
      system.add(s);
    }

    manifest.registerConfigurationSystem("configurationSystem", system, system.getClass());
  }

  List<ConfigurationSource> deriveConfigurationSources(Class<?> testClass) {
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

  @SuppressWarnings("unchecked")
  private void process(final IsolatedTestManifest manifest, final Mocker mocker, Object testInstance) {
    log.debug("processing test {}", testInstance);
    new Reflector()
          .forEachMethod(
              new UnderTestAnnotatedMethodProcessor(manifest, UnderTest.class, Bless.class))
          .process(testInstance);

  }

  @SuppressWarnings("unchecked")
  private void process(final IsolatedTestManifest manifest, final Mocker mocker) {
    log.debug("processing test class {}", testClass);
    new Reflector()
        .forEachField(
            new ControlledAnnotatedFieldProcessor(manifest, mocker, Controlled.class, Mock.class),
            new UnderTestAnnotatedFieldProcessor(manifest, UnderTest.class, Bless.class))
            .process(testClass);
  }

  public void startup() {
    log.debug("startup {}", testClass);
    mocker = MockerFactoryLoader.load();
    manifest = TestManifestFactoryLoader.load();

    if (scanRoots != null)
      manifest.scanPackages(scanRoots);

    if (configurationSources != null)
      configure();

    process(manifest, mocker);

    manifest.startup(testClass);
  }

  public void shutdown() {
    manifest.shutdown();
    log.debug("shutdown {}", testClass);
  }

  public boolean isolateLifecycles() {
    return false;
  }
}
