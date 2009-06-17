/*
 * Cam.java
 *
 * Created on November 18, 2007, 1:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package renderers;

import geometry.GeomPoint;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import javax.media.opengl.GL;
import utils.MatrixUtils;

/**
 *
 * @author basakalper
 */
public abstract class Cam extends GeomPoint
{
  //lookAt information
  public Point3f resetAnchor = new Point3f();
  public Point3f resetRotateAnchor = new Point3f();

  //perspective information
  public double fovy; //field of view angle, in	degrees, in the y	direction.
  public double apsect; //the	aspect ratio that determines the field of view in the x direction. The aspect ratio is the ratio	of x (width) to	y (height).
  public double zNear; //the	distance from the viewer to the	near clipping plane (always positive).
  public double zFar; //the	distance from the viewer to the	far clipping plane (always positive).

  //public double[] modelview = MatrixUtils.getIdentity();

  public void changeHeading(double degrees)
  {
  }

  public void changePitch(double degrees)
  {
  }

  public void changeYaw(double degrees)
  {
  }

  @Deprecated
  public void resetPerspective(GL gl, GLU glu)
  {
  }

  public void setPerspective(GL gl, GLU glu)
  {
  }


  //should be abstract
  public double[] perspective()
  {
    return MatrixUtils.getIdentity();
  }

  //should be abstract
  public double[] resetPerspective()
  {
    return MatrixUtils.getIdentity();
  }

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
  
  public void moveTo(float x, float y, float z, long when, long howfast)
  {
  }
  
  public void jumpTo(float x, float y, float z)
  {
  }
}

