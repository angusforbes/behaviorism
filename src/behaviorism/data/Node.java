/* Node.java ~ Oct 14, 2009 */

package behaviorism.data;

import behaviorism.geometry.Geom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author angus
 */
abstract public class Node
{
  public String name = "untitled";
  public Category category;

  Set<Node> datas = new HashSet<Node>();
  Map<Category, Set<Node>> categoryMap = new HashMap<Category, Set<Node>>();
  Map<Node, Set<Relationship>> relationshipMap = new HashMap<Node, Set<Relationship>>();

  public boolean hasChanged = false;

  public Node(){}

  public Set<Node> getData()
  {
    return datas;
  }

  public Geom makeGeom()
  {
    return null;
  }

  public void addNode(Node data)
  {

  }


  public void addNode(Node child, Relationship ... relationships)
  {
    for (Relationship relationship : relationships)
    {
      relationship.addLink(this, child);
      linkNode(child, relationship);
    }
  }

  public void linkNode(Node data, Relationship ... relationships)
  {
    datas.add(data);

    //System.out.println("added " + data + "\n\t to this data..." + this);
    Set<Node> categorySet = categoryMap.get(data.category);

    if (categorySet == null)
    {
      categorySet = new HashSet<Node>();
      categoryMap.put(data.category, categorySet);
    }

    categorySet.add(data);

    Set<Relationship> relationshipSet = relationshipMap.get(data);

    if (relationshipSet == null)
    {
      relationshipSet = new HashSet<Relationship>();
      relationshipMap.put(data, relationshipSet);
    }

    for (Relationship r : relationships)
    {
      relationshipSet.add(r);
    }

    hasChanged = true;
  }

  public String toString()
  {
    String str = "<" + name + ">, class=" + getClass() + " " + category;
    for (Map.Entry<Category, Set<Node>> entry : categoryMap.entrySet())
    {
      str += "\n\tcategory <" + entry.getKey() + ">";

      for (Node data : entry.getValue())
      {
        str += ("\n\t\t<" + data.name + "> ");
        for (Relationship r : relationshipMap.get(data))
        {
          str += "\n\t\t\trelationship=<" +r.category.name + ">, weight=" + r.weight;
        }
      }
    }

    return str;
  }

  public Set<Node> findByCategory(Category cat)
  {
    return categoryMap.get(cat);
  }

  public Node findByName(String name)
  {
    for (Node d : datas)
    {
      if (d.name.equals(name))
      {
        return d;
      }
    }

    System.err.println("couldn't find <" + name + ">");
    return null;
  }
}
