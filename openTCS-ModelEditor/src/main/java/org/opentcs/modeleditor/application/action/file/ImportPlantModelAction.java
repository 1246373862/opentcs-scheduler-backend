/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.application.action.file;

import java.awt.event.ActionEvent;
import static java.util.Objects.requireNonNull;
import javax.swing.AbstractAction;
import org.opentcs.components.plantoverview.PlantModelImporter;
import org.opentcs.guing.common.application.GuiManager;

/**
 */
public class ImportPlantModelAction
    extends AbstractAction {

  private final PlantModelImporter importer;
  private final GuiManager guiManager;

  /**
   * Creates a new instance.
   *
   * @param importer The importer.
   * @param guiManager The gui manager
   */
  public ImportPlantModelAction(PlantModelImporter importer, GuiManager guiManager) {
    this.importer = requireNonNull(importer, "importer");
    this.guiManager = requireNonNull(guiManager, "guiManager");
    this.putValue(NAME, importer.getDescription());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    guiManager.importModel(importer);
  }
}
