package net.stickycode.mockwire.spring25;

import net.stickycode.mockwire.CodingException;


public class ExpectedUniqueBeanException
    extends CodingException {

  public ExpectedUniqueBeanException(String message, Object... parameters) {
    super(message, parameters);
  }

}
