/* SpiralArc.java ~ Jun 12, 2010 */
package behaviorism.geometry.primitives;

import behaviorism.geometry.GeomArc;
import behaviorism.geometry.GeomPoint;
import behaviorism.geometry.GeomSimpleLine;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author angus
 */
public class SpiralArc extends CircularArc
{

  int numLoops;
  float factor;

  public SpiralArc(Point3f centerPt, float radius, float startAngle, float endAngle, int direction,
    int numLoops, float factor)
  {
    super(centerPt, radius, startAngle, endAngle, direction);
    this.numLoops = numLoops;
    this.factor = factor;
  }

  @Override
  public List<Point3f> getPoints(int resolution)
  {
    System.out.println("in SpiralArc, getPoints()");
    List<CircularArc> arcs = getArcs();

    List<Point3f> points = new ArrayList<Point3f>();

    for (CircularArc arc : arcs)
    {
      points.addAll(arc.getPoints(resolution));
    }

    return points;
  }

  public List<CircularArc> getArcs()
  {
    Segment segment = new Segment(this.centerPt, this.startAngle, this.radius);
    System.out.println("in SpiralArc, getArcs() : using segment " + segment);
    System.out.println("numCircles = " + numLoops);
    System.out.println("factor = " + factor);

    List<CircularArc> returnArcs = new ArrayList<CircularArc>();

    float initRadius = this.radius;
    System.out.println("in SpiralArc, getArcs() : initRadius = " + initRadius);

    float inc = 0f;

    float[] incs = new float[numLoops];
    float coeff = 1f;
    inc = coeff;
    incs[0] = 1f;
    int coeffDir = 1;
    for (int i = 1; i < numLoops; i++)
    {
      coeffDir *= -1;
      coeff += (float) Math.pow(factor, i);
      System.out.println("coeff =" + (coeff));
      inc += (coeff * coeffDir);
      incs[i] = inc;
      System.out.println("inc " + i + "= " + inc);
    }
    System.out.println("inc before dividing = " + inc);
    inc = 1f / inc;
    inc = initRadius * inc;

    for (int i = 0; i < numLoops; i++)
    {
      incs[i] *= inc;
      //incs[i] *= .5f;
      System.out.println("incs[" + i + "] = " + incs[i]);
    }

//    float[] incs = new float[this.numLoops];
//    float coeff = 1f;
//    int coeffDir = 1;
//    float inc = coeff;
//    incs[0] = 1f;
//    for (int i = 1; i < this.numLoops; i++)
//    {
//      System.out.println("inc is... " + inc);
//      coeffDir *= -1;
//      coeff += (float) Math.pow(this.factor, i);
//      System.out.println("coeff =" + (coeff));
//      inc += (coeff * coeffDir);
//      incs[i] = inc;
//      System.out.println("inc " + i + "= " + inc);
//    }
//    System.out.println("inc before dividing = " + inc);
//    inc = (1f / inc);
//    inc = inc * initRadius;
//    System.out.println("inc = " + inc);
//
//    for (int i = 0; i < this.numLoops; i++)
//    {
//      incs[i] *= inc;
//      System.out.println("incs[" + i + "] = " + incs[i]);
//    }

    Point3f startPt = this.centerPt;
    Vector3f startVec;
    Vector3f dirVec;

    dirVec = segment.getTangent(1f);

    for (int i = 0; i < this.numLoops; i++)
    {
      startVec = new Vector3f(dirVec);
      startVec.scale(incs[i]);
      Point3f endPoint = new Point3f(this.centerPt);
      endPoint.add(startVec);
      Segment seg = new Segment(startPt, endPoint);

      float sA, eA;
      CircularArc circ;

      int curDir = this.direction;

      if (curDir == Circle.COUNTERCLOCKWISE)
      {
        sA = seg.getAngle();
        circ = new CircularArc(
          //seg.getMidpoint(), seg.getLength() * .5f, sA, sA - Circle.PI);
          seg.getMidpoint(), seg.getLength() * .5f, sA + Circle.PI, sA, this.direction);
      }
      else
      {
        sA = seg.getAngle() - Circle.PI;
        circ = new CircularArc(
          seg.getMidpoint(), seg.getLength() * .5f, sA, sA + Circle.PI, this.direction);
      }

      if ( i == numLoops - 1)
      {
        circ.endAngle = this.endAngle;
      }

      curDir *= -1;

      returnArcs.add(circ);

      System.out.println("circ = " + circ);

      startPt = seg.p2;

    }
    return returnArcs;
  }
}
