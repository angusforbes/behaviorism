/*
 * WorldGeom.java - Created on July 12, 2007, 6:59 PM
 */
package worlds;

import behaviors.BehaviorGeom;
//import behaviors.BehaviorIsActive;
import geometry.Geom;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import sequences.Sequence;
import behaviorism.BehaviorismDriver;
import geometry.GeomPoint;
import behaviors.Behavior;
import geometry.GeomPoly;
import geometry.GeomRect;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import renderers.RendererJogl;
import renderers.cameras.Cam;
import renderers.cameras.CamBasic;
import renderers.layers.BackToFrontLayer;
import renderers.layers.RendererLayer;
import utils.Utils;

//public abstract class WorldGeom
public abstract class WorldGeom extends GeomPoint //does this make sense?
//we'll have to make a lot of chnages
//to RendererJogl modelview, etc?
{
  //public List<Geom> geoms = new CopyOnWriteArrayList<Geom>();
  //public List<BehaviorGeom> behaviors = new CopyOnWriteArrayList<BehaviorGeom>();
  public List<Sequence> sequences = new CopyOnWriteArrayList<Sequence>();
  public SortedMap<Integer, RendererLayer> layers = new TreeMap<Integer, RendererLayer>();  //protected Connector database = null;
  //public List<Geom> geoms = Collections.synchronizedList(new ArrayList<Geom>());
  //public List<BehaviorGeom> behaviors = Collections.synchronizedList(new ArrayList<BehaviorGeom>());
  //public List<Sequence> sequences = Collections.synchronizedList(new ArrayList<Sequence>());
  ////public SortedMap<Long, List<Sequence>> sequences = 
  ////				Collections.synchronizedSortedMap(new TreeMap<Long, List<Sequence>>());
  //public Data data = new Data();
  public Cam cam = new CamBasic(); //this cam should be the same as the cam in RendererJogl, or should point to it.
  //public State state = new State();

  public abstract void setUpWorld();

  public void reset()
  {
  }
  public boolean isPaused = false;

  public WorldGeom()
  {
    //initialize a default RendererLayer for this world
    layers.put(0, new BackToFrontLayer());
    //layers.put(0, new UnsortedLayer());
    //System.out.println("added default layer 0");
    this.name = "world";
    this.id = "0";
    this.parent = this.cam;
  }

 
  public static Properties loadPropertiesFile()
  {
    return loadPropertiesFile("behaviorism.properties");
  }

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
    Rectangle2D.Float sb = getScreenBoundsInWorldCoords();
    
    List<GeomPoint> worldBoundaryPoints = getWorldBoundaryPoints();
    
    GeomPoly gr = GeomPoly.makeGeomPolyWithDynamicPoints(worldBoundaryPoints);
    //gr.anchor.set(worldBoundaryPoints.get(0).anchor);
  
    return gr;
  }
  
  public List<GeomPoint> getWorldBoundaryPoints()
  {
    return RendererJogl.worldBoundaryPoints;
  }

  public Rectangle2D.Float getScreenBoundsInWorldCoords()
  {
    return RendererJogl.screenBoundsInWorldCoords;
  }

  /**
   * This method can be overridden by a subclass to attach World-specifc methods to particular keys.
   * It returns true if a World-specifc key was found (which causes the KeyboardHandler to ignore
   * the key if the same key should happen to trigger some other action). It returns false if no World-specifc
   * key was pressed. 
   * @param keys
   * @param keysPressing
   * @return true if a defined world-specific key was pressed, false otherwise.
   */
  public boolean checkKeys(boolean[] keys, boolean[] keysPressing)
  {
    return false;
  }
  /*
  protected Connector setDatabase()
  {
  return null;
  }
   */

  //this may not be necessary since now WorldGeom extends from an empty GeomPoint... check it out sometime...,
  //the only difference is that the parent is null here (should now be the the root geom point?)
  /*
  @Override
  public void addGeomToLayer(Geom g, boolean isActive, int layerNum)
  {
    WorldGeom.addGeomToSceneGraph(g, this.geoms, isActive, null);
    WorldGeom.addGeomToRendererLayer(g, layerNum);
  }
  */
  
  public static void addGeomToSceneGraph(Geom g, List<Geom> geomList, boolean isActive, Geom parentGeom)
  {
    geomList.add(g);
    g.parent = parentGeom;
    g.isActive = isActive;
  }
  
  public static void addGeomToRendererLayer(Geom g, int layerNum)
  {
    g.layerNum = layerNum; 
    
    /*
    RendererLayer layer = BehaviorismDriver.renderer.currentWorld.layers.get(layerNum);

    if (layer != null)
    {
      layer.attachedGeoms.add(g);
    }
    else
    {
      System.out.println("ERROR in addGeomToLayer! No such layer as layer " + layerNum);
      BehaviorismDriver.renderer.currentWorld.layers.get(0).attachedGeoms.add(g);
    }
    */
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

  public void addSequence(Sequence s)
  {
    //synchronized(sequences)
    {
      sequences.add(s);
    }

  //does it really matter if they are sorted??
		/*
  synchronized(sequences)
  {
  List list = sequences.get(s.baseNano);
  if(list == null)
  {
  list = new ArrayList<Sequence>();
  sequences.put(s.baseNano, list);
  }
  
  Utils.addTo(list, s);
  }
   */
  }

  /*
  public void removeSequence(Sequence s)
  {
  synchronized(sequences)
  {
  sequences.remove(s);
  }
  }
   */
  public void clearSequences(Sequence s)
  {
    //synchronized(sequences)
    {
      sequences.clear();
    }
  }

  public void clearGeoms()
  {
    //synchronized(geoms)
    {
      geoms.clear();
    }
  }

  public void destroyBehavior(BehaviorGeom b)
  {
    b.isDone = true;
  /*
  synchronized(behaviors)
  {
  //something like : b.isDone = true, so that at next tick, it will be automatically detached from Geoms.
  behaviors.remove(b);
  }
   */
  //okay also should remove it from every Geom that has it
  }

  /*
  public void registerBehavior(GeomUpdater b)
  {
  //synchronized(behaviors)
  {
  behaviors.add(b);
  }
  }
   */
  public void registerBehavior(Behavior b)
  {
    //synchronized(behaviors)
    {
      //behaviors.add(b);
    }
  }

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
    BehaviorismDriver.renderer.installWorld(this);
    
    //BehaviorismDriver.renderer.setCamera(cam);
  }
}
