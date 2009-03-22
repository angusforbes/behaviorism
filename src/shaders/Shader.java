/* Shader.java ~ Mar 15, 2009 */
package shaders;

import behaviorism.BehaviorismDriver;
import com.sun.opengl.util.BufferUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 *
 * @author angus
 */
public class Shader
{
  int shaderId = -1;
  ShaderEnum shaderType = null;
  String filename = null;


  public Shader(String filename, ShaderEnum shaderType)
  {
    this.filename = filename;
    this.shaderType = shaderType;
  }

  /**
   * Defines some paramters that need to be defined BEFORE being linked to a program
   * @param gl
   * @param programId
   */
  public void define(GL gl, int programId)
  {
    //currently overwritten by the GeometryShader
  }

  public int install(GL gl)
  {
    return install(gl, shaderType, filename);
  }

  private int install(GL gl, ShaderEnum shaderType, String filename)
  {
    try
    {
      loadAndCompileShader(gl, shaderType.type(), new File(filename));
    }
    catch (Exception e)
    {
      System.out.println("couldn't load or compile a file named <" + filename + "> !");
    }

    return shaderId;
  }


  //adapted wholesale from cylab on jogl forums
  /**
   * Loads and compiles this Shader. If successful, this Shader's shaderId will be set to
   * a value > 0.
   * @param gl The current GL context.
   * @param type A number indicating whether it's a VERTEX or FRAGMENT shader
   * @param file The file containing this Shader's code.
   * @throws java.io.IOException
   */
  public void loadAndCompileShader(GL gl, int type, File file) throws IOException
  {
    BufferedReader reader = null;

    try
    {
      reader = new BufferedReader(new FileReader(file));
      ArrayList lineList = new ArrayList(100);

      for (String line = reader.readLine(); line != null; line = reader.readLine())
      {
        lineList.add(line + "\n");
      }

      String[] lines = (String[]) lineList.toArray(new String[lineList.size()]);

      int[] lengths = new int[lines.length];
      for (int i = 0; i < lines.length; i++)
      {
        lengths[i] = lines[i].length();
      }

      shaderId = gl.glCreateShader(type);
      gl.glShaderSource(shaderId, lines.length, lines, lengths, 0);
      gl.glCompileShader(shaderId);

      // Check for compile errors
      checkCompileError(gl);

      /*

      String errors = null;
      if ((errors = getGLErrorLog(gl, shaderId)) != null)
      {
        shaderId = -1;
        throw new RuntimeException("Compile error\n" + errors);
      }

      //String error = getGLErrorLog(gl, shader);
      String error = "some error...";

      int[] compileStatus =
      {
        0
      };

      //gl.glGetObjectParameterivARB(shader, GL.GL_OBJECT_COMPILE_STATUS_ARB, compileStatus, 0);
      gl.glGetShaderiv(shaderId, GL.GL_COMPILE_STATUS, compileStatus, 0);

      if (compileStatus[0] == 0)
      {
        shaderId = -1;
        throw new IllegalArgumentException("Shader could not be compiled! " + (error == null ? "" : error));
      }

      //return shaderId;
       */
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (Exception ignoreSunsInsanity)
        {
          //do nothing
        }
      }
    }
  }

  private void checkCompileError(GL gl)
  {
        IntBuffer status = BufferUtil.newIntBuffer(1);
        gl.glGetShaderiv(shaderId, GL.GL_COMPILE_STATUS, status);

        if (status.get() == GL.GL_FALSE) {
            getInfoLog(gl);
        } else {
            System.out.println("Successfully compiled shader " + shaderId);
        }
    }

    private void getInfoLog(GL gl)
    {
        IntBuffer infoLogLength = BufferUtil.newIntBuffer(1);
        gl.glGetShaderiv(shaderId, GL.GL_INFO_LOG_LENGTH, infoLogLength);

        ByteBuffer infoLog = BufferUtil.newByteBuffer(infoLogLength.get(0));
        gl.glGetShaderInfoLog(shaderId, infoLogLength.get(0), null, infoLog);

        String infoLogString =
                Charset.forName("US-ASCII").decode(infoLog).toString();
        throw new Error("Shader compile error\n" + infoLogString);
    }

    /*
  // Checks for arbitrary GL errors. Could also be accomplished by enabling the DebugGL pipeline
  private String getGLError(GL gl)
  {
    boolean hasError = false;
    String message = "";
    for (int glErr = gl.glGetError(); glErr != GL.GL_NO_ERROR; glErr = gl.glGetError())
    {
      message += (hasError ? "\n" : "") + BehaviorismDriver.renderer.glu.gluErrorString(glErr);
      hasError = true;
    }
    return hasError ? message : null;
  }

  // Checks the info log for compile/link errors
  private String getGLErrorLog(GL gl, int obj)
  {
    boolean hasError = false;
    int[] infologLength =
    {
      0
    };
    int[] charsWritten =
    {
      0
    };
    byte[] infoLog;

    String message = "";
    String error = getGLError(gl);
    if (error != null)
    {
      message += error;
      hasError = true;
    }

    //gl.glGetShaderiv(obj, GL.GL_INFO_LOG_LENGTH, infologLength, 0);
    gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, infologLength, 0);
    error = getGLError(gl);
    if (error != null)
    {
      message += (hasError ? "\n" : "") + error;
      hasError = true;
    }

    if (infologLength[0] > 1)
    {
      infoLog = new byte[infologLength[0]];
      gl.glGetInfoLogARB(obj, infologLength[0], charsWritten, 0, infoLog, 0);
      //gl.glGetShaderInfoLog(obj, infologLength[0], charsWritten, 0, infoLog, 0);
      message += (hasError ? "\n" : "") + "InfoLog:\n" + new String(infoLog);
      hasError = true;
    }
    error = getGLError(gl);

    if (error != null)
    {
      message += (hasError ? "\n" : "") + error;
      hasError = true;
    }
    return hasError ? message : null;
  }
     */
}
