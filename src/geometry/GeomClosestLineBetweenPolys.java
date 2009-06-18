/* GeomClosestLineBetweenPolys.java ~ Aug 25, 2008 */
package geometry;

import behaviorism.BehaviorismDriver;
import com.bric.geom.BasicShape;
import geometry.GeomLine;
import geometry.GeomRect;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import utils.GeomUtils;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class GeomClosestLineBetweenPolys extends GeomLine
{
  boolean rectsIntersect = false;
  boolean isFirstTime = true;

  public GeomClosestLineBetweenPolys(GeomRect firstGeom, GeomRect secondGeom)
  {
    super(firstGeom, secondGeom);

    ////handle the transforms ourselves...
   // this.firstGeom.addGeom(this.firstOffsetPt, true);
   // this.secondGeom.addGeom(this.secondOffsetPt, true);
  }

  public GeomClosestLineBetweenPolys(GeomRect firstGeom, GeomRect secondGeom, boolean activateEndPoints)
  {
    super(firstGeom, secondGeom, activateEndPoints);

    //we handle the transforms ourselves...
  //  this.firstGeom.addGeom(this.firstOffsetPt, true);
  //  this.secondGeom.addGeom(this.secondOffsetPt, true);
  //  this.activateEndPoints = activateEndPoints;
  }

  public void updatePoints()
  {
    //a. get all points in screen coords
    //b. find intersection pts in pixels
    //c. transform intersection pts to absolute points
    //d. then we are ready to draw them

    Path2D.Float p1 = BehaviorismDriver.renderer.getScreenShapeForWorldCoords(firstGeom);
    Path2D.Float p2 = BehaviorismDriver.renderer.getScreenShapeForWorldCoords(secondGeom);

    //using BasicShape library for fast checking polygon intersection...
    BasicShape bs1 = new BasicShape(p1);
    BasicShape bs2 = new BasicShape(p2);
    
    if (bs1.intersects(bs2))
    {
      firstOffsetPt.isVisible = false;
      secondOffsetPt.isVisible = false;
      rectsIntersect = true;
      //return;
    }
    else
    {
      if (activateEndPoints)
      {
        firstOffsetPt.isVisible = true;
        secondOffsetPt.isVisible = true;
      }
    }
    
    /*
    Rectangle2D r1 = p1.getBounds2D();
    Rectangle2D r2 = p2.getBounds2D();
    
    System.out.println("does r1 (" + r1 + ") intersect r2  ("+r2+")?");
    if (r1.intersects(r2))
    {
      System.out.println("yes!");
    //if they intersect, then we don't need to draw the closest line...
    firstOffsetPt.isVisible = false;
    secondOffsetPt.isVisible = false;
    rectsIntersect = true;
    return;
    }
     System.out.println("no...");
    */
    rectsIntersect = false;

    //firstOffsetPt.isVisible = true;
    //secondOffsetPt.isVisible = true;

    Point2D pt1 = GeomUtils.centerOfMass(
      GeomUtils.getPointsFromPath2D(p1).toArray(new Point2D[0]));
    Point2D pt2 = GeomUtils.centerOfMass(
      GeomUtils.getPointsFromPath2D(p2).toArray(new Point2D[0]));

    Line2D line = new Line2D.Float(pt1, pt2);
    
    Point2D r1_ip = GeomUtils.getIntersectionBetweenLineAndPolygon(line, p1);
    Point2D r2_ip = GeomUtils.getIntersectionBetweenLineAndPolygon(line, p2);

    if (r1_ip == null)
    {
      //System.out.println("r1_ip is null...");
      return;
    }
    if (r2_ip == null)
    {
      //System.out.println("r2_ip is null...");
      return;
    }
   
    firstOffsetPt.setPos(MatrixUtils.toPoint3f(
      BehaviorismDriver.renderer.rayIntersect(
        (firstOffsetPt), (int) r1_ip.getX(), (int) r1_ip.getY())
      ) 
    );
    secondOffsetPt.setPos(MatrixUtils.toPoint3f(
      BehaviorismDriver.renderer.rayIntersect(
        (secondOffsetPt), (int) r2_ip.getX(), (int) r2_ip.getY()
      ))
    );
  }

//  @Override public void transform(GL gl, GLU glu)
//  {
//    super.transform(gl, glu);
//    firstOffsetPt.transform(gl, glu);
//    secondOffsetPt.transform(gl, glu);
//
//    updatePoints();
//  }
//  
  @Override
  public void draw(GL gl)
  {
    //firstOffsetPt.transform(gl, glu);
    //secondOffsetPt.transform(gl, glu);
    updatePoints();
    
    if (rectsIntersect == true)
    {
      return;
    }

    if (isFirstTime == true)
    {
      isFirstTime = false;
      return;
    }
    //super.draw(gl, glu, offset);
    super.draw(gl);


    /*
    if (rectsIntersect)
    {
      return;
    }

    Point3f k1 = firstOffsetPt.anchor;
    Point3f k2 = secondOffsetPt.anchor;
      
    gl.glLineWidth(this.lineWidth);

    gl.glColor4f(r, g, b, a);
    gl.glBegin(gl.GL_LINES);

    gl.glVertex3d(k1.x, k1.y, k1.z);
    gl.glVertex3d(k2.x, k2.y, k2.z);

    gl.glEnd();

    if (activateEndPoints == true)
    {
      gl.glPointSize(this.pointSize);

      gl.glBegin(gl.GL_POINTS);
      gl.glVertex3f(k1.x, k1.y, k1.z);
      gl.glVertex3f(k2.x, k2.y, k2.z);
      gl.glEnd();
    }
     */
  }
}
