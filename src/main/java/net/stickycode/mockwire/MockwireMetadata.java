package net.stickycode.mockwire;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.stickycode.mockwire.configured.MockwireConfigurationSource;
import net.stickycode.mockwire.feature.MockwireScan;

public class MockwireMetadata {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Class<?> testClass;

  private MockwireConfigurationSource configurationSource;

  private String testName;

  public MockwireMetadata(Class<?> testClass) {
    this.testClass = testClass;
    configurationSource = deriveConfigurationSource(testClass);
  }

  private List<String> deriveFrameworkPackages(Class<?> klass) {
    List<String> packages = new ArrayList<>();
    for (Annotation a : this.testClass.getAnnotations())
      if (a.annotationType().isAnnotationPresent(MockwireScan.class)) {
        MockwireScan scan = a.annotationType().getAnnotation(MockwireScan.class);
        if (scan.value().length == 0)
          throw new MockwireScanMustHaveDefinedPackagesToScanException(a);

        for (String p : scan.value())
          packages.add(p);
      }

    return packages;
  }

  private List<String> deriveContainmentRoots(Class<?> testClass) {
    MockwireContainment containment = testClass.getAnnotation(MockwireContainment.class);
    if (containment == null)
      return Collections.emptyList();

    String packageAsPath = packageToPath(testClass.getPackage());
    if (containment.value().length == 0)
      return Collections.singletonList(packageAsPath);

    List<String> paths = new ArrayList<String>();
    for (String path : containment.value()) {
      if (path.indexOf('/') > -1 && !path.startsWith("/"))
        throw new ScanRootsShouldStartWithSlashException(path);

      paths.add(path);
    }

    paths.add(packageAsPath);
    return paths;
  }

  private String packageToPath(Package p) {
    return "/" + p.getName().replace('.', '/');
  }

  MockwireConfigurationSource deriveConfigurationSource(Class<?> testClass) {
    MockwireConfigured configured = testClass.getAnnotation(MockwireConfigured.class);
    if (configured == null)
      return null;

    MockwireConfigurationSource source = mockwireConfigurationSource();
    source.add(testClass, configured.value());
    return source;
  }

  private MockwireConfigurationSource mockwireConfigurationSource() {
    MockwireConfigurationSource mockwireConfigurationSource = new MockwireConfigurationSource();
    mockwireConfigurationSource.addValue("testName", testName);
    return mockwireConfigurationSource;
  }

  public MockwireConfigurationSource getConfigurationSource() {
    return configurationSource;
  }

  public void setTestName(String name) {
    this.testName = name;
  }

  public List<String> frameworkPackages() {
    return deriveFrameworkPackages(testClass);
  }

  public List<String> containment() {
    return deriveContainmentRoots(testClass);
  }

  public Class<?> getTestClass() {
    return testClass;
  }

  public boolean isConfigured() {
    return configurationSource != null;
  }

  public boolean singleLifecycle() {
    return testClass.isAnnotationPresent(MockwireSingleLifecycle.class);
  }

}
