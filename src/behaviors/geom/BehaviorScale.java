package behaviors.geom;

import behaviors.*;
import geometry.Geom;
import javax.vecmath.Point3f;
import utils.GeomUtils;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class BehaviorScale extends BehaviorRange implements GeomUpdater
{
 

  public static BehaviorScale scale(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorScale bt = new BehaviorScale(startTime, lengthMS, range);
    bt.attachGeom(g);

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

    BehaviorScale bt = new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)));
       
    bt.attachGeom(g);

    return bt;
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
  
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.scale(offsets[0], offsets[1], offsets[2]);
    }
  }
}
