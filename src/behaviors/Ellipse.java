/* Ellipse.java (created on September 13, 2007, 10:32 PM) */

package behaviors;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;

/**
 * This class contains the parametric defintion for an ellipse, or an arc of an ellipse.
 */
public class Ellipse
{
  Point3f centerPt;
  float theta, a, b, minAng, maxAng, rangeAng;
  
  /**
   * Constructor for a full ellipse. <br>
   * a = radius horizontal, <br>
   * b = radius vertical, <br>
   * centerPt = center of ellipse, <br>
   * theta = rotaion of ellipse.
   */
  public Ellipse(Point3f centerPt, float a, float b, float theta)
  {
    this.a = a;
    this.b = b;
    this.centerPt = centerPt;
    this.theta = theta;
    this.minAng = 0f;
    this.maxAng = 360f;
    this.rangeAng = maxAng - minAng;
  }

  /**
   * Constructor for an arc of an ellipse. <br>
   * a = radius horizontal, <br>
   * b = radius vertical, <br>
   * centerPt = center of ellipse, <br>
   * theta = rotaion of ellipse, <br>
   * minAng = start angle of ellipse, <br>
   * maxAng = end angle of ellipse. <br> 
   * Use minAng = 0f, maxAng = 360f for full ellipse (or the other constructor).
   */
  public Ellipse(Point3f centerPt, float a, float b, float theta, float minAng, float maxAng)
  {
    this.a = a;
    this.b = b;
    this.centerPt = centerPt;
    this.theta = theta;
    this.minAng = minAng;
    this.maxAng = maxAng;
    this.rangeAng = maxAng - minAng;
  }

  /**
   * This method returns the points at their actual position.
   * For a list of points where each point is described as an offset from 
   * a previous point, use getVerticesRelative(). 
   * <br>
   * resolution = number of points.
   */
  public List<Point3f> getVerticesAbsolute(int resolution)
  {
    List<Point3f> returnPts = new ArrayList<Point3f>();

    float t;
    
    float inc = (float)Math.toRadians(rangeAng) / (float)resolution;
    for (int i = 0; i < resolution; i++)
    {
      t = minAng + (i * inc);
      
      float x = (float) (centerPt.x + (a * Math.cos(t) * Math.cos(theta)) 
        - (b * Math.sin(t) * Math.sin(theta)) );
      float y = (float) (centerPt.y + (a * Math.cos(t) * Math.sin(theta))
        + (b * Math.sin(t) * Math.cos(theta)) );
    }

    return returnPts;
  }

  /**
   * This is the version of getVertices that is used for time dependent paths.
   * Using offsets is necessary because the path may be influenced
   * by other behaviors, etc.
   */
  public List<Point3f> getVerticesRelative(int resolution)
  {
    List<Point3f> returnPts = new ArrayList<Point3f>();

    float t;
    Point3f prevPt = null;
    
    float inc = (float)Math.toRadians(rangeAng) / (float)resolution;
    for (int i = 0; i < resolution; i++)
    {
      t = minAng + (i * inc);
      
      float x = (float) (centerPt.x + (a * Math.cos(t) * Math.cos(theta)) 
        - (b * Math.sin(t) * Math.sin(theta)) );
      float y = (float) (centerPt.y + (a * Math.cos(t) * Math.sin(theta))
        + (b * Math.sin(t) * Math.cos(theta)) );
    
      if (i == 0)
      {
        returnPts.add(new Point3f(0f, 0f, 0f));
      }
      else
      {
        returnPts.add(new Point3f(x - prevPt.x, y - prevPt.y, 0f));
      }

      prevPt = new Point3f(x, y, 0f);
     
    }

    return returnPts;
  }

  /** 
   * Input two angles (presumably close to each other) and places start and end point
   * of a tangent line into (x1, y1) and (x2, y2). You will proably need to extend this line
   * in order to use for something. Or I guess we could just return the slope?
   */
  public void getTangentLine(float ang1, float ang2, float x1, float y1, 
          float x2, float y2)
  {
      x1 = (float) (centerPt.x + (a * Math.cos(ang1) * Math.cos(theta)) 
        - (b * Math.sin(ang1) * Math.sin(theta)) );
      y1 = (float) (centerPt.y + (a * Math.cos(ang1) * Math.sin(theta))
        + (b * Math.sin(ang1) * Math.cos(theta)) );
      
      x2 = (float) (centerPt.x + (a * Math.cos(ang2) * Math.cos(theta)) 
        - (b * Math.sin(ang2) * Math.sin(theta)) );
      y2 = (float) (centerPt.y + (a * Math.cos(ang2) * Math.sin(theta))
        + (b * Math.sin(ang2) * Math.cos(theta)) );
  }
  
  //add method to caluclate arc/ellipse length
}
