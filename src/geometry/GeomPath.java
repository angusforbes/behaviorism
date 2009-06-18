/* GeomPath.java (created on October 22, 2007, 4:51 PM) */

package geometry;

import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class GeomPath extends Geom
{
  public List<GeomPoint> vertices = new ArrayList<GeomPoint>();
 
  public GeomPath(List<GeomPoint> vertices)
  {
    this.vertices = vertices;
  }

  public void draw(GL gl)
  {
    gl.glColor4f(r, g, b, a);

    //gl.glLineWidth(1f);
    gl.glBegin(gl.GL_LINE_STRIP);
    
    for (int i = 0; i < vertices.size(); i++)
    {
        GeomPoint gp = vertices.get(i);
        gl.glVertex3f(gp.anchor.x, gp.anchor.y, gp.anchor.z);
    }
    gl.glEnd();
    
  }
 
}
