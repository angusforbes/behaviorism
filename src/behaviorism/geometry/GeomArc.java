/* GeomArc.java ~ Jun 3, 2010 */

package behaviorism.geometry;

import behaviorism.geometry.primitives.Circle;
import behaviorism.geometry.primitives.CircularArc;
import behaviorism.utils.MatrixUtils;
import behaviorism.utils.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL2;
import javax.vecmath.Point3f;

/**
 * this will be a newer better version of GeomCircle, it should replace the previous one... soon
 * @author angus
 */
public class GeomArc extends Geom
{
  private boolean isCircle = false;
  public List<Point3f> vertices = new ArrayList<Point3f>();

  //shouldn't need to specify direction here!
  public GeomArc(Circle circle, int resolution, int dir)
  {
    super(new Point3f()); //circle.centerPt);
    setColor(1f,1f,1f,1f);
    vertices.addAll(circle.getPoints(resolution, dir));
    isCircle = true;
  }

  //shouldn't need to specify direction here!
  public GeomArc(Circle circle, int resolution)
  {
    super(new Point3f()); //circle.centerPt);
    setColor(1f,1f,1f,1f);
    vertices.addAll(circle.getPoints(resolution, Circle.COUNTERCLOCKWISE));
    isCircle = true;
  }

  //shouldn't need to specify direction here!
  public GeomArc(CircularArc arc, int resolution, int dir)
  {
    //super(arc.centerPt);
    super(new Point3f());
    setColor(1f,1f,1f,1f);
    vertices.addAll(arc.getPoints(resolution, dir));
    isCircle = false;
  }

  public GeomArc(CircularArc arc, int resolution)
  {
    //super(arc.centerPt);
    super(new Point3f());
    setColor(1f,1f,1f,1f);
    vertices.addAll(arc.getPoints(resolution));
    isCircle = false;
  }

/*
  public GeomArc(SpiralArc arc, int resolution)
  {
    //super(arc.centerPt);
    super(new Point3f());
    setColor(1f,1f,1f,1f);
    vertices.addAll(arc.getPoints(resolution, arc.direction));
    isCircle = false;
  }
  */

  @Override
  public void draw()
  {
    GL2 gl = RenderUtils.getGL();

    gl.glColor4fv(color.array(), 0);
    gl.glBegin(gl.GL_LINE_STRIP);
    {
      for (Point3f vertex : vertices)
      {
        gl.glVertex3fv(MatrixUtils.toArray(vertex), 0);
      }
    }

    if (isCircle) //then need to reconnect to first vertex.
    {
      gl.glVertex3fv(MatrixUtils.toArray(vertices.get(0)), 0);
    }

    gl.glEnd();
  }





}
