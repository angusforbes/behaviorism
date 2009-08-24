/* GeomUtils.java */
package behaviorism.utils;

import behaviorism.geometry.Geom;
import behaviorism.geometry.GeomPoint;
import behaviorism.geometry.GeomPoly;
import behaviorism.geometry.GeomRect;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * This class contains static utility methods having to do with shapes and geometry.
 * In particular, in contains both specific methods to facilitate working with the
 * java2D.Shape interface (especially Path2D), as well as more general methods to find
 * the area and center of mass of a shape, or distances between points, etc.
 */
public class GeomUtils
{

  /*
  public static List<Line2D> getLinesFromPath2D(Path2D path)
  {
  PathIterator pi = path.getPathIterator(null);
  
  float[] vals = new float[6];
  
  List<Line2D> lines = new ArrayList<Line2D>();
  
  Point2D.Float prevPoint = null;
  Point2D.Float curPoint = null;
  
  while (!pi.isDone())
  {
  int type = pi.currentSegment(vals);
  
  if (type == PathIterator.SEG_CLOSE)
  {
  break;
  }
  else
  {
  if (type == PathIterator.SEG_MOVETO)
  {
  curPoint = new Point2D.Float(vals[0], vals[1]);
  }
  else
  {
  if (type == PathIterator.SEG_LINETO)
  {
  if (curPoint == null)
  {
  //System.out.println("in getLinesFromBoundingPath() : ERROR: what's up with this path???");
  }
  prevPoint = curPoint;
  curPoint = new Point2D.Float(vals[0], vals[1]);
  
  Line2D.Float line = new Line2D.Float(prevPoint, curPoint);
  lines.add(line);
  }				//System.out.println("type = " + type + ", vals = " + Arrays.toString(vals));
  }
  }
  pi.next();
  }
  
  
  for (Line2D line : lines)
  {
  System.out.printf("line : %f,%f/%f,%f\n", line.getX1(), line.getY1(), line.getX2(), line.getY2());
  }
  return lines;
  }
   */
  public static List<Line2D> getLinesFromPath2D(Path2D path)
  {
    List<Line2D> lines = new ArrayList<Line2D>();

    PathIterator pi = path.getPathIterator(null);

    double[] vals = new double[6];

    double curx = -1f;
    double cury = -1f;
    double startx = -1f;
    double starty = -1f;

    while (!pi.isDone())
    {
      int type = pi.currentSegment(vals);

      if (type == PathIterator.SEG_CLOSE)
      {
        lines.add(new Line2D.Double(curx, cury, startx, starty));
        break;
      }
      else if (type == PathIterator.SEG_MOVETO)
      {

        startx = vals[0];
        starty = vals[1];
        curx = vals[0];
        cury = vals[1];
      }
      else if (type == PathIterator.SEG_LINETO)
      {
        lines.add(new Line2D.Double(curx, cury, vals[0], vals[1]));
        curx = vals[0];
        cury = vals[1];
      }
      //handle curves later...

      pi.next();
    }
//    for (Line2D line : lines)
//    {
//      System.out.printf("line : %f,%f/%f,%f\n", line.getX1(), line.getY1(), line.getX2(), line.getY2());
//    }

    return lines;
  }

  public static List<Point2D> getPointsFromPath2D(Path2D path)
  {
    PathIterator pi = path.getPathIterator(null, .001);

    float[] vals = new float[6];

    List<Point2D> points = new ArrayList<Point2D>();

    while (!pi.isDone())
    {
      int type = pi.currentSegment(vals);

      if (type == PathIterator.SEG_CLOSE)
      {
        break;
      }
      else
      {
        if (type == PathIterator.SEG_MOVETO)
        {
          //System.out.println("MOVETO");
          points.add(new Point2D.Double(vals[0], vals[1]));
        }
        else
        {
          if (type == PathIterator.SEG_LINETO)
          {
            //System.out.println("LINETO");
            points.add(new Point2D.Double(vals[0], vals[1]));
          }
          else
          {
            if (type == PathIterator.SEG_QUADTO)
            {
              //System.out.println("QUADTO");
              points.add(new Point2D.Double(vals[0], vals[1]));
            }					//System.out.println("type = " + type + ", vals = " + Arrays.toString(vals));
          }
        }
      }
      pi.next();
    }


    for (Point2D pt : points)
    {
      //System.out.printf("point : (%f,%f)\n", pt.x, pt.y);
    }
    return points;
  }

  //Pick's algo...
  //public static float area(Path2D.Float p2d)
  public static float area(Shape shape)
  {
    Path2D.Float p2d;

    if (shape instanceof Path2D.Float)
    {
      p2d = (Path2D.Float) shape;
    }
    else
    {
      p2d = new Path2D.Float(shape);
    }

    List<Point2D> points = getPointsFromPath2D(p2d);

    int i, j, n = points.size();
    float area = 0f;

    for (i = 0; i < n; i++)
    {
      j = (i + 1) % n;
      area += points.get(i).getX() * points.get(j).getY();
      area -= points.get(j).getX() * points.get(i).getY();
    }
    area /= 2f;
    return (Math.abs(area));
  }

  public static double area(Point2D[] polyPoints)
  {
    int i, j, n = polyPoints.length;
    double area = 0;

    for (i = 0; i < n; i++)
    {
      j = (i + 1) % n;
      area += polyPoints[i].getX() * polyPoints[j].getY();
      area -= polyPoints[j].getX() * polyPoints[i].getY();
    }
    area /= 2.0;
    return (area);
  }

  /**
   * Function to calculate the center of mass for a given polygon, according
   * ot the algorithm defined at
   * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
   *
   * @param polyPoints
   *            array of points in the polygon
   * @return point that is the center of mass
   */
  public static Point2D centerOfMass(Point2D[] polyPoints)
  {
    Point2D p3f = polyPoints[0];

    double minx = p3f.getX();
    double maxx = p3f.getX();
    double miny = p3f.getY();
    double maxy = p3f.getY();

    for (int i = 1; i < polyPoints.length; i++)
    {
      p3f = polyPoints[i];
      if (p3f.getX() < minx)
      {
        minx = p3f.getX();
      }
      if (p3f.getX() > maxx)
      {
        maxx = p3f.getX();
      }
      if (p3f.getY() < miny)
      {
        miny = p3f.getY();
      }
      if (p3f.getY() > maxy)
      {
        maxy = p3f.getY();
      }
    }

    if (minx < 0 || miny < 0)
    {
      for (int i = 0; i < polyPoints.length; i++)
      {
        double newx, newy;
        if (minx < 0)
        {
          newx = polyPoints[i].getX() + Math.abs(minx);
        }
        else
        {
          newx = polyPoints[i].getX();
        }
        if (miny < 0)
        {
          newy = polyPoints[i].getY() + Math.abs(miny);
        }
        else
        {
          newy = polyPoints[i].getY();
        }

        polyPoints[i] = new Point2D.Double(newx, newy);
      }
    }

    double cx = 0, cy = 0;
    double area = area(polyPoints);

    int i, j, n = polyPoints.length;

    double factor = 0;
    for (i = 0; i < n; i++)
    {
      j = (i + 1) % n;
      factor = (polyPoints[i].getX() * polyPoints[j].getY() - polyPoints[j].getX() * polyPoints[i].getY());
      cx += (polyPoints[i].getX() + polyPoints[j].getX()) * factor;
      cy += (polyPoints[i].getY() + polyPoints[j].getY()) * factor;
    }
    area *= 6.0f;
    factor = 1 / area;
    cx *= factor;
    cy *= factor;
    if (minx < 0)
    {
      cx += minx;
    }
    if (miny < 0)
    {
      cy += miny;
    }

    Point2D res = new Point2D.Double(cx, cy);
    //res.setLocation(cx, cy);
    return res;
  }

  public static Point3f centerOfAnchorPoints(List<Geom> geoms)
  {
    float xxx = 0f;
    float yyy = 0f;

    for (int i = 0; i < geoms.size(); i++)
    {
      xxx += geoms.get(i).translate.x;
      yyy += geoms.get(i).translate.y;
    }
    return new Point3f(xxx / geoms.size(), yyy / geoms.size(), 0f);
  }

  public static Point3f centerOfPoints(List<? extends Point3f> polyPoints3f)
  {
    float xxx = 0f;
    float yyy = 0f;
    float zzz = 0f;

    for (int i = 0; i < polyPoints3f.size(); i++)
    {
      xxx += polyPoints3f.get(i).x;
      yyy += polyPoints3f.get(i).y;
      zzz += polyPoints3f.get(i).z;
    }
    return new Point3f(
      xxx / polyPoints3f.size(),
      yyy / polyPoints3f.size(),
      zzz / polyPoints3f.size());
  }

  public static Point3f centerOfMass(GeomPoly poly)
  {
    return centerOfMass(toPoint3f(poly.vertices));
  }

  public static Point3f centerOfMass(List<Point3f> polyPoints3f)
  {
    Point2D[] polyPoints = point3fListToPoint2DArray(polyPoints3f);
    Point2D com = centerOfMass(polyPoints);
    return new Point3f((float) com.getX(), (float) com.getY(), 0f);

    /*
    double cx = 0, cy = 0;
    double area = area(polyPoints);
    //Point2D.Float res = new Point2D.Float();
    int i, j, n = polyPoints.length;

    double factor = 0;
    for (i = 0; i < n; i++)
    {
    j = (i + 1) % n;
    factor = (polyPoints[i].getX() * polyPoints[j].getY() - polyPoints[j].getX() * polyPoints[i].getY());
    cx += (polyPoints[i].getX() + polyPoints[j].getX()) * factor;
    cy += (polyPoints[i].getY() + polyPoints[j].getY()) * factor;
    }
    area *= 6.0f;
    factor = 1f / area;
    cx *= factor;
    cy *= factor;

    //res.setLocation(cx, cy);
    //return new Point3f(res.x, res.y, 0f);
    return new Point3f((float)cx, (float)cy, 0f);
     */
  }

  public static List<Point3f> point2DListToPoint3fList(List<Point2D> p2ds)
  {
    List<Point3f> pts = new ArrayList<Point3f>();

    for (Point2D p2d : p2ds)
    {
      pts.add(new Point3f((float) p2d.getX(), (float) p2d.getY(), 0f));
    }
    return pts;
  }

  public static Point2D.Double[] point3fListToPoint2DArray(List<Point3f> p3fs)
  {
    Point2D.Double[] polyPoints = new Point2D.Double[p3fs.size()];

    for (int i = 0; i < p3fs.size(); i++)
    {
      Point3f p3f = p3fs.get(i);

      polyPoints[i] = new Point2D.Double(p3f.x, p3f.y);
    }

    return polyPoints;
  }

  public static boolean checkLineForPoint(Point3f pt1, Point3f pt2, Point3f pt0)
  {
    //System.out.println("is " + pt0 + " between " + pt1 + " and " + pt2 + "?");

    float minx = (float) Math.min(pt1.x, pt2.x);
    float miny = (float) Math.min(pt1.y, pt2.y);
    float maxx = (float) Math.max(pt1.x, pt2.x);
    float maxy = (float) Math.max(pt1.y, pt2.y);

    if ((pt0.x < minx || pt0.y < miny) || (pt1.x > maxx || pt0.y > maxy))
    {
      //System.out.println("impossible");
      //return false;
    }

    boolean isOnLine = checkLineForPoint(new Line2D.Float(pt1.x, pt1.y, pt2.x, pt2.y), new Point2D.Float(pt0.x, pt0.y));
    //System.out.println("the answer is : " + isOnLine);
    return isOnLine;
  }

  public static boolean checkLineForPoint(Line2D line, Point2D point)
  {
    //need to add a flub val for floating point errors???
    if (line.ptLineDist(point) == 0) //if distance == 0, then point is on the line!
    {
      return true;
    }
    if (line.getX1() == point.getX() && line.getY1() == point.getY())
    {
      return true;
    }
    if (line.getX2() == point.getX() && line.getY2() == point.getY())
    {
      return true;
    }

    return false;
  }

  public static boolean getIntersectionBetweenLines(Point3f line1_p1, Point3f line1_p2,
    Point3f line2_p1, Point3f line2_p2,
    Point3f intersectionPt)
  {
    Point2D testP2D = getIntersectionBetweenLines(new Line2D.Float(line1_p1.x, line1_p1.y, line1_p2.x, line1_p2.y),
      new Line2D.Float(line2_p1.x, line2_p1.y, line2_p2.x, line2_p2.y));

    if (testP2D == null)
    {
      return false;
    }
    else
    {
      intersectionPt.x = (float) testP2D.getX();
      intersectionPt.y = (float) testP2D.getY();
      intersectionPt.z = (float) 0f;
      return true;
    }
  }

  public static Point2D getIntersectionBetweenLineAndPolygon(
    Line2D line, Path2D poly)
  {
    List<Line2D> lines = getLinesFromPath2D(poly);

    for (Line2D polyline : lines)
    {
      Point2D p2d = getIntersectionBetweenLines(polyline, line);
      if (p2d != null)
      {
        //System.out.println("intersection point = " + p2d);
        return p2d;
      }
    }

    //no intersection...
    //System.out.println("no intersection!");
    return null;
  }

  public static Point2D getIntersectionBetweenLineAndRectangle(
    Line2D line, Rectangle2D rect)
  {
    if (!rect.intersectsLine(line))
    {
      return null;
    }

    Point2D ip = null;

    //test bottom
    ip = getIntersectionBetweenLines(line,
      new Line2D.Double(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY()));

    if (ip != null)
    {
      return ip;
    }

    //test right
    ip = getIntersectionBetweenLines(line,
      new Line2D.Double(
      rect.getX() + rect.getWidth(), rect.getY(),
      rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()));

    if (ip != null)
    {
      return ip;
    }

    //test top
    ip = getIntersectionBetweenLines(line,
      new Line2D.Double(
      rect.getX(), rect.getY() + rect.getHeight(),
      rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight()));

    if (ip != null)
    {
      return ip;
    }

    //test left
    ip = getIntersectionBetweenLines(line,
      new Line2D.Double(
      rect.getX(), rect.getY(),
      rect.getX(), rect.getY() + rect.getHeight()));

    if (ip != null)
    {
      return ip;
    }

    //error!!!
    System.out.println("ERROR : GeomUtils.getIntersectionBetweenLineAndRectangle() : how is this null?");
    return null;
  }

  public static Point2D getIntersectionBetweenLines(Line2D line1, Line2D line2) //throws MultipleIntersectionException
  {
    double dyline1, dxline1;
    double dyline2, dxline2, e, f;
    double x1line1, y1line1, x2line1, y2line1;
    double x1line2, y1line2, x2line2, y2line2;

    if (!line1.intersectsLine(line2))
    {
      return null;

      /* first, check to see if the segments intersect by parameterization
      on s and t; if s and t are both between [0,1], then the
      segments intersect */
    }
    x1line1 = line1.getX1();
    y1line1 = line1.getY1();
    x2line1 = line1.getX2();
    y2line1 = line1.getY2();

    x1line2 = line2.getX1();
    y1line2 = line2.getY1();
    x2line2 = line2.getX2();
    y2line2 = line2.getY2();

    /* check to see if the segments have any endpoints in common. If they do,
    then return the endpoints as the intersection point */
    if ((x1line1 == x1line2) && (y1line1 == y1line2))
    {
      return (new Point2D.Float((float) x1line1, (float) y1line1));
    }
    if ((x1line1 == x2line2) && (y1line1 == y2line2))
    {
      return (new Point2D.Float((float) x1line1, (float) y1line1));
    }
    if ((x2line1 == x1line2) && (y2line1 == y1line2))
    {
      return (new Point2D.Float((float) x2line1, (float) y2line1));
    }
    if ((x2line1 == x2line2) && (y2line1 == y2line2))
    {
      return (new Point2D.Float((float) x2line1, (float) y2line1));
    }

    dyline1 = -(y2line1 - y1line1);
    dxline1 = x2line1 - x1line1;

    dyline2 = -(y2line2 - y1line2);
    dxline2 = x2line2 - x1line2;

    e = -(dyline1 * x1line1) - (dxline1 * y1line1);
    f = -(dyline2 * x1line2) - (dxline2 * y1line2);

    /* compute the intersection point using
    ax+by+e = 0 and cx+dy+f = 0
    
    If there is more than 1 intersection point between two lines,
    throw an exception */


    if ((dyline1 * dxline2 - dyline2 * dxline1) == 0)
    {
      //System.out.println("RETURNING NULL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      return null; //ie the lines overlap in more than one place!!
      //    throw new MultipleIntersectionException();
    }

    return (new Point2D.Float(
      (float) (-(e * dxline2 - dxline1 * f) / (dyline1 * dxline2 - dyline2 * dxline1)),
      (float) (-(dyline1 * f - dyline2 * e) / (dyline1 * dxline2 - dyline2 * dxline1))));
  }

  /** 
   * example usage for getting distance between a point in 1D: 
   * GeomUtils.euclidianDistance(x1, x2);
   * 
   * example usage for getting distance between a point in 2D: 
   * GeomUtils.euclidianDistance(x1, y1, x2, y2);
   * 
   * example usage for getting distance between a point in 3D: 
   * GeomUtils.euclidianDistance(x1, y1, z1, x2, y2, z1);
   * 
   * @param coords
   * @return the distance between the points, in double precision
   */
  public static double euclidianDistance(double... coords)
  {
    if (coords.length % 2 != 0) //ie, if not even
    {
      System.err.println("in euclidianDistance() : error, need an even number of dimensions!");
      return 0f;
    }

    int dims = coords.length / 2;

    double dp;
    double val = 0.0;

    for (int i = 0; i < dims; i++)
    {
      dp = coords[i + dims] - coords[i];
      val += dp * dp;
    }
    return (Math.sqrt(val));
  }

  /*
  public static float euclidianDistance(float ... coords)
  {
  if (coords.length % 2 != 0) //ie, if not even
  {
  System.err.println("in euclidianDistance() : error, need an even number of dimensions!");
  return 0f;
  }
  
  int dims = coords.length / 2;
  
  float dp;
  float val = 0f;
  
  for (int i = 0; i < dims; i++)
  {
  dp = coords[i + dims] - coords[i];
  val += dp * dp;
  }
  return (float)(Math.sqrt(val));
  }
   */
  public static float getDistanceBetweenPoints(Point3f p1, Point3f p2)
  {
    return p1.distance(p2);
  }

  public static float getDistanceBetweenPoints(GeomPoint gp1, GeomPoint gp2)
  {
    return getDistanceBetweenPoints(toPoint3f(gp1), toPoint3f(gp2));
  }
  //translate??

  public static Point2D toPoint2D(Point3f p3f)
  {
    return new Point2D.Double(p3f.x, p3f.y);
  }

  public static List<Point3f> toPoint3f(List<GeomPoint> gps)
  {
    List<Point3f> p3fs = new ArrayList<Point3f>();
    for (GeomPoint gp : gps)
    {
      p3fs.add(toPoint3f(gp));
    }
    return p3fs;
  }

  public static Point3f toPoint3f(GeomPoint gp)
  {
    return new Point3f(gp.translate.x, gp.translate.y, gp.translate.z);
  }

  public static Point3f toPoint3f(Point2D p2d)
  {
    return new Point3f((float) p2d.getX(), (float) p2d.getY(), 0f);
  }

  /* returns the angle in radians */
  public static float getAngleBetweenPoints(Point3f p1, Point3f p2) //just x and y
  {
    return getAngleBetweenPoints(p1, p2, false);
  }

  /* returns the angle in radians */
  public static float getAngleBetweenPoints(GeomPoint gp1, GeomPoint gp2) //just x and y
  {
    return getAngleBetweenPoints(toPoint3f(gp1), toPoint3f(gp2), false);
  }


  /* 
  Returns the angle in radians. 
  If overlap is true, then add 360 degrees to the angle
   */
  public static float getAngleBetweenPoints(Point3f p1, Point3f p2, boolean overlap) //just x and y
  {
    //** also i am switching these around-- i think they were wrong.. check to see if it breaks someting! */
    //just 2d angle, not solid angle
    //float ny = p1.y - p2.y;
    //float nx = p1.x - p2.x; 
    float ny = p2.y - p1.y;
    float nx = p2.x - p1.x;

    double ang = Math.atan2(ny, nx);

    //I don't know why this was here!!!! I am commenting it out, hope that doesn't break anything!
    if (overlap == true && ang > 0)
    {
      ang -= Math.toRadians(360);
    }

    return (float) ang;
  }

  public static void sortPointsByAngle(final Point3f centerPt, List<Point3f> list, final int direction)
  {
    Collections.sort(list, new Comparator()
    {

      public int compare(Object a, Object b)
      {
        Point3f p1 = (Point3f) a;
        Point3f p2 = (Point3f) b;

        float r1 = getAngleBetweenPoints(centerPt, p1);
        float r2 = getAngleBetweenPoints(centerPt, p2);

        if (r1 > r2)
        {
          if (direction == 1)
          {
            return -1;
          }
          else
          {
            return 1;
          }
        }
        else
        {
          if (r1 < r2)
          {
            if (direction == 1)
            {
              return 1;
            }
            else
            {
              return -1;
            }
          }
          else
          {
            return 0;
          }
        }
      }
    });
  }

  public static void sortPointsByAngle(final GeomPoint centerPt, List<GeomPoint> list, final int direction)
  {
    Collections.sort(list, new Comparator<GeomPoint>()
    {

      public int compare(GeomPoint p1, GeomPoint p2)
      {
        //GeomPoint p1 = (GeomPoint) a;
        //GeomPoint p2 = (GeomPoint) b;

        float r1 = getAngleBetweenPoints(centerPt.translate, p1.translate);
        float r2 = getAngleBetweenPoints(centerPt.translate, p2.translate);

        if (r1 > r2)
        {
          if (direction == 1)
          {
            return -1;
          }
          else
          {
            return 1;
          }
        }
        else
        {
          if (r1 < r2)
          {
            if (direction == 1)
            {
              return 1;
            }
            else
            {
              return -1;
            }
          }
          else
          {
            return 0;
          }
        }
      }
    });
  }

  public static float getMinAngleBetweenPointAndGeom(Point3f centerPt, GeomRect rect)
  {
    return getMinAngleBetweenPointAndGeom(centerPt, rect, false);
  }

  public static float getMinAngleBetweenPointAndGeom(Point3f centerPt, GeomRect rect, boolean overlap)
  {
    Point3f a = new Point3f(rect.translate);
    Point3f b = new Point3f(rect.translate.x + rect.w, rect.translate.y, rect.translate.z);
    Point3f c = new Point3f(rect.translate.x + rect.w, rect.translate.y + rect.h, rect.translate.z);
    Point3f d = new Point3f(rect.translate.x, rect.translate.y + rect.h, rect.translate.z);

    float minAngle = getAngleBetweenPoints(centerPt, a, overlap);

    float temp = getAngleBetweenPoints(centerPt, b, overlap);

    if (temp < minAngle)
    {
      minAngle = temp;
    }

    temp = getAngleBetweenPoints(centerPt, c, overlap);
    if (temp < minAngle)
    {
      minAngle = temp;
    }

    temp = getAngleBetweenPoints(centerPt, d, overlap);
    if (temp < minAngle)
    {
      minAngle = temp;
    }

    return minAngle;

  }

  public static float getMaxAngleBetweenPointAndGeom(Point3f centerPt, GeomRect rect)
  {
    return getMaxAngleBetweenPointAndGeom(centerPt, rect, false);
  }

  public static float getMaxAngleBetweenPointAndGeom(Point3f centerPt, GeomRect rect, boolean overlap)
  {
    Point3f a = new Point3f(rect.translate);
    Point3f b = new Point3f(rect.translate.x + rect.w, rect.translate.y, rect.translate.z);
    Point3f c = new Point3f(rect.translate.x + rect.w, rect.translate.y + rect.h, rect.translate.z);
    Point3f d = new Point3f(rect.translate.x, rect.translate.y + rect.h, rect.translate.z);

    float maxAngle = getAngleBetweenPoints(centerPt, a, overlap);

    float temp = getAngleBetweenPoints(centerPt, b, overlap);

    if (temp > maxAngle)
    {
      maxAngle = temp;
    }

    temp = getAngleBetweenPoints(centerPt, c, overlap);
    if (temp > maxAngle)
    {
      maxAngle = temp;
    }

    temp = getAngleBetweenPoints(centerPt, d, overlap);
    if (temp > maxAngle)
    {
      maxAngle = temp;
    }

    return maxAngle;

  }

  /** Sorts the Geoms by the smallest angle (ie check all 4 points) -- assuming a GeomRect for now */
  public static float[][] sortGeomsByAngleAndDetermineMinAndMaxAngles(final Point3f centerPt, List<GeomRect> rects, final int direction)
  {
    float[][] minmax = new float[rects.size()][2];

    for (int i = 0; i < rects.size(); i++)
    {
      GeomRect ggg = rects.get(i);
      minmax[i][0] = (float) Math.toDegrees(GeomUtils.getMinAngleBetweenPointAndGeom(centerPt, ggg));
      minmax[i][1] = (float) Math.toDegrees(GeomUtils.getMaxAngleBetweenPointAndGeom(centerPt, ggg));

      if (minmax[i][1] - minmax[i][0] > 180f)
      {
        System.out.println("difference is too great!");
        //problem-- lets use the overlap version
        minmax[i][0] = (float) Math.toDegrees(GeomUtils.getMinAngleBetweenPointAndGeom(centerPt, ggg, true));
        minmax[i][1] = (float) Math.toDegrees(GeomUtils.getMaxAngleBetweenPointAndGeom(centerPt, ggg, true));


        if (minmax[i][0] < 0f)
        {
          minmax[i][0] += 360f;
          minmax[i][1] += 360f;
        }
        else
        {
          if (minmax[i][1] < 0f)
          {
            minmax[i][0] += 360f;
            minmax[i][1] += 360f;
          }
        }
      }


    }



    Arrays.sort(minmax, new Comparator<float[]>()
    {

      //public int compare(Object a, Object b)
      public int compare(float[] a1, float[] a2)
      {
        //float[] a1 = (float[]) a;
        //float[] a2 = (float[]) b;


        if (a1[0] > a2[0])
        {
          if (direction == 1)
          {
            return -1;
          }
          else
          {
            return 1;
          }
        }
        else
        {
          if (a1[0] < a1[0])
          {
            if (direction == 1)
            {
              return 1;
            }
            else
            {
              return -1;
            }
          }
          else
          {
            return 0;
          }
        }
      }
    });


    for (int i = 0; i < minmax.length; i++)
    {
      //System.out.println("min of rect " + i + " is " + minmax[i][0]);
      //System.out.println("max of rect " + i + " is " + minmax[i][1]);


      /*
      if (minmax[i][0] < 0f)
      {
      minmax[i][0] += 360f;
      minmax[i][1] += 360f;
      }
      else if (minmax[i][1] < 0f)
      {
      minmax[i][0] += 360f;
      minmax[i][1] += 360f;
      }
       */

      System.out.println("done sorting: min of rect " + i + " is " + minmax[i][0]);
      System.out.println("done sorting: max of rect " + i + " is " + minmax[i][1]);

    }
    return minmax;
  }

  public static float dot(Vector3f v1, Vector3f v2)
  {
    return v1.dot(v2);
  }

  public static float getDistanceBetweenPointAndLineSegment(
    Point3f pt, Point3f s1_p0, Point3f s1_p1)
  {
    Vector3f v = new Vector3f(subtractPoint3f(s1_p1, s1_p0));
    Vector3f w = new Vector3f(subtractPoint3f(pt, s1_p0));

    float c1 = dot(w, v);

    if (c1 <= 0f)
    {
      Vector3f z = new Vector3f();
      z.sub(pt, s1_p0);
      return z.length();
    }

    float c2 = dot(v, v);

    if (c2 <= c1)
    {
      Vector3f z = new Vector3f();
      z.sub(pt, s1_p1);
      return z.length();
    }

    float b = c1 / c2;

    v.scale(b);
    s1_p0.add(v);

    Vector3f z = new Vector3f();
    z.sub(pt, s1_p0);
    return z.length();
  }

  public static float getDistanceBetweenLineSegments(
    Point3f s1_p0, Point3f s1_p1,
    Point3f s2_p0, Point3f s2_p1)
  {
    Vector3f u = new Vector3f(subtractPoint3f(s1_p1, s1_p0));
    Vector3f v = new Vector3f(subtractPoint3f(s2_p1, s2_p0));
    Vector3f w = new Vector3f(subtractPoint3f(s1_p0, s2_p0));

    float a = dot(u, u);
    float b = dot(u, v);
    float c = dot(v, v);        // always >= 0
    float d = dot(u, w);
    float e = dot(v, w);
    float D = a * c - b * b;       // always >= 0
    float sc, sN, sD = D;      // sc = sN / sD, default sD = D >= 0
    float tc, tN, tD = D;      // tc = tN / tD, default tD = D >= 0

    float SMALL_NUM = .00001f;

    // compute the line parameters of the two closest points
    if (D < SMALL_NUM)
    { // the lines are almost parallel
      sN = 0.0f;        // force using point P0 on segment S1
      sD = 1.0f;        // to prevent possible division by 0.0 later
      tN = e;
      tD = c;
    }
    else
    {                // get the closest points on the infinite lines
      sN = (b * e - c * d);
      tN = (a * e - b * d);
      if (sN < 0.0f)
      {       // sc < 0 => the s=0 edge is visible
        sN = 0.0f;
        tN = e;
        tD = c;
      }
      else if (sN > sD)
      {  // sc > 1 => the s=1 edge is visible
        sN = sD;
        tN = e + b;
        tD = c;
      }
    }

    if (tN < 0f)
    {           // tc < 0 => the t=0 edge is visible
      tN = 0f;
      // recompute sc for this edge
      if (-d < 0f)
      {
        sN = 0f;
      }
      else if (-d > a)
      {
        sN = sD;
      }
      else
      {
        sN = -d;
        sD = a;
      }
    }
    else if (tN > tD)
    {      // tc > 1 => the t=1 edge is visible
      tN = tD;
      // recompute sc for this edge
      if ((-d + b) < 0f)
      {
        sN = 0;
      }
      else if ((-d + b) > a)
      {
        sN = sD;
      }
      else
      {
        sN = (-d + b);
        sD = a;
      }
    }
    // finally do the division to get sc and tc
    sc = (Math.abs(sN) < SMALL_NUM ? 0f : sN / sD);
    tc = (Math.abs(tN) < SMALL_NUM ? 0f : tN / tD);

    // get the difference of the two closest points
    //Vector3f dP = w + (sc * u) - (tc * v);  // = S1(sc) - S2(tc)
    //return norm(dP);   // return the closest distance

    u.scale(sc);
    w.add(u);
    v.scale(tc);
    w.sub(v);

    return w.length();
    //return (float)Math.sqrt(dot(w,w));

  }

  /*
  if (Line2D.linesIntersect(
  s1_p1.x, s1_p1.y, s1_p2.x, s1_p2.y,
  s2_p1.x, s2_p1.y, s2_p2.x, s2_p2.y))
  {
  return 0f;
  }
   */
  public static float linePointDist(Point3f endPt_1, Point3f endPt_2, Point3f pt)
  {
    return (float) Line2D.ptLineDist(endPt_1.x, endPt_1.y, endPt_2.x, endPt_2.y, pt.x, pt.y);
  }

  public static void sortGeomsByDistanceToLine(
    List<? extends Geom> points, final Point3f lineA, final Point3f lineB, final int direction)
  {
    Collections.sort(points, new Comparator<Geom>()
    {

      @Override
      public int compare(Geom a, Geom b)
      {
        float dA = linePointDist(lineA, lineB, a.translate);
        float dB = linePointDist(lineA, lineB, b.translate);
        if (dA > dB)
        {
          return 1;
        }
        else if (dA < dB)
        {
          return -1;
        }
        return 0;
      }
    });
  }

  public static void sortPointsByDistanceToLine(
    final Point3f lineA, final Point3f lineB, List<? extends Point3f> points, final int direction)
  {
    Collections.sort(points, new Comparator<Point3f>()
    {

      @Override
      public int compare(Point3f a, Point3f b)
      {
        float dA = linePointDist(lineA, lineB, a);
        float dB = linePointDist(lineA, lineB, b);
        if (dA > dB)
        {
          return 1;
        }
        else if (dA < dB)
        {
          return -1;
        }
        return 0;

      }
    });
  }

  /**
   * Sorts a list of points by closest distance to a specified center point. 
   * @param centerPt
   * @param list
   */
  public static void sortPointsByDistance(final Point3f centerPt, List<? extends Point3f> list)
  {
    sortPointsByDistance(centerPt, list, 1);
  }

  /**
   * Sorts a list of points by closest or furthest distance to a specified center point. 
   * @param centerPt
   * @param list
   * @param direction +1 = order asc, -1 = order desc (ie, +1 for closest or -1 for futhest)
   */
  public static void sortPointsByDistance(final Point3f centerPt, List<? extends Point3f> list, final int direction)
  {
    Collections.sort(list, new Comparator()
    {

      @Override
      public int compare(Object a, Object b)
      {
        Point3f g1 = (Point3f) a;
        Point3f g2 = (Point3f) b;

        float r1 = centerPt.distance(g1);
        float r2 = centerPt.distance(g2);

        if (r1 > r2)
        {
          if (direction == -1)
          {
            return -1;
          }
          else
          {
            return 1;
          }
        }
        else
        {
          if (r1 < r2)
          {
            if (direction == -1)
            {
              return 1;
            }
            else
            {
              return -1;
            }
          }
          else
          {
            return 0;
          }
        }
      }
    });
  }

  /** Sorts the Geoms by the smallest angle (ie check all 4 points) -- assuming a GeomRect for now */
  public static void sortGeomsByAngle(final Point3f centerPt, List<GeomRect> list, final int direction)
  {
    Collections.sort(list, new Comparator()
    {

      public int compare(Object a, Object b)
      {
        GeomRect g1 = (GeomRect) a;
        GeomRect g2 = (GeomRect) b;

        float r1 = getMinAngleBetweenPointAndGeom(centerPt, g1);
        float r2 = getMinAngleBetweenPointAndGeom(centerPt, g2);

        if (r1 > r2)
        {
          if (direction == 1)
          {
            return -1;
          }
          else
          {
            return 1;
          }
        }
        else
        {
          if (r1 < r2)
          {
            if (direction == 1)
            {
              return 1;
            }
            else
            {
              return -1;
            }
          }
          else
          {
            return 0;
          }
        }
      }
    });
  }

  public static Point2D getPointAlongLineSegment(Line2D line, double perc)
  {
    double x = line.getX1() + ((line.getX2() - line.getX1()) * perc);
    double y = line.getY1() + ((line.getY2() - line.getY1()) * perc);

    return new Point2D.Double(x, y);
  }

  /**
   * Compute the midpoint between two points.
   * @param p1 A point.
   * @param p2 Another point.
   * @return The midpoint of p1 and p2.
   */
  public static Point3f getMidpointOfTwoPoints(Point3f p1, Point3f p2)
  {
    float nx = (p1.x + p2.x) / 2f;
    float ny = (p1.y + p2.y) / 2f;
    float nz = (p1.z + p2.z) / 2f;
    return new Point3f(nx, ny, nz);
  }

  /** pass in a line segment, return a second line segment of length 1000f, which is perpendicular to the first
   * and which passes through the end point of the first line */
  public static void getPerpindicularLine(Point3f p1_a, Point3f p1_b,
    Point3f p2_a, Point3f p2_b)
  {
    float m = getInverseSlope(p1_a, p1_b);
    float b = getYIntercept(m, p1_b);

    float num = 1000f;
    //p2_a.x = p1_b.x;
    //p2_a.y = p1_b.y;
    p2_a.x = -num;
    p2_a.y = m * -num + b;
    p2_b.x = num;
    p2_b.y = m * num + b;
  }

  public static List<Point3f> getDiskPerpindicularToLineSegment(float radius, Point3f p1, Point3f p2)
  {
    List<Point3f> points = new ArrayList<Point3f>();

    Point3f r = new Point3f();
    Point3f s = new Point3f();
    getPlanePerpindicularToLineSegment(p1, p2, r, s);

    Vector3f n = new Vector3f();
    double dtheta = (Math.PI * 2.0) / 36;
    double theta = 0;
    for (int i = 0; i <= 36; i++)
    {
      theta = dtheta * i;
      n.x = (float) (r.x * Math.cos(theta) + s.x * Math.sin(theta));
      n.y = (float) (r.y * Math.cos(theta) + s.y * Math.sin(theta));
      n.z = (float) (r.z * Math.cos(theta) + s.z * Math.sin(theta));
      n.normalize();

      points.add(
        new Point3f(
        (float) (p1.x + (radius * r.x * Math.cos(theta)) + s.x * (radius * Math.sin(theta))),
        (float) (p1.y + (radius * r.y * Math.cos(theta)) + s.y * (radius * Math.sin(theta))),
        (float) (p1.z + (radius * r.z * Math.cos(theta)) + s.z * (radius * Math.sin(theta))) //(float) (p1.y + r.y * Math.cos(theta) + s.y * Math.sin(theta)),
        //(float) (p1.z + r.z * Math.cos(theta) + s.z * Math.sin(theta))
        ));
    }

    return points;
  }

  /**
   *
   * @param p1 the end point we are placng the perpindicular plane at
   * @param p2 the other end point
   * @param r r - p1 = one vector of the perpindicular plane
   * @param s s - p1 = the other vector of the perpindicular plane
   */
  public static void getPlanePerpindicularToLineSegment(Point3f p1, Point3f p2, Point3f r, Point3f s)
  {
    //Point3f n;
    Vector3f p1p2 = new Vector3f();
    Vector3f rv = new Vector3f();
    Vector3f sv = new Vector3f();
    Vector3f randomp = new Vector3f();
    p1p2.sub(p1, p2);

    //XYZ n,p,r,s,p1p2;
    //double theta,dtheta;

    randomp.x = Utils.randomFloat(); /* Create a random vector */
    randomp.y = Utils.randomFloat();
    randomp.z = Utils.randomFloat();

    rv.cross(p1p2, randomp);
    sv.cross(p1p2, rv);
    rv.normalize();
    sv.normalize();

    r.set(rv);
    s.set(sv);


  }

  /** pass in a line segment, return a second line segment of a specified length, which is perpendicular to the first
   * and which passes through the end point of the first line */
  public static void getPerpindicularLine(float length, Point3f p1_a, Point3f p1_b,
    Point3f p2_a, Point3f p2_b)
  {
    float m = getInverseSlope(p1_a, p1_b);

    double theta;
    if (Float.isNaN(m))
    {
      System.out.println("het!");
      theta = Math.toRadians(90f);
    }
    else
    {
      theta = Math.atan(m);
    }

    System.out.println("m = " + m + ", theta = " + theta);
    p2_a.x = p1_b.x - ((float) Math.cos(theta) * length);
    p2_a.y = p1_b.y - ((float) Math.sin(theta) * length);
    p2_b.x = p1_b.x + ((float) Math.cos(theta) * length);
    p2_b.y = p1_b.y + ((float) Math.sin(theta) * length);
  }

  /**
   * Given a Point (p3f) and an angle and a distance, compute the next point and return it.
   * @param p3f The initial point.
   * @param ang The direction of the next point.
   * @param dist The distance of the next point from the initial point.
   * @return The next point.
   */
  public static Point3f getNextPointUsingAngleAndDistance(final Point3f p3f, final float ang, final float dist)
  {
    Point3f nextPt = new Point3f();

    nextPt.x = p3f.x + ((float) Math.cos(Math.toRadians(ang)) * dist);
    nextPt.y = p3f.y + ((float) Math.sin(Math.toRadians(ang)) * dist);

    return nextPt;
  }

  /**
   * Given a Point (p3f) and an angle and a distance, compute the next point and return
   * a Point3f representing the difference between the next point and the initialPoint.
   * @param p3f The initial point.
   * @param ang The direction of the next point.
   * @param dist The distance of the next point from the initial point.
   * @return A point representing the vector defined by the next point minus the initial point.
   */
  public static Point3f getNextIncrementUsingAngleAndDistance(Point3f p3f, float ang, float dist)
  {
    Point3f incPt = new Point3f();

    incPt.x = ((float) Math.cos(Math.toRadians(ang)) * dist);
    incPt.y = ((float) Math.sin(Math.toRadians(ang)) * dist);

    return incPt;
  }

  public static Point3f turnDegrees(float degrees, float length, Point3f p1_a, Point3f p1_b)
  {
    Point3f p2 = new Point3f();
    float dx = p1_b.x - p1_a.x;
    float dy = p1_b.y - p1_a.y;
    double theta = Math.atan2(dy, dx) + Math.toRadians(degrees);
    p2.x = p1_b.x + ((float) Math.cos(theta) * length);
    p2.y = p1_b.y + ((float) Math.sin(theta) * length);

    return p2;
  }

  //should be called get parallel line?
  /* pass in an empty p2_a and p2_b and it is the shifted line distance "length" away from the p1 line */
  public static void getShiftedLine(
    float length,
    Point3f p1_a, Point3f p1_b,
    Point3f p2_a, Point3f p2_b)
  {
    float dx = p1_b.x - p1_a.x;
    float dy = p1_b.y - p1_a.y;
    double theta = Math.atan2(-dx, dy);

    p2_a.x = p1_a.x + ((float) Math.cos(theta) * length);
    p2_a.y = p1_a.y + ((float) Math.sin(theta) * length);
    p2_b.x = p1_b.x + ((float) Math.cos(theta) * length);
    p2_b.y = p1_b.y + ((float) Math.sin(theta) * length);
  }

  /** Sets a point (p2) on the line (defined by p1_a and p1_b) that is a specified length beyond the
   * endpoint p1_b. */
  public static void getExtendedPoint(float length, Point3f p1_a, Point3f p1_b,
    Point3f p2)
  {
    float dx = p1_b.x - p1_a.x;
    float dy = p1_b.y - p1_a.y;
    double theta = Math.atan2(dy, dx);
    p2.x = p1_b.x + ((float) Math.cos(theta) * length);
    p2.y = p1_b.y + ((float) Math.sin(theta) * length);
  }

  public static void getExtendedLine(Point3f p1_a, Point3f p1_b,
    Point3f p2_a, Point3f p2_b)
  {
    getExtendedLine(1000f, p1_a, p1_b, p2_a, p2_b);

  }

  public static void getExtendedLine(float extendDist, Point3f p1_a, Point3f p1_b,
    Point3f p2_a, Point3f p2_b)
  {
    float x_inc = 0f;
    float y_inc = 0f;

    float m = getSlope(p1_a, p1_b);

    if (Float.isNaN(m))
    {
      //then this line is parallel to the y-axis
      x_inc = 0;
      y_inc = extendDist;
    }
    else
    {
      if (m == 0f)
      {
        //then this line is parallel to the x-axis
        y_inc = 0;
        x_inc = extendDist;
      }
      else
      {
        float b = getYIntercept(m, p1_b);

        //System.out.println("m = " + m);
        //System.out.println("b = " + b);
        //System.out.println("dist = " + extendDist);
        //solve for x
        double u = Math.sqrt(Math.pow(extendDist, 2) / (1 + Math.abs(m)));
        if (m < 0f)
        {
          m *= -1;
        }
        //System.out.println("u = " + u);
        x_inc = (float) u;
        y_inc = (float) (m * u);
      }
    }
    if (p1_a.x > p1_b.x)
    {
      p2_a.x = p1_a.x + x_inc;
      p2_b.x = p1_b.x - x_inc;
    }
    else
    {
      p2_a.x = p1_a.x - x_inc;
      p2_b.x = p1_b.x + x_inc;
    }
    if (p1_a.y > p1_b.y)
    {
      p2_a.y = p1_a.y + y_inc;
      p2_b.y = p1_b.y - y_inc;
    }
    else
    {
      p2_a.y = p1_a.y - y_inc;
      p2_b.y = p1_b.y + y_inc;
    }
  }

  public static List<Point3f> getPointsOnQuadCurve(float x1, float y1,
    float ctrx, float ctrly,
    float x2, float y2, int resolution)
  {
    QuadCurve2D q2d = new QuadCurve2D.Float(x1, y1, ctrx, ctrly, x2, y2);
    System.out.printf("Quad: %f %f, %f %f, %f %f\n", x1, y1, ctrx, ctrly, x2, y2);

    Path2D.Float p2d = new Path2D.Float(q2d);

    List<Point2D> pts = getPointsFromPath2D(p2d);

    return point2DListToPoint3fList(pts);
  }

  public static float getSlope(Point3f p1, Point3f p2) //2D
  {
    //change in y divided by change in x
    float dx = p2.x - p1.x;
    float dy = p2.y - p1.y;

    if (dx == 0)
    {
      return Float.NaN; //ie, infinity
    }
    return dy / dx;
  }

  public static float getInverseSlope(Point3f p1, Point3f p2) //2D
  {
    float dx = p2.x - p1.x;
    float dy = p2.y - p1.y;

    if (dy == 0)
    {
      return Float.NaN; //ie, infinity
    }
    return -dx / dy;
  }

  public static float getYIntercept(float slope, Point3f p1) //2D
  {
    if (Float.isNaN(slope))
    {
      return Float.NaN;
    }
    return p1.y - (slope * p1.x);
  }

  /**
   * Pass in a two circles (described by a point and a radius) and two empty points that
   * will be used as the intersection points.
   * Returns false if there are no intersections.
   */
  public static boolean getIntersectionsOfTwoCircles(Point3f p0, float r0,
    Point3f p1, float r1,
    Point3f intersectPt1, Point3f intersectPt2)
  {

    float x0 = p0.x;
    float y0 = p0.y;
    float x1 = p1.x;
    float y1 = p1.y;
    float a, dx, dy, d, h, rx, ry;
    float x2, y2;

    // dx and dy are the vertical and horizontal distances between the circle centers.
    dx = x1 - x0;
    dy = y1 - y0;

    // Determine the straight-line distance between the centers.
    d = (float) Math.hypot(dx, dy);

    //Check for solvability.
    if (d > (r0 + r1))
    {
      //no solution. circles do not intersect.
      return false;
    }
    if (d < Math.abs(r0 - r1))
    {
      //no solution. one circle is contained in the other
      return false;
    }


    //'point 2' is the point where the line through the circle
    //intersection points crosses the line between the circle centers.

    // Determine the distance from point 0 to point 2.
    a = ((r0 * r0) - (r1 * r1) + (d * d)) / (2f * d);

    // Determine the coordinates of point 2.
    x2 = x0 + (dx * a / d);
    y2 = y0 + (dy * a / d);

    // Determine the distance from point 2 to either of the intersection points.
    h = (float) Math.sqrt((r0 * r0) - (a * a));

    // Now determine the offsets of the intersection points from point 2.
    rx = -dy * (h / d);
    ry = dx * (h / d);

    // Determine the absolute intersection points.
    intersectPt1.x = x2 + rx;
    intersectPt1.y = y2 + ry;
    intersectPt1.z = 0f;

    intersectPt2.x = x2 - rx;
    intersectPt2.y = y2 - ry;
    intersectPt2.z = 0f;

    return true;
  }
  //probabaly should use the other version which returns
  //an int sepcifying # of intersections

  public static boolean getIntersectionOfLineAndSphere(
    float x1, float y1, float z1, //endpoint1 of line segement
    float x2, float y2, float z2, //endpoint1 of line segement
    float x3, float y3, float z3, //center point of sphere
    float r, //radius of sphere
    Point3f intersectPt1, Point3f intersectPt2)
  {

    // x,y,z   intersection coordinates
    //
    // This function returns a pointer array which first index indicates
    // the number of intersection point, followed by coordinate pairs.




    //float x, y, z;


    float a, b, c, mu, i;

    a = (float) (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    b = (float) (2 * ((x2 - x1) * (x1 - x3) + (y2 - y1) * (y1 - y3) + (z2 - z1) * (z1 - z3)));
    c = (float) (Math.pow(x3, 2) + Math.pow(y3, 2) +
      Math.pow(z3, 2) + Math.pow(x1, 2) +
      Math.pow(y1, 2) + Math.pow(z1, 2) -
      2 * (x3 * x1 + y3 * y1 + z3 * z1) - Math.pow(r, 2));
    i = b * b - 4 * a * c;

    if (i < 0.0)
    {
      // no intersection
      return false;
    }
    else
    {
      if (i == 0.0)
      {
        // one intersection - line is tangent to sphere/circle

        mu = -b / (2 * a);
        intersectPt1.x = x1 + mu * (x2 - x1);
        intersectPt1.y = y1 + mu * (y2 - y1);
        intersectPt1.z = z1 + mu * (z2 - z1);
        return true;
      }
      else //if ( i > 0.0 )
      {
        // first intersection
        mu = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
        intersectPt1.x = x1 + mu * (x2 - x1);
        intersectPt1.y = y1 + mu * (y2 - y1);
        intersectPt1.z = z1 + mu * (z2 - z1);

        // second intersection
        mu = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
        intersectPt2.x = x1 + mu * (x2 - x1);
        intersectPt2.y = y1 + mu * (y2 - y1);
        intersectPt2.z = z1 + mu * (z2 - z1);

        return true;
      }
    }
  }

  /** returns 0 if no points are good,
  1 if the first point is good,
  2 if the second points is good,
  3 if both points are good
   */
  public static int getIntersectionOfLineAndSphere(
    boolean segmentIntersectionOnly, //if false, check the extended line...
    Point3f p1, //endpoint1 of line segement
    Point3f p2, //endpoint1 of line segement
    Point3f p3, //center point of sphere
    float r, //radius of sphere
    Point3f intersectPt1, Point3f intersectPt2)
  {

    // x,y,z   intersection coordinates
    //
    // This function returns a pointer array which first index indicates
    // the number of intersection point, followed by coordinate pairs.

    float x1 = p1.x;
    float y1 = p1.y;
    float z1 = p1.z;
    float x2 = p2.x;
    float y2 = p2.y;
    float z2 = p2.z;
    float x3 = p3.x;
    float y3 = p3.y;
    float z3 = p3.z;





    //  float x, y, z;


    float a, b, c, mu, i;

    a = (float) (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    b = (float) (2 * ((x2 - x1) * (x1 - x3) + (y2 - y1) * (y1 - y3) + (z2 - z1) * (z1 - z3)));
    c = (float) (Math.pow(x3, 2) + Math.pow(y3, 2) +
      Math.pow(z3, 2) + Math.pow(x1, 2) +
      Math.pow(y1, 2) + Math.pow(z1, 2) -
      2 * (x3 * x1 + y3 * y1 + z3 * z1) - Math.pow(r, 2));
    i = b * b - 4 * a * c;

    if (i < 0.0)
    {
      // no intersection
      return 0;
    }
    else
    {
      if (i == 0.0)
      {
        // one intersection - line is tangent to sphere/circle

        mu = -b / (2 * a);
        intersectPt1.x = x1 + mu * (x2 - x1);
        intersectPt1.y = y1 + mu * (y2 - y1);
        intersectPt1.z = z1 + mu * (z2 - z1);

        if (segmentIntersectionOnly == true)
        {
          if (p1.distance(p2) < Math.max(p1.distance(intersectPt1), p2.distance(intersectPt1)) - r)
          {
            return 0;
          }
        }

        /*if (checkLineForPoint(p1, p2, intersectPt1) == false && segmentIntersectionOnly == true)
        {
        return 0; //on line, but not on segment!
        }
         */
        return 1;
      }
      else //if ( i > 0.0 )
      {
        Point3f mp = getMidpointOfTwoPoints(p1, p2);

        boolean firstIsGood = true;
        boolean secondIsGood = true;
        // first intersection
        mu = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
        intersectPt1.x = x1 + mu * (x2 - x1);
        intersectPt1.y = y1 + mu * (y2 - y1);
        intersectPt1.z = z1 + mu * (z2 - z1);


        //System.out.println("dist between p1 and p2 = " + p1.distance(p2));
        //System.out.println("max dist from p1 or p2 to ip 1 = " + (  Math.max(p1.distance(intersectPt1), p2.distance(intersectPt1))  ) );
        //    System.out.println("r = " + r);

        //	System.out.println("intersectPt1 = " + intersectPt1);
        //	System.out.println("dist between p1 and mp = " + p1.distance(mp));
        //	System.out.println("dist between intersectPt1 and mp = " + intersectPt1.distance(mp));

        if (intersectPt1.distance(mp) > p1.distance(mp))
        {
          firstIsGood = false;
        }

        /*
        if (p1.distance(p2) < Math.max(p1.distance(intersectPt1), p2.distance(intersectPt1)) - (r))
        {
        firstIsGood = false;
        }
         */

        /*
        if (checkLineForPoint(p1, p2, intersectPt1) == false && segmentIntersectionOnly == true)
        {
        firstIsGood = false; //on line, but not on segment!
        }
         */

        // second intersection
        mu = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
        intersectPt2.x = x1 + mu * (x2 - x1);
        intersectPt2.y = y1 + mu * (y2 - y1);
        intersectPt2.z = z1 + mu * (z2 - z1);

        //		System.out.println("intersectPt2 = " + intersectPt2);
        //		System.out.println("dist between p1 and mp = " + p2.distance(mp));
//			System.out.println("dist between intersectPt2 and mp = " + intersectPt2.distance(mp));

        if (intersectPt2.distance(mp) > p1.distance(mp))
        {
          secondIsGood = false;
        }

        /*
        if (p1.distance(p2) < Math.max(p1.distance(intersectPt2), p2.distance(intersectPt2)) - r)
        {
        secondIsGood = false;
        }
         */
        /*
        if (checkLineForPoint(p1, p2, intersectPt2) == false && segmentIntersectionOnly == true)
        {
        secondIsGood = false; //on line, but not on segment!
        }
         */
        if (firstIsGood && secondIsGood)
        {
          return 3;
        }
        else
        {
          if (firstIsGood && !secondIsGood)
          {
            return 1;
          }
          else
          {
            if (!firstIsGood && secondIsGood)
            {
              return 2;
            }
          }
        }
        return 0;
      }
    }
  }

  public static String printLine(Line2D line)
  {
    return "<" + line.getX1() + "," + line.getY1() + "/" + line.getX2() + "," + line.getY2() + ">";
  }

  public static String printPoint(Point2D point)
  {
    return "<" + point.getX() + "," + point.getY() + ">";
  }

  public static String printRectangle(Rectangle2D rect)
  {
    return "<" + rect.getX() + "," + rect.getY() + "," + rect.getWidth() + "," + rect.getHeight() + ">";
  }

  /**
   * Get a Rectangle2D.Float view of a four sided Path2D.Float. This is *not* the same as
   * a call to getBounds! 
   * @param path
   * @return a rectangle2D.Float representation of the path.
   */
  public static Rectangle2D.Float pathToRect(Path2D.Float path)
  {
    //hmm isn't there some way of checking how many points the path has??
    // float x, y, w, h;

    PathIterator pi = path.getPathIterator(null);

    float[] vals = new float[6];

    Point2D.Float pt1 = new Point2D.Float();
    Point2D.Float pt2 = new Point2D.Float();

    //Point2D.Float curPoint = null;

    int idx = 0;

    while (!pi.isDone())
    {
      int type = pi.currentSegment(vals);

      if (type == PathIterator.SEG_CLOSE)
      {
        idx++;
        break;
      }
      else if (type == PathIterator.SEG_MOVETO)
      {
        idx = 0;
        pt1 = new Point2D.Float(vals[0], vals[1]);
      }
      else if (type == PathIterator.SEG_LINETO)
      {
        idx++;
        if (idx == 2)
        {
          pt2 = new Point2D.Float(vals[0], vals[1]);
        }
      }
      else
      {
        //error, this is not a rectangle!
      }

      pi.next();
    }

    if (idx != 4)
    {
      //error, this path is not a rectangle!
    }

    return new Rectangle2D.Float(pt1.x, pt1.y, pt2.x - pt1.x, pt1.y - pt2.y);
  }

  public static String printPath(
    Path2D.Float path)
  {
    String str = "";
    PathIterator pi = path.getPathIterator(null);

    float[] vals = new float[6];

    //Point2D.Float curPoint = null;

    while (!pi.isDone())
    {
      int type = pi.currentSegment(vals);

      if (type == PathIterator.SEG_CLOSE)
      {
        str += "CLOSE.\n";
        break;

      }
      else
      {
        if (type == PathIterator.SEG_MOVETO)
        {
          str += "MOVETO : " + vals[0] + "/" + vals[1] + "\n";
        }
        else
        {
          if (type == PathIterator.SEG_LINETO)
          {
            str += "LINETO : " + vals[0] + "/" + vals[1] + "\n";
          }
//handle curves later...
        }
      }
      pi.next();

    }

    return str;
  }

  /** not implemented yet! */
  public boolean checkIfBoxIsInView(Point3f pp1, Point3f pp2, Point3f pp3, Point3f pp4)
  {
    return true;
  }

  /** 
   * Checks to see if entire polygon (made up of some number of Point3fs) is completely contained
   * within the screen view. This method expects you to pass in **projected** corner ponts of a polygon. 
   * That is, the points must be in Screen coordinates, in PIXELS.
   * 
   * @param screen
   * @param pts
   * @return true if the polygon made up of projected points is completely contained in the screen veiw, otherwise false
   */
  public static boolean checkIfPolygonIsCompletelyContainedInView(Rectangle2D screen, Point3f... pts)
  {
//1. if all points are out of z-clipping planes, then return false
    boolean isWithinNearAndFarPlane = false;
    for (Point3f pt : pts)
    {
      if (pt.z > 0f && pt.z < 1f)
      {
        isWithinNearAndFarPlane = true;
        break;

      }


    }

    if (!isWithinNearAndFarPlane)
    {
      //System.out.println("all points are out of z clipping plane");
      return false;
    }

//2. if any *point* is not contained within the box, then we return false, 
//as the polygon is thus not completely contained.
    for (Point3f pt : pts)
    {
      if (!screen.contains(pt.x, pt.y))
      {
        return false;
      }

    }

    //if we are here then ALL corner points are in view
    return true;

  }

  /** pass in **projected** corner ponts of a rectangle. That is, the points 
   * must be in Screen coordinates, in PIXELS
   * 
   * returns "true" 
   * if any of the points are in the view Frustum, render it.
   * This is probably pretty slow.
   * 
   * But it is giving me great results... better than frustum culling...
   * Will have to test the speed, etc.
   **/
  public static boolean checkIfPolygonIsInView(Rectangle2D screen, Point3f... pts)
  {

    //1. if all points are out of z-clipping planes, then return false
    boolean isWithinNearAndFarPlane = false;
    for (Point3f pt : pts)
    {
      if (pt.z > 0f && pt.z < 1f)
      {
        isWithinNearAndFarPlane = true;
        break;

      }


    }

    if (!isWithinNearAndFarPlane)
    {
      //System.out.println("all points are out of z clipping plane");
      return false;
    }

//2. if any *point* is contained within the box, then we render the rectangle.
//this is the most likely situation, so we check this before dealing with projection errors, etc.
    for (Point3f pt : pts)
    {
      if (screen.contains(pt.x, pt.y))
      {
        return true;
      }

    }

    //3. check for backwards guys -- this happens because of a strange behavior with 
    //gluUnProject when points are nearly perpindicular to the viewport.
    //I am resolving by checking to see if z-value is > 1. If yes, flip the x and y vals
    for (Point3f pt : pts)
    {
      if (pt.z > 1)
      {
        pt.x *= -1;
        pt.y *= -1;
      }

    }


    //4. construct 2D projected shape from projected points
    //and make sure that it doesn't intersect with the screen shape
    Path2D.Float p2f = new Path2D.Float();
    p2f.moveTo(pts[0].x, pts[0].y);

    for (int i = 1; i <
      pts.length; i++)
    {
      p2f.lineTo(pts[i].x, pts[i].y);
    }

    p2f.closePath();


    if (p2f.intersects(screen))
    {
      //System.out.println("but line crosses a corner...");
      return true; //then needs to be rendered
    }

//System.out.println("point of rect is NOT in screen view");
    return false;
  }

//for normal opengl
  public static Point3f equiToPoint3f(
    double w, double h, double lng, double lat)
  {
    double px = (lng + 180.0) * (w / 360.0);
    double py = (lat + 90.0) * (h / 180.0);

    return new Point3f((float) px, (float) py, 0f);
  }

  public static Point3f equiToPoint3f(
    double slng, double elng,
    double slat, double elat,
    double w, double h, double lng, double lat)
  {
    double px = w * ((lng - slng) / (elng - slng));
    double py = h * ((lat - slat) / (elat - slat));

    return new Point3f((float) px, (float) py, 0f);
  }

//for orthographic projection
  public static Point equiToPixel(
    double w, double h, double lng, double lat)
  {
    int px = (int) Math.round((lng + 180.0) * (w / 360.0));
    int py = (int) Math.round(h - ((lat + 90.0) * (h / 180.0)));

    return new Point(px, py);
  }

  //wtf?
  @Deprecated
  public Point3f lngLatToPoint3f(
    float lng, float lat)
  {
//    float s1 = -75f;
//    float s2 = 0f;
//    float offset = 5f;
//    float ss = 10;

    if (lng >= -180 && lng < -80)
    { //compress
      lng = lng - (10 * ((lng + 180) / (100f)));
    }
    else
    {
      if (lng >= -80 && lng < -70)
      { //expand
        lng = lng + (10f * ((lng + 75f) / (5f)));
      }
      else
      {
        if (lng >= -70 && lng < -5)
        { //compress
          lng = lng + (10 * ((lng + 37.5f) / (-32.5f)));
        }
        else
        {
          if (lng >= -5 && lng < 5)
          { //expand
            lng = lng + (10 * ((lng) / (5f)));
          }
          else
          {
            if (lng >= 5 && lng <= 180)
            { //compress
              lng = lng - (10 * ((lng - 180) / (175f)));
            }

          }
        }
      }
    }
    lng = (lng + 180) / 360;
    lat =
      (lat - 90) / 180;

    Point3f newPoint = new Point3f((lng * 4080), (lat * 768), 0f);

    return newPoint;

  }

  /** 
   * Returns pt1 - pt2
   * @param pt1
   * @param pt2
   * @return pt1 - pt2
   */
  public static Point3f subtractPoint3f(Point3f pt1, Point3f pt2)
  {
    Point3f pt3 = new Point3f();
    pt3.sub(pt1, pt2);
    return pt3;
  }

  public static void sortRectanglesByArea(List<Rectangle2D> list, final int direction)
  {
    Collections.sort(list, new Comparator<Rectangle2D>()
    {

      public int compare(Rectangle2D a, Rectangle2D b)
      {
        double area1 = a.getWidth() * a.getHeight();
        double area2 = b.getWidth() * b.getHeight();

        if (area1 > area2)
        {
          return 1 * direction;
        }
        else if (area1 < area2)
        {
          return -1 * direction;
        }
        else
        {
          return 0;
        }
      }
    });
  }

  public static void sortRectanglesByDistanceToPoint(List<Rectangle2D> list, final Point2D p2d, final int direction)
  {
    final Point3f p3f = toPoint3f(p2d);

    Collections.sort(list, new Comparator<Rectangle2D>()
    {

      private float checkMinDist(Rectangle2D r)
      {
        float min = Float.POSITIVE_INFINITY;

        float d1 = GeomUtils.getDistanceBetweenPointAndLineSegment(
          p3f,
          new Point3f((float) r.getX(), (float) r.getY(), 0f),
          new Point3f((float) (r.getX() + r.getWidth()), (float) r.getY(), 0f));

        if (d1 < min)
        {
          min = d1;
        }

        float d2 = GeomUtils.getDistanceBetweenPointAndLineSegment(
          p3f,
          new Point3f((float) (r.getX() + r.getWidth()), (float) r.getY(), 0f),
          new Point3f((float) (r.getX() + r.getWidth()), (float) (r.getY() + r.getHeight()), 0f));

        if (d2 < min)
        {
          min = d2;
        }

        float d3 = GeomUtils.getDistanceBetweenPointAndLineSegment(
          p3f,
          new Point3f((float) (r.getX() + r.getWidth()), (float) (r.getY() + r.getHeight()), 0f),
          new Point3f((float) (r.getX()), (float) (r.getY() + r.getHeight()), 0f));

        if (d3 < min)
        {
          min = d3;
        }


        float d4 = GeomUtils.getDistanceBetweenPointAndLineSegment(
          p3f,
          new Point3f((float) (r.getX()), (float) (r.getY() + r.getHeight()), 0f),
          new Point3f((float) (r.getX()), (float) (r.getY()), 0f));

        if (d4 < min)
        {
          min = d4;
        }

        return min;
      }

      public int compare(Rectangle2D a, Rectangle2D b)
      {
        float minA = checkMinDist(a);
        float minB = checkMinDist(b);

        if (minA > minB)
        {
          return 1 * direction;
        }
        else if (minA < minB)
        {
          return -1 * direction;
        }
        return 0;
      }
    });
  }

  public static void sortRectanglesByDistanceOfCenterToPoint(List<Rectangle2D> list, final Point2D p2d, final int direction)
  {
    Collections.sort(list, new Comparator<Rectangle2D>()
    {

      public int compare(Rectangle2D a, Rectangle2D b)
      {
        Point2D pA = new Point2D.Double(a.getX() + (a.getWidth() * .5), a.getY() + (a.getHeight() * .5));
        Point2D pB = new Point2D.Double(b.getX() + (b.getWidth() * .5), b.getY() + (b.getHeight() * .5));

        double dist1 = pA.distance(p2d);
        double dist2 = pB.distance(p2d);

        if (dist1 > dist2)
        {
          return 1 * direction;
        }
        else if (dist1 < dist2)
        {
          return -1 * direction;
        }
        else
        {
          return 0;
        }
      }
    });
  }

  public static void sortRectanglesByAspectRatio(List<Rectangle2D> list, final double idealAspectRatio, final int direction)
  {
    Collections.sort(list, new Comparator<Rectangle2D>()
    {

      public int compare(Rectangle2D a, Rectangle2D b)
      {
        double arA_dist = Math.abs((a.getWidth() / a.getHeight()) - idealAspectRatio);
        double arB_dist = Math.abs((b.getWidth() / b.getHeight()) - idealAspectRatio);

        if (arA_dist > arB_dist)
        {
          return 1 * direction;
        }
        else if (arA_dist < arB_dist)
        {
          return -1 * direction;
        }
        else
        {
          return 0;
        }
      }
    });
  }
}
