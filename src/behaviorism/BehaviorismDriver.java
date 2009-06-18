/*
 * BehaviorismDriver.java, Created on June 26, 2007, 12:52 PM
 */
package behaviorism;

import behaviors.Behavior;
import com.bric.geom.BasicShape;
import renderers.VizGeom;
import renderers.RendererJogl;
import handlers.MouseHandler;
import handlers.KeyboardHandler;
import handlers.FontHandler;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import javax.media.opengl.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import utils.Utils;
import worlds.WorldGeom;

/**
 * This is the main driver for the application. (blah blah)
 * It sets up the openGL renderer,
 * loads the fonts, initializes the various handlers,
 * and loads in user properties (from attribute.properties file).
 * 
 * @author angus
 */
public class BehaviorismDriver
{

  public static RendererJogl renderer;
  public static VizGeom viz = null; //should be a singleton!
  //public static WorldGeom world; //should probably synchronize...
  public static int screenWidth;
  public static int screenHeight;
  public static int canvasHeight = 400;
  public static int canvasWidth = 600;
  public static KeyboardHandler keyListener;
  public static MouseHandler mouseListener;
  public static BasicShape basicShape;
  public boolean fullScreen = false;
  public boolean frameUndecorated = false;
  public boolean useCursor = true;
  public String applicationName = "Untitled";
  public static AtomicBoolean isShutdown = new AtomicBoolean(false);
  public static AtomicBoolean doneShutdown = new AtomicBoolean(false);

  public static void main(String[] args)
  {
    new BehaviorismDriver();
  }

  public BehaviorismDriver(WorldGeom world)
  {
    initialize(null);
    //BehaviorismDriver.renderer.currentWorld = world;
    installWorld(world, null);

    world.setUpWorld();
  }

  public BehaviorismDriver(WorldGeom world, Properties properties)
  {
    initialize(properties);
    //BehaviorismDriver.renderer.currentWorld = world;
    installWorld(world, properties);

    world.setUpWorld();
  }

  public BehaviorismDriver()
  {
    initialize(null);
    installWorld(renderer.currentWorld, null);
    BehaviorismDriver.renderer.currentWorld.setUpWorld();
  }

  public void installWorld(WorldGeom world, Properties properties)
  {
    if (properties != null)
    {
      world.setWorldParams(properties);
    }
    renderer.installWorld(world);
    Utils.sleep(2000); //give it a sec, later make it wait explicitly until opengl setup is complete
  }

  public void initialize(Properties properties)
  {
    String osName = System.getProperty("os.name");
    String osVersion = System.getProperty("os.version");

    System.out.println("osName = " + osName + " version = " + osVersion);
    //initialize renderer & scene graph 
    BehaviorismDriver.renderer = new RendererJogl();
    BehaviorismDriver.viz = new VizGeom();


    //load properties from attribute.properties
    //Properties properties = loadPropertiesFile();
    if (properties != null)
    {
      setMainParams(properties);
      setBehaviorParams(properties);
      setVizParams(properties);
      //setWorldParams(properties);
      setFontParams(properties);
    }

    //register BasicShape library with non-commercial license code
    BasicShape.register("297210770");

    //determine available fonts
    FontHandler.getInstance().determineFonts();

    //set up openGL canvas        
    GLCapabilities caps = new GLCapabilities();
    GLCapabilitiesChooser chooser = new DefaultGLCapabilitiesChooser();
    caps.setSampleBuffers(true);
    //caps.setNumSamples(4); //16
    GLCanvas canvas = new GLCanvas(caps, chooser, null, null);

    //set up frame
    JFrame frame = new JFrame(this.applicationName);
    frame.add(canvas, BorderLayout.CENTER);
    screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    if (this.useCursor == false)
    {
      Image cursorImg = canvas.createImage(new MemoryImageSource(16, 16, new int[16 * 16], 0, 16));
      Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Custom Cursor");
      frame.setCursor(cursor);
    }

    if (!this.fullScreen)
    {
      int xLocation, yLocation;
      if (canvasWidth > screenWidth)
      {
        xLocation = 0;
      }
      else //center
      {
        xLocation = (screenWidth - canvasWidth) >> 1;
      }
      yLocation = (screenHeight - canvasHeight) >> 1;

      frame.setLocation(xLocation, yLocation);
      canvas.setSize(new Dimension(canvasWidth, canvasHeight));

      frame.setUndecorated(frameUndecorated);

    }
    else //this.fullScreen==true
    {
      frame.setUndecorated(true);
      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
    }

    frame.pack();
    frame.requestFocus();
    frame.addWindowListener(new WindowAdapter()
    {

      @Override
      public void windowClosing(WindowEvent e)
      {
        shutDown();
      }
    });


    //add listeners
    keyListener = new KeyboardHandler();
    mouseListener = new MouseHandler();

    canvas.addMouseListener(mouseListener);
    canvas.addMouseMotionListener(mouseListener);
    canvas.addMouseWheelListener(mouseListener);
    canvas.addKeyListener(keyListener);
    canvas.addGLEventListener(renderer);
    canvas.requestFocus();


    frame.setVisible(true);
  }

  /*
  private Properties loadPropertiesFile()
  {
  Properties properties = new Properties();

  System.out.println("loading properties...");
  try
  {
  InputStream is = new FileInputStream(fileName);
  properties.load(is);
  is.close();

  properties.load(new FileInputStream("attribute.properties"));
  }
  catch (IOException e)
  {
  System.out.println("couldn't find attribute.properties file... using defaults");
  //now set defaults...
  try
  {

  properties.load(new FileInputStream("default.properties"));

  }
  catch (IOException e2)
  {
  System.out.println("Still couldn't find prop file-- even default.properties is missing!");
  }
  }

  return properties;
  }
   */
  private void setFontParams(Properties properties)
  {
    String df = properties.getProperty("font.defaultFont");

    if (df != null)
    {
      FontHandler.getInstance().defaultFont = df;
    }
  }

  private void setMainParams(Properties properties)
  {
    String applicationName = properties.getProperty("main.applicationName");
    if (applicationName != null)
    {
      this.applicationName = applicationName;
    }

    this.useCursor = Boolean.parseBoolean(properties.getProperty("main.useCursor"));

    this.frameUndecorated = Boolean.parseBoolean(properties.getProperty("main.frameUndecorated"));

    this.fullScreen = Boolean.parseBoolean(properties.getProperty("main.fullScreen"));

    if (!this.fullScreen)
    {
      BehaviorismDriver.canvasWidth = 600; //default width
      BehaviorismDriver.canvasHeight = 400; //default height

      try
      {
        BehaviorismDriver.canvasWidth = Integer.parseInt(properties.getProperty("main.canvasWidth"));
        BehaviorismDriver.canvasHeight = Integer.parseInt(properties.getProperty("main.canvasHeight"));
      }
      catch (NumberFormatException e)
      {
        System.out.println("error: didn't find canvas.width or canvas.height properties, so using default values");
      }
    }
    //opengl.isTexRectEnabled : support for non-power-of-two cards
    TextureIO.setTexRectEnabled(Boolean.parseBoolean(properties.getProperty("opengl.isTexRectEnabled")));
  }
  /*
  private void setWorldParams(Properties properties)
  {

  //world.class : which world are we starting with
  String worldClass = properties.getProperty("world.class");
  System.out.println(worldClass);
  if (worldClass != null)
  {
  setWorld(worldClass);
  }
  }
   */

  private void setBehaviorParams(Properties properties)
  {
    Behavior.debugBehaviors = Boolean.parseBoolean(properties.getProperty("behavior.debugBehaviors"));
  }

  private void setVizParams(Properties properties)
  {
    //how large an offset?
    VizGeom.vizOffset = Float.parseFloat(properties.getProperty("viz.offset"));

    //various debug flags
    VizGeom.drawDebugFrameRate = Boolean.parseBoolean(properties.getProperty("viz.drawDebugFrameRate"));
    VizGeom.drawDebugGrid = Boolean.parseBoolean(properties.getProperty("viz.drawDebugGrid"));
    VizGeom.drawDebugMouseDraggedPoint = Boolean.parseBoolean(properties.getProperty("viz.drawDebugMouseDraggedPoint"));
    VizGeom.drawDebugMouseMovedPoint = Boolean.parseBoolean(properties.getProperty("viz.drawDebugMouseMovedPoint"));


  }

  /** Sets the initial world using a dynamic class loader.
  If the world is not found, exit immediately!
  At some point I should figure out if this can work straight
  from the distributed jar file, rather than from a static
  directory. Look into it...
   */
  private void setWorld(String worldClass)
  {
    File file = new File("build/classes/worlds/");

    Class cls = null;
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
      BehaviorismDriver.renderer.currentWorld = (WorldGeom) cls.newInstance();
      System.out.println("cls name = " + BehaviorismDriver.renderer.currentWorld.getClass());

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

  public static void shutDown()
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

        BehaviorismDriver.renderer.animator.stop();

        System.err.println("EXITING...");


        System.exit(0);
      }
    }).start();
  }
}
