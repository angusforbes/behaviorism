/*
 * Behaviorism.java, Created on June 26, 2007, 12:52 PM
 */
package behaviorism;

import behaviors.Behavior;
import com.sun.opengl.util.texture.TextureIO;
import renderers.SceneGraph;
import renderers.Renderer;
import handlers.MouseHandler;
import handlers.KeyboardHandler;
import handlers.FontHandler;
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
import worlds.World;

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

  public int canvasWidth = 600;
  public int canvasHeight = 400;
  public boolean fullScreen = false;
  public boolean frameUndecorated = false;
  public boolean useCursor = true;
  public String applicationName = "Untitled behaviorism project";
  public AtomicBoolean isShutdown = new AtomicBoolean(false);
  public AtomicBoolean doneShutdown = new AtomicBoolean(false);

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
    if (instance != null)
    {
      return instance;
    }

    instance = new Behaviorism();

    return instance;
  }


  private Behaviorism()
  {}

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
    Behaviorism.getInstance().initialize(properties);

    if (properties != null)
    {
      world.setWorldParams(properties);
    }
    Renderer.getInstance().installWorld(world);
    Utils.sleep(2000); //give it a sec, later make it wait explicitly until opengl setup is complete

    world.setUpWorld();
  }

  public void initialize(Properties properties)
  {
    String osName = System.getProperty("os.name");
    String osVersion = System.getProperty("os.version");

    System.out.println("osName = " + osName + " version = " + osVersion);

    //load properties
    if (properties != null)
    {
      setMainParams(properties);
      setBehaviorParams(properties);
      setSceneGraphParams(properties);
      //setWorldParams(properties);
      setFontParams(properties);
    }

    //set up openGL canvas        
    GLCapabilities caps = new GLCapabilities();
    GLCapabilitiesChooser chooser = new DefaultGLCapabilitiesChooser();
    caps.setSampleBuffers(true);
    //caps.setNumSamples(4); //16
    GLCanvas canvas = new GLCanvas(caps, chooser, null, null);

    //set up frame
    JFrame frame = new JFrame(this.applicationName);
    frame.add(canvas, BorderLayout.CENTER);
    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

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
    canvas.addMouseListener(MouseHandler.getInstance());
    canvas.addMouseMotionListener(MouseHandler.getInstance());
    canvas.addMouseWheelListener(MouseHandler.getInstance());
    canvas.addKeyListener(KeyboardHandler.getInstance());
    canvas.addGLEventListener(Renderer.getInstance());
    canvas.requestFocus();

    frame.setVisible(true);
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

    this.frameUndecorated = Boolean.parseBoolean(properties.getProperty("main.frameUndecorated"));

    this.fullScreen = Boolean.parseBoolean(properties.getProperty("main.fullScreen"));

    if (!this.fullScreen)
    {
      try
      {
        canvasWidth = Integer.parseInt(properties.getProperty("main.canvasWidth"));
        canvasHeight = Integer.parseInt(properties.getProperty("main.canvasHeight"));
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

  
  }
  */

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
