/* BehaviorSize.java (created on August 12, 2007, 9:48 PM) */

package behaviors.geom.continuous;

import geometry.Geom;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

public class BehaviorSize extends BehaviorGeomContinuous
{
   public static BehaviorSize size(
    long startTime, 
    long lengthMS, 
    Point3f sizeVector)
  {
    return new BehaviorSize(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(sizeVector)).loop(LoopEnum.ONCE) );
  }
  
  public BehaviorSize(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }
  
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.w += offsets[0];
      g.h += offsets[1];
      g.d += offsets[2];
    }
  }
}
//  //boolean centerGeom = false; //true; //false means scale around lower-left corner!
//  float range_w, range_h, range_d;
//  float offset_w, offset_h, offset_d;
//    
//
//	public static void attachBehaviorSize(Geom g,
//					long startNano, long lengthMS, Point3f sizeVector)
//	{
//		g.attachBehavior( newBehaviorSize(startNano, lengthMS, sizeVector) );
//	}
//
//	public static void attachBehaviorSize(Geom g,
//					long startNano, long lengthMS, Point3f sizeVector, LoopEnum loopBehavior)
//	{
//		g.attachBehavior( newBehaviorSize(startNano, lengthMS, sizeVector, loopBehavior) );
//	}
//	
//	public static BehaviorSize newBehaviorSize(
//					long startNano, long lengthMS, Point3f sizeVector)
//	{
//		return new BehaviorSize(startNano, lengthMS, LoopEnum.ONCE,
//						0f, 0f, 0f, sizeVector.x, sizeVector.y, sizeVector.z);
//	}
//	
//	public static BehaviorSize newBehaviorSize(
//					long startNano, long lengthMS, Point3f sizeVector, LoopEnum loopBehavior)
//	{
//		return new BehaviorSize(startNano, lengthMS, loopBehavior,
//						0f, 0f, 0f, sizeVector.x, sizeVector.y, sizeVector.z);
//	}
//	
//  public BehaviorSize(long startNano, long lengthMS, LoopEnum loopBehavior, 
//          float minw, float minh, float mind, 
//          float maxw, float maxh, float maxd)
//  {
//    init(startNano, lengthMS, loopBehavior, minw, minh, mind, maxw, maxh, maxd, 0f);
//  }
//
//	/*
//   public void init(long startNano, long lengthMS, LoopEnum loopBehavior, 
//          float minx, float miny, float minz, 
//          float maxx, float maxy, float maxz, 
//          float startPercent)
//  {
//    if (startPercent < 0f || startPercent > 1f)
//    {
//      System.err.println("startPercent must be between 0f and 1f!");
//    }
//   
//    Main.world.registerBehavior(this);
//    
//    this.startPercent = startPercent;
//    
//    this.lengthNano = Utils.millisToNanos(lengthMS);
//    
//    this.startTime = startNano; //nothing will happen before the startTime
//
//    //determine what will happen after startTime... ie for startPercent != 0f
//    this.startNano = this.startTime - (long)(this.lengthNano * startPercent);
//    this.lastCheck = (long)(this.lengthNano * startPercent);
//    
//    this.range_w = maxx - minx;
//    this.range_h = maxy - miny;
//    this.range_d = maxz - minz;
//
//    this.loopBehavior = loopBehavior;
//
//    setAccelerationPoints();
//  }
//  */
//	
//  public void init(long startNano, long lengthMS, LoopEnum loopBehavior, 
//          float minx, float miny, float minz, 
//          float maxx, float maxy, float maxz, 
//          float startPercent)
//  {
//    if (startPercent < 0f || startPercent > 1f)
//    {
//      System.err.println("startPercent must be between 0f and 1f!");
//    }
//   
//    Main.world.registerBehavior(this);
//    this.startPercent = startPercent;
//    
//    this.lengthNano = Utils.millisToNanos(lengthMS);
//    
//    this.startTime = startNano; //nothing will happen before the startTime
//
//    //determine what will happen after startTime... ie for startPercent != 0f
//    this.startNano = this.startTime - (long)(this.lengthNano * startPercent);
//    this.lastCheck = (long)(this.lengthNano * startPercent);
//    
//    this.range_w = maxx - minx;
//    this.range_h = maxy - miny;
//    this.range_d = maxz - minz;
//
//    this.loopBehavior = loopBehavior;
//
//    setAccelerationPoints();
//
//		relativeStartNano = 0L;
//		relativeEndNano = lengthNano;
//  }
//
//   
//  public Point3f getOffsets()
//  {
//    return new Point3f(offset_w, offset_h, offset_d); 
//  }
// 
//  public void change(Geom g)
//  {
//    if (isActive == true)
//    {
//      g.w += offset_w;
//      g.h += offset_h;
//      g.d += offset_d;
//     }
//  }
//  
//	
//	@Override
//	public void resetOffsets()
//  {
//		offset_w = 0f;
//		offset_h = 0f;
//		offset_d = 0f;
//  }
//	
//	 
//	@Override
//	protected void addToOffsets(float percentage, int direction)
//	{
//				offset_w += (range_w * percentage * direction);
//				offset_h += (range_h * percentage * direction);
//				offset_d += (range_d * percentage * direction);
//	}	
//	
//	@Override
//	protected void subtractFromOffsets(float percentage, int direction)
//	{
//				offset_w -= (range_w * percentage * direction);
//				offset_h -= (range_h * percentage * direction);
//				offset_d -= (range_d * percentage * direction);
//	}	
//	
//	/*
// public void tick(long currentNano)
//  {
//    if (currentNano < startTime) { return; } //not ready yet
//    
//    offset_w = 0f;
//    offset_h = 0f;
//    offset_d = 0f;
//   
//    now = currentNano - startNano;
//   
//    if (now >= lengthNano) //changing direction, etc.
//    {
//      //System.out.println("in BehaviorSize.change() : now = " + Utils.nanosToMillis(now));
//      percentage = getPercentage(lengthNano) - getPercentage(lastCheck);
//
//      offset_w += (range_w * percentage * direction);
//      offset_h += (range_h * percentage * direction);
//      offset_d += (range_d * percentage * direction);
//
//      if (loopBehavior == LoopEnum.ONCE)
//      {
//        this.isDone = true;
//        this.dispose();
//				return;
//      }
//      else //REVERSE OR LOOP OR LOOP
//      {
//        if (loopBehavior == LoopEnum.LOOP)
//        {
//          offset_w -= (range_w * direction);
//          offset_h -= (range_h * direction);
//          offset_d -= (range_d * direction);
//        }
//        //we've gone too far, go in the other direction however far we've overshot...
//        
//        //push startNano forward so that we begin anew with percentage = 0f
//        startNano += lengthNano; //add length of behavior to starting time
//        //so then we also need to push out lastCheck into the past
//        lastCheck = now - lengthNano;
//        
//        //getting remainder of flipped percentage...
//        percentage = getPercentage(lastCheck);
//   
//        if (loopBehavior == LoopEnum.REVERSE)
//        {
//          direction *= -1; //switch direction
//        }
//      }
//    }
//    else //(now < lengthNano)
//    {
//      //getting normal percentage...
//      percentage = (getPercentage(now) - getPercentage(lastCheck));
//      lastCheck = now;  
//    }
//    
//    offset_w += (range_w * percentage * direction);
//    offset_h += (range_h * percentage * direction);
//    offset_d += (range_d * percentage * direction);
//  }
//	*/
//   /*
//  public void change(Geom g, long currentNano) //no parent
//  {
//    System.out.println("in BehaviorSize.change()");
//    
//    //System.out.printf("ranges : %f/%f/%f\n", range_w, range_h, range_d);
//    float timePrev = getPercentage(lastCheck);
//    long now = (currentNano)- startNano;
//    
//    if (now < 0L)
//    {
//      return;
//    }
//    System.out.println("in BehaviorSize.change() : now = " + now);
//    
//    //ng now = System.currentTimeMillis() - startNano;
//    float timeNow = getPercentage(now);
//    
//    float percentage;
//    float offset_w;
//    float offset_h;
//    float offset_d;
//    
//    if (timeNow >= 1.0f)
//    {
//      
//      //System.out.println("SWITCHEROO! switching direction from direction:" + direction); //reverse
//      percentage = 1.0f - timePrev;
//      offset_w = (range_w * percentage);
//      offset_h = (range_h * percentage);
//      offset_d = (range_d * percentage);
//      //System.out.println("backwards... offset_w = " + offset_w);
//      
//      if (direction == 1)
//      {
//        g.w += offset_w;
//        g.h += offset_h;
//        g.d += offset_d;
//        
//        //g.anchor.x -= (offset_w / 2f);
//        //g.anchor.y -= (offset_h / 2f);
//        //g.anchor.z -= (offset_d / 2f);
//        
//      }
//      else
//      {
//        g.w -= offset_w;
//        g.h -= offset_h;
//        g.d -= offset_d;
//        //g.anchor.x += (offset_w / 2f);
//        //g.anchor.y += (offset_h / 2f);
//        //g.anchor.z += (offset_d / 2f);
//        
//      }
//      
//      if (loopBehavior == Behavior.LoopEnum.ONCE )
//      {
//        g.w = maxw;
//        g.h = maxh;
//        g.d = maxd;
//        
//        
//        this.isDone = true;
//        return;
//      }
//      
//      direction *= -1; //switch direction
//      //System.out.println("SWITCHEROO! NOW switched direction to direction:" + direction); //reverse
//      
//      percentage = timeNow - 1.0f;
//      offset_w = (range_w * percentage);
//      offset_h = (range_h * percentage);
//      //System.out.println("backwards... offset_w = " + offset_w);
//      
//      if (direction == 1)
//      {
//        g.w += offset_w;
//        g.h += offset_h;
//        g.d += offset_d;
//        
//        if (centerGeom == true)
//        {
//          g.anchor.x -= (offset_w / 2f);
//          g.anchor.y -= (offset_h / 2f);
//          g.anchor.z -= (offset_d / 2f);
//        }
//      }
//      else
//      {
//        g.w -= offset_w;
//        g.h -= offset_h;
//        g.d -= offset_d;
//        if (centerGeom == true)
//        {
//          
//          g.anchor.x += (offset_w / 2f);
//          g.anchor.y += (offset_h / 2f);
//          g.anchor.z += (offset_d / 2f);
//        }
//      }
//      
//      startNano += lengthNano;
//      lastCheck = now - lengthNano;
//      //now -= startNano;
//      //timePrev = 1.0f;
//      
//      return;
//    }
//    
//    percentage = timeNow - timePrev;
//    
//    offset_w = (range_w * percentage);
//    offset_h = (range_h * percentage);
//    offset_d = (range_d * percentage);
//    
//    
//    // System.out.println("lastCheck = " + lastCheck + " now = " + now);
//    // System.out.println("HERE percentage = " + percentage + " timeNow = " + timeNow + " timePrev = " + timePrev);
//    // System.out.println("range_w = " + range_w + " offset_w = "+ offset_w);
//    //System.out.println("c.x was " + c.x);
//    //c.x += .01f; //offset_w;
//    //Main.xzoom += .01f;
//    if (direction == 1)
//    {
//      g.w += offset_w;
//      g.h += offset_h;
//      g.d += offset_d;
//      
//      if (centerGeom == true)
//      {
//        g.anchor.x -= (offset_w / 2f);
//        g.anchor.y -= (offset_h / 2f);
//        g.anchor.z -= (offset_d / 2f);
//      }
//    }
//    else
//    {
//      g.w -= offset_w;
//      g.h -= offset_h;
//      g.d -= offset_d;
//      
//      if (centerGeom == true)
//      {
//        
//        g.anchor.x += (offset_w / 2f);
//        g.anchor.y += (offset_h / 2f);
//        g.anchor.z += (offset_d / 2f);
//      }
//    }
//    
//    //System.out.println("c.x is " + c.x);
//    
//    lastCheck = now;
//    
//  }
//  */
//   
//  /*
//  public void handleLoopBehavior(Geom g)
//  {
//    switch (loopBehavior)
//    {
//      case ONCE:
//      {
//        g.anchor.x = maxw;
//        g.anchor.y = maxh;
//        g.anchor.z = maxd;
//        this.remove = true;
//        return;
//      }
//      case LOOP:
//        startNano += lengthNano;
//        if (!isAbsolute)
//        {
//          g.anchor.x += minw;
//          g.anchor.y += minh;
//          g.anchor.z += mind;
//        }
//        else
//        {
//          g.anchor.x = minw;
//          g.anchor.y = minh;
//          g.anchor.z = mind;
//        }
//        return;
//      case REVERSE:
//        startNano += lengthNano;
//        
//        if (direction == -1)
//        {
//          if (!isAbsolute)
//          {
//            g.anchor.x += minw;
//            g.anchor.y += minh;
//            g.anchor.z += mind;
//          }
//          else
//          {
//            g.anchor.x = minw;
//            g.anchor.y = minh;
//            g.anchor.z = mind;
//          }
//        }
//        else
//        {
//          if (!isAbsolute)
//          {
//            g.anchor.x += maxw - this.centerOffset;
//            g.anchor.y += maxh;
//            g.anchor.z += maxd;
//          }
//          else
//          {
//            g.anchor.x = maxw - this.centerOffset;
//            g.anchor.y = maxh;
//            g.anchor.z = maxd;
//            
//          }
//        }
//        direction *= -1;
//        return;
//      default:
//        break;
//    }
//  }
//  
//  
//  public void updateAttributes(Geom g)
//  {
//    float range_w = maxw - minw;
//    float range_h = maxh - minh;
//    float range_d = maxd - mind;
//    
//    float offset_w = (range_w * percentage);
//    float offset_h = (range_h * percentage);
//    float offset_d = (range_d * percentage);
//    
//    if (direction == -1)
//    {
//      offset_w = (float) (maxw - (offset_w));
//      offset_h = (float) (maxh - (offset_h));
//      offset_d = (float) (maxd - (offset_d));
//    }
//    else
//    {
//      offset_w = (float) (minw + (offset_w));
//      offset_h = (float) (minh + (offset_h));
//      offset_d = (float) (mind + (offset_d));
//    }
//    
//    
//    if (!isAbsolute)
//    {
//      g.anchor.x += offset_w  - this.centerOffset;
//      g.anchor.y += offset_h;
//      g.anchor.z += offset_d;
//    }
//    else
//    {
//      g.anchor.x = offset_w; //  - this.centerOffset;
//      g.anchor.y = offset_h;
//      g.anchor.z = offset_d;
//      
//    }
//    
//    return;
//  }
//   */
//}


