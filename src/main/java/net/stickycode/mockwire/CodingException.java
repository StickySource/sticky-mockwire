package net.stickycode.mockwire;


public class CodingException
    extends RuntimeException {

  public CodingException(String message, Object... parameters) {
    super(resolveMessage(message, parameters));
  }

  static String resolveMessage(String message, Object... args) {
    if (args.length == 0) // no args
      return message;

    String[] split = message.split("\\{\\}", args.length + 1);
    if (split.length == 1) // no placeholders
      return message;

    StringBuilder b = new StringBuilder();
    for (int i = 0; i < split.length; i++) {
      b.append(split[i]);
      if (i < args.length)
        appendArgument(b, args[i]);
    }

    return b.toString();
  }

  static void appendArgument(StringBuilder b, Object a) {
    if (a instanceof Throwable)
      throw new IllegalArgumentException(
          "Cannot pass throwables as parameters it will just lead to unexpected results");

    b.append(a.toString());
  }


}
