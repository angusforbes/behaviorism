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
import javax.media.opengl.glu.GLU;
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

    line.setPos(0f, 0f, 0f);
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
    this.anchor = anchor;
    ends[0] = new GeomPoint(p1.x, p1.y, p1.z);
    ends[1] = new GeomPoint(p2.x, p2.y, p2.z);
  }

  public GeomSimpleLine(Point3f p1, Point3f p2)
  {
    anchor = p1;
    ends[0] = new GeomPoint(p1.x, p1.y, p1.z);
    ends[1] = new GeomPoint(p2.x, p2.y, p2.z);
  }

  public void draw(GL gl, GLU glu, float offset)
  {
    //System.out.println("in simpleline, anchor = " + anchor);
    //float hx = anchor.x;
    //float hy = anchor.y;
    //float hz = anchor.z;

    if (isStippled == true)
    {
      gl.glEnable(GL.GL_LINE_STIPPLE);
      gl.glLineStipple(1, stipple);
    }

    //WEIRD sometimes won't draw if .5f line width1
    gl.glLineWidth(lineWidth); //
    //gl.glLineWidth(.5f);
    //gl.glLineWidth(2.5f);

    gl.glColor4f(r, g, b, a);
    gl.glBegin(gl.GL_LINES);

    //gl.glVertex3f(ends[0].anchor.x + hx, ends[0].anchor.y + hy, ends[0].anchor.z + hz);
    //gl.glVertex3f(ends[1].anchor.x + hx, ends[1].anchor.y + hy, ends[1].anchor.z + hz);
    gl.glVertex3f(ends[0].anchor.x, ends[0].anchor.y, ends[0].anchor.z + offset);
    gl.glVertex3f(ends[1].anchor.x, ends[1].anchor.y, ends[1].anchor.z + offset);

    gl.glEnd();

    if (isStippled == true)
    {
      gl.glDisable(GL.GL_LINE_STIPPLE);
    }
  }
  /*
  public Point3f draw(GL gl, GLU glu, Point3f pp) {
  
  float hx =  anchor.x + pp.x;
  float hy =  anchor.y + pp.y;
  float hz =  anchor.z + pp.z;
  
  gl.glColor4f(r, g, b, a);
  gl.glBegin(gl.GL_LINES);
  
  gl.glVertex3f(ends[0].anchor.x + hx, ends[0].anchor.y + hy, ends[0].anchor.z + hz);
  gl.glVertex3f(ends[1].anchor.x + hx, ends[1].anchor.y + hy, ends[1].anchor.z + hz);
  
  gl.glEnd();  
  
  return new Point3f(hx, hy, hz);
  }
   */
}


