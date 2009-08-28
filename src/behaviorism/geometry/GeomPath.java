/* GeomPath.java (created on October 22, 2007, 4:51 PM) */

package behaviorism. geometry;

import java.util.ArrayList;
import java.util.List;
import behaviorism.utils.RenderUtils;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.glu.GLU;
import static behaviorism.utils.RenderUtils.*;


public class GeomPath extends Geom
{
  public List<GeomPoint> vertices = new ArrayList<GeomPoint>();
 
  public GeomPath(List<GeomPoint> vertices)
  {
    this.vertices = vertices;
  }

  public void draw()
  {
    GL2 gl = getGL();
    gl.glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);

    //gl.glLineWidth(1f);
    gl.glBegin(GL_LINE_STRIP);
    
    for (int i = 0; i < vertices.size(); i++)
    {
        GeomPoint gp = vertices.get(i);
        gl.glVertex3f(gp.translate.x, gp.translate.y, gp.translate.z);
    }
    gl.glEnd();
    
  }
 
}
