/*
 * GeomPoint.java
 *
 * Created on July 12, 2007, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/**
 *
 * @author basakalper
 */
public class GeomPoint extends Geom
{
  public Point2D.Float texCoord = new Point2D.Float();
  public float pointSize = 8f;
  
  public GeomPoint()
  {
  }
  
  public GeomPoint(Point3f anchor)
  {
    this.anchor = anchor;
  }
  public GeomPoint(Point3f anchor, float pointSize)
  {
    this.anchor = anchor;
    this.pointSize = pointSize;
  }
  
  public GeomPoint(float x, float y, float z)
  {
    anchor.set(x, y, z);
  }
  public GeomPoint(float x, float y, float z, float pointSize)
  {
    anchor.set(x, y, z);
    this.pointSize = pointSize;
  }
  
  public void draw(GL gl)
  {
    gl.glColor4f(r, g, b, a);
    gl.glPointSize(this.pointSize);
    
    gl.glBegin(gl.GL_POINTS);
    //gl.glVertex3f(anchor.x, anchor.y , anchor.z);  //draws the point, it should be the point plus anchor
    gl.glVertex3f(0f, 0f, 0f);  //draws the point, it should be the point plus anchor
    gl.glEnd();
  }
  
  
  /*
  public Point3f draw(GL gl, GLU glu, Point3f pp)
  {
    
    float hx =  anchor.x + pp.x;
    float hy =  anchor.y + pp.y;
    float hz =  anchor.z + pp.z;
    
    
    gl.glColor4f(r, g, b, a);
    gl.glPointSize(8.0f);
    
    gl.glBegin(gl.GL_POINTS);
    gl.glVertex3f(hx, hy , hz);
    gl.glEnd();
    
    
    return new Point3f(hx, hy, hz);
  }
  */
  
  public void setPos(float xx, float yy, float zz)
  {
    anchor.set(xx, yy, zz);
  }
  
  public void setTexCoord(float xx, float yy)
  {
    texCoord.setLocation(xx, yy);
  }

  public static List<Point3f> geomPointfListToPoint3fList(List<GeomPoint> pts)
  {
    List<Point3f> gps = new ArrayList<Point3f>(); 
    for (GeomPoint p3f : pts)
    {
      gps.add(new Point3f(p3f.anchor));
    }
    return gps;
  }

  public static List<GeomPoint> point3fListToGeomPointList(List<Point3f> pts)
  {
    List<GeomPoint> gps = new ArrayList<GeomPoint>(); 
    for (Point3f p3f : pts)
    {
      gps.add(new GeomPoint(p3f));
    }
    return gps;
  }
}
