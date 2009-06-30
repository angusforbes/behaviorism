/* BehaviorPulse.java ~ Jun 28, 2009 */

package behaviors;

import utils.Utils;

/**
 * BehaviorPulse will become active one time every specified time period.
 * This pulse time can be updated at any time and will take effect after the next
 * scheduled pulse.
 * @author angus
 */
abstract public class BehaviorPulse extends BehaviorSimple
{
  public long pulse = 1000L; //in milliseconds

  public BehaviorPulse(long startTime)
  {
    super(startTime);
  }

  public void setPulse(long pulse)
  {
    this.pulse = pulse;
  }

  @Override
  public void tick(long currentNano)
  {
    isActive = false;

    if (isInterrupted == true && interruptNano <= currentNano)
    {
      this.isDone = true;
      return;
    }

    if (currentNano < startTime)
    {
      return;
    }

    isActive = true;

    startTime += Utils.millisToNanos(pulse);
  }

}
