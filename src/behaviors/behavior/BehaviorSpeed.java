/* BehaviorSimple.java ~ Jun 28, 2009 */

package behaviors.behavior;

import behaviors.*;
import java.util.Arrays;

/**
 * BehaviorSimple is a basic Behavior that updates the Geom every frame.
 * There is no notion of interpolation or time. The behavior will
 * run as long as the current time
 * is after the start time and it hasn't been explicitly interrupted.
 * @author angus
 */
public class BehaviorSpeed extends BehaviorDiscrete implements BehaviorUpdater
{
  float mult;
  public BehaviorSpeed(long startTime, float mult)
  {
    super(startTime);
    this.mult = mult;
  }

  public BehaviorSpeed(long startTime, long pulse, float mult)
  {
    super(startTime, new long[]{0, pulse});
    this.mult = mult;
  }

  public BehaviorSpeed(long startTime, long[] pulses, float mult)
  {
    super(startTime, pulses);
    this.mult = mult;
  }


  public void updateBehavior(Behavior b)
  {
    b.changeSpeed(mult);
  }

}
