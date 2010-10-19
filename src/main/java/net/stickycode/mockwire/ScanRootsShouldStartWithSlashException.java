package net.stickycode.mockwire;

import net.stickycode.exception.PermanentException;

@SuppressWarnings("serial")
public class ScanRootsShouldStartWithSlashException
    extends PermanentException {

  public ScanRootsShouldStartWithSlashException(String path) {
    super("The scan root '{}' for containment must start with /", path);
  }

}
