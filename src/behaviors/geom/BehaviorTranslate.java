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
public class BehaviorTranslate extends BehaviorRange implements GeomUpdater
{
 

  public static BehaviorTranslate translate(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f range)
  {
    BehaviorTranslate bt = new BehaviorTranslate(startTime, lengthMS, range);
    bt.attachGeom(g);

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
       
    bt.attachGeom(g);

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
  
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.translate(offsets[0], offsets[1], offsets[2]);
    }
  }
}
