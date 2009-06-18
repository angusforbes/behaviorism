/*
 * RenderJogl.java
 * Created on April 14, 2007, 2:07 AM
 */
package renderers;

import renderers.cameras.Cam;
import behaviorism.BehaviorismDriver;
import handlers.MouseHandler;
import handlers.KeyboardHandler;
import geometry.GeomPoint;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import handlers.FontHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import utils.MatrixUtils;
import utils.RenderUtils;
import utils.Utils;
import worlds.WorldGeom;

public class RendererJogl implements GLEventListener
{

  public Map<WorldGeom, Boolean> worlds = new ConcurrentHashMap<WorldGeom, Boolean>();
  public WorldGeom currentWorld = null;
  public VizGeom viz = null;
  public static GLUT glut;
  public static GLU glu;
  public GL gl;
  public GLAutoDrawable glDrawable;
  public Animator animator;
  public TessellationCallBack tessellationCallback = null;
  public GLUtessellator tessellationObject = null;
  public GLUnurbs nurbsRenderer = null;
  public GLUquadric quadricRenderer = null;
  public Cam cam = null;
  private FontHandler fontHandler = FontHandler.getInstance();
  /**
   * modelviewMatrix is the modelview *after* the camera has been positioned.
   * We are also calling this the "World" modelview (eg, in MatrixUtils).
   */
  public static double[] modelviewMatrix = new double[16];
  /**
   * projectionMatrix holds is the current project
   */
  public static double[] projectionMatrix = new double[16];
  /**
   * viewportBounds holds the current viewport bounds (x, y, w, h)
   */
  public static int viewportBounds[] = new int[4];
  //public static float nearPlane = .001f; //1f
  public static float nearPlane = 1f;
  public static float farPlane = 100f;
  public static double frustum[][] = null;
  public static Rectangle2D.Float screenBounds = null;
  public static Rectangle2D.Float screenBoundsInWorldCoords = null;
  public static List<GeomPoint> worldBoundaryPoints = null;
  public static boolean boundsHaveChanged = true;

  /** Sets the camera explicitly. By default we use a {@link soi3.CamBasic} with the camera located 
   * at (0, 0, -10) and pointing toward the origin.
   * @param cam An instance of a {@link soi3.Cam}.
   */
  public void installWorld(WorldGeom world)
  {
    installWorld(world, true, true);
  }

  public void installWorld(WorldGeom world, boolean isActive, boolean isCurrent)
  {
    if (!worlds.keySet().contains(world))
    {
      if (isCurrent == true)
      {
        worlds.put(world, true);
      }
      else
      {
        worlds.put(world, isActive);
      }
    }

    if (isCurrent == true)
    {
      currentWorld = world;
    }

    this.cam = currentWorld.cam;
    boundsHaveChanged = true;
  }

  public void activateWorld(WorldGeom world, boolean isCurrent)
  {
    worlds.put(world, true);

    if (isCurrent == true)
    {
      currentWorld = world;
    }
  }

  public Cam getCamera()
  {
    return this.cam;
  }

  /**
   * Sets up the modelviewMatrix for the
   * current world after positioning the camera.
   */
  public void setPerspective3D()
  {
    if (cam == null)
    {
      System.out.println("cam is null...");
      return;
    }

    projectionMatrix = MatrixUtils.perspective(
      cam.fovy,
      ((float) BehaviorismDriver.canvasWidth) / BehaviorismDriver.canvasHeight,
      RendererJogl.nearPlane,
      RendererJogl.farPlane);

    modelviewMatrix = cam.perspective();

    if (BehaviorismDriver.renderer.currentWorld != null && boundsHaveChanged == true)
    {
      //System.out.println("boundsHaveChanged!!!");
      Point3f lowerleft = MatrixUtils.toPoint3f(
        RenderUtils.rayIntersect(BehaviorismDriver.renderer.currentWorld, 0, (int) screenBounds.getHeight(), new Point3d()));
      Point3f upperright = MatrixUtils.toPoint3f(
        RenderUtils.rayIntersect(BehaviorismDriver.renderer.currentWorld, (int) screenBounds.getWidth(), 0, new Point3d()));

      screenBoundsInWorldCoords = new Rectangle2D.Float(
        lowerleft.x, lowerleft.y, upperright.x - lowerleft.x, upperright.y - lowerleft.y);


      worldBoundaryPoints.get(0).setPos(lowerleft.x, lowerleft.y, 0f);
      worldBoundaryPoints.get(1).setPos(upperright.x, lowerleft.y, 0f);
      worldBoundaryPoints.get(2).setPos(upperright.x, upperright.y, 0f);
      worldBoundaryPoints.get(3).setPos(lowerleft.x, upperright.y, 0f);
    }

  //extractFrustum();
  //System.out.println("leaving setPerspective3D()...");
  }

  //this is only being called by GeomText2... rethink...
  @Deprecated //delete me soon! (being used by GeomText2 and GeomTextPath
  public void resetPerspective3D()
  {
    projectionMatrix = MatrixUtils.perspective(cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);
    modelviewMatrix = cam.resetPerspective();
  }

  /**
   * Switches to an orthographic projection.
   */
  public void setPerspective2D()
  {
    gl.glMatrixMode(gl.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluOrtho2D(0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight, 0);

    gl.glMatrixMode(gl.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  private boolean isReady(GLAutoDrawable drawable)
  {
    if (BehaviorismDriver.doneShutdown.get() == true) //then we are in the process of closing the openGL context
    {
      return false;
    }

    if (currentWorld == null) //check if its been setup...
    {
      return false;
    }

    if (cam == null)
    {
      return false;
    }

    return true;
  }

  private void processDebugs()
  {
    BehaviorismDriver.viz.drawDebuggingInfo(gl);
  }

  private void processHandlers()
  {
    //TO DO: have a list of attached handlers...
    MouseHandler.processMouse();
    KeyboardHandler.processKeyboard();
  }

  private void clear()
  {
    gl.glClearColor(currentWorld.r, currentWorld.g, currentWorld.b, currentWorld.a); //background color of world
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  }

  private void shutdown()
  {
    System.err.println("in RendererJogl : cleaning up resources... ");
    for (WorldGeom w : worlds.keySet())
    {
      w.cleanUp();
    }
    System.err.println("in RendererJogl : we have disposed of all resources... ");
    BehaviorismDriver.doneShutdown.set(true);
  }

  @Override
  public void init(GLAutoDrawable drawable)
  {
    //init worldBoundaryPoints with 4 blank points...
    worldBoundaryPoints = new ArrayList<GeomPoint>();
    Utils.addTo(worldBoundaryPoints, new GeomPoint(), new GeomPoint(), new GeomPoint(), new GeomPoint());

    glDrawable = drawable;
    gl = glDrawable.getGL();
    glDrawable.setGL(new DebugGL(gl));
    glu = new GLU();
    glut = new GLUT();

    //print out version info..
    System.out.println("GLSL version = " + gl.glGetString(GL.GL_SHADING_LANGUAGE_VERSION));

    //gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                    // Black Background
    gl.glClearColor(currentWorld.r, currentWorld.g, currentWorld.b, currentWorld.a);                    // Black Background

//    gl.glEnable(gl.GL_LIGHT0);					// Enable Default Light (Quick And Dirty)	( NEW )
//    //gl.glEnable(gl.GL_LIGHTING);				// Enable Lighting				( NEW )
    gl.glEnable(gl.GL_COLOR_MATERIAL);				// Enable Coloring Of Material			( NEW )
    gl.glShadeModel(gl.GL_SMOOTH);                              // Enable Smooth Shading
    //gl.glShadeModel(gl.GL_FLAT);

    gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[]
      {
        0.2f, 0.2f, 0.2f, 1f
      }, 0);
    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]
      {
        0f, 0f, 2f, 1f
      }, 0);

    // setup the material properties
    //gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, new float[]
      {
        .6f, .6f, .6f, 1.0f
      }, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, new float[]
      {
        1f, 1f, 1f, 1.0f
      }, 0);
    gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, new float[]
      {
        50f
      }, 0);


    //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
    //gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);

    //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
    //gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
    gl.glDepthFunc(GL.GL_LEQUAL);							//Type of Depth test
    //gl.glDepthFunc(GL.GL_LESS);							//Type of Depth test

    gl.glEnable(GL.GL_DEPTH_TEST);  //this breaks my ATI card?

    gl.glEnable(GL.GL_POINT_SMOOTH);
    gl.glEnable(GL.GL_LINE_SMOOTH);
    gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);	// Make round points, not square points
    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);		// Antialias the lines

    gl.glEnable(GL.GL_AUTO_NORMAL);

    //gl.glDepthMask(false); //may have to futz with for overlays!
    gl.glDepthMask(true); //may have to futz with for overlays!

    //set up tesselation callbacks/objects - used by VizGeom.drawGeomPoly(...)
    tessellationCallback = new TessellationCallBack(gl, glu);
    tessellationObject = glu.gluNewTess();
    glu.gluTessCallback(tessellationObject, GLU.GLU_TESS_VERTEX, tessellationCallback);// vertexCallback);
    glu.gluTessCallback(tessellationObject, GLU.GLU_TESS_BEGIN, tessellationCallback);// beginCallback);
    glu.gluTessCallback(tessellationObject, GLU.GLU_TESS_END, tessellationCallback);// endCallback);
    glu.gluTessCallback(tessellationObject, GLU.GLU_TESS_ERROR, tessellationCallback);// errorCallback);
    glu.gluTessCallback(tessellationObject, GLU.GLU_TESS_COMBINE, tessellationCallback);// combineCallback);

    //set up global nurbs renderer
    nurbsRenderer = glu.gluNewNurbsRenderer();
    quadricRenderer = glu.gluNewQuadric();

    // 0 = do *not* synch with refresh rate (ie fast as possible), 1 = synch with refresh rate (ie always 60fps)
    gl.setSwapInterval(1); //0

    //animator = new FPSAnimator(glDrawable, 60, false);
    //animator = new FPSAnimator(glDrawable, 5, true);
    animator = new Animator(glDrawable);
    animator.setRunAsFastAsPossible(true);
    //animator.setRunAsFastAsPossible(false);

    animator.start();
  }

  @Override
  public void display(GLAutoDrawable drawable)
  {
    if (!isReady(drawable))
    {
      return;
    }

    gl = drawable.getGL();

    clear();

    //i don't like this being here...
    if (fontHandler.changeFonts.get() == true)
    {
      fontHandler.nextFont(fontHandler.fontIndex);
    }

    BehaviorismDriver.viz.draw(gl);

    //fontHandler.fontsReady.set(false);

    processHandlers();
    processDebugs();
    //gl.glFlush(); //is this necessary?

    //set this false here so that geoms that need to recalc if bounds have changed will know
    //that screen has been reshaped.
    boundsHaveChanged = false;


    if (BehaviorismDriver.isShutdown.get() == true)
    {
      shutdown();
    }
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int xstart, int ystart, int width, int height)
  {
    height = (height == 0) ? 1 : height;

    BehaviorismDriver.canvasWidth = width;
    BehaviorismDriver.canvasHeight = height;
    viewportBounds = new int[]
      {
        0, 0, width, height
      };

    RendererJogl.screenBounds = new Rectangle2D.Float(0, 0, width, height);

    boundsHaveChanged = true;

    setPerspective3D();
  }

  @Override
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
  {
  }
}


