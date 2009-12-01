/*
 * MouseHandler.java
 * Created on April 22, 2007, 12:53 AM
 */
package behaviorism.handlers;

import behaviorism.geometry.Geom;
import behaviorism.renderers.cameras.Cam;
import behaviorism.utils.MatrixUtils;
import behaviorism.utils.RenderUtils;
import behaviorism.utils.Utils;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import javax.vecmath.Point3f;
import java.awt.geom.GeneralPath;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.vecmath.Point3d;

/** 
 * MouseHandler is a wrapper for the various MouseListeners.
 * It is able to pick objects from the openGL scene and to send messages
 * to those objects as necessary. If no objects are picked, then mouse
 * movements are sent to the currently attached camera for processing.
 *
 * Types of events, in order of priority
 * 1) Click events:
 *  MousePressed : the mouse is being pressed over a geom
 *  MouseReleased : the mouse is no longer being pressed over a geom
 *  MouseClicked : the mouse has been pressed over a geom
 *  MouseDoubleClicked : the mouse has been pressed twice (or more) over a geom
 * 2) Drag events:
 *  MouseDragged : the mouse is being pressed over a geom AND the mouse is moving
 * 3) Hover events:
 *  MouseIn - the mouse is over a geom for the first time
 *  MouseOver - the mouse is over a geom the second time to the last time
 *  MouseOut - the mouse is no longer over a geom
 * 4) Move events:
 *  MouseStartedMoving : the mouse has started moving over a geom
 *  MouseMoving : the mouse is continuing to move over a geom
 *  MouseStoppedMoving: the mouse has stopped moving over a geom
 *
 * If drag events are detected, then move events will be ignored.
 * 
 * @author angus
 */
public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener
{

  public Point mousePixel = new Point();
  public Point3f mouseWorldPoint = new Point3f();
  public Point3f mouseGeomPoint = new Point3f();
  public Point3f debugMouseClickPoint = new Point3f(0f, 0f, 0f);
  public Point3f debugMouseMovePoint = new Point3f(0f, 0f, 0f);
  public Point3d dragOffsetPt = new Point3d(0, 0, 0);
  public AtomicBoolean isProcessing = new AtomicBoolean(false);
  public boolean isMoving = false;
  public boolean wasMoving = false;
  public boolean isDragging = false;
  public boolean isPressing = false;
  public boolean isClicking = false;
  public boolean isReleasing = false;
  private int mx = 0;
  private int my = 0;
  private int prev_mx = 0;
  private int prev_my = 0;
  public int button = 0;
  public int clicks = 0;
  public Geom mouseOverGeom = null;
  public Geom selectedGeom = null;
  public Geom prevSelectedGeom = null;
  public Geom hoverGeom = null;
  public Geom prevMouseOverGeom = null;
  public long lastTimeMoved = 0;
  private static final MouseHandler instance = new MouseHandler();

  /**
   * Gets (or creates then gets) the singleton MouseHandler object.
   * @return the singleton MouseHandler
   */
  public static MouseHandler getInstance()
  {
    return instance;
  }

  private MouseHandler()
  {
  }

  /**
   * processMouse is called from withing the openGL display loop.
   * It handles the selection, movement, and other interactions
   * of the mouse to objects in the current world including the camera.
   */
  public void processMouse()
  {
    if (isProcessing.get() == true)
    {
      return;
    }

    //need to get this each time because it could change even if the mouse doesn't move (if geometry changes)
    getMouseOverGeom();

    //mouse clicks
    if (isReleasing == true)
    {
      processMouseReleasing();
      isPressing = false;
      isDragging = false;
      isReleasing = false;
    }
    else if (isClicking == true)
    {
      processMouseClicking();
      isClicking = false;
    }

    //mouse moves or stops moving
    if (isMoving == true)
    {
      isMoving = false;
      if (wasMoving == false)
      {
        processMouseStartedMoving();
        wasMoving = (true);
      }
      else
      {
        processMouseMoving();
      }
    }
    else if (wasMoving == true && Utils.nanosToMillis(Utils.now() - lastTimeMoved) > 100L)
    {
      wasMoving = false;
      processMouseStoppedMoving();
    }

    //mouse drag
    if (isDragging == true)
    {
      processMouseDragging();
      isPressing = false;
      isDragging = false;
    }
    else //if dragging then don't process mouse presses or hovers
    {
      if (isPressing == true)
      {
        processMousePressing();
      }

      processMouseOver();
    }

    isProcessing.set(false);
  }

  public void processMouseOver()
  {
    //System.err.println("in processMouseOver()");

    if (hoverGeom != prevMouseOverGeom)
    {
      if (prevMouseOverGeom != null)
      {
        prevMouseOverGeom.handleMouseOut();
      }
      if (hoverGeom != null)
      {
        hoverGeom.handleMouseIn();
      }
      prevMouseOverGeom = hoverGeom;
    }
    else if (hoverGeom == prevMouseOverGeom)
    {
      if (hoverGeom != null)
      {
        hoverGeom.handleMouseOver();
      }
    }
  }

  private void processMouseMoving()
  {
    //System.err.println("in processMouseMoving : hoverGeom = " + hoverGeom);
    if (hoverGeom != null)
    {
      hoverGeom.handleMouseMoving();
    }
  }

  private void processMouseStartedMoving()
  {
    //System.err.println("in processMouseStartedMoving");
    if (hoverGeom != null)
    {
      //System.out.println("mog = " + mog);
      hoverGeom.handleMouseStartedMoving();
    }
  }

  private void processMouseStoppedMoving()
  {
    //System.err.println("in processMouseStoppedMoving\n\n");
    if (hoverGeom != null)
    {
      hoverGeom.handleMouseStoppedMoving();
    }
  }

  private void processMousePressing()
  {
    getSelectedGeom();

    if (selectedGeom != null)
    {
      determineOffsetPointForDragging(mouseWorldPoint);
      selectedGeom.handlePress();
    }
  }

  private void processMouseClicking()
  {
    getSelectedGeom();


    if (selectedGeom != null && clicks > 1)
    {
      selectedGeom.handleDoubleClick();
    }
    else if (selectedGeom != null && clicks != 0)
    {
      selectedGeom.handleClick();
    }
    else //see if the World needs a click...
    {
      RenderUtils.getWorld().handleClick();
    }

    //tripleClick?


  }

  private void processMouseDragging()
  {
    if (selectedGeom == null) //drag the camera
    {
      //dragCamera();
      RenderUtils.getWorld().handleDrag();
    }
    else if (selectedGeom != null && selectedGeom.draggableObject != null) //drag a Geom
    {
      selectedGeom.handleDrag();
    }
  }

  private void processMouseReleasing()
  {
    if (selectedGeom != null)
    {
      selectedGeom.handleRelease();
    }
  }

  private Point3f worldPtToGeomPt(Point3f pt, Geom geom)
  {
    return MatrixUtils.toPoint3f(
      MatrixUtils.getWorldPointInGeomCoordinates(
      MatrixUtils.toPoint3d(pt), RenderUtils.getCamera().modelview, geom.modelview));
  }

  private void getMouseOverGeom()
  {
    mouseWorldPoint.set(RenderUtils.getWorldCoordsForScreenCoord(mx, my));

    mouseOverGeom = selectPossibleGeom(RenderUtils.getWorld().geoms);

    if (mouseOverGeom != null)
    {
      mouseGeomPoint = worldPtToGeomPt(mouseWorldPoint, mouseOverGeom);
      hoverGeom = mouseOverGeom.hoverableObject;
    }

    //System.err.println("mouseOverGeom = " + mouseOverGeom);
  }

  private void getSelectedGeom()
  {
    if (mouseOverGeom != null)
    {
      selectedGeom = mouseOverGeom.selectableObject;
    }
    else
    {
      selectedGeom = null;
    }

    if (prevSelectedGeom != selectedGeom)
    {
      if (selectedGeom != null)
      {
        selectedGeom.handleSelected();
      }

      if (prevSelectedGeom != null)
      {
        prevSelectedGeom.handleUnselected();
      }

      prevSelectedGeom = selectedGeom;
    }

    //System.err.println("selectedGeom = " + selectedGeom);
  }

  /**
   * Default camera dragging.
   */
  public void dragCamera()
  {
    Cam cam = RenderUtils.getCamera();
    int xDif = mx - prev_mx;
    int yDif = my - prev_my;

    if (button == 1)
    {
      //System.out.println("drag camera 1");
      cam.translateX(xDif * 0.01f);
      cam.translateY(yDif * -0.01f);
    }
    else if (button == 2)
    {
      //System.out.println("drag camera 2");
      cam.translateZ(yDif * 0.05f);
    }
    else if (button == 3)
    {
      //System.out.println("drag camera 3");
      cam.changeHeading(xDif * 0.5);
      cam.changePitch(yDif * 0.5);
    }
  }

  /**
   * Default Geom dragging.
   */
  public void dragGeom() //gets called as the default behavior of Geom.dragAction()
  {
    if (button == 1)
    {
      //System.out.println("HERE - button 1 drag...");
      selectedGeom.draggableObject.setTranslate(new Point3f(RenderUtils.rayIntersect(selectedGeom.draggableObject, mx, my, dragOffsetPt)));
    }
    else if (button == 2)
    {
      //System.out.println("HERE - button 2 drag...");
      int yDif = my - prev_my;
      selectedGeom.draggableObject.scaleX(yDif * .02f);
      selectedGeom.draggableObject.scaleY(yDif * .02f);
    }
    else if (button == 3)
    {
      //System.out.println("HERE - button 3 drag...");
      int xDif = mx - prev_mx;
      int yDif = my - prev_my;
      selectedGeom.draggableObject.rotateX(yDif);
      selectedGeom.draggableObject.rotateY(xDif);
    }
  }

  private void determineOffsetPointForDragging(Point3f ptWorld)
  {
    if (selectedGeom.draggableObject != null)
    {
      dragOffsetPt = MatrixUtils.getWorldPointInGeomCoordinates(
        MatrixUtils.toPoint3d(ptWorld), RenderUtils.getCamera().modelview, selectedGeom.draggableObject.parent.modelview);

      dragOffsetPt = new Point3d(dragOffsetPt.x - selectedGeom.draggableObject.translate.x,
        dragOffsetPt.y - selectedGeom.draggableObject.translate.y,
        dragOffsetPt.z - selectedGeom.draggableObject.translate.z);
    }
  }

  /**
   * pickGeom determines which Geom (if any) was chosen by a particular
   * mouse selection. That is, given a point (in screen coordinates) and a 
   * list of Geoms, it determines the Geom that the point matches.
   * Additionally, it adjusts the z-order of the chosen Geom so that
   * it is completely viewable (by calling WorldGeom.adjustZOrder).
   * 
   * @param geoms
   * @param ptPixel
   */
  private void pickGeom(List<Geom> geoms)
  {
    selectedGeom = selectPossibleGeom(geoms);
    if (selectedGeom == null)
    {
      isDragging = false;
      return;
    }
    else
    {
      selectedGeom = selectedGeom.selectableObject;
    }
    //OKAY what we reallly want to do
    //is get every geom in ***Camera Coordinates***
    //then we know the depth value in those terms
    //now we get a list of  -- hmm I have to think about this more when the camera is actually working!
        /*
    //get list of all possible Geoms that might conceivably be selected
    List<Geom> possibleGeoms = new ArrayList<Geom>();
    selectPossibleGeoms(geoms, possibleGeoms, ptPixel); //recursive function to visit all active geoms

    //if none, then we are deselecting, return
    if (possibleGeoms.size() == 0)
    {
    selectedGeom = null;
    isDragging = false;
    return;
    }


    //else get a sublist containing all Geoms with the closest z-value and in case there is more than one, choose
    //the last one that was rendered (which will also be the last one in the sub-list).
    //List<Geom>
     */

    //this was commented out before, so it might cause problems
    //but in principle it is the right thing to do!

    //Behaviorism.renderer.currentWorld.adjustZOrder(selectedGeom);
  }

  /**
   * selectPossibleGeoms loops recursively through a list of Geoms
   * *and all of its childen* to check to see if a 
   * particular point (in screen coordinates)
   * is within its bounds. If more than one Geom contain the point, 
   * it will only return the one that is the furthest level down
   * the scene graph hierarchy (todo: is this actually what we want???)
   * 
   * TODO: look in to using the selectPossibleGeoms version 
   * which returns multiple Geoms, and then decide...
   * 
   * @param geoms
   * @return
   */
  private Geom selectPossibleGeom(List<Geom> geoms)
  {
    Geom returnGeom = null;
    //synchronized (geoms)
    {

      for (Geom g : geoms)
      {
        if (g.isActive == true && g.isSelectable == true && g.isVisible == true)        //if not active or selectable or visible, 
        //then can't be selected-- but still need to check its children...
        {
          //g.setColor(1f, 0f, 0f, 1f);
          GeneralPath s = RenderUtils.getScreenShapeForWorldCoords(g);
          if (s == null)
          {
            System.err.println("GeneralPath for " + g + " is NULL!");
            continue;
          }
          if (s.contains(mousePixel))
          {
            returnGeom = g;
          }
        }

        Geom checkGeom = selectPossibleGeom(g.geoms);
        if (checkGeom != null)
        {
          returnGeom = checkGeom;
        }
      }
    }
    return returnGeom;
  }

  /*
  private void selectPossibleGeoms(
  List<Geom> geoms, List<Geom> possibleGeoms, Point2D pt)
  {
  for (Geom g : geoms)
  {
  if (g.isActive == true && g.isSelectable == true) //if not active or selectable, then can't be selected-- but still need to check its children...
  {
  //g.setColor(1f, 0f, 0f, 1f);

  GeneralPath s = RenderUtils.getScreenShapeForWorldCoords(g);
  if (s == null)
  {
  continue;
  }

  //System.out.println("s = " + GeomUtils.printPath(s));
  //System.out.println("pt = " + pt);

  if (s.contains(pt))
  {
  //System.out.println("CONTAINS!");
  g.area = GeomUtils.area(s); //get screen area of possibly selected geom
  possibleGeoms.add(g);
  }
  }

  //selectPossibleGeoms(g.geoms, possibleGeoms, pt, ptWorld);
  selectPossibleGeoms(g.geoms, possibleGeoms, pt);
  }
  }
   */
  /****
  mouseAdapter methods : executed on Java Thread (not openGL thread) ***
   ****/
  public void mouseClicked(MouseEvent e)
  {
  }

  public void mousePressed(MouseEvent e)
  {
    //System.err.println("in MouseHandler : mousePresssed()");
    debugMouseClickPoint.x = e.getX();
    debugMouseClickPoint.y = e.getY();

    mx = e.getX();
    my = e.getY();

    mousePixel.setLocation(mx, my);

    prev_mx = mx;
    prev_my = my;

    button = e.getButton();
    clicks = e.getClickCount();

    isClicking = true;
    isPressing = true;
  }

  
  public void mouseDragged(MouseEvent e)
  {
    debugMouseClickPoint.x = e.getX();
    debugMouseClickPoint.y = e.getY();

    prev_mx = mx;
    prev_my = my;

    mx = e.getX();
    my = e.getY();

    mousePixel.setLocation(mx, my);

    isDragging = true;
  }
  
  public void mouseReleased(MouseEvent e)
  {
    isReleasing = true;
  }






  public void mouseEntered(MouseEvent e)
  {

  }
  public void mouseExited(MouseEvent e)
  {

  }

  public void mouseMoved(MouseEvent e)
  {
    debugMouseMovePoint.x = e.getX();
    debugMouseMovePoint.y = e.getY();

    mx = e.getX();
    my = e.getY();

    mousePixel.setLocation(mx, my);

    lastTimeMoved = Utils.now();
    isMoving = true;
  }

  public void mouseWheelMoved(MouseWheelEvent e)
  {
    //System.out.println("mouseWheelMoved...");
    int notches = e.getWheelRotation();

    if (notches < 0) //moved up 
    {
      //System.out.println("up");
      RenderUtils.getCamera().translateZ(notches * 0.05f);
    }
    else //moved down
    {
      //System.out.println("down");
      RenderUtils.getCamera().translateZ(notches * 0.05f);
    }
  }
  
}

