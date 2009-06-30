/*
 * GeomSimpleLine.java
 *
 * Created on August 13, 2007, 1:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package geometry;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;

/**
 * GeomSimpleLine is a simple line class to make lines which are *not* connected to any other geometry.
 * If you want to have lines that are relative to other geometry and with different modelview
 * transformations, etc, use GeomLine2, which *requires* the endpoints to be part of the scene graph
 * (that is, added with the "addGeom" method).
 */
public class GeomSimpleLine extends Geom
{

  public GeomPoint[] ends = new GeomPoint[2];
  public float lineWidth = .5f;
  boolean isStippled = false;
  short stipple = 0x0101;

  public GeomSimpleLine()
  {
  }

  @Override
  public String toString()
  {
    return super.toString() + ", e0: " + ends[0].toString() + ", e1: " + ends[1].toString() + "\n";
  }

  public static GeomSimpleLine newGeomLineWithSpecifiedWidthAndHeight(float w, float h)
  {
    GeomSimpleLine line = new GeomSimpleLine(new Point3f(),
      new Point3f(w, h, 0f));

    line.setTranslate(0f, 0f, 0f);
    return line;
  }

  public GeomSimpleLine(Point3f p1, Point3f p2, short stipple)
  {
    this.isStippled = true;
    this.stipple = stipple;
    ends[0] = new GeomPoint(p1.x, p1.y, p1.z);
    ends[1] = new GeomPoint(p2.x, p2.y, p2.z);
  }

  public GeomSimpleLine(Point3f anchor, Point3f p1, Point3f p2)
  {
    this.translate = anchor;
    ends[0] = new GeomPoint(p1.x, p1.y, p1.z);
    ends[1] = new GeomPoint(p2.x, p2.y, p2.z);
  }

  public GeomSimpleLine(Point3f p1, Point3f p2)
  {
    translate = p1;
    ends[0] = new GeomPoint(p1.x, p1.y, p1.z);
    ends[1] = new GeomPoint(p2.x, p2.y, p2.z);
  }

  public void draw(GL gl)
  {
    //System.out.println("in simpleline, translate = " + translate);
    //float hx = translate.x;
    //float hy = translate.y;
    //float hz = translate.z;

    if (isStippled == true)
    {
      gl.glEnable(GL.GL_LINE_STIPPLE);
      gl.glLineStipple(1, stipple);
    }

    //WEIRD sometimes won't draw if .5f line width1
    gl.glLineWidth(lineWidth); //
    //gl.glLineWidth(.5f);
    //gl.glLineWidth(2.5f);

    gl.glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    gl.glBegin(gl.GL_LINES);

    //gl.glVertex3f(ends[0].translate.x + hx, ends[0].translate.y + hy, ends[0].translate.z + hz);
    //gl.glVertex3f(ends[1].translate.x + hx, ends[1].translate.y + hy, ends[1].translate.z + hz);
    gl.glVertex3f(ends[0].translate.x, ends[0].translate.y, ends[0].translate.z + offset);
    gl.glVertex3f(ends[1].translate.x, ends[1].translate.y, ends[1].translate.z + offset);

    gl.glEnd();

    if (isStippled == true)
    {
      gl.glDisable(GL.GL_LINE_STIPPLE);
    }
  }
  /*
  public Point3f draw(GL gl, GLU glu, Point3f pp) {
  
  float hx =  translate.x + pp.x;
  float hy =  translate.y + pp.y;
  float hz =  translate.z + pp.z;
  
  gl.glColor4f(r, g, b, a);
  gl.glBegin(gl.GL_LINES);
  
  gl.glVertex3f(ends[0].translate.x + hx, ends[0].translate.y + hy, ends[0].translate.z + hz);
  gl.glVertex3f(ends[1].translate.x + hx, ends[1].translate.y + hy, ends[1].translate.z + hz);
  
  gl.glEnd();  
  
  return new Point3f(hx, hy, hz);
  }
   */
}


