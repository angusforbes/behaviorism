/*
 * Behaviorism.java, Created on June 26, 2007, 12:52 PM
 */
package behaviorism;

import behaviorism.behaviors.Behavior;
import behaviorism.handlers.FontHandler;
import behaviorism.handlers.KeyboardHandler;
import behaviorism.handlers.MouseHandler;
import behaviorism.renderers.Renderer;
import behaviorism.renderers.SceneGraph;
import behaviorism.utils.Utils;
import behaviorism.worlds.World;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import javax.media.opengl.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is the main driver for the application using the behaviorism framework.
 * It sets up the openGL renderer,
 * initializes the various handlers,
 * and loads in user properties (from attribute.properties file).
 * The application's Main class should extend from World,
 * and then in the main method invoke Behaviorism
 * by passing in itself and optionally a properties file via the static installWorld method.
 * This Main application class *must* override
 * setUpWorld, which allows you to add to the SceneGraph and to
 * retrieve an active openGL context, among
 * many other things!
 * 
 * @author angus
 */
//this should prob be a singleton as well... we are only supporting a single context at the
//moment... think about how to support multiple contexts...
public class Behaviorism
{

  public int prevCanvasWidth = 600;
  public int prevCanvasHeight = 400;
  public int prevLocX = -1;
  public int prevLocY = -1;
  public int canvasWidth = 600;
  public int canvasHeight = 400;
  public boolean fullScreen = false;
  public boolean frameUndecorated = false;
  public boolean useCursor = true;
  public String applicationName = "Untitled behaviorism project";
  public AtomicBoolean isShutdown = new AtomicBoolean(false);
  public AtomicBoolean doneShutdown = new AtomicBoolean(false);
  private JFrame frame;
  private GLCanvas canvas;
  private Cursor cursor;
  private GraphicsDevice device = null;
  public boolean centerFrame = true;
  public boolean isApplet = false;
  /**
   * Singleton instance of Behaviorism. The only way to use this class is via the static getInstance() method.
   */
  private static Behaviorism instance = null;

  /**
   * Gets the singleton Behaviorism driver.
   * @return the singleton Behaviorism driver
   */
  public static Behaviorism getInstance()
  {
    if (instance == null)
    {
      instance = new Behaviorism();
    }

    return instance;
  }

  /**
   * Private constructor to create the single instance of this class.
   */
  private Behaviorism()
  {
  }

  //I think I want to automatically load the default prop file (from jar)
  //and then overwrite various properites if another prop file
  //is specified.
  /**
   * This is the entrance to the behaviorism framework...
   * @param world
   */
  public static void installWorld(World world)
  {
    installWorld(world, World.loadPropertiesFile());
  }

  /**
   * This is the entrance to the behaviorism framework...
   * @param world
   * @param properties
   */
  public static void installWorld(World world, Properties properties)
  {
    Behaviorism.getInstance().installProperties(properties);

    Behaviorism.getInstance().initialize();

    if (properties != null)
    {
      world.setWorldParams(properties);
    }

    Renderer.getInstance().installWorld(world);

    world.setUpWorld();
  }

  public static void installWorld(World world, Properties properties, JApplet applet)
  {
    Behaviorism.getInstance().installProperties(properties);

    Behaviorism.getInstance().initialize(applet);

    if (properties != null)
    {
      world.setWorldParams(properties);
    }

    Renderer.getInstance().installWorld(world);

    world.setUpWorld();
  }

  public void printSystemInfo()
  {
    //print runtime info
    int mb = 1024 * 1024;
    Runtime rt = Runtime.getRuntime();
    System.out.println(System.getProperty("os.name") + " " + System.getProperty("os.version") + ", " + rt.availableProcessors() + " processors, " + ((rt.totalMemory() - rt.freeMemory()) / mb) + "MB out of " + (rt.totalMemory() / mb) + "MB used.");
  }

  public void installProperties(Properties properties)
  {
    //load properties
    if (properties != null)
    {
      setMainParams(properties);
      setBehaviorParams(properties);
      setSceneGraphParams(properties);
      //setWorldParams(properties);
      setFontParams(properties);
    }
  }

  public void initialize(JApplet applet)
  {
    printSystemInfo();

    //set up canvas
    canvas = makeCanvas();

    applet.add(canvas, BorderLayout.CENTER);
    //set up frame
    //frame = new JFrame(this.applicationName);
    //frame.add(canvas, BorderLayout.CENTER);

    //grab device
    device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    canvas.setSize(300, 300);

    //   makeAppletScreen(applet);
//    if (fullScreen)
//    {
//      makeFullScreen(frame);
//    }
//    else
//    {
//      makeNormalScreen(frame, canvas);
//    }

    //add listeners
    addListeners(canvas);

    canvas.requestFocus();

    //frame.setVisible(true);

    centerFrame = false;
  }

  public void initialize()
  {
    printSystemInfo();

    //set up canvas
    canvas = makeCanvas();

    //set up frame
    frame = new JFrame(this.applicationName);
    frame.add(canvas, BorderLayout.CENTER);

    //grab device
    device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    if (fullScreen)
    {
      makeFullScreen(frame);
    }
    else
    {
      makeNormalScreen(frame, canvas);
    }

    //add listeners
    addListeners(canvas);

    canvas.requestFocus();

    frame.setVisible(true);

    centerFrame = false;
  }

  private GLCanvas makeCanvas()
  {
    //set up openGL canvas
    GLCapabilities caps = new GLCapabilities();
    GLCapabilitiesChooser chooser = new DefaultGLCapabilitiesChooser();
    caps.setSampleBuffers(true);
    //caps.setNumSamples(4); //16
    return new GLCanvas(caps, chooser, null, null);
  }

  private void addListeners(GLCanvas can)
  {
    can.addGLEventListener(Renderer.getInstance());
    can.addKeyListener(KeyboardHandler.getInstance());
    can.addMouseListener(MouseHandler.getInstance());
    can.addMouseMotionListener(MouseHandler.getInstance());
    can.addMouseWheelListener(MouseHandler.getInstance());
  }

  private void removeListeners(GLCanvas can)
  {
    can.removeMouseListener(MouseHandler.getInstance());
    can.removeMouseMotionListener(MouseHandler.getInstance());
    can.removeMouseWheelListener(MouseHandler.getInstance());
    can.removeKeyListener(KeyboardHandler.getInstance());
    can.removeGLEventListener(Renderer.getInstance());
  }

  private void makeFullScreen(JFrame f)
  {
    f.setUndecorated(true);
    device.setFullScreenWindow(f);
  }

  private void makeNormalScreen(JFrame f, GLCanvas c)
  {
    f.setUndecorated(false);
    c.setSize(prevCanvasWidth, prevCanvasHeight);
    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    int xLocation;
    int yLocation;
    if (prevCanvasWidth > screenWidth)
    {
      xLocation = 0;
      yLocation = 0;
    }
    else if (centerFrame == true)//center
    {
      xLocation = (screenWidth - prevCanvasWidth) >> 1;
      yLocation = (screenHeight - prevCanvasHeight) >> 1;
    }
    else
    {
      xLocation = prevLocX;
      yLocation = prevLocY;
    }

    f.setLocation(xLocation, yLocation);

    f.pack();
    f.setVisible(true);
    f.addWindowListener(new WindowAdapter()
    {

      @Override
      public void windowClosing(WindowEvent e)
      {
        shutDown();
      }
    });
  }

  public void toggleFullScreen()
  {
    this.fullScreen = !this.fullScreen;

    try
    {
      Renderer.getInstance().animator.stop();
      Utils.sleep(500); //seems to be necessary to wait because animator.isAnimating() always = true!

      removeListeners(canvas);

      JFrame tmpFrame = new JFrame(this.applicationName);

      GLCanvas tmpCanvas = new GLCanvas(
        canvas.getChosenGLCapabilities(),
        new DefaultGLCapabilitiesChooser(),
        canvas.getContext(),
        null);
      tmpFrame.add(tmpCanvas);

      addListeners(tmpCanvas);

      if (fullScreen == true)
      {
        prevCanvasWidth = canvas.getWidth();
        prevCanvasHeight = canvas.getHeight();
        prevLocX = (int) frame.getLocation().getX();
        prevLocY = (int) frame.getLocation().getY();
        makeFullScreen(tmpFrame);
      }
      else
      {
        makeNormalScreen(tmpFrame, tmpCanvas);
      }

      tmpCanvas.display();

      tmpFrame.requestFocus();
      tmpCanvas.requestFocus();

      frame.dispose();

      frame = tmpFrame;
      canvas = tmpCanvas;

      Renderer.getInstance().animator = new Animator(canvas);
      Renderer.getInstance().animator.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public void toggleCursor()
  {
    useCursor = !useCursor;
    setCursor();
  }

  public void setCursor()
  {
    if (useCursor == false && frame.getCursor().getType() == Cursor.DEFAULT_CURSOR)
    {
      Image cursorImg = canvas.createImage(new MemoryImageSource(16, 16, new int[16 * 16], 0, 16));
      this.cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Custom Cursor");
      frame.setCursor(cursor);
    }
    else if (useCursor == true && frame.getCursor().getType() != Cursor.DEFAULT_CURSOR)
    {
      frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }

  private void setFontParams(Properties properties)
  {
    boolean updateDefaultFont = false;
    String fontName = "Default";
    int fontStyle = 0;

    if (properties.getProperty("font.defaultFont") != null)
    {
      fontName = properties.getProperty("font.defaultFont");
      updateDefaultFont = true;
    }

    if (properties.getProperty("font.defaultFontStyle") != null)
    {
      fontStyle = Integer.parseInt(properties.getProperty("font.defaultFontStyle"));
      updateDefaultFont = true;
    }

    if (updateDefaultFont == true)
    {
      FontHandler.getInstance().setDefaultFont(fontName, fontStyle);
    }
  }

  private void setMainParams(Properties properties)
  {
    String appName = properties.getProperty("main.applicationName");
    if (appName != null)
    {
      this.applicationName = appName;
    }

    this.useCursor = Boolean.parseBoolean(properties.getProperty("main.useCursor"));

    System.out.println("this.useCursor = " + useCursor);

    this.frameUndecorated = Boolean.parseBoolean(properties.getProperty("main.frameUndecorated"));

    this.fullScreen = Boolean.parseBoolean(properties.getProperty("main.fullScreen"));

    if (!this.fullScreen)
    {
      try
      {
        canvasWidth = Integer.parseInt(properties.getProperty("main.canvasWidth"));
        canvasHeight = Integer.parseInt(properties.getProperty("main.canvasHeight"));
        prevCanvasWidth = canvasWidth;
        prevCanvasHeight = canvasHeight;
      }
      catch (NumberFormatException e)
      {
        System.out.println("error: didn't find canvas.width or canvas.height properties, so using default values");
      }
    }
    //opengl.isTexRectEnabled : support for non-power-of-two cards
    TextureIO.setTexRectEnabled(Boolean.parseBoolean(properties.getProperty("opengl.isTexRectEnabled")));
  }

  private void setBehaviorParams(Properties properties)
  {
    Behavior.debugBehaviors = Boolean.parseBoolean(properties.getProperty("behavior.debugBehaviors"));
  }

  private void setSceneGraphParams(Properties properties)
  {
    //how large an offset?
    SceneGraph.vizOffset = Float.parseFloat(properties.getProperty("viz.offset"));

    //various debug flags
    SceneGraph.drawDebugFrameRate = Boolean.parseBoolean(properties.getProperty("viz.drawDebugFrameRate"));
    SceneGraph.drawDebugGrid = Boolean.parseBoolean(properties.getProperty("viz.drawDebugGrid"));
    SceneGraph.drawDebugMouseDraggedPoint = Boolean.parseBoolean(properties.getProperty("viz.drawDebugMouseDraggedPoint"));
    SceneGraph.drawDebugMouseMovedPoint = Boolean.parseBoolean(properties.getProperty("viz.drawDebugMouseMovedPoint"));


  }

  /** Sets the initial world using a dynamic class loader.
  If the world is not found, exit immediately!
  At some point I should figure out if this can work straight
  from the distributed jar file, rather than from a static
  directory. Look into it...
   * Not currently using this, but it's kind of a neat idea...
   */
  private void setWorld(String worldClass)
  {
    File file = new File("build/classes/worlds/");

    Class<?> cls = null;
    try
    {
      // Convert File to a URL
      URL url = file.toURI().toURL();
      URL[] urls = new URL[]
      {
        url
      };

      // Create a new class loader with the directory
      ClassLoader cl = new URLClassLoader(urls);

      // Load in the class; [worldClass].class must be located in
      // the directory build/classes/worlds/
      cls = cl.loadClass("worlds." + worldClass.trim());
      Renderer.getInstance().currentWorld = (World) cls.newInstance();
      System.out.println("cls name = " + Renderer.getInstance().currentWorld.getClass());

    }
    catch (MalformedURLException e)
    {
      System.out.println("malformed url..");
      System.exit(0);
    }
    catch (ClassNotFoundException e)
    {
      System.out.println("class not found..." + cls);
      //System.exit(0);
    }
    catch (InstantiationException e)
    {
      System.out.println("instantiation problem..." + cls);
      System.exit(0);
    }
    catch (IllegalAccessException e)
    {
      System.out.println("illegal access problem..." + cls);
      System.exit(0);
    }
  }

  public void shutDown()
  {
    new Thread(new Runnable()
    {

      public void run()
      {
        System.err.println("DISPOSING OF RESOURCES...");
        isShutdown.set(true);

        while (doneShutdown.get() == false)
        {
          Utils.sleep(10);
        }

        System.err.println("STOPPING GL THREAD...");

        Renderer.getInstance().animator.stop();

        System.err.println("EXITING...");

        System.exit(0);
      }
    }).start();
  }
}
