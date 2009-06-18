/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import behaviors.Behavior;
import behaviors.geom.continuous.BehaviorTranslate;
import geometry.GeomPoint;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class CamBasic extends Cam
{

  /** 
   * to rotate the camera in place, set the anchor to the location of where you want
   * the camera to be, and make sure that the rotateAnchor.anchor is set to (0f, 0f, 0f)
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
    this.anchor = new Point3f(anchorPt);
    this.rotateAnchor = new GeomPoint(0f, 0f, 0f);
    this.resetAnchor = new Point3f(anchorPt);

    this.fovy = 45;
  }
  
  /** return camera to original setup, with original x and y translation and no rotation.
   * This resets to the original placement, except we are still
   * moving the camera along the current z-axis. This method is used for calculating
   * a good looking text size.
   */
  @Override
  public void resetPerspective(GL gl, GLU glu)
  {
    //set perspective
//    gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
//    gl.glMatrixMode(gl.GL_PROJECTION);
//    gl.glLoadIdentity();
//    glu.gluPerspective(this.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);

//    gl.glMatrixMode(gl.GL_MODELVIEW);
//    gl.glLoadIdentity();

    //gl.glTranslatef(-resetAnchor.x, -resetAnchor.y, -resetAnchor.z);
    
    //real one...
    //gl.glTranslatef(-resetAnchor.x, -resetAnchor.y, -anchor.z);
 
    //set original rotation
    
    //set original location 
    gl.glTranslatef(resetAnchor.x, resetAnchor.y, resetAnchor.z);

  }

  /*
  @Deprecated
  public double[] resetPerspective()
  {
    modelview = MatrixUtils.getIdentity();
    MatrixUtils.translate(modelview, resetAnchor.x, resetAnchor.y, resetAnchor.z);
    return modelview;
  }
  */

  public double[] perspective()
  {
    modelview = MatrixUtils.getIdentity();
    modelview = MatrixUtils.rotate(modelview, rotate.x, 1.0f, 0.0f, 0.0f);
    modelview = MatrixUtils.rotate(modelview, rotate.y, 0.0f, 1.0f, 0.0f);
    modelview = MatrixUtils.rotate(modelview, rotate.z, 0.0f, 0.0f, 1.0f);
    modelview = MatrixUtils.translate(modelview, anchor.x, anchor.y, -anchor.z);
    return modelview;
  }

  @Override
  public void setPerspective(GL gl, GLU glu)
  {
      gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
      gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
      gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);

      gl.glTranslatef(anchor.x, anchor.y, anchor.z);
  }

  @Override
  public void changePitch(double degrees)
  {
    rotateX(+(float)degrees);
    //rotate.x += degrees;
  }

  @Override
  public void changeHeading(double degrees)
  {
    rotateY(+(float)degrees);
    //rotate.y += degrees;
  }

  @Override
  public void changeYaw(double degrees)
  {
    rotateZ(+(float)degrees);
    //rotate.z += degrees;
  }

  /*
  @Override
  public void moveX(float x)
  {
    anchor.x += x;
  }

  @Override
  public void moveY(float y)
  {
    anchor.y += y;
  }

  @Override
  public void moveZ(float z)
  {
    anchor.z += z;
  }
  */
  @Override
  public void moveTo(float x, float y, float z, long when, long howfast)
  {
    float dist_x = x - anchor.x;
    float dist_y = y - anchor.y;
    float dist_z = z - anchor.z;

    Behavior moveBehavior = BehaviorTranslate.translate(when, howfast,
      new Point3f(dist_x, dist_y, dist_z));

    attachBehavior(moveBehavior);
  }

  @Override
  public void jumpTo(float x, float y, float z)
  {
    anchor.x = x;
    anchor.y = y;
    anchor.z = z;
  }
}

