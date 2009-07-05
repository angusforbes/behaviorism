/* BehaviorInterpolated.java ~ Jun 29, 2009 */
package behaviors;

/**
 *
 * @author angus
 */
public class BehaviorRange extends BehaviorInterpolated
{

  protected float ranges[] = null;
  protected float offsets[] = null;

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
  public void tick()
  {
    resetOffsets();
    super.tick();

    if (isActive == true)
    {
      addToOffsets(offsetPercentage);
    }
  }



}