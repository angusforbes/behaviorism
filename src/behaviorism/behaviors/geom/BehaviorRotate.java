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
public class BehaviorRotate extends BehaviorRange implements GeomUpdater
{
 

  public static BehaviorRotate rotate(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorRotate bt = new BehaviorRotate(startTime, lengthMS, range);

    Scheduler.getInstance().attachGeom(bt, g);

    //bt.attachGeom(g);

    return bt;
  }

  public static BehaviorRotate rotateFromTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f startPt,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, startPt);


    BehaviorRotate bt = new BehaviorRotate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges))
        );
    //bt.attachGeom(g);
    Scheduler.getInstance().attachGeom(bt, g);
    return bt;
  }

  public static BehaviorRotate rotateTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, MatrixUtils.toPoint3f(g.rotate));

    BehaviorRotate bt = new BehaviorRotate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)));

    Scheduler.getInstance().attachGeom(bt, g);
    //bt.attachGeom(g);

    return bt;
  }

  public BehaviorRotate(ContinuousBehaviorBuilder builder)
  {
    super(builder.startTime, builder.lengthMS, builder.ranges);
    this.isLooping = builder.isLooping;
    this.isReversing = builder.isReversing;
    this.easing = builder.easing;
    this.repeats = builder.repeats;
  }

  public BehaviorRotate(long startTime, long lengthMS, Point3f p3f)
  {
    super(startTime, lengthMS, MatrixUtils.toArray(p3f));
  }
  
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.rotate(offsets[0], offsets[1], offsets[2]);
    }
  }
}
