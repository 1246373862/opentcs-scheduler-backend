/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.util.persistence.v004;

import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    propOrder = {"name", "length", "energyLevelCritical", "energyLevelGood",
                 "energyLevelFullyRecharged", "energyLevelSufficientlyRecharged",
                 "maxVelocity", "maxReverseVelocity", "properties", "vehicleLayout"})
public class VehicleTO
    extends PlantModelElementTO {

  //max velocity in mm/s.
  private int maxVelocity;
  //max rev velocity in mm/s.
  private int maxReverseVelocity;
  private Long length = 0L;
  private Long energyLevelCritical = 0L;
  private Long energyLevelGood = 0L;
  private Long energyLevelFullyRecharged = 0L;
  private Long energyLevelSufficientlyRecharged = 0L;
  private String envelopeKey;
  private VehicleLayout vehicleLayout = new VehicleLayout();

  /**
   * Creates a new instance.
   */
  public VehicleTO() {
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public Long getLength() {
    return length;
  }

  public VehicleTO setLength(@Nonnull Long length) {
    requireNonNull(length, "length");
    this.length = length;
    return this;
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public Long getEnergyLevelCritical() {
    return energyLevelCritical;
  }

  public VehicleTO setEnergyLevelCritical(@Nonnull Long energyLevelCritical) {
    requireNonNull(energyLevelCritical, "energyLevelCritical");
    this.energyLevelCritical = energyLevelCritical;
    return this;
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public Long getEnergyLevelGood() {
    return energyLevelGood;
  }

  public VehicleTO setEnergyLevelGood(@Nonnull Long energyLevelGood) {
    requireNonNull(energyLevelGood, "energyLevelGood");
    this.energyLevelGood = energyLevelGood;
    return this;
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public Long getEnergyLevelFullyRecharged() {
    return energyLevelFullyRecharged;
  }

  public VehicleTO setEnergyLevelFullyRecharged(@Nonnull Long energyLevelFullyRecharged) {
    requireNonNull(energyLevelFullyRecharged, "energyLevelFullyRecharged");
    this.energyLevelFullyRecharged = energyLevelFullyRecharged;
    return this;
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public Long getEnergyLevelSufficientlyRecharged() {
    return energyLevelSufficientlyRecharged;
  }

  public VehicleTO setEnergyLevelSufficientlyRecharged(
      @Nonnull Long energyLevelSufficientlyRecharged) {
    requireNonNull(energyLevelSufficientlyRecharged, "energyLevelSufficientlyRecharged");
    this.energyLevelSufficientlyRecharged = energyLevelSufficientlyRecharged;
    return this;
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public int getMaxVelocity() {
    return maxVelocity;
  }

  public VehicleTO setMaxVelocity(@Nonnull int maxVelocity) {
    this.maxVelocity = maxVelocity;
    return this;
  }

  @XmlAttribute
  @XmlSchemaType(name = "unsignedInt")
  public int getMaxReverseVelocity() {
    return maxReverseVelocity;
  }

  public VehicleTO setMaxReverseVelocity(@Nonnull int maxReverseVelocity) {
    this.maxReverseVelocity = maxReverseVelocity;
    return this;
  }
  
  @XmlAttribute
  @Nullable
  public String getEnvelopeKey() {
    return envelopeKey;
  }

  public VehicleTO setEnvelopeKey(@Nullable String envelopeKey) {
    this.envelopeKey = envelopeKey;
    return this;
  }

  @XmlElement(required = true)
  public VehicleLayout getVehicleLayout() {
    return vehicleLayout;
  }

  public VehicleTO setVehicleLayout(@Nonnull VehicleLayout vehicleLayout) {
    this.vehicleLayout = requireNonNull(vehicleLayout, "vehicleLayout");
    return this;
  }

  @XmlAccessorType(XmlAccessType.PROPERTY)
  public static class VehicleLayout {

    private String color = "";

    /**
     * Creates a new instance.
     */
    public VehicleLayout() {
    }

    @XmlAttribute(required = true)
    public String getColor() {
      return color;
    }

    public VehicleLayout setColor(@Nonnull String color) {
      this.color = requireNonNull(color, "color");
      return this;
    }
  }
}
