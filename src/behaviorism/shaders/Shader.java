/* Shader.java ~ Mar 15, 2009 */
package behaviorism.shaders;

import com.sun.opengl.util.BufferUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.jar.JarFile;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static behaviorism.utils.RenderUtils.*;


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
  public void define(int programId)
  {
    //currently only needs to be overwritten by the GeometryShader
  }

  public int install()
  {
    System.out.println("FILENAME = " + filename);
    return install(shaderType, filename);
  }

  private int install(ShaderEnum shaderType, String filename)
  {
    boolean success = false;

    System.out.println("trying to read shader form JAR...");
    try
    {
      success = loadAndCompileShaderFromResourceJar(shaderType.type(), filename);
    }
    catch (Exception e)
    {
      System.out.println("couldn't load or compile a file named <" + filename + "> from resource JAR!");
    }

    if (success == false)
    {
      System.out.println("trying to read shader form File...");
      try
      {
        success = loadAndCompileShaderFromFile(shaderType.type(), new File(filename));
      }
      catch (Exception e)
      {
        System.out.println("couldn't load or compile a file named <" + filename + "> from file!");
      }
    }

    return shaderId;
  }

  public boolean loadAndCompileShaderFromResourceJar(int type, String filename) throws IOException
  {
    GL2 gl = getGL();
    System.err.println("IN loadAndCompileShader...");
    BufferedReader reader = null;
 //   JarFile jar = new JarFile("dist/lib/behaviorism.jar");

//    Enumeration entries = jar.entries();
//    for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();)
//       System.err.println(e.nextElement());

    InputStream is = getClass().getResourceAsStream("/" + filename);
    try
    {
      reader = new BufferedReader(new InputStreamReader(is));
      ArrayList<String> lineList = new ArrayList<String>(100);

      for (String line = reader.readLine(); line != null; line = reader.readLine())
      {
        lineList.add(line + "\n");
      }

      String[] lines = lineList.toArray(new String[lineList.size()]);

      int[] lengths = new int[lines.length];
      for (int i = 0; i < lines.length; i++)
      {
        lengths[i] = lines[i].length();
      }

      shaderId = gl.glCreateShader(type);
      gl.glShaderSource(shaderId, lines.length, lines, lengths, 0);
      gl.glCompileShader(shaderId);

      // Check for compile errors
      checkCompileError();
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

    return true;
  }


  public boolean loadAndCompileShaderFromResourceJar_OLD_REAL(int type, String filename) throws IOException
  {
    GL2 gl = getGL();
    System.err.println("IN loadAndCompileShader...");
    BufferedReader reader = null;
    JarFile jar = new JarFile("dist/lib/behaviorism.jar");

//    Enumeration entries = jar.entries();
//    for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();)
//       System.err.println(e.nextElement());

    InputStream is = jar.getInputStream(jar.getEntry("resources/" + filename));
    try
    {
      reader = new BufferedReader(new InputStreamReader(is));
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
      checkCompileError();
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

    return true;
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
  public boolean loadAndCompileShaderFromFile(int type, File file) throws IOException
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

      GL2 gl = getGL();

      shaderId = gl.glCreateShader(type);
      gl.glShaderSource(shaderId, lines.length, lines, lengths, 0);
      gl.glCompileShader(shaderId);

      // Check for compile errors
      checkCompileError();

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

    return true;
  }

  private void checkCompileError()
  {
    IntBuffer status = BufferUtil.newIntBuffer(1);
    getGL().glGetShaderiv(shaderId, GL_COMPILE_STATUS, status);

    if (status.get() == GL_FALSE)
    {
      getInfoLog();
    }
    else
    {
      System.out.println("Successfully compiled shader " + shaderId);
    }
  }

  private void getInfoLog()
  {
    IntBuffer infoLogLength = BufferUtil.newIntBuffer(1);
    getGL().glGetShaderiv(shaderId, GL_INFO_LOG_LENGTH, infoLogLength);

    ByteBuffer infoLog = BufferUtil.newByteBuffer(infoLogLength.get(0));
    getGL().glGetShaderInfoLog(shaderId, infoLogLength.get(0), null, infoLog);

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
