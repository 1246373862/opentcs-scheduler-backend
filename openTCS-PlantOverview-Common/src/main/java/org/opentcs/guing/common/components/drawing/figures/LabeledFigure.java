/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.components.drawing.figures;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.DragHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.opentcs.guing.base.components.properties.event.AttributesChangeListener;
import org.opentcs.guing.common.components.drawing.course.OriginChangeListener;

/**
 * A figure that is labeled by another figure.
 */
public abstract class LabeledFigure
    extends GraphicalCompositeFigure
    implements AttributesChangeListener,
               OriginChangeListener {

  /**
   * The figure of the label of this labeled figure.
   */
  private TCSLabelFigure fLabel;

  /**
   * Creates a new instance.
   */
  public LabeledFigure() {
  }

  public void setLabel(TCSLabelFigure label) {
    add(0, label);  // Allow only one label for each figure
    addFigureListener(label);
    label.setParent(this);
    fLabel = label;
  }

  public TCSLabelFigure getLabel() {
    return fLabel;
  }

  /**
   * Sets the visibility of the label.
   *
   * @param visible Indicates whether the label should be visible or not.
   */
  public void setLabelVisible(boolean visible) {
    fLabel.setLabelVisible(visible);
  }

  public abstract Shape getShape();

  @Override
  public TCSFigure getPresentationFigure() {
    return (TCSFigure) super.getPresentationFigure();
  }

  @Override
  public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    boolean ret = getPresentationFigure().handleMouseClick(p, evt, view);

    return ret;
  }

  @Override
  public void changed() {
    super.changed();
    updateModel();
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    Collection<Handle> handles = new ArrayList<>();

    switch (detailLevel) {
      case -1: // Mouse Moved
        handles.add(new BoundsOutlineHandle(getPresentationFigure(), false, true));
        break;

      case 0: // Mouse clicked
        MoveHandle.addMoveHandles(this, handles);
        for (Figure child : getChildren()) {
          MoveHandle.addMoveHandles(child, handles);
          handles.add(new DragHandle(child));
        }

        break;

      case 1: // Double-Click
        ResizeHandleKit.addResizeHandles(this, handles);
        break;

      default:
        break;
    }

    return handles;
  }

  @Override
  public <T> void set(AttributeKey<T> key, T newValue) {
    super.set(key, newValue);

    if (fLabel != null) {
      fLabel.set(key, newValue);
    }
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    basicSetPresentationFigureBounds(anchor, anchor);

    if (fLabel != null) {
      Point2D.Double p = getStartPoint();
      p.x += fLabel.getOffset().x;
      p.y += fLabel.getOffset().y;
      fLabel.setBounds(p, p);
    }
  }

  @Override
  public void originLocationChanged(EventObject event) {
    updateModel();
  }

  @Override
  public void originScaleChanged(EventObject event) {
    scaleModel(event);
  }

  public abstract void updateModel();

  /**
   * Scales the model coodinates accodring to changes to the layout scale.
   *
   * @param event The event containing the layout scale change.
   */
  public abstract void scaleModel(EventObject event);

  @Override
  public LabeledFigure clone() {
    LabeledFigure clone = (LabeledFigure) super.clone();
    clone.fLabel = null;
    return clone;
  }
}
