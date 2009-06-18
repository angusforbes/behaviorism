/* GeomBlob.java (created on October 27, 2007, 6:32 PM) */

package geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import utils.GeomUtils;

public class GeomBlob extends Geom
{
  public float radius;
  public int resolution = 360;
  public float startAngle = 0f;
  public float endAngle = 360f;
  public List<Geom> gpts;
  public Geom centerGeom;
  public GeomBlob(List<Geom> gpts, Geom centerGeom)
  {
    this.gpts = gpts;
    this.centerGeom = centerGeom;
    this.isSelectable = true;
    this.scale.x = 1f;
    this.scale.y = 1f;
    this.scale.z = 1f;
  }


  public List<Point3f> getAnchorPointsForGeoms(List<Geom> gs)
  {
    List<Point3f> p3fs = new ArrayList<Point3f>();

    for (Geom g : gs)
    {
      p3fs.add(g.anchor);
    }

    return p3fs;
  }

  public List<BlobElement> makeBlobFromPoints()
  {
    float blobDist = .2f;
    Point3f centerPt = GeomUtils.centerOfAnchorPoints(gpts);
    centerGeom.anchor = centerPt;
    //Point3f centerPt = new Point3f(0f, 0f, 0f);
    
    List<Point3f> pts = getAnchorPointsForGeoms(gpts);
    synchronized(gpts)
    {
      //GeomUtils.sortPointsByAngle(new GeomPoint(centerPt), gpts, -1);
      GeomUtils.sortPointsByAngle(centerPt, pts, -1);
      /*
      for (int j = 0; j < gpts.size(); j++)
      {
        if (j == 0)
        {
          gpts.get(j).setColor(1f, 0f, 0f, 1f);
        }
        if (j == 1)
        {
          gpts.get(j).setColor(0f, 1f, 0f, 1f);
        }
        if (j == 2)
        {
          gpts.get(j).setColor(0f, 0f, 1f, 1f);
        }
        if (j == 3)
        {
          gpts.get(j).setColor(1f, 1f, 0f, 1f);
        }
        if (j == 4)
        {
          gpts.get(j).setColor(0f, 1f, 1f, 1f);
        }
       
      }
       
      pts = GeomPoint.geomPointfListToPoint3fList(gpts);
       */
    }
    

    //remove irrelevant ones
    Point3f c, n, p;
    List<Point3f> removals = new ArrayList<Point3f>();
    for (int i = pts.size() - 1; i >= 0; i--)
    {
      //System.out.println("i = " + i);
      c = pts.get(i);
      if (i == pts.size() - 1)
      {
        p = pts.get(0);
        n = pts.get(i - 1);
      }
      else if (i == 0)
      {
        p = pts.get(i + 1);
        n = pts.get(pts.size() - 1);
      }
      else
      {
        p = pts.get(i + 1);
        n = pts.get(i - 1);
      }

      Point3f insctPt1 = new Point3f(0f, 0f, 0f);
      Point3f insctPt2 = new Point3f(0f, 0f, 0f);
      //does this point circle intersect next points circle?
      boolean doesIntersect = GeomUtils.getIntersectionsOfTwoCircles(
              p, blobDist,
              n, blobDist,
              insctPt1, insctPt2);
      
      if (doesIntersect == true)//AND add in : and c closer to avgPt than both p and n!
      {
        System.out.println("next and prev do intersect");
        removals.add(c);
      }
        
    }

    for (int i = 0; i < removals.size(); i++)
    {
      System.out.println("removing " + removals.get(i));
      pts.remove(removals.get(i));
    }
  //System.out.println("pts: " + Arrays.toString(pts.toArray()) );
    List<BlobElement> bes = new ArrayList<BlobElement>();
    


    
    Point3f prevPt, curPt, nextPt;
    BlobElement prevBlobElement = null;
    BlobElement curBlobElement = null;
        
    for(int i = 0; i < pts.size()  ; i++)
    {
      curBlobElement = null;
      curPt = pts.get(i);
      if (i == pts.size() - 1)
      {
        nextPt = pts.get(0);
      }
      else
      {
        nextPt = pts.get(i + 1);
      }
      
      if (i != 0)
      {
        //test prev
      }
      
      
      Point3f insctPt1 = new Point3f(0f, 0f, 0f);
      Point3f insctPt2 = new Point3f(0f, 0f, 0f);
      //does this point circle intersect next points circle?
      boolean doesIntersect = GeomUtils.getIntersectionsOfTwoCircles(
              curPt, blobDist,
              nextPt, blobDist,
              insctPt1, insctPt2);
      
      if (doesIntersect == true)
      {
        //if (i == 1) System.out.println("here a??? - circles intersect!!!");
        Point3f shiftedPt1 = new Point3f(0f, 0f, 0f);
        Point3f shiftedPt2 = new Point3f(0f, 0f, 0f);
        GeomUtils.getShiftedLine(blobDist, curPt, nextPt, shiftedPt1, shiftedPt2);
        
        //add Line : shiftedPt1 --> shiftedPt2
        curBlobElement = new BlobElement(shiftedPt1, shiftedPt2);
        //bes.add();
      }
      else //circles do not intersect
      {
        boolean addShiftedLine = false;
        
        //right-shifted line between center and curPt
        Point3f shiftedPt_a1 = new Point3f(0f, 0f, 0f);
        Point3f shiftedPt_a2 = new Point3f(0f, 0f, 0f);
        //left-shifted line between center and nextPt
        Point3f shiftedPt_b1 = new Point3f(0f, 0f, 0f);
        Point3f shiftedPt_b2 = new Point3f(0f, 0f, 0f);
        
        GeomUtils.getShiftedLine(-blobDist, centerPt, curPt, shiftedPt_a1, shiftedPt_a2);
        GeomUtils.getShiftedLine(blobDist, centerPt, nextPt, shiftedPt_b1, shiftedPt_b2);
        
        //bes.add(new BlobElement(shiftedPt_a1, shiftedPt_a2));
        //bes.add(new BlobElement(shiftedPt_b1, shiftedPt_b2));
        
        Point3f intersectPt1 = new Point3f(0f, 0f, 0f);
        Point3f intersectPt2 = new Point3f(0f, 0f, 0f);
        
        int doesCurrentLineIntersectWithNextCircle = GeomUtils.getIntersectionOfLineAndSphere(
                true,
                shiftedPt_a1, shiftedPt_a2, nextPt, blobDist,
                intersectPt1, intersectPt2 );
        
        if (doesCurrentLineIntersectWithNextCircle >0)
        {
          //if (i == 1) System.out.println("AAA");
          //System.out.println("intersectPt1 = " + intersectPt1);
          //System.out.println("intersectPt2 = " + intersectPt2);
          addShiftedLine = true;
        }
        
        
        int doesNextLineIntersectWithCurrentCircle = GeomUtils.getIntersectionOfLineAndSphere(
                true,
                shiftedPt_b1, shiftedPt_b2, curPt, blobDist,
                intersectPt1, intersectPt2 );
        
        //temp...
        //bes.add(new BlobElement(shiftedPt_b1, shiftedPt_b2));
        
        if (doesNextLineIntersectWithCurrentCircle > 0)
        {
          //if (i == 1) System.out.println("BBB : next Line intersects with current circle");
          //if (i == 1) System.out.println("intersectPt1 = " + intersectPt1);
          //if (i == 1) System.out.println("intersectPt2 = " + intersectPt2);
          addShiftedLine = true;
        }
        
        if (addShiftedLine == true)
        {
          //if (i == 1) System.out.println("Line / Circle intersection !");
          
          Point3f shiftedPt1 = new Point3f(0f, 0f, 0f);
          Point3f shiftedPt2 = new Point3f(0f, 0f, 0f);
          GeomUtils.getShiftedLine(blobDist, curPt, nextPt, shiftedPt1, shiftedPt2);
          
            //everything is fine just add curLine
            //add Line : shiftedPt1 --> shiftedPt2
            curBlobElement = new BlobElement(shiftedPt1, shiftedPt2);
            //bes.add(curLine);
          
          
         
        }
        else //circle and line do not intersect
        {
          //if (i == 1) System.out.println("circle and line do NOT intersect!");
          Point3f intersectionPt = new Point3f(0f, 0f, 0f);
          boolean doLinesIntersect = GeomUtils.getIntersectionBetweenLines(shiftedPt_a1, shiftedPt_a2, shiftedPt_b1, shiftedPt_b2, intersectionPt);
          
          if (doLinesIntersect == true)
          {
            //if (i == 1) System.out.println("intersectionPt = " + intersectionPt);
            //add circle end angle for curPt.
            //add Line : shiftedPt_a2 --> intersectionPt
            bes.add(new BlobElement(shiftedPt_a2, intersectionPt));
            //bes.add(new BlobElement(shiftedPt_a2, shiftedPt_a1));
            
            //add Line : intersectionPt --> shiftedPt_b2
            bes.add(new BlobElement(intersectionPt, shiftedPt_b2));
            //bes.add(new BlobElement(shiftedPt_b1, shiftedPt_b2));
            //(later turn these two lines into some type of bezier curve
          }
          else //not sure if this is even a possibility, but if it is...
          {
            //add circle end angle for curPt.
            //add Line : shiftedPt_a2 --> shifted_a1
            bes.add(new BlobElement(shiftedPt_a2, shiftedPt_a1));
            
          }
          
        }//end circle and line do not intersect
      }//end two circles do not intersect case
    
      if (curBlobElement != null)
      {
      if (prevBlobElement != null) //check for intersection
          {
            Point3f intersectionPt = new Point3f(0f, 0f, 0f);
            boolean doLinesIntersect = GeomUtils.getIntersectionBetweenLines(
                    curBlobElement.p1, curBlobElement.p2,
                    prevBlobElement.p1, prevBlobElement.p2, intersectionPt);
            
            if (doLinesIntersect == true)
            {
              prevBlobElement.p2 = intersectionPt;
              curBlobElement.p1 = intersectionPt;
            }
          }

      bes.add(curBlobElement);
      
      }
          prevBlobElement = curBlobElement;    
    }//end for loop
    
    return bes;
  }

  public void draw(GL gl)
  {
    //maybe only do this when necessary?
    List<BlobElement> elements = makeBlobFromPoints();
    
    gl.glColor4f(r, g, b, a);
    
    //gl.glBegin(gl.GL_LINE_STRIP);
    gl.glBegin(gl.GL_LINES);
    
    for (int i = 0; i < elements.size(); i++)
    {
      BlobElement be = elements.get(i);
      if (be.type.equals("line"))
      {
        if (i == 0)
        {
          gl.glColor4f(1f, 0f, 0f, 1f);
        }
        else if (i == 1)
        {
          gl.glColor4f(0f, 1f, 0f, 1f);
        }
        else if (i == 2)
        {
          gl.glColor4f(0f, 0f, 1f, 1f);
        }
        else if (i == 3)
        {
          gl.glColor4f(1f, 1f, 1f, 1f);
        }
        gl.glVertex3f(be.p1.x, be.p1.y, be.p1.z);
        gl.glVertex3f(be.p2.x, be.p2.y, be.p2.z);
      }
      
    }
    /*
    double inc = (endAngle - startAngle)/(double)resolution;
    double angle = startAngle;
    for (int i = 0; i <= resolution; i++ )
    {
        gl.glVertex3f((float) (0 + (radius * Math.cos(Math.toRadians(angle)))),
                (float)(0 + (radius * Math.sin(Math.toRadians(angle)))),
                z);
    angle+=inc;
    }
     */
    gl.glEnd();
    
  }
  
}

class BlobElement
{
  Point3f p1, p2;
  float radius, ang1, ang2;
  String type;
  
  public BlobElement(Point3f p1, Point3f p2)
  {
    this.type = "line";
    this.p1 = p1;
    this.p2 = p2;
  }
  public BlobElement(Point3f p1, float radius, float ang1, float ang2)
  {
    this.type = "arc";
    this.radius = radius;
    this.ang1 = ang1;
    this.ang2 = ang2;
  }
}