/* BehaviorPath.java (created on August 15, 2007, 4:22 PM) */
package behaviors.geom.continuous;
import behaviorism.BehaviorismDriver;
import geometry.Geom;
import java.util.*;
import javax.vecmath.Point3f;
import behaviors.Behavior.LoopEnum;
import utils.Utils;

public class BehaviorPath extends BehaviorGeomContinuous
{
  //private float prevPercent = 0f;
  private Point3f prevPoint = null;
  private Point3f offsetPt = null;
  private Point3f percPt = null;
  float offset_x, offset_y, offset_z = 0f;
     
  public List<KeyFrame> keyFrames = new ArrayList<KeyFrame>();
  
  public BehaviorPath(long startNano, long lengthMS, LoopEnum loopBehavior,
          List<Point3f> points,
          float startPercent,
          int direction)
  {
       BehaviorismDriver.renderer.currentWorld.registerBehavior(this);
 
    this.direction = direction;
    initializeKeyFramesFromPoints(points);
    
    if (this.direction == -1)
    {
      this.prevPoint = (getPointFromPercentage(1f - startPercent));
    }
    else
    {
    this.prevPoint = getPointFromPercentage(startPercent);
    }
    this.lengthNano = Utils.millisToNanos(lengthMS);
    
    this.startTime = startNano; //nothing will happen before the startTime
    //determine what will happen after startTime... ie for startPercent != 0f
    this.startNano = this.startTime - (long)(this.lengthNano * startPercent);
    this.lastCheck = (long)(this.lengthNano * startPercent);
    
    this.loopBehavior = loopBehavior;
    
    setAccelerationPoints();
  }
  
  public void initializeKeyFramesFromPoints(List<Point3f> points)
  {
    float totalLength = 0f;
    
    if (points.size() < 2) //need at least two points!
    {
      return;
    }
    
    Point3f prevPoint = points.get(0);
    for (int i = 1; i < points.size(); i++)
    {
      Point3f curPoint = points.get(i);
      totalLength += prevPoint.distance(curPoint);
      prevPoint = curPoint;
    }
    
    //now we know totalLength of entire path
    
    prevPoint = points.get(0);
    float percentage = 0f;
    KeyFrame kf = new KeyFrame(prevPoint, percentage);
    this.keyFrames.add(kf);
    float curLength = 0f;
    for (int i = 1; i < points.size(); i++)
    {
      Point3f curPoint = points.get(i);
      curLength += prevPoint.distance(curPoint);
      if (i == points.size() - 1)
      {
        percentage = 1f;
      }
      else
      {
        percentage = (curLength/totalLength);
      }
      
      kf = new KeyFrame(curPoint, percentage);
      this.keyFrames.add(kf);
      
      prevPoint = curPoint;
    }

    //debug
    KeyFrame.printKeyFrames(this.keyFrames);
  }
  
  public Point3f getPointFromPercentage(float percentage)
  {
    float cperc = this.keyFrames.get(0).percentage;
    Point3f cpoint = this.keyFrames.get(0).point;
    
    for (int i = 1; i < this.keyFrames.size(); i++)
    {
      KeyFrame kf = keyFrames.get(i);
      
      //System.out.println("is " + kf.percentage + " >= " + percentage + "?");
      if (kf.percentage >= percentage)
      {
        //then the point is between here and prev (cperc)
        float totPerc = kf.percentage - cperc;
        float relPerc = percentage - cperc;
        
        float actPerc = relPerc / totPerc;
        
        float px = cpoint.x + ((kf.point.x - cpoint.x) * actPerc);
        float py = cpoint.y + ((kf.point.y - cpoint.y) * actPerc);
        float pz = cpoint.z + ((kf.point.z - cpoint.z) * actPerc);
        
        return new Point3f(px, py, pz);
      }
      else
      {
        cperc = kf.percentage;
        cpoint = kf.point;
      }
    }
    
    //shouldn't happen, but because of floating point errors it sometimes does...
    //return max
    System.out.println("FLOATING POINT ERROR...");
    return this.keyFrames.get(keyFrames.size() - 1).point;
    //return null; //error!
  }
  
  public Point3f calculateAnchorOffsets(Point3f prevPt, Point3f curPt)
  {
    if (prevPt == null)
    {
      System.out.println("prevPt = null");
    }
    if (curPt == null)
    {
      System.out.println("curPt = null");
    }
    Point3f offsetPt = new Point3f(curPt.x - prevPt.x,
            curPt.y - prevPt.y,
            curPt.z - prevPt.z);
    
    return offsetPt;
    
  }
  
  public void updatePreviousPoint(Point3f prevPoint, Point3f offsetPt)
  {
    prevPoint.x += offsetPt.x;
    prevPoint.y += offsetPt.y;
    prevPoint.z += offsetPt.z;
  }
  
  public void updatePreviousPoint(Point3f flipPoint)
  {
    prevPoint.x =flipPoint.x;
    prevPoint.y =flipPoint.y;
    prevPoint.z =flipPoint.z;
  }

	@Override
  //public void change(Geom g)
  public void updateGeom(Geom g)
  {
    g.translate.x += offset_x;
    g.translate.y += offset_y;
    g.translate.z += offset_z;
  }
  
  //public void change(Geom g, long currentNano)
  public void tick(long currentNano)
  {
    isActive = false;
    if (currentNano < startTime) { return; } //not ready yet
    isActive = true;
    
    offset_x = 0f;
    offset_y = 0f;
    offset_z = 0f;
 
    long now = currentNano - startNano;
    
    if (now >= lengthNano)
    {
      System.out.println("lastCheck = " + lastCheck + " lengthMS = " + lengthNano);
      System.out.println("point from percentage = "+ getPointFromPercentage(1f));
      if (direction == 1)
      {
        percPt = getPointFromPercentage(1f);
        offsetPt = calculateAnchorOffsets(prevPoint, percPt);
        updatePreviousPoint(percPt);
      }
      else
      {
        percPt = getPointFromPercentage(0f);
        offsetPt = calculateAnchorOffsets(prevPoint, percPt);
        updatePreviousPoint(percPt);
      }
      
      System.out.println("calc offset point : percentage = " + (1f - percentage) + " prevPoint = " + prevPoint);
      
      //g.translate.x += offsetPt.x;
      //g.translate.y += offsetPt.y;
      //g.translate.z += offsetPt.z;
      offset_x += offsetPt.x;
      offset_y += offsetPt.y;
      offset_z += offsetPt.z;
     
      //System.out.println("DIRECTION = " + direction + ", translate = " + g.translate);
      //updatePreviousPoint(g.translate);
      
      if (loopBehavior == LoopEnum.ONCE)
      {
        this.isDone = true;
        //System.out.println("translate = " + g.translate);
       
        return;
      }
      else 
      {
        if (loopBehavior == LoopEnum.CONTINUE)
        {
          if (direction == 1)
          {
            offsetPt = calculateAnchorOffsets(prevPoint, getPointFromPercentage(0f));
          }
          else
          {
            offsetPt = calculateAnchorOffsets(prevPoint, getPointFromPercentage(1f));
          }
          /*
          g.translate.x -= offsetPt.x;
          g.translate.y -= offsetPt.y;
          g.translate.z -= offsetPt.z;
          */
          offset_x -= offsetPt.x;
          offset_y -= offsetPt.y;
          offset_z -= offsetPt.z;
          
          startNano += lengthNano; //add length of behavior to starting time
          lastCheck = now - lengthNano;
          percentage = getPercentage(lastCheck);

          System.out.println("offsetPt = " + offsetPt + " prevPoint = " + prevPoint);
        }
        else if (loopBehavior == LoopEnum.LOOP)
        {
          startNano += lengthNano; //add length of behavior to starting time
          lastCheck = now - lengthNano;
          percentage = getPercentage(lastCheck);
        }
        else if (loopBehavior == LoopEnum.REVERSE)
        {
          startNano += lengthNano; //add length of behavior to starting time
          lastCheck = now - lengthNano;
          percentage = getPercentage(lastCheck);

          direction *= -1; //switch direction
        }
      }
    }
    else
    {
      //getting normal percentage...
      //percentage = (getPercentage(now) - getPercentage(lastCheck));
      percentage = (getPercentage(now));
      lastCheck = now;
    }
    
    if (direction == 1)
    {
      offsetPt = calculateAnchorOffsets(prevPoint, getPointFromPercentage(percentage));
    }
    else
    {
      offsetPt = calculateAnchorOffsets(prevPoint, getPointFromPercentage(1f - percentage));
    }

    /*
    g.translate.x += offsetPt.x;
    g.translate.y += offsetPt.y;
    g.translate.z += offsetPt.z;
    */
    offset_x += offsetPt.x;
    offset_y += offsetPt.y;
    offset_z += offsetPt.z;
     
    updatePreviousPoint(prevPoint, offsetPt);
  }
}


