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
public class GeomGlutCube extends Geom
{
  float length;
  boolean isSolid = false; //true = solid, false = wire
  public GeomGlutCube(Point3f p3f, float length, boolean isSolid)
  {
    super(p3f);
    this.length = length;
    this.isSolid = isSolid;
  }

  public void draw(GL gl)
  {
    gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      BehaviorismDriver.renderer.glut.glutSolidCube(length);
    }
    else
    {
      BehaviorismDriver.renderer.glut.glutWireCube(length);
    }
  }
}
