package net.stickycode.mockwire;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.stickycode.configuration.ConfigurationSource;
import net.stickycode.mockwire.UnderTestAnnotatedFieldProcessor.MockwireConfigurationSourceProvider;
import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.binder.TestManifestFactoryLoader;
import net.stickycode.mockwire.configured.MockwireConfigurationSource;
import net.stickycode.mockwire.configured.MockwireConfiguredIsRequiredToTestConfiguredCodeException;
import net.stickycode.mockwire.feature.MockwireScan;
import net.stickycode.reflector.Reflector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockwireContext
    implements MockwireConfigurationSourceProvider {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final String version;

  static {
    version = PomUtils.loadVersion("net.stickycode.mockwire", "sticky-mockwire");
    System.out.println("Mockwire v" + version + " see http://www.stickycode.net/mockwire.html");
  }

  private final Class<?> testClass;

  private String[] scanRoots;

  private List<ConfigurationSource> configurationSources;

  private Mocker mocker;

  private IsolatedTestManifest manifest;

  private MockwireConfigurationSource source;

  private List<String> frameworkPackages;

  private String testName;

  public MockwireContext(Class<?> testClass) {
    this.testClass = testClass;
  }

  private void initialise() {
    deriveFrameworkPackages(this.testClass);
    this.scanRoots = deriveContainmentRoots(this.testClass);
    this.configurationSources = deriveConfigurationSources(this.testClass);
  }

  private void deriveFrameworkPackages(Class<?> klass) {
    for (Annotation a : this.testClass.getAnnotations()) {
      if (a.annotationType().isAnnotationPresent(MockwireScan.class)) {
        MockwireScan scan = a.annotationType().getAnnotation(MockwireScan.class);
        if (scan.value().length == 0)
          throw new MockwireScanMustHaveDefinedPackagesToScanException(a);

        for (String p : scan.value()) {
          addFrameworkPackages(p);
        }
      }
    }
  }

  private void addFrameworkPackages(String p) {
    if (frameworkPackages == null)
      frameworkPackages = new ArrayList<String>();

    frameworkPackages.add(p);
  }

  private String[] deriveContainmentRoots(Class<?> testClass) {
    MockwireContainment containment = testClass.getAnnotation(MockwireContainment.class);
    if (containment == null)
      return null;

    String packageAsPath = packageToPath(testClass.getPackage());
    if (containment.value().length == 0)
      return new String[] { packageAsPath };

    List<String> paths = new LinkedList<String>();
    for (String path : containment.value()) {
      if (path.indexOf('/') > -1 && !path.startsWith("/"))
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
    if (manifest == null)
      throw new IllegalStateException("startup has not been called");

    log.debug("initialising test '{}'", testInstance);
    process(manifest, mocker, testInstance);
    manifest.prepareTest(testInstance);
    if (configurationSources != null)
      manifest.configure();
  }

  private void registerConfigurationSources() {
    if (frameworkPackages == null)
      throw new MockwireConfiguredIsRequiredToTestConfiguredCodeException();


    manifest.registerConfiguationSystem(configurationSources);
  }

  List<ConfigurationSource> deriveConfigurationSources(Class<?> testClass) {
    MockwireConfigured configured = testClass.getAnnotation(MockwireConfigured.class);
    if (configured == null)
      return null;

    LinkedList<ConfigurationSource> sources = new LinkedList<ConfigurationSource>();

    source = mockwireConfigurationSource();
    source.add(testClass, configured.value());
    sources.add(source);

    return sources;
  }

  private MockwireConfigurationSource mockwireConfigurationSource() {
    MockwireConfigurationSource mockwireConfigurationSource = new MockwireConfigurationSource();
    mockwireConfigurationSource.addValue("testName", testName);
    return mockwireConfigurationSource;
  }

  private void process(final IsolatedTestManifest manifest, final Mocker mocker, Object testInstance) {
    log.debug("processing test instance '{}'", testInstance);
    new Reflector()
        .forEachMethod(
            new UncontrolledAnnotatedMethodProcessor(manifest))
        .process(testInstance);

  }

  private void process(final IsolatedTestManifest manifest, final Mocker mocker) {
    log.debug("processing test class '{}'", testClass);
    new Reflector()
        .forEachField(
            new ControlledAnnotatedFieldProcessor(manifest, mocker),
            new UnderTestAnnotatedFieldProcessor(manifest, this))
        .process(testClass);
  }

  public void startup() {
    log.debug("startup {}", testClass);
    initialise();

    mocker = MockerFactoryLoader.load();
    manifest = TestManifestFactoryLoader.load();

    if (configurationSources != null)
      registerConfigurationSources();
    
    if (frameworkPackages != null)
      manifest.initialiseFramework(frameworkPackages);

    if (scanRoots != null)
      manifest.scanPackages(scanRoots);

    process(manifest, mocker);

    manifest.startup(testClass);
  }

  public void shutdown() {
    manifest.shutdown();
    log.debug("shutdown {}", testClass);
  }

  public boolean isolateLifecycles() {
    return true;
  }

  public MockwireConfigurationSource getConfigurationSource() {
    if (source != null)
      return source;

    source = mockwireConfigurationSource();
    configurationSources = new LinkedList<ConfigurationSource>();
    configurationSources.add(source);

    registerConfigurationSources();

    return source;
  }

  public void setTestName(String name) {
    this.testName = name;
  }
}
