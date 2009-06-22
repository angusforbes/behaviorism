/* MatrixUtils.java (created on August 30, 2007, 4:49 PM) */
package utils;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import geometry.Geom;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.media.opengl.GL;
import javax.vecmath.Point3d;
import javax.vecmath.SingularMatrixException;
import renderers.Renderer;

/** 
 * This class contains static utility methods having to do with matrix manipulations. 
 * Most of the methods transform a point from one coordinate system into another one.
 * That is, the same point, but expressed from the viewpoint of a different modelview.
 * None of the methods overwrite the point that is passed in. Rather they return a
 * new Point3f representing the transformation into the new coordinate system. 
 * <p>
 * This framework uses "absolute coordinates" to represent the identity modelview. 
 * "World coordinates" refer the base transformations (rotations and translations) of the 
 * specified World class (that is, the world class that contains the first list of geoms).
 * "Geom coordinates" refer to coordinates in terms of the modelview (rotations, translations,
 * and scaling) for a particular Geom.
 * <p>
 * Transformations between these views are necessary in various cases. For instance, for mouse picking 
 * and dragging. And also to draw lines from one Geom to a child or parent Geom that is represented
 * with a different modelview.
 */
public class MatrixUtils
{
  ////raw transformations

  private static Point3d getPointInAbsoluteCoordinates(Point3d pt, double[] modelview)
  {
    double[] temp = Arrays.copyOf(modelview, 16);
    Matrix4d matrix = new Matrix4d(temp);
    //Matrix4d matrix = new Matrix4d(modelview);
    Point3d absPt = new Point3d(pt);
    matrix.transpose();
    matrix.transform(absPt);
    return absPt;
  }

  private static Point3d getPointInAbsoluteCoordinates(Point3d pt, double[] modelview,
    boolean transpose)
  {
    double[] temp = Arrays.copyOf(modelview, 16);
    Matrix4d matrix = new Matrix4d(temp);
    //Matrix4d matrix = new Matrix4d(modelview);
    Point3d absPt = new Point3d(pt);
    if (transpose == true)
    {
      matrix.transpose();
    }
    matrix.transform(absPt);
    return absPt;
  }

  private static Point3d getAbsolutePointInCoordinates(Point3d absPt, double[] modelview)
  {
    double[] temp = Arrays.copyOf(modelview, 16);
    Matrix4d matrix = new Matrix4d(temp);

    //Matrix4d matrix = new Matrix4d(modelview);
    Point3d returnPt = new Point3d(absPt);
    //System.out.println("matrix = " + matrix);
    try
    {
      matrix.invert();
    }
    catch (SingularMatrixException sme)
    {
      //doesn't seem to be a problem...");
    }
    matrix.transpose();
    matrix.transform(returnPt);

    return returnPt;
  }

  private static Point3d getAbsolutePointInCoordinates(Point3d absPt, double[] modelview,
    boolean transpose)
  {
    double[] temp = Arrays.copyOf(modelview, 16);
    Matrix4d matrix = new Matrix4d(temp);

    //Matrix4d matrix = new Matrix4d(modelview);
    Point3d returnPt = new Point3d(absPt);
    matrix.invert();

    if (transpose == true)
    {
      matrix.transpose();
    }
    matrix.transform(returnPt);

    return returnPt;
  }


  ////low-level utility methods for transforming between the various (world, geom, absolute) coordinate systems
  public static Point3d getWorldPointInGeomCoordinates(Point3d worldPt, double[] worldModelview, double[] geomModelview)
  {
    Point3d absPt = getPointInAbsoluteCoordinates(worldPt, worldModelview);

    return getAbsolutePointInCoordinates(absPt, geomModelview);
  }

  public static Point3d getWorldPointInGeomCoordinates(Point3d worldPt, double[] worldModelview, double[] geomModelview,
    boolean transpose)
  {
    Point3d absPt = getPointInAbsoluteCoordinates(worldPt, worldModelview, true);

    return getAbsolutePointInCoordinates(absPt, geomModelview, true);
  }

  public static Point3d getGeomPointInAbsoluteCoordinates(Point3d geomPt, double[] geomModelview)
  {
    return getPointInAbsoluteCoordinates(geomPt, geomModelview);
  }

  public static Point3d getWorldPointInAbsoluteCoordinates(Point3d worldPt, double[] worldModelview)
  {
    return getPointInAbsoluteCoordinates(worldPt, worldModelview);
  }

  /**
   * input a point in one coordinate system, and outputs that point in terms of another coordinate
   * system.
   */
  public static Point3d getGeomPointInGeomCoordinates(Point3d origPt, double[] origModelview,
    double[] destModelview)
  {
    Point3d absPt = getPointInAbsoluteCoordinates(origPt, origModelview);

    return getAbsolutePointInCoordinates(absPt, destModelview);
  }

  public static Point3d getGeomPointInWorldCoordinates(Point3d geomPt, double[] geomModelview, double[] worldModelview)
  {
    Point3d absPt = getPointInAbsoluteCoordinates(geomPt, geomModelview);
    return getAbsolutePointInCoordinates(absPt, worldModelview);
  }

  public static Point3d getAbsolutePointInWorldCoordinates(Point3d absPt, double[] worldModelview)
  {
    return getAbsolutePointInCoordinates(absPt, worldModelview);
  }

  public static Point3d getAbsolutePointInGeomCoordinates(Point3d absPt, double[] geomModelview)
  {
    return getAbsolutePointInCoordinates(absPt, geomModelview);
  }


  ////higher level utility methods (take in a Geom, or have more logic, etc)
  /**
   * Returns the translate point of the inputGeom in terms of the destGeom's coordinate system
   */
  public static Point3d getGeomPointInGeomCoordinates(Geom inputGeom, Geom destGeom)
  {
    return getGeomPointInGeomCoordinates(new Point3d(inputGeom.translate), inputGeom.modelview, destGeom.modelview);
  }

  public static void debugPointRelativeToGeom(Point3d absPt, Geom g)
  {
    Point3d worldPt = getAbsolutePointInWorldCoordinates(absPt, RenderUtils.getCamera().modelview);
    Point3d geomPt = getAbsolutePointInGeomCoordinates(absPt, g.modelview);

    System.out.println("absPt   = " + absPt);
    System.out.println("worldPt = " + worldPt);
    System.out.println("geomPt  = " + geomPt);

    if (g.parent != null)
    {
      Point3d parentPt = getAbsolutePointInGeomCoordinates(absPt, g.parent.modelview);
      System.out.println("parentPt= " + parentPt);
    }
  }

  public static void debugGeom(Geom g)
  {
    Point3d zeroPt = new Point3d(g.translate); //new Point3f(0f, 0f, 0f);
    Point3d absPt = getGeomPointInAbsoluteCoordinates(zeroPt, g.modelview);
    Point3d worldPt = getGeomPointInWorldCoordinates(zeroPt, g.modelview, RenderUtils.getCamera().modelview);
    Point3d geomPt = getGeomPointInGeomCoordinates(zeroPt, g.modelview, g.modelview);


    System.out.println("orig pt = " + g.translate);
    System.out.println(" abs pt = " + absPt);
    System.out.println("wrld pt = " + worldPt);
    System.out.println("geom pt = " + geomPt);

    if (g.parent != null)
    {
      Point3d parentGeomPt = getGeomPointInGeomCoordinates(zeroPt, g.modelview, g.parent.modelview);

      System.out.println("prnt pt = " + parentGeomPt);

    }
  }

  public static void printVector(double[] dv)
  {

    System.out.println("*double vector:*");
    for (int i = 0; i < dv.length; i++)
    {
      System.out.printf("%+f ", dv[i]);
    }
    System.out.println("");

  }

  public static double[] getTranslationColumnFromMatrix(double[] da)
  {
    return getColumnFromMatrix(da, 3);
  }

  public static double[] getColumnFromMatrix(double[] da, int colNum)
  {
    int idx = colNum * 4;
    return new double[]
      {
        da[idx], da[idx + 1], da[idx + 2], da[idx + 3]
      };
  }

  /**
   * Prints out the 4x4 matrix in column order.
   * @param da The
   */
  public static void printMatrix(double[] da)
  {
    System.out.printf("%+f %+f %+f %+f\n", da[0], da[4], da[8], da[12]);
    System.out.printf("%+f %+f %+f %+f\n", da[1], da[5], da[9], da[13]);
    System.out.printf("%+f %+f %+f %+f\n", da[2], da[6], da[10], da[14]);
    System.out.printf("%+f %+f %+f %+f\n", da[3], da[7], da[11], da[15]);

  //in row order
    /*
  System.out.printf("%f %f %f %f\n", da[0], da[1], da[2], da[3]);
  System.out.printf("%f %f %f %f\n", da[4], da[5], da[6], da[7]);
  System.out.printf("%f %f %f %f\n", da[8], da[9], da[10], da[11]);
  System.out.printf("%f %f %f %f\n", da[12], da[13], da[14], da[15]);
   */
  }

  public static double[] getDoubleArrayFromMatrix4d(Matrix4d m4d)
  {
    double[] da = new double[16];
    da[0] = m4d.m00;
    da[1] = m4d.m01;
    da[2] = m4d.m02;
    da[3] = m4d.m03;
    da[4] = m4d.m10;
    da[5] = m4d.m11;
    da[6] = m4d.m12;
    da[7] = m4d.m13;
    da[8] = m4d.m20;
    da[9] = m4d.m21;
    da[10] = m4d.m22;
    da[11] = m4d.m23;
    da[12] = m4d.m30;
    da[13] = m4d.m31;
    da[14] = m4d.m32;
    da[15] = m4d.m33;
    /*
    da[0] = m4d.getM00();
    da[1] = m4d.getM01();
    da[2] = m4d.getM02();
    da[3] = m4d.getM03();
    da[4] = m4d.getM10();
    da[5] = m4d.getM11();
    da[6] = m4d.getM12();
    da[7] = m4d.getM13();
    da[8] = m4d.getM20();
    da[9] = m4d.getM21();
    da[10] = m4d.getM22();
    da[11] = m4d.getM23();
    da[12] = m4d.getM30();
    da[13] = m4d.getM31();
    da[14] = m4d.getM32();
    da[15] = m4d.getM33();
     */
    return da;
  }

  //this works, but the rayIntersect method is kind fo confusing... maybe clean it up sometime?
  public static Point3f pixelToWorld(int x, int y)
  {
    return MatrixUtils.toPoint3f(
        RenderUtils.rayIntersect(
          RenderUtils.getWorld(), x, y)
      );
  }
  public static double[] getIdentity()
  {
    Matrix4d m4d = new Matrix4d();
    m4d.setIdentity();
    return getDoubleArrayFromMatrix4d(m4d);
  }

  public static Point3d toPoint3d(Point3f p3f)
  {
    return new Point3d(p3f.x, p3f.y, p3f.z);
  }

  public static Point3f toPoint3f(Point3d p3d)
  {
    return new Point3f((float) p3d.x, (float) p3d.y, (float) p3d.z);
  }

  public static float[] toArray(Point3f p3f)
  {
    return new float[]
      {
        p3f.x, p3f.y, p3f.z
      };
  }

  public static double[] toArray(Point3d p3d)
  {
    return new double[]
      {
        p3d.x, p3d.y, p3d.z
      };
  }
  //public static DecimalFormat pointFormatter = new DecimalFormat("#.###");
  public static DecimalFormat pointFormatter = new DecimalFormat("#.########");

  public static String toString(double[] da)
  {
    return "double[]: " + da[0] + " " + da[1] + " " + da[2] + " " + da[3] + "\n" +
      da[4] + " " + da[5] + " " + da[6] + " " + da[7] + "\n" +
      da[8] + " " + da[9] + " " + da[10] + " " + da[11] + "\n" +
      da[12] + " " + da[13] + " " + da[14] + " " + da[15];
  }

  public static String toString(Point3d p3d)
  {
    return "Point3d: " + pointFormatter.format(p3d.x) + ", " + pointFormatter.format(p3d.y) + ", " +
      pointFormatter.format(p3d.z);
  }

  public static String toString(Point3f p3f)
  {
    return "Point3f: " + pointFormatter.format(p3f.x) + ", " + pointFormatter.format(p3f.y) + ", " +
      pointFormatter.format(p3f.z);
  }

  public static double[] toArray(Matrix4d matrix)
  {
    double[] arr = new double[16];
    arr[0] = matrix.m00;
    arr[1] = matrix.m01;
    arr[2] = matrix.m02;
    arr[3] = matrix.m03;

    arr[4] = matrix.m10;
    arr[5] = matrix.m11;
    arr[6] = matrix.m12;
    arr[7] = matrix.m13;

    arr[8] = matrix.m20;
    arr[9] = matrix.m21;
    arr[10] = matrix.m22;
    arr[11] = matrix.m23;

    arr[12] = matrix.m30;
    arr[13] = matrix.m31;
    arr[14] = matrix.m32;
    arr[15] = matrix.m33;

    return arr;
  }

  /*
  public static int M(int row, int col)
  {
  return col * 4 + row;
  }
   */
  public static int getMatrixIndex(int row, int col)
  {
    return ((col << 2) + row);
  }

  /*
  public static double[] matmul34( final double[] a, final double[] b )
  {
  double[] product = new double[16];

  for (int i = 0; i < 3; i++)
  {
  System.out.println("i = " + i);
  final double ai0=a[getMatrixIndex(i,0)],  ai1=a[getMatrixIndex(i,1)],  ai2=a[getMatrixIndex(i,2)],  ai3=a[getMatrixIndex(i,3)];
  System.out.println("P(i,0) = " + getMatrixIndex(i,0));
  System.out.println("P(i,1) = " + getMatrixIndex(i,1));
  System.out.println("P(i,2) = " + getMatrixIndex(i,2));
  System.out.println("P(i,3) = " + getMatrixIndex(i,3));
  System.out.println("B(i,0) = " + getMatrixIndex(i,0));
  System.out.println("B(i,1) = " + getMatrixIndex(i,1));
  System.out.println("B(i,2) = " + getMatrixIndex(i,2));
  System.out.println("B(i,3) = " + getMatrixIndex(i,3));
  System.out.println("B(2,2) = " + getMatrixIndex(2,2));

  product[getMatrixIndex(i,0)] = ai0 * b[getMatrixIndex(0,0)] + ai1 * b[getMatrixIndex(1,0)] + ai2 * b[getMatrixIndex(2,0)];
  product[getMatrixIndex(i,1)] = ai0 * b[getMatrixIndex(0,1)] + ai1 * b[getMatrixIndex(1,1)] + ai2 * b[getMatrixIndex(2,1)];
  product[getMatrixIndex(i,2)] = ai0 * b[getMatrixIndex(0,2)] + ai1 * b[getMatrixIndex(1,2)] + ai2 * b[getMatrixIndex(2,2)];
  product[getMatrixIndex(i,3)] = ai0 * b[getMatrixIndex(0,3)] + ai1 * b[getMatrixIndex(1,3)] + ai2 * b[getMatrixIndex(2,3)] + ai3;
  }
  product[getMatrixIndex(3,0)] = 0;
  product[getMatrixIndex(3,1)] = 0;
  product[getMatrixIndex(3,2)] = 0;
  product[getMatrixIndex(3,3)] = 1;

  return product;
  }
   */
  /**
   * Project the specified Point3f into window coordinates using the global modelview and projection matrices.
   * If visible,
   * x is between viewport[0] and viewport[0] + viewport[2]
   * y is between viewport[1] and viewport[1] + viewport[3]
   * and z is between 0 (the nearPlane) and 1 (the farPlane).
   * @param p3f
   * @return the Point3f in window coordinates.
   */
  public static Point3f project(Point3f p3f)
  {
    double[] windowVec = project(
      pointToHomogenousCoords(p3f),
      RenderUtils.getCamera().modelview,
      RenderUtils.getCamera().projection,
      RenderUtils.getCamera().viewport);

    return new Point3f((float) windowVec[0], (float) windowVec[1], (float) windowVec[2]);
  }

  public static Point3f project(Point3f p3f, double[] modelview)
  {
    double[] windowVec = project(
      pointToHomogenousCoords(p3f),
      modelview,
      RenderUtils.getCamera().projection,
      RenderUtils.getCamera().viewport);

    return new Point3f((float) windowVec[0], (float) windowVec[1], (float) windowVec[2]);
  }

  /**
   * Project the specified homogenous point into window coordinates given a specified modelview matrix, 
   * projection matrix, and viewport. 
   * If visible, 
   * x is between viewport[0] and viewport[0] + viewport[2]
   * y is between viewport[1] and viewport[1] + viewport[3]
   * and z is between 0 (the nearPlane) and 1 (the farPlane).
   * @param objectPt
   * @param modelview
   * @param projection
   * @param viewport
   * @return The point in window coordinates.
   */
  public static double[] project(double[] objectPt, double[] modelview, double[] projection, int[] viewport)
  {
    double[] eyeVec = objectCoordsToEyeCoords(objectPt, modelview);
    double[] clipVec = eyeCoordsToClipCoords(eyeVec, projection);
    double[] deviceVec = clipCoordsToDeviceCoords(clipVec);
    double[] windowVec = deviceCoordsToWindowCoords(deviceVec, viewport);

    return windowVec;
  }

  public static double[] unproject(double[] pixelPt, double[] modelview, double[] projection, int[] viewport)
  {
    double[] deviceVec = windowCoordsToDeviceCoords(pixelPt, viewport);
    double[] clipVec = deviceCoordsToClipCoords(deviceVec);
    double[] eyeVec = clipCoordsToEyeCoords(clipVec, projection);
    double[] objectVec = eyeCoordsToObjectCoords(eyeVec, modelview);
    return normalizeHomogeneousVector(objectVec);
  }

  public static double[] unprojectOld(double[] pixelPt, double[] modelview, double[] projection, int[] viewport)
  {
    double[] mvp = multiplyMatrixByMatrix(projection, modelview);
    mvp = invertMatrix(mvp);

    double y = (viewport[3] - pixelPt[1]);

    FloatBuffer zBuf = FloatBuffer.allocate(1);
    RenderUtils.getRenderer().gl.glReadPixels(
      (int) pixelPt[0], (int) y, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zBuf);
    float z = zBuf.get();
    double in[] = new double[]
    {
      pixelPt[0], y, z, 1.0
    };

    in[0] = (in[0] - (double) viewport[0]) / (double) viewport[2];
    in[1] = (in[1] - (double) viewport[1]) / (double) viewport[3];

    in[0] = in[0] * 2 - 1;
    in[1] = in[1] * 2 - 1;
    in[2] = in[2] * 2 - 1;

    double[] out = multiplyMatrixByVector(mvp, in);
    out[0] /= out[3];
    out[1] /= out[3];
    out[2] /= out[3];
    return out;
  }

  /**
   * From mesa GLU. only works on 4x4 matrices. Might be faster to use vecmath or apache version.
   * @param m
   * @return
   */public static double[] invertMatrix(double[] m)
  {
    double[] invOut = new double[16];
    double[] inv = new double[16];
    double det;
    int i;

    inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] + m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10];
    inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] - m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10];
    inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] + m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9];
    inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] - m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9];
    inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] - m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10];
    inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] + m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10];
    inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] - m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9];
    inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] + m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9];
    inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] + m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6];
    inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] - m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6];
    inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] + m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5];
    inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] - m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5];
    inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] - m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6];
    inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] + m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6];
    inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] - m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5];
    inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] + m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5];

    det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];
    //if (det == 0)
    //    return GL_FALSE;

    det = 1.0 / det;

    for (i = 0; i < 16; i++)
    {
      invOut[i] = inv[i] * det;
    }

    return invOut;
  //return GL_TRUE;
  }

  public static double[] pointToHomogenousCoords(Point3f p3f)
  {
    float[] point = MatrixUtils.toArray(p3f);

    return new double[]
      {
        point[0], point[1], point[2], 1.0
      };
  }

  public static double[] pointToHomogenousCoords(double[] point)
  {
    return new double[]
      {
        point[0], point[1], point[2], 1.0
      };
  }

  public static double[] normalizeHomogeneousVector(double[] vec)
  {
    return new double[]
      {
        vec[0] / vec[3],
        vec[1] / vec[3],
        vec[2] / vec[3]
      };
  }

  /**
   * Transforms a Point3f into homogeneous eye coordinatees.
   * @param objectP3f.
   * @param modelview.
   * @return The point in eye coordinates.
   */
  public static double[] objectCoordsToEyeCoords(Point3f objectP3f, double[] modelview)
  {
    return multiplyMatrixByVector(modelview, pointToHomogenousCoords(objectP3f));
  }

  /**
   * Transforms a point in homogeneous object coordinates into homogeneous eye coordinatees.
   * @param objectVec A vector of length 4.
   * @param modelview The 4x4 modelview matrix.
   * @return The point in eye coordinates.
   */
  public static double[] objectCoordsToEyeCoords(double[] objectVec, double[] modelview)
  {
    return multiplyMatrixByVector(modelview, objectVec);
  }

  /**
   * Transforms a point in homogenous eye coordinates into homogenous object coordinates.
   * The method itself handles inverting the modelview matrix, so just
   * pass in the normal modelview.
   * @param eyeVec A vector of length 4.
   * @param modelview The 4x4 modelview matrix
   * @return The point in object coordinates.
   */
  public static double[] eyeCoordsToObjectCoords(double[] eyeVec, double[] modelview)
  {
    return multiplyMatrixByVector(invertMatrix(modelview), eyeVec);
  }

  /**
   * Transforms a point in homogeneous eye coordinates into homogeneous clip coordinates.
   * @param eyeVec A vector of length 4.
   * @param projection The 4x4 projection matrix.
   * @return The point in clip coordinates.
   */
  public static double[] eyeCoordsToClipCoords(double[] eyeVec, double[] projection)
  {
    return multiplyMatrixByVector(projection, eyeVec);
  }

  /**
   * Transforms a point in homogenous clip coordinates into homogenous eye coordinates.
   * The method itself handles inverting the projection matrix, so just
   * pass in the normal projection.
   * @param clipVec A vector of length 4.
   * @param modelview The 4x4 projection matrix
   * @return The point in eye coordinates.
   */
  public static double[] clipCoordsToEyeCoords(double[] clipVec, double[] projection)
  {
    return multiplyMatrixByVector(invertMatrix(projection), clipVec);
  }

  /**
   * Transforms the point in homogeneous clip coordinates to normalized device coordinates. This
   * is done by taking the x, y, and z coords and dividing by w. The w is then discarded.
   * @param clipVec A vector of length 4.
   * @return The point in normalized device coordinates.
   */
  public static double[] clipCoordsToDeviceCoords(double[] clipVec)
  {
    return normalizeHomogeneousVector(clipVec);
  }

  /**
   * Transforms the point in normalized device coordinates into homogenous clip coordinates.
   * @param deviceVec
   * @return The point in homogenous clip coordinates.
   */
  public static double[] deviceCoordsToClipCoords(double[] deviceVec)
  {
    return pointToHomogenousCoords(new double[]
      {
        deviceVec[0] * 2 - 1,
        deviceVec[1] * 2 - 1,
        deviceVec[2] * 2 - 1,
      });
  }

  /**
   * Transforms the point in normalized device coordinates to window coordinates.
   * @param deviceVec A vector of length 3.
   * @param viewport The viewport bounds of the window.
   * @return The point in window (pixel) coordinates.
   */
  public static double[] deviceCoordsToWindowCoords(double[] deviceVec, int[] viewport)
  {
    return new double[]
      {
        viewport[0] + (((deviceVec[0] + 1) * (viewport[2])) / 2),
        viewport[1] + (((deviceVec[1] + 1) * (viewport[3])) / 2),
        (deviceVec[2] + 1) / 2
      };
  }

  /**
   * Transforms the point in window coordinates into normalized device coordinates.
   * If the window z value is not given, we calculate it from the depth buffer
   * by calling glReadPixels.
   * @param pixels
   * @param viewport
   * @return The point in normalized device coordinates.
   */
  public static double[] windowCoordsToDeviceCoords(double[] pixels, int[] viewport)
  {
    double x, y, z;
    
    x = pixels[0];

    if (pixels.length == 2) //then we calculate the depth oursleves
    {
      y = (viewport[3] - pixels[1]);

      FloatBuffer zBuf = FloatBuffer.allocate(1);
      RenderUtils.getRenderer().gl.glReadPixels(
        (int) pixels[0], (int) y, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zBuf);
      z = zBuf.get();
    }
    else //we assume you know what you are doing
    {
      y = pixels[1]; //might need to reverse as well?
      z = pixels[2];
    }

    return (new double[]
      {
        (x - (double) viewport[0]) / (double) viewport[2],
        (y - (double) viewport[1]) / (double) viewport[3],
        z
      });

  }

  /**
   * Multiply A * vec.
   * @param A A 4x4 matrix.
   * @param vec A vector of length 4.
   * @return The result of A * vec as a new vector of length 4.
   */
  public static double[] multiplyMatrixByVector(final double[] A, final double[] vec)
  {
    double[] product = new double[4];

    for (int i = 0; i < 4; i++)
    {
      final double ai0 = A[getMatrixIndex(i, 0)],  ai1 = A[getMatrixIndex(i, 1)],  ai2 = A[getMatrixIndex(i, 2)],  ai3 = A[getMatrixIndex(i, 3)];

      product[getMatrixIndex(i, 0)] =
        ai0 * vec[getMatrixIndex(0, 0)] +
        ai1 * vec[getMatrixIndex(1, 0)] +
        ai2 * vec[getMatrixIndex(2, 0)] +
        ai3 * vec[getMatrixIndex(3, 0)];
    }

    return product;
  }

  /**
   * Multiply A * B.
   * @param A A 4x4 matrix.
   * @param B Another 4x4 matrix.
   * @return The result of A * B in a new 4x4 matrix.
   */
  public static double[] multiplyMatrixByMatrix(final double[] A, final double[] B)
  {
    double[] product = new double[16];
    for (int i = 0; i < 4; i++)
    {
      final double ai0 = A[getMatrixIndex(i, 0)],  ai1 = A[getMatrixIndex(i, 1)],  ai2 = A[getMatrixIndex(i, 2)],  ai3 = A[getMatrixIndex(i, 3)];

      product[getMatrixIndex(i, 0)] = ai0 * B[getMatrixIndex(0, 0)] + ai1 * B[getMatrixIndex(1, 0)] + ai2 * B[getMatrixIndex(2, 0)] + ai3 * B[getMatrixIndex(3, 0)];
      product[getMatrixIndex(i, 1)] = ai0 * B[getMatrixIndex(0, 1)] + ai1 * B[getMatrixIndex(1, 1)] + ai2 * B[getMatrixIndex(2, 1)] + ai3 * B[getMatrixIndex(3, 1)];
      product[getMatrixIndex(i, 2)] = ai0 * B[getMatrixIndex(0, 2)] + ai1 * B[getMatrixIndex(1, 2)] + ai2 * B[getMatrixIndex(2, 2)] + ai3 * B[getMatrixIndex(3, 2)];
      product[getMatrixIndex(i, 3)] = ai0 * B[getMatrixIndex(0, 3)] + ai1 * B[getMatrixIndex(1, 3)] + ai2 * B[getMatrixIndex(2, 3)] + ai3 * B[getMatrixIndex(3, 3)];
    }

    return product;
  }

  public static double[] perspective(double fovy, double aspect, double zNear, double zFar)
  {
    double[] m = getIdentity();

    double sine, cotangent, deltaZ;
    double radians = fovy / 2.0 * Math.PI / 180.0;

    deltaZ = zFar - zNear;
    sine = Math.sin(radians);
    if ((deltaZ == 0) || (sine == 0) || (aspect == 0))
    {
      return m;
    }
    cotangent = Math.cos(radians) / sine;

    m[0] = cotangent / aspect;
    m[5] = cotangent;
    m[10] = -(zFar + zNear) / deltaZ;
    m[11] = -1;
    m[14] = -2 * zNear * zFar / deltaZ;
    m[15] = 0;
    return m;
  }

  public static double[] rotate(double[] mat, double angle, double x, double y, double z)
  {
    double xx, yy, zz, xy, yz, zx, xs, ys, zs, one_c, s, c;
    double m[] = MatrixUtils.getIdentity();
    boolean optimized = false;

    s = (float) Math.sin(Math.toRadians(angle));
    c = (float) Math.cos(Math.toRadians(angle));

    if (x == 0.0F)
    {
      if (y == 0.0F)
      {
        if (z != 0.0F)
        {
          optimized = true;
          /* rotate only around z-axis */
          m[getMatrixIndex(0, 0)] = c;
          m[getMatrixIndex(1, 1)] = c;
          if (z < 0.0F)
          {
            m[getMatrixIndex(0, 1)] = s;
            m[getMatrixIndex(1, 0)] = -s;
          }
          else
          {
            m[getMatrixIndex(0, 1)] = -s;
            m[getMatrixIndex(1, 0)] = s;
          }
        }
      }
      else if (z == 0.0F)
      {
        optimized = true;
        /* rotate only around y-axis */
        m[getMatrixIndex(0, 0)] = c;
        m[getMatrixIndex(2, 2)] = c;
        if (y < 0.0F)
        {
          m[getMatrixIndex(0, 2)] = -s;
          m[getMatrixIndex(2, 0)] = s;
        }
        else
        {
          m[getMatrixIndex(0, 2)] = s;
          m[getMatrixIndex(2, 0)] = -s;
        }
      }
    }
    else if (y == 0.0F)
    {
      if (z == 0.0F)
      {
        optimized = true;
        /* rotate only around x-axis */
        m[getMatrixIndex(1, 1)] = c;
        m[getMatrixIndex(2, 2)] = c;
        if (x < 0.0F)
        {
          m[getMatrixIndex(1, 2)] = s;
          m[getMatrixIndex(2, 1)] = -s;
        }
        else
        {
          m[getMatrixIndex(1, 2)] = -s;
          m[getMatrixIndex(2, 1)] = s;
        }
      }
    }

    //haven't tested this... we are calling each rotation around the axis separately so we
    //don't get here via the Geom.transform method...
    if (!optimized)
    {
      final float mag = (float) Math.sqrt(x * x + y * y + z * z);

      if (mag <= 1.0e-4)
      {
        /* no rotation, leave mat as-is */
        return mat;
      //return;
      }

      x /= mag;
      y /= mag;
      z /= mag;


      /*
       *     Arbitrary axis rotation matrix.
       *
       *  This is composed of 5 matrices, Rz, Ry, T, Ry', Rz', multiplied
       *  like so:  Rz * Ry * T * Ry' * Rz'.  T is the final rotation
       *  (which is about the X-axis), and the two composite transforms
       *  Ry' * Rz' and Rz * Ry are (respectively) the rotations necessary
       *  from the arbitrary axis to the X-axis then back.  They are
       *  all elementary rotations.
       *
       *  Rz' is a rotation about the Z-axis, to bring the axis vector
       *  into the x-z plane.  Then Ry' is applied, rotating about the
       *  Y-axis to bring the axis vector parallel with the X-axis.  The
       *  rotation about the X-axis is then performed.  Ry and Rz are
       *  simply the respective inverse transforms to bring the arbitrary
       *  axis back to it's original orientation.  The first transforms
       *  Rz' and Ry' are considered inverses, since the data from the
       *  arbitrary axis gives you info on how to get to it, not how
       *  to get away from it, and an inverse must be applied.
       *
       *  The basic calculation used is to recognize that the arbitrary
       *  axis vector (x, y, z), since it is of unit length, actually
       *  represents the sines and cosines of the angles to rotate the
       *  X-axis to the same orientation, with theta being the angle about
       *  Z and phi the angle about Y (in the order described above)
       *  as follows:
       *
       *  cos ( theta )] = x / sqrt ( 1 - z^2 )
       *  sin ( theta )] = y / sqrt ( 1 - z^2 )
       *
       *  cos ( phi )] = sqrt ( 1 - z^2 )
       *  sin ( phi )] = z
       *
       *  Note that cos ( phi ) can further be inserted to the above
       *  formulas:
       *
       *  cos ( theta )] = x / cos ( phi )
       *  sin ( theta )] = y / sin ( phi )
       *
       *  ...etc.  Because of those relations and the standard trigonometric
       *  relations, it is pssible to reduce the transforms down to what
       *  is used below.  It may be that any primary axis chosen will give the
       *  same results (modulo a sign convention) using thie method.
       *
       *  Particularly nice is to notice that all divisions that might
       *  have caused trouble when parallel to certain planes or
       *  axis go away with care paid to reducing the expressions.
       *  After checking, it does perform correctly under all cases, since
       *  in all the cases of division where the denominator would have
       *  been zero, the numerator would have been zero as well, giving
       *  the expected result.
       */

      xx = x * x;
      yy = y * y;
      zz = z * z;
      xy = x * y;
      yz = y * z;
      zx = z * x;
      xs = x * s;
      ys = y * s;
      zs = z * s;
      one_c = 1.0F - c;

      /* We already hold the identity-matrix so we can skip some statements */
      m[getMatrixIndex(0, 0)] = (one_c * xx) + c;
      m[getMatrixIndex(0, 1)] = (one_c * xy) - zs;
      m[getMatrixIndex(0, 2)] = (one_c * zx) + ys;
      /*    m[getMatrixIndex(0,3)] = 0.0F; */

      m[getMatrixIndex(1, 0)] = (one_c * xy) + zs;
      m[getMatrixIndex(1, 1)] = (one_c * yy) + c;
      m[getMatrixIndex(1, 2)] = (one_c * yz) - xs;
      /*    m[getMatrixIndex(1,3)] = 0.0F; */

      m[getMatrixIndex(2, 0)] = (one_c * zx) - ys;
      m[getMatrixIndex(2, 1)] = (one_c * yz) + xs;
      m[getMatrixIndex(2, 2)] = (one_c * zz) + c;
    /*    m[getMatrixIndex(2,3)] = 0.0F; */

    /*
    m[getMatrixIndex(3,0)] = 0.0F;
    m[getMatrixIndex(3,1)] = 0.0F;
    m[getMatrixIndex(3,2)] = 0.0F;
    m[getMatrixIndex(3,3)] = 1.0F;
     */
    }
    //matrix_multf( mat, m, MAT_FLAG_ROTATION );

    //System.out.println("in rotate... mat was");
    //printMatrix(mat);
    //System.out.println("in rotate... rot matrix = " + m);
    //printMatrix(m);
    mat = multiplyMatrixByMatrix(mat, m);
    //System.out.println("in rotate... mat is");
    //printMatrix(mat);
    return mat;
  }

  public static double[] scale(double[] m, double x, double y, double z)
  {
    m[0] *= x;
    m[4] *= y;
    m[8] *= z;
    m[1] *= x;
    m[5] *= y;
    m[9] *= z;
    m[2] *= x;
    m[6] *= y;
    m[10] *= z;
    m[3] *= x;
    m[7] *= y;
    m[11] *= z;

    return m;
  }

  public static double[] translate(double[] m, Point3f p3f)
  {
    return translate(m, p3f.x, p3f.y, p3f.z);
  }

  public static double[] translate(double[] m, double x, double y, double z)
  {
    m[12] = m[0] * x + m[4] * y + m[8] * z + m[12];
    m[13] = m[1] * x + m[5] * y + m[9] * z + m[13];
    m[14] = m[2] * x + m[6] * y + m[10] * z + m[14];
    m[15] = m[3] * x + m[7] * y + m[11] * z + m[15];

    return m;
  }

  /*
  public static void rotate(double[] mat, double angX, double angY, double angZ)
  {

  }
   */
}
