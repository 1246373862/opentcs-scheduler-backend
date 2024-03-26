/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.components.dockable;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import javax.inject.Singleton;
import org.opentcs.guing.common.components.dockable.DockableHandlerFactory;
import org.opentcs.guing.common.components.dockable.DockingManager;

/**
 * A Guice module for this package.
 */
public class DockableInjectionModule
    extends AbstractModule {

  /**
   * Creates a new instance.
   */
  public DockableInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(DockableHandlerFactory.class));

    bind(DockingManagerModeling.class).in(Singleton.class);
    bind(DockingManager.class).to(DockingManagerModeling.class);
  }
}
