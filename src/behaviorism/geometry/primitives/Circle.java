/* Circle.java ~ Jun 2, 2010 */

package behaviorism.geometry.primitives;

import behaviorism.utils.GeomUtils;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author angus
 */
public class Circle 
{
  public static final int CLOCKWISE = -1;
  public static final int COUNTERCLOCKWISE = +1;
  public static float PI = (float) (Math.PI);
  public static float TWOPI = (float) (2.0 * Math.PI);
  public static float HALFPI = (float) (0.5 * Math.PI);

  public Point3f centerPt;
  public float radius;

  public Circle(Point3f centerPt, float radius)
  {
    this.centerPt = centerPt;
    this.radius = radius;
  }

  public String toString()
  {
    return "Circle: " + centerPt + ", radius="+ radius;
  }
  
  public Segment getSegment(float angle)
  {
    return getSegment(angle, radius);
  }

  public Segment getSegment(float angle, float length)
  {
    return new Segment(centerPt, getPoint(angle, length));
  }

  public Segment getTangentSegment(float angle, int dir)
  {
    return getTangentSegment(angle, dir, 1f);
  }

  public Segment getTangentSegment(float angle, int dir, float length)
  {
    Point3f pt = getPoint(angle);
    return new Segment(pt, getTangentVector(angle, dir, length));
  }

  public Vector3f getTangentVector(float angle, int dir)
  {
    return getTangentVector(angle, dir, 1f);
  }

  public Vector3f getTangentVector(float angle, int dir, float length)
  {
    Vector3f vec = null;

    if (dir == COUNTERCLOCKWISE)
    {
       vec = new Vector3f(
        (float)(Math.cos(angle + HALFPI)),
        (float)(Math.sin(angle + HALFPI)),
        0f);
    }
    else if (dir == CLOCKWISE)
    {
       vec = new Vector3f(
        (float)(Math.cos(angle - HALFPI)),
        (float)(Math.sin(angle - HALFPI)),
        0f);
    }

    vec.normalize();
    vec.scale(length);
    return vec;
  }

  public Segment getChord(float angle1, float angle2)
  {
    return new Segment(getPoint(angle1), getPoint(angle2));
  }

  public Point3f getPoint(float angle) //angle in radians
  {
    return getPoint(angle, radius);
  }

  public Point3f getPoint(float angle, float length) //angle in radians
  {
    Point3f pt = new Point3f(
       (float)(centerPt.x + Math.cos(angle) * length),
       (float)(centerPt.y + Math.sin(angle) * length),
       0f);

    //System.out.println("pt = " + pt);
    return pt;
  }

  public List<Point3f> getPoints(int resolution)
  {
    return getPoints(resolution, COUNTERCLOCKWISE);
  }

  public List<Point3f> getPoints(int resolution, int dir)
  {
    List<Point3f> pts = new ArrayList<Point3f>();

    float inc = TWOPI / (float) resolution;

    if (dir == COUNTERCLOCKWISE)
    {
      for (int i = 0; i < resolution; i++)
      {
     //   System.out.println("i * inc = " + (i * inc));

        pts.add(getPoint(i * inc));
      }
    }
    else if (dir == CLOCKWISE)
    {
      for (int i = 0; i < resolution; i++)
      {
        pts.add(getPoint(-i * inc));
      }
    }

    return pts;
  }

  public List<Point3f> getPoints(float startAngle, float endAngle, int resolution)
  {
    return getPoints(startAngle, endAngle, resolution, COUNTERCLOCKWISE);
  }

  public List<Point3f> getPoints(float startAngle, float endAngle, int resolution, int dir)
  {
    List<Point3f> pts = new ArrayList<Point3f>();
 // System.out.println("endAngle = " + endAngle);
  //    System.out.println("startAngle = " + startAngle);

    startAngle = (float) GeomUtils.clampAngle(startAngle);
    endAngle = (float) GeomUtils.clampAngle(endAngle);
  //System.out.println("CLAMPED endAngle = " + endAngle);
   //   System.out.println("CLAMPED startAngle = " + startAngle);
    
    if (dir == COUNTERCLOCKWISE)
    {
      if (endAngle < startAngle)
      {
        endAngle += TWOPI;
      }
       //System.out.println("CCW NOW endAngle = " + endAngle);
      //System.out.println("CCW NOW startAngle = " + startAngle);


     // System.out.println("resolution = " + resolution);
      float inc = (endAngle - startAngle) / (float) resolution;
     // System.out.println("inc = " + inc);
      for (int i = 0; i <= resolution; i++)
      {
   //          System.out.println("startAngle + i * inc = " + (startAngle + i * inc));

        pts.add(getPoint(startAngle + i * inc));
      }
    }
    else if (dir == CLOCKWISE)
    {
      if (startAngle < endAngle)
      {
        startAngle += TWOPI;
      }

//      System.out.println("CW NOW endAngle = " + endAngle);
 //     System.out.println("CW NOW startAngle = " + startAngle);

      float inc = (endAngle - startAngle) / (float) resolution;

      for (int i = 0; i <= resolution; i++)
      {
        pts.add(getPoint(startAngle + i * inc));
      }
    }

    return pts;
  }

  public static Circle makeCircleFromSegment(Segment seg)
  {
    return new Circle(seg.getMidpoint(), seg.getLength() * .5f);
  }

  


}
