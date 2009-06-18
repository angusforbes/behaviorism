/* GeomGlutSphere.java ~ Feb 7, 2009 */

package geometry.glut;

import behaviorism.BehaviorismDriver;
import geometry.Geom;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
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
    gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      BehaviorismDriver.renderer.glut.glutSolidSphere(radius, slices, stacks);
    }
    else
    {
      BehaviorismDriver.renderer.glut.glutWireSphere(radius, slices, stacks);
    }
  }

}
