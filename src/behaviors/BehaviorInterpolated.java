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
  public float rawPercentage = 0f;
  public float prevRawPercentage = 0f;
  public float percentage = 0f;
  public long loopLength = Utils.millisToNanos(1000L);
  int numRepeats = 100;
  int repeat = 0;

  public BehaviorInterpolated(long startTime, long lengthMS)
  {
    super(startTime);
    this.loopLength = Utils.millisToNanos(lengthMS);
  }
  public boolean timeToLoop = false;
  private float overshoot = 0f;
  public float dir = 1f;

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

    this.rawPercentage = getRawPercentage(currentNano - startTime, loopLength);

    if (this.rawPercentage >= 1.0f)
    {
      repeat++;
      System.out.println("repeat = " + repeat);
      //if (reverse)
      //reverse();
      //continueGoing();
      loopGoing();
      timeToLoop = true;

    //once();

    }
    else
    {
      this.offsetPercentage = (rawPercentage - prevRawPercentage) * dir;
      this.prevRawPercentage = rawPercentage;
    }

  //ONCE
//    if (timeToLoop == true)
//    {
//      this.isDone = true;
//    }

  }

  public void continueGoing()
  {
    //up to 1.0 mark
    this.overshoot = this.rawPercentage - 1.0f;
    this.rawPercentage = 1.0f;
    this.offsetPercentage = (rawPercentage - prevRawPercentage);
    this.prevRawPercentage = rawPercentage;

    startTime += loopLength;

    if (repeat < numRepeats)
    {
      //handle overshoot
      this.prevRawPercentage = overshoot;
      this.offsetPercentage += (overshoot);
    }
    else
    {
      this.isDone = true;
    }
  }


  public void loopGoing()
  {
    //up to 1.0 mark
    this.overshoot = this.rawPercentage - 1.0f;
    this.rawPercentage = 1.0f;
    this.offsetPercentage = (rawPercentage - prevRawPercentage);
    this.prevRawPercentage = rawPercentage;

    startTime += loopLength;

    if (repeat < numRepeats)
    {

      //handle overshoot
      this.prevRawPercentage = overshoot;
      this.offsetPercentage += (overshoot);
    }
    else
    {
      this.isDone = true;
    }
  }

  public void reverse()
  {
    //up to 1.0 mark
    this.overshoot = this.rawPercentage - 1.0f;
    this.rawPercentage = 1.0f;
    this.offsetPercentage = (rawPercentage - prevRawPercentage) * dir;
    this.prevRawPercentage = rawPercentage;


    dir *= -1f;
    startTime += loopLength;

    if (repeat < numRepeats)
    {

      //handle overshoot
      this.prevRawPercentage = overshoot;
      this.offsetPercentage += (overshoot * dir);
    }
    else
    {
      this.isDone = true;
    }
  }

  public void once()
  {
    this.overshoot = this.rawPercentage - 1.0f;
    this.rawPercentage = 1.0f;
    this.offsetPercentage = (rawPercentage - prevRawPercentage) * dir;
    this.prevRawPercentage = rawPercentage;
    this.isDone = true;
  }

  public float getRawPercentage(long currentNano, long loopNano)
  {
    return (float) ((float) currentNano / (float) loopNano); //% of raw time elapsed
  }
}
