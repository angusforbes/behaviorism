/* GeomGluNurbsCurve.java ~ Feb 7, 2009 */
package geometry.glu;

import geometry.Geom;
import geometry.GeomPoint;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUnurbs;
import javax.vecmath.Point3f;
import renderers.Renderer;

/**
 *
 * @author angus
 */
public class GeomGluNurbsCurve extends Geom
{
  boolean drawControlPoints = true; //false;
  boolean drawControlLines = false; //true; //false;

  int numKnots;
  public List<GeomPoint> dynamicControlPoints = null;
  float[] knotArray;
  int stride;
  float[] controlArray;
  int order;

  /**
   * Creates a non-dynamic NURBS curve out of a set of Point3fs.
   * @param p3f
   * @param p3fs
   * @param knotArray
   * @param order
   * @param drawControlPoints
   */
  public GeomGluNurbsCurve(Point3f p3f, List<Point3f> p3fs, float[] knotArray, int order, boolean drawControlPoints)
  {
    super(p3f);

    this.numKnots = p3fs.size() + order;
    this.knotArray = knotArray;
    this.order = order;
    this.stride = 3;
    this.controlArray = new float[3 * p3fs.size()];

    for (int i = 0; i < p3fs.size(); i++)
    {
      controlArray[(i * 3) + 0] = p3fs.get(i).x;
      controlArray[(i * 3) + 1] = p3fs.get(i).y;
      controlArray[(i * 3) + 2] = p3fs.get(i).z;
    }

    this.drawControlPoints = drawControlPoints;
  }

  /**
   * Constructs a NURBS curve out of a set of GeomPoints which get attached to this Geom.
   * These GeomPoints can be controlled via behaviors to change the position of the NURBS curve.
   * @param p3f
   * @param controlPoints
   * @param knotArray
   * @param order
   */
  public GeomGluNurbsCurve(Point3f p3f, List<GeomPoint> controlPoints, float[] knotArray, int order)
  {
    super(p3f);

    this.dynamicControlPoints = controlPoints;
    for(GeomPoint gp : this.dynamicControlPoints)
    {
      addGeom(gp);
      gp.isVisible = false; //drawControlPoints;
    }

    this.numKnots = controlPoints.size() + order; 
    this.knotArray = knotArray;
    this.order = order;
    this.stride = 3;
    this.controlArray = new float[3 * controlPoints.size()];

    updateControlArray();
  }

  private void updateControlArray()
  {
    for (int i = 0; i < dynamicControlPoints.size(); i++)
    {
      controlArray[(i * 3) + 0] = dynamicControlPoints.get(i).translate.x;
      controlArray[(i * 3) + 1] = dynamicControlPoints.get(i).translate.y;
      controlArray[(i * 3) + 2] = dynamicControlPoints.get(i).translate.z;
    }
  }

  public void draw(GL gl)
  {
      GLU glu = Renderer.getInstance().glu;

    if (dynamicControlPoints != null) //and there has been a change
    {
      updateControlArray();
    }

      gl.glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    gl.glLineWidth(1);

    GLUnurbs nurbs = Renderer.getInstance().nurbsRenderer;

    glu.gluBeginCurve(nurbs);
//    glu.gluNurbsCurve(nurbs,
//      numKnots, knotArray, stride, controlArray, order, GL.GL_MAP1_TEXTURE_COORD_2);
//    glu.gluNurbsCurve(nurbs,
//      numKnots, knotArray, stride, controlArray, order, GL.GL_MAP1_NORMAL);
    glu.gluNurbsCurve(nurbs,
      numKnots, knotArray, stride, controlArray, order, GL.GL_MAP1_VERTEX_3);
    glu.gluEndCurve(nurbs);


    if (drawControlLines == true)
    {
      gl.glLineWidth(1f);

      gl.glBegin(GL.GL_LINE_STRIP);
      for (int i = 0; i < dynamicControlPoints.size(); i++)
      {
        gl.glVertex3f(controlArray[i * stride], controlArray[i * stride + 1], controlArray[i * stride + 2]);
      }
      gl.glEnd();
    }

    if (drawControlPoints == true)
    {
      gl.glPointSize(5f);

      gl.glBegin(GL.GL_POINTS);
      for (int i = 0; i < dynamicControlPoints.size(); i++)
      {
        gl.glVertex3f(controlArray[i * stride], controlArray[i * stride + 1], controlArray[i * stride + 2]);
      }
      gl.glEnd();
    }
  }
}
