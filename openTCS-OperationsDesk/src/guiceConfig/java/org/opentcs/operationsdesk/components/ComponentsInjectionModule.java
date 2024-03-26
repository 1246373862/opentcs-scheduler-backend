/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.operationsdesk.components;

import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import java.awt.event.MouseListener;
import javax.inject.Singleton;
import org.opentcs.guing.common.components.tree.AbstractTreeViewPanel;
import org.opentcs.guing.common.components.tree.BlockMouseListener;
import org.opentcs.guing.common.components.tree.BlocksTreeViewManager;
import org.opentcs.guing.common.components.tree.BlocksTreeViewPanel;
import org.opentcs.guing.common.components.tree.ComponentsTreeViewManager;
import org.opentcs.guing.common.components.tree.ComponentsTreeViewPanel;
import org.opentcs.guing.common.components.tree.TreeMouseAdapter;
import org.opentcs.guing.common.components.tree.TreeView;
import org.opentcs.operationsdesk.components.dialogs.DialogsInjectionModule;
import org.opentcs.operationsdesk.components.dockable.DockableInjectionModule;
import org.opentcs.operationsdesk.components.drawing.DrawingInjectionModule;
import org.opentcs.operationsdesk.components.layer.LayersInjectionModule;
import org.opentcs.operationsdesk.components.properties.PropertiesInjectionModule;
import org.opentcs.operationsdesk.components.tree.elements.TreeElementsInjectionModule;

/**
 * A Guice module for this package.
 */
public class ComponentsInjectionModule
    extends AbstractModule {

  /**
   * Creates a new instance.
   */
  public ComponentsInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new DialogsInjectionModule());
    install(new DockableInjectionModule());
    install(new DrawingInjectionModule());
    install(new PropertiesInjectionModule());
    install(new TreeElementsInjectionModule());

    install(new ComponentsTreeViewModule());
    install(new BlocksTreeViewModule());

    install(new LayersInjectionModule());
  }

  private static class ComponentsTreeViewModule
      extends PrivateModule {

    ComponentsTreeViewModule() {
    }

    @Override
    protected void configure() {
      // Within this (private) module, there should only be a single tree panel.
      bind(ComponentsTreeViewPanel.class)
          .in(Singleton.class);

      // Bind the tree panel annotated with the given annotation to our single
      // instance and expose only this annotated version.
      bind(AbstractTreeViewPanel.class)
          .to(ComponentsTreeViewPanel.class);
      expose(ComponentsTreeViewPanel.class);

      // Bind TreeView to the single tree panel, too.
      bind(TreeView.class)
          .to(ComponentsTreeViewPanel.class);

      // Bind and expose a single manager for the single tree view/panel.
      bind(ComponentsTreeViewManager.class)
          .in(Singleton.class);
      expose(ComponentsTreeViewManager.class);

      bind(MouseListener.class)
          .to(TreeMouseAdapter.class);
    }
  }

  private static class BlocksTreeViewModule
      extends PrivateModule {

    BlocksTreeViewModule() {
    }

    @Override
    protected void configure() {
      // Within this (private) module, there should only be a single tree panel.
      bind(BlocksTreeViewPanel.class)
          .in(Singleton.class);

      // Bind the tree panel annotated with the given annotation to our single
      // instance and expose only this annotated version.
      bind(AbstractTreeViewPanel.class)
          .to(BlocksTreeViewPanel.class);
      expose(BlocksTreeViewPanel.class);

      // Bind TreeView to the single tree panel, too.
      bind(TreeView.class)
          .to(BlocksTreeViewPanel.class);

      // Bind and expose a single manager for the single tree view/panel.
      bind(BlocksTreeViewManager.class)
          .in(Singleton.class);
      expose(BlocksTreeViewManager.class);

      bind(MouseListener.class)
          .to(BlockMouseListener.class);
    }
  }
}
