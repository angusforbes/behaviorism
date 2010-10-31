/* IsInPlaneRelationship.java ~ Oct 18, 2009 */
package behaviorism.data;

/**
 *
 * @author angus
 */
public class IsInPlaneRelationship extends Relationship
{

  public static Category isInPlaneCategory = new Category("is in plane");

  public IsInPlaneRelationship()
  {
    super(isInPlaneCategory); //, parent, child);
  }
}
