/* BehaviorGeomContinuous.java ~ Aug 17, 2008 */

package behaviors.geom.intermittent;

import behaviors.BehaviorIntermittent;
import behaviors.BehaviorIntermittent.IntermittentBehaviorBuilder;
import behaviors.geom.*;

/**
 *
 * @author angus
 */
public abstract class BehaviorGeomIntermittent extends BehaviorIntermittent implements GeomUpdater
{
  
  public BehaviorGeomIntermittent(IntermittentBehaviorBuilder builder)
  {
    super(builder);
  }
  
}
