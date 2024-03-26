/**
 * (c): IML, JHotDraw.
 */
package org.opentcs.thirdparty.guing.common.jhotdraw.application.toolbar;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import static java.util.Objects.requireNonNull;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.draw.tool.DragTracker;
import org.jhotdraw.draw.tool.SelectAreaTracker;
import org.jhotdraw.draw.tool.Tool;
import org.opentcs.guing.common.application.ApplicationState;

/**
 * The default selection tool.
 */
public abstract class AbstractMultipleSelectionTool
    extends DelegationSelectionTool {

  /**
   * A bit mask for the left mouse button being clicked and the ctrl key being
   * pressed.
   */
  private static final int CTRL_LEFT_MASK
      = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK;
  /**
   * Stores the application's current state.
   */
  private final ApplicationState appState;
  /**
   * We store the last mouse click here, to support multi-click behavior, that
   * is, a behavior that is invoked, when the user clicks on the same spot
   * multiple times, but in a longer interval than needed for a double click.
   */
  private MouseEvent lastClickEvent;
  /**
   * Drawing-related actions for popup menus created by this tool.
   */
  private final Collection<Action> drawingActions;
  /**
   * Selection-related actions for popup menus created by this tool.
   */
  private final Collection<Action> selectionActions;

  /**
   * Creates a new instance.
   *
   * @param appState Stores the application's current state.
   * @param selectAreaTracker The tracker to be used for area selections in the drawing.
   * @param dragTracker The tracker to be used for dragging figures.
   * @param drawingActions Drawing-related actions for the popup menus created by this tool.
   * @param selectionActions Selection-related actions for the popup menus created by this tool.
   */
  public AbstractMultipleSelectionTool(ApplicationState appState,
                                       SelectAreaTracker selectAreaTracker,
                                       DragTracker dragTracker,
                                       Collection<Action> drawingActions,
                                       Collection<Action> selectionActions) {
    super(drawingActions, selectionActions);
    this.appState = requireNonNull(appState, "appState");
    requireNonNull(selectAreaTracker, "selectAreaTracker");
    requireNonNull(dragTracker, "dragTracker");
    this.drawingActions = requireNonNull(drawingActions, "drawingActions");
    this.selectionActions = requireNonNull(selectionActions, "selectionActions");

    setSelectAreaTracker(selectAreaTracker);
    setDragTracker(dragTracker);
  }

  @Override // DelegationSelectionTool
  public void mouseClicked(MouseEvent evt) {
    if (!evt.isConsumed()) {
      if (evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1) {
        handleDoubleClick(evt);
      }
      else if (evt.getButton() == MouseEvent.BUTTON3) {
        // Handle right click as double click
        handleDoubleClick(evt);
      }
      else if (evt.getClickCount() == 1
          && lastClickEvent != null
          && lastClickEvent.getClickCount() == 1
          && lastClickEvent.getX() == evt.getX()
          && lastClickEvent.getY() == evt.getY()) {
        // click with ctrl
        if (((evt.getModifiersEx() & CTRL_LEFT_MASK) > 0)
            && ((lastClickEvent.getModifiersEx() & CTRL_LEFT_MASK) > 0)) {
          handleMultiClick(evt);
        }
      }
    }

    lastClickEvent = evt;
  }

  @Override // DelegationSelectionTool
  protected void handleDoubleClick(MouseEvent evt) {
    DrawingView v = getView();
    Point pos = new Point(evt.getX(), evt.getY());
    Handle handle = v.findHandle(pos);

    // Special case PathConnection: Ignore double click
    if (handle != null && !(handle instanceof BezierOutlineHandle)) {
      handle.trackDoubleClick(pos, evt.getModifiersEx());
    }
    else {
      Point2D.Double p = viewToDrawing(pos);

      // Note: The search sequence used here, must be
      // consistent with the search sequence used by the
      // HandleTracker, the SelectAreaTracker and SelectionTool.
      // If possible, continue to work with the current selection
      Figure figure = null;

      if (isSelectBehindEnabled()) {
        for (Figure f : v.getSelectedFigures()) {
          if (f.contains(p)) {
            figure = f;
            break;
          }
        }
      }
      // If the point is not contained in the current selection,
      // search for a figure in the drawing.
      if (figure == null) {
        figure = v.findFigure(pos);
      }

      Figure outerFigure = figure;

      if (figure != null && figure.isSelectable()) {
        Tool figureTool = figure.getTool(p);

        if (figureTool == null) {
          figure = getDrawing().findFigureInside(p);

          if (figure != null) {
            figureTool = figure.getTool(p);
          }
        }

        if (figureTool != null) {
          setTracker(figureTool);
          figureTool.mousePressed(evt);
        }
        else {
          if (outerFigure.handleMouseClick(p, evt, getView())) {
            v.clearSelection();
            v.addToSelection(outerFigure);
          }
          else {
            v.clearSelection();
            v.addToSelection(outerFigure);
          }
        }
      }
    }

    evt.consume();
  }

  @Override
  protected void showPopupMenu(Figure figure, Point p, Component c) {
    // --- JHotDraw code starts here ---
    JPopupMenu menu = new JPopupMenu();
    JMenu submenu = null;
    String submenuName = null;
    List<Action> popupActions = new ArrayList<>();
    if (figure != null) {
      List<Action> figureActions = new ArrayList<>(figure.getActions(viewToDrawing(p)));
      if (!popupActions.isEmpty() && !figureActions.isEmpty()) {
        popupActions.add(null);
      }
      popupActions.addAll(figureActions);
      if (!popupActions.isEmpty() && !selectionActions.isEmpty()) {
        popupActions.add(null);
      }
      popupActions.addAll(selectionActions);
    }
    if (!popupActions.isEmpty() && !drawingActions.isEmpty()) {
      popupActions.add(null);
    }
    popupActions.addAll(drawingActions);

    HashMap<Object, ButtonGroup> buttonGroups = new HashMap<>();
    for (Action a : popupActions) {
      if (a != null && a.getValue(ActionUtil.SUBMENU_KEY) != null) {
        if (submenuName == null || !submenuName.equals(a.getValue(ActionUtil.SUBMENU_KEY))) {
          submenuName = (String) a.getValue(ActionUtil.SUBMENU_KEY);
          submenu = new JMenu(submenuName);
          menu.add(submenu);
        }
      }
      else {
        submenuName = null;
        submenu = null;
      }
      if (a == null) {
        if (submenu != null) {
          submenu.addSeparator();
        }
        else {
          menu.addSeparator();
        }
      }
      else {
        AbstractButton button;

        if (a.getValue(ActionUtil.BUTTON_GROUP_KEY) != null) {
          ButtonGroup bg = buttonGroups.get(a.getValue(ActionUtil.BUTTON_GROUP_KEY));
          if (bg == null) {
            bg = new ButtonGroup();
            buttonGroups.put(a.getValue(ActionUtil.BUTTON_GROUP_KEY), bg);
          }
          button = new JRadioButtonMenuItem(a);
          bg.add(button);
          button.setSelected(a.getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE);
        }
        else if (a.getValue(ActionUtil.SELECTED_KEY) != null) {
          button = new JCheckBoxMenuItem(a);
          button.setSelected(a.getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE);
        }
        else {
          button = new JMenuItem(a);
        }

        if (submenu != null) {
          submenu.add(button);
        }
        else {
          menu.add(button);
        }
      }
    }
    // --- JHotDraw code ends here ---

    for (JMenuItem action : customPopupMenuItems()) {
      menu.add(action);
    }

    menu.show(c, p.x, p.y);
  }

  public abstract List<JMenuItem> customPopupMenuItems();
}
