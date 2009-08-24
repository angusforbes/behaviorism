/* BehaviorPulse.java ~ Jun 28, 2009 */

package behaviorism.behaviors;

/**
 * BehaviorCount will become active one time every specified time period, at which point
 * the counter will be increased (or decreased) according to the current increment.
 * Classes extending from BehaviorCount can query the current count and use it
 * for some counting purpose.
 * @author angus
 */
abstract public class BehaviorCount extends BehaviorPulse
{
  protected int count = 0;
  protected int increment = 1;

  public BehaviorCount(long startTime, long pulse)
  {
    super(startTime, pulse);
  }

  public BehaviorCount(long startTime, long pulse, int startCount, int incrementCount)
  {
    super(startTime, pulse);
    this.count = startCount;
    this.increment = incrementCount;
  }

  @Override
  public void tick()
  {
    super.tick();
    if (isActive)
    {
      count();
    }

  }

  public void count()
  {
    this.count += increment;
  }

  public void setCount(int count)
  {
    this.count = count;
  }

  public int getCount()
  {
    return this.count;
  }


  @Override
  public void reverse()
  {
    super.reverse();
    //nextTime = getTick() + (getTick() - millisToNanos(pulse));
    increment *= -1;
  }
}
