/* GeomGlutSphere.java ~ Feb 7, 2009 */
package behaviorism.geometry.glut;

import behaviorism.geometry.Geom;
import static behaviorism.utils.RenderUtils.*;
import behaviorism.utils.RenderUtils;
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

  public void draw()
  {
    getGL().glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    if (isSolid == true)
    {
      RenderUtils.getGLUT().glutSolidCube(length);
    }
    else
    {
      RenderUtils.getGLUT().glutWireCube(length);
    }
  }
}
