package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorRange;
import behaviorism.behaviors.Scheduler;
import behaviorism.geometry.Geom;
import behaviorism.utils.GeomUtils;
import behaviorism.utils.MatrixUtils;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class BehaviorTranslate extends BehaviorRange implements GeomUpdater
{
 

  /**
   * Creates a BehaviorTranslate without automatically attaching it to a Geom.
   * @param startTime
   * @param lengthMS
   * @param range
   * @return
   */
  public static BehaviorTranslate translate(
    long startTime,
    long lengthMS,
    Point3f range)
  {
    return new BehaviorTranslate(startTime, lengthMS, range);
  }

  public static BehaviorTranslate translate(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorTranslate bt = new BehaviorTranslate(startTime, lengthMS, range);
    //bt.attachGeom(g);
    Scheduler.getInstance().attachGeom(bt, g);
    return bt;
  }

  public static BehaviorTranslate translateFromTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f startPt,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, startPt);


    BehaviorTranslate bt = new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges))
        );
    bt.attachGeom(g);

    return bt;
  }

  public static BehaviorTranslate translateTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, g.translate);

    BehaviorTranslate bt = new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)));

    Scheduler.getInstance().attachGeom(bt, g);

    //bt.attachGeom(g);

    return bt;
  }

  public BehaviorTranslate(ContinuousBehaviorBuilder builder)
  {
    super(builder.startTime, builder.lengthMS, builder.ranges);
    this.isLooping = builder.isLooping;
    this.isReversing = builder.isReversing;
    this.easing = builder.easing;
    this.repeats = builder.repeats;
  }

  public BehaviorTranslate(long startTime, long lengthMS, Point3f p3f)
  {
    super(startTime, lengthMS, MatrixUtils.toArray(p3f));
  }
  
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.translate(offsets[0], offsets[1], offsets[2]);
    }
  }
}
