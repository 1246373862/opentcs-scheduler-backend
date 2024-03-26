/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.operationsdesk.transport.sequences;

import com.google.inject.assistedinject.Assisted;
import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.SharedKernelServicePortal;
import org.opentcs.access.SharedKernelServicePortalProvider;
import org.opentcs.components.plantoverview.ObjectHistoryEntryFormatter;
import org.opentcs.data.ObjectHistory;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.OrderSequence;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.guing.common.components.dialogs.DialogContent;
import org.opentcs.operationsdesk.transport.CompositeObjectHistoryEntryFormatter;
import org.opentcs.operationsdesk.transport.UneditableTableModel;
import org.opentcs.operationsdesk.util.I18nPlantOverviewOperating;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays an order sequence.
 */
public class OrderSequenceView
    extends DialogContent {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(OrderSequenceView.class);
  /**
   * A formatter for timestamps.
   */
  private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  /**
   * The order sequence to be shown.
   */
  private final OrderSequence fOrderSequence;
  /**
   * The portal provider to be used.
   */
  private final SharedKernelServicePortalProvider portalProvider;
  /**
   * A formatter for history entries.
   */
  private final ObjectHistoryEntryFormatter historyEntryFormatter;

  /**
   * Creates new instance.
   *
   * @param sequence The order sequence.
   * @param historyEntryFormatter A formatter for history entries.
   * @param portalProvider Provides access to a portal.
   */
  @Inject
  public OrderSequenceView(@Assisted OrderSequence sequence,
                           @Nonnull CompositeObjectHistoryEntryFormatter historyEntryFormatter,
                           SharedKernelServicePortalProvider portalProvider) {
    this.fOrderSequence = requireNonNull(sequence, "sequence");
    this.historyEntryFormatter = requireNonNull(historyEntryFormatter, "historyEntryFormatter");
    this.portalProvider = requireNonNull(portalProvider, "portalProvider");
    initComponents();
    initFields();
  }

  /**
   * Returns the order sequence.
   *
   * @return The order sequence.
   */
  public OrderSequence getOrderSequence() {
    return fOrderSequence;
  }

  @Override
  public void update() {
    // Do nada.
  }

  @Override
  public final void initFields() {
    setDialogTitle(ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.OSDETAIL_PATH)
        .getString("orderSequenceView.title"));
    // Name
    String name = getOrderSequence().getName();
    textFieldName.setText(name);

    boolean complete = getOrderSequence().isComplete();
    checkBoxComplete.setSelected(complete);
    checkBoxComplete.setEnabled(!complete);

    boolean finished = getOrderSequence().isFinished();
    checkBoxFinished.setSelected(finished);

    boolean failureFatal = getOrderSequence().isFailureFatal();
    checkBoxFailureFatal.setSelected(failureFatal);

    int finishedIndex = getOrderSequence().getFinishedIndex();
    textFieldFinishedIndex.setText("" + finishedIndex);

    TCSObjectReference<Vehicle> intendedVehicle = getOrderSequence().getIntendedVehicle();

    if (intendedVehicle != null) {
      textFieldIntendedVehicle.setText(intendedVehicle.getName());
    }

    TCSObjectReference<Vehicle> processingVehicle = getOrderSequence().getProcessingVehicle();

    if (processingVehicle != null) {
      textFieldProcessingVehicle.setText(processingVehicle.getName());
    }

    textType.setText(getOrderSequence().getType());

    DefaultTableModel tableModel = new UneditableTableModel();
    tableModel.setColumnIdentifiers(new String[]{"Name"});

    for (TCSObjectReference<TransportOrder> to : getOrderSequence().getOrders()) {
      String[] row = new String[1];
      row[0] = to.getName();
      tableModel.addRow(row);
    }

    transportOrdersTable.setModel(tableModel);

    propertiesTable.setModel(createPropertiesTableModel());

    historyTable.setModel(createHistoryTableModel());
    historyTable.getColumnModel().getColumn(0).setPreferredWidth(100);
    historyTable.getColumnModel().getColumn(1).setPreferredWidth(300);
    historyTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer());
  }

  private TableModel createPropertiesTableModel() {
    DefaultTableModel tableModel = new UneditableTableModel();

    tableModel.setColumnIdentifiers(
        new String[]{
          ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.OSDETAIL_PATH)
              .getString(
                  "orderSequenceView.table_properties.column_propertiesKey.headerText"
              ),
          ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.OSDETAIL_PATH)
              .getString(
                  "orderSequenceView.table_properties.column_propertiesValue.headerText"
              )
        }
    );
    fOrderSequence.getProperties().entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
        .forEach(entry -> {
          tableModel.addRow(new String[]{entry.getKey(), entry.getValue()});
        });

    return tableModel;
  }

  private TableModel createHistoryTableModel() {
    DefaultTableModel tableModel = new UneditableTableModel();

    tableModel.setColumnIdentifiers(
        new String[]{
          ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.OSDETAIL_PATH)
              .getString("orderSequenceView.table_history.column_timestamp.headerText"),
          ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.OSDETAIL_PATH)
              .getString("orderSequenceView.table_history.column_event.headerText")
        }
    );

    for (ObjectHistory.Entry entry : fOrderSequence.getHistory().getEntries()) {
      tableModel.addRow(new String[]{
        TIMESTAMP_FORMAT.format(Date.from(entry.getTimestamp())),
        historyEntryFormatter.apply(entry).get()
      });
    }

    return tableModel;
  }

  // CHECKSTYLE:OFF
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    generalPanel = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    labelName = new javax.swing.JLabel();
    textFieldName = new javax.swing.JTextField();
    labelFinishedIndex = new javax.swing.JLabel();
    textFieldFinishedIndex = new javax.swing.JTextField();
    labelIntendedVehicle = new javax.swing.JLabel();
    textFieldIntendedVehicle = new javax.swing.JTextField();
    labelProcessingVehicle = new javax.swing.JLabel();
    textFieldProcessingVehicle = new javax.swing.JTextField();
    labelType = new javax.swing.JLabel();
    textType = new javax.swing.JTextField();
    jPanel2 = new javax.swing.JPanel();
    checkBoxComplete = new javax.swing.JCheckBox();
    checkBoxFinished = new javax.swing.JCheckBox();
    checkBoxFailureFatal = new javax.swing.JCheckBox();
    transportOrdersPanel = new javax.swing.JPanel();
    transportOrdersScrollPane = new javax.swing.JScrollPane();
    transportOrdersTable = new javax.swing.JTable();
    propertiesPanel = new javax.swing.JPanel();
    propertiesScrollPane = new javax.swing.JScrollPane();
    propertiesTable = new javax.swing.JTable();
    historyPanel = new javax.swing.JPanel();
    historyScrollPane = new javax.swing.JScrollPane();
    historyTable = new javax.swing.JTable();

    setLayout(new java.awt.GridBagLayout());

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/org/opentcs/plantoverview/operating/dialogs/orderSequenceDetail"); // NOI18N
    generalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("orderSequenceView.panel_general.border.title"))); // NOI18N
    generalPanel.setLayout(new java.awt.GridBagLayout());

    jPanel1.setLayout(new java.awt.GridBagLayout());

    labelName.setFont(labelName.getFont());
    labelName.setText(bundle.getString("orderSequenceView.panel_general.label_name.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    jPanel1.add(labelName, gridBagConstraints);

    textFieldName.setEditable(false);
    textFieldName.setColumns(10);
    textFieldName.setFont(textFieldName.getFont());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    jPanel1.add(textFieldName, gridBagConstraints);

    labelFinishedIndex.setFont(labelFinishedIndex.getFont());
    labelFinishedIndex.setText(bundle.getString("orderSequenceView.panel_general.label_finishedIndex.text")); // NOI18N
    labelFinishedIndex.setToolTipText(bundle.getString("orderSequenceView.panel_general.label_finishedIndex.tooltipText")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    jPanel1.add(labelFinishedIndex, gridBagConstraints);

    textFieldFinishedIndex.setEditable(false);
    textFieldFinishedIndex.setColumns(10);
    textFieldFinishedIndex.setFont(textFieldFinishedIndex.getFont());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    jPanel1.add(textFieldFinishedIndex, gridBagConstraints);

    labelIntendedVehicle.setFont(labelIntendedVehicle.getFont());
    labelIntendedVehicle.setText(bundle.getString("orderSequenceView.panel_general.label_intendedVehicle.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    jPanel1.add(labelIntendedVehicle, gridBagConstraints);

    textFieldIntendedVehicle.setEditable(false);
    textFieldIntendedVehicle.setColumns(10);
    textFieldIntendedVehicle.setFont(textFieldIntendedVehicle.getFont());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    jPanel1.add(textFieldIntendedVehicle, gridBagConstraints);

    labelProcessingVehicle.setFont(labelProcessingVehicle.getFont());
    labelProcessingVehicle.setText(bundle.getString("orderSequenceView.panel_general.label_processingVehicle.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    jPanel1.add(labelProcessingVehicle, gridBagConstraints);

    textFieldProcessingVehicle.setEditable(false);
    textFieldProcessingVehicle.setColumns(10);
    textFieldProcessingVehicle.setFont(textFieldProcessingVehicle.getFont());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    jPanel1.add(textFieldProcessingVehicle, gridBagConstraints);

    labelType.setFont(labelType.getFont());
    labelType.setText(bundle.getString("orderSequenceView.panel_general.label_type.text")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
    jPanel1.add(labelType, gridBagConstraints);

    textType.setEditable(false);
    textType.setColumns(10);
    textType.setFont(textType.getFont());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    jPanel1.add(textType, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
    gridBagConstraints.weightx = 0.5;
    generalPanel.add(jPanel1, gridBagConstraints);

    jPanel2.setLayout(new java.awt.GridBagLayout());

    checkBoxComplete.setText(bundle.getString("orderSequenceView.panel_general.checkBox_complete.text")); // NOI18N
    checkBoxComplete.setToolTipText(bundle.getString("orderSequenceView.panel_general.checkBox_complete.tooltipText")); // NOI18N
    checkBoxComplete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkBoxCompleteActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add(checkBoxComplete, gridBagConstraints);

    checkBoxFinished.setText(bundle.getString("orderSequenceView.panel_general.checkBox_finished.text")); // NOI18N
    checkBoxFinished.setToolTipText(bundle.getString("orderSequenceView.panel_general.checkBox_finished.tooltipText")); // NOI18N
    checkBoxFinished.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add(checkBoxFinished, gridBagConstraints);

    checkBoxFailureFatal.setText(bundle.getString("orderSequenceView.panel_general.checkBox_failureFatal.text")); // NOI18N
    checkBoxFailureFatal.setToolTipText(bundle.getString("orderSequenceView.panel_general.checkBox_failureFatal.tooltipText")); // NOI18N
    checkBoxFailureFatal.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel2.add(checkBoxFailureFatal, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
    generalPanel.add(jPanel2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.1;
    add(generalPanel, gridBagConstraints);

    transportOrdersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("orderSequenceView.panel_transportOrders.border.title"))); // NOI18N
    transportOrdersPanel.setLayout(new java.awt.GridBagLayout());

    transportOrdersScrollPane.setPreferredSize(new java.awt.Dimension(100, 100));
    transportOrdersScrollPane.setViewportView(transportOrdersTable);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    transportOrdersPanel.add(transportOrdersScrollPane, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    add(transportOrdersPanel, gridBagConstraints);

    propertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("orderSequenceView.panel_properties.border.text"))); // NOI18N
    propertiesPanel.setLayout(new java.awt.BorderLayout());

    propertiesScrollPane.setPreferredSize(new java.awt.Dimension(150, 100));

    propertiesTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {},
        {},
        {},
        {}
      },
      new String [] {

      }
    ));
    propertiesScrollPane.setViewportView(propertiesTable);

    propertiesPanel.add(propertiesScrollPane, java.awt.BorderLayout.CENTER);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    add(propertiesPanel, gridBagConstraints);

    historyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("orderSequenceView.panel_history.border.text"))); // NOI18N
    historyPanel.setLayout(new java.awt.BorderLayout());

    historyScrollPane.setPreferredSize(new java.awt.Dimension(150, 100));

    historyTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {},
        {},
        {},
        {}
      },
      new String [] {

      }
    ));
    historyScrollPane.setViewportView(historyTable);

    historyPanel.add(historyScrollPane, java.awt.BorderLayout.CENTER);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    add(historyPanel, gridBagConstraints);
  }// </editor-fold>//GEN-END:initComponents
  // CHECKSTYLE:ON

  private void checkBoxCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxCompleteActionPerformed
    ResourceBundleUtil bundle
        = ResourceBundleUtil.getBundle(I18nPlantOverviewOperating.OSDETAIL_PATH);
    int n = JOptionPane.showConfirmDialog(
        this,
        bundle.getString("orderSequenceView.optionPane_markSequenceCompleteConfirmation.message"),
        bundle.getString("orderSequenceView.optionPane_markSequenceCompleteConfirmation.title"),
        JOptionPane.YES_NO_OPTION
    );

    if (n == JOptionPane.OK_OPTION) {
      try (SharedKernelServicePortal sharedPortal = portalProvider.register()) {
        sharedPortal.getPortal().getTransportOrderService()
            .markOrderSequenceComplete(fOrderSequence.getReference());
        checkBoxComplete.setEnabled(false);
      }
      catch (KernelRuntimeException exc) {
        LOG.warn("Exception setting order sequence complete", exc);
      }
    }
    else {
      checkBoxComplete.setSelected(false);
    }
  }//GEN-LAST:event_checkBoxCompleteActionPerformed

  // CHECKSTYLE:OFF
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox checkBoxComplete;
  private javax.swing.JCheckBox checkBoxFailureFatal;
  private javax.swing.JCheckBox checkBoxFinished;
  private javax.swing.JPanel generalPanel;
  private javax.swing.JPanel historyPanel;
  private javax.swing.JScrollPane historyScrollPane;
  private javax.swing.JTable historyTable;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JLabel labelFinishedIndex;
  private javax.swing.JLabel labelIntendedVehicle;
  private javax.swing.JLabel labelName;
  private javax.swing.JLabel labelProcessingVehicle;
  private javax.swing.JLabel labelType;
  private javax.swing.JPanel propertiesPanel;
  private javax.swing.JScrollPane propertiesScrollPane;
  private javax.swing.JTable propertiesTable;
  private javax.swing.JTextField textFieldFinishedIndex;
  private javax.swing.JTextField textFieldIntendedVehicle;
  private javax.swing.JTextField textFieldName;
  private javax.swing.JTextField textFieldProcessingVehicle;
  private javax.swing.JTextField textType;
  private javax.swing.JPanel transportOrdersPanel;
  private javax.swing.JScrollPane transportOrdersScrollPane;
  private javax.swing.JTable transportOrdersTable;
  // End of variables declaration//GEN-END:variables
  // CHECKSTYLE:ON

  /**
   * A cell renderer that adds a tool tip with the cell's value.
   */
  private static class ToolTipCellRenderer
      extends DefaultTableCellRenderer {

    /**
     * Creates a new instance.
     */
    ToolTipCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
      Component component = super.getTableCellRendererComponent(table,
                                                                value,
                                                                isSelected,
                                                                hasFocus,
                                                                row,
                                                                column);

      ((JComponent) component).setToolTipText(value.toString());

      return component;
    }
  }
}
