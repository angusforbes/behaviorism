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
    //System.out.println("I should never be here...");
    if (VizGeom.EXPLICITLY_CALCULATE_MODELVIEW == true)
    {
      gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
      gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
      gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);

      gl.glTranslatef(anchor.x, anchor.y, anchor.z);
    }
    else
    {
    //set perspective
//    gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
//    gl.glMatrixMode(gl.GL_PROJECTION);
//    gl.glLoadIdentity();
//    glu.gluPerspective(this.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, RendererJogl.nearPlane, RendererJogl.farPlane);
//
//    gl.glMatrixMode(gl.GL_MODELVIEW);
//    gl.glLoadIdentity();

    //set rotation
    gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
    gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
    gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);
    
    //set location
    //real one
    //gl.glTranslatef(-anchor.x, -anchor.y, -anchor.z);
    
    gl.glTranslatef(anchor.x, anchor.y, -anchor.z);
    }

  /* not using anymore, but good to know...
  double[] da = MatrixUtils.getIdentity();
  
  da[8] = m_DirectionVector.x;
  da[9] = m_DirectionVector.y; 
  da[10] = m_DirectionVector.z; 
  da[11] = 0;
  
  da[4] = m_UpVector.x; 
  da[5] = m_UpVector.y; 
  da[6] = m_UpVector.z; 
  da[7] = 0;
  
  da[0] = m_RightVector.x; 
  da[1] = m_RightVector.y; 
  da[2] = m_RightVector.z; 
  da[3] = 0;
  
  da[12] = m_StrafeX; da[13] = m_StrafeY; da[14] = m_Zoom; da[15] = 1;
  
  //MatrixUtils.printMatrix(da);
  //gl.glMultMatrixd(da, 0);
  //glu.gluLookAt(m_Position.x, m_Position.y, m_Position.z, center.x, center.y, center.z, m_UpVector.x, m_UpVector.y, m_UpVector.z);
   */
  }

  @Override
  public void changePitch(double degrees)
  {
    rotate.x += degrees;
  }

  @Override
  public void changeHeading(double degrees)
  {
    rotate.y += degrees;
  }

  @Override
  public void changeYaw(double degrees)
  {
    rotate.z += degrees;
  }

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

