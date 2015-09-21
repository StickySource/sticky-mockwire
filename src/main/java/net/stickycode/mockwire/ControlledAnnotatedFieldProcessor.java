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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.stickycode.bootstrap.StickyBootstrap;
import net.stickycode.reflector.AnnotatedFieldProcessor;

class ControlledAnnotatedFieldProcessor
    extends AnnotatedFieldProcessor {

  private final StickyBootstrap manifest;

  private final MockwireMockerBridge mocker;

  private static Class<? extends Annotation>[] controls;

  static {
    controls = AnnotationFinder.load("mockwire", "control");
  }

  ControlledAnnotatedFieldProcessor(StickyBootstrap bootstrap, MockwireMockerBridge mocker) {
    super(controls);
    this.manifest = bootstrap;
    this.mocker = mocker;
  }

  @Override
  public void processField(Object target, Field field) {
    mocker.process(field.getName(), target, field, field.getType());
  }
}