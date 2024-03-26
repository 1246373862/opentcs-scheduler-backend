/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.modeleditor.application.menus;

import com.google.inject.assistedinject.Assisted;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.util.Objects.requireNonNull;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.model.visualization.ElementPropKeys;
import org.opentcs.guing.base.components.properties.event.AttributesChangeEvent;
import org.opentcs.guing.base.components.properties.event.NullAttributesChangeListener;
import org.opentcs.guing.base.components.properties.type.CoordinateProperty;
import org.opentcs.guing.base.components.properties.type.ModelAttribute;
import org.opentcs.guing.base.components.properties.type.StringProperty;
import org.opentcs.guing.base.model.ModelComponent;
import org.opentcs.guing.base.model.elements.LocationModel;
import org.opentcs.guing.base.model.elements.PointModel;
import org.opentcs.guing.common.components.drawing.OpenTCSDrawingEditor;
import org.opentcs.guing.common.components.drawing.figures.FigureConstants;
import org.opentcs.guing.common.components.drawing.figures.LabeledFigure;
import org.opentcs.guing.common.components.properties.CoordinateUndoActivity;
import org.opentcs.guing.common.event.ResetInteractionToolCommand;
import org.opentcs.modeleditor.util.I18nPlantOverviewModeling;
import org.opentcs.thirdparty.guing.common.jhotdraw.application.action.edit.UndoRedoManager;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;
import org.opentcs.util.event.EventHandler;

/**
 * A menu item for copying the value of the model properties of selected points
 * or locations to the corresponding layout properties.
 */
public class ModelToLayoutMenuItem
    extends JMenuItem {

  /**
   * The <code>DrawingEditor</code> instance.
   */
  private final DrawingEditor drawingEditor;
  /**
   * The UndoRedoManager instance to be used.
   */
  private final UndoRedoManager undoRedoManager;
  /**
   * Where we send events.
   */
  private final EventHandler eventBus;
  /**
   * The components factory.
   */
  private final MenuItemComponentsFactory componentsFactory;
  /**
   * A flag if the values of ALL points and location shall be copied when
   * the menu item is clicked. If false only the selected figures will be
   * considered.
   */
  private final boolean copyAll;

  /**
   * Creates a new instance.
   *
   * @param drawingEditor A <code>DrawingEditor</code> instance.
   * @param undoRedoManager The application's undo/redo manager.
   * @param eventHandler Where this instance sends events.
   * @param componentsFactory The components factory.
   * @param copyAll Indicates whether the values of ALL points and locations
   * shall be copied when the menu item is clicked. If false only the selected
   * figures will be considered.
   */
  @Inject
  public ModelToLayoutMenuItem(OpenTCSDrawingEditor drawingEditor,
                               UndoRedoManager undoRedoManager,
                               @ApplicationEventBus EventHandler eventHandler,
                               MenuItemComponentsFactory componentsFactory,
                               @Assisted boolean copyAll) {
    super(ResourceBundleUtil.getBundle(I18nPlantOverviewModeling.MENU_PATH)
        .getString("modelToLayoutMenuItem.text"));
    this.drawingEditor = requireNonNull(drawingEditor, "drawingEditor");
    this.undoRedoManager = requireNonNull(undoRedoManager, "undoRedoManager");
    this.eventBus = requireNonNull(eventHandler, "eventHandler");
    this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
    this.copyAll = copyAll;

    setIcon(new ImageIcon(
        getClass().getClassLoader()
            .getResource("org/opentcs/guing/res/symbols/menu/arrow-down-3.png")
    ));
    setMargin(new Insets(0, 2, 0, 2));
    addActionListener();
  }

  private void addActionListener() {
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        for (Figure figure : copyAll
            ? getView().getDrawing().getFiguresFrontToBack()
            : getView().getSelectedFigures()) {
          ModelComponent model = figure.get(FigureConstants.MODEL);
          if (model instanceof PointModel || model instanceof LocationModel) {
            updateLayoutX(model);
            updateLayoutY(model);
            // ... and move the figure
            final LabeledFigure labeledFigure = (LabeledFigure) figure;
            labeledFigure.propertiesChanged(new AttributesChangeEvent(
                new NullAttributesChangeListener(), model));

            model.propertiesChanged(new NullAttributesChangeListener());
            eventBus.onEvent(new ResetInteractionToolCommand(this));
          }
        }
      }
    });
  }

  private DrawingView getView() {
    return drawingEditor.getActiveView();
  }

  private void updateLayoutY(ModelComponent model) {
    CoordinateProperty modelProperty;
    if (model instanceof PointModel) {
      modelProperty = (CoordinateProperty) model.getProperty(PointModel.MODEL_Y_POSITION);
    }
    else {
      modelProperty = (CoordinateProperty) model.getProperty(LocationModel.MODEL_Y_POSITION);
    }
    CoordinateUndoActivity cua
        = componentsFactory.createModelToLayoutCoordinateUndoActivity(modelProperty);
    cua.snapShotBeforeModification();
    modelProperty.setChangeState(ModelAttribute.ChangeState.DETAIL_CHANGED);
    StringProperty spy;
    if (model instanceof PointModel) {
      spy = (StringProperty) model.getProperty(ElementPropKeys.POINT_POS_Y);
    }
    else {
      spy = (StringProperty) model.getProperty(ElementPropKeys.LOC_POS_Y);
    }
    spy.setText(String.valueOf(((Number) modelProperty.getValue()).intValue()));
    spy.markChanged();
    cua.snapShotAfterModification();
    undoRedoManager.addEdit(cua);
  }

  private void updateLayoutX(ModelComponent model) {
    CoordinateProperty modelProperty;
    if (model instanceof PointModel) {
      modelProperty = (CoordinateProperty) model.getProperty(PointModel.MODEL_X_POSITION);
    }
    else {
      modelProperty = (CoordinateProperty) model.getProperty(LocationModel.MODEL_X_POSITION);
    }
    CoordinateUndoActivity cua
        = componentsFactory.createModelToLayoutCoordinateUndoActivity(modelProperty);
    cua.snapShotBeforeModification();
    modelProperty.setChangeState(ModelAttribute.ChangeState.DETAIL_CHANGED);
    // Copy the model coordinates to the layout coordinates...
    StringProperty spx;
    if (model instanceof PointModel) {
      spx = (StringProperty) model.getProperty(ElementPropKeys.POINT_POS_X);
    }
    else {
      spx = (StringProperty) model.getProperty(ElementPropKeys.LOC_POS_X);
    }
    spx.setText(String.valueOf(((Number) modelProperty.getValue()).intValue()));
    spx.markChanged();
    cua.snapShotAfterModification();
    undoRedoManager.addEdit(cua);
  }
}
