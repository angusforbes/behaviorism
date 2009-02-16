/* BehaviorGeomContinuous.java ~ Aug 17, 2008 */

package behaviors.geom.continuous;

import behaviors.geom.*;
import behaviors.BehaviorContinuous;

/**
 *
 * @author angus
 */
public abstract class BehaviorGeomContinuous extends BehaviorContinuous implements GeomUpdater
{
  
  public BehaviorGeomContinuous()
  {
  }
  
  public BehaviorGeomContinuous(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }
}
