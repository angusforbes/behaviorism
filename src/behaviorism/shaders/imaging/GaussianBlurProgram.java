/* GaussianBlurProgram.java ~ Jun 12, 2009 */
package behaviorism.shaders.imaging;

import java.util.Arrays;

/**
 *
 * @author angus
 */
public class GaussianBlurProgram extends ConvolutionProgram
{
  public GaussianBlurProgram()
  {
  }

  public void update()
  {
    if (kernelW == 0)
    {
      unrollKernel(createBlurKernel1D(kernelH, false));
    }
    else if (kernelH == 0)
    {
      unrollKernel(createBlurKernel1D(kernelW, true));
    }
    else
    {
      unrollKernel(createBlurKernel2D(kernelW, kernelH));
    }

    createOffsets(kernelW, kernelH, pixelW, pixelH);
  }

  public float[][] createBlurKernel2D(int width, int height)
  {
    float[][] matrix = new float[width * 2 + 1][height * 2 + 1];
    float sigmaW = width / 3f;
    float sigmaH = height / 3f;
    float normW = (float) (1.0 / (Math.sqrt(2 * Math.PI) * sigmaW));
    float normH = (float) (1.0 / (Math.sqrt(2 * Math.PI) * sigmaH));
    float coeffW = 2 * sigmaW * sigmaW;
    float coeffH = 2 * sigmaH * sigmaH;

    float gX, gY, g;
    float total = 0;
    for (int i = -width; i <= width; i++)
    {
      for (int j = -height; j <= height; j++)
      {
        gX = (float) (normW * Math.exp(-i * i / coeffW));
        gY = (float) (normH * Math.exp(-j * j / coeffH));
        g = gX * gY;
        //System.out.println("gX = " + gX);
        //System.out.println("gY = " + gY);
        //System.out.println("g = " + g);
        matrix[i + width][j + height] = g;
        total += g;
      }
    }

    float sum = 0f;
    for (int i = 0; i <= 2 * width; i++)
    {
      for (int j = 0; j <= 2 * height; j++)
      {
        matrix[i][j] /= total;
        sum += matrix[i][j];
      }
    }

//    System.out.println("total = " + total);
//    System.out.println("matrix = " + Arrays.toString(matrix));
//    System.out.println("sum = " + sum);

    return matrix;
  }

  public float[][] createBlurKernel1D(int width, boolean H)
  {
    //System.out.println("in createBlurKernel1D...");
    float[] matrix = new float[width * 2 + 1];
    float sigma = width / 3f;
    float norm = (float) (1.0 / (Math.sqrt(2 * Math.PI) * sigma));
    float coeff = 2 * sigma * sigma;
    float total = 0;
    for (int i = -width; i <= width; i++)
    {
      float g = (float) (norm * Math.exp(-i * i / coeff));
      matrix[i + width] = g;
      total += g;
    }

    float sum = 0f;
    for (int i = 0; i <= 2 * width; i++)
    {
      matrix[i] /= total;
      sum += matrix[i];
    }
//    System.out.println("total = " + total);
//    System.out.println("matrix = " + Arrays.toString(matrix));
//    System.out.println("sum = " + sum);

    if (H == true)
    {
      float[][] k = new float[][]
      {
        matrix
      };
      return k;
    }
    else
    {
      float[][] k = new float[matrix.length][1];

      for (int i = 0; i < matrix.length; i++)
      {
        k[i][0] = matrix[i];
      }

      return k;
    }
  }
}
