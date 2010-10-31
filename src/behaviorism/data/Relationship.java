/* Relationship.java ~ Oct 14, 2009 */

package behaviorism.data;

import java.util.ArrayList;

/**
 * A relationship is a special kind of DataNode that has a basic parent and a child connection
 * @author angus
 */
public class Relationship extends Node
{
  public double weight = 1.0; //by default should be between -1 and +1, of course can be overridden

  public Relationship(Category category)
  {
    this.category = category;
  }

  public Relationship(Category category, double weight)
  {
    this.category = category;
    setWeight(weight);
  }

  public void addLink(Node parent, Node child)
  {
    linkNode(parent, ParentRelationship.getInstance());
    linkNode(child, ParentRelationship.getInstance());
  }
  
  public Node getParent()
  {
    return new ArrayList<Node>(categoryMap.get(ParentRelationship.getInstance().category)).get(0);
  }

  public Node getChild()
  {
    return new ArrayList<Node>(categoryMap.get(ChildRelationship.getInstance().category)).get(0);
  }

  public void setWeight(double weight)
  {
    if (weight > 1.0)
    {
      weight = 1.0;
    }
    if (weight < -1.0)
    {
      weight = -1.0;
    }
    this.weight = weight;
  }

  public double getWeight()
  {
    return weight;
  }

  public String toString()
  {
    return "Relationship of type "+category + ", weight =" + weight;
  }
}
