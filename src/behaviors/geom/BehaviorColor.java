package behaviors.geom;

import behaviors.*;
import geometry.Colorf;
import geometry.Geom;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class BehaviorColor extends BehaviorRange implements GeomUpdater
{

  public static BehaviorColor color(
    Geom g,
    long startTime,
    long lengthMS,
    Colorf color)
  {
    BehaviorColor bt = new BehaviorColor(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(color.array()));
    bt.attachGeom(g);

    return bt;
  }

  public static BehaviorColor colorFromTo(
    Geom g,
    long startTime,
    long lengthMS,
    Colorf color1,
    Colorf color2)
  {
    Colorf color = Colorf.distance(color1, color2);

    BehaviorColor bt = new BehaviorColor(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(color.array()));

    bt.attachGeom(g);

    return bt;
  }

  public static BehaviorColor colorTo(
    Geom g,
    long startTime,
    long lengthMS,
    Colorf color2)
  {
    Colorf color = Colorf.distance(g.color, color2);


    BehaviorColor bt = new BehaviorColor(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(color.array()));

    bt.attachGeom(g);

    return bt;
  }

  public BehaviorColor(ContinuousBehaviorBuilder builder)
  {
    super(builder.startTime, builder.lengthMS, builder.ranges);
    this.isLooping = builder.isLooping;
    this.isReversing = builder.isReversing;
    this.easing = builder.easing;
    this.repeats = builder.repeats;
  }

  public BehaviorColor(long startTime, long lengthMS, Point3f p3f)
  {
    super(startTime, lengthMS, MatrixUtils.toArray(p3f));
  }

  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.color.r += offsets[0];
      g.color.g += offsets[1];
      g.color.b += offsets[2];
      g.color.a += offsets[3];
    }
  }
}
