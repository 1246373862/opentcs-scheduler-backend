/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.application.action;

import com.google.inject.AbstractModule;
import javax.inject.Singleton;

/**
 * An injection module for this package.
 */
public class ActionInjectionModule
    extends AbstractModule {

  /**
   * Creates a new instance.
   */
  public ActionInjectionModule() {
  }

  @Override
  protected void configure() {
    bind(ViewActionMap.class).in(Singleton.class);
  }

}
