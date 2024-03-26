/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.components.properties.panel;

import javax.swing.JPanel;

/**
 * A factory for properties-related panels.
 */
public interface PropertiesPanelFactory {

  /**
   * Creates a new <code>PropertiesTableContent</code>.
   *
   * @param dialogParent The component to be used as the parent for dialogs
   * created by the new instance.
   * @return The newly created instance.
   */
  PropertiesTableContent createPropertiesTableContent(JPanel dialogParent);
}
