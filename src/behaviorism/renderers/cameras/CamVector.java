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
public class CamVector extends Cam
{

  Vector3f posVec, viewVec, rightVec, upVec;
  float rotCamX, rotCamY, rotCamZ;
  float rotateSpeed = 10f;
  float translateSpeed = .3f;


  private Vector3f forward;
  private Vector3f up;
  private Vector3f side;
  private double[] lookat;
  private Point3f initialPosition;

  /** 
   * to rotate the camera in place, set the translate to the location of where you want
   * the camera to be, and make sure that the rotateAnchor.translate is set to (0f, 0f, 0f)
   */
  public CamVector(Point3f p3f)
  {
    initialize(p3f);
  }

  public CamVector(float x, float y, float z)
  {
    initialize(new Point3f(x, y, z));
  }

  public CamVector()
  {
    initialize(new Point3f(0f, 0f, 5f));
  }

  public void resetCamera()
  {
    posVec = new Vector3f(initialPosition);
    viewVec = new Vector3f(0f, 0f, -1f);
    rightVec = new Vector3f(1f, 0f, 0f);
    upVec = new Vector3f(0f, 1f, 0f);
  }

  public void initialize(Point3f anchorPt)
  {
    initialPosition = anchorPt;

    resetCamera();

    setViewPlanes(1.0, 100.0);
    setFovy(45.0);
  }

  @Override
  public void view()
  {

    if (isTransformed == true)
    {
      //The point at which the camera looks:
      Vector3f viewpoint = new Vector3f(posVec);
      viewpoint.add(viewVec);

      modelview = calcLookAtMatrix(
        posVec.x, posVec.y, posVec.z,
        viewpoint.x, viewpoint.y, viewpoint.z,
        upVec.x, upVec.y, upVec.z);
//    MatrixUtils.printMatrix(getCamera().modelview);
//    System.out.println("...");
    }
  }

  public double[] calcLookAtMatrix(
    float pvX, float pvY, float pvZ,
    float vpX, float vpY, float vpZ,
    float upX, float upY, float upZ)
  {
    forward = new Vector3f(vpX - pvX, vpY - pvY, vpZ - pvZ);
    up = new Vector3f(upX, upY, upZ);

    forward.normalize();

    side = new Vector3f();
    side.cross(forward, up);
    side.normalize();

    /* Recompute up as: up = side x forward */
    up.cross(side, forward);

    lookat = MatrixUtils.getIdentity();
    lookat[0 * 4 + 0] = side.x;
    lookat[1 * 4 + 0] = side.y;
    lookat[2 * 4 + 0] = side.z;

    lookat[0 * 4 + 1] = up.x;
    lookat[1 * 4 + 1] = up.y;
    lookat[2 * 4 + 1] = up.z;

    lookat[0 * 4 + 2] = -forward.x;
    lookat[1 * 4 + 2] = -forward.y;
    lookat[2 * 4 + 2] = -forward.z;

    return MatrixUtils.translate(lookat, -pvX, -pvY, -pvZ);
  }

  @Override
  public void rotateX(float angle)
  {
    //System.out.println("in CamVector : rotateX() ");
    //rotCamX += angle;

    //Rotate viewdir around the right vector:
    Vector3f tmp1 = new Vector3f(viewVec);
    tmp1.scale((float) Math.cos(Math.toRadians(angle)));
    Vector3f tmp2 = new Vector3f(upVec);
    tmp2.scale((float) Math.sin(Math.toRadians(angle)));
    tmp1.add(tmp2);

    viewVec.normalize(tmp1);

    //now compute the new UpVector (by cross product)
    upVec.cross(viewVec, rightVec);
    upVec.scale(-1f);
    //render();
    isTransformed = true;

  }

  @Override
  public void rotateY(float angle)
  {
    //System.out.println("in changeHeading : vec...");
    //rotCamY += (float)angle;

    //Rotate viewdir around the up vector:
    Vector3f tmp1 = new Vector3f(viewVec);
    tmp1.scale((float) Math.cos(Math.toRadians(angle)));
    Vector3f tmp2 = new Vector3f(rightVec);
    tmp2.scale((float) Math.sin(Math.toRadians(angle)));
    tmp1.sub(tmp2);

    viewVec.normalize(tmp1);

    //now compute the new UpVector (by cross product)
    rightVec.cross(viewVec, upVec);
    isTransformed = true;

  }

  @Override
  public void rotateZ(float angle)
  {
    //rotCamZ += (float)angle;

    //Rotate viewdir around the right vector:
    Vector3f tmp1 = new Vector3f(rightVec);
    tmp1.scale((float) Math.cos(Math.toRadians(angle)));
    Vector3f tmp2 = new Vector3f(upVec);
    tmp2.scale((float) Math.sin(Math.toRadians(angle)));
    tmp1.add(tmp2);

    rightVec.normalize(tmp1);

    //now compute the new UpVector (by cross product)
    upVec.cross(viewVec, rightVec);
    upVec.scale(-1f);
    //  render();
    isTransformed = true;

  }

  @Override
  public void translateX(float dist)
  {
    Vector3f tmp1 = new Vector3f(rightVec);
    tmp1.scale(dist);
    posVec.add(tmp1);
    isTransformed = true;

  }

  @Override
  public void translateY(float dist)
  {
    Vector3f tmp1 = new Vector3f(upVec);
    tmp1.scale(dist);
    posVec.add(tmp1);
    isTransformed = true;

  }

  @Override
  public void translateZ(float dist)
  {
    Vector3f tmp1 = new Vector3f(viewVec);
    tmp1.scale(-dist);
    posVec.add(tmp1);
    isTransformed = true;

  }
}









