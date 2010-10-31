/* GraphPlane.java ~ Oct 18, 2009 */

package behaviorism.data.planes;

import behaviorism.data.*;

/**
 *
 * @author angus
 */
public class GraphPlane extends Node
{

  public static Category graphPlaneCategory = new Category("plane");

  public GraphPlane(String name)
  {
    this.name = name;
    this.category = graphPlaneCategory;
  }

  public void addNode(Node data)
  {
    super.addNode(data, new IsInPlaneRelationship());
  }
}
