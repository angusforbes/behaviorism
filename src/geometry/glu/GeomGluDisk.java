/* GeomGluDisk.java ~ May 25, 2009 */

package geometry.glu;

import behaviorism.BehaviorismDriver;
import geometry.Geom;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class GeomGluDisk extends Geom
{
  public float innerRadius = 0f;
  public float outerRadius = 1f;
  public int resolution = 32;
  public float startAngle = 0f;
  public float endAngle = 360f;

  public GeomGluDisk(Point3f centerPt)
  {
    //use defaults
  }

  public GeomGluDisk(Point3f centerPt, float innerRadius, float outerRadius, float startAngle, float endAngle, int resolution)
  {
    super(centerPt);
  
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
    this.resolution = resolution;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    //setVerts();
 
  }

  public void draw(GL gl)
  {
    GLU glu = BehaviorismDriver.renderer.glu;

    gl.glColor4f(r, g, b, a);
    glu.gluPartialDisk(BehaviorismDriver.renderer.quadricRenderer,
      this.innerRadius, this.outerRadius,
      this.resolution, this.resolution,
      this.startAngle, this.endAngle - this.startAngle);


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
