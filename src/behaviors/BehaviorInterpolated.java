/* BehaviorInterpolated.java ~ Jun 29, 2009 */
package behaviors;

import utils.Utils;

/**
 *
 * @author angus
 */
public class BehaviorInterpolated extends Behavior
{

  public float offsetPercentage = 0f;
  public float percentage = 0f;
  public long loopLength;
  public int numRepeats = 1;
  public int repeat = 0;
  public boolean isReversing = false;
  public boolean timeToLoop = false;
  public int dir = 1; //1 = forward, -1 = backward

  private float overshoot = 0f;
  private float prevPercentage = 0f;


  public BehaviorInterpolated(long startTime, long lengthMS)
  {
    super(startTime);
    this.loopLength = Utils.millisToNanos(lengthMS);
  }
  
  public void tick(long currentNano)
  {
    timeToLoop = false;
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
    this.percentage = getRawPercentage(currentNano - startTime, loopLength);

    if (this.percentage >= 1.0f)
    {
      this.overshoot = this.percentage - 1.0f;
      this.percentage = 1.0f;
      this.offsetPercentage = (percentage - prevPercentage) * dir;
      this.prevPercentage = percentage;

      timeToLoop = true;

      repeat++;
      if (repeat >= numRepeats)
      {
        this.isDone = true;
      }
      else
      {
        if (isReversing == true)
        {
          reverseBehavior();
        }
        else
        {
          continueBehavior();
        }
        //loopBehavior(); //no sense of loop in simple version...
      }
    }
    else
    {
      this.offsetPercentage = (percentage - prevPercentage) * dir;
      System.out.println("HERE offsetPercentage = " + offsetPercentage);
      this.prevPercentage = percentage;
    }

  }

  public void continueBehavior()
  {
    startTime += loopLength;

    this.prevPercentage = overshoot;
    this.offsetPercentage += (overshoot);
  }

  public void loopBehavior()
  {
    startTime += loopLength;

    this.prevPercentage = overshoot;
    this.offsetPercentage += (overshoot);
  }

  public void reverseBehavior()
  {
    dir *= -1f;
    startTime += loopLength;

    //handle overshoot
    this.prevPercentage = overshoot;
    this.offsetPercentage += (overshoot * dir);
  }

  public float getRawPercentage(long currentNano, long loopNano)
  {
    return (float) ((float) currentNano / (float) loopNano); //% of raw time elapsed
  }
}
