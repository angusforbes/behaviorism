/* GeomGluDisk.java ~ May 25, 2009 */

package behaviorism. geometry.glut;

import behaviorism.geometry.Geom;
import static behaviorism.utils.RenderUtils.*;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class GeomGlutTorus extends Geom
{
  public float innerRadius = .25f; //inner radius is the radius of the "tube"
  public float outerRadius = 1f; //outer radius is the radius of big circle the the tube is centered on
  public int slices = 32; //number of big cricles the torus is made up of
  public int stacks = 32; //number of little tubes segments the torus is made up of
  public boolean isSolid = false;
  public float lineWidth = 1f;

  public GeomGlutTorus(Point3f centerPt)
  {
    super(centerPt);
    //use defaults
  }

  public GeomGlutTorus(Point3f centerPt, float innerRadius, float outerRadius,
    int slices, int stacks, boolean isSolid)
  {
    super(centerPt);
  
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
    this.slices = slices;
    this.stacks = stacks;
    this.isSolid = isSolid;
    //setVerts();
 
  }

  public void draw()
  {

    getGL().glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      getGLUT().glutSolidTorus(innerRadius, outerRadius, slices, stacks);
    }
    else
    {
      //gl.glLineWidth(lineWidth);
      getGLUT().glutWireTorus(innerRadius, outerRadius, slices, stacks);
    }
  }

  /*
  da = 2.0 * M_PI / slices;
   dr = (outerRadius - innerRadius) / (GLfloat) loops;

   GLfloat dtc = 2.0f * outerRadius;
	 GLfloat sa, ca;
	 GLfloat r1 = innerRadius;
	 GLint l;
	 for (l = 0; l < loops; l++) {
	    GLfloat r2 = r1 + dr;
	    if (qobj->Orientation == GLU_OUTSIDE) {
	       GLint s;
	       glBegin(GL_QUAD_STRIP);
	       for (s = 0; s <= slices; s++) {
		  GLfloat a;
		  if (s == slices)
		     a = 0.0;
		  else
		     a = s * da;
		  sa = sin(a);
		  ca = cos(a);
		  TXTR_COORD(0.5 + sa * r2 / dtc, 0.5 + ca * r2 / dtc);
		  glVertex2f(r2 * sa, r2 * ca);
		  TXTR_COORD(0.5 + sa * r1 / dtc, 0.5 + ca * r1 / dtc);
		  glVertex2f(r1 * sa, r1 * ca);
	       }
	       glEnd();
	    }
	    else {
	       GLint s;
	       glBegin(GL_QUAD_STRIP);
	       for (s = slices; s >= 0; s--) {
		  GLfloat a;
		  if (s == slices)
		     a = 0.0;
		  else
		     a = s * da;
		  sa = sin(a);
		  ca = cos(a);
		  TXTR_COORD(0.5 - sa * r2 / dtc, 0.5 + ca * r2 / dtc);
		  glVertex2f(r2 * sa, r2 * ca);
		  TXTR_COORD(0.5 - sa * r1 / dtc, 0.5 + ca * r1 / dtc);
		  glVertex2f(r1 * sa, r1 * ca);
	       }
	       glEnd();
	    }
	    r1 = r2;
	 }
	 break;
   */
}
