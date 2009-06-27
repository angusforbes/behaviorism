/* BehaviorGeomContinuous.java ~ Aug 17, 2008 */

package behaviors.geom.continuous;

import behaviors.geom.*;
import behaviors.BehaviorContinuous;
import geometry.Geom;

/**
 *
 * @author angus
 */
public class BehaviorGeomContinuous extends BehaviorContinuous implements GeomUpdater
{
  
  public BehaviorGeomContinuous()
  {
  }
  
  public BehaviorGeomContinuous(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }

  public void updateGeom(Geom g)
  {};
}
