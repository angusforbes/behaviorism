/* ParentRelationship.java ~ Oct 18, 2009 */

package behaviorism.data;

/**
 *
 * @author angus
 */
public class ParentRelationship extends Relationship
{
  public static ParentRelationship instance = new ParentRelationship();

  public static ParentRelationship getInstance()
  {
    return instance;
  }

  private ParentRelationship()
  {
    super(new Category("parent"));
  }
}
