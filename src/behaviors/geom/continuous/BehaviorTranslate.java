package behaviors.geom.continuous;

import behaviors.geom.continuous.BehaviorGeomContinuous;
import behaviors.Behavior.LoopEnum;
import behaviors.BehaviorContinuous.ContinuousBehaviorBuilder;
import geometry.Geom;
import javax.vecmath.Point3f;
import utils.GeomUtils;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class BehaviorTranslate extends BehaviorGeomContinuous
{
  public static BehaviorTranslate translate(
    long startTime, 
    long lengthMS, 
    Point3f ranges)
  {
    return new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).loop(
        //(LoopEnum.REVERSE).howManyTimes(4))
        LoopEnum.ONCE )
        );
  }

  public static BehaviorTranslate translate(
    Geom g,
    long startTime, 
    long lengthMS, 
    Point3f ranges)
  {
    BehaviorTranslate bt = new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).loop(
        LoopEnum.ONCE )
       );
  
    g.attachBehavior(bt);

    return bt;
  }



  public static BehaviorTranslate translateFromTo(
    long startTime, 
    long lengthMS, 
    Point3f startPt,
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, startPt);
    return new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).loop(
        //(LoopEnum.REVERSE).howManyTimes(4))
        LoopEnum.ONCE )
        );
  }

  public static BehaviorTranslate translateTo(
    Geom g,
    long startTime, 
    long lengthMS, 
    Point3f endPt)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, g.anchor);
    
    BehaviorTranslate bt = new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).loop(
        LoopEnum.ONCE )
       );

    g.attachBehavior(bt);

    return bt;
  }

  public BehaviorTranslate(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }
  
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.anchor.x += offsets[0];
      g.anchor.y += offsets[1];
      g.anchor.z += offsets[2];
    }
  }
}
