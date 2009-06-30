/* BehaviorTimed.java ~ Jun 28, 2009 */

package behaviors;

import java.util.ArrayList;
import java.util.List;
import utils.Utils;

/**
 *
 * @author angus
 */
abstract public class BehaviorTimed extends Behavior
{
  public int loopCount = 0;

  protected long relativeStartNano = 0L;
  protected long relativeEndNano = 0L;
 // public long startTime = 0L;
  public long startNano = 0L;
  public long lengthNano = 0L;
  public float percentage = 0f;
  public float startPercent = 0f;
  public long now = 0L;
  public long lastCheck = 0L;
  public LoopEnum loopBehavior = LoopEnum.NONE;
  protected int direction = 1; //this needs to be protected, set only on initialization! -- this is because changing it in midstream would mess everything up!
  public List<AccelerationPoint> aps = new ArrayList<AccelerationPoint>();

  public enum LoopEnum
  {
    // a negative number means "do it forever"
    NONE(0), ONCE(1), REVERSE(-1), CONTINUE(-1), LOOP(-1);

    public int howManyTimes = 1;
    public LoopEnum howManyTimes(int times)
    {
      howManyTimes = times;
      return this;
    }

    LoopEnum(int times)
    {
      howManyTimes = times;
    }
  }

  /*
  public void tick(long nano)
  {
  }
  */

  public void pause(long nano)
  {
    this.startNano = nano - lastCheck;
  }

   public void reverse()
  {
    //System.out.println("!!!! in reverse: lastCheck was = " + Utils.nanosToMillis(lastCheck));
    this.direction *= -1; //switch direction
    //this.lastCheck = relativeStartNano + (lengthNano - lastCheck);
    //long inc = relativeEndNano - lastCheck;
    //System.out.println("inc = " + inc);

    relativeEndNano = lastCheck + (lastCheck - relativeStartNano);
    relativeStartNano = relativeEndNano - lengthNano;

    //this.startNano -= (lengthNano - lastCheck) - lastCheck;
    //this.percentage = 1.0f - getPercentage(lastCheck);
    //this.lastCheck += (lengthNano - lastCheck) - lastCheck;
    System.out.println("in reverse: lastCheck is = " + Utils.nanosToMillis(lastCheck));
  }

   public void changeSpeed(float amount) //i.e. 2f doubles, .5f halves
  {
    if (amount == 0f) {
      amount = .00000001f;
    }
    if (amount < 0f) {
      amount = Math.abs(amount);
      reverse();
    }
    float ratio = 1f / amount;

    this.lengthNano *= ratio;
    this.relativeEndNano = (long) (lastCheck + ((relativeEndNano - lastCheck) * ratio));
    this.relativeStartNano = (long) (lastCheck - ((lastCheck - relativeStartNano) * ratio));
  }

   public long getFlipNano()
  {
    return startNano + lengthNano;
  }

   /**
   * Gets the percentage of time that has passed between the start of this
   * behavior and the end of the behavior. This method does not take in to account
   * at all the values of the AccelrationPoints. It is simply the raw percentage
   * value of time passed.
   */
  public float getRawPercentage(long currentNano)
  {
    return (float) ((float) currentNano / (float) lengthNano); //% of raw time elapsed
  }

  /**
   * Gets the percentage of time that has passed between the start of this
   * behavior and the end of the behavior, appropriately adjusted by the
   * AccelelrationPoints attached to the behavior
   */
  public float getPercentage(long currentNano)
  {
    float perc_time = 0f;
    float perc_dist = 0f;

    float percentageAtMillis = getRawPercentage(currentNano);

    //System.out.println("in getPercentage(long): ( " + currentNano + "/" + lengthNano+ " ) init raw percentageAtMillis... = " + percentageAtMillis);

    if (percentageAtMillis < 0f || percentageAtMillis > 1f) {
      System.out.println("!!!!!!!!!!!!!!!!!!!!!in getPercentage(long): exiting: weird percentage! = " + percentageAtMillis);
    /* Come up with better way to indicate this... this shoudl never happen except perhaps at very beginning when loading */
    //System.exit(0);
    }

    //adjust the raw percentage if there are acceleration points
    float diff_dist, diff_time, part_dist;

    for (int i = 0; i < aps.size(); i++) {
      AccelerationPoint ap = aps.get(i);

      if (percentageAtMillis < ap.percentage_time) {
        diff_dist = ap.percentage_dist - perc_dist;
        diff_time = ap.percentage_time - perc_time;
        part_dist = (diff_dist * (percentageAtMillis - perc_time)) / diff_time;
        percentageAtMillis = (perc_dist + part_dist);
        break;
      } else {
        perc_time = ap.percentage_time;
        perc_dist = ap.percentage_dist;
      }
    }

    //System.out.println("in getPercentage(long): returning percentageAtMillis... = " + percentageAtMillis);

    return (float) percentageAtMillis;
  }

  protected void setAccelerationPoints()
  {
    setAccelerationPoints(null);
  }

  /**
   * Sets the AccelerationPoints for this Behavior. If the input argument is null or empty,
   * then sets a single default AccelerationPoint of (1.0f, 1.0f) so that checking on the
   * AccelerationPoints won't cause a problem.
   */
  public void setAccelerationPoints(List<AccelerationPoint> aps) //just default for now
  {
    if (aps == null || aps.size() == 0) {
      this.aps.add(new AccelerationPoint(1.0f, 1.0f));
    } else {
      this.aps = aps;
    }
  }

    @Override
  public String toString()
  {
    String str = "" + getClass() + " : ";
    if (name != null)
    {
      str += "name=" + name + " : ";
    }
    if (id != null)
    {
      str += "id=" + id + " : ";
    }

    str += "lengthMS=" + Utils.nanosToMillis(lengthNano) + ", percentage="+percentage + ", dir="+ direction +
      " loop="+loopBehavior;
    return str;
  }

}
