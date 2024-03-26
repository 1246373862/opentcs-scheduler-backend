/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.common.components.drawing.figures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import org.jhotdraw.draw.AbstractAttributedFigure;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.Geom;
import org.opentcs.guing.common.components.drawing.ZoomPoint;
import org.opentcs.guing.common.components.drawing.course.Origin;

/**
 * A Figure for the coordinate system's origin.
 */
public class OriginFigure
    extends AbstractAttributedFigure {

  /**
   * The enclosing rectangle.
   */
  protected final Rectangle fDisplayBox;
  /**
   * The width and height.
   */
  private final int fSideLength;
  /**
   * The origin's model.
   */
  private Origin fModel;
  /**
   * The exact position of the figure.
   */
  private final ZoomPoint fZoomPoint;

  /**
   * Creates a new instance.
   */
  public OriginFigure() {
    super();
    fSideLength = 20;
    fZoomPoint = new ZoomPoint(0.0, 0.0);
    fDisplayBox = new Rectangle(-fSideLength / 2, -fSideLength / 2,
                                fSideLength, fSideLength);
    set(AttributeKeys.STROKE_COLOR, Color.blue);
    setSelectable(false);
  }

  /**
   * Set's the origin's model.
   *
   * @param model The model.
   */
  public void setModel(Origin model) {
    fModel = model;
    getModel().setPosition(Geom.center(fDisplayBox));
  }

  /**
   * Returns the origin's model.
   *
   * @return The model.
   */
  public Origin getModel() {
    return fModel;
  }

  /**
   * Returns the exact position of the origin.
   *
   * @return The exact position of the origin.
   */
  public ZoomPoint getZoomPoint() {
    return fZoomPoint;
  }

  @Override
  public Rectangle2D.Double getBounds() {
    Rectangle2D.Double r2d = new Rectangle2D.Double();
    r2d.setRect(fDisplayBox.getBounds2D());

    return r2d;
  }

  @Override
  public boolean contains(Point2D.Double p) {
    Rectangle r = (Rectangle) fDisplayBox.clone();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this);
    r.x -= grow;
    r.y -= grow;
    r.width += grow * 2;
    r.height += grow * 2;

    return r.contains(p);
  }

  @Override
  public Object getTransformRestoreData() {
    return fDisplayBox.clone();
  }

  @Override
  public void restoreTransformTo(Object restoreData) {
    Rectangle r = (Rectangle) restoreData;
    fDisplayBox.x = r.x;
    fDisplayBox.y = r.y;
    fDisplayBox.width = r.width;
    fDisplayBox.height = r.height;
    fZoomPoint.setX(r.x + 0.5 * r.width);
    fZoomPoint.setY(r.y + 0.5 * r.height);
  }

  @Override
  public void transform(AffineTransform tx) {
    Point2D center = getZoomPoint().getPixelLocationExactly();
    Point2D lead = new Point2D.Double();  // not used
    setBounds(
        (Point2D.Double) tx.transform(center, center),
        (Point2D.Double) tx.transform(lead, lead));
  }

  @Override
  public void changed() {
    super.changed();
    getModel().setPosition(Geom.center(fDisplayBox));
    getModel().notifyLocationChanged();
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    // Only change the position here, NOT the size!
    // Draw the center of the figure at the mouse cursor's position.
    fZoomPoint.setX(anchor.x);
    fZoomPoint.setY(anchor.y);
    fDisplayBox.x = (int) (anchor.x - 0.5 * fSideLength);
    fDisplayBox.y = (int) (anchor.y - 0.5 * fSideLength);
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    // No handles for the origin figure.
    return new ArrayList<>();
  }

  @Override
  protected void drawFill(Graphics2D g) {
    // No filling for the origin's figure.
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    // Outline: "Crosshair" with circle
    Rectangle r = (Rectangle) fDisplayBox.clone();

    if (r.width > 0 && r.height > 0) {
      g.drawLine(r.x + r.width / 2, r.y, r.x + r.width / 2, r.y + r.height);
      g.drawLine(r.x, r.y + r.height / 2, r.x + r.width, r.y + r.height / 2);
      r.grow(-4, -4);
      g.drawOval(r.x, r.y, r.width, r.height);
    }
  }

  @Override
  public int getLayer() {
    return FigureOrdinals.ORIGIN_FIGURE_ORDINAL;
  }
}
