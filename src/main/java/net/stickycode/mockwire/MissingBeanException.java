package net.stickycode.mockwire;


public class MissingBeanException
    extends CodingException {

  public MissingBeanException(String message, Object... parameters) {
    super(message, parameters);
  }

}
