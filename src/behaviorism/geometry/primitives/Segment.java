/* Segment.java ~ Sep 18, 2009 */

package behaviorism.geometry.primitives;

import behaviorism.utils.GeomUtils;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author angus
 */
public class Segment 
{
    public Point3f p1;
    public Point3f p2;

    public Segment(Point3f p1, Point3f p2)
    {
      this.p1 = p1;
      this.p2 = p2;
    }

    public Segment(Point3f p1, Vector3f v)
    {
      this.p1 = p1;
      this.p2 = new Point3f(p1);
      p2.add(v);
    }

    public Point3f getMidpoint()
    {
      return getPoint(.5f);
    }

    public Point3f getPoint(float perc)
    {
      return GeomUtils.getPercPointOfTwoPoints(p1, p2, perc);
    }

    public Vector3f getTangent()
    {
      Vector3f v = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
      v.normalize();
      return v;
    }

    public Vector3f getPerpindicularTangent(int dir)
    {
      Segment s = getPerpindicularSegment(0f, 1f, dir);
      return new Vector3f(s.p2.x - p1.x, s.p2.y - p1.y, s.p2.z - p1.z);
    }

    //needs a direction argument... +1 or -1
    public Segment getPerpindicularSegment(final float startDist, final float endDist, final int dir)
    {
      return GeomUtils.getPerpindicularSegment(this, startDist, endDist, dir);
    }

    public Segment getPerpindicularSegment(float length)
    {
      return GeomUtils.getPerpindicularSegment(this, length);
    }

    public Point3f getIntersection(final Segment s)
    {
      //calc intersection if there is one, else return null
      return GeomUtils.getIntersectionBetweenLineSegments2(this, s);
    }

    public String toString()
    {
      return "Segment: start=" + p1 + ", end=" + p2;

    }
}
