/**
 * Copyright (c) 2010 RedEngine Ltd, http://www.redengine.co.nz. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
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
