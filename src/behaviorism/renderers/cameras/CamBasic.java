/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package behaviorism.renderers.cameras;

import behaviorism.utils.MatrixUtils;
import javax.vecmath.Point3f;
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
    //initialize(new Point3f(0f, 0f, 0f));
  }

  public void initialize(Point3f anchorPt)
  {
    this.translate = new Point3f(anchorPt);
    //this.rotateAnchor = new GeomPoint(0f, 0f, 0f);
    this.rotateAnchor = new Point3f(0f, 0f, 0f);
    this.resetAnchor = new Point3f(anchorPt);

    //modelview = MatrixUtils.translate(modelview, translate.x, translate.y, -translate.z);


    setViewPlanes(1.0, 100.0);
    setFovy(45.0);
    //setFovy(60.0);
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
  public void view()
  {
    if (isTransformed == true)
    {
      //System.out.println("in camBasic : view()");
      modelview = MatrixUtils.getIdentity();
      modelview = MatrixUtils.rotate(modelview, rotate.x, 1.0f, 0.0f, 0.0f);
      modelview = MatrixUtils.rotate(modelview, rotate.y, 0.0f, 1.0f, 0.0f);
      modelview = MatrixUtils.rotate(modelview, rotate.z, 0.0f, 0.0f, 1.0f);
      modelview = MatrixUtils.translate(modelview, translate.x, translate.y, -translate.z);
      //return modelview;

    //  MatrixUtils.printMatrix(getCamera().modelview);
    //  System.out.println("...");
    }

    /*
    public void rotateX(float x)
    {
    modelview = MatrixUtils.rotate(modelview, x, 1.0f, 0.0f, 0.0f);
    isTransformed = true;
    }

    public void rotateY(float y)
    {
    modelview = MatrixUtils.rotate(modelview, y, 0.0f, 1.0f, 0.0f);
    isTransformed = true;}

    public void rotateZ(float z)
    {
    modelview = MatrixUtils.rotate(modelview, z, 0.0f, 0.0f, 1.0f);
    isTransformed = true;
    }
     */
  }

}
