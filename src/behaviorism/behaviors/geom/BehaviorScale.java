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
public class BehaviorScale extends BehaviorRange implements GeomUpdater
{

   public static BehaviorScale scale(
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorScale bs = new BehaviorScale(startTime, lengthMS, range);
    return bs;

   }

  public static BehaviorScale scale(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorScale bt = new BehaviorScale(startTime, lengthMS, range);
    Scheduler.getInstance().attachGeom(bt, g);
    return bt;
  }

  public static BehaviorScale scaleFromTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f startPt,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, startPt);

    BehaviorScale bt = new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges))
        );
    bt.attachGeom(g);

    return bt;
  }

  public static BehaviorScale scaleTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, MatrixUtils.toPoint3f(g.scale));

    BehaviorScale bs = new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).
      isReversing(true) );
       
    bs.attachGeom(g);

    return bs;
  }

  public BehaviorScale(ContinuousBehaviorBuilder builder)
  {
    super(builder.startTime, builder.lengthMS, builder.ranges);
    this.isLooping = builder.isLooping;
    this.isReversing = builder.isReversing;
    this.easing = builder.easing;
    this.repeats = builder.repeats;
  }

  public BehaviorScale(long startTime, long lengthMS, Point3f p3f)
  {
    super(startTime, lengthMS, MatrixUtils.toArray(p3f));
  }
  
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.scale(offsets[0], offsets[1], offsets[2]);
    }
  }
}
