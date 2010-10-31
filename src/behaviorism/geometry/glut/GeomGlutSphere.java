/* GeomGlutSphere.java ~ Feb 7, 2009 */
package behaviorism.geometry.glut;

import behaviorism.geometry.Geom;
import static behaviorism.utils.RenderUtils.*;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class GeomGlutSphere extends Geom
{

  float radius;
  int slices;
  int stacks;
  boolean isSolid = false; //true = solid, false = wire

  public GeomGlutSphere(Point3f p3f, float radius, int slices, int stacks, boolean isSolid)
  {
    super(p3f);
    this.radius = radius;
    this.slices = slices;
    this.stacks = stacks;
    this.isSolid = isSolid;
  }

  public void draw()
  {
    getGL().glLineWidth(1f);
    getGL().glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      getGLUT().glutSolidSphere(radius, slices, stacks);
    }
    else
    {
      getGLUT().glutWireSphere(radius, slices, stacks);
    }
  }
}
