/* Category.java ~ Oct 14, 2009 */

package behaviorism.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author angus
 */
public class Category 
{
  String name;
  public static Set<Category> categorySet = new HashSet<Category>();
  public static Map<String, Category> categoryMap = new HashMap<String, Category>();

  public Category(String name)
  {
    this.name = name;
    categorySet.add(this);
    categoryMap.put(name, this);
  }

  public String toString()
  {
    return name;
  }
}
