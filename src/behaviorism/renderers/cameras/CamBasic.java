/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviorism.renderers.cameras;

import behaviorism.utils.MatrixUtils;
import javax.vecmath.Point3f;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import behaviorism.utils.RenderUtils;
import static behaviorism.utils.RenderUtils.*;
/**
 *
 * @author angus
 */
public class CamBasic extends Cam
{

  /** 
   * to rotate the camera in place, set the translate to the location of where you want
   * the camera to be, and make sure that the rotateAnchor.translate is set to (0f, 0f, 0f)
   */
  public CamBasic(Point3f p3f)
  {
    initialize(p3f);
  }

  public CamBasic(float x, float y, float z)
  {
    initialize(new Point3f(x, y, z));
  }

  public CamBasic()
  {
    //initialize(new Point3f(0f, 0f, -5f));
    initialize(new Point3f(0f, 0f, 5f));
  }

  public void initialize(Point3f anchorPt)
  {
    this.translate = new Point3f(anchorPt);
    //this.rotateAnchor = new GeomPoint(0f, 0f, 0f);
    this.rotateAnchor = new Point3f(0f, 0f, 0f);
    this.resetAnchor = new Point3f(anchorPt);

    setViewPlanes(1.0, 100.0);
    setFovy(45.0);
  }

  /**
   * Returns camera to original setup, with original x and y translation and no rotation.
   * This resets to the original placement, except we are still
   * moving the camera along the current z-axis. This method is used for calculating
   * a good looking text size.
   */
  @Override
  public void resetPerspective()
  {
    //set original location 
    getGL().glTranslatef(resetAnchor.x, resetAnchor.y, resetAnchor.z);
  }

  @Override
  //public double[] perspective()
  public void perspective()
  {

    modelview = MatrixUtils.getIdentity();
    modelview = MatrixUtils.rotate(modelview, rotate.x, 1.0f, 0.0f, 0.0f);
    modelview = MatrixUtils.rotate(modelview, rotate.y, 0.0f, 1.0f, 0.0f);
    modelview = MatrixUtils.rotate(modelview, rotate.z, 0.0f, 0.0f, 1.0f);
    modelview = MatrixUtils.translate(modelview, translate.x, translate.y, -translate.z);
    //return modelview;
  }

  @Override
  public void setPerspective()
  {
    GL2 gl = getGL();
    gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
    gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
    gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);

    gl.glTranslatef(translate.x, translate.y, translate.z);
  }

  @Override
  public void changePitch(double degrees)
  {
    rotateX(+(float) degrees);
    //rotate.x += degrees;
  }

  @Override
  public void changeHeading(double degrees)
  {
    rotateY(+(float) degrees);
    //rotate.y += degrees;
  }

  @Override
  public void changeYaw(double degrees)
  {
    rotateZ(+(float) degrees);
    //rotate.z += degrees;
  }

  /*
  @Override
  public void translateX(float x)
  {
  translate.x += x;
  }

  @Override
  public void translateY(float y)
  {
  translate.y += y;
  }

  @Override
  public void translateZ(float z)
  {
  translate.z += z;
  }
   */
  @Override
  public void moveTo(float x, float y, float z, long when, long howfast)
  {
    /*
    float dist_x = x - translate.x;
    float dist_y = y - translate.y;
    float dist_z = z - translate.z;

    Behavior moveBehavior = BehaviorTranslate.translate(when, howfast,
    new Point3f(dist_x, dist_y, dist_z));

    attachBehavior(moveBehavior);
     */
  }

  @Override
  public void jumpTo(float x, float y, float z)
  {
    translate.x = x;
    translate.y = y;
    translate.z = z;
  }
}

