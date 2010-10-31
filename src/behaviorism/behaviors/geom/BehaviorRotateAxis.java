package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorRange;
import behaviorism.behaviors.Scheduler;
import behaviorism.geometry.Geom;
import behaviorism.utils.MatrixUtils;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author angus
 */
public class BehaviorRotateAxis extends BehaviorRange implements GeomUpdater
{
  Vector3f axis;

  public static BehaviorRotateAxis rotate(
    Geom g,
    long startTime,
    long lengthMS,
    Vector3f axis,
    float angle)
  {
    BehaviorRotateAxis bt = new BehaviorRotateAxis(startTime, lengthMS, axis, angle);

    Scheduler.getInstance().attachGeom(bt, g);

    //bt.attachGeom(g);

    return bt;
  }

  public BehaviorRotateAxis(ContinuousBehaviorBuilder builder)
  {
    super(builder.startTime, builder.lengthMS, builder.ranges);
    this.isLooping = builder.isLooping;
    this.isReversing = builder.isReversing;
    this.easing = builder.easing;
    this.repeats = builder.repeats;
  }

  public BehaviorRotateAxis(long startTime, long lengthMS, Vector3f axis, float angle)
  {
    super(startTime, lengthMS, new float[]{angle});
    this.axis = axis;
  }
  
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      System.err.println("offset[0] = " + offsets[0]);
      g.rotateAxis(this.axis, offsets[0]);
    }
  }
}
