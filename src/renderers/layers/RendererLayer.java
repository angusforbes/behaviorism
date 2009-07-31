/* RendererLayer.java ~ Aug 23, 2008 */

package renderers.layers;

import renderers.*;
import geometry.Geom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.vecmath.Point3d;
import utils.MatrixUtils;

/**
 * getMatrixIndex RendererLayer is a general grouping of Geoms around a particular set of state attributes,
 * which can be ordered in a particular way (as defined by the sortGeomsInLayer() method).
 * <p> 
 * getMatrixIndex Geom is attached to a RendererLayer using any of the addGeomToLayer() variations from the Geom
 * class, or by using an addGeom() variation (which attaches the Geom to the default layer).
 * Before attaching a Geom to a custom layer you must first create and add the layer to the World
 * using the addLayer(int layerNum, RendererLayer layer) method in WorldGeom.
 * <p>
 * The attachedGeoms list is actually filled within the initial traversal of the 
 * scene graph (the "transform" section within the display loop) based on the layerNum setting 
 * of each active Geom (which is set in the addGeom methods). That is, the layers are cleared and refilled 
 * with each display loop.
 * <p>
 * The layers themselves are stored within the World, in a hash which maps
 * the display priority (the key) to the RendererLayer (the value). Layers are added to 
 * the world using the addLayer method.
 * <p>
 * In the "draw" section of the display loop, each layer is traversed from highest draw priority 
 * to lowest (where the smaller number has the higher priority-- i.e., layer 0 is traversed first).
 * <p>
 * Before being rendered the geoms attached to the layer are sorted by an algorithm specified by overriding
 * sortGeomsInLayer, or by the the simple default back-to-front sorting algorithm based on the z-buffer
 * of each geoms anchor point. For instance, if you don't want the layer sorted (and just to use the order
 * imposed by the order of initial insertion onto the scene graph) then you could quickly create a 
 * anonymous inner version of RendererLayer like so:
 * 
    RendererLayer unsortedLayer = new RendererLayer()
    {
     @Override
     public void sortGeomsInLayer()
     {
      //don't sort me!
     }
    };
 * 
 * <p>
 * Once the geoms are sorted, the State attributes of the RendererLayer are then set, and the geoms are drawn
 * in order.
 * <p>
 * Note that the State attributes can also be set individually by the Geom, which could cause 
 * unwanted behavior if, for instance, the 2nd geom in the layer changed the state and you expect
 * the 3rd geom to have the state associated with the layer and not with the 2nd geom! For now,
 * it is good practice to either manage the idividual state of *all* of the Geoms in a layer, or to 
 * manage the idividual state of *none* of them 
 * (and let the layer itself set the state for all geoms in the layer).  
 *
 * NOTE: this has been fixed-- if a geom overwrites a layer state, we push the layer state
 * before changing to geoms State, and then pop back to the layer state
 * once we are done rendering the geom.
 * @author angus
 */
abstract public class RendererLayer 
{
  public boolean isSortable = true;
  //can't sort a f'ing CopyOnWriteArrayList!!!!
  final public List<Geom> attachedGeoms = Collections.synchronizedList(new ArrayList<Geom>());
  //public List<Geom> attachedGeoms = new CopyOnWriteArrayList<Geom>();
  public State state = new State();

  /**
   * Sorts the geoms attached to this layer according to the specified sorting algorithm-- 
   * This method must be overridden to define a sorting method.  
   */
  public void sortGeomsInLayer()
  {
    if (isSortable == true)
    {
      System.err.println("ERROR in " + getClass() + ", this Layer is Sortable and must define a sort method.");
    }
  }
  
  //Static implementations of sorting algorithms (to be used by RendererLayer subclasses)
    
  final public static void sortGeomsByAnchorDepth(final List<Geom> geomList, final boolean backToFront)
  {
    Collections.sort(geomList, new Comparator<Geom>()
    {
      @Override
      public int compare(Geom a, Geom b)
      {
        Point3d p3d_a = MatrixUtils.getGeomPointInAbsoluteCoordinates(
         new Point3d(0, 0, 0), a.modelview);
        Point3d p3d_b = MatrixUtils.getGeomPointInAbsoluteCoordinates(
         new Point3d(0, 0, 0), b.modelview);
 
        //System.out.println("comparing " + p3d_a.z + " with " + p3d_b.z);
        if (p3d_a.z < p3d_b.z)
        {
          if (backToFront)
          {
            return -1;
          }
          else
          {
            return 1;
          }
        }
        else if (p3d_a.z > p3d_b.z)
        {
          if (backToFront)
          {
            return 1;
          }
          else
          {
            return -1;
          }
        }
        else
        {
          return 0;
        }
      }
    });
 
  }    

  
  
  final public static void sortGeomsByType(List<Geom> geomList, final String[] types)
  {
    Collections.sort(geomList, new Comparator<Geom>()
    {
      public int compare(Geom a, Geom b)
      {
        //System.out.println("a.getClass = " + a.getClass().getName());
        int type_a = -1;
        int type_b = -1;
        for (int i = 0; i < types.length; i++)
        {
          //System.out.println("does <" + a.getClass().getName() + "> equals <" + types[i] + ">");
          if (a.getClass().getSimpleName().equals(types[i]))
          {
            //System.out.println("yes!");
            type_a = i;
            break;
          }
        }
        for (int i = 0; i < types.length; i++)
        {
          //System.out.println("does <" + b.getClass().getName() + "> equals <" + types[i] + ">");
          if (b.getClass().getSimpleName().equals(types[i]))
          {
            //System.out.println("yes!");
            type_b = i;
            break;
          }
        }
        //System.out.println("comparing " + p3d_a.z + " with " + p3d_b.z);
        if (type_a < type_b)
        {
            return -1;
        }
        else if (type_a > type_b)
        {
            return 1;
        }
        else
        {
          return 0;
        }
      }
    });
 
  }
}
