package behaviors.geom.continuous;

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
    LoopEnum loop,
    Point3f ranges)
  {
    BehaviorTranslate bt = new BehaviorTranslate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).loop(
        loop )
       );

    g.attachBehavior(bt);

    return bt;
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
    Point3f ranges = GeomUtils.subtractPoint3f(endPt, g.translate);
    
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
      g.move(offsets[0], offsets[1], offsets[2]);
    }
  }
}
