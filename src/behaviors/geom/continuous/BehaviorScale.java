/*
 * BehaviorScale.java
 * Created on May 26, 2007, 3:03 PM
 */
package behaviors.geom.continuous;

import geometry.Geom;
import javax.vecmath.Point3f;
import utils.GeomUtils;
import utils.MatrixUtils;

public class BehaviorScale extends BehaviorGeomContinuous
{

  public static BehaviorScale scale(
    long startTime,
    long lengthMS,
    Point3f scaleVector)
  {
    return new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(scaleVector)).loop(LoopEnum.ONCE));
  }

  public static BehaviorScale scale(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f scaleVector)
  {

    BehaviorScale bs = new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(scaleVector)).loop(
      LoopEnum.ONCE));

    g.attachBehavior(bs);

    return bs;
  }

  public static BehaviorScale scale(
    Geom g,
    long startTime,
    long lengthMS,
    LoopEnum loop,
    Point3f scaleVector)
  {

    BehaviorScale bs = new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(scaleVector)).loop(
      loop));

    g.attachBehavior(bs);

    return bs;
  }

  public static BehaviorScale scaleTo(
    Geom g,
    long startTime,
    long lengthMS,
    Point3f endScaleVector)
  {
    Point3f ranges = GeomUtils.subtractPoint3f(endScaleVector, MatrixUtils.toPoint3f(g.scale));

    BehaviorScale bs = new BehaviorScale(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(MatrixUtils.toArray(ranges)).loop(
      LoopEnum.ONCE));

    g.attachBehavior(bs);

    return bs;
  }

  public BehaviorScale(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }

  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.scaleX(offsets[0]);
      g.scaleY(offsets[1]);
      g.scaleZ(offsets[2]);
    }
  }
//  float range_x, range_y, range_z;
//  float offset_x, offset_y, offset_z;
//	
//	/**
//	 * Creates and attaches a Behavior that scales the specified Geom the percentages
//	 * in the x, y and z dimensions specified by the scaleVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has scaled the 
//	 * the dimensions specified by the scaleVector.
//  float range_x, range_y, range_z;
//  float offset_x, offset_y, offset_z;
//	
//	/**
//	 * Creates and attaches a Behavior that scales the specified Geom the percentages
//	 * in the x, y and z dimensions specified by the scaleVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has scaled the 
//	 * the dimensions specified by the scaleVector.
//  float range_x, range_y, range_z;
//  float offset_x, offset_y, offset_z;
//	
//	/**
//	 * Creates and attaches a Behavior that scales the specified Geom the percentages
//	 * in the x, y and z dimensions specified by the scaleVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has scaled the 
//	 * the dimensions specified by the scaleVector.
//  float range_x, range_y, range_z;
//  float offset_x, offset_y, offset_z;
//	
//	/**
//	 * Creates and attaches a Behavior that scales the specified Geom the percentages
//	 * in the x, y and z dimensions specified by the scaleVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has scaled the 
//	 * the dimensions specified by the scaleVector.
//  float range_x, range_y, range_z;
//  float offset_x, offset_y, offset_z;
//	
//	/**
//	 * Creates and attaches a Behavior that scales the specified Geom the percentages
//	 * in the x, y and z dimensions specified by the scaleVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has scaled the 
//	 * the dimensions specified by the scaleVector.
//	 * @param startNano
//	 * @param lengthMS
//	 * @param scaleVector
//	 */
//	public static void attachBehaviorScale(Geom g,
//					long startNano, long lengthMS, Point3f scaleVector)
//	{
//		g.attachBehavior( new BehaviorScale(startNano, lengthMS, LoopEnum.ONCE,
//						0f, 0f, 0f, scaleVector.x, scaleVector.y, scaleVector.z)    );
//	}
//	
//	/**
//	 * Returns a Behavior thats scales the current Geom the percentages
//	 * in the x, y and z dimensions as specified by the scaleVector parameter. 
//	 * The behavior starts at the nanosecond specified by startNano and 
//	 * runs for a number of milliseconds specified by lengthMS. 
//	 * The behavior operates only a single time
//	 * and schedules itself to be destroyed once the Geom has scaled in 
//	 * the dimensions specified by the scaleVector.
//	 * @param startNano
//	 * @param lengthMS
//	 * @param scaleVector
//	 * @return getMatrixIndex BehaviorTranslate that can be attached to a Geom
//	 */
//	public static BehaviorScale newBehaviorScale(
//					long startNano, long lengthMS, Point3f scaleVector)
//	{
//		return new BehaviorScale(startNano, lengthMS, LoopEnum.ONCE,
//						0f, 0f, 0f, scaleVector.x, scaleVector.y, scaleVector.z);
//	}
//	
//  /** 
//   This constructor calculates the range from a starting point to an ending point.
//   The startPercentage is always assumed to be 0f-- which means
//   that the behavior starts at the current point of the Geom, and ends at
//   the currentPoint plus the (max - min) points.
//   Basically this is a convenience constructor that calculates the range,
//   and assumes always starting at beginning of forward direction. 
//   */
//  public BehaviorScale(long startNano, long lengthMS, LoopEnum loopBehavior,
//          float minx, float miny, float minz,
//          float maxx, float maxy, float maxz)
//  {
//    init(startNano, lengthMS, loopBehavior, minx, miny, minz, maxx, maxy, maxz, 0f);
//  }
//  
//  /** 
//   This constructor calculates the range based on 
//   a startPercent and a range of points.
//   The startingPoint is always the Geom coords + (0f,0f,0f),
//   and the startPercent tells how far we go before reversing direction.
//   For instance, if the Geom is at 0f,0f,0f and the range is 2f,0f,0f
//   and the startPercent is .5f, then
//   the Behavior will start at 0f,0f,0f, proceed to 1f,0f,0f, and then 
//   backwards all the way to -1f,0f,0f, and then bounce between -1x and 1x forever.
//   */
//  public BehaviorScale(long startNano, long lengthMS, LoopEnum loopBehavior,
//          float maxx, float maxy, float maxz,
//          float startPercent)
//  {
//    init(startNano, lengthMS, loopBehavior, 0f, 0f, 0f, maxx, maxy, maxz, startPercent);
//  }
//
//  public void init(long startNano, long lengthMS, LoopEnum loopBehavior, float minx, float miny, float minz, float maxx, float maxy, float maxz, float startPercent)
//  {
//    if (startPercent < 0f || startPercent > 1f)
//    {
//      System.err.println("startPercent must be between 0f and 1f!");
//    }
//  
//    Main.world.registerBehavior(this);
//  
//    this.startPercent = startPercent; 
//    this.lengthNano = Utils.millisToNanos(lengthMS);
//    
//    this.startTime = startNano; //nothing will happen before the startTime
//
//    //determine what will happen after startTime... ie for startPercent != 0f
//    this.startNano = this.startTime - (long)(this.lengthNano * this.startPercent);
//    this.lastCheck = (long)(this.lengthNano * this.startPercent);
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
// 
//	@Override
//	public void resetOffsets()
//  {
//		offset_x = 0f;
//    offset_y = 0f;
//    offset_z = 0f;
//  }
//
//	public Point3f getOffsets()
//  {
//    //System.out.println("here -- offset_x = " + offset_x);
//    return new Point3f(offset_x, offset_y, offset_z); 
//  }
//	
//	@Override
//  public void change(Geom g)
//  {
//    if (isActive == true)
//    {
//      g.scale.x += offset_x;
//      g.scale.y += offset_y;
//      g.scale.z += offset_z;
//    }
//		//System.out.println("in BehaviorScale : changing Geom of type " + g.getClass() + ", " + g.scale);
//	}
//
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
}


