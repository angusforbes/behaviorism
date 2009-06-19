/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers.cameras;

import geometry.GeomPoint;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class CamOrbit extends Cam
{
	/** 
	 * anchor = the point that you are rotating around and either facing, or facing away from.
	 * rotateAnchor = position of the camera relative to the anchor (in general use a negative z value only)
	 */
	public CamOrbit()
	{
		anchor = new Point3f(0f, 0f, 10f); //lookAt point

		//if the z value of rotateAnchor is negative, then the camera will point TOWARD the object
		//situated at the lookAt point (which it is rotating around). Otherwise is will point 
		//AWAY from that point, and thus will never see it! (maybe that's what you want though). In general,
		//you normally will want to only have use the z value, and make sure it is negative.
		rotateAnchor = new GeomPoint(0f, 0f, -5f); //distance vector
	}

	/** Make sure that the viewDistance is negative if you want the camera to look at the lookAtPoint,
	 * or positive to have the camera's "back" to the lookAtPoint.
	 * @param lookAtPoint the point that the camera orbits around
	 * @param viewDistance the distance "in front" of the lookAtPoint (should be negative to look at the lookAtPoint)
	 */
	public CamOrbit(Point3f lookAtPoint, float viewDistance)
	{
		this.anchor = lookAtPoint;
		this.rotateAnchor = new GeomPoint(0f, 0f, viewDistance);
	}
	
	public CamOrbit(float x, float y, float z, float viewDistance)
	{
		this.anchor = new Point3f(x, y, z);
		this.rotateAnchor = new GeomPoint(0f, 0f, viewDistance);
	}

	public CamOrbit(Point3f lookAtPoint, GeomPoint rotateAnchor)
	{
		this.anchor = lookAtPoint;
		this.rotateAnchor = rotateAnchor;
	}

  public void perspective()
  {
    System.out.println("ERROR -- we haven't set up the perspective for CamOrbit!!! TO DO!!!");
    System.exit(0);
  }
  @Override
	public void setPerspective(GL gl, GLU glu)
	{
		gl.glTranslatef(rotateAnchor.anchor.x, rotateAnchor.anchor.y, rotateAnchor.anchor.z);

		gl.glRotatef((float) rotate.x, 1.0f, 0.0f, 0.0f);
		gl.glRotatef((float) rotate.y, 0.0f, 1.0f, 0.0f);
		gl.glRotatef((float) rotate.z, 0.0f, 0.0f, 1.0f);
		
		gl.glTranslatef(anchor.x, anchor.y, anchor.z);
	}

  @Override
	public void changePitch(double degrees)
	{
		rotate.x += degrees;
	}

  @Override
	public void changeHeading(double degrees)
	{
		rotate.y += -degrees;
	}

  @Override
	public void changeYaw(double degrees)
	{
		rotate.z += degrees;
	}

  @Override
	public void moveX(float x)
	{
			//rotateAnchor.anchor.x += x;
	}

  @Override
	public void moveY(float y)
	{
			//rotateAnchor.anchor.y += y;
	}

  @Override
	public void moveZ(float z)
	{
			rotateAnchor.anchor.z += z;
	}
}
