/* GeomGluDisk.java ~ May 25, 2009 */
package behaviorism.geometry.glu;

import javax.vecmath.Point3f;
import behaviorism.utils.RenderUtils;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.glu.GLU;
import static behaviorism.utils.RenderUtils.*;


/**
 *
 * @author angus
 */
public class GeomGluCylinder extends GeomGluQuadric
{

  public float base = 1f;
  public float top = 1f;
  public float height = 1f;
  public int slices = 32;
  public int stacks = 32;

  public GeomGluCylinder(Point3f centerPt)
  {
    super(centerPt);
  }

  public GeomGluCylinder(Point3f centerPt, float base, float top, float height, int slices, int stacks)
  {
    super(centerPt);

    this.base = base;
    this.top = top;
    this.height = height;
    this.slices = slices;
    this.stacks = stacks;
  }

  @Override
  public void draw()
  {
    super.draw();
    GL2 gl = getGL();
    gl.glColor4fv(color.array(), 0);

    glu.gluCylinder(quadric,
      base, top, height, slices, stacks);
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
