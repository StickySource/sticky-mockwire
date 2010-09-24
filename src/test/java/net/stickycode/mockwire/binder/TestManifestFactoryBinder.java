package net.stickycode.mockwire.binder;

import net.stickycode.mockwire.IsolatedTestManifest;
import net.stickycode.mockwire.spring25.SpringIsolatedTestManifest;


public class TestManifestFactoryBinder
    implements TestManifestFactory {

  @Override
  public IsolatedTestManifest create() {
    return new SpringIsolatedTestManifest();
  }

}
