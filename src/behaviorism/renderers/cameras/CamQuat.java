/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviorism.renderers.cameras;

import behaviorism.utils.MatrixUtils;
import behaviorism.utils.RenderUtils;
import static javax.media.opengl.GL2.*;
//import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import static behaviorism.utils.RenderUtils.*;
//import static javax.vecmath.*;
//import static javax.vecmath.Vector3f;

/**
 *
 * @author angus
 */
public class CamQuat extends Cam
{
  Vector3f posVec, viewVec, rightVec, upVec;
  float rotCamX, rotCamY, rotCamZ;
  float rotateSpeed = 10f;
  float translateSpeed = .3f;

  /** 
   * to rotate the camera in place, set the translate to the location of where you want
   * the camera to be, and make sure that the rotateAnchor.translate is set to (0f, 0f, 0f)
   */
  public CamQuat(Point3f p3f)
  {
    initialize(p3f);
  }

  public CamQuat(float x, float y, float z)
  {
    initialize(new Point3f(x, y, z));
  }

  public CamQuat()
  {
    initialize(new Point3f(0f, 0f, -5f));
  }

  public void resetCamera()
  {
    posVec = new Vector3f(0f, 0f, 5f);
    viewVec = new Vector3f(0f, 0f, -1f);
    rightVec = new Vector3f(1f, 0f, 0f);
    upVec = new Vector3f(0f, 1f, 0f);
  }

  public void initialize(Point3f anchorPt)
  {
    resetCamera();
        this.translate = new Point3f(anchorPt);
        //this.rotateAnchor = new GeomPoint(0f, 0f, 0f);
        this.rotateAnchor = new Point3f(0f, 0f, 0f);
        this.resetAnchor = new Point3f(anchorPt);

    setViewPlanes(1.0, 100.0);
    setFovy(45.0);
  }

  
  @Override
  //public double[] view()
  public void view()
  {


   
    //The point at which the camera looks:
    Vector3f viewpoint = new Vector3f(posVec);
    viewpoint.add(viewVec);
//
//    //as we know the up vector, we can easily use gluLookAt:
//    getGLU().gluLookAt(
//    posVec.x, posVec.y, posVec.z,
//    viewpoint.x, viewpoint.y, viewpoint.z,
//    upVec.x, upVec.y, upVec.z
//      );
//
//    RenderUtils.getGL().glGetDoublev(GL_MODELVIEW_MATRIX, modelview, 0);
//
    modelview = calcLookAtMatrix(
      posVec.x, posVec.y, posVec.z,
      viewpoint.x, viewpoint.y, viewpoint.z,
      upVec.x, upVec.y, upVec.z);
    //MatrixUtils.printMatrix(getCamera().modelview);
  }

  public double[] calcLookAtMatrix(
    float pvX, float pvY, float pvZ,
    float vpX, float vpY, float vpZ,
    float upX, float upY, float upZ)
  {
    Vector3f forward = new Vector3f(vpX - pvX, vpY - pvY, vpZ - pvZ);
    Vector3f up = new Vector3f(upX, upY, upZ);

    forward.normalize();

    Vector3f side = new Vector3f();
    side.cross(forward, up);
    side.normalize();

    /* Recompute up as: up = side x forward */
    up.cross(side, forward);

    double[] matrix = MatrixUtils.getIdentity();
    matrix[0 * 4 + 0] = side.x;
    matrix[1 * 4 + 0] = side.y;
    matrix[2 * 4 + 0] = side.z;

    matrix[0 * 4 + 1] = up.x;
    matrix[1 * 4 + 1] = up.y;
    matrix[2 * 4 + 1] = up.z;

    matrix[0 * 4 + 2] = -forward.x;
    matrix[1 * 4 + 2] = -forward.y;
    matrix[2 * 4 + 2] = -forward.z;

    return MatrixUtils.translate(matrix, -pvX, -pvY, -pvZ);

  }

  @Override
  public void rotateX(float angle)
  {
    System.out.println("in CamVector : rotateX() ");
    //rotCamX += angle;

    //Rotate viewdir around the right vector:
    Vector3f tmp1 = new Vector3f(viewVec);
    tmp1.scale((float)Math.cos(Math.toRadians(angle)));
    Vector3f tmp2 = new Vector3f(upVec);
    tmp2.scale((float)Math.sin(Math.toRadians(angle)));
    tmp1.add(tmp2);

    viewVec.normalize(tmp1);

    //now compute the new UpVector (by cross product)
    upVec.cross(viewVec, rightVec);
    upVec.scale(-1f);
    //render();
    isTransformed = true;

  }

  //rotates the coordinate system using a quaternion made from an arbitrary axis
  public void rotateQuaternion(Vector3f axis, double radians)
  {
    //make a quaternion and rotate all 3 vectors around it
    double[] matQuat = MatrixUtils.getIdentity();
    double cosA = Math.cos(radians);
    double oneC = 1 - cosA;
    double sinA = Math.sin(radians);

    axis.normalize();
    double ux = axis.x;
    double uy = axis.y;
    double uz = axis.z;

    matQuat[0 * 4 + 0] = ux*ux*oneC + cosA;
    matQuat[1 * 4 + 0] = ux*uy*oneC - uz*sinA;
    matQuat[2 * 4 + 0] = ux*uz*oneC + uy*sinA;

    matQuat[0 * 4 + 1] = uy*ux*oneC + uz * sinA;
    matQuat[1 * 4 + 1] = uy*uy*oneC + cosA;
    matQuat[2 * 4 + 1] = uy*uz*oneC - ux*sinA;

    matQuat[0 * 4 + 2] = uz*ux*oneC - uy * sinA;
    matQuat[1 * 4 + 2] = uz*uy*oneC + ux * sinA;
    matQuat[2 * 4 + 2] = uz*uz*oneC + cosA;

    double[] vv = MatrixUtils.multiplyMatrixByVector(matQuat,
      new double[]{viewVec.x, viewVec.y, viewVec.z, 1});
    viewVec.x = (float)vv[0];
    viewVec.y = (float)vv[1];
    viewVec.z = (float)vv[2];

    double[] rv = MatrixUtils.multiplyMatrixByVector(matQuat,
      new double[]{rightVec.x, rightVec.y, rightVec.z, 1});
    rightVec.x = (float)rv[0];
    rightVec.y = (float)rv[1];
    rightVec.z = (float)rv[2];

    double[] up = MatrixUtils.multiplyMatrixByVector(matQuat,
      new double[]{upVec.x, upVec.y, upVec.z, 1});
    upVec.x = (float)up[0];
    upVec.y = (float)up[1];
    upVec.z = (float)up[2];
  }

  @Override
  public void rotateAxis(Vector3f axis, float angle)
  {
    System.err.println("in CamQuat : rotateAxis...");
    double radians = Math.toRadians(angle);
    rotateQuaternion(axis, radians);
    isTransformed = true;
  }

  @Override
  public void rotateY(float angle)
  {
    double radians = Math.toRadians(angle);
    Vector3f axis = new Vector3f(0f, 1f, 0f);
    rotateQuaternion(axis, radians);
    isTransformed = true;
  }

  @Override
  public void rotateZ(float angle)
  {
    double radians = Math.toRadians(angle);
    Vector3f axis = new Vector3f(0f, 0f, 1f);
    rotateQuaternion(axis, radians);
    isTransformed = true;
  }

  @Override
  public void translateX( float dist )
  {
    Vector3f tmp1 = new Vector3f(rightVec);
    tmp1.scale(dist);
    posVec.add(tmp1);
       isTransformed = true;

  }

  @Override
  public void translateY( float dist )
  {
    Vector3f tmp1 = new Vector3f(upVec);
    tmp1.scale(dist);
    posVec.add(tmp1);
       isTransformed = true;

  }

  @Override
  public void translateZ( float dist )
  {
    Vector3f tmp1 = new Vector3f(viewVec);
    tmp1.scale(-dist);
    posVec.add(tmp1);
    isTransformed = true;

  }

}









