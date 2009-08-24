/* BehaviorInterpolated.java ~ Jun 29, 2009 */
package behaviorism.behaviors;

import behaviorism.behaviors.easing.Easing;
import behaviorism.utils.Utils;
import static behaviorism.utils.RenderUtils.*;

/**
 *
 * @author angus
 */
public class BehaviorInterpolated extends Behavior
{

  public float offsetPercentage = 0f;
  public float percentage = 0f;
  public long loopLength;
  public int repeats = 1;
  public int repeat = 0;
  public boolean isReversing = false;
  public boolean isLooping = false;
  public boolean timeToLoop = false;
  public int dir = 1; //1 = forward, -1 = backward
  public Easing easing = null; // = new EasingPull();
  private float overshoot = 0f;
  private float prevPercentage = 0f;

  public BehaviorInterpolated(long startTime, long lengthMS)
  {
    super(startTime);
    this.loopLength = Utils.millisToNanos(lengthMS);
  }

  public void setStartingPercentage(float startPerc)
  {
    if (startPerc != 0f)
    {
      prevPercentage = startPerc;
      System.out.println("loopLength * startPerc = " + Utils.nanosToMillis(loopLength * startPerc));
      nextTime += (loopLength * startPerc);
    }
  }

  private float calculateOffsetPercentage(float perc, float prevPerc, int dir)
  {
    float offPerc;
    if (easing == null)
    {
      offPerc = perc - prevPerc;
    }
    else
    {
      offPerc = easing.getPercentage(perc) - easing.getPercentage(prevPerc);
    }

    return offPerc * dir;
  }

  public void tick()
  {
    timeToLoop = false;
    isActive = (false);

    if (isInterrupted == true && interruptNano <= getTick())
    {
      this.isDone = (true);
      return;
    }

    if (getTick() < nextTime)
    {
      //System.out.println("too soon..." + Utils.nanosToMillis(nextTime - getTick()));
      return;
    }

    isActive = (true);
    this.percentage = getRawPercentage(getTick() - nextTime, loopLength);

    if (this.percentage >= 1.0f)
    {
      this.overshoot = this.percentage - 1.0f;
      this.percentage = 1.0f;
      this.offsetPercentage = calculateOffsetPercentage(percentage, prevPercentage, dir);
      this.prevPercentage = percentage;

      timeToLoop = true;

      if (repeats > -1) //-1 is our code for repeating forever
      {
        repeat++;
        if (repeat >= repeats)
        {
          this.isDone = (true);
          return;
        }
      }

      if (isReversing == true)
      {
        reverseBehavior();
      }
      else if (isLooping == true)
      {
        loopBehavior();
      }
      else
      {
        continueBehavior();
      }

    }
    else
    {
      this.offsetPercentage = calculateOffsetPercentage(percentage, prevPercentage, dir);

      // System.out.println("\n***percentage = " + percentage);
//      System.out.println("prev percentage = " + prevPercentage);
//      System.out.println("eased percentage = " + easing.getPercentage(percentage));
//      System.out.println("eased ppercentage = " + easing.getPercentage(prevPercentage));
//      System.out.println("offsetPercentage = " + offsetPercentage + "***\n");
      this.prevPercentage = percentage;
    }

  }

  public void continueBehavior()
  {
    nextTime += loopLength;
    this.prevPercentage = overshoot;

    if (easing == null)
    {
      this.offsetPercentage += overshoot;
    }
    else
    {
      this.offsetPercentage += easing.getPercentage(overshoot);
    }
  }

  public void loopBehavior()
  {
    nextTime += loopLength;

    this.prevPercentage = overshoot;
    if (easing == null)
    {
      this.offsetPercentage += (-1f + (overshoot));
    }
    else
    {
      this.offsetPercentage += (-1f + (easing.getPercentage(overshoot)));
    }
  }

  public void reverseBehavior()
  {
    dir *= -1f;
    nextTime += loopLength;

    //handle overshoot
    this.prevPercentage = overshoot;

    if (easing == null)
    {
      this.offsetPercentage += overshoot * dir;
    }
    else
    {
      this.offsetPercentage += easing.getPercentage(overshoot) * dir;
    }
  }

  public float getRawPercentage(long currentNano, long loopNano)
  {
    return (float) ((float) currentNano / (float) loopNano); //% of raw time elapsed
  }

  @Override
  public void changeSpeed(float increase)
  {
    //update pulses
    float ratio = 1f / increase;

    loopLength *= ratio;
    //and we can't forget to correctly update speed of current cycle
    nextTime = getTick() + (long) ((nextTime - getTick()) * ratio);
  }

  @Override
  public void reverse()
  {
    nextTime = getTick() + (Utils.millisToNanos(loopLength) - (nextTime - getTick()));
    dir *= -1;
  }
}
