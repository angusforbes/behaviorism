/* MatrixUtils.java (created on August 30, 2007, 4:49 PM) */

package utils;

import behaviorism.BehaviorismDriver;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import geometry.Geom;
import java.text.DecimalFormat;
import java.util.Arrays;
import javax.vecmath.Point3d;
import javax.vecmath.SingularMatrixException;

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
    catch(SingularMatrixException sme)
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
  
  
  public static Point3d getWorldPointInGeomCoordinates(Point3d worldPt, double[] worldModelview, double[] geomModelview )
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
  public static Point3d getAbsolutePointInWorldCoordinates(Point3d absPt, double[] worldModelview )
  {
    return getAbsolutePointInCoordinates(absPt, worldModelview);
  }
  public static Point3d getAbsolutePointInGeomCoordinates(Point3d absPt, double[] geomModelview )
  {
    return getAbsolutePointInCoordinates(absPt, geomModelview);
  }


  ////higher level utility methods (take in a Geom, or have more logic, etc)

	
  /**
   * Returns the anchor point of the inputGeom in terms of the destGeom's coordinate system
   */
  public static Point3d getGeomPointInGeomCoordinates(Geom inputGeom, Geom destGeom)
  {
    return getGeomPointInGeomCoordinates(new Point3d(inputGeom.anchor), inputGeom.modelview, destGeom.modelview);
  }

  public static void debugPointRelativeToGeom(Point3d absPt, Geom g)
  {
    Point3d worldPt = getAbsolutePointInWorldCoordinates(absPt, BehaviorismDriver.renderer.modelviewMatrix);
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
    Point3d zeroPt = new Point3d(g.anchor); //new Point3f(0f, 0f, 0f);
    Point3d absPt = getGeomPointInAbsoluteCoordinates(zeroPt, g.modelview);
    Point3d worldPt = getGeomPointInWorldCoordinates(zeroPt, g.modelview, BehaviorismDriver.renderer.modelviewMatrix);
    Point3d geomPt = getGeomPointInGeomCoordinates(zeroPt, g.modelview, g.modelview);

      
    System.out.println("orig pt = " + g.anchor);
    System.out.println(" abs pt = " + absPt);
    System.out.println("wrld pt = " + worldPt);
    System.out.println("geom pt = " + geomPt);
  
    if (g.parent != null)
    {
      Point3d parentGeomPt = getGeomPointInGeomCoordinates(zeroPt, g.modelview, g.parent.modelview);

    System.out.println("prnt pt = " + parentGeomPt);

    }
  }
 
	public static void printDoubleArray(double[] da)
	{
		System.out.println("*double array:*");
		System.out.printf("%f %f %f %f\n", da[0], da[1], da[2], da[3]);
		System.out.printf("%f %f %f %f\n", da[4], da[5], da[6], da[7]);
		System.out.printf("%f %f %f %f\n", da[8], da[9], da[10], da[11]);
		System.out.printf("%f %f %f %f\n", da[12], da[13], da[14], da[15]);
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
		return new Point3f((float)p3d.x, (float)p3d.y, (float)p3d.z);
	}

  public static float[] toArray(Point3f p3f)
  {
    return new float[]{p3f.x, p3f.y, p3f.z};
  }
  public static double[] toArray(Point3d p3d)
  {
    return new double[]{p3d.x, p3d.y, p3d.z};
  }
	//public static DecimalFormat pointFormatter = new DecimalFormat("#.###");
	public static DecimalFormat pointFormatter = new DecimalFormat("#.########");
  
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
			
}
