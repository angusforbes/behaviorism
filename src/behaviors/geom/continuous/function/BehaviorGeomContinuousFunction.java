/* BehaviorGeomContinuous.java ~ Aug 17, 2008 */

package behaviors.geom.continuous.function;

import behaviors.geom.*;
import behaviors.BehaviorContinuous;
import functions.Function;

/**
 *
 * @author angus
 */
public abstract class BehaviorGeomContinuousFunction 
  extends BehaviorContinuous implements GeomUpdater, FunctionUpdater
{
  Function function;
  
  public BehaviorGeomContinuousFunction()
  {
  }
  
  public BehaviorGeomContinuousFunction(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }
}
