/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.components.properties.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.opentcs.guing.base.components.properties.type.AbstractProperty;
import org.opentcs.guing.base.components.properties.type.AcceptableInvalidValue;
import org.opentcs.guing.base.components.properties.type.MultipleDifferentValues;
import org.opentcs.guing.base.components.properties.type.Property;

/**
 * A standard cell renderer for for properties in general.
 */
public class StandardPropertyCellRenderer
    extends javax.swing.table.DefaultTableCellRenderer {

  /**
   * The background color for uneditable properties.
   */
  public static final Color BG_UNEDITABLE = new Color(0xE0E0E0);

  /**
   * Creates a new instance.
   */
  public StandardPropertyCellRenderer() {
    super();
    setStyle();
  }

  /**
   * Initialises the style fo the labels.
   */
  protected final void setStyle() {
    setFont(new Font("Dialog", Font.PLAIN, 12));
    setHorizontalAlignment(JLabel.LEFT);
    setBorder(null);
  }

  /**
   * Returns a component with the visualisation for this property.
   *
   * @return a component with the visualisation for this property.
   */
  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected,
                                                                hasFocus, row, column);
    label.setText(value.toString());

    if (value instanceof AbstractProperty
        && ((AbstractProperty) value).getValue() instanceof AcceptableInvalidValue) {
      AbstractProperty property = (AbstractProperty) value;
      AcceptableInvalidValue invalidValue = (AcceptableInvalidValue) property.getValue();
      label.setText(invalidValue.getDescription());
      label.setToolTipText(invalidValue.getHelptext());
    }
    else if (value instanceof Property) {
      label.setToolTipText(((Property) value).getHelptext());
    }

    decorate(table, row, column, label, value);

    return this;
  }

  protected void decorate(JTable table, int row, int column, JLabel label, Object value) {
    AttributesTable attributesTable = (AttributesTable) table;
    boolean editable = attributesTable.isEditable(row);

    switch (column) {
      case 0:
        label.setBackground(BG_UNEDITABLE);
        label.setForeground(Color.darkGray);
        break;

      case 1:
        if (value instanceof MultipleDifferentValues) {
          label.setBackground(AbstractPropertyCellEditor.DIFFERENT_VALUE_COLOR);
        }
        else {
          label.setBackground(editable ? Color.white : BG_UNEDITABLE);
        }
        label.setForeground(editable ? Color.blue : Color.darkGray);
        break;

      default:
    }
  }
}
