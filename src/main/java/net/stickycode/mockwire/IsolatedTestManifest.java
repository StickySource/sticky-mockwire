package net.stickycode.mockwire;




public interface IsolatedTestManifest {

  boolean hasRegisteredType(Class<?> klass);

  void registerBean(String beanName, Object bean);

  void registerType(String beanName, Class<?> type);

  void autowire(Object testInstance);

}
