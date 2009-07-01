/* BehaviorPulse.java ~ Jun 28, 2009 */

package behaviors;

import utils.Utils;

/**
 * BehaviorPulse will become active one time every specified time period.
 * This pulse time can be updated at any time and will take effect after the next
 * scheduled pulse. The on/off value is toggled at every pulse, or you can set it
 * manually whenever. The idea is that your updateGeom/Behavior method will
 * refer to this on/off value and use it as needed.
 * @author angus
 */
abstract public class BehaviorSwitch extends BehaviorPulse
{
  public boolean on = true;

  public BehaviorSwitch(long startTime, long pulse)
  {
    super(startTime, pulse);
  }

  public void setOn(boolean on)
  {
    this.on = on;
  }
  public void on()
  {
    this.on = true;
  }
  public void off()
  {
    this.on = false;
  }
  public void toggleSwitch()
  {
    this.on = !this.on;
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
    toggleSwitch();
  }

}
