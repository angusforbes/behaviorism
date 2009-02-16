/* Geom.java - Created on July 12, 2007, 7:52 PM */
package geometry;

import renderers.State;
import behaviors.Behavior;
//import behaviors.BehaviorIsActive;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import data.Data;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import behaviorism.BehaviorismDriver;
import behaviors.geom.discrete.BehaviorIsActive;
import java.util.Collections;
import java.util.Comparator;
import renderers.RendererJogl;
import utils.MatrixUtils;
import utils.Utils;
import worlds.WorldGeom;

public abstract class Geom //extends Point3f //Object
{

  /**
   * A name for this Geom. Does not need to be unique.
   */
  public String name = "untitled";
  /**
   * A unique id for this Geom.
   */
  public String id = ""; //not currently used...
  /**
   * A concurrent list of all child Geoms attached to this Geom.
   */
  public List<Geom> geoms = new CopyOnWriteArrayList<Geom>();
  /**
   * A concurrent list of all Behaviors attached to this Geom.
   */
  //public List<Behavior> behaviors = new CopyOnWriteArrayList<Behavior>();
  public List<Behavior> behaviors = new CopyOnWriteArrayList<Behavior>();
  /**
   * A list of all Datas attached to this Geom.
   */
  //public List<Data> datas = Collections.synchronizedList(new ArrayList<Data>());
  public Data data = new Data();
  /** this will soon be deprecated-- we should be using just regular x,y,z */
  public Point3f anchor = new Point3f(0f, 0f, 0f);
  public float x = 0f;
  public float y = 0f;
  public float z = 0f;
  /**
   * The relative point (in parent's coordinates) around which the object is to rotate.
   * By default it is set to be the lower left corner of the object
   */
  public GeomPoint rotateAnchor = null; //new GeomPoint(.5f, .5f, 0f);
  /**
   * A point which describes the rotation around the x, y, and z axes. Rotations are in degrees, not radians.
   */
  public Point3d rotate = new Point3d(0, 0, 0);
  /**
   * The relative point (in parent's coordinates) around which the object is to scale.
   * By default it is set to be the lower left corner of the object
   */
  public Point3f scaleAnchor = new Point3f(0f, 0f, 0f);
  /**
   * A point which describes the scaling factor of the width, height, and depth dimensions of the object.
   * By default each dimension is set to a factor of 1.
   */
  public Point3d scale = new Point3d(1, 1, 1);
  //public boolean childrenInheritRotation = true;
  //public boolean childrenInheritScale = true;
  //temp!!! used for compatabilty with BehaviorSize3D... rethink...
  public float w = 1f;
  public float h = 1f;
  public float d = 0f;
  /** we are not really using this... should we be? Looks we are *always* using ScaleEnum.CENTER (in the transform) */
  @Deprecated
  public ScaleEnum scaleDirection = ScaleEnum.NE; //default
  public float r = (float) Math.random();
  public float g = (float) Math.random();
  public float b = (float) Math.random();
  public float a = 1f;
  public float area = 0f;
  //various flags for rendering, texturing, picking, timing, etc.
  public boolean isActive = false; //if true, it will be displayed
  public boolean isDone = false; //if true, it will be removed
  public boolean isTextured = false; //if true, we will handle texture coords
  public boolean isReady = false; //ie, has texture been loaded/generated?
  public boolean isBound = false; //ie, has texture been bound to GL
  public boolean isSelectable = false; //ie, can it be selected by a mouseClick?
  public boolean isVisible = true; //ie, is it currently within the view frustum? (if no, can't be picked)
  public boolean isAttached = false; //ie, is it attached to the scenegraph (has it been drawn once?)
  public TextureData textureData = null;
  public Texture texture = null;
  /**
   * A 4x4 double array representing the Geom's openGL modelview matrix within the scenegraph hierarchy.
   */
  public double[] modelview = MatrixUtils.getIdentity();
  /**
   * An object representing the current set of openGL states of this Geom.
   * If null, the renderer will automatically inheret it's parent's states
   * (ie, either the parent Geom, or the current world iteself).
   */
  public State state = null;
  public int layerNum = 0;
  //indicates which Geom mouse actions we fire when the MouseHandler finds a mouse event on this Geom.
  //The default value is simply to use itself.
  public Geom draggableObject = this;
  public Geom selectableObject = this;
  public Geom clickableObject = this;
  public Geom releasableObject = this;
  public Geom mouseoverableObject = this;

  /**
   * The parent of this Geom. Is either the Geom it is attached to, or null if the Geom is attached directly to the current world.
   * This is used by the MouseHandler to determine object selection, etc.
   */
  public Geom parent = null;

  public Geom()
  {
  }

  public Geom(float x, float y, float z)
  {
    setPos(x, y, z);
  }

  public Geom(Point3f p3f)
  {
    setPos(p3f);
  }

  /**
   * Instructions for the Geom to draw itself within the openGL context. Called during each frame of the openGL display loop.
   * gl and glu define the openGL context. offset is a tiny value that can be used to ensure that there are no rendering errors
   * on objects that overlap on the same plane.
   * @param gl
   * @param glu
   * @param offset
   */
  public abstract void draw(GL gl, GLU glu, float offset);

  /**
   * Checks to see if the entire Geom is completely viewable within the screen bounds.
   * @return true if is completely visible, false if not.
   */
  public boolean checkIsCompletelyVisible() //later make abstract-- just haven't had time to put method in all Geoms yet
  {
    //System.out.println("here... " + this.getClass());
    return true;
  }

  //later make abstract-- just haven't had time to put method in all Geoms yet
  public boolean checkIsVisible()
  {
    //System.out.println("here... " + this.getClass());
    this.isVisible = true;
    return true;
  }

  public void toggleIsVisible()
  {
    this.isVisible = !this.isVisible;
  }

  public void drawDebugGeom(GL gl, GLU glu)
  {
    gl.glDisable(gl.GL_BLEND);
    if (rotateAnchor != null)
    {
      gl.glPointSize(10f);
      gl.glColor4f(0f, 1f, 0f, 1f);
      gl.glBegin(gl.GL_POINTS);

      //gl.glVertex3f(0f, 0f, 0f);
      gl.glVertex3f(rotateAnchor.anchor.x, rotateAnchor.anchor.y, rotateAnchor.anchor.z);
      /*
      gl.glVertex3f(anchor.x + rotateAnchor.anchor.x,
      anchor.y + rotateAnchor.anchor.y,
      anchor.z + rotateAnchor.anchor.z);
       */
      gl.glEnd();
    }
    if (scaleAnchor != null)
    {
      gl.glPointSize(5f);

      gl.glColor4f(1f, 0f, 0f, 1f);
      gl.glBegin(gl.GL_POINTS);
      gl.glVertex3f(scaleAnchor.x, scaleAnchor.y, scaleAnchor.z);
      gl.glEnd();
    }
    gl.glEnable(gl.GL_BLEND);
  }

  /**
   * Translates a point in this Geom's coordinates into world coordinates.
   * Just a convenience method that calls MatrixUtils.getGeomPointInWorldCoordinates() with appropriate parameters.
   * @param geomPt
   * @return the point in world coordinates
   */
  public Point3f geomPointToWorldPoint(Point3f geomPt)
  {
    return MatrixUtils.toPoint3f(
      MatrixUtils.getGeomPointInWorldCoordinates(MatrixUtils.toPoint3d(geomPt), modelview, RendererJogl.modelviewMatrix));

  }

  /**
   * Transforms the Geoms's modelview matrix according to its translate, rotate, and scale parameters.
   * This object is final and thus cannot be overriden. It is called by the Renderer during each display loop.
   * Actually there are reasons to override it..., now it is non-final again... (see GeomClosestLineBetweenPolys).
   * @param gl
   * @param glu
   */
  public void transform(GL gl, GLU glu)
  {
    // translate command
    gl.glTranslatef(anchor.x, anchor.y, anchor.z);

    // rotate commands
    if (rotateAnchor != null)
    {
      gl.glTranslatef(rotateAnchor.anchor.x, rotateAnchor.anchor.y, rotateAnchor.anchor.z);
      gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
      gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
      //System.out.println("rotate y = " + (float)rotate.y);
      gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);
      gl.glTranslatef(-rotateAnchor.anchor.x, -rotateAnchor.anchor.y, -rotateAnchor.anchor.z);
    }

    // scale commands
    gl.glTranslatef(scaleAnchor.x, scaleAnchor.y, scaleAnchor.z);
    gl.glScalef((float) scale.x, (float) scale.y, (float) scale.z);
    gl.glTranslatef(-scaleAnchor.x, -scaleAnchor.y, -scaleAnchor.z);
  }

  public void scale(GL gl)
  {
    ScaleEnum se = scaleDirection;
    //determineScaleAnchor(ScaleEnum.NE);
    //determineScaleAnchor(ScaleEnum.CENTER);

    gl.glTranslatef(scaleAnchor.x, scaleAnchor.y, scaleAnchor.z);
    gl.glScalef((float) scale.x, (float) scale.y, (float) scale.z);
    gl.glTranslatef(-scaleAnchor.x, -scaleAnchor.y, -scaleAnchor.z);

    //gl.glScalef((float)scale.x, (float)scale.y, (float)scale.z);
    scaleDirection = se;
  }

  public void translate(GL gl, float tx, float ty, float tz)
  {
    gl.glTranslatef(tx, ty, tz);
  }

  /**
   * This version of addGeom activates the Geom a specified number of milliseconds in the future.
   * It is simply a convience method to avoid having to define a BehaviorIsActive behavior since
   * it is such a common thing to need to do.
   * @param g
   * @param millisInFuture
   */
  public void addGeom(Geom g, long millisInFuture)
  {
    addGeomToLayer(g, false, 0);

    BehaviorIsActive bia = BehaviorIsActive.activateAtMillis(g, System.nanoTime(), millisInFuture);
  }

  public void addGeomToLayer(Geom g, long millisInFuture, int layerNum)
  {
    addGeomToLayer(g, false, layerNum);

    BehaviorIsActive bia = BehaviorIsActive.activateAtMillis(g, System.nanoTime(), millisInFuture);
  }

  /**
   * This version of addGeom activates the Geom a specified number of milliseconds in the future
   * from a specifed base time (in nanoseconds). The idea is that you would have a uniform
   * base time from when all behaviors should start, and you can increment the millisInFuture as
   * necessary. Ie, at the start of a sequence call:
   *
   * long baseNano = System.nanoTime();
   *
   * and then reuse that time istead of making repeated calls to System.nanoTime().
   *
   * ie,
   *
   * addGeom(g1, baseNano, 1000L);
   * //do stuff
   * //sleep(100L);
   * //etc
   * addGeom(g2, baseNano, 2000L);
   *
   * It is simply a convience method to avoid having to define a BehaviorIsActive behavior since
   * it is such a common thing to need to do.
   * @param g
   * @param millisInFuture
   */
  public void addGeom(Geom g, long baseNano, long millisInFuture)
  {
    addGeomToLayer(g, false, 0);

    BehaviorIsActive.activateAtMillis(g, baseNano, millisInFuture);
  }

  public void addGeomActivateChildrenFalse(Geom g, long baseNano, long millisInFuture)
  {
    addGeomToLayer(g, false, 0);

    BehaviorIsActive bia = BehaviorIsActive.activateAtMillis(g, baseNano, millisInFuture);
    bia.activateChildren = false;
  }

  public void addGeomToLayer(Geom g, long baseNano, long millisInFuture, int layerNum)
  {
    addGeomToLayer(g, false, layerNum);

    BehaviorIsActive.activateAtMillis(g, baseNano, millisInFuture);
  }

  public void addGeom(Geom g, long baseNano,
    List<Long> timesMSs)
  {
    addGeomToLayer(g, false, 0);

    BehaviorIsActive.activateBetweenMillis(g, baseNano, timesMSs);
  }

  public void addGeomToLayer(Geom g, long baseNano, List<Long> timesMSs, int layerNum)
  {
    addGeomToLayer(g, false, layerNum);

    BehaviorIsActive.activateBetweenMillis(g, baseNano, timesMSs);
  }

  /**
   * Adds a Geom to this WorldGeom. By default the Geom is immediately activated.
   * @param g The Geom being added.
   */
  public void addGeom(Geom g)
  {
    addGeomToLayer(g, true, 0);
  }

  public void addGeom(Geom g, boolean isActive)
  {
    addGeomToLayer(g, isActive, 0);
  }

  public void addGeomToLayer(Geom g, int layerNum)
  {
    addGeomToLayer(g, true, layerNum);
  }

  /**
   * This version of addGeom activates the Geom a specified number of milliseconds in the future.
   * It is simply a convience method to avoid having to define a BehaviorIsActive behavior since
   * it is such a common thing to need to do.
   * @param g
   * @param millisInFuture
   */
//	public void addGeom(Geom g, long millisInFuture)
//  {
//		addGeom(g, System.nanoTime(), millisInFuture);
//  }
  /**
   * This version of addGeom activates the Geom a specified number of milliseconds in the future
   * from a specifed base time (in nanoseconds). The idea is that you would have a uniform
   * base time from when all behaviors should start, and you can increment the millisInFuture as
   * necessary. Ie, at the start of a sequence call:
   *
   * long baseNano = System.nanoTime();
   *
   * and then reuse that time istead of making repeated calls to System.nanoTime().
   *
   * ie,
   *
   * addGeom(g1, baseNano, 1000L);
   * //do stuff
   * //sleep(100L);
   * //etc
   * addGeom(g2, baseNano, 2000L);
   *
   * It is simply a convience method to avoid having to define a BehaviorIsActive behavior since
   * it is such a common thing to need to do.
   * @param g
   * @param millisInFuture
   */

//  public void addGeom(Geom g, long baseNano, long millisInFuture)
//  {
//    g.isActive = false;
//    g.parent = this;
//    geoms.add(g);
//      
//	  BehaviorIsActive bia = BehaviorIsActive.activateAtMillis(g, baseNano, millisInFuture);
//		//g.attachBehavior(bia);
//  }
//
//  public void addGeomToLayer(Geom g, long baseNano, long millisInFuture, int layerNum)
//  {
//    addGeomToSceneGraph(g, this.geoms, false, this);
//    BehaviorismDriver.renderer.currentWorld.addGeomToRendererLayer(g, layerNum);
// 
//    BehaviorIsActive bia = BehaviorIsActive.activateAtMillis(g, baseNano, millisInFuture);
//  }
//  
  public void addGeomToLayer(Geom g, boolean isActive, int layerNum)
  {
    WorldGeom.addGeomToSceneGraph(g, this.geoms, isActive, this);
    WorldGeom.addGeomToRendererLayer(g, layerNum);
  }

  /**
   * This version of addGeom waits until the Geom has been drawn once (so that the modelview is updated correctly)
   * before returning. If the parameter isActive is true the Geom will be activated only after is has been attached to the scene graph.
   * If the parameter isActive is false then it will need to be manually activated.
   * @param g
   * @param isActive
   */
  public void addGeomAndWaitUntilAdded(Geom g, boolean isActive)
  {
    addGeom(g);

    while (g.isAttached == false)
    {
      Utils.sleep(30); //could make this less I guess
    }

    g.isActive = isActive;
  }

//	public void addGeom(Geom g, long baseNano, 
//					List<Long> timesMSs)
//  {
//      g.isActive = false;
//			g.parent = this;
//			geoms.add(g);
//
//	  BehaviorIsActive bia = BehaviorIsActive.activateBetweenMillis(g, baseNano, timesMSs);
//		g.attachBehavior(bia);
//  }
//	
  /**
   * Adds a child Geom to this parent Geom. By default the Geom is immediately activated.
   * @param g The child Geom being added.
   */
//  public void addGeom(Geom g)
//  {
//      geoms.add(g);
//      g.parent = this;
//			g.isActive = true;
//  }
//  
  /**
   * adds a child Geom to this parent Geom object, and optionally flagging it to be immediately activated.
   * @param g
   * @param isActive
   */
//  public void addGeom(Geom g, boolean isActive)
//  {
//      geoms.add(g);
//      g.parent = this;
//      g.isActive = isActive;
//  }
//	
  /**
   * removeGeom flags the Geom to be deactivated and removed from its parent during the next display loop.
   * @param g
   */
  public void removeGeom(Geom g)
  {
    g.isActive = false;
    g.isDone = true;
  }

  public void clearGeoms()
  {
    //synchronized(geoms)
    {
      for (Geom g : geoms)
      {
        g.parent = null;
      }
      geoms.clear();
    }
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void deActivate()
  {
    this.isActive = false;
    for (int g = 0; g < this.geoms.size(); g++)
    {
      this.geoms.get(g).deActivate();
    }
  }

  /*
  public void setPos(Point3d p3d)
  {
    Point3f p3f = MatrixUtils.toPoint3f(p3d);
    anchor.set(p3f);
    this.x = p3f.x;
    this.y = p3f.y;
    this.z = p3f.z;
  }
  */

  public void setPos(Point3f p3f)
  {
    anchor.set(p3f);
    this.x = p3f.x;
    this.y = p3f.y;
    this.z = p3f.z;
  }

  public void setPos(Point3f p3f, float w, float h)
  {
    anchor.set(p3f);
    this.x = p3f.x;
    this.y = p3f.y;
    this.z = p3f.z;
    this.w = w;
    this.h = h;
  }

  public void setPos(float x, float y, float z)
  {
    anchor.set(x, y, z);
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void setPos(float x, float y, float z, float w, float h)
  {
    anchor.set(x, y, z);
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    this.h = h;
  }

  public Colorf getColor()
  {
    return new Colorf(this.r, this.g, this.b, this.a);
  }

  public void setColor(Colorf c)
  {
    this.r = c.r;
    this.g = c.g;
    this.b = c.b;
    this.a = c.a;
  }

  public void setColor(float r, float g, float b, float a)
  {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public void setColor(float r, float g, float b)
  {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = 1f;
  }

  public void detachBehavior(Behavior b)
  {
    //synchronized(behaviors)
    {
      behaviors.remove(b);
    }
  }

  //old version... remove later...
  public void attachBehavior(Behavior b)
  {
    //synchronized(behaviors)
    {
      behaviors.add(b);
    }
  }
//  
//  public void attachBehavior(GeomUpdater b)
//  {
//    //synchronized(behaviors)
//    {
//      behaviors.add(b);
//    }
//  }
//  

  /** attach a Data object of some sort to this Geom */
  /*
  public void attachData(Data d)
  {
  synchronized(datas)
  {
  datas.add(d);
  }
  }
   */
  /** detach a Data object of some sort to this Geom */
  /*
  public void detachData(Data d)
  {
  synchronized(datas)
  {
  datas.remove(d);
  }
  }
   */
  ////replace this with dot product code!!!
  public static float getAngleBetweenGeoms(Geom g1, Geom g2)
  {
    //just 2d angle, not solid angle
    float ny = g1.anchor.y - g2.anchor.y;
    float nx = g1.anchor.x - g2.anchor.x;

    return (float) Math.atan2(ny, nx);
  }

  //replace this with Euclidian distance code!!!
  public static float getDistanceBetweenGeoms(Geom g1, Geom g2)
  {
    //just 2d distance // now 3d
    float ny = g1.anchor.y - g2.anchor.y;
    float nx = g1.anchor.x - g2.anchor.x;
    float nz = g1.anchor.z - g2.anchor.z;

    return (float) Math.sqrt(ny * ny + nx * nx + nz * nz);
  }

  public static Point3f getNormalVectorBetweenGeoms(Geom g1, Geom g2)
  {
    //just 2d distance // now 3d
    float ny = g1.anchor.y - g2.anchor.y;
    float nx = g1.anchor.x - g2.anchor.x;
    float nz = g1.anchor.z - g2.anchor.z;

    float lngt = getDistanceBetweenGeoms(g1, g2);
    if (lngt == 0)
    {
      //System.out.println("was zero");
      return new Point3f(0, 0, 0);
    }
    else
    {
      return new Point3f(nx / lngt, ny / lngt, nz / lngt);
    }

  }

  /*
  public static Point3f scalePoint(Point3f anch, Point3f scale, Point3f vert)
  {
  // in direction x

  float nrmDistX = vert.x - anch.x;
  float scaledDistX = scale.x * nrmDistX;
  float scaledPosX = anch.x + scaledDistX;

  float nrmDistY = vert.y - anch.y;
  float scaledDistY = scale.y * nrmDistY;
  float scaledPosY = anch.y + scaledDistY;

  float nrmDistZ = vert.z - anch.z;
  float scaledDistZ = scale.z * nrmDistZ;
  float scaledPosZ = anch.z + scaledDistZ;

  return new Point3f(scaledPosX, scaledPosY, scaledPosZ);
  }
   */
  @Deprecated
  public boolean checkBinding()
  {
    if (isReady == false)
    {
      return false;
    }

    if (isBound == false)
    {
      bind();
    }

    return true;
  }

  //will figure this out later!
  @Deprecated
  public void bind() //bind textureData to texture
  {
    //normalized_height = maxSize;
    //normalized_width = (float) ((float)textureData.getWidth() / (float)textureData.getHeight()) * maxSize;

    texture = TextureIO.newTexture(textureData);

    //normalizeSize();

    isBound = true;

  //textureData.dispose();
  //textureData = null;
  }

  public GeomPoint getRotateAnchor()
  {
    return rotateAnchor;
  }

  public void setRotateAnchor(GeomPoint rotateAnchor)
  {
    this.rotateAnchor = rotateAnchor;
  }

  public void setRotateAnchor(Point3f rotateAnchorPt)
  {
    this.rotateAnchor = new GeomPoint(rotateAnchorPt);
    //since we can attach behaviors to this point it needs to be added to scene graph!
    //addGeom(this.rotateAnchor);
  }

  public void setScaleAnchor(Point3f scaleAnchor)
  {
    this.scaleAnchor = scaleAnchor;
  }


  public void determineRotateAnchor(RotateEnum re)
  {
    //should make these do something! prob make the default behave like GeomRect, and then have special subclasses override
  }

  public void determineScaleAnchor(ScaleEnum re)
  {
    //should make these do something! prob make the default behave like GeomRect, and then have special subclasses override
  }

  /*
   * Sets an arbitrary point to be rotated around,
   * automatically takes care of centering based on scaleAnchor (which must already be defined).
   * Additionally, the rotateAnchor is added to the scene graph so that
   * behaviors can be attached to it (ie behaviors that translate position).
   * Attach behaviors by grabbing it from the Geom itself (rotateAnchor.anchor).
   */
  public void determineRotateAnchor(Point3f ra)
  {
    //this.rotateAnchor = new GeomPoint(ra.x - scaleAnchor.x, ra.y - scaleAnchor.y, ra.z - scaleAnchor.z);
    this.rotateAnchor = new GeomPoint(ra.x, ra.y, ra.z);
    //since we can attach behaviors to this point it needs to be added to scene graph!
    addGeom(this.rotateAnchor);
  }

  public void determineScaleAnchor(Point3f scaleAnchor)
  {
    this.scaleAnchor = scaleAnchor;
  }

  public static List<Point3f> getAnchorPointsForGeoms(List<Geom> gs)
  {
    List<Point3f> p3fs = new ArrayList<Point3f>();

    for (Geom g : gs)
    {
      p3fs.add(g.anchor);
    }

    return p3fs;
  }

  /* changes the coordinates of the Geom so that it center will be on a specified point */
  public void centerGeom(Point3f centerPt)
  {
    this.anchor.x = centerPt.x - (this.w * .5f);
    this.anchor.y = centerPt.y - (this.h * .5f);
    this.anchor.z = centerPt.z - (this.d * .5f);
  }

  @Override
  public String toString()
  {
    String str = "Geom : anchorPoint = " + anchor;
    return str;
  }

  /** these "action" methods SHOULD be overridden! */
  public void clickAction(MouseEvent e)
  {
    //System.out.println("Geom superclass clickAction : I shouldn't be here... i should be with my children!");
    System.out.println("you clicked a " + getClass());
  }

  public void doubleClickAction(MouseEvent e)
  {
    //System.out.println("Geom superclass doubleClickAction : I shouldn't be here... i should be with my children!");
  }

  public void releaseAction(MouseEvent e)
  {
    //System.out.println("Geom superclass releaseAction : I shouldn't be here... i should be with my children!");
  }

  public void mouseOverAction(MouseEvent e)
  {
    //System.out.println("Geom superclass releaseAction : I shouldn't be here... i should be with my children!");
  }

  public final void handleClick(MouseEvent e)
  {
    if (clickableObject != null)
    {
      clickableObject.clickAction(e);
    }
  }

  public final void handleDoubleClick(MouseEvent e)
  {
    //System.out.println("in Geom:handleDoubleClick()");
    if (clickableObject != null)
    {
      clickableObject.doubleClickAction(e);
    }
  }

  public final void handleRelease(MouseEvent e)
  {
    if (releasableObject != null)
    {
      releasableObject.releaseAction(e);
    }
  }

  public final void handleMouseOver(MouseEvent e)
  {
    if (mouseoverableObject != null)
    {
      mouseoverableObject.mouseOverAction(e);
    }

  }
  /** Use this if you want a different object to be "selected" when this obejct is picked
  by the mouse. For instance, if you draw a GeomCircle as a boundary around some GeomPhoto,
  and that GeomCircle is a child of the GeomPhoto, then you probably want the actual GeomPhoto
  to be selected instead of the GeomCircle when the GeomCircle is picked. Otherwise you are just moving
  the boundary around! */
  public final void registerSelectableObject(Geom g)
  {
    if (g == null)
    {
      isSelectable = false;
    }
    selectableObject = g;
  }

  public final void registerDraggableObject(Geom g)
  {
    draggableObject = g;
  }

  public final void registerClickableObject(Geom g)
  {
    if (g == null)
    {
      isSelectable = false;
    }

    clickableObject = g;
  }

  public final void registerReleasableObject(Geom g)
  {
    releasableObject = g;
  }

  public final void registerMouseoverableObject(Geom g)
  {
    mouseoverableObject = g;
  }

  /**
   * registerObject is a convenice method for registering *all* aspects
   * of an particular object to this object at once. For example,
   * if a title (GeomText) is attached to a GeomImage, we might actually want 
   * every single mouse action on the text to apply to the GeomImage instead.
   * For more complicated behaviors, you can register different actions to 
   * different objects using the registerXxxxableObject methods. 
   * 
   * ex.: 
   * GeomImage gi = new GeomImage("file:myphoto.txt");
   * gi.clickAction
   * GeomText gt = new GeomText(0f,0f,0f,1f,1f,"a title");
   * gt.registerObject(gi);
   * 
   * In this example, now any mouse event received on the GeomText object will look to the 
   * the action methods defined in the GeomImage. Picking and dragging will 
   * likewise be done on the GeomImage rather than the GeomText.
   * 
   * By default, a Geom is registered to itself.
   */
  public final void registerObject(Geom g)
  {
    if (g == null)
    {
      isSelectable = false;
    }

    selectableObject = g;
    draggableObject = g;
    clickableObject = g;
    releasableObject = g;
  }

  /**
   * Reduces the visual prominence of this node and its children.
   */
  public void fade()
  {
    Colorf c = getColor();
    c.a *= 0.5f;
    setColor(c);
    if (state == null)
    {
      state = new State();
    }
    state.BLEND = true;
    for (Geom myg : geoms)
    {
      myg.fade();
    }
  }
  /**
   * Increases the visual prominence of this node.
   */
  public boolean isBold;

  public Point3f getMinAnchor()
  {
    Point3d geomPt_wc;
    Point3d geomPt = new Point3d(anchor.x, anchor.y, anchor.z);
    Point3d worldPt = MatrixUtils.getGeomPointInWorldCoordinates(geomPt, modelview, RendererJogl.modelviewMatrix);
    Point3f worldPtf = new Point3f((float) worldPt.x, (float) worldPt.y, (float) worldPt.z);
    Point3f min = BehaviorismDriver.renderer.projectPoint(worldPtf, RendererJogl.modelviewMatrix);

    for (Geom g : geoms)
    {
      Point3f gmin = g.getMinAnchor();
      if (gmin.x < min.x)
      {
        min.x = gmin.x;
      }
      if (gmin.y < min.y)
      {
        min.y = gmin.y;
      }
      if (gmin.z < min.z)
      {
        min.z = gmin.z;
      }
    }
    return min;
  }

  public Point3f getMaxAnchor()
  {
    Point3d geomPt_wc;
    Point3d geomPt = new Point3d(anchor.x, anchor.y, anchor.z);
    Point3d worldPt = MatrixUtils.getGeomPointInWorldCoordinates(geomPt, modelview, RendererJogl.modelviewMatrix);
    Point3f worldPtf = new Point3f((float) worldPt.x, (float) worldPt.y, (float) worldPt.z);
    Point3f max = BehaviorismDriver.renderer.projectPoint(worldPtf, RendererJogl.modelviewMatrix);
    for (Geom g : geoms)
    {
      Point3f gmax = g.getMaxAnchor();
      if (gmax.x > max.x)
      {
        max.x = gmax.x;
      }
      if (gmax.y > max.y)
      {
        max.y = gmax.y;
      }
      if (gmax.z > max.z)
      {
        max.z = gmax.z;
      }
    }
    return max;
  }

  public Point3f getCentroid()
  {
    int nchildren = countChildren();
    Point3f sum = sumChildAnchors();
    sum.x /= nchildren;
    sum.y /= nchildren;
    sum.z /= nchildren;
    return sum;
  }

  private int countChildren()
  {
    int nchildren = 1;
    for (Geom g : geoms)
    {
      nchildren += g.countChildren();
    }
    return nchildren;
  }

  private Point3f sumChildAnchors()
  {
    Point3d geomPt_wc;
    Point3d geomPt = new Point3d(anchor.x, anchor.y, anchor.z);
    Point3d worldPt = MatrixUtils.getGeomPointInWorldCoordinates(geomPt, modelview, RendererJogl.modelviewMatrix);
    Point3f worldPtf = new Point3f((float) worldPt.x, (float) worldPt.y, (float) worldPt.z);
    Point3f sum = BehaviorismDriver.renderer.projectPoint(worldPtf, RendererJogl.modelviewMatrix);
    for (Geom g : geoms)
    {
      Point3f sum1 = g.sumChildAnchors();
      sum.x += sum1.x;
      sum.y += sum1.y;
      sum.z += sum1.z;
    }
    return sum;
  }

  public void dispose()
  {
    if (texture != null)
    {
      //System.out.println("disposing texture... ");
      texture.dispose();
      texture = null;
    }
    if (textureData != null)
    {
      textureData.flush();
      textureData = null;
    }
  }
}
