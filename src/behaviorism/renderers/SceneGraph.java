package behaviorism.renderers;

import behaviorism.behaviors.Behavior;
import behaviorism.behaviors.behavior.BehaviorUpdater;
import behaviorism.behaviors.geom.GeomUpdater;
import behaviorism.geometry.Geom;
import behaviorism.handlers.FontHandler;
import behaviorism.handlers.MouseHandler;
import behaviorism.renderers.layers.RendererLayer;
import behaviorism.utils.RenderUtils;
import behaviorism.utils.Utils;
import com.sun.opengl.util.awt.TextRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;
import static behaviorism.utils.RenderUtils.*;

public class SceneGraph
{

  public static boolean drawDebugFrameRate = true;
  public static boolean drawDebugGeom = false;
  public static boolean drawDebugGrid = false;
  public static boolean drawDebugMouseDraggedPoint = true;
  public static boolean drawDebugMouseMovedPoint = true;
  public static boolean drawDebugCorners = false;
  public static boolean drawDebugLines = false;
  public static boolean reverse = false;
  public long currentNano = 0L;
  private long lastTime = 0L;
  private long nowTime = 0L;
  private int frames = 0;
  private float offset = 0f;
  private int fps = 0;
  public float step = 0f;
  public long stepSize = 20L;
  public boolean isStepping = false;
  public static float vizOffset = .00001f;
  private List<Geom> invisiblePickingGeoms = new ArrayList<Geom>(); //thinking...
  private static final SceneGraph instance = new SceneGraph();

  /**
   * Gets (or creates then gets) the singleton SceneGraph object.
   * @return the singleton SceneGraph
   */
  public static SceneGraph getInstance()
  {
    return instance;
  }

  private SceneGraph()
  {
  }

  /*
  private void processBehaviors(Geom g)
  {
  for (int i = g.behaviors.size() - 1; i >= 0; i--)
  {
  Behavior b = g.behaviors.get(i);

  processBehavior(b, g);

  if (b.isActive == true && b.isPaused == false)
  {
  // b.updateGeom(g);
  if (b instanceof GeomUpdater)
  {
  ((GeomUpdater) b).updateGeom(g);
  }
  }
  }
  }

  private void processBehavior(Behavior b, Geom g)
  {
  if (b.isDone == true && Behavior.debugBehaviors == false) //don't remove if just stepping...
  {
  g.behaviors.remove(b);
  b.isActive = (false);
  b.dispose();
  return;
  }

  if (reverse == true)
  {
  System.out.println("reverse is true...");
  //b.reverse();
  b.changeSpeed(2f);
  }

  if (isStepping == true)
  {
  if (step != 0)
  {
  b.step(currentNano, Utils.millisToNanos((long) (step * stepSize)));
  }
  else
  {
  System.out.println("pausing... allegedly...");
  b.pause(currentNano);
  }
  }
  else //not globally paused
  {
  if (b.isPaused == true) //locally paused
  {
  b.pause(currentNano);
  }
  else //not globally or locally paused
  {
  b.tick();
  }
  }
  }
   */
  public void processScheduledBehaviors()
  {
    /////testing new scheduler////////////
    // System.out.println(" global behaviors size : " + RenderUtils.getWorld().behaviors2.size() );
    for (Behavior b : RenderUtils.getWorld().behaviors2)
    {
      b.tick();

      b.update(); //generic update

      // System.out.println("attached geoms size : " + b.attachedGeoms);
      for (Geom g : b.attachedGeoms)
      {
        // System.out.println("updating Geom " + g);
        // System.out.println("is g done? " + g.isDone);
        if (g.isDone == true)
        {
          b.attachedGeoms.remove(g);
        }
        else if (b.isActive == true) //g.isActive?
        {
          ((GeomUpdater) b).updateGeom(g);
        }
      }

      //System.out.println("attached behaviors size : " + b.attachedGeoms);
      for (Behavior b2 : b.attachedBehaviors)
      {
        if (b2.isDone == true)
        {
          b2.attachedBehaviors.remove(b2);
        }
        else if (b.isActive == true)
        {
          ((BehaviorUpdater) b).updateBehavior(b2);
        }
      }

      if (b.autoRemove == true && b.isScheduled == true && b.attachedBehaviors.size() == 0 && b.attachedGeoms.size() == 0)
      {
        b.isDone = (true);
      }

      if (b.isDone == true)
      {

        //System.out.println("REMOVING behavior " + b.getClass() + " from scheduler");
        RenderUtils.getWorld().behaviors2.remove(b);
        b.isActive = (false);
        b.dispose();
      }
    }
    /////end testing new scheduler////////////
  }

  /**
   * Draws each element in the scene graph. The steps to render these elements includes the following:
   * 1. Set up the camera; 2. Update any high-level sequences; 3. Determine the modelview matrix of each
   * element in the scene graph; 4. Process each layer of geometry as needed (ie, sort and set state);
   * 5. Render the geometry to the framebuffer.
   * @param gl
   * @param glu
   */
  public void draw()
  {
    this.currentNano = System.nanoTime();
    this.offset = 0f; //reset offset

    processScheduledBehaviors();

    // update camera & set up projectionMatrix & basic modelview using camera
    updateCameraBehavior();

    // execute high=level sequences
    //Sequence.executeSequences(RenderUtils.getWorld().sequences, this.currentNano);
    //getWorld().sequence.execute(currentNano);

    // clear layers -- bleh (think about...), else more complicated when removing a Geom...
    for (Map.Entry<Integer, RendererLayer> entry : getWorld().layers.entrySet())
    {
      RendererLayer layer = entry.getValue();
      layer.attachedGeoms.clear(); //can we do this only if there is an actual change?
    }

    //traverse scene graph to determine each element's transformation matrix
    List<Geom> worldGeom = new ArrayList<Geom>();
    worldGeom.add(getWorld());
    traverseGeoms(worldGeom,
      getWorld().isTransformed || getCamera().isTransformed,
      offset);

//    getWorld().isTransformed = true; //
//    getWorld().transform(); //
//    traverseGeoms(worldGeom, true, offset); // testing... use version above...

    getWorld().isTransformed = false;
    getCamera().isTransformed = false;

    //iterate through layers and render each element to screen.
    drawGeoms();
  }

  private void updateCameraBehavior()
  {
    Geom cam = getCamera();

    if (cam == null)
    {
      return;
    }

    if (getWorld().isPaused != true)
    {
      /*
      for (int i = cam.behaviors.size() - 1; i >= 0; i--)
      {
      Behavior b = cam.behaviors.get(i);

      //        b.updateGeom(cam);

      if (b instanceof GeomUpdater)
      {
      //b.change(cam);
      ((GeomUpdater) b).updateGeom(cam);
      }

      if (b.isDone == true && Behavior.debugBehaviors == false)
      {
      cam.behaviors.remove(b);
      b.dispose();
      b = null;
      }
      }
       */
    }

    getRenderer().setPerspective3D();
  }

  private void traverseGeoms(List<Geom> geoms, boolean parentTransformed, float prevOffset) //, long currentNano, int level, float prevOffset)
  {
    List<Geom> scheduledForRemovalGeoms = new ArrayList<Geom>();

    for (Geom g : geoms)
    {
      //processBehaviors(g);

      offset = prevOffset + vizOffset; //.00001f; //ideal, works good on my nvidia card

      if (parentTransformed == true) //ie, if the parent has changed, then the children need to change too.
      {
        g.isTransformed = true;
      }

      g.transform();
      getLayer(g.layerNum).attachedGeoms.add(g);
      traverseGeoms(g.geoms, g.isTransformed, offset);
      //traverseGeoms(g.geoms, true, offset);

      g.isTransformed = false;

      if (g.isDone == true)
      {
        scheduledForRemovalGeoms.add(g);
      }
    }

    for (Geom g : scheduledForRemovalGeoms)
    {
      g.dispose();
      geoms.remove(g);
      g = null;
    }
  }

  //need to clean this up a bit...
  public void drawGeoms()
  {
    GL2 gl = getGL();
    gl.glMatrixMode(GL_PROJECTION);
    gl.glLoadMatrixd(getCamera().projection, 0);
    gl.glMatrixMode(GL_MODELVIEW);

    invisiblePickingGeoms.clear();

    for (Map.Entry<Integer, RendererLayer> entry : getWorld().layers.entrySet())
    {
      //System.out.println("layer at pos " + entry.getKey() + " has " + entry.getValue().attachedGeoms.size() + " entries.");
      RendererLayer layer = entry.getValue();

      synchronized (layer.attachedGeoms)
      {
        if (layer.isSortable == true)
        {
          layer.sortGeomsInLayer();
        }

        layer.state.state();

        for (Geom g : layer.attachedGeoms)
        {
          if (!g.isActive || !g.isVisible || g.isDone) //then ignore
          {
            continue;
          }

          if (g.hasState == true) //then override the layer's State
          {
            gl.glPushAttrib(GL_ALL_ATTRIB_BITS);
            g.getState().state();
          }

          gl.glLoadMatrixd(g.modelview, 0);
          //think about this... Only if geom isSelectable...
          //if ((layer.state.DEPTH_TEST == false && g.state == null) || (g.state != null && g.state.DEPTH_TEST == false))
          {
            invisiblePickingGeoms.add(g);
          }

          g.draw();

          if (g.hasState == true) //reset to Layer's State if we just overrode it...
          {
            //layer.state.state(gl);
            gl.glPopAttrib();
          }
        }
      }
    }

    //draw invisiblePickingGeoms
    //hmm is it slower to reload a matrix or to change the depth testing?
    if (invisiblePickingGeoms.size() > 0)
    {
      gl.glEnable(GL_DEPTH_TEST);
      gl.glEnable(GL_BLEND);

      for (Geom g : invisiblePickingGeoms)
      {
        gl.glLoadMatrixd(g.modelview, 0);
        g.drawPickingBackground(); //TO DO handle picking backgorunds!
      }
    }

    //gl.glPopMatrix();
    //BehaviorismDriver.renderer.setPerspective3D();

  }

  /*
  public void drawGeom(GL gl, GLU glu, Geom g, float offset)
  {
    //g.transform(gl, glu);

    //g.checkIsVisible();

    if (!g.isActive)
    {
      return;
    }

    if (!g.isVisible)
    {
      //	return;
    }

    
//    if (MouseHandler.getInstance().selectedGeom == g)
//    {
//    System.out.println("you selcted g " + g);
//    State.printCurrentState(gl);
//    }
     

    //g.draw(gl, glu, offset);
    g.setOffset(offset); //that is, only if specified-- TO DO
    g.draw(gl);

    if (drawDebugGeom == true)
    {
      g.drawDebugGeom(gl, glu);
    }

  }
  */

  /*
  //arbitrary axis!
  public void rotateAroundAxis(Coord c1, Coord c2, float theta)
  {
  float rx = (c2.x - c1.x);
  float ry = (c2.y - c1.y);
  float rz = (c2.z - c1.z);
  
  gl.glTranslatef(c1.x, c1.y, c1.z);
  gl.glRotatef(theta, rx, ry, rz);
  gl.glTranslatef(-c1.x, -c1.y, -c1.z);
  }
  
  //arbitrary axis!
  public void rotateAroundAxis(float x1, float y1, float z1, float x2, float y2, float z2, float theta)
  {
  // example that works! (c is any coord), rotates around Y axis 2 unit to the left
  //rotateAroundAxis(c.x - 2f, c.y + 1, c.z, c.x - 2f, c.y, c.z, c.angle_y);
  
  
  float rx = (x2 - x1);
  float ry = (y2 - y1);
  float rz = (z2 - z1);
  
  gl.glTranslatef(x1, y1, z1);
  gl.glRotatef(theta, rx, ry, rz);
  gl.glTranslatef(-x1, -y1, -z1);
  }
   */
  public void drawGrid()
  {
    GL2 gl = getGL();
    gl.glPushMatrix();
    {
      gl.glLoadMatrixd(getCamera().modelview, 0);

      float minx = -5f,
        miny = -5f;
      float maxx = 5f,
        maxy = 5f;
      float inc = 1f;

      gl.glColor4f(1f, 1f, 1f, 1f);

      //draw grid lines
      gl.glLineWidth(.5f);
      gl.glBegin(GL_LINES);
      for (float x = minx; x <= maxx; x += inc)
      {
        gl.glVertex2f(x, miny);
        gl.glVertex2f(x, maxy);
      }
      for (float y = miny; y <= maxy; y += inc)
      {
        gl.glVertex2f(minx, y);
        gl.glVertex2f(maxx, y);
      }
      gl.glEnd();

      //draw grid points
      gl.glPointSize(4f);
      gl.glBegin(GL_POINTS);
      for (float x = minx; x <= maxx; x += inc)
      {
        for (float y = miny; y <= maxy; y += inc)
        {
          gl.glVertex3f(x, y, 0f);
        }
      }
      gl.glEnd();

      //draw origin
      gl.glPointSize(8f);
      gl.glBegin(GL_POINTS);
      gl.glVertex3f(0f, 0f, 0f);
      gl.glEnd();
    }
    gl.glPopMatrix();

  }

  public void drawDebuggingInfo()
  {
    GL2 gl = getGL();

    if (drawDebugGrid == true)
    {
      drawGrid();
    }

    //Set to orthographic projectionMatrix
    getRenderer().setPerspective2D();

    //draw selected debugging info
    if (drawDebugFrameRate == true)
    {
      drawFrameRate();
    }

    if (drawDebugMouseDraggedPoint == true)
    {
      drawDebugSelectPoint();
    }

    if (SceneGraph.drawDebugMouseMovedPoint == true)
    {
      drawDebugMousePoint();
    }
  }

  /** 
   * Draws the current pixel position of the mouse as a green point. This position is returned from the MouseMotionListener,
   * as handled by the MouseHandler.getInstance().
   * 
   * @param gl
   */
  private void drawDebugMousePoint()
  {
    GL2 gl = getGL();

    if (MouseHandler.getInstance().debugMouseMovePoint != null)
    {
      gl.glColor4f(0f, 1f, 0f, 1f);
      gl.glPointSize(10f);

      gl.glBegin(GL_POINTS);
      gl.glVertex2f(MouseHandler.getInstance().debugMouseMovePoint.x, MouseHandler.getInstance().debugMouseMovePoint.y);
      gl.glEnd();
    }
  }

  /**
   * Draws the pixel position of the last point the mouse was clicked as a red point.
   * This position is returned from the MouseMotionListener,
   * as handled by the MouseHandler.getInstance().
   *
   * @param gl
   */
  private void drawDebugSelectPoint()
  {
    GL2 gl = getGL();

    if (MouseHandler.getInstance().debugMouseClickPoint != null)
    {
      gl.glColor4f(1f, 0f, 0f, 1f);
      gl.glPointSize(10f);

      gl.glBegin(GL_POINTS);
      gl.glVertex2f(MouseHandler.getInstance().debugMouseClickPoint.x, MouseHandler.getInstance().debugMouseClickPoint.y);
      gl.glEnd();
    }
  }

  private void drawFrameRate()
  {
    this.frames++;
    this.nowTime = Utils.nanosToMillis(currentNano);

    if (this.nowTime > this.lastTime + 1000)
    {
      this.lastTime = this.nowTime;
      this.fps = this.frames * 1;
      this.frames = 0;
    }

    TextRenderer debugTextRenderer = FontHandler.getInstance().getDefaultFont(18);

    debugTextRenderer.beginRendering(
      RenderUtils.getCamera().viewport[2],
      RenderUtils.getCamera().viewport[3]);
    debugTextRenderer.setColor(1f, 1f, 1f, 1f);
    debugTextRenderer.draw("fps: " + this.fps, 5, 5);
    debugTextRenderer.endRendering();
  }

  /*
  public void drawJava2DCoord(GL gl , CoordJava2D c)
  {
    
    if (c.img != null)
    {
    int inset = 2;
    int w = c.img.getWidth(null);
    int h = c.img.getHeight(null);

    if (w < 0 || h < 0)
    {
    return; //not loaded yet
    }

    TextureRenderer debugTextRenderer = new TextureRenderer(w, h, false);
    Graphics2D g = debugTextRenderer.createGraphics();

    //g.setColor(Color.GRAY);
    //g.fillRect(0,0,w,h);
    //g.drawImage(ImageFilterHandler.filterScale(img, scale, scale), 20, 20, dsw.bufferedImage.getWidth(null) - 40, dsw.bufferedImage.getHeight(null) - 40, null);
    //g.drawImage(c.img, inset, inset, w - inset*2, h - inset*2, null);
    g.drawImage(c.img, 0, 0, w, h, null);
    g.dispose();
    //debugTextRenderer.sync(0, 0, w, h);

    //debugTextRenderer.beginOrthoRendering(w, h );
    //debugTextRenderer.drawOrthoRect(0,0, 0,0, w,h);
    //debugTextRenderer.endOrthoRendering();

    debugTextRenderer.begin3DRendering();
    debugTextRenderer.draw3DRect(c.x, c.y, c.z, 0, 0, w, h, .05f);
    debugTextRenderer.end3DRendering();
    }
     
  }
   */
}
