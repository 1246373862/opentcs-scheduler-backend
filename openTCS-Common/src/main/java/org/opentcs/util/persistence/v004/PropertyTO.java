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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"name", "value"})
public class PropertyTO {

  private String name = "";
  private String value = "";

  /**
   * Creates a new instance.
   */
  public PropertyTO() {
  }

  @XmlAttribute(required = true)
  public String getName() {
    return name;
  }

  public PropertyTO setName(@Nonnull String name) {
    this.name = requireNonNull(name, "name");
    return this;
  }

  @XmlAttribute(required = true)
  public String getValue() {
    return value;
  }

  public PropertyTO setValue(@Nonnull String value) {
    requireNonNull(value, "value");
    this.value = value;
    return this;
  }
}
