/*
 * MouseHandler.java
 * Created on April 22, 2007, 12:53 AM
 */
package handlers;

import geometry.Geom;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import javax.vecmath.Point3f;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.vecmath.Point3d;
import renderers.cameras.Cam;
import utils.GeomUtils;
import utils.MatrixUtils;
import utils.RenderUtils;
import utils.Utils;

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
public class MouseHandler extends MouseAdapter
{

  public Point mousePixel = new Point();
  public Point3f mouseWorld = new Point3f();
  public Point3f mouseGeom = new Point3f();
  public Point3f debugWorldPoint = new Point3f(0f, 0f, 0f);
  public Point3f debugSelectPoint = new Point3f(0f, 0f, 0f);
  public Point3f debugMousePoint = new Point3f(0f, 0f, 0f);
  public Point3f zeroPt = new Point3f(0f, 0f, 0f);
  public Point3d offsetPt = new Point3d(0, 0, 0);
  public AtomicBoolean isMoving = new AtomicBoolean(false);
  public AtomicBoolean wasMoving = new AtomicBoolean(false);
  public boolean isDragging = false;
  public boolean isPressing = false;
  public boolean isClicking = false;
  public AtomicBoolean isProcessing = new AtomicBoolean(false);
  public boolean isReleasing = false;
  public int pre_abs_mx = 0;
  public int pre_abs_my = 0;
  public int abs_mx = 0;
  public int abs_my = 0;
  public int mx = 0;
  public int my = 0;
  public int button = 0;
  public int clicks = 0;
  public int pre_mx = 0;
  public int pre_my = 0;
  public Geom selectedGeom = null;
  public Geom mouseOverGeom = null;
  public long lastTimeMoved = 0;
  private static MouseHandler instance = null;

  /**
   * Gets (or creates then gets) the singleton MouseHandler object.
   * @return the singleton MouseHandler
   */
  public static MouseHandler getInstance()
  {
    if (instance != null)
    {
      return instance;
    }

    instance = new MouseHandler();

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

    //System.out.println( "isPressing: " + isPressing + " isProcessing: " + isProcessing );

    if (isReleasing == true)
    {
      processMouseReleasing();
      isPressing = false;
      isDragging = false;
      //isMoving = false;
      //selectedGeom = null;
      isReleasing = false;
    }
    else if (isClicking == true)
    {
      processMouseClicking();
    }
    else if (isMoving.get() == true)
    {
      isMoving.set(false);
      if (wasMoving.get() == false)
      {
        processMouseStartedMoving();
        wasMoving.set(true);
      }
      else
      {
        processMouseMoving();
      }
    }
    else if (wasMoving.get() == true && Utils.nanosToMillis(Utils.now() - lastTimeMoved) > 100L)
    {
      wasMoving.set(false);
      processMouseStoppedMoving();
    }
    else if (isDragging == true)
    {
      isPressing = false;
      processMouseDragging();
      isDragging = false;
    }
    else if (isPressing == true) // && isProcessing == false)
    {
      processMousePressing();
    }
    else //check mouse overs
    {
      processMouseOver();
    }


    isClicking = false;

    //always do this at end
    isProcessing.set(false);
  }
  public Geom prevMouseOverGeom = null;

  public void processMouseOver()
  {
    //System.out.println("in processMouseOver()");
    Geom mog = getMouseOverGeom();

    if (mog != prevMouseOverGeom)
    {
      if (prevMouseOverGeom != null)
      {
        prevMouseOverGeom.handleMouseOut();
      }
      if (mog != null)
      {
        mog.handleMouseIn();
      }
      prevMouseOverGeom = mog;
    }
    else if (mog == prevMouseOverGeom)
    {
      if (mog != null)
      {
        mog.handleMouseOver();
      }
    }
  }

  private Geom getMouseOverGeom()
  {
    //double coords[] = RenderUtils.getWorldCoordsForScreenCoord(mx, my);
    //Point3d ptWorld = new Point3d(coords[0], coords[1], coords[2]);
    Point3f ptWorld = RenderUtils.getWorldCoordsForScreenCoord(mx, my);

    debugWorldPoint = ptWorld;
    mouseWorld.set(ptWorld);

    Point2D.Float ptPixel = new Point2D.Float((float) mx, (float) my);

    Geom testMouseOverGeom = selectPossibleGeom(RenderUtils.getWorld().geoms, ptPixel);
    if (testMouseOverGeom != null)
    {
      mouseOverGeom = testMouseOverGeom.hoverableObject;

      //System.out.println("mouse over : " + mouseOverGeom);
      mouseGeom = worldPtToSelctedGeomPt(mouseWorld, mouseOverGeom);

      return mouseOverGeom;
    }

    return null;

  }

  private void processMouseMoving()
  {
    //System.out.println("in processMouseMoving");
    Geom mog = getMouseOverGeom();
    if (mog != null)
    {
      mouseOverGeom.handleMouseMoving();
    }
  }

  private void processMouseStartedMoving()
  {
    //System.out.println("in processMouseStartedMoving");
    Geom mog = getMouseOverGeom();
    if (mog != null)
    {
      //System.out.println("mog = " + mog);
      mouseOverGeom.handleMouseStartedMoving();
    }
  }

  private void processMouseStoppedMoving()
  {
    //System.out.println("in processMouseStoppedMoving\n\n");
    Geom mog = getMouseOverGeom();
    if (mog != null)
    {
      //System.out.println("mog = " + mog);
      mouseOverGeom.handleMouseStoppedMoving();
    }
  }

  private /*static*/ Point3f worldPtToSelctedGeomPt(Point3f mouseWorld, Geom selectedGeom)
  {
    return MatrixUtils.toPoint3f(
      MatrixUtils.getWorldPointInGeomCoordinates(
      MatrixUtils.toPoint3d(mouseWorld), RenderUtils.getCamera().modelview, selectedGeom.modelview));
  }

  private void getSelectedGeom()
  {
//    double coords[] = RenderUtils.getWorldCoordsForScreenCoord(mx, my);
//    Point3d ptWorld = new Point3d(coords[0], coords[1], coords[2]);
//    System.out.printf("ptWorld = %f/%f/%f\n", ptWorld.x, ptWorld.y, ptWorld.z);
//
    Point3f ptWorld = RenderUtils.getWorldCoordsForScreenCoord(mx, my);

    //System.out.printf("ptWorld2 = %f/%f/%f\n", coords2[0],  coords2[1],  coords2[2]);
    debugWorldPoint = ptWorld;
    mouseWorld.set(ptWorld);

    Point2D.Float ptPixel = new Point2D.Float((float) mx, (float) my);

    pickGeom(RenderUtils.getWorld().geoms, ptPixel);

    if (selectedGeom != null)
    {
      mouseGeom = worldPtToSelctedGeomPt(mouseWorld, selectedGeom.clickableObject);
      //hmm, or selectableObject??


      ///System.out.println("you picked " + selectedGeom);

      Point3d p3d_a = MatrixUtils.getGeomPointInAbsoluteCoordinates(
        new Point3d(0, 0, 0), selectedGeom.modelview);
      Point3d p3d_b = MatrixUtils.getGeomPointInAbsoluteCoordinates(
        new Point3d(selectedGeom.w, selectedGeom.h, 0), selectedGeom.modelview);

//      System.out.println("in abs coords it's anchor is " + MatrixUtils.toString(p3d_a) +
//      " and other corner is " + MatrixUtils.toString(p3d_b));

      determineOffsetPointForDragging(ptWorld);

      //this is done here so that handleClick can
      //be called as soon as the correct selectGeom is chosen
    }
  }

  private void processMousePressing()
  {
    getSelectedGeom();

    if (selectedGeom != null)
    {
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
    else if (selectedGeom != null)
    {
      System.out.println("PRESSING");
    }

    //tripleClick?


  }

  public /*static*/ void processMouseDragging()
  {
    if (selectedGeom == null) //drag the camera
    {
      dragCamera();
    }
    else if (selectedGeom != null && selectedGeom.draggableObject != null) //drag a Geom
    {
      //Hmm this is default behavior... to move the object along with the mouse
      dragGeom();

      //but also can be customized...
      /** temp **/
//      double coords[] = RenderUtils.getWorldCoordsForScreenCoord(mx, my);
//      Point3d ptWorld = new Point3d(coords[0], coords[1], coords[2]);
      Point3f ptWorld = RenderUtils.getWorldCoordsForScreenCoord(mx, my);

      //System.out.println("ptWorld = " + ptWorld);
      debugWorldPoint = ptWorld;
      mouseWorld.set(ptWorld);

      if (selectedGeom != null)
      {
        mouseGeom = worldPtToSelctedGeomPt(mouseWorld, selectedGeom.clickableObject);
      }
      /** end temp **/
      selectedGeom.handleDrag();
    }
  }

  public /*static*/ void processMouseReleasing()
  {
    if (selectedGeom != null)
    {
      selectedGeom.handleRelease();
    }
  }

  private /*static*/ void dragCamera()
  {
    Cam cam = RenderUtils.getCamera();
    int xDif = mx - pre_mx;
    int yDif = my - pre_my;

    if (button == 1)
    {
      System.out.println("drag camera 1");
      cam.translateX(xDif * 0.01f);
      cam.translateY(yDif * -0.01f);
    }
    else if (button == 2)
    {
      System.out.println("drag camera 2");
      cam.translateZ(yDif * 0.05f);
    }
    else if (button == 3)
    {
      System.out.println("drag camera 3");
      cam.changeHeading(xDif * 0.5);
      cam.changePitch(yDif * 0.5);
    }
  }

  public /*static*/ void dragGeom()
  {
    if (button == 1)
    {
      //System.out.println("HERE - button 1 drag...");
      selectedGeom.draggableObject.setTranslate(new Point3f(RenderUtils.rayIntersect(selectedGeom.draggableObject, mx, my, offsetPt)));
    }
    else if (button == 2)
    {
      //System.out.println("HERE - button 2 drag...");
      int yDif = my - pre_my;
      selectedGeom.draggableObject.scaleX(yDif * .02f);
      selectedGeom.draggableObject.scaleY(yDif * .02f);
    }
    else if (button == 3)
    {
      //System.out.println("HERE - button 3 drag...");
      int xDif = mx - pre_mx;
      int yDif = my - pre_my;
      selectedGeom.draggableObject.rotateX(yDif);
      selectedGeom.draggableObject.rotateY(xDif);
    }
  }

  private /*static*/ void determineOffsetPointForDragging(Point3f ptWorld)
  {
    if (selectedGeom.draggableObject != null)
    {
      if (selectedGeom.draggableObject.parent == null)
      {
        offsetPt = MatrixUtils.toPoint3d(ptWorld);
      }
      else
      {
        //should use getWorldPointInGeomCoord???
        //offsetPt = MatrixUtils.getAbsolutePointInGeomCoordinates(ptWorld, selectedGeom.draggableObject.parent.modelview);
        offsetPt = MatrixUtils.getWorldPointInGeomCoordinates(
          MatrixUtils.toPoint3d(ptWorld), RenderUtils.getCamera().modelview, selectedGeom.draggableObject.parent.modelview);
      }

      offsetPt = new Point3d(offsetPt.x - selectedGeom.draggableObject.translate.x,
        offsetPt.y - selectedGeom.draggableObject.translate.y,
        offsetPt.z - selectedGeom.draggableObject.translate.z);
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
  private /*static*/ void pickGeom(List<Geom> geoms, Point2D.Float ptPixel)
  {
    selectedGeom = selectPossibleGeom(geoms, ptPixel);
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
   * @param pt
   * @return 
   */
  private /*static*/ Geom selectPossibleGeom(List<Geom> geoms, Point2D pt)
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
          Path2D s = RenderUtils.getScreenShapeForWorldCoords(g);
          if (s == null)
          {
            continue;
          }
          if (s.contains(pt))
          {
            returnGeom = g;
          }
        }

        Geom checkGeom = selectPossibleGeom(g.geoms, pt);
        if (checkGeom != null)
        {
          returnGeom = checkGeom;
        }
      }
    }
    return returnGeom;
  }

  private /*static*/ void selectPossibleGeoms(
    List<Geom> geoms, List<Geom> possibleGeoms, Point2D pt)
  {
    for (Geom g : geoms)
    {
      if (g.isActive == true && g.isSelectable == true) //if not active or selectable, then can't be selected-- but still need to check its children...
      {
        //g.setColor(1f, 0f, 0f, 1f);

        Path2D.Float s = RenderUtils.getScreenShapeForWorldCoords(g);
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

  /****
  mouseAdapter methods : executed on Java Thread (not openGL thread) ***
   ****/
  @Override
  public void mouseClicked(MouseEvent e)
  {
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    //System.out.println("mouse pressed! selectedGeom = " + selectedGeom);
    mx = e.getX();
    my = e.getY();

    mousePixel.setLocation(mx, my);

    pre_mx = mx;
    pre_my = my;

    button = e.getButton();
    clicks = e.getClickCount();

    isClicking = true;
    isPressing = true;

    debugSelectPoint.x = e.getX();
    debugSelectPoint.y = e.getY();
  }

  @Override
  public void mouseDragged(MouseEvent e)
  {
    pre_mx = mx;
    pre_my = my;

    mx = e.getX();
    my = e.getY();

    mousePixel.setLocation(mx, my);

    isDragging = true;

    debugSelectPoint.x = e.getX();
    debugSelectPoint.y = e.getY();
  }

  @Override
  public void mouseReleased(MouseEvent e)
  {

    isReleasing = true;

  }

  @Override
  public void mouseMoved(MouseEvent e)
  {
    debugMousePoint.x = e.getX();
    debugMousePoint.y = e.getY();
    mx = e.getX();
    my = e.getY();

    mousePixel.setLocation(mx, my);

    lastTimeMoved = Utils.now();
    isMoving.set(true);


  }

  @Override
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

