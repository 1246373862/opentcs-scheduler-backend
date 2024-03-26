/**
 * (c): IML, JHotDraw.
 *
 * Changed by IML to allow access to ResourceBundle.
 *
 *
 * @(#)SendToBackAction.java
 *
 * Copyright (c) 2003-2008 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package org.opentcs.thirdparty.modeleditor.jhotdraw.application.action.draw;

import java.util.ArrayList;
import java.util.Collection;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.opentcs.guing.common.util.ImageDirectory;
import static org.opentcs.modeleditor.util.I18nPlantOverviewModeling.TOOLBAR_PATH;
import org.opentcs.thirdparty.guing.common.jhotdraw.util.ResourceBundleUtil;

/**
 * SendToBackAction.
 *
 * @author Werner Randelshofer
 */
public class SendToBackAction
    extends AbstractSelectedAction {

  /**
   * This action's ID.
   */
  public static final String ID = "edit.sendToBack";

  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(TOOLBAR_PATH);

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor
   */
  public SendToBackAction(DrawingEditor editor) {
    super(editor);

    putValue(NAME, BUNDLE.getString("sendToBackAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("sendToBackAction.shortDescription"));
    putValue(SMALL_ICON, ImageDirectory.getImageIcon("/toolbar/object-order-back.png"));

    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    final DrawingView view = getView();
    final Collection<Figure> figures = new ArrayList<>(view.getSelectedFigures());
    sendToBack(view, figures);
    fireUndoableEditHappened(new AbstractUndoableEdit() {
      @Override
      public String getPresentationName() {
        return ResourceBundleUtil.getBundle(TOOLBAR_PATH)
            .getString("sendToBackAction.undo.presentationName");
      }

      @Override
      public void redo()
          throws CannotRedoException {
        super.redo();
        SendToBackAction.sendToBack(view, figures);
      }

      @Override
      public void undo()
          throws CannotUndoException {
        super.undo();
        BringToFrontAction.bringToFront(view, figures);
      }
    });
  }

  public static void sendToBack(DrawingView view, Collection<Figure> figures) {
    Drawing drawing = view.getDrawing();

    for (Figure figure : figures) {
      drawing.sendToBack(figure);
    }
  }
}
