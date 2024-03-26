/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.operationsdesk.application.menus;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 */
public class MenusInjectionModule
    extends AbstractModule {

  /**
   * Creates a new instance.
   */
  public MenusInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(MenuFactory.class));
  }

}
