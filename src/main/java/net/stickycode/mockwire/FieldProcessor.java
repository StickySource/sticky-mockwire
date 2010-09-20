package net.stickycode.mockwire;

import java.lang.reflect.Field;


public interface FieldProcessor {

  void processField(Object target, Field field);

  boolean canProcess(Field field);

}
