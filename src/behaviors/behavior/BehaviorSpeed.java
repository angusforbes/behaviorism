/* BehaviorSimple.java ~ Jun 28, 2009 */

package behaviors.behavior;

import behaviors.*;

/**
 * BehaviorSimple is a basic Behavior that updates the Geom every frame.
 * There is no notion of interpolation or time. The behavior will
 * run as long as the current time
 * is after the start time and it hasn't been explicitly interrupted.
 * @author angus
 */
public class BehaviorSpeed extends BehaviorSimple implements BehaviorUpdater
{
  float mult;
  public BehaviorSpeed(long startTime, float mult)
  {
    super(startTime);
    this.mult = mult;
  }

  public void updateBehavior(Behavior b)
  {
    b.changeSpeed(mult);
  }

}
