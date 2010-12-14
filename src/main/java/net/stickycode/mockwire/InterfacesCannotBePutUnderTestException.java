package net.stickycode.mockwire;

import java.lang.reflect.Field;

import net.stickycode.exception.PermanentException;

@SuppressWarnings("serial")
public class InterfacesCannotBePutUnderTestException
    extends PermanentException {

  public InterfacesCannotBePutUnderTestException(Field field) {
    super("@Bless'd field '{}' on test '{}' " +
        "is not instantiable with type '{}'. " +
        "Blessing is used to identify the code you " +
        "wish to test, did you mean @Mock or perhaps bless " +
        "a concrete implementation instead?",
        field.getName(),
        field.getDeclaringClass().getSimpleName(),
        field.getType().getName());

  }

}
