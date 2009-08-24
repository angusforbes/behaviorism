/*
 * Corner.java
 * Created on March 27, 2007, 2:18 PM
 */

package behaviorism. algorithms;

import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Corner
{
  final public static int NORTHWEST = 0;
  final public static int NORTHEAST = 1;
  final public static int SOUTHWEST = 2;
  final public static int SOUTHEAST = 3;
  
  final public static int NW = 0;
  final public static int NE = 1;

  final public static int EN = 2;
  final public static int ES = 3;
  final public static int SE = 4;
  final public static int SW = 5;
  final public static int WN = 6;
  final public static int WS = 7;
  
  
  //public Point p = null;
  public Point2D p;
  public Point2D mark = null;
  public float markSpace = .03f;
  public int type = -1;
  public int location = -1;
  public double distToCenter = -1;
  public String typeStr = "";
  public String locationStr = "";
  
  //public Rectangle2D.Float r = null; //available rectangle
  public java.util.List<Rectangle2D> possibleRectangles = new ArrayList<Rectangle2D>();
  //public java.util.List<Rectangle> possibleRectangles = new ArrayList<Rectangle>();
  
  public java.util.List<Dot> xdots = new ArrayList<Dot>();
  public java.util.List<Dot> ydots = new ArrayList<Dot>();
  public java.util.List<Dot> dots = new ArrayList<Dot>();
  
  //type is the *direction* the corner is facing
  //location is the *position* on the rectangle
  
  //there are 4 possible directions
  //there are 8 unique posiitons 
  public Corner(Point2D p, int type, int location)
  {
    this.p = p;
    this.type = type;
    this.location = location;
    this.locationStr = printCornerLocation();
    switch(this.type)
    {
      case NORTHEAST:
        this.mark = new Point2D.Double(p.getX() + markSpace, p.getY() + markSpace);
        this.typeStr = "NORTH EAST";
        break;
      case NORTHWEST:
        this.mark = new Point2D.Double(p.getX() - markSpace, p.getY() + markSpace);
        this.typeStr = "NORTH WEST";
        break;
      case SOUTHEAST:
        this.mark = new Point2D.Double(p.getX() + markSpace, p.getY() - markSpace);
        this.typeStr = "SOUTH EAST";
        break;
      case SOUTHWEST:
        this.mark = new Point2D.Double(p.getX() - markSpace, p.getY() - markSpace);
        this.typeStr = "SOUTH WEST";
        break;
    }
    //this.distToCenter = getDistToCenter();
  }
 
  public void addRectangle(Rectangle2D rect)
  {
    possibleRectangles.add(rect);
  }
//  public void addRectangle(Rectangle rect)
//  {
//    possibleRectangles.add(rect);
//  }
  
  
  public static void sortCornersByClosenessToPoint(final Point2D p, List<Corner> corners)
  {
    Collections.sort(corners, new Comparator()
    {
      public int compare(Object a, Object b)
      {
        
        Corner c1 = (Corner)a;
        Corner c2 = (Corner)b;
        
        double dist1 = p.distance(c1.p);
        double dist2 = p.distance(c2.p);
        
        if (dist1 < dist2)
        {
          return -1;
        }
        else if (dist1 > dist2)
        {
          return 1;
        }
        else
        {
          return 0;
        }
      }
    });
  }
  
  
  public double getDistToCenter()
  {
    //Point2D.Float centerp = RectangleFloat.centerPoint;
    //return Math.sqrt( Math.pow(centerp.getX() - p.getX(), 2) + Math.pow(centerp.getY() - p.getY(), 2) );
    return 0.0;
  }
 
  public String printCornerType()
  {
    switch(type)
    {
      case NORTHWEST:
        return "NW";
      case NORTHEAST:
        return "NE";
      case SOUTHWEST:
        return "SW";
      case SOUTHEAST:
        return "SE";
      default:
        return "Bad Corner Type!";
    }
  }
  
  public String printCornerLocation()
  {
    switch(location)
    {
      case NW:
        return "NW";
      case NE:
        return "NE";
      case EN:
        return "EN";
      case ES:
        return "ES";
      case SW:
        return "SW";
      case SE:
        return "SE";
      case WN:
        return "WN";
      case WS:
        return "WS";
      default:
        return "Bad Corner Location!";
    }
  }
  public String toString()
  {
    return "" + p + " type:" + typeStr + " loc:" + locationStr + " distToCenter:" + distToCenter;
  }
}
