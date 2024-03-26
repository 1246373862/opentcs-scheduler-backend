/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.components.tree.elements;

import java.util.Set;
import javax.swing.JPopupMenu;

/**
 * A null context. Use this when no special context is required.
 */
public class NullContext
    implements UserObjectContext {

  /**
   * Creates a new instance.
   */
  public NullContext() {
  }

  @Override
  public JPopupMenu getPopupMenu(Set<UserObject> selectedUserObjects) {
    return new JPopupMenu();
  }

  @Override
  public boolean removed(UserObject userObject) {
    return true;
  }

  @Override
  public ContextType getType() {
    return ContextType.NULL;
  }
}
