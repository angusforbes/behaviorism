/* GeomGlutSphere.java ~ Feb 7, 2009 */

package geometry.glut;

import geometry.Geom;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import renderers.Renderer;

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
    gl.glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      Renderer.getInstance().glut.glutSolidCube(length);
    }
    else
    {
      Renderer.getInstance().glut.glutWireCube(length);
    }
  }
}
