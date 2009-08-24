/* GeomGlutSphere.java ~ Feb 7, 2009 */
package behaviorism.geometry.glut;

import behaviorism.geometry.Geom;
import behaviorism.utils.RenderUtils;
import javax.media.opengl.GL;
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

  public void draw(GL gl)
  {
    gl.glLineWidth(1f);
    gl.glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      RenderUtils.getRenderer().glut.glutSolidSphere(radius, slices, stacks);
    }
    else
    {
      RenderUtils.getRenderer().glut.glutWireSphere(radius, slices, stacks);
    }
  }
}
