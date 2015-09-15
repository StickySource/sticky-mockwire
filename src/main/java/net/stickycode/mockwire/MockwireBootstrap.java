package net.stickycode.mockwire;

import java.lang.reflect.Method;

import net.stickycode.bootstrap.StickyBootstrap;
import net.stickycode.mockwire.binder.MockerFactoryLoader;
import net.stickycode.mockwire.configured.MockwireConfigurationSource;
import net.stickycode.reflector.Reflector;

public class MockwireBootstrap
    implements MockwireContainer {

  private StickyBootstrap bootstrap;

  private MockwireMetadata metadata;

  private Mocker mocker;

  public MockwireBootstrap(MockwireMetadata metadata) {
    this.metadata = metadata;
  }

  @Override
  public void startup() {
    this.bootstrap = StickyBootstrap.crank();
    this.mocker = MockerFactoryLoader.load();

    MockwireFrameworkBridge.bridge().initialise(bootstrap, metadata.getTestClass());

    new Reflector()
        .forEachField(
            new ControlledAnnotatedFieldProcessor(bootstrap, mocker),
            new UnderTestAnnotatedFieldProcessor(bootstrap, metadata))
        .process(metadata.getTestClass());

    bootstrap.scan(metadata.frameworkPackages());
    bootstrap.scan(metadata.containment());

    if (metadata.isConfigured())
      bootstrap.registerSingleton("mockwireConfiguration", metadata.getConfigurationSource(), MockwireConfigurationSource.class);
  }

  @Override
  public void shutdown() {
    bootstrap.shutdown();
  }

  @Override
  public MockwireContainer startTest(Object test) {
    bootstrap.inject(test);

    bootstrap.start();

    return this;
  }

  @Override
  public void endTest(Object test) {
  }

  @Override
  public Object[] deriveParameters(Method method) {
    if (method.getParameterCount() == 0)
      return null;

    Object[] params = new Object[method.getParameterCount()];
    int i = 0;
    for (Class<?> type : method.getParameterTypes()) {
      params[i++] = bootstrap.find(type);
    }
    return params;
  }

}
