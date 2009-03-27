/*
 * Viz.java
 * Created on January 27, 2007, 3:19 PM
 */
package renderers;

import renderers.layers.RendererLayer;
import behaviors.Behavior;
import behaviorism.BehaviorismDriver;
import handlers.MouseHandler;
import behaviors.geom.GeomUpdater;
import com.sun.opengl.util.j2d.TextRenderer;
import geometry.Geom;
import geometry.GeomPoint;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import handlers.FontHandler;
import java.util.Map;
import sequences.Sequence;
import utils.DebugTimer;
import utils.MatrixUtils;
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
  //public List<Geom> attachedGeoms = new ArrayList<Geom>();
  int ccc = 0;

  private void processBehavior(Behavior b, Geom g)
  {
    if (b.isDone == true && Behavior.debugBehaviors == false) //don't remove if just stepping...
        {
          g.behaviors.remove(b);
          b.isActive = false;
          //BehaviorismDriver.renderer.currentWorld.behaviors.remove(b);
          b.dispose();
          //b = null;
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
          //System.out.println("step = " + step);
          //System.out.println("stepSize = " + stepSize);
          if (step != 0)
          {
            b.step(currentNano, Utils.millisToNanos((long) (step * stepSize)));
            //b.isPaused = false;
          }
          else
          {
            System.out.println("pausing... allegedly...");
            b.pause(currentNano);
            //b.isPaused = true;
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
            //b.isPaused = false;
            b.tick(currentNano);
          }
        }
  }

  DebugTimer timer1 = new DebugTimer();
  DebugTimer timer2 = new DebugTimer();
  public void draw(GL gl, GLU glu)
  {
    this.currentNano = System.nanoTime();
    this.offset = 0f; //reset offset


    // update camera & set up projectionMatrix & basic modelview using camera
    updateCameraBehavior();
    BehaviorismDriver.renderer.setPerspective3D();

    //System.out.println("sequences size = " + BehaviorismDriver.renderer.currentWorld.sequences.size());
    Sequence.executeSequences(BehaviorismDriver.renderer.currentWorld.sequences, this.currentNano);    //tick on registered Behaviors...
    //synchronized (BehaviorismDriver.renderer.currentWorld.behaviors)
    {
      /*
      //System.out.println("registered behaviors size = " + BehaviorismDriver.renderer.currentWorld.behaviors.size());
      for (int ii = BehaviorismDriver.renderer.currentWorld.behaviors.size() - 1; ii >= 0; ii--)
      {
        //BehaviorGeom b = BehaviorismDriver.renderer.currentWorld.behaviors.get(ii);
        Behavior b = BehaviorismDriver.renderer.currentWorld.behaviors.get(ii);
        //removals must be done first
        if (b.isDone == true && Behavior.debugBehaviors == false) //don't remove if just stepping...
        {
          BehaviorismDriver.renderer.currentWorld.behaviors.remove(b);
          continue;
        }

        processBehavior(b);
     
        
        /////////
        if (reverse == true)
        {
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
         /////////
      }
    */    
    }
     
    reverse = false;

    //NO, not here, we are drawing stuff only via layers now
//    if (BehaviorismDriver.renderer.currentWorld.state != null)
//    {
//      BehaviorismDriver.renderer.currentWorld.state.setState(gl);
//    }

    //RendererLayer.clearGeoms(BehaviorismDriver.renderer.currentWorld.layers);
    //can we do this only if there is an actual change?
    for (Map.Entry<Integer, RendererLayer> entry : BehaviorismDriver.renderer.currentWorld.layers.entrySet())
    {
      RendererLayer layer = entry.getValue();
      layer.attachedGeoms.clear();
    }
    //attachedGeoms.clear();


    //NO - we don't need to push and pop attrib bits here
    //gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);

    //timer1.resetTime();
    traverseGeoms(gl, glu, BehaviorismDriver.renderer.currentWorld.geoms, currentNano, 0, 0f);
    //System.out.println("time in traverseGeom = " + timer1.resetTime());

    //gl.glPopAttrib();

    //timer1.resetTime();
    drawGeoms(gl, glu);
    //System.out.println("time in drawGeoms = " + timer1.resetTime());

  
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
  }

  public void traverseGeoms(GL gl, GLU glu, List<Geom> geoms, long currentNano, int level, float prevOffset)
  {
     // System.out.println("in traverseGeoms : geoms.size = " + geoms.size());
    //update the behaviors that are attached to
    //synchronized (geoms)
    {
      List<Geom> scheduledForRemovalGeoms = new ArrayList<Geom>();

      //draw coordinates based on updates made by behaviors.
      //for (Geom g : geoms)
      for (int num = 0; num < geoms.size(); num++)
      {
        Geom g = geoms.get(num);
        //System.out.println("Geom class = " + g.getClass());
        //if (BehaviorismDriver.renderer.currentWorld.isPaused != true)
        {
          for (int i = g.behaviors.size() - 1; i >= 0; i--)
          {
            Behavior b = g.behaviors.get(i);
        //    System.out.println("about to process behavior " + b);

            processBehavior(b, g);
            
          //  System.out.println("is (" + b + ") active/isPaused = " + b.isActive + "/" + b.isPaused);
            if (b.isActive == true && b.isPaused == false)
            {
            //  System.out.println("is " + b + " an instanceof GeomUpdater?");
              if (b instanceof GeomUpdater)
              {
              //  System.out.println("yes...");
                //b.change(g);
                //System.out.println("about to update behavior " + b);
                ((GeomUpdater) b).updateGeom(g);
              }
              else
              {
               // System.out.println("no " + b.getClass() + " is not an instance of GeomUpdater!!!");

              }
            }

//            if (b.isDone == true && Behavior.debugBehaviors == false)
//            {
//              g.behaviors.remove(b);
//              b.dispose();
//            }
          }
        }

        /*
        if (g.state != null)
        {
          g.state.setState(gl);
        }
        */
       // gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS); //PUSH current state onto attribute stack
        //float offset = calculateOffset(level, num, prevOffset);
        //offset += .0005f; //.00001f;
        offset += vizOffset; //.00001f; //ideal, works good on my nvidia card
        //offset += .0002f; //seems good for ati card

        //LOGIC HERE
        //1. execute geom's transformation and store the new matirx in the geom's modelview
        //2. if it is viewable/active, then make sure that it is attached

        if (EXPLICITLY_CALCULATE_MODELVIEW == true) //testing if this is faster to calc transformations ourselves...
        {
          g.transform2(); //EXPERIMENTAL ONE
          BehaviorismDriver.renderer.currentWorld.layers.get(g.layerNum).attachedGeoms.add(g);
          traverseGeoms(gl, glu, g.geoms, currentNano, level + 1, offset);

        }
        else
        {
          gl.glPushMatrix(); ////PUSH current matrix onto stack
          g.transform(gl, glu); //REAL ONE
          BehaviorismDriver.renderer.currentWorld.layers.get(g.layerNum).attachedGeoms.add(g);
          //if (g.isSelectable == true)
          //if (g.isActive == true && g.isVisible == true)
          {
            gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, g.modelview, 0);
            g.isAttached = true;
          }
          traverseGeoms(gl, glu, g.geoms, currentNano, level + 1, offset);
          gl.glPopMatrix(); //POP matrix off stack and return to previoeus matrix
        }
        //gl.glPopAttrib(); //POP attributes off stack and return to previous state

        
        if (g.isDone == true)
        {
            //System.out.println("geom is DONE!");
          scheduledForRemovalGeoms.add(g);
        }

      }

      for (Geom g : scheduledForRemovalGeoms)
      {
        g.dispose();

        
        geoms.remove(g);
        g = null;
      }
    } // end sync
  }

  /*
  private float calculateOffset(int level, int num, float prevOffset)
  {
    float offset = (float) (prevOffset + (Math.pow(10, -3 - level) * (num + 1)));
    //System.out.printf("in calculateOffset() : level=%d, num=%d offset = %f\n", level, num, offset);
    return offset;
  }
  */

  /*
  public void transformGeom(GL gl, GLU glu, Geom g)
  {
    g.transform(gl, glu);
  }
  */

  List<Geom> invisiblePickingGeoms = new ArrayList<Geom>();

  public void drawGeoms(GL gl, GLU glu)
  {
    if (EXPLICITLY_CALCULATE_MODELVIEW == true)
    {
      gl.glMatrixMode(gl.GL_PROJECTION);
      gl.glLoadMatrixd(RendererJogl.projectionMatrix, 0);
      gl.glMatrixMode(gl.GL_MODELVIEW);
      //gl.glLoadMatrixd(cam.modelview);
    }

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

        if (g instanceof GeomPoint)
        {
        //System.out.println("g : " + idx + " : " + g);
        System.out.println("modelview matrix = :");
        MatrixUtils.printDoubleArray(RendererJogl.modelviewMatrix);
        System.out.println("projection matrix = :");
        MatrixUtils.printDoubleArray(RendererJogl.projectionMatrix);
        System.out.println("point modelview matrix = :");
        MatrixUtils.printDoubleArray(g.modelview);
        }
        gl.glLoadMatrixd(g.modelview, 0);

        
        if (g instanceof GeomPoint)
        {
          //object coords --> view coords
          double[] eyeVec = MatrixUtils.objectCoordsToEyeCoords(
            MatrixUtils.pointToHomogenousCoords(g.anchor), RendererJogl.modelviewMatrix);

          double[] clipVec = MatrixUtils.eyeCoordsToClipCoords(eyeVec, RendererJogl.projectionMatrix);

          double[] deviceVec = MatrixUtils.clipCoordsToDeviceCoords(clipVec);
          
          double[] windowVec = MatrixUtils.deviceCoordsToWindowCoords(deviceVec, RendererJogl.viewportBounds);

           System.out.println("windowVec by hand = ");
           MatrixUtils.printDoubleVector(windowVec);
          System.out.println("projected = " + BehaviorismDriver.renderer.projectPoint(g.anchor, RendererJogl.modelviewMatrix));
        }

        //if ((layer.state.DEPTH_TEST == false && g.state == null) || (g.state != null && g.state.DEPTH_TEST == false))
        {
          invisiblePickingGeoms.add(g);
        }
        g.draw(gl, glu, 0f);

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
      g.drawPickingBackground(gl);
    }
    }
    
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

    g.draw(gl, glu, offset);

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
    //gl.glColor4f(1f, 1f, 1f, 1f);
    //BehaviorismDriver.renderer.glut.glutSolidSphere(2, 2, 2);

    float minx = -5f,
      miny = -5f;
    float maxx = 5f,
      maxy = 5f;
    float inc = 1f;

    gl.glColor4f(1f, 1f, 1f, 1f);

    //test draw frid lines
    gl.glLineWidth(.5f);
    gl.glBegin(gl.GL_LINES);
    for (float x = minx; x <= maxx; x += inc)
    {
      gl.glVertex3f(x, miny, 0f);
      gl.glVertex3f(x, maxy, 0f);
    }
    for (float y = miny; y <= maxy; y += inc)
    {
      gl.glVertex3f(minx, y, 0f);
      gl.glVertex3f(maxx, y, 0f);
    }

    gl.glEnd();

    //test draw grid points
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

    //test draw origin
    gl.glPointSize(8f);
    gl.glBegin(gl.GL_POINTS);
    gl.glVertex3f(0f, 0f, 0f);
    gl.glEnd();

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
      BehaviorismDriver.viz.drawDebugSelect(gl);
    }

    if (VizGeom.drawDebugMouseMovedPoint == true)
    {
      BehaviorismDriver.viz.drawDebugMousePoint(gl);
    }
  }

  /** this method takes the pixel position of the mouse (returned from the MouseMotionListener,
   * as handled by the MouseHandler) and renders an indicator of it on the screen (using 
   * orthographic projectionMatrix).
   * 
   * @param gl
   */
  public void drawDebugMousePoint(GL gl)
  {
    if (MouseHandler.debugMousePoint != null)
    {
      gl.glColor4f(0f, 1f, 0f, 1f);
      gl.glPointSize(10f);

      gl.glBegin(gl.GL_POINTS);
      gl.glVertex3f(MouseHandler.debugMousePoint.x, MouseHandler.debugMousePoint.y, MouseHandler.debugMousePoint.z);
      //gl.glVertex2f(MouseHandler.debugMousePoint.x, MouseHandler.debugMousePoint.y);
      gl.glEnd();
    }
  }

  public void drawDebugSelect(GL gl)
  {
    if (MouseHandler.debugSelectPoint != null)
    {
      gl.glColor4f(1f, 0f, 0f, 1f);
      gl.glPointSize(10f);

      gl.glBegin(gl.GL_POINTS);
      gl.glVertex3f(MouseHandler.debugSelectPoint.x, MouseHandler.debugSelectPoint.y, MouseHandler.debugSelectPoint.z);

      gl.glEnd();
    }
  }

  public void drawFrameRate(GL gl)
  {
    this.frames++;
    this.nowTime = Utils.nanosToMillis(currentNano); // / 1000000); //System.currentTimeMillis();
    //System.out.println("time between frames = " + (nowTime - lastTime));

    if (this.nowTime > this.lastTime + 1000)
    {

      //System.out.println("" + this.frames + " perSec");
      this.lastTime = this.nowTime;
      this.fps = this.frames * 1;
      this.frames = 0;
    }

    if (FontHandler.getInstance().textRenderers.size() > 0)
    {
      TextRenderer tr = (FontHandler.getInstance().textRenderers.get(0)); //just get smallest font
      //TextRenderer tr = new TextRenderer(new Font("Arial", Font.PLAIN, 36), true, true, null, false)  ;
               
      if (tr != null)
      {
      
       // TextRenderHack.fixIt( tr );
 
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
        tr.setUseVertexArrays(false);
      
        tr.beginRendering((int) (BehaviorismDriver.canvasWidth), (int) (BehaviorismDriver.canvasHeight));
        tr.setColor(1f, 1f, 1f, 1f);
        //tr.draw("fps: " + this.fps, 15, 10);
        tr.draw("fps: " + this.fps, 75, 75);
        tr.endRendering();

        tr.flush();
      }
    }

  }
}
