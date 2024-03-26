/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.base.components.properties.type;

import java.util.ArrayList;
import java.util.List;
import org.opentcs.guing.base.model.ModelComponent;

/**
 * A property for angles.
 */
public class AngleProperty
    extends AbstractQuantity<AngleProperty.Unit> {

  /**
   * Creates a new instance.
   *
   * @param model The model component.
   */
  public AngleProperty(ModelComponent model) {
    this(model, Double.NaN, Unit.DEG);
  }

  /**
   * Creates a new instance.
   *
   * @param model The model component.
   * @param value The property value.
   * @param unit The value's unit.
   */
  public AngleProperty(ModelComponent model, double value, Unit unit) {
    super(model, value, unit, Unit.class, relations());
  }

  @Override
  public Object getComparableValue() {
    return String.valueOf(fValue) + getUnit();
  }

  @Override
  public void setValue(Object newValue) {
    if (newValue instanceof Double) {
      if (getUnit() == Unit.DEG) {
        super.setValue(((double) newValue) % 360);
      }
      else {
        super.setValue(((double) newValue) % (2 * Math.PI));
      }
    }
    else {
      super.setValue(newValue);
    }
  }

  @Override
  protected void initValidRange() {
    validRange.setMin(0);
  }

  private static List<Relation<Unit>> relations() {
    List<Relation<Unit>> relations = new ArrayList<>();
    relations.add(new Relation<>(Unit.DEG, Unit.RAD, 180.0 / Math.PI));
    relations.add(new Relation<>(Unit.RAD, Unit.DEG, Math.PI / 180.0));
    return relations;
  }

  /**
   * The supported units.
   */
  public enum Unit {
    /**
     * Degrees.
     */
    DEG("deg"),
    /**
     * Radians.
     */
    RAD("rad");

    private final String displayString;

    Unit(String displayString) {
      this.displayString = displayString;
    }

    @Override
    public String toString() {
      return displayString;
    }
  }
}
