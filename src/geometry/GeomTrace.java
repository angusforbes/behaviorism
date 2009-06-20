/* GeomTrace.java (created on August 28, 2007, 1:35 PM) */
package geometry;

import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/*
 * GeomTrace keeps a list of the history of points and draws a line between all of these.
 */
public class GeomTrace extends GeomPoint
{
  public float lineWidth = 1f;
  public LinkedList<Point3f> trace;
  public int maxPoints = 10000;

  /**
   * Creates a new GeomTrace anchored at a specified point with the default number of trace points.
   * @param p3f The achor point.
   */
  public GeomTrace(Point3f p3f)
  {
    super(p3f);
    this.trace = new LinkedList<Point3f>();
  }

  /**
   * Creates a new GeomTrace anchored at a specified point with a maximum number of trace points.
   * @param p3f The achor point.
   * @param maxPoints The maximum number of points in the trace.
   */
  public GeomTrace(Point3f p3f, int maxPoints)
  {
    super(p3f);
    this.maxPoints = maxPoints;
    this.trace = new LinkedList<Point3f>();
  }

  /**
   * Creates a new GeomTrace anchored at a specified point with a maximum number of trace points.
   * @param p3f The achor point.
   * @param maxPoints The maximum number of points in the trace.
   */
  public GeomTrace(Point3f p3f, List<Point3f> trace, int maxPoints)
  {
    super(p3f);
    this.maxPoints = maxPoints;
    this.trace = new LinkedList<Point3f>(trace);
  }


  /**
   * Adds a point to the trace. Checks to make sure the same point isn't already at the end of the List of points.
   * If adding another point would make the size of the List of points greater than maxPoints, then first delete
   * the oldest point before adding this one.
   * @param p3f The point we are adding to the trace.
   */
  public void addPoint(Point3f p3f)
  {
    if (this.trace.size() > 0)
    {
      if (!(this.trace.getLast().equals(p3f)))
      {
        if (this.trace.size() > maxPoints)
        {
          this.trace.removeFirst();
        }
        this.trace.addLast(new Point3f(p3f));
      }
    }
    else
    {
      this.trace.addLast(new Point3f(p3f));
    }
  }

  @Override
  public void draw(GL gl)
  {
    //System.out.println("in GeomTrace() : trace size = " + trace.size());
    gl.glColor4f(r, g, b, a);
    //gl.glColor4f(1f, 1f, 1f, 1f);
    gl.glLineWidth(this.lineWidth);

    gl.glBegin(gl.GL_LINE_STRIP);
    //gl.glBegin(gl.GL_TRIANGLE_STRIP);
    //gl.glBegin(gl.GL_POLYGON);
    //gl.glBegin(gl.GL_POINTS);

    //Point3f p3f1 = new Point3f(0f, 0f, 0f);
    Point3f p3f1 = new Point3f(this.translate);
    //synchronized(trace)
    {
      for (int i = 0; i < trace.size(); i++)
      {
        p3f1 = trace.get(i);

        //System.out.println("currentPt = " + p3f1 + ", prevPt = " + prev_p3f1);
        gl.glVertex3f(p3f1.x, p3f1.y, p3f1.z);
      }
      gl.glEnd();
    }
  //trace.add(new Point3f(0f - translate.x, 0f - translate.y, 0f - translate.z));
    /*
  gl.glColor4f(r, g, b, a);
  gl.glPointSize(this.pointSize);

  gl.glBegin(gl.GL_POINTS);
  gl.glVertex3f(translate.x, translate.y , translate.z);  //draws the point, it should be the point plus translate
  gl.glEnd();
   */
  }
}
