/* BehaviorRotate.java (created on August 14, 2007, 5:39 PM) */

package behaviors.geom.continuous;

import geometry.Geom;
import behaviors.Behavior.LoopEnum;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

public class BehaviorRotate extends BehaviorGeomContinuous
{
  float range_x, range_y, range_z;
  float offset_x, offset_y, offset_z;

    public static BehaviorRotate rotate(
    long startTime, 
    long lengthMS, 
    Point3f rotateVector)
  {
    return new BehaviorRotate(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(rotateVector)).loop(LoopEnum.ONCE) );
  }
  
  public BehaviorRotate(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }
  
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.rotate.x += offsets[0];
      g.rotate.y += offsets[1];
      g.rotate.z += offsets[2];
    }
  }

  
//
//	/**
//	 * Creates and attaches a BehaviorRotate that rotates the specified Geom
//	 * around the Geom's rotateAnchor some degrees 
//	 * in the x, y, and z directions as specified by the rotateVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has finished the
//	 * rotation defined by the rotateVector.
//	 * @param startNano
//	 * @param lengthMS
//	 * @param rotateVector
//	 */
////	public static void attachBehaviorRotate(Geom g,
////					long startNano, long lengthMS, Point3f rotateVector)
////	{
////		g.attachBehavior( newBehaviorRotate(startNano, lengthMS, rotateVector) ); 
////	}
//
//	/**
//	 * Same as attachBehaviorRotate but requires you to specify the loop behavior.
//	 * @param g
//	 * @param startNano
//	 * @param lengthMS
//	 * @param rotateVector
//	 * @param loopBehavior
//	 */
//	public static void attachBehaviorRotate(Geom g,
//					long startNano, long lengthMS, Point3f rotateVector,
//					LoopEnum loopBehavior)
//	{
//		g.attachBehavior( newBehaviorRotate(startNano, lengthMS, rotateVector, loopBehavior) ); 
//	}
//
//	/**
//	 * Returns a BehaviorRotate that rotates the specified Geom
//	 * around the Geom's rotateAnchor some degrees 
//	 * in the x, y, and z directions as specified by the rotateVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has finished the
//	 * rotation defined by the rotateVector.
//	 * @param startNano The start time in nanoseconds
//	 * @param lengthMS The length it takes to execute the rotation
//	 * @param rotateVector The vector that defines the rotation
//	 * @return A BehaviorRotate that can be attached to a Geom
//	 */
//	public static BehaviorRotate newBehaviorRotate(
//					long startNano, long lengthMS, Point3f rotateVector)
//	{
//		return new BehaviorRotate(startNano, lengthMS, LoopEnum.ONCE,
//						0f, 0f, 0f, rotateVector.x, rotateVector.y, rotateVector.z);
//	}
//
//	/**
//	 * Same as newBehaviorRotate but requires you to specify the loop behavior.
//	 * @param startNano
//	 * @param lengthMS
//	 * @param rotateVector
//	 * @param loopBehavior
//	 * @return
//	 */
//	public static BehaviorRotate newBehaviorRotate(
//					long startNano, long lengthMS, Point3f rotateVector, LoopEnum loopBehavior)
//	{
//		return new BehaviorRotate(startNano, lengthMS, loopBehavior,
//						0f, 0f, 0f, rotateVector.x, rotateVector.y, rotateVector.z);
//	}
//	
//	
//  public BehaviorRotate(long startNano, long lengthMS, LoopEnum loopBehavior,
//          float minx, float miny, float minz, 
//          float maxx, float maxy, float maxz)
//  {
//    init(startNano, lengthMS, loopBehavior, minx, miny, minz, maxx, maxy, maxz, 0f);
//  }
// 
//  public BehaviorRotate(long startNano, long lengthMS, LoopEnum loopBehavior, 
//          float maxx, float maxy, float maxz, 
//          float startPercent)
//  {
//    init(startNano, lengthMS, loopBehavior, 0f, 0f, 0f, maxx, maxy, maxz, startPercent);
//  }
//  
//	
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
//    BehaviorismDriver.renderer.currentWorld.registerBehavior(this);
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
//    this.range_x = maxx - minx;
//    this.range_y = maxy - miny;
//    this.range_z = maxz - minz;
//
//    this.loopBehavior = loopBehavior;
//
//    setAccelerationPoints();
//
//		relativeStartNano = 0L;
//		relativeEndNano = lengthNano;
//  }
//
//  public Point3f getOffsets()
//  {
//    return new Point3f(offset_x, offset_y, offset_z); 
//  }
// 
//	@Override
//  public void change(Geom g)
//  {
//    if (isActive == true)
//    {
//      g.rotate.x += offset_x;
//      g.rotate.y += offset_y;
//      g.rotate.z += offset_z;
//    }
//  }
//	
//	@Override
//	public void resetOffsets()
//  {
//		offset_x = 0f;
//		offset_y = 0f;
//		offset_z = 0f;
//  }
//	 
//	@Override
//	protected void addToOffsets(float percentage, int direction)
//	{
//				offset_x += (range_x * percentage * direction);
//				offset_y += (range_y * percentage * direction);
//				offset_z += (range_z * percentage * direction);
//	}	
//	
//	@Override
//	protected void subtractFromOffsets(float percentage, int direction)
//	{
//				offset_x -= (range_x * percentage * direction);
//				offset_y -= (range_y * percentage * direction);
//				offset_z -= (range_z * percentage * direction);
//	}	
//	
//	/*
//  public void tick(long currentNano)
//  {
//    if (currentNano < startTime) { return; } //not ready yet
//    
//    offset_x = 0f;
//    offset_y = 0f;
//    offset_z = 0f;
//    
//    now = currentNano - startNano;
//   
//    if (now >= lengthNano) //changing direction, etc.
//    {
//      //System.out.println("in BehaviorRotate.change() : now = " + Utils.nanosToMillis(now));
//      
//      percentage = getPercentage(lengthNano) - getPercentage(lastCheck);
//
//     // System.out.println("percentage lengthNano = " + getPercentage(lengthNano));
//     // System.out.println("percentage lastCheck = " + getPercentage(lastCheck));
//     // System.out.println("percentage = " + percentage);
//      
//      offset_x += (range_x * percentage * direction);
//      offset_y += (range_y * percentage * direction);
//      offset_z += (range_z * percentage * direction);
//      
//      if (loopBehavior == LoopEnum.ONCE)
//      {
//        this.isDone = true;
//        return;
//      }
//      else //if (loopBehavior == LoopEnum.REVERSE)
//      {
//        if (loopBehavior == LoopEnum.LOOP)
//        {
//          offset_x -= (range_x * direction);
//          offset_y -= (range_y * direction);
//          offset_z -= (range_z * direction);
//        }
//      
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
//    offset_x += (range_x * percentage * direction);
//    offset_y += (range_y * percentage * direction);
//    offset_z += (range_z * percentage * direction);
//  }
//	 */
}
