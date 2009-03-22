package shaders;

import javax.media.opengl.GL;

/**
 *
 * @author angus
 */
public enum ShaderEnum
{
  VERTEX(GL.GL_VERTEX_SHADER),
  FRAGMENT(GL.GL_FRAGMENT_SHADER),
  GEOMETRY(GL.GL_GEOMETRY_SHADER_EXT);

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
