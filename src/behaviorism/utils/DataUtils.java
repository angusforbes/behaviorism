/* DataUtils.java ~ Nov 5, 2009 */

package behaviorism.utils;

import behaviorism.data.Node;
import behaviorism.geometry.Geom;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author angus
 */
public class DataUtils 
{
  public static Set<Node> getNodesFromGeoms(Collection<Geom> geoms)
  {
    Set<Node> nodes = new HashSet<Node>();

    for (Geom g : geoms)
    {
      if (g.data != null)
      {
        nodes.add(g.data);
      }
    }

    return nodes;
  }

  public static Set<Geom> getGeomsFromNodes(Collection<Node> nodes, Collection<Geom> possibleGeoms)
  {
    Set<Geom> geoms = new HashSet<Geom>();

    for (Geom possibleGeom : possibleGeoms)
    {
      Node node = possibleGeom.data;

      if (node != null && nodes.contains(node))
      {
        geoms.add(possibleGeom);
      }
    }

    return geoms;
  }

}
