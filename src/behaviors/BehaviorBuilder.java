/* BehaviorBuilder.java ~ Jul 2, 2009 */
package behaviors;

import utils.Utils;

/**
 *
 * @author angus
 */
abstract public class BehaviorBuilder
{
  public float startPercent = 0f;
  public long lengthMS = 0L;
  public long startTime = 0L;

  public BehaviorBuilder(long lengthMS)
  {
    this.startTime = Utils.now();
    this.lengthMS = lengthMS;
  }

  public BehaviorBuilder(long startTime, long lengthMS)
  {
    this.startTime = startTime;
    this.lengthMS = lengthMS;
  }

  //public abstract Behavior build();

}

