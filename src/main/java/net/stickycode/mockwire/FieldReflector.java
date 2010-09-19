package net.stickycode.mockwire;


public class FieldReflector {

  private Reflector reflector;

  public FieldReflector(Reflector reflector) {
    super();
    this.reflector = reflector;
  }

  public void process(Object testInstance) {
    reflector.process(testInstance);
  }

  public FieldReflector apply(FieldProcessor annotationFieldProcessor) {
    reflector.addFieldProcessor(annotationFieldProcessor);
    return this;
  }

}
