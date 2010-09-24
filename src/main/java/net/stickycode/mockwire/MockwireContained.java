package net.stickycode.mockwire;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.stickycode.mockwire.junit4.MockwireContext;


public class MockwireContained implements MockwireContext {

  private final Class<?> testClass;
  private final List<String> scanRoots;

  public MockwireContained(Class<?> testClass, MockwireContainment containment) {
    this.testClass = testClass;
    this.scanRoots = deriveContainmentRoots(testClass, containment);
  }

  public MockwireContained(Class<?> testClass) {
    this.testClass = testClass;
    MockwireContainment containment = testClass.getAnnotation(MockwireContainment.class);
    this.scanRoots = deriveContainmentRoots(testClass, containment);
  }

  private List<String> deriveContainmentRoots(Class<?> testClass2, MockwireContainment containment) {
    if (containment == null || containment.value().length == 0)
      return Collections.singletonList(packageToPath(testClass.getPackage()));

    List<String> paths = new LinkedList<String>();
    for (String path : containment.value()) {
      if (!path.startsWith("/"))
        throw new CodingException("The scan root '{}' for containment must start with /", path);

      paths.add(path);
    }

    return paths;
  }

  private String packageToPath(Package p) {
    return "/" + p.getName().replace('.', '/');
  }

  @Override
  public void initialiseTestInstance(Object testInstance) {
  }

}
