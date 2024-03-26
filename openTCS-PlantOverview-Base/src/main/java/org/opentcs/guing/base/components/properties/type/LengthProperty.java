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
 * A property for a length.
 */
public class LengthProperty
    extends AbstractQuantity<LengthProperty.Unit> {

  /**
   * Creates a new instance.
   *
   * @param model The model component.
   */
  public LengthProperty(ModelComponent model) {
    this(model, 0, Unit.MM);
  }

  /**
   * Creates a new instance with a value and a unit.
   *
   * @param model The model component.
   * @param value The value.
   * @param unit The unit.
   */
  public LengthProperty(ModelComponent model, double value, Unit unit) {
    super(model, value, unit, Unit.class, relations());
  }

  @Override // Property
  public Object getComparableValue() {
    return String.valueOf(fValue) + getUnit();
  }

  private static List<Relation<Unit>> relations() {
    List<Relation<Unit>> relations = new ArrayList<>();
    relations.add(new Relation<>(Unit.MM, Unit.CM, 10));
    relations.add(new Relation<>(Unit.CM, Unit.M, 100));
    relations.add(new Relation<>(Unit.M, Unit.KM, 1000));
    return relations;
  }

  @Override
  protected void initValidRange() {
    validRange.setMin(0);
  }

  /**
   * Supported length units.
   */
  public enum Unit {

    /**
     * Millimeters.
     */
    MM("mm"),
    /**
     * Centimeters.
     */
    CM("cm"),
    /**
     * Meters.
     */
    M("m"),
    /**
     * Kilometers.
     */
    KM("km");

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
