/* Program.java ~ Mar 15, 2009 */
package shaders;

import com.sun.opengl.util.BufferUtil;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;

/**
 * Program is a wrapper for a GLSL Shader Program. It contains a List of
 * all of the individual Shaders that are associated with the Program,
 * as well as methods to bind, link, and use the program within an openGL context.
 * It also contains a Map of all uniform variables associated with the Program
 * to simplify passing data into the shaders.
 *
 * To use a Program, you generally follow these steps:
 *
 * 1. Make a new Program object, passing in the associated Shader objects.
 * 2. Then, when an openGL context is active, install() the Program. This will
 *  a) create a openGL program,
 *  b) load and compile all associated Shader objects (if they are not yet installed),
 *  c) attach those shaders to the program,
 *  d) define any parameters needed for the shader (eg, a GeometryShader needs to have input and output types set),
 *  e) link the program to the openGL context, and
 *  f) store all available uniform variables in the uniformToIdMap
 * 3. When you want to use the Program, call the bind() method. When you want to
 *  revert to the normal openGL pipeline functionality, call unbind().
 *
 * Uniform variables can be passed to the shaders using the glUniform methods and the id of the uniform variable.
 * This id can be retrieved using the uniform() method. For example, from within
 * the openGL display method or a Geom's draw method you might set the program like so:
 *
 * if (program.programId <= 0)
 * {
 *  program.install(gl);
 * }
 * program.bind(gl);
 * gl.glUniform1f(program.uniform("MyFloat"), newval);
 * gl.glBegin(GL.GL_POINTS);
 * gl.glVertex3f(0f, 0f, 0f);
 * gl.glEnd();
 * program.unbind(gl);
 *
 * @author angus
 */
public class Program
{

  public int programId;
  public List<Shader> shaders = new ArrayList<Shader>();
  private Map<String, Integer> uniformToIdMap = new HashMap<String, Integer>();
  private Map<String, Integer> attributeToIdMap = new HashMap<String, Integer>();

  /**
   * Construct a new Program out of some number Shaders.
   * The Program will not actually be installed until "install()" is called.
   * @param shaders The shaders to be associated with this Program.
   */
  public Program(Shader... shaders)
  {
    for (Shader shader : shaders)
    {
      this.shaders.add(shader);
    }
  }

  /**
   * Creates and installs this Program and its associated Shaders. Loads and compiles the associated shaders
   * (if they are not already installed. Links the associated shaders.
   * Creates a map of all available uniform variables
   * to their variable ids.
   * @param gl
   */
  public void install(GL gl)
  {
    //System.out.println("in super install..");
    //System.out.println("there are " + shaders.size() + " shaders available... ");

    //create program
    programId = gl.glCreateProgram();

    //load and compile each shader (if have not already done so)
    for (Shader shader : shaders)
    {
      if (shader.shaderId <= 0) //then we need to load & compile
      {
        shader.install(gl);
        System.out.println("loaded a shader with id = " + shader.shaderId + " and type = " + shader.shaderType.type());
      }

      //attach shader to this program
      attach(gl, shader);

      shader.define(gl, programId);

    }

    //link this program
    link(gl);

    System.out.println("ABOUT TO CHECK LINK");
    checkLinkAndValidationErrors(gl);
    System.out.println("DONE CHECK LINK");

    System.out.println("programId = " + programId);
    //map all available uniform variables
    bind(gl);
    mapUniforms(gl);
    mapAttributes(gl);
    unbind(gl);
  }

  /**
   * Retrieves the id value associated with this uniform variable name.
   * @param varName
   * @return the id of the variable.
   */
  public int uniform(String varName)
  {
    return uniformToIdMap.get(varName);
  }

  /**
   * Retrieves the id value associated with this vertex attribute variable name.
   * @param varName
   * @return
   */
  public int attribute(String varName)
  {
    return attributeToIdMap.get(varName);
  }

  /**
   * This method maps the names of uniform variables in the program's shaders
   * to the id used to retrieve them. Once they are mapped, they can be retrieved via
   * the "uniform("nameOfShaderUniform") method. The program must be bound (via the bind() method)
   * before this method is called.
   *
   * Apparently the uniform variable needs to be defined AND *used* inside at least one
   * shader for it to be seen.
   * @param gl
   */
  public void mapUniforms(GL gl)
  {
    int[] length = new int[1];
    int[] size = new int[1];
    int[] type = new int[1];
    byte[] name = new byte[256];
    int[] count = new int[1];

    gl.glGetProgramiv(programId, GL.GL_ACTIVE_UNIFORMS, count, 0);
    for (int i = 0; i < count[0]; i++)
    {
      gl.glGetActiveUniform(programId, i, 100,
        length, 0,
        size, 0,
        type, 0,
        name, 0);

      String varName = (new String(name)).trim();

      System.out.println("uniform : " + varName);
      uniformToIdMap.put(varName, gl.glGetUniformLocation(programId, varName));
    }
  }

  /**
   * This method maps the names of vertex attribute variables in the program's VERTEX shaders
   * to the id used to retrieve them. Once they are mapped, they can be retrieved via
   * the "attribute("nameOfShaderUniform") method. The program must be bound (via the bind() method)
   * before this method is called.
   *
   * Apparently the attribute variable needs to be defined AND *used* inside at least one
   * vertex shader for it to be seen.
   *
   * @param gl
   */
  public void mapAttributes(GL gl)
  {
    int[] length = new int[1];
    int[] size = new int[1];
    int[] type = new int[1];
    byte[] name = new byte[256];
    int[] count = new int[1];

    gl.glGetProgramiv(programId, GL.GL_ACTIVE_ATTRIBUTES, count, 0);
    for (int i = 0; i < count[0]; i++)
    {
      gl.glGetActiveAttrib(programId, i, 100,
        length, 0,
        size, 0,
        type, 0,
        name, 0);

      String varName = (new String(name)).trim();

      System.out.println("attribute : " + varName);
      attributeToIdMap.put(varName, gl.glGetAttribLocation(programId, varName));
    }
  }

  /**
   * Attaches a Shader object to this Program object.
   * @param gl
   * @param shader
   */
  public void attach(GL gl, Shader shader)
  {
    gl.glAttachShader(programId, shader.shaderId);
  }

  /**
   * Links this Program to the GL context.
   * @param gl
   */
  public void link(GL gl)
  {
    gl.glLinkProgram(programId);
  }

  /**
   * Binds this Program so that we use its shaders rather than
   * the regular openGL pipeline. Also, the program must be bound
   * before we set any of the uniform variables for its attached shaders.
   * @param gl
   */
  public void bind(GL gl)
  {
    gl.glUseProgram(programId);
  }

  /**
   * Unbinds all Programs so that we use the regular openGL pipeline.
   * @param gl
   */
  public void unbind(GL gl)
  {
    gl.glUseProgram(0);
  }

  private void checkLinkAndValidationErrors(GL gl)
  {
		IntBuffer status = BufferUtil.newIntBuffer(1);
		gl.glGetProgramiv(programId, GL.GL_LINK_STATUS, status);

		if (status.get() == GL.GL_FALSE) {
			getInfoLog(gl);
		} else {
			status.rewind();
			gl.glValidateProgram(programId);
			gl.glGetProgramiv(programId, GL.GL_VALIDATE_STATUS, status);
			if (status.get() == GL.GL_FALSE) {
				getInfoLog(gl);
			} else {
				System.out.println("Successfully linked program " + programId);
			}
		}
	}


    private void getInfoLog(GL gl)
    {
        IntBuffer infoLogLength = BufferUtil.newIntBuffer(1);
        gl.glGetProgramiv(programId, GL.GL_INFO_LOG_LENGTH, infoLogLength);

        ByteBuffer infoLog = BufferUtil.newByteBuffer(infoLogLength.get(0));
        gl.glGetProgramInfoLog(programId, infoLogLength.get(0), null, infoLog);

        String infoLogString =
                Charset.forName("US-ASCII").decode(infoLog).toString();
        throw new Error("Shader compile error\n" + infoLogString);
    }

}
