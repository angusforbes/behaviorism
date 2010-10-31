/* GeometryShader.java ~ Mar 16, 2009 */
package behaviorism. shaders;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static behaviorism.utils.RenderUtils.*;

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

  public int inputType = GL_TRIANGLES;
  public int outputType = GL_TRIANGLE_STRIP;
  
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
  public void define(int programId)
  {
    GL2 gl = getGL();
    System.out.println("programId = " + programId);
    gl.glProgramParameteri(programId, GL_GEOMETRY_INPUT_TYPE, inputType);
    //gl.glProgramParameteriEXT(programId, gl.GL_GEOMETRY_OUTPUT_TYPE_EXT, gl.GL_LINE_STRIP);
    gl.glProgramParameteri(programId, GL_GEOMETRY_OUTPUT_TYPE, outputType);

    int[] maxVerts = new int[1];
    gl.glGetIntegerv(GL_MAX_GEOMETRY_OUTPUT_VERTICES, maxVerts, 0);
    gl.glProgramParameteri(programId, GL_GEOMETRY_VERTICES_OUT, Math.min(maxVerts[0], numOutVertices));
    System.out.println("GL_MAX_GEOMETRY_OUTPUT_VERTICES_EXT  count = " + maxVerts[0]);
  }
}
