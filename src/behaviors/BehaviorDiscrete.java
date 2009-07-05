/* BehaviorDiscrete.java ~ Jul 3, 2009 */
package behaviors;

import static utils.RenderUtils.*;
import utils.Utils;

/**
 *
 * @author angus
 */
public class BehaviorDiscrete extends Behavior
{

  protected long[] lengths = null;
  protected long[] pulses = null;
  protected int index = 0;
  public int repeats = 1;
  public int repeat = 0;
  public boolean isLooping = true;
  public boolean isReversing = false;
  public boolean timeToLoop = false;
  public int dir = 1;

  //pulses should start at 0, and then increase
  public BehaviorDiscrete(long startTime, long[] pulses)
  {
    super(startTime);
    this.pulses = pulses;
    pulsesToLengths();
  }

  public void setPulses(long[] pulses)
  {
    this.pulses = pulses;
    pulsesToLengths();
  }

  //lengths will be one length less than pulses
  protected void pulsesToLengths()
  {
    lengths = new long[pulses.length - 1];

    for (int i = 1; i < pulses.length; i++)
    {
      lengths[i - 1] = pulses[i] - pulses[i - 1];
    }
  }

  @Override
  public void tick()
  {

    isActive = false;
    timeToLoop = false;

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

    if (index >= lengths.length || index < 0)
    {
      repeat++;
      timeToLoop = true;

      if (repeat < repeats)
      {
        if (isReversing == true)
        {
          reverseBehavior();
        }
        else
        {
          loopBehavior();
        }
      }
      else
      {
        this.isDone = (true);
        return;
      }
    }

//    System.out.println("\n dir = " + dir);
//    System.out.println("index = " + index + ", we will sleep for " + lengths[index] + " ms");
    nextTime += Utils.millisToNanos(lengths[index]);
    index+=dir;
  }

  public void loopBehavior()
  {
    index = 0;
  }

  public void reverseBehavior()
  {
    dir *= -1;
    index += dir;;
  }

  @Override
  public void changeSpeed(float increase)
  {
    //update pulses
    float ratio = 1f/increase;
    for (int i = 0; i < pulses.length; i++)
    {
      pulses[i] *= ratio;
    }

    //update lengths
    pulsesToLengths();

    //and we can't forget to correctly update speed of current cycle
    nextTime = getTick() + (long)((nextTime - getTick()) * ratio);
  }

  @Override
  public void reverse()
  {
    dir *= -1;
    nextTime = getTick() + (Utils.millisToNanos(lengths[index]) - (nextTime - getTick()));
  }

}
