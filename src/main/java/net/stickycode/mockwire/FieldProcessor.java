package net.stickycode.mockwire;

import java.lang.reflect.Field;


public interface FieldProcessor {

  void processField(Field field);

  boolean canProcess(Field field);

}
