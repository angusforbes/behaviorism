/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package geometry;

/**
 *
 * @author basakalper
 */


import behaviorism.BehaviorismDriver;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import utils.GeomUtils;

public class GeomPolyStrip extends Geom{


  public List<GeomPoint> vertices = new ArrayList<GeomPoint>();
  //public List<GeomPoint> selectableBoundary = new ArrayList<GeomPoint>();
  public boolean isConvex = true;
  public Path2D.Float path2D = null;
  
  public GeomPolyStrip()
  {
    this.isSelectable = true;
  }
  
  
  
  public GeomPolyStrip(List<Point3f> lst)
  {
    this.isSelectable = true;
    for(int i=0; i < lst.size(); i++)
    {
      Point3f p3f = lst.get(i);
      //System.out.printf("adding %f %f %f\n", p3f.x, p3f.y, p3f.z);
      vertices.add(new GeomPoint(p3f.x, p3f.y, p3f.z));
    }
    
    //add each GeomPoint to scene hierarchy
    for(int i=0; i<vertices.size(); i++)
    {
      //verts.get(i).isActive = true;
      this.geoms.add(vertices.get(i));
    }
    
    //selectableBoundary = verts;
  }
  
  public GeomPolyStrip(List<Point3f> lst, Point3f rel)
  {
    this.isSelectable = true;
    for(int i=0; i < lst.size(); i++)
    {
      Point3f p3f = lst.get(i);
      System.out.printf("adding %f %f %f\n", p3f.x, p3f.y, p3f.z);
      vertices.add(new GeomPoint(p3f.x, p3f.y, p3f.z));
    }
    
    this.anchor.set(rel.x, rel.y, rel.z);
    
    //add each GeomPoint to scene hierarchy
    for(int i=0; i<vertices.size(); i++)
    {
      //verts.get(i).isActive = true;
      this.geoms.add(vertices.get(i));
    }
    
    //selectableBoundary = verts;
  }
  
  
  public GeomPolyStrip(Point3f rel)
  {
    super(rel);
    this.isSelectable = true;
  }
  public GeomPolyStrip(float x, float y, float z)
  {
    super(x,y, z);
    this.isSelectable = true;
  }
  
  
  
  
  //also sets it...
  public Path2D.Float makePath2DFromPoly()
  {
    Path2D.Float p2d = new Path2D.Float();
    GeomPoint gp = this.vertices.get(0);
    p2d.moveTo(gp.anchor.x, gp.anchor.y);
    
    for (int i = 1; i < this.vertices.size(); i++)
    {
      gp = this.vertices.get(i);
      p2d.lineTo(gp.anchor.x, gp.anchor.y);
    }
    
    //last one...
    gp = this.vertices.get(0);
    p2d.lineTo(gp.anchor.x, gp.anchor.y);
    
    this.path2D = p2d;
    this.area = GeomUtils.area(this.path2D);
    return p2d;
  }

 

  protected void drawConvex(GL gl, GLU glu, float offset)
  {
    gl.glBegin(gl.GL_QUAD_STRIP);
    for(int i = 0; i < vertices.size(); i++)
    {
        gl.glVertex3f(vertices.get(i).anchor.x, vertices.get(i).anchor.y, vertices.get(i).anchor.z + offset);
    }
      
    gl.glEnd();
  }

  protected void drawTesselated(GL gl, GLU glu, float offset)
  {
      System.out.println("Warning: tesellated draw method is not implemented in GeomPolyStrip");
      
  }
    
	
	
  public void draw(GL gl)
  {
    GLU glu = BehaviorismDriver.renderer.glu;

        gl.glColor4f(r, g, b, a);
     
        if (isConvex == true)
        {
            drawConvex(gl, glu, offset);
        }
        else //is not a convex polygon, if we aren't sure-- just assume that it is not...
        {
            drawTesselated(gl, glu, offset);
        }
  }
  
}

