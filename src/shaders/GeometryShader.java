/* GeometryShader.java ~ Mar 16, 2009 */
package shaders;

import javax.media.opengl.GL;

/**
 *
 * @author angus
 */
public class GeometryShader extends Shader
{

  //public int inputType = GL.GL_LINES;
  //public int outputType = GL.GL_LINE_STRIP;

  //public int inputType = GL.GL_POINTS;
  //public int outputType = GL.GL_POINTS;

  public int inputType = GL.GL_TRIANGLES;
  public int outputType = GL.GL_TRIANGLE_STRIP;
  
  public int numOutVertices = 1024;

  public GeometryShader(String filename)
  {
    super(filename, ShaderEnum.GEOMETRY);
  }

  /**
   * Defines some paramters that need to be defined BEFORE being linked to a program.
   * The GeometryShader MUST define input and output types and the number of out vertices.
   * @param gl
   * @param programId
   */
  @Override
  public void define(GL gl, int programId)
  {
    System.out.println("programId = " + programId);
    gl.glProgramParameteriEXT(programId, gl.GL_GEOMETRY_INPUT_TYPE_EXT, inputType);
    //gl.glProgramParameteriEXT(programId, gl.GL_GEOMETRY_OUTPUT_TYPE_EXT, gl.GL_LINE_STRIP);
    gl.glProgramParameteriEXT(programId, gl.GL_GEOMETRY_OUTPUT_TYPE_EXT, outputType);

    int[] maxVerts = new int[1];
    gl.glGetIntegerv(gl.GL_MAX_GEOMETRY_OUTPUT_VERTICES_EXT, maxVerts, 0);
    gl.glProgramParameteriEXT(programId, gl.GL_GEOMETRY_VERTICES_OUT_EXT, Math.min(maxVerts[0], numOutVertices));
    System.out.println("GL_MAX_GEOMETRY_OUTPUT_VERTICES_EXT  count = " + maxVerts[0]);
  }
}
