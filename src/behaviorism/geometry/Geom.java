/* Geom.java - Created on July 12, 2007, 7:52 PM */
package behaviorism.geometry;

import behaviorism.behaviors.Behavior;
import behaviorism.behaviors.geom.BehaviorActivateGeom;
import behaviorism.behaviors.geom.BehaviorRemoveGeom;
import behaviorism.data.Node;
import behaviorism.handlers.MouseHandler;
import behaviorism.renderers.State;
import behaviorism.textures.TextureImage;
import behaviorism.utils.MatrixUtils;
import behaviorism.utils.RenderUtils;
import behaviorism.utils.Utils;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import static behaviorism.utils.MatrixUtils.*;
import static behaviorism.utils.RenderUtils.*;
import static behaviorism.utils.Utils.*;

public abstract class Geom
{

  /**
   * A name for this Geom. Does not need to be unique.
   */
  public String name = null;
  /**
   * A unique id for this Geom.
   */
  public String id = null; //not currently used...
  /**
   * A List of all child Geoms attached to this Geom.
   */
  public List<Geom> geoms = new CopyOnWriteArrayList<Geom>();
  /**
   * A List of all Behaviors that are affecting this Geom.
   */
  public List<Behavior> behaviors = new CopyOnWriteArrayList<Behavior>();
  /**
   * A root Data node that can hold various types of Data attached to this Geom.
   * This should be NULL by default, only used if needed!!!
   */
  public Node data = null; //new Data();
  //used for centering, etc, without changing the modelview
  public Point3f translateAnchor = new Point3f(0f, 0f, 0f);
  public Point3f translate = new Point3f(0f, 0f, 0f);
  /**
   * The relative point (in parent's coordinates) around which the object is to rotate.
   * By default it is set to be the lower left corner of the object
   */
  public Point3f rotateAnchor = new Point3f(0f, 0f, 0f); //new GeomPoint(.5f, .5f, 0f);
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
   * Point which describes the scaling factor of the width, height, and depth dimensions of the object.
   * By default each dimension is set to a factor of 1.
   */
  public Point3d scale = new Point3d(1, 1, 1);
  //thinking about this...
  //if rotateAnchor is relative to current Geom - Then -
  //  update modelview with translate
  //  then update modelview with rotateAnchor, rotate, then update modelview with -rotateAnchor
  //if rotateAnchor is relative to parent Geom - Then -
  //  put the translation AFTER the rotation
  //
  //same with scale setTranslate...
  //these should be both set to TRUE as the defaults.
  boolean rotateRelative = true;
  boolean scaleRelative = true;
  //protected float w = 1f;
  //protected float h = 1f;
  //protected float d = 0f;
  //make this protected and add proper setters!
  public float w = 1f;
  public float h = 1f;
  public float d = 0f;
  public Colorf color = new Colorf();
  @Deprecated
  public float area = 0f; //i think i was using this for an picking alogrithm. it was a dumb idea. this shouldn't be stored.
  //used if need for polygon offset in 2D
  public float offset = 0f;
  //various flags for rendering, texturing, picking, timing, etc.
  public boolean isTransformed = true; //if true, we will update the transformation matrix
  public boolean isActive = true; //if true, it will be displayed
  public boolean isDone = false; //if true, it will be removed
  public boolean isTextured = false; //if true, we will handle texture coords
  public boolean isReady = false; //ie, has texture been loaded/generated?
  public boolean isBound = false; //ie, has texture been bound to GL
  public boolean isSelectable = false; //ie, can it be selected by a mouseClick?
  public boolean isVisible = true; //ie, is it currently within the view frustum? (if no, can't be picked)
  public boolean isCalculated = false; //ie, is it attached to the scenegraph (has it been drawn once?)
  //public TextureData textureData = null;
  //public Texture texture = null;
  public List<TextureImage> textures = null;
  /**
   * A 4x4 double array representing the Geom's openGL modelview matrix within the scenegraph hierarchy.
   */
  public double[] modelview = getIdentity();
  //public Matrix4d modelview2 = new Matrix4d(MatrixUtils.getIdentity());
  //public double[] modelview3 = MatrixUtils.getIdentity(); //(MatrixUtils.getIdentity());
  //.setIdentity();
  /**
   * An object representing the current set of openGL states of this Geom.
   * If null, the renderer will automatically inheret it's parent's states
   * (ie, either the parent Geom, or the current world iteself).
   */
  private State state = null;
  public boolean hasState = false;
  public int layerNum = 0;
  //indicates which Geom mouse actions we fire when the MouseHandler finds a mouse event on this Geom.
  //The default value is simply to use itself.
  public Geom draggableObject = this;
  public Geom selectableObject = this;
  public Geom pressableObject = this;
  public Geom clickableObject = this;
  public Geom releasableObject = this;
  public Geom hoverableObject = this;
  public Geom enterableObject = this;
  public Geom exitableObject = this;
  /**
   * The parent of this Geom. Is either the Geom it is attached to, or null if the Geom is attached directly to the current world.
   * This is used by the MouseHandler to determine object selection, etc.
   */
  public Geom parent = null;

  public Geom()
  {
  }

  /**
   * Constructs a new Geom initially placed at the world coordinates
   * unprojected from pixel coordinates where (0,0) is the top left corner.
   * The depth value
   * is calculated based on the position of the World z value.
   * @param x
   * @param y
   */
  public Geom(int x, int y)
  {
    setTranslate(pixelToWorld(x, y));
  }

  /**
   * Constructs a new Geom initially placed at the Geom coordinates
   * unprojected from pixel coordinates where (0,0) is the bottom left corner
   * of the reference Geom. The depth value is calculated based on the (0,0)
   * position of this reference Geom. Note that we don't actually have to
   * attach this Geom to the indicated reference, we are just using it
   * as a reference to position the Geom.
   * @param upperLeft
   * @param x
   * @param y
   */
  public Geom(boolean upperLeft, int x, int y)
  {
    if (upperLeft == true)
    {
      setTranslate(pixelToWorld(x, y));
    }
    else
    {
      int www = RenderUtils.getViewport()[2];
      int hhh = RenderUtils.getViewport()[3];

      int pxX = x + www / 2;
      int pxY = y + hhh / 2;

      setTranslate(
        pixelToWorld(pxX, pxY));
    }
  }

  public Geom(float x, float y, float z)
  {
    setTranslate(x, y, z);
  }

  public Geom(Point3f p3f)
  {
    setTranslate(p3f);
  }

  public void setState(State state)
  {
    this.state = state;
    this.hasState = true;// (this.state != null);
  }

  /**
   * Gets the current State object. If State is null, then it creates the default State.
   * (perhaps we should create a copy of the Layer state... except if it hasn't been added
   * to the SceneGraph then we won't have a layer...)
   * @return
   */
  public State getState()
  {
    if (this.state == null)
    {
      this.state = new State();
      this.hasState = true;
    }
    return this.state;
  }

  public void setWidth(int w)
  {
    System.out.println("PIXEL ");

    int www = RenderUtils.getViewport()[2];
    int hhh = RenderUtils.getViewport()[3];

    this.w = pixelToWorld(
      www / 2 + w,
      hhh / 2 - 0).x;

    System.out.println("width 1 = " + this.w);


  }

  public void setWidth(float w)
  {
    this.w = w;
  }

  public void setHeight(float h)
  {
    this.h = h;
  }

  public void setDepth(float d)
  {
    this.d = d;
  }

  public void setDimensions(float w, float h, float d)
  {
    this.w = w;
    this.h = h;
    this.d = d;
  }

  public void setDimensions(float w, float h)
  {
    this.w = w;
    this.h = h;
    this.d = 0f;
  }

  /**
   * Called by the scenegraph for objects that need an openGL context. For instance, GeomText
   * needs to figure out its width using the TextRenderer. Leave empty if you don't need it.
   */
  public void calculate()
  {
  }

  /**
   * Instructions for the Geom to draw itself within the openGL context. Called during each frame of the openGL display loop.
   * gl and glu define the openGL context. offset is a tiny value that can be used to ensure that there are no rendering errors
   * on objects that overlap on the same plane.
   * @param gl
   * @param glu
   * @param offset
   */
  //public abstract void draw(GL gl, GLU glu, float offset);
  public abstract void draw();

  //have the sceneGraph set the offset manually only if necessary, rather than passing
  //it in with every frame.
  public float getOffset()
  {
    return this.offset;
  }

  public void setOffset(float offset)
  {
    this.offset = offset;
  }

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

  //is this being used??
  @Deprecated
  public void toggleIsVisible()
  {
    this.isVisible = !this.isVisible;
  }

  /*
  //Hmm-- is this useful here? It is not being used anywhere...
  @Deprecated
  public void drawDebugGeom(GL gl, GLU glu)
  {
  gl.glDisable(gl.GL_BLEND);
  if (rotateAnchor != null)
  {
  gl.glPointSize(10f);
  gl.glColor4f(0f, 1f, 0f, 1f);
  gl.glBegin(gl.GL_POINTS);

  //gl.glVertex3f(0f, 0f, 0f);
  gl.glVertex3f(rotateAnchor.x, rotateAnchor.y, rotateAnchor.z);

  //      gl.glVertex3f(translate.x + rotateAnchor.translate.x,
  //      translate.y + rotateAnchor.translate.y,
  //      translate.z + rotateAnchor.translate.z);

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
   */
  /**
   * Translates a point in this Geom's coordinates into world coordinates.
   * Just a convenience method that calls MatrixUtils.getGeomPointInWorldCoordinates() with appropriate parameters.
   * @param geomPt
   * @return the point in world coordinates
   */
  //seems useful, but it's not being used
  public Point3f geomPointToWorldPoint(Point3f geomPt)
  {
    return toPoint3f(
      //MatrixUtils.getGeomPointInWorldCoordinates(MatrixUtils.toPoint3d(geomPt), modelview, RendererJogl.modelviewMatrix));
      getGeomPointInWorldCoordinates(toPoint3d(geomPt), modelview, getCamera().modelview));

  }

  /**
   * To be overridden if the object is selectable and the depth test = false so that the object can be picked.
   * Basically you draw an invisible object with the depth test = true.
   * @param gl
   */
  public void drawPickingBackground()
  {
  }

  public void transform()
  {
    if (!isTransformed)
    {
      return; //UNCOMMENT THIS
    }

    //System.err.println("in transform for Geom of type " + getClass());
    if (parent == null)
    {
      System.err.println("is transform() : WHY IS PARENT NULL??????");
      return;
    }

    if (parent.modelview == null)
    {
      System.err.println("is transform() : WHY IS TRANSFORM NULL??????");
      return;
    }

    /* System.arrayCopy is slightly faster that Arrays.copyOf, allegedly. */
    System.arraycopy(parent.modelview, 0, modelview, 0, 16);

    if (rotateRelative == true && scaleRelative == true)
    {
      transformTranslate();
      transformRotate();
      transformScale();
    }
    else if (rotateRelative == true && scaleRelative == false)
    {
      transformScale();
      transformTranslate();
      transformRotate();
    }
    else if (rotateRelative == false && scaleRelative == true)
    {
      transformRotate();
      transformTranslate();
      transformScale();
    }
    else if (rotateRelative == false && scaleRelative == false)
    {
      transformRotate();
      transformScale();
      transformTranslate();
    }
  }

  private void transformTranslate()
  {
    if (translate.x == 0f &&
      translate.y == 0f &&
      translate.z == 0f)
    {
      return;
    }

    modelview = MatrixUtils.translate(modelview, translate.x, translate.y, translate.z);
  }

  //private void transformRotate()
  public void transformRotate()
  {
    if (rotate.x == 0f && rotate.y == 0f && rotate.z == 0f)
    {
      return;
    }

    if (rotateAnchor != null)
    {
      //modelview = MatrixUtils.translate(modelview, rotateAnchor.translate.x, rotateAnchor.translate.y, rotateAnchor.translate.z);
      modelview = MatrixUtils.translate(modelview, rotateAnchor.x, rotateAnchor.y, rotateAnchor.z);
      modelview = MatrixUtils.rotate(modelview, rotate.x, 1f, 0f, 0f);
      modelview = MatrixUtils.rotate(modelview, rotate.y, 0f, 1f, 0f);
      modelview = MatrixUtils.rotate(modelview, rotate.z, 0f, 0f, 1f);
      //modelview = MatrixUtils.translate(modelview, -rotateAnchor.translate.x, -rotateAnchor.translate.y, -rotateAnchor.translate.z);
      modelview = MatrixUtils.translate(modelview, -rotateAnchor.x, -rotateAnchor.y, -rotateAnchor.z);
    }
  }

  private void transformScale()
  {
    if (scale.x == 1f && scale.y == 1f && scale.z == 1f)
    {
      return;
    }

    modelview = MatrixUtils.translate(modelview, scaleAnchor.x, scaleAnchor.y, scaleAnchor.z);
    modelview = MatrixUtils.scale(modelview, scale.x, scale.y, scale.z);
    modelview = MatrixUtils.translate(modelview, -scaleAnchor.x, -scaleAnchor.y, -scaleAnchor.z);
  }

  /**
   * Transforms the Geoms's modelview matrix according to its translate, rotate, and scale parameters.
   * This object is final and thus cannot be overriden. It is called by the Renderer during each display loop.
   * Actually there are reasons to override it..., now it is non-final again... (see GeomClosestLineBetweenPolys).
   * @param gl
   * @param glu
   */

  /*
  public void transform(GL gl, GLU glu)
  {
  System.out.println("in transform() old-- WHY?");
  // translate command
  double[] temp = new double[16];
  //gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, temp, 0);
  //System.out.println("modelview was :");
  //MatrixUtils.printDoubleArray(temp);

  gl.glTranslatef(translate.x, translate.y, translate.z);

  //gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, temp, 0);
  //System.out.println("modelview is :");
  //MatrixUtils.printDoubleArray(temp);

  // rotate commands
  if (rotateAnchor != null)
  {
  gl.glTranslatef(rotateAnchor.translate.x, rotateAnchor.translate.y, rotateAnchor.translate.z);
  gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
  gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
  //System.out.println("rotate y = " + (float)rotate.y);
  gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);
  gl.glTranslatef(-rotateAnchor.translate.x, -rotateAnchor.translate.y, -rotateAnchor.translate.z);
  }

  // scale commands
  gl.glTranslatef(scaleAnchor.x, scaleAnchor.y, scaleAnchor.z);
  gl.glScalef((float) scale.x, (float) scale.y, (float) scale.z);
  gl.glTranslatef(-scaleAnchor.x, -scaleAnchor.y, -scaleAnchor.z);
  }
   */
  //MOVE THESE somewhere!!! only used in a couple of places...

  /*
  public void scale(GL gl)
  {
  //ScaleEnum se = scaleDirection;
  //determineScaleAnchor(ScaleEnum.NE);
  //determineScaleAnchor(ScaleEnum.CENTER);

  gl.glTranslatef(scaleAnchor.x, scaleAnchor.y, scaleAnchor.z);
  gl.glScalef((float) scale.x, (float) scale.y, (float) scale.z);
  gl.glTranslatef(-scaleAnchor.x, -scaleAnchor.y, -scaleAnchor.z);

  //gl.glScalef((float)scale.x, (float)scale.y, (float)scale.z);
  //scaleDirection = se;
  }

  public void translate(GL gl, float tx, float ty, float tz)
  {
  gl.glTranslatef(tx, ty, tz);
  }
   */
  public Point3f getCenter()
  {
    //default behavior... obviously needs to be overwritten by almost every Geom.
    return new Point3f(w * .5f, h * .5f, d * .5f);
  }

  /**
   * This version of addGeom activates the Geom a specified number of milliseconds in the future.
   * It is simply a convience method to avoid having to define a BehaviorActivateGeom behavior since
   * it is such a common thing to need to do.
   * @param g
   * @param millisInFuture
   */
  public void addGeom(Geom g, long millisInFuture)
  {
    addGeomToLayer(g, false, 0);

    BehaviorActivateGeom.activateAtMillis(g, now(), millisInFuture);
  }

  public void addGeomToLayer(Geom g, long millisInFuture, int layerNum)
  {
    addGeomToLayer(g, false, layerNum);

    BehaviorActivateGeom.activateAtMillis(g, System.nanoTime(), millisInFuture);
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
   * It is simply a convience method to avoid having to define a BehaviorActivateGeom behavior since
   * it is such a common thing to need to do.
   * @param g
   * @param millisInFuture
   */
  public void addGeom(Geom g, long baseNano, long millisInFuture)
  {
    addGeomToLayer(g, false, 0);

    BehaviorActivateGeom.activateAtMillis(g, baseNano, millisInFuture);
  }

  //wha?
  public void addGeomActivateChildrenFalse(Geom g, long baseNano, long millisInFuture)
  {
    addGeomToLayer(g, false, 0);

    BehaviorActivateGeom bia = BehaviorActivateGeom.activateAtMillis(g, baseNano, millisInFuture);
    bia.updateChildren = false;
  }

  public void addGeomToLayer(Geom g, long baseNano, long millisInFuture, int layerNum)
  {
    addGeomToLayer(g, false, layerNum);

    BehaviorActivateGeom.activateAtMillis(g, baseNano, millisInFuture);
  }

  /*
  public void addGeom(Geom g, long baseNano,
  List<Long> timesMSs)
  {
  addGeomToLayer(g, false, 0);

  BehaviorActivateGeom.activateBetweenMillis(g, baseNano, timesMSs);
  }

  public void addGeomToLayer(Geom g, long baseNano, List<Long> timesMSs, int layerNum)
  {
  addGeomToLayer(g, false, layerNum);

  BehaviorActivateGeom.activateBetweenMillis(g, baseNano, timesMSs);
  }
   */
  /**
   * Adds a Geom to this World. By default the Geom is immediately activated.
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
   * It is simply a convience method to avoid having to define a BehaviorActivateGeom behavior since
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
   * It is simply a convience method to avoid having to define a BehaviorActivateGeom behavior since
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
//	  BehaviorActivateGeom bia = BehaviorActivateGeom.activateAtMillis(g, baseNano, millisInFuture);
//		//g.attachBehavior(bia);
//  }
//
//  public void addGeomToLayer(Geom g, long baseNano, long millisInFuture, int layerNum)
//  {
//    addGeomToSceneGraph(g, this.geoms, false, this);
//    BehaviorismDriver.renderer.currentWorld.addGeomToRendererLayer(g, layerNum);
// 
//    BehaviorActivateGeom bia = BehaviorActivateGeom.activateAtMillis(g, baseNano, millisInFuture);
//  }
//  
  public void addGeomToLayer(Geom g, boolean isActive, int layerNum)
  {
    getWorld().addGeomToSceneGraph(g, this.geoms, isActive, this);
    getWorld().addGeomToRendererLayer(g, layerNum);
  }

  /**
   * This version of addGeom waits until the Geom has been drawn once (so that the modelview is updated correctly)
   * before returning. If the parameter isVisible is true the Geom will be activated only after is has been
   * attached to the scene graph and the initial calculate() method has been called for the geom.
   * If the parameter isVisible is false then it will need to be manually set to be visible.
   * @param g
   * @param isVisible
   */
  public void addGeomAndWaitUntilCalculated(final Geom g, final boolean isVisible)
  {
    g.isVisible = false;
    addGeom(g);

    while (g.isCalculated == false)
    {
      Utils.sleep(10); //could make this less I guess
    }

    g.isVisible = isVisible;
  }
//	public void addGeom(Geom g, long baseNano, 
//					List<Long> timesMSs)
//  {
//      g.isActive = false;
//			g.parent = this;
//			geoms.add(g);
//
//	  BehaviorActivateGeom bia = BehaviorActivateGeom.activateBetweenMillis(g, baseNano, timesMSs);
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
    BehaviorRemoveGeom.removeAtMillis(g, now(), 0L);

    // g.isActive = false;
    // g.isDone = true;
  }

  public void removeGeom(Geom g, long millisInFuture)
  {
    BehaviorRemoveGeom.removeAtMillis(g, now(), millisInFuture);

    // g.isActive = false;
    // g.isDone = true;
  }

  public void removeGeom(Geom g, long nano, long millisInFuture)
  {
    BehaviorRemoveGeom.removeAtMillis(g, nano, millisInFuture);

    // g.isActive = false;
    // g.isDone = true;
  }

  public void removeGeoms(Collection<Geom> geoms)
  {
    for (Geom g : geoms)
    {
      removeGeom(g);
    }
  }

  public void clearGeoms()
  {
    //synchronized(geoms)
    {
      for (Geom g : geoms)
      {
        removeGeom(g);
        //      g.parent = null;
      }
      //   geoms.clear();
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

  public void translate(float x, float y, float z)
  {
    translateX(x);
    translateY(y);
    translateZ(z);
  }

  public void translateX(float x)
  {
    if (x != 0f)
    {
      //translate.setX(translate.x + x);
      translate.set(translate.x + x, translate.y, translate.z);

      isTransformed = true;
    }
  }

  public void translateY(float y)
  {
    if (y != 0f)
    {
      //translate.setY(translate.y + y);
      translate.set(translate.x, translate.y + y, translate.z);
      isTransformed = true;
    }
  }

  public void translateZ(float z)
  {
    if (z != 0f)
    {
      //translate.setZ(translate.z + z);
      translate.set(translate.x, translate.y, translate.z + z);
      isTransformed = true;
    }
  }

  public void setTranslate(Point3f p3f)
  {
    if (!this.translate.equals(p3f))
    {
      translate.set(p3f);
      isTransformed = true;
    }
  }

  public void setTranslate(float x, float y, float z)
  {
    if (x != translate.x || y != translate.y || z != translate.z)
    {
      translate.set(x, y, z);
      isTransformed = true;
    }
  }

  public void scale(float x, float y, float z)
  {
    scaleX(x);
    scaleY(y);
    scaleZ(z);
  }

  public void scaleX(float x)
  {
    if (x != 0f)
    {
      //scale.setX(scale.x + x);
      scale.set(scale.x + x, scale.y, scale.z);
      isTransformed = true;
    }
  }

  public void scaleY(float y)
  {
    if (y != 0f)
    {
      //scale.setY(scale.y + y);
      scale.set(scale.x, scale.y + y, scale.z);
      isTransformed = true;
    }
  }

  public void scaleZ(float z)
  {
    if (z != 0f)
    {
      //scale.setZ(scale.z + z);
      scale.set(scale.x, scale.y, scale.z + z);
      isTransformed = true;
    }
  }

  public void setScale(float x, float y, float z)
  {
    if (x != scale.x || y != scale.y || z != scale.z)
    {
      scale.set(x, y, z);
      isTransformed = true;
    }
  }

  public void setScale(Point3d p3d)
  {
    if (!this.scale.equals(p3d))
    {
      scale.set(p3d);
      isTransformed = true;
    }
  }

  public void rotateAxis(Vector3f axis, float angle)
  {

  }

  public void rotate(float x, float y, float z)
  {
    rotateX(x);
    rotateY(y);
    rotateZ(z);
  }

  public void rotateX(float x)
  {
    if (x != 0f)
    {
      //rotate.setX(rotate.x + x);
      rotate.set(rotate.x + x, rotate.y, rotate.z);
      isTransformed = true;
    }
  }

  public void rotateY(float y)
  {
    if (y != 0f)
    {
      //rotate.setY(rotate.y + y);
      rotate.set(rotate.x, rotate.y + y, rotate.z);
      isTransformed = true;
    }
  }

  public void rotateZ(float z)
  {
    if (z != 0f)
    {
      //rotate.setZ(rotate.z + z);
      rotate.set(rotate.x, rotate.y, rotate.z + z);
      isTransformed = true;
    }
  }

  public void setRotate(float x, float y, float z)
  {
    if (x != rotate.x || y != rotate.y || z != rotate.z)
    {
      rotate.set(x, y, z);
      isTransformed = true;
    }
  }

  public void setRotate(Point3d p3d)
  {
    if (!this.rotate.equals(p3d))
    {
      rotate.set(p3d);
      isTransformed = true;
    }
  }

  public Colorf getColor()
  {
    //return new Colorf(this.r, this.g, this.b, this.a);
    return color;
  }

  public void setColor(Colorf c)
  {
    this.color = c;
//    this.r = c.r;
//    this.g = c.g;
//    this.b = c.b;
//    this.a = c.a;
  }

  public void setColor(float r, float g, float b, float a)
  {
    this.color = new Colorf(r, g, b, a);
//    this.r = r;
//    this.g = g;
//    this.b = b;
//    this.a = a;
  }

  public void setColor(float r, float g, float b)
  {
    this.color = new Colorf(r, g, b, 1f);
//    this.r = r;
//    this.g = g;
//    this.b = b;
//    this.a = 1f;
  }

  /*
  public void detachBehavior(Behavior b)
  {
  //synchronized(behaviors)
  {
  behaviors.remove(b);
  }
  }

  //old version... remove later... (LATER) huh? what's old about this???
  public void attachBehavior(Behavior b)
  {
  //synchronized(behaviors)
  {
  behaviors.add(b);
  }
  }
   */
  /**
   * Handles initialization and update of textures.
   * If textureData is not yet available, return false.
   * If textureData is loaded, but texture not yet create, create texture.
   * If textureData has changed, update the texture.
   * @return true if all textures are ready, false otherwise.
   */
  // final public boolean updateTextures()
  //{
//    if (this.textures == null)
//    {
//      return true;
//    }
//
//    for (TextureImage ti : this.textures)
//    {
//      if (!ti.updateTexture())
//      {
//        return false;
//      }
//    }
  //return false; //true;
  // }
  final public void bindTexture(int which)
  {
    this.textures.get(which).texture.bind();
  }

  /**
   * shouldn't ever have to use this!
   * @param which
   * @return
   */
  final public int getTextureId(int which)
  {
    return this.textures.get(which).texture.getTextureObject();
  }

  final public void attachTexture(TextureImage ti)
  {
    if (textures == null)
    {
      //System.out.println("in attachTexture : texures is null");
      //textures = new ArrayList<TextureImage>();
      textures = new CopyOnWriteArrayList<TextureImage>();
    }

    textures.add(ti);
    //System.out.println("There are " + textures.size() + " attached to this Geom " + this);
    //ti.attachedGeoms.add(this);
  }

  final public void detachTexture(TextureImage ti)
  {
    textures.remove(ti);
    //ti.attachedGeoms.remove(this);
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
  //The following 3 methods should be removed. The same functionality is in GeomUtils!
  @Deprecated
  public static float getAngleBetweenGeoms(Geom g1, Geom g2)
  {
    //just 2d angle, not solid angle
    float ny = g1.translate.y - g2.translate.y;
    float nx = g1.translate.x - g2.translate.x;

    return (float) Math.atan2(ny, nx);
  }

  //replace this with Euclidian distance code!!!
  @Deprecated
  public static float getDistanceBetweenGeoms(Geom g1, Geom g2)
  {
    //just 2d distance // now 3d
    float ny = g1.translate.y - g2.translate.y;
    float nx = g1.translate.x - g2.translate.x;
    float nz = g1.translate.z - g2.translate.z;

    return (float) Math.sqrt(ny * ny + nx * nx + nz * nz);
  }

  @Deprecated
  public static Point3f getNormalVectorBetweenGeoms(Geom g1, Geom g2)
  {
    //just 2d distance // now 3d
    float ny = g1.translate.y - g2.translate.y;
    float nx = g1.translate.x - g2.translate.x;
    float nz = g1.translate.z - g2.translate.z;

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
  /*
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
   */

  /*
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
   */
  public void rotateAnchor(float x, float y, float z)
  {
    rotateAnchorX(x);
    rotateAnchorY(y);
    rotateAnchorZ(z);
  }

  public void rotateAnchorX(float x)
  {
    if (x != 0f)
    {
      //rotateAnchor.setX(rotateAnchor.x + x);
      rotateAnchor.set(rotateAnchor.x + x, rotateAnchor.y, rotateAnchor.z);
      isTransformed = true;
    }
  }

  public void rotateAnchorY(float y)
  {
    if (y != 0f)
    {
      //rotateAnchor.setY(rotateAnchor.y + y);
      rotateAnchor.set(rotateAnchor.x, rotateAnchor.y + y, rotateAnchor.z);
      isTransformed = true;
    }
  }

  public void rotateAnchorZ(float z)
  {
    if (z != 0f)
    {
      //rotateAnchor.setZ(rotateAnchor.z + z);
      rotateAnchor.set(rotateAnchor.x, rotateAnchor.y, rotateAnchor.z + z);
      isTransformed = true;
    }
  }

  public void setRotateAnchor(float x, float y, float z)
  {
    if (x != rotateAnchor.x || y != rotateAnchor.y || z != rotateAnchor.z)
    {
      rotateAnchor.set(x, y, z);
      isTransformed = true;
    }
  }

  public void setRotateAnchor(Point3f p3f)
  {
    System.out.println("in setRotateAnchor : setting to " + p3f);
    if (!this.rotateAnchor.equals(p3f))
    {
      rotateAnchor.set(p3f);
      System.out.println("so now rotateAnchor = " + rotateAnchor);
      isTransformed = true;
    }
  }

  public void scaleAnchor(float x, float y, float z)
  {
    scaleAnchorX(x);
    scaleAnchorY(y);
    scaleAnchorZ(z);
  }

  public void scaleAnchorX(float x)
  {
    if (x != 0f)
    {
      //scaleAnchor.setX(scaleAnchor.x + x);
      scaleAnchor.set(scaleAnchor.x + x, scaleAnchor.y, scaleAnchor.z);
      isTransformed = true;
    }
  }

  public void scaleAnchorY(float y)
  {
    if (y != 0f)
    {
      //scaleAnchor.setY(scaleAnchor.y + y);
      scaleAnchor.set(scaleAnchor.x, scaleAnchor.y + y, scaleAnchor.z);
      isTransformed = true;
    }
  }

  public void scaleAnchorZ(float z)
  {
    if (z != 0f)
    {
      //scaleAnchor.setZ(scaleAnchor.z + z);
      scaleAnchor.set(scaleAnchor.x, scaleAnchor.y, scaleAnchor.z + z);
      isTransformed = true;
    }
  }

  public void setScaleAnchor(float x, float y, float z)
  {
    if (x != scaleAnchor.x || y != scaleAnchor.y || z != scaleAnchor.z)
    {
      scaleAnchor.set(x, y, z);
      isTransformed = true;
    }
  }

  public void setScaleAnchor(Point3f p3f)
  {
    if (!this.scaleAnchor.equals(p3f))
    {
      scaleAnchor.set(p3f);
      isTransformed = true;
    }
  }

  /*
  public void translateAnchor(float x, float y, float z)
  {
  translateAnchorX(x);
  translateAnchorY(y);
  translateAnchorZ(z);
  }

  public void translateAnchorX(float x)
  {
  if (x != 0f)
  {
  translateAnchor.setX(translateAnchor.x + x);
  isTransformed = true;
  }
  }

  public void translateAnchorY(float y)
  {
  if (y != 0f)
  {
  translateAnchor.setY(translateAnchor.y + y);
  isTransformed = true;
  }
  }

  public void translateAnchorZ(float z)
  {
  if (z != 0f)
  {
  translateAnchor.setZ(translateAnchor.z + z);
  isTransformed = true;
  }
  }

  public void setTranslateAnchor(float x, float y, float z)
  {
  if (x != translateAnchor.x || y != translateAnchor.y || z != translateAnchor.z)
  {
  System.out.println("setting translate anchor = " + x + "/" + y + "/" +z);
  translateAnchor.set(x, y, z);
  isTransformed = true;
  }
  }

  public void setTranslateAnchor(Point3f p3f)
  {
  if (!this.translateAnchor.equals(p3f))
  {
  translateAnchor.set(p3f);
  isTransformed = true;
  }
  }
   */
  /*
  public void determineRotateAnchor(RotateEnum re)
  {
  //should make these do something! prob make the default behave like GeomRect, and then have special subclasses override
  }

  public void determineScaleAnchor(ScaleEnum re)
  {
  //should make these do something! prob make the default behave like GeomRect, and then have special subclasses override
  }
   */
  /*
   * Sets an arbitrary point to be rotated around,
   * automatically takes care of centering based on scaleAnchor (which must already be defined).
   * Additionally, the rotateAnchor is added to the scene graph so that
   * behaviors can be attached to it (ie behaviors that translate position).
   * Attach behaviors by grabbing it from the Geom itself (rotateAnchor.translate).
   */
  /*
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
   */

  /* changes the coordinates of the Geom so that it center will be on a specified point */
  //this is silly. put it somwhere else. Maybe in GeomUtils?
  @Deprecated
  public void centerGeom(Point3f centerPt)
  {
    this.translate.x = centerPt.x - (this.w * .5f);
    this.translate.y = centerPt.y - (this.h * .5f);
    this.translate.z = centerPt.z - (this.d * .5f);
  }

  @Override
  public String toString()
  {

    String str = "" + getClass().getSimpleName() + " : ";
    if (name != null)
    {
      str += "name=" + name + " : ";
    }
    if (id != null)
    {
      str += "id=" + id + " : ";
    }

    str += "translate = " + translate;
    return str;
  }

  /** these "action" methods SHOULD be overridden! */
  public void selectedAction(Geom originatingGeom)
  {
  }

  public void unselectedAction(Geom originatingGeom)
  {
  }
  //pressing and holding down

  public void pressAction(Geom originatingGeom)
  {
  }

  public void clickAction(Geom originatingGeom)
  {
    //System.out.println("Geom superclass clickAction : I shouldn't be here... i should be with my children!");
    //System.out.println("you clicked a " + getClass());
  }

  public void doubleClickAction(Geom originatingGeom) //can ignore originatingGeom unless needed
  {
    //System.out.println("Geom superclass doubleClickAction : I shouldn't be here... i should be with my children!");
  }

  public void releaseAction(Geom originatingGeom)
  {
    //System.out.println("Geom superclass releaseAction : I shouldn't be here... i should be with my children!");
  }

  // these three are for hovering without moving
  public void mouseInAction(Geom originatingGeom)
  {
  }

  public void mouseOverAction(Geom originatingGeom)
  {
  }

  public void mouseOutAction(Geom originatingGeom)
  {
  }

  //these three look for mouse movement
  public void mouseMovingAction(Geom originatingGeom)
  {
    //  System.out.println("i am a " + getClass());
    // System.out.println("Geom superclass mouseMovingAction : I shouldn't be here... i should be with my children!");
  }

  public void mouseStoppedMovingAction(Geom originatingGeom)
  {
    //  System.out.println("i am a " + getClass());
    // System.out.println("Geom superclass mouseMovingAction : I shouldn't be here... i should be with my children!");
  }

  public void mouseStartedMovingAction(Geom originatingGeom)
  {
    //  System.out.println("i am a " + getClass());
    // System.out.println("Geom superclass mouseMovingAction : I shouldn't be here... i should be with my children!");
  }

  //combo mouse pressed and mouse moved
  //default is to move/scale/rotate the geom (depending on with button combo is being used).
  //this can be overidden if you want different behavior.
  public void dragAction(Geom originatingGeom)
  {
    MouseHandler.getInstance().dragGeom();
    //System.out.println("you dragged a " + getClass());
  }

  public final void handleSelected()
  {
    if (selectableObject != null)
    {
      selectableObject.selectedAction(this);
    }
  }

  public final void handleUnselected()
  {
    if (selectableObject != null)
    {
      selectableObject.unselectedAction(this);
    }
  }

  public final void handleDrag()
  {
    if (draggableObject != null)
    {
      draggableObject.dragAction(this);
    }
  }

  public final void handleMouseIn()
  {
    if (hoverableObject != null)
    {
      hoverableObject.mouseInAction(this);
    }
  }

  public final void handleMouseOver()
  {
    if (hoverableObject != null)
    {
      hoverableObject.mouseOverAction(this);
    }
  }

  public final void handleMouseOut()
  {
    if (hoverableObject != null)
    {
      hoverableObject.mouseOutAction(this);
    }
  }

  public final void handlePress()
  {
    if (pressableObject != null)
    {
      pressableObject.pressAction(this);
    }
  }

  public final void handleClick()
  {
    if (clickableObject != null)
    {
      clickableObject.clickAction(this);
    }
  }

  public final void handleDoubleClick()
  {
    //System.out.println("in Geom:handleDoubleClick()");
    if (clickableObject != null)
    {
      clickableObject.doubleClickAction(this); //we tell the registered object where the click came from...
    }
  }

  public final void handleRelease()
  {
    if (releasableObject != null)
    {
      releasableObject.releaseAction(this);
    }
  }

  public final void handleMouseStartedMoving()
  {
    if (hoverableObject != null)
    {
      hoverableObject.mouseStartedMovingAction(this);
    }

  }

  public final void handleMouseMoving()
  {
    if (hoverableObject != null)
    {
      hoverableObject.mouseMovingAction(this);
    }

  }

  public final void handleMouseStoppedMoving()
  {
    if (hoverableObject != null)
    {
      hoverableObject.mouseStoppedMovingAction(this);
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

  public final void registerEnterableObject(Geom g)
  {
    enterableObject = g;
  }

  public final void registerExitableObject(Geom g)
  {
    exitableObject = g;
  }

  public final void registerDraggableObject(Geom g)
  {
    draggableObject = g;
  }

  public final void registerClickableObject(Geom g)
  {
    clickableObject = g;
  }

  public final void registerPressableObject(Geom g)
  {
    pressableObject = g;
  }

  public final void registerReleasableObject(Geom g)
  {
    releasableObject = g;
  }

  public final void registerHoverableObject(Geom g)
  {
    hoverableObject = g;
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
  // who uses this???? I think it was from the IGERT demo... should remove or translate it into to the igert project
  /*
  @Deprecated
  public boolean isBold;
  @Deprecated
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
   */
  //TO DO: I don't know who uses these anymore. I think maybe Basak used them??? They shouldn't be here!
  /*
  public Point3f getMinAnchor()
  {
  Point3d geomPt_wc;
  Point3d geomPt = new Point3d(translate.x, translate.y, translate.z);
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
  Point3d geomPt = new Point3d(translate.x, translate.y, translate.z);
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
  Point3d geomPt = new Point3d(translate.x, translate.y, translate.z);
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
   */
  //TO DO - need to fix this to work with list of textures...
  /**
   * Recursively calls the dispose() method this Geom and all attached children.
   * This is called automatically upon application shutdown.
   */
  final public void cleanUp()
  {
    dispose();
    for (Geom geom : geoms)
    {
      geom.cleanUp();
    }
  }

  /**
   * Disposes all special resources assoicated with this Geom. By default
   * this doesn't do anything, but this method should be overwritten
   * by classes that use an external native library, such as an audio library, etc. 
   * Note that normal disposal for textures, etc, is automatically by the cleanUp() method.
   */
  public void dispose()
  {
    if (textures != null)
    {
      textures.clear();
    }

//    {
//      for (TextureImage texture : textures)
//      {
//        texture.dispose();
//      }
//    }
  /*
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
     */
  }
}
