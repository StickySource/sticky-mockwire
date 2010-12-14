package net.stickycode.mockwire;

import java.lang.reflect.Field;

import net.stickycode.exception.PermanentException;

@SuppressWarnings("serial")
public class NonStaticMemberTypesCannotBePutUnderTestException
    extends PermanentException {

  public NonStaticMemberTypesCannotBePutUnderTestException(Field field) {
    super("@Bless'd field '{}' on test '{}' has non static inner class '{}' as type. " +
          "Add a static modifier to it so it can be blessed.\n" +
          "For example\n" +
          "public class SomeTest {\n" +
          "  private class InnerType {\n" +
          "  }\n" +
          "}\n" +
          "Should look more like\n" +
          "public class SomeTest {\n" +
          "  private static class InnerType {\n" +
          "  }\n" +
          "}\n",
          field.getName(), field.getDeclaringClass().getSimpleName(), field.getType().getName());
  }

}
