/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.base.model;

/**
 * Defines constants for {@link ModelComponent}s that have model coordinates.
 */
public interface PositionableModelComponent
    extends ModelComponent {

  /**
   * Key for the X (model) cordinate.
   */
  String MODEL_X_POSITION = "modelXPosition";
  /**
   * Key for the Y (model) cordinate.
   */
  String MODEL_Y_POSITION = "modelYPosition";
}
