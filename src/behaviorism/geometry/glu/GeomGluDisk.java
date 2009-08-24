/* GeomGluDisk.java ~ May 25, 2009 */

package behaviorism. geometry.glu;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class GeomGluDisk extends GeomGluQuadric
{
  public float innerRadius = 0f;
  public float outerRadius = 1f;
  public int slices = 32;
  public int loops = 32;

  public GeomGluDisk(Point3f centerPt)
  {
    super(centerPt);
  }

  public GeomGluDisk(Point3f centerPt, float innerRadius, float outerRadius, int slices, int loops)
  {
    super(centerPt);
  
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
    this.slices = slices;
    this.loops = loops;
    //setVerts();
 
  }

  public void draw(GL gl)
  {
    super.draw(gl);

    gl.glColor4fv(color.array(), 0);

    glu.gluDisk(quadric,
      this.innerRadius, this.outerRadius,
      this.slices, this.loops);
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
