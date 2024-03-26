/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor;

import org.opentcs.customizations.plantoverview.PlantOverviewInjectionModule;

/**
 * Configures/binds the default importers and exporters of the openTCS plant overview.
 */
public class DefaultImportersExportersModule
    extends PlantOverviewInjectionModule {

  /**
   * Creates a new instance.
   */
  public DefaultImportersExportersModule() {
  }

  @Override
  protected void configure() {
    plantModelImporterBinder();
    plantModelExporterBinder();
  }
}
