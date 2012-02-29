package net.stickycode.mockwire;

import java.lang.annotation.Annotation;

import net.stickycode.exception.PermanentException;

@SuppressWarnings("serial")
public class MockwireScanMustHaveDefinedPackagesToScanException
    extends PermanentException {

  public MockwireScanMustHaveDefinedPackagesToScanException(Annotation a) {
    super("A mockwire feature {} marked for scanning should define the packages that should be scanned to support the feature", a
        .annotationType());
  }

}
