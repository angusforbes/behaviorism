/* ChildRelationship.java ~ Oct 18, 2009 */

package behaviorism.data;

/**
 *
 * @author angus
 */
public class ChildRelationship extends Relationship
{
  public static ChildRelationship instance = new ChildRelationship();

  public static ChildRelationship getInstance()
  {
    return instance;
  }

  private ChildRelationship()
  {
    super(new Category("child"));
  }
}
