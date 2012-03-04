package net.stickycode.mockwire;

import java.lang.annotation.Annotation;

import net.stickycode.reflector.AnnotatedFieldSettingProcessor;
import net.stickycode.reflector.ValueSource;


public class MockwireInjectingFieldProcessor
    extends AnnotatedFieldSettingProcessor {
  
  private static Class<? extends Annotation>[] injectionMarkers= AnnotationFinder.load("mockwire", "injection");
  
  public MockwireInjectingFieldProcessor(ValueSource source) {
    super(source, injectionMarkers);
  }

}
