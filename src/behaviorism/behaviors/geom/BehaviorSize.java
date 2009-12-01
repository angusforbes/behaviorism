package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorRange;
import behaviorism.geometry.Geom;
import behaviorism.utils.GeomUtils;
import behaviorism.utils.MatrixUtils;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class BehaviorSize extends BehaviorRange implements GeomUpdater
{
 

  public static BehaviorSize size(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorSize bt = new BehaviorSize(startTime, lengthMS, range);
    bt.attachGeom(g);

    return bt;
  }


  public static BehaviorSize sizeFromTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f startPt,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, startPt);


    BehaviorSize bt = new BehaviorSize(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges))
        );
    bt.attachGeom(g);

    return bt;
  }

  public static BehaviorSize sizeTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, new Point3f(g.w, g.h, g.d));

    BehaviorSize bt = new BehaviorSize(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)));
       
    bt.attachGeom(g);

    return bt;
  }

  public BehaviorSize(ContinuousBehaviorBuilder builder)
  {
    super(builder.startTime, builder.lengthMS, builder.ranges);
    this.isLooping = builder.isLooping;
    this.isReversing = builder.isReversing;
    this.easing = builder.easing;
    this.repeats = builder.repeats;
  }

  public BehaviorSize(long startTime, long lengthMS, Point3f p3f)
  {
    super(startTime, lengthMS, MatrixUtils.toArray(p3f));
  }
  
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.w += offsets[0];
      g.h += offsets[1];
      g.d += offsets[2];

      //g.size(offsets[0], offsets[1], offsets[2]);
    }
  }
}
