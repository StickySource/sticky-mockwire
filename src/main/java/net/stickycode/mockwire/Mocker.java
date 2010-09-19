package net.stickycode.mockwire;



public interface Mocker {

  <T> T mock(Class<T> type);

}
