/* ConvolutionProgram.java ~ Jun 12, 2009 */
package behaviorism. shaders.imaging;

import behaviorism.shaders.FragmentShader;
import behaviorism.shaders.Program;
import behaviorism.shaders.Shader;
import behaviorism.shaders.VertexShader;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import behaviorism.utils.RenderUtils;
import static behaviorism.utils.RenderUtils.*;

/**
 *
 * @author angus
 */

// We can create the kernel once we know the the kernelW and kernelH
//so this can be created in constructors...

// We can create the offsets once we know the pixelW and pixelH (and kW and kH)
//may have to wait for texture size to be determined (ie, in the case of video
//the dimensions are not known until the first frame starts playing...

//need away to update these dynamically...

abstract public class ConvolutionProgram extends Program
{

  float[][] kernel = null;
  int pixelW = -1;
  int pixelH = -1;
  int kernelW = -1;
  int kernelH = -1;
  float scaleFactor = 1f; //default
  int kernelSize = -1; //should = (kernelW * 2 + 1) * (kernelH * 2 + 1)
  public float[] offsets = null;
  public float[] kernelValue = null;

  /**
   * Constructs a ConvolutionProgram that will need to be initialized manually.
   */
  public ConvolutionProgram()
  {
    super(
      new VertexShader("shaders/effects/convolve.vert"),
      new FragmentShader("shaders/effects/convolve.frag"));
  }

  /**
   * Constructs a ConvolutionProgram with custom shaders that will need to be initialized manually.
   * @param shaders
   */
  public ConvolutionProgram(Shader ... shaders)
  {
    super(shaders);
  }

  /**
   * Constructs a ConvolutionProgram with an specified kernel size and texture size.
   * @param kernelW
   * @param kernelH
   * @param pixelW
   * @param pixelH
   */
  public ConvolutionProgram(int kernelW, int kernelH, int pixelW, int pixelH)
  {
    super(
      new VertexShader("shaders/effects/convolve.vert"),
      new FragmentShader("shaders/effects/convolve.frag"));

    initialize(kernelW, kernelH, pixelW, pixelH);
  }

  /**
   * Constructs a ConvolutionProgram using custom shaders with an specified kernel size and texture size.
   * @param kernelW
   * @param kernelH
   * @param pixelW
   * @param pixelH
   * @param shaders
   */
  public ConvolutionProgram(int kernelW, int kernelH, int pixelW, int pixelH, Shader ... shaders)
  {
    super(shaders);

    initialize(kernelW, kernelH, pixelW, pixelH);
  }


  public void setPixelSize(int pw, int ph)
  {
    if (this.pixelW != pw || this.pixelH != ph)
    {
      this.pixelW = pw;
      this.pixelH = ph;
      if (canUpdate())
      {
        update();
      }
    }
  }

  public void setKernelSize(int w, int h)
  {
    System.out.println("w/h/kw/kh = " + w + "/" + h + "/" + this.kernelW + "/" + this.kernelH );
    if (this.kernelW != w || this.kernelH != h)
    {
      this.kernelSize = (2 * w + 1) * (2 * h + 1);
      this.kernelW = w;
      this.kernelH = h;
      if (canUpdate())
      {
        update();
      }
    }
    else
    {
      System.out.println("no need to update...");
    }
  }

  public void setScaleFactor(float sf)
  {
    if (this.scaleFactor != sf)
    {
      this.scaleFactor = sf;

//      if (canUpdate())
//      {
//        update();
//      }
    }
    else
    {
      System.out.println("no need to update...");
    }
  }

  public boolean canUpdate()
  {
    if (kernelW <= 0 && kernelH <= 0)
    {
      System.out.println("either width or height must be greater than 0");
      return false;
    }
    if (pixelW <= 0 || pixelH <= 0)
    {
      System.out.println("pixel width & height must be greater than 0");
      return false;
    }
    return true;
  }

  public void initialize(int w, int h, int pw, int ph)
  {
    System.out.println("initializing : w/h/pw/ph = " + w + "/" + h + "/" + pw + "/" + ph);
    if (w == this.kernelW && h == this.kernelW && pw == this.pixelW && ph == this.pixelH)
    {
      return; //all data is current
    }
    this.pixelW = pw;
    this.pixelH = ph;
    this.kernelSize = (2 * w + 1) * (2 * h + 1);
    this.kernelW = w;
    this.kernelH = h;

    if (canUpdate())
    {
      update();
    }
  }

  abstract public void update();

  public void createOffsets(int kernelW, int kernelH, int pixelW, int pixelH)
  {
    //System.out.println("in createOffsets() ... ");
    int w = kernelW * 2 + 1;
    int h = kernelH * 2 + 1;
    this.offsets = new float[w * h * 2];

    float xinc = 1f / (float) (pixelW - 1);
    float yinc = 1f / (float) (pixelH - 1);
    int xhalf = kernelW;
    int yhalf = kernelH;
    //System.out.println("xinc / yinc = " + xinc + "/" +yinc);
    //System.out.println("xhalf / yhalf = " + xhalf + "/" +yhalf);
    int idx = 0;

    for (int x = 0; x < w; x++)
    {
      for (int y = 0; y < h; y++)
      {
//        System.out.println("x/y = " + x + "/" + y + ", idx = " + idx + ", " +
//          "val = " + ((x - xhalf) * xinc) + "/" + ((y - yhalf) * yinc));
        offsets[idx++] = (x - xhalf) * xinc;
        offsets[idx++] = (y - yhalf) * yinc;

      }
    }
    //System.out.println("offsets array = " + Arrays.toString(offsets));
  }

  /**
   * Unrolls a 2D kernel array into a 1D array that is usable by a GLSL fragment shader.
   * @param kernel A 2D convolution kernel
   * @param scaleFactor
   * @return the unrolled kernel
   */
  public float[] unrollKernel(float[][] kernel)
  {
    float[] kv = new float[(kernelW * 2 + 1) * (kernelH * 2 + 1) * 4];

    float val;
    int idx = 0;
    //for(int y=0; y<kernel.length; y++)
    for (int x = 0; x < kernel.length; x++)
    {
      for (int y = 0; y < kernel[x].length; y++)
      //for(int x=0; x<kernel[y].length; x++)
      {
        val = kernel[x][y];
        System.out.println("? x/y = " + x + "/" + y + ", idx = " + idx + ", val = " + val);
        kv[idx++] = val;
        kv[idx++] = val;
        kv[idx++] = val;
        kv[idx++] = val;

      //System.out.println("val = " + val);
      }
    }

    //System.out.println("kernelValue array = " + Arrays.toString(kernelValue));
    this.kernelValue = kv;
    return kv;
  }

  @Override
  public void uniforms()
  {
    GL2 gl = getGL();
    //System.out.println("A : kernelSize = " + kernelSize);
    gl.glUniform4f(uniform("ScaleFactor"), scaleFactor, scaleFactor, scaleFactor, scaleFactor);
    gl.glUniform1i(uniform("KernelSize"), kernelSize);
    //System.out.println("B");
    gl.glUniform4fv(uniform("KernelValue[0]"), kernelValue.length, kernelValue, 0);
    //System.out.println("C");
    gl.glUniform2fv(uniform("Offset[0]"), offsets.length, offsets, 0);
  }
}