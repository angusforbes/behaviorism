/* GaussianBlurProgram.java ~ Jun 12, 2009 */
package shaders.imaging;

import javax.media.opengl.GL;
import org.apache.commons.math.util.MathUtils;
import shaders.FragmentShader;
import shaders.VertexShader;

/**
 *
 * @author angus
 */
public class SobelEdgeDetectProgram extends ConvolutionProgram
{

  //placeholder for created 2D kernel
  float[][] sobelKernelH;
  float[][] sobelKernelV;

  //unrolled 1D kernels that are sent to shader
  float[] kernelValueH;
  float[] kernelValueV;

  public SobelEdgeDetectProgram()
  {
    super(
      new VertexShader("shaders/effects/sobel.vert"),
      new FragmentShader("shaders/effects/sobel.frag"));
  }

  public SobelEdgeDetectProgram(int kernelW, int kernelH, int pixelW, int pixelH)
  {
    super(kernelW, kernelH, pixelW, pixelH,
      new VertexShader("shaders/effects/sobel.vert"),
      new FragmentShader("shaders/effects/sobel.frag"));
  }

  public void update()
  {
    createSobelKernels(kernelW, kernelH); //this also handles the unrolling
    createOffsets(kernelW, kernelH, pixelW, pixelH);
  }

  public void createSobelKernels(int width, int height)
  {
    int w = width * 2 + 1;
    int h = height * 2 + 1;
    sobelKernelH = new float[w][h];
    sobelKernelV = new float[w][h];

    float gX, gY;
    for (int i = -width; i <= width; i++)
    {
      for (int j = -height; j <= height; j++)
      {
        gX = smooth(j + height, h) * diff(i + width, w);
        gY = smooth(i + width, w) * diff(j + height, h);

        sobelKernelH[i + width][j + height] = gX;
        sobelKernelV[i + width][j + height] = gY;

      //System.out.println("i/j = " + i + "/" + j + ", gX = " + gX);
      //System.out.println("i/j = " + i + "/" + j + ", gY = " + gY);
      }
    }

    kernelValueH = unrollKernel(sobelKernelH);
    kernelValueV = unrollKernel(sobelKernelV);

  }

  public int smooth(int x, int val)
  {
    return (int) (MathUtils.factorial(val - 1) / (MathUtils.factorial(val - 1 - x) * MathUtils.factorial(x)));
  }

  public int pascal(int k, int n)
  {
    if (k >= 0 && k <= n)
    {
      return (int) (MathUtils.factorial(n) / (MathUtils.factorial(n - k) * MathUtils.factorial(k)));
    }
    return 0;
  }

  public int diff(int x, int val)
  {
    return pascal(x, val - 2) - pascal(x - 1, val - 2);
  }

  @Override
  public void uniforms(GL gl)
  {
    //System.out.println("A : kernelSize = " + kernelSize);
    gl.glUniform4f(uniform("ScaleFactor"), scaleFactor, scaleFactor, scaleFactor, scaleFactor);
    gl.glUniform1i(uniform("KernelSize"), kernelSize);
    //System.out.println("B");
    gl.glUniform4fv(uniform("KernelValueH[0]"), kernelValueH.length, kernelValueH, 0);
    gl.glUniform4fv(uniform("KernelValueV[0]"), kernelValueV.length, kernelValueV, 0);
    //System.out.println("C");
    gl.glUniform2fv(uniform("Offset[0]"), offsets.length, offsets, 0);
  }
}
