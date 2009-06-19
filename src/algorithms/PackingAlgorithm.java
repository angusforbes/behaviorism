/*
 * PackingAlgorithm.java
 * Created on April 22, 2007, 9:56 AM
 */

package algorithms;

import geometry.GeomRect;
import geometry.text.GeomTextOutset;
import java.util.List;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import utils.GeomUtils;

public class PackingAlgorithm
{
  //variables you might actually want to change
  public float minPossibleRectangleSize = .1f;
  public float flub = .01f; //for floating point errors, prob should make super small
  public List<GeomRect> placedGeomTexts = new ArrayList<GeomRect>();
  
  /* debugging flags */
  boolean debug = false;
  //boolean debug = true;
  
  public static int debugLocation = Corner.NW;
  
  //NW
  public static boolean switchNE = true; //true;
  public static boolean switchWS = true;
  
  //NE
  public static boolean switchNW = true; //false;
  public static boolean switchES = true; //true;
  
  //SE
  public static boolean switchEN = true;
  public static boolean switchSW = true;
  
  //SW
  public static boolean switchSE = true;
  public static boolean switchWN = true;
  
  public static Rectangle2D gTestRect = null;
  public static Rectangle2D gTestRect2 = null;
  public static Rectangle2D gTestRect3 = null;
  public static Point2D gTestPt1 = null;
  public static Point2D gTestPt2 = null;
  /* end debugging flags */
  
  //used internally
  private Rectangle2D boundingBox = null;
  private Path2D.Float boundingPath = null;
  //private List<Line2D> boundingLines = null;
  private List<Line2D> boundingLines = null;
  
  
  public PackingAlgorithm()
  {
  }

  public PackingAlgorithm(Path2D.Float boundingPath)
  {
    initialize(boundingPath);
  }

  public PackingAlgorithm(List<GeomTextOutset> geomTexts, Path2D.Float boundingPath) //make a poly later...
  {
    initialize(geomTexts, boundingPath);
  }
  
  //place them ourselves...
  public void initialize(Rectangle2D boundingRect)
  {
    placedGeomTexts = new ArrayList<GeomRect>();
    
    this.boundingPath = new Path2D.Float(boundingRect);
    Rectangle2D bounds = this.boundingPath.getBounds2D();
    this.boundingBox = new Rectangle2D.Double((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
    this.boundingLines = GeomUtils.getLinesFromPath2D(boundingPath);
  }
  
  public void initialize(Path2D.Float boundingPath)
  {
    placedGeomTexts = new ArrayList<GeomRect>();
    
    this.boundingPath = boundingPath;
    Rectangle2D bounds = this.boundingPath.getBounds2D();
    this.boundingBox = new Rectangle2D.Double((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
    this.boundingLines = GeomUtils.getLinesFromPath2D(boundingPath);
  }
  
  public void initialize(List<GeomTextOutset> geomTexts, Rectangle2D boundingRect)
  {
    initialize(geomTexts, new Path2D.Float(boundingRect));
  }
  public void initialize(List<GeomTextOutset> geomTexts, Path2D.Float boundingPath)
  {
    placedGeomTexts = new ArrayList<GeomRect>();
    
    this.boundingPath = boundingPath;
    Rectangle2D bounds = this.boundingPath.getBounds2D();
    this.boundingBox = new Rectangle2D.Double((float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
    this.boundingLines = GeomUtils.getLinesFromPath2D(boundingPath);
    
      boolean firstTime = true;
    for(GeomTextOutset gt : geomTexts)
    {
      placeGeom(gt, firstTime);
      firstTime = false;
    }
  }
  
  
  
  public boolean placeGeom(GeomRect newGeom, boolean firstTime)
  {
    //System.out.println("in placeGeomi!");
    if (debug)
    {
      if (firstTime)
      {
        /*
        Rectangle2D rectangle = newGeom.makeRectangle2DFromRect();
    
        java.util.List<Corner> newCorners = makeCornersForRectangle(newGeom);
        //Corner closestCorner = findClosestCorner(rectangle, 1200f, .005f); //.005f //max distance, min width&height
        newGeom.setCoordFromRectangle(rectangle); //this updates the visualization

        placedGeomTexts.add(newGeom);
        positionRectangle(rectangle, newGeom.corners.get(0));
  
        newGeom.corners.addAll(newCorners);
    
        adjustCorners(newGeom.rectangle);
    
        return true;
         */
        //System.out.println("THIS IS THE FIRST PHOTO!");
      }
      else
      {
        //System.out.println("second...!");
      }
    }
    Rectangle2D rectangle = newGeom.makeRectangle2DFromRect();
    //Rectangle rectangle = newGeom.makeRectangle2DFromRect();
    
    //does this rectangle intersect an existing rectangle?
    boolean doesPhotoIntersect = false;
    for (GeomRect p : this.placedGeomTexts)
    {
      if (debug) {System.out.println("\t\tchecking a rect...");}
      Rectangle2D intersectRectangle = p.makeRectangle2DFromRect();
      //Rectangle intersectRectangle = p.makeRectangle2DFromRect();
      
      //if (debug) System.out.println("does " + rectangle + " intersect " + intersectRectangle + "?");
      if(rectangle.intersects(intersectRectangle))
      {
        if (debug) System.out.println("yes, this rectangle intersects an existing rectangle...");
        
        doesPhotoIntersect = true;
        break;
      }
    }
    
    //does the bounding box completely contain the photo?
    if (!boundingPath.contains(rectangle))
    {
      //if (debug) System.out.println("yes, this rectangle intersects the bounding box...");
      doesPhotoIntersect = true;
    }
    
    //if (debug) System.out.println("does this photo intersect another photo???");
    if (firstTime != true && doesPhotoIntersect == true)
    {
      //if(debug) System.out.println("yes, this rectangle intersects another photo");
      //find closest corner (within range and with a legitimate possible rectangle
      Corner closestCorner = findClosestCorner(rectangle, 20f, .4f, .4f); //max distance, min width&height
      //Corner closestCorner = findClosestCorner(rectangle, 100f, .0001f); //.005f //max distance, min width&height
      
      if (closestCorner == null && firstTime == false)
      {
        //we failed to find a legitimate corner
        rectangle.setRect(0f, 0f, 0f, 0f);
        if(debug) System.out.println("NULL COULDN'T FIND A MATCH!!!!");
        newGeom.isActive = false;
        newGeom.setCoordFromRectangle(); //this updates the visualization
        //System.exit(0);
        return false;
      }
      
      
      //we know that our rectangle will fit one of the corners
      //rectangles, so position our rectangle against the corner
      //in proper direction
      
      positionRectangle(rectangle, closestCorner);
      
    }
    else
    {
      //if(debug) System.out.println("NO!!!");
    }
    
    //newCoord.setCoordFromRectangle(); //this updates the visualization
    newGeom.setCoordFromRectangle(rectangle); //this updates the visualization
    
    //placedCoordPhotos.add(c);
    
    //check to see if this rectangle interferes with existing possible rectangles, adjust them
    
    //make corners for piece we just made
    //this shoudl NOT have newest coordPhoto in placedCoordPhotos!!!
    java.util.List<Corner> newCorners = makeCornersForRectangle(newGeom);
    
    //add corners and possible rectangles for this rectangle
    removeOverlappingCorners(newCorners, rectangle); //DO WE NEED THIHS??? --we might! verify!
    
    placedGeomTexts.add(newGeom); //this needs to happen before adjustCorners is called
    
    newGeom.corners.clear();
    newGeom.corners.addAll(newCorners);
    
    //verify this...
    //if(debug) System.out.println("before adjustCorners");
    // placedCoordPhotos.add(newCoord); //this needs to happen before adjustCorners is called
    adjustCorners(newGeom.rectangle);
    //if(debug) System.out.println("after adjustCorners");
    
    //gTestRect3 = rectangle;
    
    //if(debug) System.out.printf("current corners size = %d \n", newCorners.size());
  
    return true;
  }
  
  //removes bad current corners & bad exisiting corners
  public void removeOverlappingCorners(List<Corner> newCorners, Rectangle2D newr)
  {
    /**** ADJUST AVAILBLE RECTANGLE FOR EACH CORNER HERE (if not erased completely) ***/
    
    //1. check new corners and make sure that they don't overlap with existing
    //rectangles
    
    //go through each existingPhoto
    for (int i = 0; i < placedGeomTexts.size(); i++)
    {
      Rectangle2D r = placedGeomTexts.get(i).rectangle;
      
      for (int ii = newCorners.size() - 1; ii >= 0; ii--)
      {
        Corner c = newCorners.get(ii);
        
        Point2D m = c.mark;
        //if(r.contains(c.mark))
        //System.out.println("does r (" + r + ") contain mark (" + m + ")?");
        if (r.getX() < m.getX() && r.getX() + r.getWidth() > m.getX() && r.getY() < m.getY() && r.getY() + r.getHeight() > m.getY())
        {
          //System.out.println("YES!");
          newCorners.remove(c);
        }
        
        //This is necessary becuase otherwise
        //this corner is on the edge of box
        //and will erroneously be seen as contained
        //within the box
        //So we remove it.
        
        /**** ANGUS ADD this back in-- make it works with Path2D.Float!!!!
         * /*
         * if (c.mark.y > boundingBox.getY() + boundingBox.getHeight() - flub || c.mark.y < boundingBox.getY() + flub)
         * {
         * newCorners.remove(c);
         * }
         * if (c.mark.x > boundingBox.getX() + boundingBox.getWidth() - flub || c.mark.x < boundingBox.getX() + flub)
         * {
         * newCorners.remove(c);
         * }
         */
      }
    }
    
    //2. check new Rectangle2D.Double and make sure that it doesn't ovelap
    //with existing corners
    for (GeomRect cp : placedGeomTexts)
    {
      for (int i = cp.corners.size() - 1; i >= 0; i--) //these are the global corners
      {
        Corner c = cp.corners.get(i);
        
        Point2D m = c.mark;
        
        if (newr.getX() < m.getX() && newr.getX() + newr.getWidth() > m.getX() 
          && newr.getY() < m.getY() && newr.getY() + newr.getHeight() > m.getY())
        {
          cp.corners.remove(c);
        }
      }
    }
  }
  
  public void adjustCorners(Rectangle2D r)
  {
    //if(debug) System.out.printf("in adjustCorners! \n");
    //loop through all placed photos
    //for each corner get all possible rectangles
    //if the possible rectangle intersects the newly added coordphoto
    //then adjust that corner by recalculating all of its possible rectangles
    for (GeomRect cp : placedGeomTexts)
    {
      for (int i = cp.corners.size() - 1; i >= 0; i--)
      {
        Corner c = cp.corners.get(i);
        
        for (int ii = 0; ii < c.possibleRectangles.size(); ii++)
        {
          Rectangle2D possibleRectangle = c.possibleRectangles.get(ii);
          
          //if there is even one interference, let's just recalculate all possible rectangles for this corner!
          if (possibleRectangle.intersects(r))
          {
            //if(debug) System.out.println("*** INTERSECTS! new rect ("+r+") intersects poss r ("+possibleRectangle+")");
            //recalculate possible rectangles for this corner
            
            //cp.corners.remove(c);
            if (setPossibleRectangles(c) == false)
            {
              //if there are no possible rectangles, remove the corner forever
              cp.corners.remove(c);
            }
            
            break;
          }
        }
      }
    }
  }
  
  public boolean setPossibleRectangles(Corner c)
  {
    if (c.location == debugLocation)
    {
      //if(debug) System.out.println("\n****\nin setPossibleRectangles : location = " + debugLocation);
    }
    
    c.possibleRectangles.clear();
    //Rectangle2D initialPossibleRectangle = null;
    //float xdist, ydist;
    
    c.dots.clear();
    //gTestRect = null;
    gTestRect2 = null;
    gTestRect3 = null;
    
    
    Rectangle2D initialPossibleRectangle = makeInitialPossibleRectanglesFromCorner(c);
    
    //find possible rectangle boundaries
    Rectangle2D intersectRectangle = null;
    //if(debug) System.out.println("There are " + placedGeomTexts.size() + " existing rects on screen");
    for (int i = 0; i < placedGeomTexts.size() ; i++)
    {
      Rectangle2D r = placedGeomTexts.get(i).rectangle;
      
      //the creation of this testRect is required for handling floating point errors
      //warning-- the mehtod createIntersection will return values < 0 if there is no actual intersection!!!
      //thus we test if width & height are actually < 0, and set them to being emp.getY() (that is, 0f)
      intersectRectangle = new Rectangle2D.Double();
      intersectRectangle.setRect(initialPossibleRectangle.createIntersection(r));
      
      //if(debug) System.out.println("testRect = " + intersectRectangle);
      if (intersectRectangle.getWidth() < 0f || intersectRectangle.getHeight() < 0f)
      {
        intersectRectangle.setRect(intersectRectangle.getX(), intersectRectangle.getY(),
          0, 0);
      }
      
      //gTestRect3 = initialPossibleRectangle;
      gTestRect2 = intersectRectangle;
      //gTestRect = r;
      
      //if (c.location == debugLocation)
      {
        if(debug)
        {
          //System.out.println("is flub > w or h of intersecting rect? " + flub + " > "
          //+ intersectRectangle.getWidth() + " || " + intersectRectangle.getHeight() );
        }
      }
      if (intersectRectangle.getWidth() < flub || intersectRectangle.getHeight() < flub)
      {
        //if(debug) System.out.println("yes, so we don't do anything, now look at another existing rect...");
        continue; //then there is NO intersection, look at another photo to see if there is one
      }
      else
      {
        //if(debug) System.out.println("no... so we make new dots...");
      }
      
      //if we are here, we know that the photo intersects with the init rectangle
      //no check to see if this photo also contains the corner.
      if (overlapCorner(r, c)) //does the exisiting rectangle completely overlap this corner?
      {
        //if(debug) System.out.println("overlapping corner!");
        return false; //if yes, indicate that this corner needs to be deleted!
      }
      
      if (c.location == debugLocation)
      {
        //if(debug) System.out.println("adding dots... ");
        //if(debug) System.out.println("we started with : dots.size = " + c.dots.size());
      }
      
      addDotsForIntersectingRectangle(initialPossibleRectangle, intersectRectangle, c);
      
      if (c.location == debugLocation)
      {
        //if(debug) System.out.println("now dots.size = " + c.dots.size());
      }
    }
    
    //make possible rectangles
    if (c.location == debugLocation)
    {
      
      //if(debug) System.out.println("\n before sorting dots... corner " + c);
      //if(debug) Dot.printDots(c.dots, " 1 dots... ");
      
    }
    Dot.sortDotsByClosestToCorner(c.dots);
    
    if (c.location == debugLocation)
    {
      //if(debug) System.out.println("... after sorting, before removeIllegalDots there are " + c.dots.size() + " dots...");
      //if(debug) Dot.printDots(c.dots, " 2 dots... ");
      
    }
    
    Dot.removeIllegalDots(c.dots, flub);
    
    if (c.location == debugLocation)
    {
      //if(debug) System.out.println("... after removeIllegalDots there are now " + c.dots.size());
      //if(debug) Dot.printDots(c.dots, " after... dots... ");
      
    }
    
    Dot.sortDots(c.dots, "x");
    Collections.reverse(c.dots);
    
    if (c.location == debugLocation)
    {
      //if(debug) System.out.println("looping through dots to make possible rectangles...");
    }
    
    for (int i = 0; i < c.dots.size() - 1; i++)
    {
      Dot curDot = c.dots.get(i);
      Dot nextDot = c.dots.get(i + 1);
      
      Rectangle2D possibleRectangle = null;
      
      if (c.type == Corner.NORTHEAST)
      {
        possibleRectangle = new Rectangle2D.Double(initialPossibleRectangle.getX(), initialPossibleRectangle.getY(),
                curDot.xdist, nextDot.ydist);
      }
      else if (c.type == Corner.NORTHWEST)
      {
        possibleRectangle = new Rectangle2D.Double(initialPossibleRectangle.getX() + initialPossibleRectangle.getWidth() - curDot.xdist, initialPossibleRectangle.getY(),
                curDot.xdist, nextDot.ydist);
      }
      else if (c.type == Corner.SOUTHWEST)
      {
        possibleRectangle = new Rectangle2D.Double(initialPossibleRectangle.getX() + initialPossibleRectangle.getWidth() - curDot.xdist, initialPossibleRectangle.getY() + initialPossibleRectangle.getHeight() - nextDot.ydist,
                curDot.xdist, nextDot.ydist);
      }
      else if (c.type == Corner.SOUTHEAST)
      {
        possibleRectangle = new Rectangle2D.Double(initialPossibleRectangle.getX(), initialPossibleRectangle.getY() + initialPossibleRectangle.getHeight() - nextDot.ydist,
                curDot.xdist, nextDot.ydist);
      }
      
      if (c.location == debugLocation)
      {
        //if(debug) System.out.println("adding possible rectangle " + possibleRectangle);
      }
      
      if (possibleRectangle != null)
      {
        List<Rectangle2D> newPossibleRects = new ArrayList<Rectangle2D>();
        
        //a. test to see if it intersects with bounding line of bounding path
        //...if yes break it into smaller pieces so that none of the rectangles go outside
        //the bounds of the boundingPath--
        //by this point it should only intersect with at most ONE bounding line
        //...if no, continue on
        
        //if bounding path completely contains this rectangle, then can just add it
        if (possibleRectangle.getWidth() < (flub * 2f) || possibleRectangle.getHeight() < (flub * 2f))
        {
          //System.out.println("too small to even test...");
          continue;
        }
        if (boundingPath.contains(possibleRectangle.getX() + flub, possibleRectangle.getY() + flub,
                possibleRectangle.getWidth() - (flub * 2f), possibleRectangle.getHeight() - (flub * 2f)) )
          //if (boundingPath.contains(possibleRectangle))
          //if (!boundingPath.intersects(possibleRectangle))
        {
          //System.out.println("yes contains!!!!!!!!!!!!!!!!!!!!!!!!");
          //newPossibleRects.add(possibleRectangle);
          
          if (checkIfNotTooSmall(possibleRectangle))
          {
            c.addRectangle(possibleRectangle); //this adds to the list in Corner: c.possibleRectangles
          }
          
          continue; //don't need to break up this possibleRectangle, continue to next possibleRectangle
        }
        
        newPossibleRects = checkForBoundaryOverlap(c, possibleRectangle);
        
        if (newPossibleRects.size() == 0)
        {
          //System.out.println("ERROR : m.getY()be-- why couldn't we break up possibleRectangle???");
          continue;
        }
        
        //b. make sure possible rectangle(s) are not too small
        for (Rectangle2D r2f : newPossibleRects)
        {
          if (checkIfNotTooSmall(r2f))
          {
            c.addRectangle(r2f); //this adds to the list in Corner: c.possibleRectangles
          }
        }
      }
    }
    if (c.location == debugLocation)
    {
      //if(debug) System.out.println("this corner has " + c.possibleRectangles.size() + " possibleRectangles");
    }
    
    if (c.location != debugLocation)
    {
      c.dots.clear();
      //gTestRect = null;
      gTestRect2 = null;
      gTestRect3 = null;
    }
    
    if (c.location == debugLocation)
    {
      //if(debug) System.out.println("HOW MANY POSSIBLE RECTS ARE THERE? " + c.possibleRectangles.size());
    }
    if (c.possibleRectangles.size() > 0)
    {
      return true;
    }
    return false; //no possibleRectangles for this corner
  }
  
  public Corner findClosestCorner(Rectangle2D r, float maxDist, float minWidth, float minHeight)
  //public Corner findClosestCorner(Rectangle r, float maxDist, float minSide)
  {
    Corner closestCorner = null;
    
    List<Corner> corners = new ArrayList<Corner>();
    for (GeomRect cp : placedGeomTexts)
    {
      corners.addAll(cp.corners);
    }
   
    
    if(debug) System.out.println("in findClosestCorner! total available corners to check : " + corners.size());
    
    Point2D centerPoint = new Point2D.Double((float)r.getCenterX(), (float)r.getCenterY());
    Corner.sortCornersByClosenessToPoint(centerPoint, corners);
  
    //acutally let's unsort them now! (test)
    //Collections.shuffle(corners);
    
    for (Corner c : corners)
    {
      if (centerPoint.distance(c.p) > maxDist)
      {
        //temp-- not shrinking
        //return null;
        
        break;
      }
      for (Rectangle2D cr : c.possibleRectangles)
      //for (Rectangle cr : c.possibleRectangles)
      {
        if (cr.getWidth() > r.getWidth() && cr.getHeight() > r.getHeight())
        {
          if(debug) System.out.printf("incoming w/h = %f/%f, available w/h = %f/%f \n", r.getWidth(), r.getHeight(), cr.getWidth(), cr.getHeight());
          
          return c;
          //return null;
        }
      }
    }

    //YES THERE IS A PROBLEM BELOW!!! SHRINKING IS NOT PERFECT!!!!
    //as of 08/31/2008 !!!
    
    //okay if we are here, we didn't find a legit corner
    //so let's loop through again, this time trying to shrink it
    //making sure that the min width and height are >= minSide
    
    //TO DO!!! ANGUS-- this part isn't working!
    //need to set a range for legal shrinking, etc
    for (Corner c : corners)
    {
      if (centerPoint.distance(c.p) > maxDist)
      {
        //if(debug) System.out.println("too far away...");
        break;
      }
      
      for (Rectangle2D cr : c.possibleRectangles)
      {
        float crh = (float)cr.getHeight();
        float rh = (float)r.getHeight();
        float crw = (float)cr.getWidth();
        float rw = (float)r.getWidth();
        
        //first: is this possibleRectangle actually too small?
        if (crh < minHeight || crw < minWidth)
        {
          continue;
        }
        
        //shrink to fit... if possible
        

        float scale = 1f;
        
        if (crw > rw)
        {
          //then the height must be too small, else it would have fit already!
          
          scale = crh/rh;
        }
        else
        {
          scale = crw/rw;
        }
        
        if (scale * rw <= crw && scale * rh <= crh)
        {
          r.setRect(r.getX(), r.getY(), scale * rw, scale * rh); 
          //r.setWidth(scale * rw);
          //r.setHeight(scale * rh);
          return c;
        }
        
        //resize incoming rectangle!!!
        //if(debug) System.out.println("tried with scale... " + scale);
        //return corner
      }
    }
    
    //if we are here, then we couldn't even shrink to fit...
    return null;
  }
  
  
  public void positionRectangle(Rectangle2D r, Corner c)
  //public void positionRectangle(Rectangle r, Corner c)
  {
    Point2D p = c.p;
    if (c.type == Corner.NORTHWEST)
    {
      r.setRect(p.getX() - r.getWidth(), p.getY(), r.getWidth(), r.getHeight());
    }
    else if (c.type == Corner.NORTHEAST)
    {
      r.setRect(p.getX(), p.getY(), r.getWidth(), r.getHeight());
    }
    else if (c.type == Corner.SOUTHEAST)
    {
      r.setRect(p.getX(), p.getY() - r.getHeight(), r.getWidth(), r.getHeight());
    }
    else if (c.type == Corner.SOUTHWEST)
    {
      r.setRect(p.getX() - r.getWidth(), p.getY() - r.getHeight(), r.getWidth(), r.getHeight());
    }
  }
  
  
  //does the photo rectangle overl this corner completely?
  //pr = photo's rectangle,
  public boolean overlapCorner(Rectangle2D pr, Corner c)
  {
    
    boolean overlaps = false;
    switch(c.type)
    {
      case Corner.NORTHEAST:
        if (pr.getX() <= c.p.getX() + flub && pr.getY() <= c.p.getY() + flub)
          return true;
        break;
      case Corner.SOUTHEAST:
        if (pr.getX() <= c.p.getX() + flub && pr.getY() + pr.getHeight() >= c.p.getY() - flub) //in fact check to see if less than minarea, if so REMOVE CORNER
          return true;
        break;
      case Corner.NORTHWEST:
        if (pr.getX() + pr.getWidth() >= c.p.getX() - flub && pr.getY() <= c.p.getY() + flub)
          return true;
        break;
      case Corner.SOUTHWEST:
        if (pr.getX() + pr.getWidth() >= c.p.getX() - flub &&  pr.getY() + pr.getHeight() >= c.p.getY() - flub)
          return true;
        break;
      default:
        //if(debug) System.out.println("in overlapCorner() : error : bad Corner type!");
        return false;
    }
    
    return false;
  }
  
  
  public void addDotsForIntersectingRectangle(Rectangle2D cornerRect, Rectangle2D intersectRect, Corner c)
  {
    //System.out.println("******in addDotsForIntersectingRectangle");
    float xdist, ydist;
    
    if (c.type == Corner.SOUTHEAST) //get northmost and westmost lines
    {
      xdist = (float) Math.abs(c.p.getX() - intersectRect.getX());
      ydist = (float) Math.abs(c.p.getY() - (intersectRect.getY() + intersectRect.getHeight()) );
      c.dots.add(new Dot(intersectRect.getX(), intersectRect.getY() + intersectRect.getHeight(), xdist, ydist));
    }
    else if (c.type == Corner.NORTHEAST) //get southmost and westmost lines
    {
      xdist = (float) Math.abs(c.p.getX() - intersectRect.getX());
      ydist = (float) Math.abs(c.p.getY() - (intersectRect.getY()));
      c.dots.add(new Dot(intersectRect.getX(), intersectRect.getY(), xdist, ydist));
    }
    else if (c.type == Corner.NORTHWEST) //get southmost and eastmost lines
    {
      xdist = (float) Math.abs(c.p.getX() - (intersectRect.getX() + intersectRect.getWidth()));
      ydist = (float) Math.abs(c.p.getY() - (intersectRect.getY()));
      c.dots.add(new Dot(intersectRect.getX() + intersectRect.getWidth(), intersectRect.getY(), xdist, ydist));
    }
    else if (c.type == Corner.SOUTHWEST) //get southmost and eastmost lines
    {
      xdist = (float) Math.abs(c.p.getX() - (intersectRect.getX() + intersectRect.getWidth()));
      ydist = (float) Math.abs(c.p.getY() - (intersectRect.getY() + intersectRect.getHeight()));
      c.dots.add(new Dot(intersectRect.getX() + intersectRect.getWidth(), intersectRect.getY() + intersectRect.getHeight(), xdist, ydist));
    }
  }
  
  public java.util.List<Corner> makeCornersForRectangle(GeomRect cp)
  {
    Rectangle2D r = cp.rectangle;
    java.util.List<Corner> cs = new ArrayList<Corner>();
    Corner c;
    
    //north east corners (top-left, right-bottom)
    if (switchNW == true)
    {
      c = new Corner(new Point2D.Double(r.getX(), r.getY() + r.getHeight()), Corner.NORTHEAST, Corner.NW);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }
    if (switchES == true)
    {
      c = new Corner(new Point2D.Double(r.getX() + r.getWidth(), r.getY()), Corner.NORTHEAST, Corner.ES);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }
    
    //north west corners (top-right, left-bottom)
    if (switchNE == true)
    {
      c = new Corner(new Point2D.Double(r.getX() + r.getWidth(), r.getY() + r.getHeight()), Corner.NORTHWEST, Corner.NE);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }
    if (switchWS == true)
    {
      c = new Corner(new Point2D.Double(r.getX(), r.getY()), Corner.NORTHWEST, Corner.WS);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }

    //south east corners (right-top, bottom-left)
    if (switchEN == true)
    {
      c = new Corner(new Point2D.Double(r.getX() + r.getWidth(), r.getY() + r.getHeight()), Corner.SOUTHEAST, Corner.EN);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }
    if (switchSW == true)
    {
      
      c = new Corner(new Point2D.Double(r.getX(), r.getY()), Corner.SOUTHEAST, Corner.SW);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }

    //south west corners (bottom-right, left-top)
    if (switchSE == true)
    {
      c = new Corner(new Point2D.Double(r.getX() + r.getWidth(), r.getY()), Corner.SOUTHWEST, Corner.SE);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }
    if (switchWN == true)
    {
      c = new Corner(new Point2D.Double(r.getX(), r.getY() + r.getHeight()), Corner.SOUTHWEST, Corner.WN);
      if (setPossibleRectangles(c))
      {
        cs.add(c);
        cp.corners.add(c);
      }
    }
    
    return cs;
  }
  
  //1. find intersections of initialPossibleRectangle with lines from bounding path
  //2. find all points of lines within initialPossibleRectangle
  private Rectangle2D makeInitialPossibleRectanglesFromCorner(Corner c)
  //ANGUS TO DO-- add flubs to all w/h of lineV, lineH!!! (only done it for NORTHEAST so far
  {
    //System.out.println("********** in makeInitialPossibleRectanglesFromCorner");
    Rectangle2D initialPossibleRectangle = null;
    float xdist, ydist;
    
    if (c.type == Corner.NORTHEAST)
    {
      initialPossibleRectangle = new Rectangle2D.Double(c.p.getX(), c.p.getY(),
              boundingBox.getX() + boundingBox.getWidth() - c.p.getX(),  boundingBox.getY() + boundingBox.getHeight() - c.p.getY());
      
      Line2D lineH = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX() + initialPossibleRectangle.getWidth() + flub, c.p.getY());
      Line2D lineV = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX(), c.p.getY() + initialPossibleRectangle.getHeight() + flub);
      
      //xdist = initialPossibleRectangle.getWidth();
      //ydist = initialPossibleRectangle.getHeight();
      
      //c.dots.add(new Dot(initialPossibleRectangle.getX() + initialPossibleRectangle.getWidth(), initialPossibleRectangle.getY(), xdist, 0f));
      //c.dots.add(new Dot(initialPossibleRectangle.getX(), initialPossibleRectangle.getY() + initialPossibleRectangle.getHeight(), 0f, ydist));
      
      //for (Line2D line : boundingLines)
      for (Line2D line : boundingLines)
      {
        Point2D pt1 = line.getP1();
        if (initialPossibleRectangle.contains(pt1))
        {
          float ptX = (float)pt1.getX();
          float ptY = (float)pt1.getY();
          
          //System.out.printf("\n*** 1. does this dot = corner? dot: %f,%f, crn: %f, %f\n", ptX, ptY, c.p.getX(), c.p.getY());
          if (ptX != c.p.getX() || ptY != c.p.getY())
          {
            // System.out.println("NO add it...");
            
            c.dots.add(new Dot(ptX, ptY, ptX - c.p.getX(), ptY - c.p.getY()));
          }
        }
        
        Point2D intersectingPt;
        
        if (debug)
        {
          //System.out.printf("does line(%f,%f / %f,%f) intersect lineH(%f,%f / %f,%f) ?\n",
          //line.x1, line.y1, line.x2, line.y2, lineH.x1, lineH.y1, lineH.x2, lineH.y2);
        }
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineH, line);
        
        if (intersectingPt != null)
        {
          //if (debug) System.out.println("YES IT DOES!");
          xdist = (float)(intersectingPt.getX() - c.p.getX());
          
          //System.out.printf("\n 2 ***does this dot = corner? dot: %f,%f, crn: %f, %f\n",
          //intersectingPt.getX(), intersectingPt.getY(), c.p.getX(), c.p.getY());
          if (intersectingPt.getX() != c.p.getX() || intersectingPt.getY() != c.p.getY())
          {
            //System.out.println("NO add it...");
            
            c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), xdist, 0f));
          }
        }
        else
        {
          //if (debug) System.out.println("Nope...");
        }
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineV, line);
        
        if (debug)
        {
          //System.out.printf("does line(%f,%f / %f,%f) intersect lineV(%f,%f / %f,%f) ? ",
          //line.x1, line.y1, line.x2, line.y2, lineV.x1, lineV.y1, lineV.x2, lineV.y2);
        }
        
        if (intersectingPt != null)
        {
          //if (debug) System.out.println("YES IT DOES!");
          ydist = (float)(intersectingPt.getY() - c.p.getY());
          
          //System.out.printf("\n 3 ***does this dot = corner? dot: %f,%f, crn: %f, %f\n",
          //intersectingPt.getX(), intersectingPt.getY(), c.p.getX(), c.p.getY());
          if (intersectingPt.getX() != c.p.getX() || intersectingPt.getY() != c.p.getY())
          {
            //System.out.println("NO add it...");
            c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), 0f, ydist));
          }
          
          
        }
        else
        {
          //if (debug) System.out.println("nope...");
        }
        
      }
    }
    else if (c.type == Corner.NORTHWEST)
    {
      initialPossibleRectangle = new Rectangle2D.Double(boundingBox.getX(), c.p.getY(),
              c.p.getX() - boundingBox.getX(),  boundingBox.getY() + boundingBox.getHeight() - c.p.getY());
      
      //Line2D lineH = new Line2D.Double(boundingBox.getX() - flub, c.p.getY(), boundingBox.getX() + initialPossibleRectangle.getWidth() + (flub*2f), c.p.getY());
      Line2D lineH = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX() - initialPossibleRectangle.getWidth() - flub, c.p.getY());
      //Line2D lineV = new Line2D.Double(boundingBox.getX() + initialPossibleRectangle.getWidth() , c.p.getY(), boundingBox.getX() + initialPossibleRectangle.getWidth(), c.p.getY() + initialPossibleRectangle.getHeight() + flub);
      Line2D lineV = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX(), c.p.getY() + initialPossibleRectangle.getHeight() + flub);
      
      /*
      xdist = initialPossibleRectangle.getWidth();
      ydist = initialPossibleRectangle.getHeight();
       
      c.dots.add(new Dot(initialPossibleRectangle.getX(), initialPossibleRectangle.getY(), xdist, 0f));
      c.dots.add(new Dot(initialPossibleRectangle.getX() + initialPossibleRectangle.getWidth(), initialPossibleRectangle.getY()  + initialPossibleRectangle.getHeight(), 0f, ydist));
       */
      
      //for (Line2D line : boundingLines)
      for (Line2D line : boundingLines)
      {
        Point2D pt1 = line.getP1();
        if (initialPossibleRectangle.contains(line.getP1()))
        {
          float ptX = (float)pt1.getX();
          float ptY = (float)pt1.getY();
          
          c.dots.add(new Dot(ptX, ptY, c.p.getX() - ptX, ptY - c.p.getY()));
        }
        
        
        Point2D intersectingPt;
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineH, line);
        
        if (intersectingPt != null)
        {
          xdist = (float)(c.p.getX() - intersectingPt.getX());
          c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), xdist, 0f));
        }
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineV, line);
        
        if (intersectingPt != null)
        {
          ydist = (float)(intersectingPt.getY() - c.p.getY());
          c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), 0f, ydist));
        }
      }
      
      
    }
    else if (c.type == Corner.SOUTHEAST)
    {
      //initRect = new Rectangle2D.Double(c.p.getX(), c.p.getY(), box.x + box.width - c.p.getX(), box.y + box.height - c.p.getY());
      initialPossibleRectangle = new Rectangle2D.Double(c.p.getX(), boundingBox.getY(), boundingBox.getX() + boundingBox.getWidth() - c.p.getX(),  c.p.getY() - boundingBox.getY());
      
      Line2D lineH = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX() + initialPossibleRectangle.getWidth() + flub, c.p.getY());
      Line2D lineV = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX(), c.p.getY() - initialPossibleRectangle.getHeight() - flub);
      
      
      
      /*
      xdist = initialPossibleRectangle.getWidth();
      ydist = initialPossibleRectangle.getHeight();
       
      c.dots.add(new Dot(initialPossibleRectangle.getX() + initialPossibleRectangle.getWidth(), initialPossibleRectangle.getY() + initialPossibleRectangle.getHeight(), xdist, 0f));
      c.dots.add(new Dot(initialPossibleRectangle.getX(), initialPossibleRectangle.getY(), 0f, ydist));
       */
      
      //for (Line2D line : boundingLines)
      for (Line2D line : boundingLines)
      {
        
        Point2D pt1 = line.getP1();
        if (initialPossibleRectangle.contains(line.getP1()))
        {
          float ptX = (float)pt1.getX();
          float ptY = (float)pt1.getY();
          
          c.dots.add(new Dot(ptX, ptY, ptX - c.p.getX(), c.p.getY() - ptY));
          //System.out.println("SE dot 1");
        }
        
        
        Point2D intersectingPt;
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineH, line);
        if (debug)
        {
          //System.out.printf("does line(%f,%f / %f,%f) intersect lineH(%f,%f / %f,%f) ?\n",
          //line.x1, line.y1, line.x2, line.y2, lineH.x1, lineH.y1, lineH.x2, lineH.y2);
        }
        if (intersectingPt != null)
        {
          //System.out.println("yes...");
          xdist = (float)(intersectingPt.getX() - c.p.getX());
          c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), xdist, 0f));
          //System.out.println("SE dot 2");
        }
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineV, line);
        if (debug)
        {
          //System.out.printf("does line(%f,%f / %f,%f) intersect lineV(%f,%f / %f,%f) ? \n",
          //line.x1, line.y1, line.x2, line.y2, lineV.x1, lineV.y1, lineV.x2, lineV.y2);
        }
        
        if (intersectingPt != null)
        {
          //System.out.println("yes...");
          //ydist = intersectingPt.getY() - c.p.getY();
          ydist = (float)(c.p.getY() - intersectingPt.getY() );
          c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), 0f, ydist));
          //System.out.println("SE dot 3");
        }
      }
      //Dot.printDots(c.dots);
    }
    else if (c.type == Corner.SOUTHWEST)
    {
      initialPossibleRectangle = new Rectangle2D.Double(boundingBox.getX(), boundingBox.getY(), c.p.getX() - boundingBox.getX(), c.p.getY() - boundingBox.getY());
      
      Line2D lineH = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX() - initialPossibleRectangle.getWidth() - flub, c.p.getY());
      //Line2D lineH = new Line2D.Double(boundingBox.getX(), c.p.getY(), boundingBox.getX() + initialPossibleRectangle.getWidth(), c.p.getY());
      //Line2D lineV = new Line2D.Double(c.p.getX(), c.p.getY() - initialPossibleRectangle.getHeight(), c.p.getX(), c.p.getY());
      Line2D lineV = new Line2D.Double(c.p.getX(), c.p.getY(), c.p.getX(), c.p.getY() - initialPossibleRectangle.getHeight() - flub);
     
      /*
      xdist = initialPossibleRectangle.getWidth();
      ydist = initialPossibleRectangle.getHeight();
       
      c.dots.add(new Dot(initialPossibleRectangle.getX(), initialPossibleRectangle.getY() + initialPossibleRectangle.getHeight(), xdist, 0f));
      c.dots.add(new Dot(initialPossibleRectangle.getX() + initialPossibleRectangle.getWidth(), initialPossibleRectangle.getY() , 0f, ydist));
       */
      
      
      //for (Line2D line : boundingLines)
      for (Line2D line : boundingLines)
      {
        Point2D pt1 = line.getP1();
        if (initialPossibleRectangle.contains(line.getP1()))
        {
          float ptX = (float)pt1.getX();
          float ptY = (float)pt1.getY();
          
          c.dots.add(new Dot(ptX, ptY, c.p.getX() - ptX, c.p.getY() - ptY));
        }
        
        Point2D intersectingPt;
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineH, line);
        
        if (intersectingPt != null)
        {
          xdist = (float) (c.p.getX() - intersectingPt.getX());
          c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), xdist, 0f));
        }
        
        intersectingPt = GeomUtils.getIntersectionBetweenLines(lineV, line);
        
        if (intersectingPt != null)
        {
          ydist = (float) (c.p.getY() - intersectingPt.getY());
          c.dots.add(new Dot(intersectingPt.getX(), intersectingPt.getY(), 0f, ydist));
        }
      }
    }
    
    return initialPossibleRectangle;
  }
  

  //simplify this, make a method so as to not have to repeat for each corner type... ANGUS
  private List<Rectangle2D> checkForBoundaryOverlap(Corner c, Rectangle2D possibleRectangle)
  {
    List<Rectangle2D> newPossibleRects = new ArrayList<Rectangle2D>();
    
    //System.out.println("\n**** new poss rects ***");
    for (int j = 0; j < boundingLines.size(); j++)
    {
      //System.out.println("checking line ("+j+")\n");
      //Line2D line = boundingLines.get(j);
      Line2D line = boundingLines.get(j);
      
      if (line.intersects(possibleRectangle))
      {
        //System.out.println("it intersects...");
        
        Line2D topLine = new Line2D.Double(possibleRectangle.getX(), possibleRectangle.getY() + possibleRectangle.getHeight(),
                possibleRectangle.getX() + possibleRectangle.getWidth(), possibleRectangle.getY() + possibleRectangle.getHeight());
        
        Line2D rightLine = new Line2D.Double(possibleRectangle.getX() + possibleRectangle.getWidth(), possibleRectangle.getY(),
                possibleRectangle.getX() + possibleRectangle.getWidth(), possibleRectangle.getY() + possibleRectangle.getHeight());
        
        Line2D leftLine = new Line2D.Double(possibleRectangle.getX(), possibleRectangle.getY(),
                possibleRectangle.getX(), possibleRectangle.getY() + possibleRectangle.getHeight());
        
        Line2D bottomLine = new Line2D.Double(possibleRectangle.getX(), possibleRectangle.getY(),
                possibleRectangle.getX() + possibleRectangle.getWidth(), possibleRectangle.getY());
        
        if (c.type == Corner.NORTHEAST)
        {
          /*
          //check to make sure this isn't just a straight line,
          //in which case we just want to use the rectangle we have
          Point2D p2d_a = new Point2D.Double(possibleRectangle.getX() + possibleRectangle.getWidth(),
                  possibleRectangle.getY() + possibleRectangle.getHeight());
          Point2D p2d_b = new Point2D.Double(possibleRectangle.getX(),
                  possibleRectangle.getY());
           
          //System.out.print("does our line " + printLine(line) + " contain our point? " + printPoint(p2d_a));
          //System.out.print("\nor does our line " + printLine(line) + " contain our point? " + printPoint(p2d_b));
           
          //if (checkLineForPoint(line, p2d_a) == true || checkLineForPoint(line, p2d_b) == true)
          if (checkLineForPoint(line, p2d_b) == true ) //|| checkLineForPoint(line, p2d_b) == true)
          {
            //System.out.println("   yes it does... try next line");
            continue;
          }
          else
          {
            //System.out.println(" no it doesn't! keep going...");
          }
           */
          
          Point2D intersectPtTop = GeomUtils.getIntersectionBetweenLines(topLine, line);
          
          if (intersectPtTop == null)
          {
            intersectPtTop = GeomUtils.getIntersectionBetweenLines(leftLine, line);
          }
          
          Point2D intersectPtRight = GeomUtils.getIntersectionBetweenLines(rightLine, line);
          if (intersectPtRight == null)
          {
            intersectPtRight = GeomUtils.getIntersectionBetweenLines(bottomLine, line);
          }
          
          if (intersectPtTop != null && intersectPtRight != null)
          {
            //System.out.println("top point = " + printPoint(intersectPtTop));
            //System.out.println("right point = " + printPoint(intersectPtRight));
            //brokeUpPossRect = true;
            //System.out.println("yes we are breaking up PossRect!!!!!!!!!!!!!!!!!!!!!!");
            
            float intersectPtsWidth = (float) (intersectPtRight.getX() - intersectPtTop.getX());
            float intersectPtHeight = (float) (intersectPtTop.getY() - intersectPtRight.getY());
            
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            Rectangle2D brokenRect = new Rectangle2D.Double(possibleRectangle.getX(),
                    possibleRectangle.getY(),
                    (intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * .5f),
                    (intersectPtTop.getY() - possibleRectangle.getY()) - (intersectPtHeight * .5f) );
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(possibleRectangle.getX(),
                    possibleRectangle.getY(),
                    (intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * .25f),
                    (intersectPtTop.getY() - possibleRectangle.getY()) - (intersectPtHeight * .25f) );
            
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            brokenRect = new Rectangle2D.Double(possibleRectangle.getX(),
                    possibleRectangle.getY(),
                    (intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * .75f),
                    (intersectPtTop.getY() - possibleRectangle.getY()) - (intersectPtHeight * .75f) );
            
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            brokenRect = new Rectangle2D.Double(possibleRectangle.getX(),
                    possibleRectangle.getY(),
                    (intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * 1f),
                    (intersectPtTop.getY() - possibleRectangle.getY()) - (intersectPtHeight * 1f) );
            
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            brokenRect = new Rectangle2D.Double(possibleRectangle.getX(),
                    possibleRectangle.getY(),
                    (intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * 0f),
                    (intersectPtTop.getY() - possibleRectangle.getY()) - (intersectPtHeight * 0f) );
            
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
          }
        }
        
        if (c.type == Corner.NORTHWEST)
        {
          Point2D intersectPtTop = GeomUtils.getIntersectionBetweenLines(topLine, line);
          
          if (intersectPtTop == null)
          {
            intersectPtTop = GeomUtils.getIntersectionBetweenLines(rightLine, line);
          }
          
          Point2D intersectPtRight = GeomUtils.getIntersectionBetweenLines(leftLine, line);
          if (intersectPtRight == null)
          {
            intersectPtRight = GeomUtils.getIntersectionBetweenLines(bottomLine, line);
          }
          
          gTestPt1 = intersectPtTop;
          gTestPt2 = intersectPtRight;
          gTestRect = possibleRectangle;
          
          if (intersectPtTop != null && intersectPtRight != null)
          {
            //System.out.println("top point = " + printPoint(intersectPtTop));
            //System.out.println("right point = " + printPoint(intersectPtRight));
            //brokeUpPossRect = true;
            //System.out.println("yes we are breaking up PossRect!!!!!!!!!!!!!!!!!!!!!!");
            
            float intersectPtsWidth = (float) (intersectPtTop.getX() - intersectPtRight.getX());
            //float intersectPtsWidth = possibleRectangle.getWidth();
            float intersectPtHeight = (float) (intersectPtTop.getY() - intersectPtRight.getY());
            
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            Rectangle2D brokenRect = new Rectangle2D.Double(
                    intersectPtRight.getX() + (intersectPtsWidth * .5f),
                    possibleRectangle.getY(),
                    ((possibleRectangle.getX() + possibleRectangle.getWidth())- intersectPtTop.getX()) + (intersectPtsWidth * .5f),
                    (intersectPtTop.getY() - possibleRectangle.getY())- (intersectPtHeight * .5f) );
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    intersectPtRight.getX() + (intersectPtsWidth * .75f),
                    possibleRectangle.getY(),
                    ((possibleRectangle.getX() + possibleRectangle.getWidth())- intersectPtTop.getX()) + (intersectPtsWidth * .25f),
                    (intersectPtTop.getY() - possibleRectangle.getY())- (intersectPtHeight * .25f) );
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    intersectPtRight.getX() + (intersectPtsWidth * .25f),
                    possibleRectangle.getY(),
                    ((possibleRectangle.getX() + possibleRectangle.getWidth())- intersectPtTop.getX()) + (intersectPtsWidth * .75f),
                    (intersectPtTop.getY() - possibleRectangle.getY())- (intersectPtHeight * .75f) );
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    intersectPtRight.getX() + (intersectPtsWidth * 0f),
                    possibleRectangle.getY(),
                    ((possibleRectangle.getX() + possibleRectangle.getWidth())- intersectPtTop.getX()) + (intersectPtsWidth * 1f),
                    (intersectPtTop.getY() - possibleRectangle.getY())- (intersectPtHeight * 1f) );
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    intersectPtRight.getX() + (intersectPtsWidth * 1f),
                    possibleRectangle.getY(),
                    ((possibleRectangle.getX() + possibleRectangle.getWidth())- intersectPtTop.getX()) + (intersectPtsWidth * 0f),
                    (intersectPtTop.getY() - possibleRectangle.getY())- (intersectPtHeight * 0f) );
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
          }
          
        }
        
        if (c.type == Corner.SOUTHEAST)
        {
          Point2D intersectPtTop = GeomUtils.getIntersectionBetweenLines(bottomLine, line);
          
          if (intersectPtTop == null)
          {
            intersectPtTop = GeomUtils.getIntersectionBetweenLines(leftLine, line);
          }
          
          Point2D intersectPtRight = GeomUtils.getIntersectionBetweenLines(rightLine, line);
          if (intersectPtRight == null)
          {
            intersectPtRight = GeomUtils.getIntersectionBetweenLines(topLine, line);
          }
          
          if (intersectPtTop != null && intersectPtRight != null)
          {
            //System.out.println("top point = " + printPoint(intersectPtTop));
            //System.out.println("right point = " + printPoint(intersectPtRight));
            //brokeUpPossRect = true;
            //System.out.println("yes we are breaking up PossRect!!!!!!!!!!!!!!!!!!!!!!");
            
            float intersectPtsWidth = (float) (intersectPtRight.getX() - intersectPtTop.getX());
            float intersectPtHeight =  (float) (intersectPtRight.getY() - intersectPtTop.getY());
            
            float bx = (float) ((intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * .5f));
            float bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * .5f));
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            Rectangle2D brokenRect = new Rectangle2D.Double(
                    c.p.getX(),
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            bx = (float) ((intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * .75f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * .25f));
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX(),
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            bx = (float) ((intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * .25f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * .75f));
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX(),
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            bx = (float) ((intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * 1f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * 0f));
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX(),
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            bx = (float) ((intersectPtTop.getX() - possibleRectangle.getX()) + (intersectPtsWidth * 0f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * 1f));
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX(),
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
          }
        }
        
        if (c.type == Corner.SOUTHWEST)
        {
          Point2D intersectPtTop = GeomUtils.getIntersectionBetweenLines(bottomLine, line);
          
          if (intersectPtTop == null)
          {
            intersectPtTop = GeomUtils.getIntersectionBetweenLines(rightLine, line);
          }
          
          Point2D intersectPtRight = GeomUtils.getIntersectionBetweenLines(leftLine, line);
          if (intersectPtRight == null)
          {
            intersectPtRight = GeomUtils.getIntersectionBetweenLines(topLine, line);
          }
          
          if (intersectPtTop != null && intersectPtRight != null)
          {
            //System.out.println("top point = " + printPoint(intersectPtTop));
            //System.out.println("right point = " + printPoint(intersectPtRight));
            //brokeUpPossRect = true;
            //System.out.println("yes we are breaking up PossRect!!!!!!!!!!!!!!!!!!!!!!");
            
            //float intersectPtsWidth = intersectPtRight.getX() - intersectPtTop.getX();
            float intersectPtsWidth = (float) (intersectPtTop.getX() - intersectPtRight.getX());
            float intersectPtHeight =  (float)(intersectPtRight.getY() - intersectPtTop.getY());
            
            float bx = (float ) (c.p.getX() - intersectPtTop.getX() + (intersectPtsWidth * .5f));
            float bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * .5f));
            //System.out.println("bx = " + bx);
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            Rectangle2D brokenRect = new Rectangle2D.Double(
                    c.p.getX() - bx,
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            bx = (float) (c.p.getX()- intersectPtTop.getX() + (intersectPtsWidth * .25f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * .75f));
            //System.out.println("bx = " + bx);
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX() - bx,
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            bx =(float)( c.p.getX()- intersectPtTop.getX() + (intersectPtsWidth * .75f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * .25f));
            //System.out.println("bx = " + bx);
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX() - bx,
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            bx = (float) (c.p.getX()- intersectPtTop.getX() + (intersectPtsWidth * 1f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * 0f));
            //System.out.println("bx = " + bx);
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX() - bx,
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + GeomUtils.printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
            
            
            bx = (float) (c.p.getX()- intersectPtTop.getX() + (intersectPtsWidth * 0f));
            bh = (float) (c.p.getY() - intersectPtRight.getY() + (intersectPtHeight * 1f));
            //System.out.println("bx = " + bx);
            //System.out.println("bh = " + bh);
            //System.out.println("intersectPtsWidth = " + intersectPtsWidth + " intersectPtHeight = " + intersectPtHeight);
            brokenRect = new Rectangle2D.Double(
                    c.p.getX() - bx,
                    c.p.getY() - bh,
                    bx,
                    bh ) ;
            //System.out.println("broken rectangle = " + GeomUtils.printRectangle(brokenRect));
            newPossibleRects.add(brokenRect);
          }
        }
      }
    }
    
    return newPossibleRects;
  }
  
  private boolean checkIfNotTooSmall(Rectangle2D r2f)
  {
    if (r2f.getWidth() > minPossibleRectangleSize && r2f.getHeight() > minPossibleRectangleSize)
    {
      return true;
    }
    else
    {
      //System.out.println("rectangle is too small!");
      return false;
    }
  }
  

}
