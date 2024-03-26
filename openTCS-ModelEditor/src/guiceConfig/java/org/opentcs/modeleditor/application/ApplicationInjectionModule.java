/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.application;

import com.google.inject.AbstractModule;
import java.awt.Component;
import javax.inject.Singleton;
import javax.swing.JFrame;
import org.jhotdraw.app.Application;
import org.opentcs.customizations.plantoverview.ApplicationFrame;
import org.opentcs.guing.common.application.ApplicationState;
import org.opentcs.guing.common.application.ComponentsManager;
import org.opentcs.guing.common.application.GuiManager;
import org.opentcs.guing.common.application.PluginPanelManager;
import org.opentcs.guing.common.application.ProgressIndicator;
import org.opentcs.guing.common.application.SplashFrame;
import org.opentcs.guing.common.application.StatusPanel;
import org.opentcs.guing.common.application.ViewManager;
import org.opentcs.modeleditor.application.action.ActionInjectionModule;
import org.opentcs.modeleditor.application.menus.MenusInjectionModule;
import org.opentcs.modeleditor.application.toolbar.ToolBarInjectionModule;
import org.opentcs.thirdparty.guing.common.jhotdraw.application.action.edit.UndoRedoManager;
import org.opentcs.thirdparty.modeleditor.jhotdraw.application.OpenTCSSDIApplication;

/**
 * An injection module for this package.
 */
public class ApplicationInjectionModule
    extends AbstractModule {

  /**
   * The application's main frame.
   */
  private final JFrame applicationFrame = new JFrame();

  /**
   * Creates a new instance.
   */
  public ApplicationInjectionModule() {
  }

  @Override
  protected void configure() {
    install(new ActionInjectionModule());
    install(new MenusInjectionModule());
    install(new ToolBarInjectionModule());

    bind(ApplicationState.class).in(Singleton.class);

    bind(UndoRedoManager.class).in(Singleton.class);

    bind(ProgressIndicator.class)
        .to(SplashFrame.class)
        .in(Singleton.class);
    bind(StatusPanel.class).in(Singleton.class);

    bind(JFrame.class)
        .annotatedWith(ApplicationFrame.class)
        .toInstance(applicationFrame);
    bind(Component.class)
        .annotatedWith(ApplicationFrame.class)
        .toInstance(applicationFrame);

    bind(ViewManagerModeling.class)
        .in(Singleton.class);
    bind(ViewManager.class).to(ViewManagerModeling.class);

    bind(Application.class)
        .to(OpenTCSSDIApplication.class)
        .in(Singleton.class);

    bind(OpenTCSView.class).in(Singleton.class);
    bind(GuiManager.class).to(OpenTCSView.class);
    bind(ComponentsManager.class).to(OpenTCSView.class);
    bind(PluginPanelManager.class).to(OpenTCSView.class);
  }

}
