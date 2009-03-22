/*
 * RenderJogl.java
 * Created on April 14, 2007, 2:07 AM
 */
package renderers;

import behaviorism.BehaviorismDriver;
import handlers.MouseHandler;
import handlers.KeyboardHandler;
import geometry.Geom;
import geometry.GeomPoint;
import geometry.GeomPoly;
import geometry.GeomRect;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import handlers.FontHandler;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import shaders.FragmentShader;
import shaders.Program;
import shaders.VertexShader;
import utils.GeomUtils;
import utils.MatrixUtils;
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
  //public static Animator animator;
  public Animator animator;
  public TessellationCallBack tessellationCallback = null;
  public GLUtessellator tessellationObject = null;
  public GLUnurbs nurbsRenderer = null;
  public GLUquadric quadricRenderer = null;
  /** Automatically initialized to a default {@link soi3.CamBasic} unless set explicitly by the World */
  public Cam cam = null;
  private FontHandler fontHandler = FontHandler.getInstance();
  /**
   * modelviewMatrix is the modelview numKnotns*after* the camera has been positioned.
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
  public static float nearPlane = .001f; //1f
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


    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);                    // Black Background
    //gl.glEnable(gl.GL_LIGHT0);					// Enable Default Light (Quick And Dirty)	( NEW )
    //gl.glEnable(gl.GL_LIGHTING);				// Enable Lighting				( NEW )
    //gl.glEnable(gl.GL_COLOR_MATERIAL);				// Enable Coloring Of Material			( NEW )
    //gl.glShadeModel(gl.GL_SMOOTH);                              // Enable Smooth Shading

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

    //Utils.sleep(1000);

    // 0 = do *not* synch with refresh rate (ie fast as possible), 1 = synch with refresh rate (ie always 60fps)
    gl.setSwapInterval(1); //1 

    //animator = new FPSAnimator(glDrawable, 60, false);
    //animator = new FPSAnimator(glDrawable, 5, true);
    animator = new Animator(glDrawable);
    //animator.setRunAsFastAsPossible(true);
    //animator.setRunAsFastAsPossible(false);

    animator.start();
    

  }

  float zoomval = 1f;
  int z_var, xc_var, yc_var;
  float xcval, ycval;
  int textureID = -1;

  public void
drawBox(float size)
{
  float n[][] = new float[][] {
    {-1.0f, 0.0f, 0.0f},
    {0.0f, 1.0f, 0.0f},
    {1.0f, 0.0f, 0.0f},
    {0.0f, -1.0f, 0.0f},
    {0.0f, 0.0f, 1.0f},
    {0.0f, 0.0f, -1.0f}
  };

  int faces[][] = new int[][]
  {
    {0, 1, 2, 3},
    {3, 2, 6, 7},
    {7, 6, 5, 4},
    {4, 5, 1, 0},
    {5, 6, 2, 1},
    {7, 4, 0, 3}
  };

  float v[][] = new float[8][3];
  int i;

  v[0][0] = v[1][0] = v[2][0] = v[3][0] = -size / 2;
  v[4][0] = v[5][0] = v[6][0] = v[7][0] = size / 2;
  v[0][1] = v[1][1] = v[4][1] = v[5][1] = -size / 2;
  v[2][1] = v[3][1] = v[6][1] = v[7][1] = size / 2;
  v[0][2] = v[3][2] = v[4][2] = v[7][2] = -size / 2;
  v[1][2] = v[2][2] = v[5][2] = v[6][2] = size / 2;

  for (i = 5; i >= 0; i--) {
    gl.glBegin(GL.GL_QUADS);
    gl.glNormal3fv(n[i], 0);
    gl.glVertex3fv(v[faces[i][0]], 0);
    gl.glVertex3fv(v[faces[i][1]], 0);
    gl.glVertex3fv(v[faces[i][2]], 0);
    gl.glVertex3fv(v[faces[i][3]], 0);
    gl.glEnd();
  }
}
  //testing the loading of shader
  private int loadAndCompileShader(GL gl, int type, File location) throws IOException
    {
        BufferedReader reader = null;
        try
        {
            //reader = new BufferedReader(new InputStreamReader(location.openStream()));
            reader = new BufferedReader(new FileReader(location));
            ArrayList lineList = new ArrayList(100);
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                lineList.add(line + "\n");
            }
            String[] lines = (String[]) lineList.toArray(new String[lineList.size()]);
            int[] lengths = new int[lines.length];
            for (int i = 0; i < lines.length; i++)
            {
                lengths[i] = lines[i].length();
            }
            int shader = gl.glCreateShader(type);
            gl.glShaderSourceARB(shader, lines.length, lines, lengths, 0);

            gl.glCompileShaderARB(shader);

            // Check for compile errors
            String errors = null;
		if ((errors = getGLErrorLog(gl, shader)) != null)
			throw new RuntimeException("Compile error\n" + errors);

            //String error = getGLErrorLog(gl, shader);
            String error = "some error...";

            int[] compileStatus = {0};
            gl.glGetObjectParameterivARB(shader, GL.GL_OBJECT_COMPILE_STATUS_ARB, compileStatus, 0);
            if (compileStatus[0] == 0)
            {
              throw new IllegalArgumentException("Shader could not be compiled! " + (error == null ? "" : error));
            }
            return shader;
        }
        finally
        {
            if (reader != null) try{ reader.close(); } catch(Exception ignoreSunsInsanity){}
        }
    }

  // Checks for arbitrary GL errors. Could also be accomplished by enabling the DebugGL pipeline
	private String getGLError(GL gl)
	{
		boolean hasError = false;
		String message = "";
		for (int glErr = gl.glGetError(); glErr != GL.GL_NO_ERROR; glErr = gl.glGetError())
		{
			message += (hasError ? "\n" : "") + glu.gluErrorString(glErr);
			hasError = true;
		}
		return hasError ? message : null;
	}

	// Checks the info log for compile/link errors
	private String getGLErrorLog(GL gl, int obj)
	{
		boolean hasError = false;
		int[] infologLength = {0};
		int[] charsWritten = {0};
		byte[] infoLog;

		String message = "";
		String error = getGLError(gl);
		if (error != null)
		{
			message += error;
			hasError = true;
		}

		gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, infologLength, 0);
		error = getGLError(gl);
		if (error != null)
		{
			message += (hasError ? "\n" : "") + error;
			hasError = true;
		}

		if (infologLength[0] > 1)
		{
			infoLog = new byte[infologLength[0]];
			gl.glGetInfoLogARB(obj, infologLength[0], charsWritten, 0, infoLog, 0);
			message += (hasError ? "\n" : "") + "InfoLog:\n" + new String(infoLog);
			hasError = true;
		}
		error = getGLError(gl);
		if (error != null)
		{
			message += (hasError ? "\n" : "") + error;
			hasError = true;
		}
		return hasError ? message : null;
	}

  public void loadWorldIdentity()
  {
    gl.glLoadIdentity();
  }

  /**
   * setPerspective3D sets up the modelviewMatrix for the 
   * current world after positioning the camera
   */
  public void setPerspective3D()
  {
    if (cam == null)
    {
      return;
    }
    
    //gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
   
    if (VizGeom.EXPLICITLY_CALCULATE_MODELVIEW == true)
    {
      projectionMatrix = MatrixUtils.perspective(cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);
      modelviewMatrix = cam.perspective();
    }
    else
    {
      gl.glMatrixMode(gl.GL_PROJECTION);

      gl.glLoadIdentity();
      glu.gluPerspective(cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);

      gl.glMatrixMode(gl.GL_MODELVIEW);
      gl.glLoadIdentity();

      cam.setPerspective(gl, glu);

      //store openGL information
      gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projectionMatrix, 0);
      gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelviewMatrix, 0);
    }
    if (BehaviorismDriver.renderer.currentWorld != null && boundsHaveChanged == true)
    {
      System.out.println("boundsHaveChanged!!!");
      Point3f lowerleft = MatrixUtils.toPoint3f(rayIntersect(BehaviorismDriver.renderer.currentWorld, 0, (int) screenBounds.getHeight(), new Point3d()));
      Point3f upperright = MatrixUtils.toPoint3f(rayIntersect(BehaviorismDriver.renderer.currentWorld, (int) screenBounds.getWidth(), 0, new Point3d()));

      screenBoundsInWorldCoords = new Rectangle2D.Float(
        lowerleft.x, lowerleft.y, upperright.x - lowerleft.x, upperright.y - lowerleft.y);


      worldBoundaryPoints.get(0).setPos(lowerleft.x, lowerleft.y, 0f);
      worldBoundaryPoints.get(1).setPos(upperright.x, lowerleft.y, 0f);
      worldBoundaryPoints.get(2).setPos(upperright.x, upperright.y, 0f);
      worldBoundaryPoints.get(3).setPos(lowerleft.x, upperright.y, 0f);

      boundsHaveChanged = false;
    }

  //extractFrustum();
  //System.out.println("leaving setPerspective3D()...");
  }

  //this is only being called by geomtext... rethink...
  @Deprecated //delete me soon! (being used by GeomText2 and GeomTextPath
  public void resetPerspective3D()
  {

    if (VizGeom.EXPLICITLY_CALCULATE_MODELVIEW == true)
    {
      projectionMatrix = MatrixUtils.perspective(cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);
      modelviewMatrix = cam.resetPerspective();
    }
    else
    {
    //gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
    gl.glMatrixMode(gl.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);

    gl.glMatrixMode(gl.GL_MODELVIEW);
    gl.glLoadIdentity();

    cam.resetPerspective(gl, glu);
    }
  }

  /**
   * setPerspective2D switches to orthographic projection
   * (eg, for printing debug text and points).
   */
  public void setPerspective2D()
  {
    //gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
    gl.glMatrixMode(gl.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluOrtho2D(0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight, 0);

    gl.glMatrixMode(gl.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  @Override
  public void display(GLAutoDrawable drawable)
  {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    if (currentWorld == null)
    {
      return;
    }

    if (cam == null)
    {
      return;
    }

    gl = drawable.getGL();

    if (fontHandler.changeFonts.get() == true)
    {
      fontHandler.nextFont(fontHandler.fontIndex);
    }
 
    BehaviorismDriver.viz.draw(gl, glu);

    //fontHandler.fontsReady.set(false);

    MouseHandler.processMouse();
    KeyboardHandler.processKeyboard();

    BehaviorismDriver.viz.drawDebuggingInfo(gl);
    //gl.glFlush(); //is this necessary?
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int xstart, int ystart, int width, int height)
  {
    height = (height == 0) ? 1 : height;

    BehaviorismDriver.canvasWidth = width;
    BehaviorismDriver.canvasHeight = height;
    viewportBounds = new int[]{0,0,width,height};

    RendererJogl.screenBounds = new Rectangle2D.Float(0, 0, width, height);

    boundsHaveChanged = true;

    setPerspective3D();
  }

  @Override
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
  {
  }

  /** 
   * getWorldCoordsForScreenCoord transforms a mouse point (in screen coordinates) 
   * into "world" coordinates (ie, the coordinates after the camera has been positioned).
   *
   * //assumes you are picking on an object with some depth 
   * //otherwise uses far edge of clipping plane (ie z=100f) 
   * //which obviously distorts true world coords.
   * (TO DO-- add a check for this!)
   * 
   * @param x
   * @param y
   * @return - a double array holding the x, y, z world coordinate of the screen point
   */
  public double[] getWorldCoordsForScreenCoord(int x, int y)
  {
    double modelview[] = new double[16];
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double worldCoords[] = new double[3];

    if (VizGeom.EXPLICITLY_CALCULATE_MODELVIEW)
    {
      projection= MatrixUtils.perspective(cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);
      modelview = cam.perspective();
      viewport = viewportBounds;
      //gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);
    }
    else
    {
    gl.glLoadIdentity();

    setPerspective3D();

    gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);
    }
    //invert y value properly
    y = (int) ((float) viewport[3] - (float) y);


    //get z value from scene
    FloatBuffer zBuf = FloatBuffer.allocate(1);
    gl.glReadPixels(x, (int) y, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zBuf);
    float z = zBuf.get();
    //System.out.println("z depth = " + z);

    //System.out.println("z calcs = " + z2);
    //unproject mouse coords into world coords!
    glu.gluUnProject((double) x, (double) y, (double) z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      worldCoords, 0);

    return worldCoords;
  }

  /**
   * (currently not being used... 
   * the z variable is between 0 and 1 (the near plane and the far plane!) 
   */
  public Point3d getWorldCoordsForScreenCoord(int x, int y, double z)
  {
    gl.glPushMatrix();
    double modelview[] = new double[16];
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double worldCoords[] = new double[3];

    setPerspective3D();

    gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);

    //invert y value properly
    y = (int) ((float) viewport[3] - (float) y);

    //unproject mouse coords into world coords!
    glu.gluUnProject((double) x, (double) y, (double) z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      worldCoords, 0);
    gl.glPopMatrix();
    return new Point3d(worldCoords[0], worldCoords[1], worldCoords[2]);

  }

  /**
   * (only being used by printFrustrum...) 
   * the z variable is between 0 and 1 (the near plane and the far plane!) 
   */
  public Point3f getWorldCoordsForScreenCoord(int x, int y, double z, double[] modelview)
  {
    //double modelview[] = new double[16];
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double worldCoords[] = new double[3];

    //gl.glLoadIdentity();
    //setWorldCoords(gl, glu); //ie, account for translations...

    //gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);

    //invert y value properly
    //y = (int) ((float) viewportBounds[3] - (float) y);

    //get z value from scene
    //FloatBuffer zBuf = FloatBuffer.allocate(1);
    //gl.glReadPixels(x, (int) y, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zBuf);
    //float z = zBuf.get();

    //unproject mouse coords into world coords!
    glu.gluUnProject((double) x, (double) y, (double) z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      worldCoords, 0);

    return new Point3f((float) worldCoords[0], (float) worldCoords[1], (float) worldCoords[2]);
  }

  /*
  public double[] getWorldCoordsForScreenCoord(Geom g, int x, int y, float z)
  {
  double modelview[] = new double[16];
  double projectionMatrix[] = new double[16];
  int viewportBounds[] = new int[4];
  double worldCoords[] = new double[3];
  
  loadWorldIdentity();
  setWorldCoords(gl);
  gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview, 0);
  gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projectionMatrix, 0);
  gl.glGetIntegerv(gl.GL_VIEWPORT, viewportBounds, 0);
  
  //make "carpet" to pick from while dragging
  Point3d zeroPt = new Point3d(0,0,0);
  Point3d worldPt = MatrixUtils.getGeomPointInWorldCoordinates(zeroPt, g.modelview,
  modelviewMatrix);
  
  gl.glBegin(gl.GL_POLYGON);
  gl.glColor4f(0f, 1f, 0f, .2f); //make this transparent unless debugging
  
  double nd1 = -1; //-100f;
  double nd2 = 1; //100f;
  
  double ndx = worldPt.x;
  double ndy = worldPt.y;
  double ndz = worldPt.z;
  
  gl.glVertex3d(ndx + nd1, ndy + nd1, ndz);
  gl.glVertex3d(ndx + nd2, ndy + nd1, ndz);
  gl.glVertex3d(ndx + nd2, ndy + nd2, ndz);
  gl.glVertex3d(ndx + nd1, ndy + nd2, ndz);
  
  gl.glEnd();
  
  
  //invert y value properly
  double y_dub = (int) (viewportBounds[3] - (double)y);
  
  //read z value from "carpet" we draw to the buffer in between frames whilst dragging
  FloatBuffer zBuf = FloatBuffer.allocate(1);
  gl.glReadPixels( x, (int)y_dub, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zBuf );
  float z_dub = zBuf.get();
  
  //unproject mouse coords into world coords!
  glu.gluUnProject((double) x, y_dub, (double)z_dub,
  modelview, 0,
  projectionMatrix, 0,
  viewportBounds, 0,
  worldCoords, 0);
  
  return worldCoords;
  }
   */
  /*
  public double[] getScreenCoordsForWorldCoord(Geom g)
  {
  double modelview[] = new double[16];
  double projectionMatrix[] = new double[16];
  int viewportBounds[] = new int[4];
  double windowCoords[] = new double[3];
  
  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview, 0);
  gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
  gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);
  
  glu.gluProject(g.anchor.x, g.anchor.y, g.anchor.z,
  modelview, 0,
  projectionMatrix, 0,
  viewportBounds, 0,
  windowCoords, 0);
  return (windowCoords);
  }
   */
  
  public Rectangle2D.Float getScreenRectangleForWorldCoords(GeomRect gr)
  {
    Path2D.Float p2d = getScreenShapeForWorldCoords(gr);
    return GeomUtils.pathToRect(p2d);
  }
  
  
  //gluProject maps object coords to screen coords
  //public Shape getScreenShapeForWorldCoords(GL gl, GLU glu, Geom g)
  public Path2D.Float getScreenShapeForWorldCoords(Geom g)
  {
    Path2D.Float p2f = null;
    double projection[] = new double[16];
    int viewport[] = new int[4];

    if (VizGeom.EXPLICITLY_CALCULATE_MODELVIEW == true)
    {
      projection = projectionMatrix;
      viewport = viewportBounds;
    }
    else
    {
      gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
      gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    }
    if (g instanceof GeomRect)
    {
      p2f = projectGeomRect((GeomRect) g, g.modelview, projection, viewport);
    }
    else if (g instanceof GeomPoly)
    {
      p2f = projectGeomPoly((GeomPoly) g, g.modelview, projection, viewport);
    }


    return p2f;
  }

  
  public List<Float> getScreenRectInGeomCoordnates(Geom g, Rectangle2D.Float r2f)
  {
    return getScreenRectInGeomCoordnates(g, (int)r2f.x, (int)r2f.y, (int)r2f.width, (int)r2f.height);
  }
  
  public List<Float> getScreenRectInGeomCoordnates(Geom g, int x, int y, int w, int h)
  {
    List<Float> geomPts = new ArrayList<Float>();

    Point3d p3f_xy = rayIntersect(g, (int)x, (int)y);
    Point3d p3f_wh = rayIntersect(g, (int)(x + w), (int)(y + h));

    geomPts.add((float) p3f_xy.x);
    geomPts.add((float) p3f_xy.y);
    geomPts.add((float) (p3f_wh.x - p3f_xy.x));
    geomPts.add((float) (p3f_wh.y - p3f_xy.y));
    
    return geomPts;
  }
  
  public List<Point3f> getScreenPointsInGeomCoordnates(Geom g, List<Point3f> screenPts)
  {
    List<Point3f> geomPts = new ArrayList<Point3f>();

    for (Point3f s_p3f : screenPts)
    {
      Point3f g_p3f = MatrixUtils.toPoint3f(rayIntersect(g, (int)s_p3f.x, (int)s_p3f.y));
      geomPts.add(g_p3f);
    }
    
    return geomPts;
  }
  
  public Point3d rayIntersect(Geom g, int x, int y)
  {
    return rayIntersect(g, x, y, new Point3d());
  }

  public Point3d rayIntersect(Geom g, int x, int y, Point3d offsetPt)
  {
    //1. get values of ray (at screen coords) at near and far points (in world coordinates)
    //2. get Geom in world coordinates
    //3. get percentage where ray intersects geom's z-value
    //4. use that percentage to calculate x and y offset
    //5. transfrom point into parent's coordinates
    //6. return point

    //double modelview[] = new double[16];
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double wcsN[] = new double[3];
    double wcsF[] = new double[3];
    //double offsets[] = new double[3];

    if (VizGeom.EXPLICITLY_CALCULATE_MODELVIEW == true)
    {
      projection = projectionMatrix;
      viewport = viewportBounds;
    }
    else
    {
    //setPerspective3D();
    //setWorldCoords();
    //gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, viewport, 0);
    }
    //invert y value properly
    y = (int) ((float) viewport[3] - (float) y);

    glu.gluUnProject((double) x, (double) y, 0.0, //-1?
      modelviewMatrix, 0,
      projection, 0,
      viewport, 0,
      wcsN, 0);

    glu.gluUnProject((double) x, (double) y, 1.0,
      modelviewMatrix, 0,
      projection, 0,
      viewport, 0,
      wcsF, 0);

    Point3d nearPt = new Point3d(wcsN[0], wcsN[1], wcsN[2]);
    Point3d farPt = new Point3d(wcsF[0], wcsF[1], wcsF[2]);

    //now get Geom in world coords
    Point3d geomPt_wc;

    Point3d geomPt = new Point3d(g.anchor.x + offsetPt.x, g.anchor.y + offsetPt.y, g.anchor.z + offsetPt.z);
    if (g.parent != null)
    {
      geomPt_wc = MatrixUtils.getGeomPointInWorldCoordinates(geomPt, g.parent.modelview, modelviewMatrix);
    }
    else
    {
      geomPt_wc = geomPt;
    }

    double maxz = GeomUtils.euclidianDistance(nearPt.z, farPt.z);
    double tryz = GeomUtils.euclidianDistance(nearPt.z, geomPt_wc.z);
    double perc = tryz / maxz;

    double rangex = GeomUtils.euclidianDistance(nearPt.x, farPt.x);
    double addx = rangex * perc;
    double finalx;

    if (farPt.x < nearPt.x)
    {
      finalx = nearPt.x - addx;
    }
    else
    {
      finalx = nearPt.x + addx;
    }

    double rangey = GeomUtils.euclidianDistance(nearPt.y, farPt.y);
    double addy = rangey * perc;
    double finaly;
    if (farPt.y < nearPt.y)
    {
      finaly = nearPt.y - addy;
    }
    else
    {
      finaly = nearPt.y + addy;
    }

    Point3d returnPt = new Point3d(finalx, finaly, geomPt_wc.z);

    if (g.parent != null)
    {
      returnPt = MatrixUtils.getWorldPointInGeomCoordinates(returnPt, modelviewMatrix, g.parent.modelview);
    }

    return new Point3d(returnPt.x - offsetPt.x, returnPt.y - offsetPt.y, returnPt.z - offsetPt.z);
  }

  /**
   * currently not being used... investigate...
   * @param gl
   * @param mv
   * @param g
   * @return
   */
  public static double[] transformWithoutRotation(GL gl, double[] mv, Geom g)
  {
    gl.glPushMatrix(); ////PUSH current matrix onto stack

    gl.glLoadIdentity();
    gl.glMultMatrixd(mv, 0);

    // translate command
    gl.glTranslatef(g.anchor.x, g.anchor.y, g.anchor.z);

    // scale commands
    gl.glTranslatef(g.scaleAnchor.x, g.scaleAnchor.y, g.scaleAnchor.z);
    gl.glScalef((float) g.scale.x, (float) g.scale.y, (float) g.scale.z);
    gl.glTranslatef(-g.scaleAnchor.x, -g.scaleAnchor.y, -g.scaleAnchor.z);


    double transformedMV[] = new double[16];
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, transformedMV, 0);



    gl.glPopMatrix();

    return transformedMV;

  }

  /**
   * projectPoint takes a point (with coordinates in a particular modelview)
   * into screen coordinates. The z value will be between 0 and 1 
   * (representing the near and far planes).
   * 
   * @param p3f
   * @param modelview
   * @return
   */
  public Point3f projectPoint(Point3f p3f, double[] modelview)
  {
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double screenCoords[] = new double[3];

    //System.out.println("in projectPoint: p3f = " + p3f);
    //System.out.println("proj...");
    //MatrixUtils.printDoubleArray(projectionMatrix);
    //System.out.println("meodelview...");
    //MatrixUtils.printDoubleArray(modelview);
    //System.out.println("viewport = " + Arrays.toString(viewportBounds));

    //Don't think we need to get each time!
    //	gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
    //	gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    projection = projectionMatrix;
    viewport = viewportBounds;

    glu.gluProject(p3f.x, p3f.y, p3f.z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x = screenCoords[0];
    double y = screenCoords[1];
    double z = screenCoords[2]; //between 0 (nearPlane) and 1 (farPlane)


    //return screen point
    return new Point3f((float) x, (float) y, (float) z);
  }

  public int getWidthOfObjectInPixels(Geom g, float inset)
  {
    //setPerspective3D();
    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double screenCoords[] = new double[3];

    //gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
    //gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    glu.gluProject(0f, 0f, 0f,
      g.modelview, 0,
      projectionMatrix, 0,
      viewportBounds, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];
    double y1 = screenCoords[1];

    glu.gluProject(g.w, 0f, 0f,
      g.modelview, 0,
      projectionMatrix, 0,
      viewportBounds, 0,
      screenCoords, 0);

    double x2 = screenCoords[0];
    double y2 = screenCoords[1];

    int dist = (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
    return dist;
  }

  public Point geomPointToScreenPoint(Point3f geomPoint, double[] modelview, double[] projection, int[] viewport)
  {
    double screenCoords[] = new double[3];
    glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];
    double y1 = screenCoords[1];

    return new Point((int) x1, (int) y1);
  }
  
  public int getXOfObjectInPixels(Geom g, double[] modelview, double[] projection, int[] viewport)
  {
    double screenCoords[] = new double[3];
    glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];

    return (int) x1;
  }
  
  public int getYOfObjectInPixels(Geom g, double[] modelview, double[] projection, int[] viewport)
  {
    double screenCoords[] = new double[3];
    glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double y1 = screenCoords[1];

    return (int) y1;
  }
  public int getWidthOfObjectInPixels(Geom g, float inset, double[] modelview, double[] projection, int[] viewport)
  {
    //setPerspective3D();
    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double screenCoords[] = new double[3];

    //gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
    //gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];
    double y1 = screenCoords[1];

    glu.gluProject(g.w, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x2 = screenCoords[0];
    double y2 = screenCoords[1];

    glu.gluProject(inset, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x3 = screenCoords[0];
    double y3 = screenCoords[1];

    int insetdist = (int) (GeomUtils.euclidianDistance(x1, y1, x3, y3));
    //int dist = (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
    int dist = (int) (GeomUtils.euclidianDistance(x1, y1, x2, y2));
    return dist - insetdist;
  }

  public int getHeightOfObjectInPixels(Geom g, float inset)
  {
    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double windowCoords[] = new double[3];

//		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
//		gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    glu.gluProject(0f, 0f, 0f,
      g.modelview, 0,
      projectionMatrix, 0,
      viewportBounds, 0,
      windowCoords, 0);

    double x1 = windowCoords[0];
    double y1 = windowCoords[1];

    glu.gluProject(0f, g.h, 0f,
      g.modelview, 0,
      projectionMatrix, 0,
      viewportBounds, 0,
      windowCoords, 0);
    double x2 = windowCoords[0];
    double y2 = windowCoords[1];

    return (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
  }

  public int getHeightOfObjectInPixels(Geom g, float inset, double[] modelview, double[] projection, int[] viewport)
  {
    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double windowCoords[] = new double[3];

//		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
//		gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);

    double x1 = windowCoords[0];
    double y1 = windowCoords[1];

    glu.gluProject(0f, g.h, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);
    double x2 = windowCoords[0];
    double y2 = windowCoords[1];

    
    glu.gluProject(inset, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);

    double x3 = windowCoords[0];
    double y3 = windowCoords[1];

    int insetdist = (int) (GeomUtils.euclidianDistance(x1, y1, x3, y3));
 
    //return (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
    int dist =  (int) (GeomUtils.euclidianDistance(x1, y1, x2, y2));

    return dist - insetdist;
  }
  

  public Path2D.Float projectGeomRect(GeomRect g,
    double[] modelview, double[] projection, int[] viewport)
  {
    Path2D.Float p2f = new Path2D.Float();

    double hx = 0.0;
    double hy = 0.0;
    double hz = 0.0;
    //double hx = (double)g.anchor.x;
    //double hy = (double)g.anchor.y;
    //double hz = (double)g.anchor.z;
    double screenCoords[] = new double[3];

    //System.out.print("screenCoords[2] : ");

    glu.gluProject(hx, hy, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.moveTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    glu.gluProject(hx + g.w, hy, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.lineTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    glu.gluProject(hx + g.w, hy + g.h, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.lineTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    glu.gluProject(hx, hy + g.h, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.lineTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    p2f.closePath();

    //System.out.println("");
    return p2f;
  }

  public Path2D.Float projectGeomPoly(GeomPoly g,
    double[] modelview, double[] projection, int[] viewport)
  {
    Path2D.Float p2f = new Path2D.Float();

    double hx = 0.0; //(double)g.anchor.x;
    double hy = 0.0; //(double)g.anchor.y;
    double hz = 0.0; //(double)g.anchor.z;

    double windowCoords[] = new double[3];

    GeomPoint gp = g.vertices.get(0);
    //GeomPoint gp = g.selectableBoundary.get(0);

    glu.gluProject(hx + gp.anchor.x, hy + gp.anchor.y, hz + gp.anchor.z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);
    p2f.moveTo((float) windowCoords[0], (float) (BehaviorismDriver.canvasHeight - windowCoords[1]));

    //System.out.println("g.verts.size = " + g.selectableBoundary.size());
    for (int i = 1; i < g.vertices.size(); i++) //for (int i = 1; i < g.selectableBoundary.size(); i++)
    {
      gp = g.vertices.get(i);
      //gp = g.selectableBoundary.get(i);

      glu.gluProject(hx + gp.anchor.x, hy + gp.anchor.y, hz + gp.anchor.z,
        modelview, 0,
        projection, 0,
        viewport, 0,
        windowCoords, 0);
      p2f.lineTo((float) windowCoords[0], (float) (BehaviorismDriver.canvasHeight - windowCoords[1]));
    }

    p2f.closePath();
    
    //GeomUtils.printPath(p2f);
    
    return p2f;
  }

  public static void testChangeOfCoordinates(Geom g)
  {
    /*
    Geom gp = BehaviorismDriver.renderer.currentWorld.geoms.get(0);
    Geom gc = BehaviorismDriver.renderer.currentWorld.geoms.get(0).geoms.get(0);
    Geom childPt = gc.geoms.get(0);
    
    System.out.println("\n\tWe have selected the " + g.name + " geom");
    
    System.out.println("gp = " + gp.name);
    System.out.println("gc = " + gc.name);
    
    Point3f zeroPt = new Point3f(0f, 0f, 0f);
    
    Point3f gp_abs_coords = MatrixUtils.getGeomPointInAbsoluteCoordinates(zeroPt, gp.modelview);
    
    System.out.println("parent in parent coords: " + gp.anchor);
    System.out.println("parent in absolute coords: " + gp_abs_coords);
    Point3f parent_in_child_coords = MatrixUtils.getGeomPointInGeomCoordinates(zeroPt, gp.modelview, gc.modelview);
    System.out.println("parent point ("+zeroPt+") in child coords: " + parent_in_child_coords);
    Point3f parent_in_world_coords = MatrixUtils.getGeomPointInWorldCoordinates(zeroPt, gp.modelview, modelviewMatrix);
    System.out.println("parent point ("+zeroPt+") in world coords: " + parent_in_world_coords);
    
    
    Point3f gc_abs_coords = MatrixUtils.getGeomPointInAbsoluteCoordinates(zeroPt, gc.modelview);
    System.out.println("child in child coords: " + gc.anchor);
    System.out.println("child in absolute coords: " + gc_abs_coords);
    Point3f child_in_parent_coords = MatrixUtils.getGeomPointInGeomCoordinates(zeroPt, gc.modelview, gp.modelview);
    System.out.println("child point ("+zeroPt+") in parent coords: " + child_in_parent_coords);
    Point3f child_in_world_coords = MatrixUtils.getGeomPointInWorldCoordinates(zeroPt, gc.modelview, modelviewMatrix);
    System.out.println("child point ("+zeroPt+") in world coords: " + child_in_world_coords);
    
    
    Point3f testPt = new Point3f(0f,1f,0f); //point in world
    //testPt = MatrixUtils.transformWorldPointToAbsoluteCoordinates(testPt, modelviewMatrix);
    
    //System.out.println("test point ABS = " + testPt);
    Point3f p_in_c_coords = MatrixUtils.getWorldPointInGeomCoordinates(testPt, modelviewMatrix, gc.modelview);
    //Point3f p_in_c_coords = MatrixUtils.transformAbsolutePointToGeomCoordinates(testPt, gc.modelview);
    //System.out.println("point ("+testPt+") in child coords: " + p_in_c_coords);
    childPt.anchor = p_in_c_coords;
    
    //p_in_c_coords = MatrixUtils.getGeomPointInGeomCoordinates(zeroPt, gp.modelview, gc.modelview);
    //System.out.println("parent point ("+zeroPt+") in child coords: " + p_in_c_coords);
     */
  }

  public static boolean getBoolean(GL gl, int param)
  {
    ByteBuffer bb = ByteBuffer.allocate(1);
    gl.glGetBooleanv(param, bb);

    if (bb.get(0) == 0)
    {
      return false;
    }
    return true;
  }

  public static void extractFrustum()
  {
    frustum = new double[6][4];

    double[] modl = modelviewMatrix;
    double[] proj = projectionMatrix;
    double[] clip = new double[16];
    double t;

    /* Combine the two matrices (multiply projectionMatrix by modelview) */
    clip[ 0] = modl[ 0] * proj[ 0] + modl[ 1] * proj[ 4] + modl[ 2] * proj[ 8] + modl[ 3] * proj[12];
    clip[ 1] = modl[ 0] * proj[ 1] + modl[ 1] * proj[ 5] + modl[ 2] * proj[ 9] + modl[ 3] * proj[13];
    clip[ 2] = modl[ 0] * proj[ 2] + modl[ 1] * proj[ 6] + modl[ 2] * proj[10] + modl[ 3] * proj[14];
    clip[ 3] = modl[ 0] * proj[ 3] + modl[ 1] * proj[ 7] + modl[ 2] * proj[11] + modl[ 3] * proj[15];

    clip[ 4] = modl[ 4] * proj[ 0] + modl[ 5] * proj[ 4] + modl[ 6] * proj[ 8] + modl[ 7] * proj[12];
    clip[ 5] = modl[ 4] * proj[ 1] + modl[ 5] * proj[ 5] + modl[ 6] * proj[ 9] + modl[ 7] * proj[13];
    clip[ 6] = modl[ 4] * proj[ 2] + modl[ 5] * proj[ 6] + modl[ 6] * proj[10] + modl[ 7] * proj[14];
    clip[ 7] = modl[ 4] * proj[ 3] + modl[ 5] * proj[ 7] + modl[ 6] * proj[11] + modl[ 7] * proj[15];

    clip[ 8] = modl[ 8] * proj[ 0] + modl[ 9] * proj[ 4] + modl[10] * proj[ 8] + modl[11] * proj[12];
    clip[ 9] = modl[ 8] * proj[ 1] + modl[ 9] * proj[ 5] + modl[10] * proj[ 9] + modl[11] * proj[13];
    clip[10] = modl[ 8] * proj[ 2] + modl[ 9] * proj[ 6] + modl[10] * proj[10] + modl[11] * proj[14];
    clip[11] = modl[ 8] * proj[ 3] + modl[ 9] * proj[ 7] + modl[10] * proj[11] + modl[11] * proj[15];

    clip[12] = modl[12] * proj[ 0] + modl[13] * proj[ 4] + modl[14] * proj[ 8] + modl[15] * proj[12];
    clip[13] = modl[12] * proj[ 1] + modl[13] * proj[ 5] + modl[14] * proj[ 9] + modl[15] * proj[13];
    clip[14] = modl[12] * proj[ 2] + modl[13] * proj[ 6] + modl[14] * proj[10] + modl[15] * proj[14];
    clip[15] = modl[12] * proj[ 3] + modl[13] * proj[ 7] + modl[14] * proj[11] + modl[15] * proj[15];

    /* Extract the numbers for the RIGHT plane */
    frustum[0][0] = clip[ 3] - clip[ 0];
    frustum[0][1] = clip[ 7] - clip[ 4];
    frustum[0][2] = clip[11] - clip[ 8];
    frustum[0][3] = clip[15] - clip[12];

    /* Normalize the result */
    t = Math.sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2]);
    //frustum[0][0] /= t;
    //frustum[0][1] /= t;
    //frustum[0][2] /= t;
    //frustum[0][3] /= t;

    /* Extract the numbers for the LEFT plane */
    frustum[1][0] = clip[ 3] + clip[ 0];
    frustum[1][1] = clip[ 7] + clip[ 4];
    frustum[1][2] = clip[11] + clip[ 8];
    frustum[1][3] = clip[15] + clip[12];

    /* Normalize the result */
    t = Math.sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2]);
    //frustum[1][0] /= t;
    //frustum[1][1] /= t;
    //frustum[1][2] /= t;
    //frustum[1][3] /= t;

    /* Extract the BOTTOM plane */
    frustum[2][0] = clip[ 3] + clip[ 1];
    frustum[2][1] = clip[ 7] + clip[ 5];
    frustum[2][2] = clip[11] + clip[ 9];
    frustum[2][3] = clip[15] + clip[13];

    /* Normalize the result */
    t = Math.sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2]);
    //frustum[2][0] /= t;
    //frustum[2][1] /= t;
    //frustum[2][2] /= t;
    //frustum[2][3] /= t;

    /* Extract the TOP plane */
    frustum[3][0] = clip[ 3] - clip[ 1];
    frustum[3][1] = clip[ 7] - clip[ 5];
    frustum[3][2] = clip[11] - clip[ 9];
    frustum[3][3] = clip[15] - clip[13];

    /* Normalize the result */
    t = Math.sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2]);
    //frustum[3][0] /= t;
    //frustum[3][1] /= t;
    //frustum[3][2] /= t;
    //frustum[3][3] /= t;

    /* Extract the FAR plane */
    frustum[4][0] = clip[ 3] - clip[ 2];
    frustum[4][1] = clip[ 7] - clip[ 6];
    frustum[4][2] = clip[11] - clip[10];
    frustum[4][3] = clip[15] - clip[14];

    /* Normalize the result */
    t = Math.sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2]);
    //frustum[4][0] /= t;
    //frustum[4][1] /= t;
    //frustum[4][2] /= t;
    //frustum[4][3] /= t;

    /* Extract the NEAR plane */
    frustum[5][0] = clip[ 3] + clip[ 2];
    frustum[5][1] = clip[ 7] + clip[ 6];
    frustum[5][2] = clip[11] + clip[10];
    frustum[5][3] = clip[15] + clip[14];

    /* Normalize the result */
    t = Math.sqrt(frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1] + frustum[5][2] * frustum[5][2]);
  //frustum[5][0] /= t;
  //frustum[5][1] /= t;
  //frustum[5][2] /= t;
  //frustum[5][3] /= t;

  }

  public static boolean isLineInFrustum(Point3d p3d1, Point3d p3d2)
  {
    return isLineInFrustum((float) p3d1.x, (float) p3d1.y, (float) p3d1.z,
      (float) p3d2.x, (float) p3d2.y, (float) p3d2.z);
  }

  //this isn't a perfect test, it returns some false positives.
  //I check to make sure that the line crosses two planes,
  //but it still could return results that are actually outside of the 
  //frustum!
  public static boolean isLineInFrustum(float x1, float y1, float z1,
    float x2, float y2, float z2)
  {
    //System.out.printf("in isLineInFrustum... (%f/%f/%f), (%f/%f/%f)\n", x1, y1, z1, x2,y2, z2);
    int crossings = 0;
    double top,
      bot,
      u;
    for (int p = 0; p < 6; p++)
    {
      top = frustum[p][0] * x1 + frustum[p][1] * y1 + frustum[p][2] * z1 + frustum[p][3];
      bot = frustum[p][0] * (x1 - x2) + frustum[p][1] * (y1 - y2) + frustum[p][2] * (z1 - z2);
      u = top / bot;

      //System.out.println("u = " + u);
      if (u > 0 && u < 1)
      {
        /*
        System.out.println("p = " + p + ": intersects plane! " + frustum[p][0] + " " +
        frustum[p][1] + " " +
        frustum[p][2] + " " +
        frustum[p][3]);
         */
        crossings++;
      }
    }

    if (crossings > 1)
    {
      return true;
    }
    //System.out.println("crossings = " + crossings);
    return false;
  }

  /** Assuming that lines are between each point, e.g. p1-->p2, and p2-->p3, then p3-->p1 */
  public static boolean areLinesInFrustum(Point3d... pts)
  {
    for (int i = 0; i < pts.length - 1; i++)
    {
      if (isLineInFrustum(pts[i], pts[i + 1]) == true)
      {
        return true;
      }
    }

    if (pts.length > 2) //check last line
    {
      if (isLineInFrustum(pts[pts.length - 1], pts[0]) == true)
      {
        return true;
      }
    }

    return false;
  }

  public static boolean arePointsInFrustum(Point3d... pts)
  {
    for (Point3d pt : pts)
    {
      if (isPointInFrustum((float) pt.x, (float) pt.y, (float) pt.z))
      {
        return true;
      }
    }

    return false;
  }

  public static boolean isPointInFrustum(Point3d p3d)
  {
    return isPointInFrustum((float) p3d.x, (float) p3d.y, (float) p3d.z);
  }

  public static boolean isPointInFrustum(float x, float y, float z)
  {
    for (int p = 0; p < 6; p++)
    {
      if (frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3] <= 0)
      {
        return false;
      }
    }
    return true;
  }
}


