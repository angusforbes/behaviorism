/* ContinuousBehaviorBuilder.java ~ Jul 3, 2009 */
package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorBuilder;
import behaviorism.behaviors.easing.Easing;

/**
 *
 * @author angus
 */
public class ContinuousBehaviorBuilder extends BehaviorBuilder
{

  public boolean isLooping = false;
  public boolean isReversing = false;
  public float ranges[] = null; //protected?
  public Easing easing = null;
  public int repeats = 1;

  public ContinuousBehaviorBuilder(long lengthMS)
  {
    super(lengthMS);
  }

  public ContinuousBehaviorBuilder(long startTime, long lengthMS)
  {
    super(startTime, lengthMS);
  }

  public ContinuousBehaviorBuilder repeats(int repeats)
  {
    this.repeats = repeats;
    return this;
  }

  public ContinuousBehaviorBuilder easing(Easing easing)
  {
    this.easing = easing;
    return this;
  }

  public ContinuousBehaviorBuilder isLooping(boolean isLooping)
  {
    this.isLooping = isLooping;
    if (isLooping == true)
    {
      this.isReversing = false;
    }

    return this;
  }

  public ContinuousBehaviorBuilder isReversing(boolean isReversing)
  {
    this.isReversing = isReversing;

    if (isReversing == true)
    {
      this.isLooping = false;
    }
    return this;

  }

  public ContinuousBehaviorBuilder ranges(float ranges[])
  {
    this.ranges = ranges;

    return this;
  }

  public ContinuousBehaviorBuilder range(float range)
  {
    this.ranges(new float[]
      {
        range
      });

    return this;
  }

  /*
  public ContinuousBehaviorBuilder range(float min, float max)
  {
  this.ranges = new float[]
  {
  max - min
  };
  this.minvals = new float[]
  {
  min
  };

  this.offsets = new float[1];
  return this;
  }


  public ContinuousBehaviorBuilder ranges(float[] min, float max[])
  {
  if (max.length != min.length)
  {
  System.out.println("ERROR!!!");
  //handle this later...
  }

  this.ranges = new float[max.length];
  this.minvals = new float[max.length];
  for (int i = 0; i < ranges.length; i++)
  {
  this.ranges[i] = max[i] - min[i];
  this.minvals[i] = min[i];
  }

  this.offsets = new float[ranges.length];

  return this;
  }
   */
}
