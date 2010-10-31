/* CircularArc.java ~ Jun 3, 2010 */
package behaviorism.geometry.primitives;

import behaviorism.utils.GeomUtils;
import java.util.List;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class CircularArc extends Circle
{
  public float startAngle;
  public float endAngle;
  public int direction;

  public CircularArc(Point3f centerPt, float radius, float startAngle, float endAngle) //angle and endAngle in radians
  {
    super(centerPt, radius);
    this.startAngle = (float)GeomUtils.clampAngle(startAngle);
    this.endAngle = (float)GeomUtils.clampAngle(endAngle);
    this.direction = COUNTERCLOCKWISE;
  }

  public CircularArc(Point3f centerPt, float radius, float startAngle, float endAngle, int direction) //angle and endAngle in radians
  {
    super(centerPt, radius);
    this.startAngle = (float)GeomUtils.clampAngle(startAngle);
    this.endAngle = (float)GeomUtils.clampAngle(endAngle);
    this.direction = direction;
  }
  @Override
  public List<Point3f> getPoints(int resolution)
  {
    return getPoints(this.startAngle, this.endAngle, resolution, this.direction);
  }

  @Override
  public List<Point3f> getPoints(int resolution, int dir)
  {
    return getPoints(startAngle, endAngle, resolution, dir);
  }

  //put these in GeomUtils?
  public static CircularArc makeArcFromCircle(Circle c, float startAngle, float endAngle)
  {
    return new CircularArc(c.centerPt, c.radius, startAngle, endAngle);
  }

  public String toString()
  {
    return "" + centerPt + ", radius=" + radius + ", startAngle = " + startAngle + ", endAngle = " + endAngle +
      ", direction = " + direction;
  }
}
