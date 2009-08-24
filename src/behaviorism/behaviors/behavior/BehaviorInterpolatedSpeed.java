/* BehaviorInterpolatedSpeed.java ~ Jul 6, 2009 */

package behaviorism.behaviors.behavior;

import behaviorism.behaviors.Behavior;
import behaviorism.behaviors.BehaviorDiscrete;
import behaviorism.behaviors.BehaviorRange;

/**
 *
 * @author angus
 */
public class BehaviorInterpolatedSpeed extends BehaviorRange implements BehaviorUpdater
{
  public BehaviorInterpolatedSpeed(long startTime, long lengthMS, long min, long max)
  {
    super(startTime, lengthMS, new float[]{(float)max - (float)min});
  }
  
  public long sum(long[] array)
  {
    long sum = 0L;
    for (long num : array)
    {
      sum += num;
    }
    return sum;
  }
  public void updateBehavior(Behavior b)
  {
    if (b instanceof BehaviorDiscrete)
    {
      sum(((BehaviorDiscrete)b).lengths);


    }
  //  b.changeSpeed(mult);
  }

}
