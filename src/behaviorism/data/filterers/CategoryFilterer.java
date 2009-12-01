/* CategoryFilterer.java ~ Oct 14, 2009 */

package behaviorism.data.filterers;

import behaviorism.data.*;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author angus
 */
public class CategoryFilterer extends NodeFilterer
{
  Set<Category> categories = new HashSet<Category>();

  public CategoryFilterer(Category ... categories)
  {
    for (Category c : categories)
    {
      this.categories.add(c);
    }
  }

  @Override
  public boolean filter(Node node)
  {
    if (categories.contains(node.category))
    {
      return true;
    }

    return false;
  }
}
