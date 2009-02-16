/* GeomFixedVector.java ~ Aug 20, 2008 */
package geometry;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/**
 * GeomFixedVector adds both end points of a GeomSimpleLine to the scene graph so that child Geoms may be appended
 * to either end of the line. (These end points are by default not active, and thus not displayed). 
 * The first end point is considered fixed, and defined by the anchor point,
 * while the other end is unfixed, and is defined only by an angle and radius in relation to the fixed end point.
 * And each loop of the display loop the position of the unfixed endpoint is recalculated. Thus, a simple behavior
 * can be attached to the angle and/or radius of the GeomFixedVector to control the direction and magnitude
 * of the vector around a particular origin point.
 * 
 * Only 2D handled right now...
 * @author angus
 */
public class GeomFixedVector extends GeomSimpleLine
{
  public float angle;
  public float radius;

  public GeomFixedVector(Point3f anchor, float angle, float radius)
  {
    this.angle=angle;
    this.radius=radius;

    this.anchor = anchor;
    //ends[0] = new GeomPoint(anchor);
    ends[0] = new GeomPoint();
    ends[1] = new GeomPoint(); //unfixed point
  
    addGeom(ends[0], false);
    addGeom(ends[1], false);


  }

  public void updateVector(float angle, float radius)
  {
    this.angle = angle;
    this.radius = radius;
  }

  protected void calculateUnfixedPoint()
  {
    ends[1].anchor.x = (float) (Math.cos(Math.toRadians(this.angle)) * this.radius);
    ends[1].anchor.y = (float) (Math.sin(Math.toRadians(this.angle)) * this.radius);
    //ends[1].anchor.z = ends[0].anchor.z;
  }

  @Override
  public void draw(GL gl, GLU glu, float offset)
  {
    calculateUnfixedPoint();
    super.draw(gl, glu, offset);
  }

  @Override
  public String toString()
  {
    return super.toString() + ", angle=" + this.angle + ", radius=" + this.radius + "\n";
  }

}
