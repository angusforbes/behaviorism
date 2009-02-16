/* BehaviorIntermittentTet.java ~ Aug 18, 2008 */

package behaviors.geom.intermittent;

import geometry.Geom;
import geometry.text.GeomText2;

/**
 *
 * @author angus
 */
public class BehaviorIntermittentTet extends BehaviorGeomIntermittent
{

  public BehaviorIntermittentTet(IntermittentBehaviorBuilder builder)
  {
    super(builder);
  }
  
  public void updateGeom(Geom geom)
  {
    System.out.println("yes updating at... " + System.currentTimeMillis() );
  
    ((GeomText2)geom).text = "" + System.currentTimeMillis();
  }
}
