/* DebugTimer.java ~ Jan 25, 2009 */

package utils;

/**
 *
 * @author angus
 */
public class DebugTimer 
{
  long time;
  public DebugTimer()
  {
    resetTime();
  }

  public long checkTime()
  {
    return Utils.nanosToMillis(Utils.now() - time);
  }

  public long resetTime()
  {
    long prevTime = time;

    this.time = Utils.now();

    return Utils.nanosToMillis(time - prevTime);
  }

  public long getTime()
  {
    return time;
  }

  public void setTime(long nano)
  {
    this.time = nano;
  }
}
