/* BehaviorPulse.java ~ Jun 28, 2009 */

package behaviorism.behaviors;

/**
 * BehaviorPulse will become active one time every specified time period.
 * This pulse time can be updated at any time and will take effect immediately
 * if using the changeSpeed method, or after the next pulse if set directly.
 * @author angus
 */
abstract public class BehaviorPulse extends BehaviorDiscrete
{
  public BehaviorPulse(long startTime, long pulse)
  {
    super(startTime, new long[]{0, pulse});
  }

  public void setPulse(long pulse)
  {
    setPulses(new long[]{0, pulse});
  }

  public long getPulse()
  {
    return pulses[1];
  }

}
