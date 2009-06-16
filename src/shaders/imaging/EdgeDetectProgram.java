/* GaussianBlurProgram.java ~ Jun 12, 2009 */
package shaders.imaging;

/**
 *
 * @author angus
 */
public class EdgeDetectProgram extends ConvolutionProgram
{

  float[][] blurFilter = new float[][]
  {
    /*
    {0f,  1f, 0f},
    {1f,  -4f,  1f},
    {0f,  1f, 0f}
     */

    //{1f,  -2f,  1f}

    /*
    {0f,  0f,  -1f, 0f, 0f},
    {0f,  -1f,  -2f, -1f, 0f},
    {-1f,  -2f, 16f, -2f, -1f},
    {0f,  -1f,  -2f, -1f, 0f},
    {0f,  0f,  -1f, 0f, 0f}
     */

    // {-1f,  -2f, 16f, -2f, -1f}

    /*
    {0f,  0f,  1f, 0f, 0f},
    {0f,  1f,  2f, 1f, 0f},
    {1f,  2f,  -16f, 2f, 1f},
    {0f,  1f,  2f, 1f, 0f},
    {0f,  0f,  1f, 0f, 0f}
     */
    {
      0f, 0f, 0f, 1f, 0f, 0f, 0f
    },
    {
      0f, 0f, 1f, 2f, 1f, 0f, 0f
    },
    {
      0f, 1f, 2f, 4f, 2f, 1f, 0f
    },
    {
      1f, 2f, 4f, -44f, 4f, 2f, 1f
    },
    {
      0f, 1f, 2f, 4f, 2f, 1f, 0f
    },
    {
      0f, 0f, 1f, 2f, 1f, 0f, 0f
    },
    {
      0f, 0f, 0f, 1f, 0f, 0f, 0f
    }
  };

  public EdgeDetectProgram()
  {
  }

  public EdgeDetectProgram(int kernelW, int kernelH, int pixelW, int pixelH)
  {
    super(kernelW, kernelH, pixelW, pixelH);
  }

  public void update()
    {
    unrollKernel(createEdgeDetectKernel(kernelW, kernelH));
    createOffsets(kernelW, kernelH, pixelW, pixelH);
  }

  public float[][] createEdgeDetectKernel(int width, int height)
  {
    //int divider = width + height;
    float[][] matrix = new float[width * 2 + 1][height * 2 + 1];

    int max = Math.max(width, height);
    int xdist, dist;
    float g;
    float total = 0;
    for (int i = -width; i <= width; i++)
    {
      xdist = Math.abs(i);
      for (int j = -height; j <= height; j++)
      {
        dist = xdist * Math.abs(j);
        //System.out.println("dist = " + dist + ", max = " + Math.max(width, height));

        if (dist > 0 && dist <= max)
        {
          g = (float) (Math.pow(2, max - (dist)));
          total += g;
          matrix[i + width][j + height] = g;
        }
      //System.out.println("i/j = " + i + "/" + j + ", dist = " + dist + ", g = " + g);
      }
    }

    matrix[0 + width][0 + height] = -total;

    System.out.println("total = " + total);
    return matrix;
  }

  /* -- good idea, but doesn't do what i wnat...
  //center is neg 1f. next rings around are pos 1/kernelW
  public float[][] createEdgeDetectKernel2(int width, int height)
  {
  int divider = width + height;
  float[][] matrix = new float[width * 2 + 1][height * 2 + 1];

  float vW = 0;
  float coeffW = 1f;
  for (int i = 0; i < width; i++)
  {
  vW += coeffW;
  coeffW /= 2f;
  }

  vW = 1/vW;

  float vH = 0;
  float coeffH = 1f;
  for (int i = 0; i < height; i++)
  {
  vH += coeffH;
  coeffH /= 2f;
  }

  vH = 1/vH;

  float gX, gY, g;
  float total = 0;
  for (int i = -width; i <= width; i++)
  {
  for (int j = -height; j <= height; j++)
  {
  gX = vW / (float) (Math.pow(2, Math.abs(i)));
  gY = vH / (float) (Math.pow(2, Math.abs(j)));

  //gX = (float) (normW * Math.exp(-i * i / coeffW));
  //gY = (float) (normH * Math.exp(-j * j / coeffH));
  g = (gX * gY) / divider;
  //System.out.println("gX = " + gX);
  //System.out.println("gY = " + gY);
  System.out.println("i/j = " + i + "/" + j + ", g = " + g);
  matrix[i + width][j + height] = g;
  total += g;
  }
  }

  matrix[0 + width][0 + height] = -1.25f; //(divider * 2);

  System.out.println("total = " + total);
  return matrix;
  }
   */
}
