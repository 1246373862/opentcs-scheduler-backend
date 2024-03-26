/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.application.action.file;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.opentcs.guing.common.application.GuiManager;
import static org.opentcs.guing.common.util.I18nPlantOverview.MENU_PATH;
import org.opentcs.guing.common.util.ImageDirectory;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 */
public class SaveModelAction
    extends AbstractAction {

  /**
   * This action's ID.
   */
  public static final String ID = "file.saveModel";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);

  private final GuiManager view;

  /**
   * Creates a new instance.
   *
   * @param view The gui manager
   */
  public SaveModelAction(GuiManager view) {
    this.view = view;

    putValue(NAME, BUNDLE.getString("saveModelAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("saveModelAction.shortDescription"));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl S"));
    putValue(MNEMONIC_KEY, Integer.valueOf('S'));

    ImageIcon icon = ImageDirectory.getImageIcon("/menu/document-save.png");
    putValue(SMALL_ICON, icon);
    putValue(LARGE_ICON_KEY, icon);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    view.saveModel();
  }
}
