/* BehaviorInterpolated.java ~ Jun 29, 2009 */
package behaviors;

import utils.Utils;

/**
 *
 * @author angus
 */
public class BehaviorRange extends BehaviorInterpolated
{
  protected float ranges[] = null;
  protected float offsets[] = null;

  public boolean isLooping = false; //true = pop back to start, false = keep going...

  public BehaviorRange(long startTime, long lengthMS, float[] ranges)
  {
    super(startTime, lengthMS);
    this.ranges = ranges;
    this.offsets = new float[ranges.length];
  }


  final protected void resetOffsets()
  {
    for (int i = 0; i < ranges.length; i++)
    {
      offsets[i] = 0f;
    }
  }

  final protected void addToOffsets(float percentage)
  {
    for (int i = 0; i < ranges.length; i++)
    {
      offsets[i] += ranges[i] * percentage;
    }
  }

  final protected void subtractFromOffsets(float percentage)
  {
    for (int i = 0; i < ranges.length; i++)
    {
      offsets[i] -= ranges[i] * percentage;
    }
  }

  final public float[] getOffsets()
  {
    return offsets;
  }

  final public float[] getRanges()
  {
    return ranges;
  }

  @Override
  public void tick(long currentNano)
  {
    resetOffsets();
    super.tick(currentNano);

    if (isActive == true)
    {
      
      if (timeToLoop == true && isLooping == true)
      {
        subtractFromOffsets(1.0f);
      }

      addToOffsets(offsetPercentage);
    }
  }
}
