/* GeomGluQuadric.java ~ Jun 22, 2009 */

package geometry.glu;

import static javax.media.opengl.glu.GLU.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3f;
import utils.RenderUtils;
import geometry.Geom;
import javax.media.opengl.GL;

/**
 *
 * @author angus
 */
abstract public class GeomGluQuadric extends Geom
{
  public GLU glu = null;
  public GLUquadric quadric = null;
  int orientation = GLU_OUTSIDE;
  int drawStyle = GLU_FILL;
  int normals = GLU_SMOOTH;
  boolean texture = false;

  public GeomGluQuadric(Point3f p3f)
  {
    super(p3f);
    glu = RenderUtils.getGLU();
    quadric = RenderUtils.getQuadric();
  }

  public void orientation(int side)
  {
    if (side == GLU_INSIDE || side == GLU_OUTSIDE)
    {
      this.orientation = side;
    }
    else
    {
      System.err.println("Legal values are GLU_INSIDE and GLU_OUTSIDE");
    }
  }

  public void drawStyle(int style)
  {
  if (style == GLU_FILL || style == GLU_SILHOUETTE || style == GLU_POINT || style == GLU_LINE)
    {
      this.drawStyle = style;
    }
    else
    {
      System.err.println("Legal values are GLU_FILL, GLU_POINT, GLU_LINE, and GLU_SILHOUETTE");
    }
  }

  public void texture(boolean createTextureCoords)
  {
    this.texture = createTextureCoords;
  }
  
  public void normals(int normals)
  {
    if (normals == GLU_SMOOTH || normals == GLU_FLAT || normals == GLU_NONE)
    {
      this.normals = normals;
    }
    else
    {
      System.err.println("Legal values are GLU_SMOOTH, GLU_FLAT, and GLU_NONE");
    }
  }
                
  public void draw(GL gl)
  {
    glu.gluQuadricOrientation(quadric, orientation);
    glu.gluQuadricDrawStyle(quadric, drawStyle);
    glu.gluQuadricNormals(quadric, normals);
    glu.gluQuadricTexture(quadric, texture);
    
  }

}
