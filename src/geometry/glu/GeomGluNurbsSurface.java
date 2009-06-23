/* GeomGluNurbsSurface.java ~ Apr 13, 2009 */
package geometry.glu;

import geometry.Geom;
import geometry.GeomPoint;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUnurbs;
import javax.vecmath.Point3f;
import renderers.Renderer;
import utils.RenderUtils;

/**
 *

 * @author angus
 */
public class GeomGluNurbsSurface extends Geom
{

  boolean drawControlPoints = false; //false;
  boolean drawControlLines = false;
  int numPointsU;
  int numPointsV;
  int numKnotsU;
  int numKnotsV;
  public GeomPoint[][] dynamicControlPoints = null;
  float[] knotArrayU;
  float[] knotArrayV;
  int strideU;
  int strideV;
  float[] controlArray;
  float[][][] controlArray2;
  int orderU;
  int orderV;

  private float[] toControlArray(float[][][] c3)
  {
    float c1[] = new float[c3.length * c3[0].length * c3[0][0].length];

    int idx = 0;
    for (int i = 0; i < c3.length; i++)
    {
      for (int j = 0; j < c3[i].length; j++)
      {
        for (int k = 0; k < c3[i][j].length; k++)
        {
          c1[idx] = c3[i][j][k];
          idx++;
        }
      }
    }
    return c1;
  }

  /**
   * Creates a non-dynamic NURBS curve out of a set of Point3fs.
   * @param p3f
   * @param p3fs
   * @param knotArray
   * @param order
   * @param drawControlPoints
   */
  public GeomGluNurbsSurface(Point3f p3f, List<Point3f> p3fs,
    float[] knotArrayU, float[] knotArrayV,
    int orderU, int orderV,
    boolean drawControlPoints)
  {
    super(p3f);

    this.numKnotsU = p3fs.size() + orderU;
    this.numKnotsV = p3fs.size() + orderU;
    this.knotArrayU = knotArrayU;
    this.orderU = orderU;
    this.orderV = orderV;
    this.strideU = 3;
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
  public GeomGluNurbsSurface(Point3f p3f,
    GeomPoint[][] controlPoints,
    float[] knotArrayU, float[] knotArrayV,
    int orderU, int orderV)
  {
    super(p3f);

    this.dynamicControlPoints = controlPoints;

    this.numPointsU = this.dynamicControlPoints.length;
    this.numPointsV = this.dynamicControlPoints[0].length;

    for (int i = 0; i < numPointsU; i++)
    {
      for (int j = 0; j < numPointsV; j++)
      {
        GeomPoint gp = this.dynamicControlPoints[i][j];
        addGeom(gp);
        gp.isVisible = false; //true; //drawControlPoints;
      }
    }

    //this.numKnots = controlPoints.size() + orderU;
    this.knotArrayU = knotArrayU;
    this.knotArrayV = knotArrayV;
    this.orderU = orderU;
    this.orderV = orderV;

    strideV = 3;
    strideU = numPointsV * strideV;

    this.controlArray = new float[3 * numPointsU * numPointsV];

    updateControlArray();
  }

  private void updateControlArray()
  {
    for (int i = 0; i < numPointsU; i++)
    {
      for (int j = 0; j < numPointsV; j++)
      {
        int idx = ((i * numPointsV * 3) + (j * 3));

        //System.out.println("idx = " + idx);
        controlArray[idx + 0] = dynamicControlPoints[i][j].translate.x;
        controlArray[idx + 1] = dynamicControlPoints[i][j].translate.y;
        controlArray[idx + 2] = dynamicControlPoints[i][j].translate.z;
      }
    }

  //System.out.println("controlArray = " + Arrays.toString(controlArray));
  }

  public void draw(GL gl)
  {
    GLU glu = RenderUtils.getGLU();

    if (dynamicControlPoints != null) //and there has been a change
    {
      updateControlArray();
    }

//    gl.glEnable(GL.GL_AUTO_NORMAL);
//    gl.glDisable(GL.GL_BLEND);

    gl.glColor4fv(color.array(), 0);
    //gl.glColor4f(r, g, b, a);
    gl.glLineWidth(1);

    GLUnurbs nurbs = RenderUtils.getNurbs();


    glu.gluBeginSurface(nurbs);
//    glu.gluNurbsSurface(nurbs,
//      numKnots, knotArray, stride, controlArray, order, GL.GL_MAP1_TEXTURE_COORD_2);
//    glu.gluNurbsCurve(nurbs,
//      numKnots, knotArray, stride, controlArray, order, GL.GL_MAP1_NORMAL);

//    glu.gluNurbsSurface(nurbs,
//      knotArrayU.length, knotArrayU, knotArrayV.length, knotArrayV,
//      strideU, strideV,
//      controlArray,
//      orderU, orderV,
//      GL.GL_MAP2_NORMAL);

    glu.gluNurbsSurface(nurbs,
      knotArrayU.length, knotArrayU, knotArrayV.length, knotArrayV,
      strideU, strideV,
      controlArray,
      orderU, orderV,
      GL.GL_MAP2_VERTEX_3);

    glu.gluEndSurface(nurbs);

    if (drawControlLines == true)
    {
      gl.glLineWidth(1f);

      gl.glColor4f(0f, 1f, 0f, 1f);
      for (int i = 0; i < numPointsU; i++)
      {
        gl.glBegin(GL.GL_LINE_STRIP);

        for (int j = 0; j < numPointsV; j++)
        {
          int idx = ((i * numPointsV * 3) + (j * 3));

          gl.glVertex3f(
            controlArray[idx + 0],
            controlArray[idx + 1],
            controlArray[idx + 2]);
        }
        gl.glEnd();
      }

      gl.glColor4f(0f, 0f, 1f, 1f);
      for (int i = 0; i < numPointsV; i++)
      {
        gl.glBegin(GL.GL_LINE_STRIP);
        for (int j = 0; j < numPointsU; j++)
        {
          int idx = (i * 3) + (j * numPointsU * 3);
          gl.glVertex3f(
            controlArray[idx + 0],
            controlArray[idx + 1],
            controlArray[idx + 2]);
        }
        gl.glEnd();
      }

    }

    if (drawControlPoints == true)
    {
      gl.glPointSize(15f);
      gl.glColor4f(1f, 1f, 1f, 1f);

      gl.glBegin(GL.GL_POINTS);
      for (int i = 0; i < controlArray.length; i += 3)
      {
        gl.glVertex3f(controlArray[i + 0], controlArray[i + 1], controlArray[i + 2]);
      }
      gl.glEnd();
    }

  }
}

