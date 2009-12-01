/*
 * World.java - Created on July 12, 2007, 6:59 PM
 */
package behaviorism.worlds;

import behaviorism.behaviors.Behavior;
import behaviorism.geometry.Geom;
import behaviorism.geometry.GeomPoint;
import behaviorism.geometry.GeomPoly;
import behaviorism.geometry.GeomRect;
import behaviorism.handlers.MouseHandler;
import behaviorism.renderers.Renderer;
import behaviorism.renderers.State;
import behaviorism.renderers.cameras.Cam;
import behaviorism.renderers.cameras.CamBasic;
import behaviorism.renderers.layers.BackToFrontLayer;
import behaviorism.renderers.layers.RendererLayer;
import behaviorism.sequences.Sequence;
import behaviorism.utils.FileUtils;
import behaviorism.utils.RenderUtils;
import behaviorism.utils.Utils;
import java.util.List;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JApplet;
import org.grlea.log.SimpleLogger;

public abstract class World extends GeomPoint
{

  public Sequence sequence = new Sequence(); //CopyOnWriteArrayList<Sequence>();
  public SortedMap<Integer, RendererLayer> layers = new TreeMap<Integer, RendererLayer>();  //protected Connector database = null;
  public Cam cam = new CamBasic(); //this cam should be the same as the cam in RendererJogl, or should point to it.
  //TESTING
  public List<Behavior> behaviors2 = new CopyOnWriteArrayList<Behavior>();
  public static final SimpleLogger log = new SimpleLogger(World.class);

  public void scheduleBehavior(Behavior b)
  {
    behaviors2.add(b);
  }
  //DONE WITH TEST CODE

  public abstract void setUpWorld();

  /**
   * This method needs to be called from the start() method of the JApplet
   * in order to insure that the JOGL Renderer & Canvas are set up properly *before* calling
   * the customized setUpWorld() for the project, as it may rely on
   * information about the screen bounds, etc., which are only available after
   * the JOGL canvas is ready.
   * @param applet
   */
  public void setUpWorld(JApplet applet)
  {
    Thread t = new Thread()
    {

      public void run()
      {

        while (RenderUtils.getRenderer().isInstalled == false)
        {
          System.err.println("in setUpWorld(JApplet)... waiting for JOGL Renderer to be installed...");
          Utils.sleep(1000);
        }

        System.err.println("in setUpWorld(JApplet)... JOGL Renderer is now available.");
        setUpWorld();
      }
    };

    t.start();
  }

  public void reset()
  {
  }
  public boolean isPaused = false;

  public World()
  {
    //initialize a default RendererLayer for this world
    layers.put(0, new BackToFrontLayer());
    //layers.put(0, new UnsortedLayer());
    //System.out.println("added default layer 0");
    this.name = "world";
    this.id = "0";
    this.parent = this.cam;
  }

  /**
   * It doesn't make sense to set the State of the World, since it will never actually
   * be rendered. Rather, we probably want to set the State of one of the Layers.
   * If a user attempts to set the World's State, we will default to setting
   * the State of the default Layer 0.
   * @param state
   */
  @Override
  public void setState(State state)
  {
    layers.get(0).state = state;
  }

  /**
   * Since the World itself doesn't have a State, we will assume that
   * we are instead trying to get the default Layer's State.
   * @return
   */
  @Override
  public State getState()
  {
    return layers.get(0).state;
  }

  public void setState(int layer, State state)
  {
    layers.get(layer).state = state;
  }

  public State getState(int layer)
  {
    return layers.get(layer).state;
  }

  public static Properties loadPropertiesFile()
  {
    return loadPropertiesFile("behaviorism.properties");
  }

  //logic should be:
  //look in local directory...
  //look in behaviorism.jar (this *should* always exisit)
  //use defualts...
  public static Properties loadPropertiesFile(String fileName)
  {
    Properties properties = new Properties();

    System.out.println("loading properties file...");
    try
    {
      InputStream is = new FileInputStream(fileName);
      properties.load(is);
      is.close();
    }
    catch (IOException e)
    {
      System.out.println("couldn't find properties file " + fileName + "... using defaults");
      return null;
    }

    return properties;
  }

  public static Properties loadPropertiesFileFromJarFile(String fileName)
  {
    log.entry("in loadPropertiesFileFromJarFile(" + fileName + ")");
    Properties properties = new Properties();

    try
    {
      InputStream is = FileUtils.class.getResourceAsStream(fileName);
      properties.load(is);
      is.close();
      log.info("successfully loaded properties file " + fileName + ".");
    }
    catch (Exception e)
    {
      log.info("couldn't find properties file " + fileName + "!");
      properties = null;
    }

    log.exit("out loadPropertiesFileFromJarFile()");
    return properties;
  }

  public void setWorldParams(Properties properties)
  {
  }

  //hmm not dynamic at the moment...
  public GeomRect getWorldRect()
  {
    Rectangle2D.Float sb = getScreenBoundsInWorldCoords();

    System.out.println("sb:" + sb);
    return new GeomRect(sb.x, sb.y, 0f, sb.width, sb.height);
  }

  //hmm think about this a bit more...
  public GeomPoly getWorldGeom()
  {
    //Rectangle2D.Float sb = getScreenBoundsInWorldCoords();

    List<GeomPoint> worldBoundaryPoints = getWorldBoundaryPoints();

    GeomPoly gr = GeomPoly.makeGeomPolyWithDynamicPoints(worldBoundaryPoints);
    //gr.anchor.set(worldBoundaryPoints.get(0).anchor);

    return gr;
  }

  public List<GeomPoint> getWorldBoundaryPoints()
  {
    return Renderer.worldBoundaryPoints;
  }

  public Rectangle2D.Float getScreenBoundsInWorldCoords()
  {
    return Renderer.screenBoundsInWorldCoords;
  }

  /**
   * This method can be overridden by a subclass to attach World-specifc methods to particular keys.
   * It returns true if a World-specifc key was found (which causes the KeyboardHandler to ignore
   * the key if the same key shoul happen to trigger some other action). It returns false if no World-specifc
   * key was pressed. 
   * @param keys
   * @param keysPressing
   * @return true if a defined world-specific key was pressed, false otherwise.
   */
  public boolean checkKeys(boolean[] keys, boolean[] keysPressing)
  {
    return false;
  }

  //this can be used by a Geom to notify the world that a click was registered, in case a click
  //affects a more global property
//  public void doubleClickAction(Geom g)
//  { }
//  public void clickAction(Geom g)
//  { }
  /*
  protected Connector setDatabase()
  {
  return null;
  }
   */

  //this may not be necessary since now World extends from an empty GeomPoint... check it out sometime...,
  //the only difference is that the parent is null here (should now be the the root geom point?)
  /*
  @Override
  public void addGeomToLayer(Geom g, boolean isActive, int layerNum)
  {
  World.addGeomToSceneGraph(g, this.geoms, isActive, null);
  World.addGeomToRendererLayer(g, layerNum);
  }
   */
  public void addGeomToSceneGraph(Geom g, List<Geom> geomList, boolean isActive, Geom parentGeom)
  {
    geomList.add(g);
    g.parent = parentGeom;
    g.isActive = isActive;
  }

  public void addGeomToRendererLayer(Geom g, int layerNum)
  {
    g.layerNum = layerNum;

    ///HMM this was all commented out, but it seems right...

    RendererLayer layer = layers.get(layerNum);

    if (layer != null)
    {
      layer.attachedGeoms.add(g);
    }
    else
    {
      System.err.println("ERROR in addGeomToLayer! No such layer as layer " + layerNum);
      System.err.println("... using layer 0 instead ...");
      layers.get(0).attachedGeoms.add(g);
    }

  }

  public void addLayer(int layerNum, RendererLayer layer)
  {
    layers.put(layerNum, layer);
  }

  /** 
   * removeGeom flags the Geom to be deactivated and removed from its parent during the next display loop. 
   * @param g
   */
  /*
  @Override
  public void removeGeom(Geom g)
  {
  g.isActive = false;
  g.isDone = true;
  }
   */
  /**
   * This version of addGeom waits until the Geom has been drawn once (so that the modelview is updated correctly)
   * before returning. If the parameter isActive = true the Geom will be activated only after is has been attached to the scene graph.
   * @param g
   * @param isActive
   */
  /*
  public void addGeomAndWaitUntilAdded(Geom g, boolean isActive)
  {
  addGeom(g);

  while (g.isAttached == false)
  {
  Utils.sleep(10);
  }

  g.isActive = isActive;
  }
   */

  /*
  public void addSequence(Sequence s)
  {
  //synchronized(sequences)
  {
  sequences.add(s);
  }

  //does it really matter if they are sorted??

  //  synchronized(sequences)
  //  {
  //  List list = sequences.get(s.baseNano);
  //  if(list == null)
  //  {
  //  list = new ArrayList<Sequence>();
  //  sequences.put(s.baseNano, list);
  //  }
  //
  //  Utils.addTo(list, s);
  //  }

  }
   */

  /*
  public void removeSequence(Sequence s)
  {
  synchronized(sequences)
  {
  sequences.remove(s);
  }
  }
   */
  /*
  public void clearSequences(Sequence s)
  {
  //synchronized(sequences)
  {
  sequences.clear();
  }
  }
   */
  /** 
  This rewinds to the parent-most node of the Geom. It then traverses all the nodes back down to the Geom, 
  and at each level makes the node the last thing rendered among all other nodes at that level. I don't think
  this needs any synchronization because it only happens when a user clicks on a node (and thus makes in the
  new selectedGeom). I could be wrong and there may be some situation where it should be synched up.
   */
  public void adjustZOrder(Geom g)
  {
    if (g.parent == null) //then the parent is this world itself
    {
      //synchronized(this.geoms)
      {
        this.geoms.remove(g);
        this.geoms.add(g);
      }
    }
    else
    {

      adjustZOrder(g.parent);
      //synchronized(g.parent.geoms)
      {

        g.parent.geoms.remove(g);
        g.parent.geoms.add(g);
      }
    }
  }

  /** Sets the camera with the default basic Cam */
  protected void setCamera()
  {
    //setCamera(new CamOrbit(0f, 0f, 0f, -5f)); //look at center(0f,0f,0f) from 5 units back (0f,0f,-5f)
    setCamera(new CamBasic(0f, 0f, -5f)); //position at anchor (0f,0f,5f) and look straight (0f,0f,-infinity)
    //setCamera(new CamBasic());
    //Cam cam = new Cam();
  }

  /** Sets the camera with the camera of your choice */
  protected void setCamera(Cam cam)
  {
    this.cam = cam;

    //make sure renderer has this camera
    RenderUtils.getRenderer().installWorld(this); //is this right??????

    //BehaviorismDriver.renderer.setCamera(cam);
  }

  /**
   * Default behavior of World is to drag the camera when the mouse is dragged.
   * Can be overidden as necessary.
   */
  @Override
  public void dragAction(Geom originatingGeom)
  {
    MouseHandler.getInstance().dragCamera();
  }
}
