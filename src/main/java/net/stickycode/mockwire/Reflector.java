package net.stickycode.mockwire;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;


public class Reflector {

  private List<FieldProcessor> fieldProcessors = new LinkedList<FieldProcessor>();

  public Reflector() {
  }

  public Reflector forEachField(FieldProcessor... processors) {
    for (FieldProcessor fieldProcessor : processors) {
      fieldProcessors.add(fieldProcessor);
    }
    return this;
  }

  public void process(Object target) {
    Class<?> type = target.getClass();
    while (type != Object.class) {
      processFields(target, type);
      type = type.getSuperclass();
    }
  }

  private void processFields(Object target, Class<?> type) {
    Field[] fields = type.getDeclaredFields();
    for (Field field : fields)
      for (FieldProcessor processor : fieldProcessors)
        if (processor.canProcess(field))
          processor.processField(field);

  }

}
