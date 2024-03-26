/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.base.components.properties.type;

import java.util.List;

/**
 * Interface for a property indicating this property has different
 * possible values to choose from.
 *
 * @param <E> The type of elements that can be selected from.
 */
public interface Selectable<E> {

  /**
   * Sets the possible values.
   *
   * @param possibleValues An array with the possible values.
   */
  void setPossibleValues(List<E> possibleValues);

  /**
   * Returns the possible values.
   *
   * @return The possible values.
   */
  List<E> getPossibleValues();
}
