package behaviorism.shaders;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import behaviorism.utils.RenderUtils;
import static behaviorism.utils.RenderUtils.*;

/**
 *
 * @author angus
 */
public enum ShaderEnum
{

  VERTEX(GL_VERTEX_SHADER),
  FRAGMENT(GL_FRAGMENT_SHADER),
  GEOMETRY(GL_GEOMETRY_SHADER);
  private int type = -1;

  ShaderEnum(int type)
  {
    this.type = type;
  }

  public int type()
  {
    return type;
  }
}
