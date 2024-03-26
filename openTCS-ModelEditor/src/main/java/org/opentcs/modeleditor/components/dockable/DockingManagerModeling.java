/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.components.dockable;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.group.CGroupBehavior;
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.guing.common.components.dockable.AbstractDockingManager;
import org.opentcs.guing.common.components.dockable.CStack;
import org.opentcs.guing.common.components.properties.SelectionPropertiesComponent;
import org.opentcs.guing.common.components.tree.BlocksTreeViewManager;
import org.opentcs.guing.common.components.tree.ComponentsTreeViewManager;
import org.opentcs.modeleditor.components.layer.LayerGroupsPanel;
import org.opentcs.modeleditor.components.layer.LayersPanel;
import org.opentcs.modeleditor.util.I18nPlantOverviewModeling;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * Utility class for working with dockables.
 */
public class DockingManagerModeling
    extends AbstractDockingManager {

  /**
   * ID of the tab pane that contains the course, transport orders and order sequences.
   */
  public static final String COURSE_TAB_PANE_ID = "course_tab_pane";
  /**
   * ID of the tab pane that contains the components, blocks and groups.
   */
  private static final String TREE_TAB_PANE_ID = "tree_tab_pane";
  private static final String LAYER_TAB_PANE_ID = "layer_tab_pane";
  private static final String COMPONENTS_ID = "comp_dock";
  private static final String BLOCKS_ID = "block_dock";
  private static final String PROPERTIES_ID = "properties_id";
  private static final String LAYERS_ID = "layers_id";
  private static final String LAYER_GROUPS_ID = "layer_groups_id";
  /**
   * The tree view manager for components.
   */
  private final ComponentsTreeViewManager componentsTreeViewManager;
  /**
   * The tree view manager for blocks.
   */
  private final BlocksTreeViewManager blocksTreeViewManager;
  /**
   * The panel displaying the properties of the currently selected driving course components.
   */
  private final SelectionPropertiesComponent selectionPropertiesComponent;
  /**
   * The panel displaying the layers in the plant model.
   */
  private final LayersPanel layersPanel;
  /**
   * The panel displaying the layer groups in the plant model.
   */
  private final LayerGroupsPanel layerGroupsPanel;
  /**
   * Tab pane that contains the components, blocks and groups.
   */
  private CStack treeTabPane;
  /**
   * Tab pane that contains the course, transport orders and order sequences.
   */
  private CStack courseTabPane;
  /**
   * Tab pane that contains layers and layer groups.
   */
  private CStack layerTabPane;

  /**
   * Creates a new instance.
   *
   * @param applicationFrame The application's main frame.
   * @param componentsTreeViewManager The tree view manager for components.
   * @param blocksTreeViewManager The tree view manager for blocks.
   * @param selectionPropertiesComponent The panel displaying the properties of the currently
   * selected driving course components.
   * @param layersPanel The panel displaying the layers in the plant model.
   * @param layerGroupsPanel The panel displaying the layer groups in the plant model.
   */
  @Inject
  public DockingManagerModeling(@ApplicationFrame JFrame applicationFrame,
                                ComponentsTreeViewManager componentsTreeViewManager,
                                BlocksTreeViewManager blocksTreeViewManager,
                                SelectionPropertiesComponent selectionPropertiesComponent,
                                LayersPanel layersPanel,
                                LayerGroupsPanel layerGroupsPanel) {
    super(new CControl(applicationFrame));
    this.componentsTreeViewManager = requireNonNull(componentsTreeViewManager,
                                                    "componentsTreeViewManager");
    this.blocksTreeViewManager = requireNonNull(blocksTreeViewManager, "blocksTreeViewManager");
    this.selectionPropertiesComponent = requireNonNull(selectionPropertiesComponent,
                                                       "selectionPropertiesComponent");
    this.layersPanel = requireNonNull(layersPanel, "layersPanel");
    this.layerGroupsPanel = requireNonNull(layerGroupsPanel, "layerGroupsPanel");
  }

  @Override
  public void reset() {
    removeDockable(BLOCKS_ID);
    removeDockable(COMPONENTS_ID);
    removeDockable(PROPERTIES_ID);
    removeDockable(LAYERS_ID);
    removeDockable(LAYER_GROUPS_ID);
    getCControl().removeStation(getTabPane(COURSE_TAB_PANE_ID));
    getCControl().removeStation(getTabPane(TREE_TAB_PANE_ID));
    getCControl().removeStation(getTabPane(LAYER_TAB_PANE_ID));
  }

  @Override
  public void initializeDockables() {
    getCControl().setGroupBehavior(CGroupBehavior.TOPMOST);

    // Disable keyboard shortcuts to avoid collisions.
    getCControl().putProperty(CControl.KEY_GOTO_NORMALIZED, null);
    getCControl().putProperty(CControl.KEY_GOTO_EXTERNALIZED, null);
    getCControl().putProperty(CControl.KEY_GOTO_MAXIMIZED, null);
    getCControl().putProperty(CControl.KEY_MAXIMIZE_CHANGE, null);

    ResourceBundleUtil bundle
        = ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.DOCKABLE_PATH);
    CGrid grid = new CGrid(getCControl());
    courseTabPane = new CStack(COURSE_TAB_PANE_ID);
    addTabPane(COURSE_TAB_PANE_ID, courseTabPane);
    treeTabPane = new CStack(TREE_TAB_PANE_ID);
    addTabPane(TREE_TAB_PANE_ID, treeTabPane);
    layerTabPane = new CStack(LAYER_TAB_PANE_ID);
    addTabPane(LAYER_TAB_PANE_ID, layerTabPane);
    DefaultSingleCDockable treeViewDock
        = createDockable(COMPONENTS_ID,
                         bundle.getString("dockingManagerModeling.panel_components.title"),
                         (JComponent) componentsTreeViewManager.getTreeView(),
                         false);
    DefaultSingleCDockable treeBlocks
        = createDockable(BLOCKS_ID,
                         bundle.getString("dockingManagerModeling.panel_blocks.title"),
                         (JComponent) blocksTreeViewManager.getTreeView(),
                         false);

    grid.add(0, 0, 250, 400, treeTabPane);
    grid.add(0, 400, 250, 300,
             createDockable(PROPERTIES_ID,
                            bundle.getString("dockingManagerModeling.panel_properties.title"),
                            selectionPropertiesComponent,
                            false));
    DefaultSingleCDockable layersDock
        = createDockable(LAYERS_ID,
                         bundle.getString("dockingManagerModeling.panel_layers.title"),
                         layersPanel,
                         false);
    DefaultSingleCDockable layerGroupsDock
        = createDockable(LAYER_GROUPS_ID,
                         bundle.getString("dockingManagerModeling.panel_layerGroups.title"),
                         layerGroupsPanel,
                         false);
    grid.add(0, 700, 250, 300, layerTabPane);
    grid.add(400, 0, 1000, 500, courseTabPane);

    getCControl().getContentArea().deploy(grid);

    // init tab panes
    addTabTo(treeViewDock, TREE_TAB_PANE_ID, 0);
    addTabTo(treeBlocks, TREE_TAB_PANE_ID, 1);
    addTabTo(layersDock, LAYER_TAB_PANE_ID, 0);
    addTabTo(layerGroupsDock, LAYER_TAB_PANE_ID, 1);
    treeTabPane.getStation().setFrontDockable(treeViewDock.intern());
    layerTabPane.getStation().setFrontDockable(layersDock.intern());
  }
}
