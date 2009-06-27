/* BehaviorGeom.java (created on August 28, 2007, 12:54 PM) */
package behaviors;

import geometry.Geom;
import utils.Utils;

abstract public class BehaviorGeom extends Behavior
{
  /*
   * BehaviorTranslate = new BehaviorTranslate.Builder(startNano, lenghtMS).
  loop(LoopEnum.ONCE).startPercent(0f).ranges(float[] min, float[] max);
   */

  public static class BehaviorBuilder
  {
    //really want these to be protected? think about this..
    public float startPercent = 0f;
    public long lengthNano = 0L;
    public long startTime = 0L;
    public LoopEnum loopBehavior = LoopEnum.ONCE;
    //public  float ranges[] = null;
    //public  float offsets[] = null;
    //public  List<AccelerationPoint> accelerationPoints = null;

    public BehaviorBuilder(long lengthMS)
    {
      this.startTime = Utils.now();
      this.lengthNano = Utils.millisToNanos(lengthMS);
    }

    public BehaviorBuilder(long startTime, long lengthMS)
    {
      this.startTime = startTime;
      this.lengthNano = Utils.millisToNanos(lengthMS);
    }
  }

  public void initBehavior(long startTime, long lengthMS, LoopEnum loopBehavior,
    float startPercent)
  {
    if (startPercent < 0f || startPercent > 1f)
    {
      System.err.println("startPercent must be between 0f and 1f!");
    }

    this.startPercent = startPercent;

    this.lengthNano = Utils.millisToNanos(lengthMS);

    this.startTime = startTime; //nothing will happen before the startTime

    //determine what will happen after startTime... ie for startPercent != 0f
    this.startNano = this.startTime - (long) (this.lengthNano * startPercent);
    this.lastCheck = (long) (this.lengthNano * startPercent);

    this.loopBehavior = loopBehavior;

    setAccelerationPoints();

    relativeStartNano = 0L;
    relativeEndNano = lengthNano;
  }


  protected void initRange()
  {
  }
  
  //each subclass needs to do this...
  abstract public void change(Geom g);
} 

