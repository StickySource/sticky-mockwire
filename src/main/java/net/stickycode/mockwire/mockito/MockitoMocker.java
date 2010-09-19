package net.stickycode.mockwire.mockito;

import org.mockito.Mockito;

import net.stickycode.mockwire.Mocker;


public class MockitoMocker implements Mocker {

  @Override
  public <T> T mock(Class<T> type) {
    return Mockito.mock(type);
  }

}
