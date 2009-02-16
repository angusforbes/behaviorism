/* BehaviorGeomContinuous.java ~ Aug 17, 2008 */

package behaviors.geom.discrete;

import behaviors.BehaviorDiscrete;
import behaviors.BehaviorDiscrete.DiscreteBehaviorBuilder;
import behaviors.geom.*;

/**
 *
 * @author angus
 */
public abstract class BehaviorGeomDiscrete extends BehaviorDiscrete implements GeomUpdater
{
  public BehaviorGeomDiscrete(DiscreteBehaviorBuilder builder)
  {
    super(builder);
  }
}
