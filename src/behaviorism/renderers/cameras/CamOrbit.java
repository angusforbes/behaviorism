/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviorism.renderers.cameras;

import javax.vecmath.Point3f;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import behaviorism.utils.RenderUtils;
import static behaviorism.utils.RenderUtils.*;

/**
 *
 * @author angus
 */
public class CamOrbit extends Cam
{

  /**
   * translate = the point that you are rotating around and either facing, or facing away from.
   * rotateAnchor = position of the camera relative to the translate (in general use a negative z value only)
   */
  public CamOrbit()
  {
    translate = new Point3f(0f, 0f, 10f); //lookAt point

    //if the z value of rotateAnchor is negative, then the camera will point TOWARD the object
    //situated at the lookAt point (which it is rotating around). Otherwise is will point
    //AWAY from that point, and thus will never see it! (maybe that's what you want though). In general,
    //you normally will want to only have use the z value, and make sure it is negative.
    //rotateAnchor = new GeomPoint(0f, 0f, -5f); //distance vector
    rotateAnchor = new Point3f(0f, 0f, -5f); //distance vector
  }

  /** Make sure that the viewDistance is negative if you want the camera to look at the lookAtPoint,
   * or positive to have the camera's "back" to the lookAtPoint.
   * @param lookAtPoint the point that the camera orbits around
   * @param viewDistance the distance "in front" of the lookAtPoint (should be negative to look at the lookAtPoint)
   */
  public CamOrbit(Point3f lookAtPoint, float viewDistance)
  {
    this.translate = lookAtPoint;
    //this.rotateAnchor = new GeomPoint(0f, 0f, viewDistance);
    this.rotateAnchor = new Point3f(0f, 0f, viewDistance);
  }

  public CamOrbit(float x, float y, float z, float viewDistance)
  {
    this.translate = new Point3f(x, y, z);
    //this.rotateAnchor = new GeomPoint(0f, 0f, viewDistance);
    this.rotateAnchor = new Point3f(0f, 0f, viewDistance);
  }

  //public CamOrbit(Point3f lookAtPoint, GeomPoint rotateAnchor)
  public CamOrbit(Point3f lookAtPoint, Point3f rotateAnchor)
  {
    this.translate = lookAtPoint;
    this.rotateAnchor = rotateAnchor;
  }

  public void view()
  {
    System.out.println("ERROR -- we haven't set up the perspective for CamOrbit!!! TO DO!!!");
    System.exit(0);
  }

  /*
  public void setPerspective()
  {
    GL2 gl = getGL();
    //gl.glTranslatef(rotateAnchor.translate.x, rotateAnchor.translate.y, rotateAnchor.translate.z);
    gl.glTranslatef(rotateAnchor.x, rotateAnchor.y, rotateAnchor.z);

    gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
    gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
    gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);

    gl.glTranslatef(translate.x, translate.y, translate.z);
  }
   */

  public void changePitch(double degrees)
  {
    rotate.x += degrees;
  }

  public void changeHeading(double degrees)
  {
    rotate.y += -degrees;
  }

  public void changeYaw(double degrees)
  {
    rotate.z += degrees;
  }

  public void translateX(float x)
  {
    //rotateAnchor.translate.x += x;
  }

  public void translateY(float y)
  {
    //rotateAnchor.translate.y += y;
  }

  public void translateZ(float z)
  {
    rotateAnchor.z += z;
  }
}
