package geometry;

import behaviorism.BehaviorismDriver;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.vecmath.Point3f;
import utils.GeomUtils;
import utils.Utils;

public class GeomPoly extends Geom
{
  public List<GeomPoint> vertices = new ArrayList<GeomPoint>();
  //public List<GeomPoint> selectableBoundary = new ArrayList<GeomPoint>();
  public boolean isConvex = true;
  public Path2D.Float path2D = null;
  
  public static GeomPoly makeGeomPolyWithDynamicPoints(List<GeomPoint> vertices)
  {
    GeomPoly poly = new GeomPoly();
    poly.vertices = vertices;
    return poly;
  }
  
  
  public GeomPoly()
  {
    this.isSelectable = true;
  }
  public GeomPoly(boolean rand)
  {
    this.isSelectable = true;

    //initialize a poly with 4 vertices for now
    float nx = Utils.randomFloat(-2.5f, -1.5f);
    float ny = Utils.randomFloat(-2.5f, -1.5f);
    vertices.add(new GeomPoint(nx, ny, 0f));
    
    nx += Utils.randomFloat(3f, 5f);
    ny += Utils.randomFloat(-1f, 1f);
    vertices.add(new GeomPoint(nx, ny, 0f));
    
    nx += Utils.randomFloat(-1f, 1f);
    ny += Utils.randomFloat(4f, 5f);
    vertices.add(new GeomPoint(nx, ny, 0f));
    
    nx -= Utils.randomFloat(3f, 5f);
    ny += Utils.randomFloat(-1f, 1f);
    vertices.add(new GeomPoint(nx, ny, 0f));

    //selectableBoundary = verts;
  }

  public GeomPoly(Point3f ... p3fs)
  {
    //this.isSelectable = true;
    for(int i=0; i < p3fs.length; i++)
    {
      Point3f p3f = p3fs[i];
      vertices.add(new GeomPoint(p3f.x, p3f.y, p3f.z));
    }

    //add each GeomPoint to scene hierarchy
//    for(int i=0; i<vertices.size(); i++)
//    {
//      this.geoms.add(vertices.get(i));
//    }

  }
  /// goddamned erasure!!! won't let me have a constructor with List<point3f> and List<GeomPoint> !!! 
  
  public GeomPoly(List<Point3f> lst)
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
  
  
  public GeomPoly(List<Point3f> lst, Point3f rel)
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
  
  
  public GeomPoly(Point3f rel)
  {
    super(rel);
    this.isSelectable = true;
  }
  public GeomPoly(float x, float y, float z)
  {
    super(x,y, z);
    this.isSelectable = true;
  }
  
  public GeomPoly(List<Point3f> lst, boolean addToSceneGraph)
  {
    this.isSelectable = true;
    for(int i=0; i < lst.size(); i++)
    {
      Point3f p3f = lst.get(i);
      System.out.printf("adding %f %f %f\n", p3f.x, p3f.y, p3f.z);
      vertices.add(new GeomPoint(p3f.x, p3f.y, p3f.z));
    }
    
    if (addToSceneGraph == true)
    {
      //add each GeomPoint to scene hierarchy
      for(int i=0; i<vertices.size(); i++)
      {
        //verts.get(i).isActive = true;
        this.geoms.add(vertices.get(i));
      }
    }

    //selectableBoundary = verts;
  }
  
	@Deprecated 
  public void addVert(GeomPoint p)
  {
    vertices.add(p);
  }
  
	@Deprecated 
  public GeomPoint getVert(int i)
  {
    return vertices.get(i);
  }
 
	@Deprecated 
  public int vertsSize()
  {
    return vertices.size();
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

  //this isn't exactly the same as thhe other transform methods
  //but it should be-- investigate..
  /*
  public void transform(GL gl, GLU glu)
  {
    float hx =  anchor.x;
    float hy =  anchor.y;
    float hz =  anchor.z;
    float sax =  scaleAnchor.x;
    float say =  scaleAnchor.y;
    float saz =  scaleAnchor.z;
       
    //1. translate command
    gl.glTranslatef(hx, hy, hz);
    
    //3. rotate commands
    if(rotateAnchor != null)
    {
      float rax =  rotateAnchor.anchor.x;
      float ray =  rotateAnchor.anchor.y;
      float raz =  rotateAnchor.anchor.z;
      
      float rasx =  1f/scale.x;
      float rasy =  1f/scale.y;
      float rasz =  1f;
      //System.out.println("rotateAnchor.anchor = " + rotateAnchor.anchor);
      //System.out.println("rotateAnchor.scale = " + rotateAnchor.scale);

      //temp drawing rotate anchor
      gl.glColor4f(0f, 1f, 0f, 1f);
      gl.glBegin(gl.GL_POINTS);
      gl.glVertex3f(rax, ray, raz);
      gl.glEnd();
      //end temp drawing rotate anchor
      
      gl.glTranslatef(rax, ray, raz);
      gl.glRotatef(rotate.x,1.0f,0.0f,0.0f);
      gl.glRotatef(rotate.y,0.0f,1.0f,0.0f);
      gl.glRotatef(rotate.z,0.0f,0.0f,1.0f);
      gl.glTranslatef(-rax, -ray, -raz);
    }


    //2. scale commands
    
    //temp drawing scale anchor
    gl.glColor4f(1f, 0f, 0f,1f);
    gl.glBegin(gl.GL_POINTS);
    //gl.glVertex3f(sax, say, saz);
    gl.glVertex3f(0f, 0f,0f);
    gl.glEnd();
    
    gl.glScalef(scale.x, scale.y, scale.z);
    //gl.glTranslatef(-hx, -hy, -hz);
    gl.glTranslatef(-sax , -say , -saz );

    //System.out.println("scaleAnchor = " + scaleAnchor);  
    //System.out.println("anchor = " + anchor);  
  }
    */

	protected void drawConvex(GL gl, GLU glu, float offset)
	{
		gl.glBegin(gl.GL_POLYGON);
      for(int i = 0; i < vertices.size(); i++)
      {
        //System.out.printf("%d: drawing point (%f,%f)\n ", i, vertices.get(i).x, vertices.get(i).y);
        //gl.glVertex3f(getVert(i).anchor.x + anchor.x, getVert(i).anchor.y + anchor.y, getVert(i).anchor.z + anchor.z);
        gl.glVertex3f(vertices.get(i).anchor.x, vertices.get(i).anchor.y, vertices.get(i).anchor.z + offset);
      }
      
      gl.glEnd();
	}
	protected void drawTesselated(GL gl, GLU glu, float offset)
	{

      GLUtessellator tobj = BehaviorismDriver.renderer.tessellationObject;
      
      if (tobj == null)
      {
        //error-- tesselationObjbect not ready yet, or initialized wrong (in RendererJogl!)
        return;
      }
      
      double geomInfo[][] = new double[vertices.size()][6];
      for(int i=0; i< vertices.size(); i++)
      {
        GeomPoint p3f = vertices.get(i);
        
        //double[] dubArr = new double[]{(float)(p3f.anchor.x + anchor.x),
        //(float)(p3f.anchor.y + anchor.y),
        //(float)(p3f.anchor.z + anchor.z + offset)};
        double[] dubArr = new double[]{(float)(p3f.anchor.x),
        (float)(p3f.anchor.y),
        (float)(p3f.anchor.z + offset),
				r, g, b
				
				
				};
        
        geomInfo[i] = dubArr;
      }
    
			/*
       //top part of example deals with color... not sure about textures yet!
      double star[][] = new double[][] {// [5][6]; 6x5 in java
            //{ 5.0, 5.0, 0.0, 1.0, 0.0, 1.0 },
            //{ 3.0, 2.0, 0.0, 1.0, 1.0, 0.0 },
            //{ 4.0, 5.0, 0.0, 0.0, 1.0, 1.0 },
            //{ 2.0, 1.0, 0.0, 1.0, 0.0, 0.0 },
            //{ 4.0, 1.0, 0.0, 0.0, 1.0, 0.0 } };
            { 0.0, 0.0, 0.0 },
            { 3.0, 0.0, 0.0 },
            { 3.0, 3.0, 0.0 },
            { 0.0, 3.0, 0.0 },

            { 1.0, 1.0, 0.0 },
            { 2.0, 1.0, 0.0 },
            { 2.0, 2.0, 0.0 },
            { 1.0, 2.0, 0.0 }

            };
       */
      
      //glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);
      glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_NONZERO);
      //glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
      glu.gluTessBeginPolygon(tobj, null);
      glu.gluTessBeginContour(tobj);

			/*
      glu.gluTessVertex(tobj, star[0], 0, star[0]);
      glu.gluTessVertex(tobj, star[1], 0, star[1]);
      glu.gluTessVertex(tobj, star[2], 0, star[2]);
      glu.gluTessVertex(tobj, star[3], 0, star[3]);
      glu.gluTessEndContour(tobj);
     
       glu.gluTessBeginContour(tobj);

      glu.gluTessVertex(tobj, star[4], 0, star[4]);
      glu.gluTessVertex(tobj, star[5], 0, star[5]);
      glu.gluTessVertex(tobj, star[6], 0, star[6]);
      glu.gluTessVertex(tobj, star[7], 0, star[7]);
      */
      
			for (int i = 0; i < vertices.size(); i++)
			{
				glu.gluTessVertex(tobj, geomInfo[i], 0, geomInfo[i]);
			}
			/*
			glu.gluTessVertex(tobj, geomInfo[1], 0, geomInfo[1]);
      glu.gluTessVertex(tobj, geomInfo[2], 0, geomInfo[2]);
      glu.gluTessVertex(tobj, geomInfo[3], 0, geomInfo[3]);
      glu.gluTessVertex(tobj, geomInfo[4], 0, geomInfo[4]);
      */
      
      glu.gluTessEndContour(tobj);
      glu.gluTessEndPolygon(tobj);
      //glu.gluDeleteTess(tobj); //what does this do?
	}
	
  public void draw(GL gl, GLU glu, float offset)
  {
		
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
  
  /*
  public Point3f draw(GL gl, GLU glu, Point3f pp)
  {
    float hx=0, hy=0, hz=0;
    hx = anchor.x + pp.x;
    hy = anchor.y + pp.y;
    hz = anchor.z + pp.z;
    
    gl.glColor4f(r, g, b, a);
    
    if (isConvex == true)
    {
      gl.glColor4f(r, g, b, a);
      
      gl.glBegin(gl.GL_POLYGON);
      
      for(int i = 0; i < vertsSize(); i++)
      {
        //System.out.printf("%d: drawing point (%f,%f)\n ", i, g.getVert(i).anchor.x + g.anchor.x, g.getVert(i).anchor.y + g.anchor.y);
        gl.glVertex3f(getVert(i).anchor.x + hx, getVert(i).anchor.y + hy, getVert(i).anchor.z + hz);
      }
      
      gl.glEnd();
      return new Point3f(hx, hy, hz);
      
    }
    else //is not a convex polygon, if we aren't sure-- just assume that it is not...
    {
      //FIX THIS LATER-- after fixing main part above-- just needs to return a valid anchor point
      GLUtessellator tobj = BehaviorismDriver.renderer.tessellationObject;
      
      if (tobj == null)
      {
        //error-- tesselationObjbect not ready yet, or initialized wrong (in RendererJogl!)
        return null;
      }
      
      double geomInfo[][] = new double[vertsSize()][3];
      for(int i=0; i<vertsSize(); i++)
      {
        GeomPoint p3f = getVert(i);
        
        double[] dubArr = new double[]{(float)(p3f.anchor.x + anchor.x),
        (float)(p3f.anchor.y + anchor.y),
        (float)(p3f.anchor.z + anchor.z)};
        
        geomInfo[i] = dubArr;
      }
      
      gl.glColor4f(r, g, b, a);
      
      glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);
      glu.gluTessBeginPolygon(tobj, null);
      glu.gluTessBeginContour(tobj);
      glu.gluTessVertex(tobj, geomInfo[0], 0, geomInfo[0]);
      glu.gluTessVertex(tobj, geomInfo[1], 0, geomInfo[1]);
      glu.gluTessVertex(tobj, geomInfo[2], 0, geomInfo[2]);
      glu.gluTessVertex(tobj, geomInfo[3], 0, geomInfo[3]);
      glu.gluTessVertex(tobj, geomInfo[4], 0, geomInfo[4]);
      glu.gluTessEndContour(tobj);
      glu.gluTessEndPolygon(tobj);
      //glu.gluDeleteTess(tobj); //what does this do?

      return null;
    }
    
  }
  */

  public String toString()
  {
    String s = "vertices: ";
    for(int i=0; i < vertices.size(); i++)
    {
      s += "v" + i + ": " + vertices.get(i) + " ";
    }
    return s;
  }
}
