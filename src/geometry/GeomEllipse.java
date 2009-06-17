/* GeomEllipse.java (created on February 17, 2008, 1:08 AM) */

package geometry;

import behaviorism.BehaviorismDriver;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.vecmath.Point3f;
import handlers.MouseHandler;
import renderers.RendererJogl;

public class GeomEllipse extends GeomPoly
{
  public float innerRadius = 0f;
  public float outerRadius = 1f;
  public int resolution = 32;
  public float startAngle = 0f;
  public float endAngle = 360f;
	public float borderSize = 0f;
  public float outerW = 1f;
	public float outerH = 1f;
					
  public GeomEllipse(float x, float y, float z, float outerW, float outerH)
  {
    super(x,y,z);
		
    initialize(outerW, outerH, 0f, 0f, 360f, 32);
  }
	
	public GeomEllipse(float x, float y, float z, float outerW, float outerH, float borderSize)
  {
    super(x,y,z);
		
    initialize(outerW, outerH, borderSize, 0f, 360f, 64);
  } 
	
	public GeomEllipse(Point3f p3f, float outerW, float outerH)
  {
    super(p3f);
		
    initialize(outerW, outerH, 0f, 0f, 360f, 64);
  }
	
	public GeomEllipse(Point3f p3f, float outerW, float outerH, float borderSize)
  {
    super(p3f);
		
    initialize(outerW, outerH, borderSize, 0f, 360f, 64);
  } 
	
	/*
  public GeomEllipse(Point3f centerPt, float radius)
  {
    super(centerPt);
    initialize(0f, radius, 0f, 360f, 32);
  }
  
  public GeomEllipse(float x, float y, float z, float radius, int resolution)
  {
    super(x,y,z);
    initialize(0f, radius, 0f, 360f, resolution);
  }
  
  public GeomEllipse(Point3f centerPt, float innerRadius, float outerRadius, int resolution)
  {
    super(centerPt);
    initialize(innerRadius, outerRadius, 0f, 360f, resolution);
  }
  
  public GeomEllipse(float x, float y, float z, float innerRadius, float outerRadius, float startAngle, float endAngle, int resolution)
  {
    super(x,y,z);
    initialize(innerRadius, outerRadius, startAngle, endAngle, resolution);
  }
  
  public GeomEllipse(Point3f centerPt, float innerRadius, float outerRadius, float startAngle, float endAngle, int resolution)
  {
    super(centerPt);
    initialize(innerRadius, outerRadius, startAngle, endAngle, resolution);
  }
	*/
	
  public void initialize(float outerW, float outerH, float borderSize, float startAngle, float endAngle, int resolution)
  {
    this.isSelectable = true;
    this.outerW = outerW;
		this.outerH = outerH;
    this.borderSize = borderSize;
		//this.h = h;
    //this.scale.x = 1f;
    //this.scale.y = 1f;
    //this.scale.z = 1f;
    this.scale.x = 1;
    this.scale.y = 1;
    this.scale.z = 1;
    this.resolution = resolution;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    setVerts();
  }
  
  public void setVerts()
  {
    double inc;
    double angle;
    vertices.clear();
    
    angle = startAngle;
    inc = (endAngle - startAngle)/(double)resolution;
    
    if (startAngle == 0f && endAngle == 360f) //Circle
    {
      inc = (endAngle - startAngle)/(double)resolution;
      
      for (int i = 0; i <= resolution; i++ )
      {
        this.vertices.add(new GeomPoint((float) (0 + (outerW * Math.cos(Math.toRadians(angle)))),
                (float)(0 + (outerH * Math.sin(Math.toRadians(angle)))),
                anchor.z //z
                ));
        angle+=inc;
        
      }
      angle -= inc;
      for (int i = resolution ; i >= 0; i-- )
      {
        //this.verts.add(new GeomPoint((float) (0 + (innerW * Math.cos(Math.toRadians(angle)))),
        //        (float)(0 + (innerH * Math.sin(Math.toRadians(angle)))),
        //        z));
				if (borderSize <= 0f)
				{
					//this.vertices.add(new GeomPoint(0f, 0f, z));
					this.vertices.add(new GeomPoint(0f, 0f, anchor.z));
				}
				else
				{
					//this.verts.add(new GeomPoint((float) (0f + ((outerW-(outerW * borderSize)) * Math.cos(Math.toRadians(angle)))),
				//					(float)(0f + ((outerH-(outerH * borderSize)) * Math.sin(Math.toRadians(angle)))), z));
						this.vertices.add(new GeomPoint((float) (0f + ((outerW-borderSize) * Math.cos(Math.toRadians(angle)))),
                (float)(0f + ((outerH-borderSize) * Math.sin(Math.toRadians(angle)))), 
                anchor.z //z
                ));
				
				}
        angle-=inc;
      }
    }
    else //Disk
    {
      inc = (endAngle - startAngle)/(double)resolution;
      
      for (int i = 0; i <= resolution; i++ )
      {
        this.vertices.add(new GeomPoint((float) (0 + (outerW * Math.cos(Math.toRadians(angle)))),
                (float)(0 + (outerH * Math.sin(Math.toRadians(angle)))),
                anchor.z //z
                ));
        angle+=inc;
        
      }
      angle -= inc;
      
      if (innerRadius > 0f) //reverse through points along inner radius
      {
        for (int i = resolution ; i >= 0; i-- )
        {
					this.vertices.add(new GeomPoint((float) (0f + ((outerW-borderSize) * Math.cos(Math.toRadians(angle)))),
                (float)(0f + ((outerH-borderSize) * Math.sin(Math.toRadians(angle)))),
                anchor.z //z
                ));
				
          //this.verts.add(new GeomPoint((float) (0 + (innerW * Math.cos(Math.toRadians(angle)))),
          //        (float)(0 + (innerH * Math.sin(Math.toRadians(angle)))),
          //        z));
          angle-=inc;
        }
      }
      else //just add the one center point
      {
        this.vertices.add(new GeomPoint(0f, 0f, 0f));
      }
    }
  }
  
  public void draw(GL gl, GLU glu, float offset)
  {
    if (this == MouseHandler.selectedGeom)
    {
      //System.out.println("offset = " + offset);
    }
   
	//	gl.glRotatef(30f,0f,0f,1f);
    //get Tesselator object
    GLUtessellator tobj = BehaviorismDriver.renderer.tessellationObject;
    
    if (tobj == null)
    {
      //error-- tesselationObjbect not ready yet, or initialized wrong (in RendererJogl!)
      return;
    }
    
    //get state variables
    boolean depthTest = RendererJogl.getBoolean(gl, GL.GL_DEPTH_TEST);
    
    if (depthTest == false && isSelectable == true)
    {
      //then we need to render it invisibly with DEPTH_TEST on so that we can pick it
      gl.glEnable(GL.GL_DEPTH_TEST);
      
      if (startAngle == 0f && endAngle == 360f)
      {
        gl.glColor4f(0f, 0f, 0f, 0f);
        drawCircleTesselation(gl, glu, tobj, offset);
      }
      else
      {
        gl.glColor4f(0f, 0f, 0f, 0f);
        drawDiskTesselation(gl, glu, tobj, offset);
      }
      
      gl.glDisable(GL.GL_DEPTH_TEST);
    }
    
    gl.glColor4f(r, g, b, a);
    
    if (startAngle == 0f && endAngle == 360f)
    {
      drawCircleTesselation(gl, glu, tobj, offset);
    }
    else
    {
      drawDiskTesselation(gl, glu, tobj, offset);
    }
  }
  
  private void drawDiskTesselation(GL gl, GLU glu, GLUtessellator tobj, float offset)
  {
    //glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);
    
    glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
    glu.gluTessBeginPolygon(tobj, null);
    
    glu.gluTessBeginContour(tobj);
    
    for (int i = 0; i < vertsSize(); i++)
    {
      GeomPoint p3f = getVert(i);
      double[] dubArr = new double[]{(p3f.anchor.x ),
      (p3f.anchor.y),
      (p3f.anchor.z + offset)};
      
      glu.gluTessVertex(tobj, dubArr, 0, dubArr);
    }
    
    
    glu.gluTessEndContour(tobj);
    glu.gluTessEndPolygon(tobj);
    
  }
  
  
  private void drawCircleTesselation(GL gl, GLU glu, GLUtessellator tobj, float offset)
  {
    //glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);
    glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
    glu.gluTessBeginPolygon(tobj, null);
    
    glu.gluTessBeginContour(tobj);
    for (int i = 0; i < vertsSize() / 2; i++)
    {
      GeomPoint p3f = getVert(i);
      double[] dubArr = new double[]{(p3f.anchor.x ),
      (p3f.anchor.y),
      (p3f.anchor.z + offset)};
      
      glu.gluTessVertex(tobj, dubArr, 0, dubArr);
    }
    glu.gluTessEndContour(tobj);
    
    
    //if (innerRadius > 0f)
    if (borderSize > 0f)
    {
      //float perc = innerRadius / outerRadius;
      //float perc = innerW / outerW;
      glu.gluTessBeginContour(tobj);
      
      //for (int i =0; i < vertsSize(); i++)
      for (int i = vertsSize()/2; i < vertsSize(); i++)
      {
        GeomPoint p3f = getVert(i);
        
        double[] dubArr = new double[]{(p3f.anchor.x ),
        (p3f.anchor.y ),
        ( (p3f.anchor.z ) + offset)};
        
        glu.gluTessVertex(tobj, dubArr, 0, dubArr);
      }
      
      
      glu.gluTessEndContour(tobj);
      
    }
    
    glu.gluTessEndPolygon(tobj);
    //glu.gluDeleteTess(tobj); //what does this do?
    
  }
  
  
  
  
  public void determineRotateAnchor(RotateEnum rotatePosition)
  {
    switch (rotatePosition)
    {
      case CENTER:
        this.rotateAnchor = new GeomPoint(0f, 0f, 0f);
        break;
    }
    
  }
  /*
  public void handleDoubleClick(MouseEvent me)
  {
    System.out.println("DOUBLE CLICK!!!!");
  }
   */
}
