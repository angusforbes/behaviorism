/* BehaviorIntermittentTest.java ~ Aug 18, 2008 */

package behaviors.geom.intermittent;

import geometry.Geom;
import geometry.text.GeomTextOutset;

/**
 *
 * @author angus
 */
public class BehaviorIntermittentTest extends BehaviorGeomIntermittent
{

  public BehaviorIntermittentTest(IntermittentBehaviorBuilder builder)
  {
    super(builder);
  }
  
  public void updateGeom(Geom geom)
  {
    System.out.println("yes updating at... " + System.currentTimeMillis() );
  
    ((GeomTextOutset)geom).text = "" + System.currentTimeMillis();
  }
}
