package behaviorism.renderers.cameras;

import behaviorism.geometry.GeomPoint;
import behaviorism.utils.MatrixUtils;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import javax.media.opengl.GL;

/**
 * openGL uses a RHS, meaning that the Z axis looks down the *negative* z axis.
 * That is, camera is at, say 10z, and things are place in front of it,
 * from 9.9z (close to camera) to -100z (far away from it).
 * To move something away from the camera you *decrease* its z value.
 */
public abstract class Cam extends GeomPoint
{
  //lookAt information

  public Point3f resetAnchor = new Point3f();
  public Point3f resetRotateAnchor = new Point3f();
  //view information
  private double fovy; //field of view angle, in	degrees, in the y	direction.
  private double aspect; //the	aspect ratio that determines the field of view in the x direction. The aspect ratio is the ratio	of x (width) to	y (height).
  private double nearPlane = 1f; //the	distance from the viewer to the	near clipping plane (always positive).
  private double farPlane = 100f; //the	distance from the viewer to the	far clipping plane (always positive).
  public double[] projection;
  public boolean projectionHasChanged = true;
  public int[] viewport;

  public void setViewPlanes(double near, double far)
  {
    this.nearPlane = near;
    this.farPlane = far;
    this.projectionHasChanged = true;
  }

  public double getFovy()
  {
    return fovy;
  }

  public double getAspectRatio()
  {
    return aspect;
  }

  public void setFovy(double fovy)
  {
    this.fovy = fovy;
    this.projectionHasChanged = true;
    System.err.println("new fovy is : " + fovy);
  }

  public void setAspectRatio(double aspect)
  {
    this.aspect = aspect;
    this.projectionHasChanged = true;
    System.err.println("new aspect ration is : " + aspect);
  }

  public void setAspectRatio(int w, int h)
  {
    this.aspect = (double) w / (double) h;
    this.projectionHasChanged = true;
    System.err.println("new aspect ration is : " + aspect);
  }

  public void setViewport(int x, int y, int w, int h)
  {
    this.viewport = new int[]
      {
        x, y, w, h
      };

    System.err.println("new viewport is : " + x + "/" + y + "/" + w + "/" + h);
    this.projectionHasChanged = true;
  }

  /*
  public void changeHeading(double degrees)
  {
  }

  public void changePitch(double degrees)
  {
  }

  public void changeYaw(double degrees)
  {
  }
  */

  @Deprecated
  public void resetPerspective()
  {
  }

  /*
  public void setPerspective()
  {
  }
  */
  
  //should be abstract
  //public double[] view()
  abstract public void view();
//  {
//    return MatrixUtils.getIdentity();
//  }

  public void projection()
  {
    if (projection == null || projectionHasChanged == true) //or has changed... TO DO
    {

      System.err.println("in Cam.projection() : UPDATING PROJECTION");
      projection = MatrixUtils.perspective(
        fovy,
        aspect,
        //((float) Behaviorism.canvasWidth) / Behaviorism.canvasHeight,
        nearPlane, farPlane);

      projectionHasChanged = false;
    }
  }

  //should be abstract
//  public double[] resetPerspective()
//  {
//    return MatrixUtils.getIdentity();
//  }

  /*
  public void moveX(float x)
  {
  }

  public void moveY(float y)
  {
  }

  public void moveZ(float z)
  {
  }
   */
  /*
  public void moveTo(float x, float y, float z, long when, long howfast)
  {
  }

  public void jumpTo(float x, float y, float z)
  {
  }
   */
}

