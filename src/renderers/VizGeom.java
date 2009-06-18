package renderers;

import renderers.layers.RendererLayer;
import behaviors.Behavior;
import behaviorism.BehaviorismDriver;
import handlers.MouseHandler;
import behaviors.geom.GeomUpdater;
import com.sun.opengl.util.j2d.TextRenderer;
import geometry.Geom;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import handlers.FontHandler;
import java.util.Map;
import sequences.Sequence;
import utils.Utils;

public class VizGeom
{

  /**
   * Sets whether or not we calculate the modelview matricies ourselves as we
   * traverse the scenegraph. Seems to work, but experimental...
   */
  public static boolean EXPLICITLY_CALCULATE_MODELVIEW = true; //false; //true;
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

  private void processBehaviors(Geom g)
  {
    for (int i = g.behaviors.size() - 1; i >= 0; i--)
    {
      Behavior b = g.behaviors.get(i);

      processBehavior(b, g);

      if (b.isActive == true && b.isPaused == false)
      {
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
      b.isActive = false;
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
        b.tick(currentNano);
      }
    }
  }

  /**
   * Draws each element in the scene graph. The steps to render these elements includes the following:
   * 1. Set up the camera; 2. Update any high-level sequences; 3. Determine the modelview matrix of each
   * element in the scene graph; 4. Process each layer of geometry as needed (ie, sort and set state);
   * 5. Render the geometry to the framebuffer.
   * @param gl
   * @param glu
   */
  public void draw(GL gl)
  {
    this.currentNano = System.nanoTime();
    this.offset = 0f; //reset offset

    // update camera & set up projectionMatrix & basic modelview using camera
    updateCameraBehavior();

    // execute high=level sequences
    Sequence.executeSequences(BehaviorismDriver.renderer.currentWorld.sequences, this.currentNano);

    // clear layers -- bleh (think about...)
    for (Map.Entry<Integer, RendererLayer> entry : BehaviorismDriver.renderer.currentWorld.layers.entrySet())
    {
      RendererLayer layer = entry.getValue();
      layer.attachedGeoms.clear(); //can we do this only if there is an actual change?
    }

    //traverse scene graph to determine each element's transformation matrix
    traverseGeoms(gl, BehaviorismDriver.renderer.currentWorld.geoms,
      BehaviorismDriver.renderer.currentWorld.isTransformed || BehaviorismDriver.renderer.cam.isTransformed,
      offset);
    BehaviorismDriver.renderer.currentWorld.isTransformed = false;
    BehaviorismDriver.renderer.cam.isTransformed = false;

    //iterate through layers and render each element to screen.
    drawGeoms(gl);
  }

  private void updateCameraBehavior()
  {
    Geom cam = BehaviorismDriver.renderer.getCamera();

    if (cam == null)
    {
      return;
    }

    if (BehaviorismDriver.renderer.currentWorld.isPaused != true)
    {
      for (int i = cam.behaviors.size() - 1; i >= 0; i--)
      {
        Behavior b = cam.behaviors.get(i);

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
    }

    BehaviorismDriver.renderer.setPerspective3D();
  }

  private void traverseGeoms(GL gl, List<Geom> geoms, boolean parentTransformed, float prevOffset) //, long currentNano, int level, float prevOffset)
  {
    List<Geom> scheduledForRemovalGeoms = new ArrayList<Geom>();

    for (Geom g : geoms)
    {
      processBehaviors(g);

      offset = prevOffset + vizOffset; //.00001f; //ideal, works good on my nvidia card

      if (parentTransformed == true) //ie, if the parent has changed, then the children need to change too.
      {
        g.isTransformed = true;
      }

      g.transform2(); //EXPERIMENTAL ONE
      BehaviorismDriver.renderer.currentWorld.layers.get(g.layerNum).attachedGeoms.add(g);
      traverseGeoms(gl, g.geoms, g.isTransformed, offset); //, currentNano, level + 1, offset);

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
  public void drawGeoms(GL gl)
  {
    gl.glMatrixMode(gl.GL_PROJECTION);
    gl.glLoadMatrixd(RendererJogl.projectionMatrix, 0);
    gl.glMatrixMode(gl.GL_MODELVIEW);

    //gl.glPushMatrix();


    invisiblePickingGeoms.clear();
    for (Map.Entry<Integer, RendererLayer> entry : BehaviorismDriver.renderer.currentWorld.layers.entrySet())
    {
      //System.out.println("layer at pos " + entry.getKey() + " has " + entry.getValue().attachedGeoms.size() + " entries.");
      RendererLayer layer = entry.getValue();

      layer.sortGeomsInLayer();


      layer.state.setState(gl);



      int idx = 0;
      for (Geom g : layer.attachedGeoms)
      {
        //System.out.println("idx " + idx + " : g is a " + g.getClass());

        if (!g.isActive)
        {
          continue;
        }
        if (!g.isVisible)
        {
          continue;
        }
        if (g.isDone)
        {
          continue;
        }

//        if (g instanceof GeomTextOutset)
//        {
//          System.out.println("huhuhuhu?");
//        }

        if (g.state != null) //individual geoms can still override layer... should this be allowed???
        {
          g.state.setState(gl);
        }

        /*
        if (g instanceof GeomPoint)
        {
        //System.out.println("g : " + idx + " : " + g);
        System.out.println("modelview matrix = :");
        MatrixUtils.printMatrix(RendererJogl.modelviewMatrix);
        System.out.println("projection matrix = :");
        MatrixUtils.printMatrix(RendererJogl.projectionMatrix);
        System.out.println("point modelview matrix = :");
        MatrixUtils.printMatrix(g.modelview);
        }
         */
        gl.glLoadMatrixd(g.modelview, 0);

        /*
        if (g instanceof GeomPoint)
        {
        MatrixUtils.printVector(MatrixUtils.pointToHomogenousCoords(g.anchor));
        Point3f windowVec = MatrixUtils.project(g.anchor);
        System.out.println("windowVec = " + windowVec);


        //          //object coords --> view coords
        //          double[] eyeVec = MatrixUtils.objectCoordsToEyeCoords(
        //            g.anchor,
        //            RendererJogl.modelviewMatrix);
        //
        //          double[] clipVec = MatrixUtils.eyeCoordsToClipCoords(eyeVec, RendererJogl.projectionMatrix);
        //
        //          double[] deviceVec = MatrixUtils.clipCoordsToDeviceCoords(clipVec);
        //
        //          double[] windowVec = MatrixUtils.deviceCoordsToWindowCoords(deviceVec, RendererJogl.viewportBounds);
        //
        //           System.out.println("windowVec by hand = ");
        //   MatrixUtils.printVector(windowVec);
        //System.out.println("projected = " + BehaviorismDriver.renderer.projectPoint(g.anchor, RendererJogl.modelviewMatrix));
        }
         */
        //if ((layer.state.DEPTH_TEST == false && g.state == null) || (g.state != null && g.state.DEPTH_TEST == false))
        {
          invisiblePickingGeoms.add(g);
        }
        //g.draw(gl, glu, 0f);
        g.draw(gl);

        if (g.state != null) //individual geoms can still override layer... should this be allowed??? this is slow
        {
          layer.state.setState(gl); //have to revert to normal layer state if the geom has overridden it
        }

        idx++;
      }
    }

    //draw invisiblePickingGeoms

    if (invisiblePickingGeoms.size() > 0)
    {
      gl.glEnable(GL.GL_DEPTH_TEST);

      for (Geom g : invisiblePickingGeoms)
      {
        gl.glLoadMatrixd(g.modelview, 0);
        g.drawPickingBackground(gl); //TO DO handle picking backgorunds!
      }
    }

  //gl.glPopMatrix();
  //BehaviorismDriver.renderer.setPerspective3D();

  }

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

    /*
    if (MouseHandler.selectedGeom == g)
    {
    System.out.println("you selcted g " + g);
    State.printCurrentState(gl);
    }
     */

    //g.draw(gl, glu, offset);
    g.setOffset(offset); //that is, only if specified-- TO DO
    g.draw(gl);

    if (drawDebugGeom == true)
    {
      g.drawDebugGeom(gl, glu);
    }

  }

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
  public void drawGrid(GL gl)
  {
    gl.glPushMatrix();
    {
      gl.glLoadMatrixd(BehaviorismDriver.renderer.cam.modelview, 0);

      float minx = -5f,
        miny = -5f;
      float maxx = 5f,
        maxy = 5f;
      float inc = 1f;

      gl.glColor4f(1f, 1f, 1f, 1f);

      //draw grid lines
      gl.glLineWidth(.5f);
      gl.glBegin(gl.GL_LINES);
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
      gl.glBegin(gl.GL_POINTS);
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
      gl.glBegin(gl.GL_POINTS);
      gl.glVertex3f(0f, 0f, 0f);
      gl.glEnd();
    }
    gl.glPopMatrix();

  }

  public void drawDebuggingInfo(GL gl)
  {
    if (drawDebugGrid == true)
    {
      drawGrid(gl);
    }

    //Set to orthographic projectionMatrix
    BehaviorismDriver.renderer.setPerspective2D();

    //draw selected debugging info
    if (VizGeom.drawDebugFrameRate == true)
    {
      BehaviorismDriver.viz.drawFrameRate(gl);
    }

    if (VizGeom.drawDebugMouseDraggedPoint == true)
    {
      BehaviorismDriver.viz.drawDebugSelectPoint(gl);
    }

    if (VizGeom.drawDebugMouseMovedPoint == true)
    {
      BehaviorismDriver.viz.drawDebugMousePoint(gl);
    }
  }

  /** 
   * Draws the current pixel position of the mouse as a green point. This position is returned from the MouseMotionListener,
   * as handled by the MouseHandler.
   * 
   * @param gl
   */
  private void drawDebugMousePoint(GL gl)
  {
    if (MouseHandler.debugMousePoint != null)
    {
      gl.glColor4f(0f, 1f, 0f, 1f);
      gl.glPointSize(10f);

      gl.glBegin(gl.GL_POINTS);
      gl.glVertex2f(MouseHandler.debugMousePoint.x, MouseHandler.debugMousePoint.y);
      gl.glEnd();
    }
  }

  /**
   * Draws the pixel position of the last point the mouse was clicked as a red point.
   * This position is returned from the MouseMotionListener,
   * as handled by the MouseHandler.
   *
   * @param gl
   */
  private void drawDebugSelectPoint(GL gl)
  {
    if (MouseHandler.debugSelectPoint != null)
    {
      gl.glColor4f(1f, 0f, 0f, 1f);
      gl.glPointSize(10f);

      gl.glBegin(gl.GL_POINTS);
      gl.glVertex2f(MouseHandler.debugSelectPoint.x, MouseHandler.debugSelectPoint.y);
      gl.glEnd();
    }
  }

  private void drawFrameRate(GL gl)
  {
    this.frames++;
    this.nowTime = Utils.nanosToMillis(currentNano);

    if (this.nowTime > this.lastTime + 1000)
    {
      this.lastTime = this.nowTime;
      this.fps = this.frames * 1;
      this.frames = 0;
    }

    if (FontHandler.getInstance().textRenderers.size() > 0)
    {
      TextRenderer tr = (FontHandler.getInstance().textRenderers.get(0)); //just get smallest font

      if (tr != null)
      {
        tr.beginRendering(BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
        tr.setColor(1f, 1f, 1f, 1f);
        tr.draw("fps: " + this.fps, 5, 5);
        tr.endRendering();
      }
    }
  }

  public void drawJava2DCoord(GL gl /*, CoordJava2D c*/)
  {
    /*
    if (c.img != null)
    {
    int inset = 2;
    int w = c.img.getWidth(null);
    int h = c.img.getHeight(null);

    if (w < 0 || h < 0)
    {
    return; //not loaded yet
    }

    TextureRenderer tr = new TextureRenderer(w, h, false);
    Graphics2D g = tr.createGraphics();

    //g.setColor(Color.GRAY);
    //g.fillRect(0,0,w,h);
    //g.drawImage(ImageFilterHandler.filterScale(img, scale, scale), 20, 20, dsw.bufferedImage.getWidth(null) - 40, dsw.bufferedImage.getHeight(null) - 40, null);
    //g.drawImage(c.img, inset, inset, w - inset*2, h - inset*2, null);
    g.drawImage(c.img, 0, 0, w, h, null);
    g.dispose();
    //tr.sync(0, 0, w, h);

    //tr.beginOrthoRendering(w, h );
    //tr.drawOrthoRect(0,0, 0,0, w,h);
    //tr.endOrthoRendering();

    tr.begin3DRendering();
    tr.draw3DRect(c.x, c.y, c.z, 0, 0, w, h, .05f);
    tr.end3DRendering();
    }
     */
  }
}
