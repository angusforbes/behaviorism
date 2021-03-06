/*
 * RenderJogl.java
 * Created on April 14, 2007, 2:07 AM
 */
package behaviorism.renderers;

import behaviorism.handlers.FontHandler;
import behaviorism.Behaviorism;
import behaviorism.data.Data;
import behaviorism.geometry.GeomPoint;
import behaviorism.handlers.KeyboardHandler;
import behaviorism.handlers.MouseHandler;
import behaviorism.renderers.cameras.Cam;
import behaviorism.textures.TextureManager;
import behaviorism.utils.RenderUtils;
import behaviorism.worlds.World;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.gl2.GLUT;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
//import javax.media.opengl.GL;
import javax.media.opengl.DebugGL2;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLUnurbs;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.gl2.GLUgl2;
import javax.swing.JApplet;
import org.grlea.log.SimpleLogger;
import static behaviorism.utils.MatrixUtils.*;
import static behaviorism.utils.RenderUtils.*;
import static behaviorism.utils.Utils.*;

public class Renderer implements GLEventListener
{
  public Map<World, Boolean> worlds = new ConcurrentHashMap<World, Boolean>();
  public World currentWorld = null;
  //public SceneGraph sceneGraph = null;
  public static GLUT glut;
  public static GLUgl2 glu;
  public GL2 gl;
  public Animator animator;
  public TessellationCallback tessellationCallback = null;
  public GLUtessellator tessellationObject = null;
  public GLUnurbs nurbsRenderer = null;
  public GLUquadric quadricRenderer = null;
  public Cam cam = null;
  public static double frustum[][] = null;
  public static Rectangle2D.Float screenBounds = null;//don't need this one, just use viewport
  public static Rectangle2D.Float screenBoundsInWorldCoords = null;
  public static List<GeomPoint> worldBoundaryPoints = null;
  public static boolean boundsHaveChanged = true;
  public AtomicBoolean togglingFullScreen = new AtomicBoolean(false);
  public  boolean isInstalled = false;
  private static final Renderer instance = new Renderer();
  //hack for cell tango 2009
  public List<Data> texturesToDispose = new CopyOnWriteArrayList<Data>();
  public AtomicBoolean isDisposing = new AtomicBoolean(false);
  public static final SimpleLogger log = new SimpleLogger(Renderer.class);

  /**
   * Gets (or creates then gets) the singleton Renderer object.
   * @return the singleton Renderer
   */
  public static Renderer getInstance()
  {
    return instance;
  }

  private Renderer()
  {
  }

  public void installWorld(World world)
  {
    installWorld(world, true, true);
  }


  public void installWorld(World world, JApplet applet)
  {
    if (!worlds.keySet().contains(world))
    {
      worlds.put(world, true);
    }

    currentWorld = world;

    this.cam = currentWorld.cam;

    boundsHaveChanged = true;

    System.err.println("in installWorld : applet");
//    while (isInstalled == false) //we need to wait until the reshape method is called via the gl context.
//    {
//      System.err.println("in installWorld applet, waiting to be installed...");
//      sleep(1000);
//    }
  }

  public void installWorld(World world, boolean isActive, boolean isCurrent)
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

    while (isInstalled == false) //we need to wait until the reshape method is called via the gl context.
    {
      sleep(10);
    }
  }

  public void activateWorld(World world, boolean isCurrent)
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
    cam.projection();
    cam.view();

    //extractFrustum();
    //System.out.println("leaving setPerspective3D()...");
  }

  /**
   * Switches to an orthographic projection.
   */
  public void setPerspective2D()
  {
    gl.glMatrixMode(gl.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluOrtho2D(0, cam.viewport[2], cam.viewport[3], 0);

    gl.glMatrixMode(gl.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  private boolean isReady(GLAutoDrawable drawable)
  {
    if (Behaviorism.getInstance().doneShutdown.get() == true) //then we are in the process of closing the openGL context
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

    //make sure we have the right GL context
    //gl = drawable.getGL();
    gl = drawable.getGL().getGL2();

    /*** THIS IS REAL!! TURN BACK ON!! ***/
    //Behaviorism.getInstance().setCursor(); //make sure we have the proper cursor

    return true;
  }

  //no reason for this to be in scene graph... move it here or to renderutils.
  private void processDebugs()
  {
    RenderUtils.getSceneGraph().drawDebuggingInfo();
  }

  private void processInputs()
  {
    //TO DO: have a list of attached handlers...
    
    if (Behaviorism.getInstance().useMouse == true)
    {
      MouseHandler.getInstance().processMouse();
    }
    KeyboardHandler.getInstance().processKeyboard();
  }

  private void clearScreen()
  {
    gl.glClearColor(
      currentWorld.color.r,
      currentWorld.color.g,
      currentWorld.color.b,
      currentWorld.color.a); //background color of world
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  private void shutdown()
  {
    log.entry("in shutdown() : cleaning up resources... ");

    for (World w : worlds.keySet())
    {
      log.info("cleaning up world " + w.getClass());
      w.cleanUp();
      log.info("cleaned up world " + w.getClass());
    }

    log.info("disposing of all textures");
    TextureManager.getInstance().disposeTextures();

    log.info("we have disposed of all resources... ");
    Behaviorism.getInstance().doneShutdown.set(true);
    log.entry("out shutdown()");
  }

  public void display(GLAutoDrawable drawable)
  {
   // log.entry("in display()...");
   
    if (!isReady(drawable))
    {
      return;
    }

    clearScreen();

    FontHandler.getInstance().installFonts();
    TextureManager.getInstance().updateTextures();

    RenderUtils.getSceneGraph().draw();

//    try
//    {
//    Screenshot.writeToFile(new File("savedImages/blah_" + System.currentTimeMillis() + ".jpg"),
//      cam.viewport[2], cam.viewport[3]);
//    }
//    catch(Exception e)
//    {
//      e.printStackTrace();
//    }

    processInputs();

    processDebugs();

    //set this false here so that geoms that need to recalc if bounds have changed will know
    //that screen has been reshaped.
    boundsHaveChanged = false;

    if (Behaviorism.getInstance().isShutdown.get() == true)
    {
      shutdown();
    }
  }

  public void init(GLAutoDrawable drawable)
  {
    System.err.println("in Renderer init!");
    drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
    this.gl = drawable.getGL().getGL2();

    System.out.println(
      "GL v" + gl.glGetString(GL_VERSION) + ", " +
      "GLSL v" + gl.glGetString(GL_SHADING_LANGUAGE_VERSION));

    glu = new GLUgl2();
    glut = new GLUT();

    /** TURN ON THESE TO INIT LIGHT, need to make a better lighting strategy **/
//    gl.glEnable(gl.GL_LIGHT0);					// Enable Default Light (Quick And Dirty)	( NEW )
//    gl.glEnable(gl.GL_LIGHTING);				// Enable Lighting				( NEW )
//    gl.glEnable(gl.GL_COLOR_MATERIAL);				// Enable Coloring Of Material			( NEW )
    gl.glShadeModel(gl.GL_SMOOTH);                              // Enable Smooth Shading
    //gl.glShadeModel(gl.GL_FLAT);

    gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[]
      {
        0.2f, 0.2f, 0.2f, 1f
      }, 0);
    gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[]
      {
        //0f, 0f, 2f, 1f //REAL
        2f, 2f, 2f, 1f
      }, 0);

    // setup the material properties
    //gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
    gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, new float[]
      {
        .6f, .6f, .6f, 1.0f
      }, 0);
    gl.glMaterialfv(GL_FRONT, GL_SPECULAR, new float[]
      {
        1f, 1f, 1f, 1.0f
      }, 0);
    gl.glMaterialfv(GL_FRONT, GL_SHININESS, new float[]
      {
        50f
      }, 0);


    //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
    //gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);

    //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
    //gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
    gl.glDepthFunc(GL_LEQUAL);							//Type of Depth test
    //gl.glDepthFunc(GL.GL_LESS);							//Type of Depth test

    gl.glEnable(GL_DEPTH_TEST);  //this breaks my ATI card?

    gl.glEnable(GL_POINT_SMOOTH);
    gl.glEnable(GL_LINE_SMOOTH);
    gl.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);	// Make round points, not square points
    gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);		// Antialias the lines

    gl.glEnable(GL_AUTO_NORMAL);

    //gl.glDepthMask(false); //may have to futz with for overlays!
    gl.glDepthMask(true); //may have to futz with for overlays!


    // 0 = do *not* synch with refresh rate (ie fast as possible), 1 = synch with refresh rate (ie always 60fps)
    gl.setSwapInterval(1); //0

    
    //animator = new FPSAnimator(glDrawable, 60, false);
    //animator = new FPSAnimator(glDrawable, 5, true);
    //animator = new Animator(glDrawable);
    animator = new Animator(drawable);
    //animator.setRunAsFastAsPossible(true);
    animator.setRunAsFastAsPossible(false);

    animator.start();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
  {
    System.err.println("in reshape() : RESHAPE IS BEING CALLLED : " + x + "/" +y + "/" +width + "/" +height);
    Renderer.screenBounds = new Rectangle2D.Float(0, 0, width, height);

    System.err.print("in reshape() : CHECKING CAM...");
    if (cam != null)
    {
      System.err.println(" ... it is available");
      cam.setViewport(0, 0, width, height);
      cam.setAspectRatio(width, height);
      setPerspective3D();

      setBoundaries();

      isInstalled = true;
    }
    else
    {
      System.err.println(" ... it is null!");
    }

  }

//  @Override
//  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
//  {
//  }
  //We may need to call this manaully in the case of appelts... test this out...
  public void setBoundaries()
  {
    Point3f lowerleft = toPoint3f(
      rayIntersect(currentWorld, 0, (int) screenBounds.getHeight(), new Point3d()));
    Point3f upperright = toPoint3f(
      rayIntersect(currentWorld, (int) screenBounds.getWidth(), 0, new Point3d()));

    screenBoundsInWorldCoords = new Rectangle2D.Float(
      lowerleft.x, lowerleft.y, upperright.x - lowerleft.x, upperright.y - lowerleft.y);

    if (worldBoundaryPoints == null)
    {
      worldBoundaryPoints = new ArrayList<GeomPoint>();
      addTo(worldBoundaryPoints, new GeomPoint(), new GeomPoint(), new GeomPoint(), new GeomPoint());
    }

    worldBoundaryPoints.get(0).setTranslate(lowerleft.x, lowerleft.y, 0f);
    worldBoundaryPoints.get(1).setTranslate(upperright.x, lowerleft.y, 0f);
    worldBoundaryPoints.get(2).setTranslate(upperright.x, upperright.y, 0f);
    worldBoundaryPoints.get(3).setTranslate(lowerleft.x, upperright.y, 0f);

    boundsHaveChanged = true;

  }

  public void dispose(GLAutoDrawable arg0)
  {
    log.entry("in dispose()");

    if (Behaviorism.isApplet == true)
    {
      //hmm...
     System.err.println("in dispose() for an applet... what to do?");
    }
    else
    {
    log.info("should stuff be disposed of here?");
    shutdown();
    log.info("goodbye!!!");
    log.exit("out dispose()");
    System.err.println("done disposing...");

    //System.err.println("APPLET TEST... not exiting...");

    System.exit(0);
    }
  }
}


