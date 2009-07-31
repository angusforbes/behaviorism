/* BehaviorPulse.java ~ Jun 28, 2009 */

package behaviors;

import static utils.RenderUtils.*;
import static utils.Utils.*;

/**
 * BehaviorPulse will become active one time every specified time period.
 * This pulse time can be updated at any time and will take effect immediately
 * if using the changeSpeed method, or after the next pulse if set directly.
 * @author angus
 */
abstract public class BehaviorPulseOld extends BehaviorSimple
{
  public long pulse = 1000L; //in milliseconds

  public BehaviorPulseOld(long startTime, long pulse)
  {
    super(startTime);
    setPulse(pulse);
  }

  public void setPulse(long pulse)
  {
    this.pulse = pulse;
  }

  @Override
  public void tick()
  {
    isActive = false;

    if (isInterrupted == true && interruptNano <= getTick())
    {
      this.isDone = true;
      return;
    }

    if (getTick() < nextTime)
    {
      return;
    }

    isActive = true;

    nextTime += millisToNanos(pulse);
  }

  @Override
  public void changeSpeed(float increase)
  {
    //update pulses
    float ratio = 1f/increase;

    pulse *= ratio;

    //and we can't forget to correctly update speed of current cycle
    nextTime = getTick() + (long)((nextTime - getTick()) * ratio);
  }


  @Override
  public void reverse()
  {
    nextTime = getTick() + (getTick() - millisToNanos(pulse));
  }
}
