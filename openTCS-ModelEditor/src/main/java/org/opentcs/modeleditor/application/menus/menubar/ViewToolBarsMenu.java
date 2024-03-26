/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.application.menus.menubar;

import java.util.Collection;
import static java.util.Objects.requireNonNull;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import org.opentcs.modeleditor.util.I18nPlantOverviewModeling;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 */
public class ViewToolBarsMenu
    extends JMenu {

  private static final ResourceBundleUtil LABELS_MENU
      = ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.MENU_PATH);
  private static final ResourceBundleUtil LABELS_TOOLBAR
      = ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.TOOLBAR_PATH);

  public ViewToolBarsMenu(Collection<Action> viewActions) {
    super(LABELS_MENU.getString("viewToolBarsMenu.text"));
    requireNonNull(viewActions, "viewActions");

    JCheckBoxMenuItem checkBoxMenuItem;
    for (Action a : viewActions) {
      checkBoxMenuItem = new JCheckBoxMenuItem(a);
      add(checkBoxMenuItem);

      if (checkBoxMenuItem.getText().equals(
          LABELS_TOOLBAR.getString("toolBarManager.toolbar_drawing.title")
      )) {
        checkBoxMenuItem.setEnabled(false); // "Draw"-Toolbar musn't be disabled.
      }
    }

  }

}
