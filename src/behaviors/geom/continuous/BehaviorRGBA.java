/*
 * BehaviorRGBA.java
 * Created on May 13, 2007, 4:06 PM
 */
package behaviors.geom.continuous;

import geometry.Geom;
import behaviors.Behavior.LoopEnum;
import geometry.Colorf;

public class BehaviorRGBA extends BehaviorGeomContinuous
{

  public static BehaviorRGBA colorChange(
    long startTime,
    long lengthMS,
    Colorf colorVector)
  {
    return new BehaviorRGBA(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(colorVector.array()).loop(LoopEnum.ONCE));
  }

   public static BehaviorRGBA colorChange(
    Geom g, 
    long startTime,
    long lengthMS,
    Colorf colorVector)
  {
     
    BehaviorRGBA brgba = new BehaviorRGBA(
      new ContinuousBehaviorBuilder(startTime, lengthMS).ranges(colorVector.array()).loop(LoopEnum.ONCE));
  
    g.attachBehavior(brgba);

    return brgba;
   }

  public BehaviorRGBA(ContinuousBehaviorBuilder builder)
  {
    super(builder);
  }

  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.color.r += offsets[0];
      g.color.g += offsets[1];
      g.color.b += offsets[2];
      g.color.a += offsets[3];

    }

  /*
  if (g.r > 1.0f) {   g.r = 1.0f;}
  if (g.g > 1.0f) {   g.g = 1.0f;}
  if (g.b > 1.0f) {   g.b = 1.0f;}
  if (g.a > 1.0f) {   g.a = 1.0f;}
  if (g.r < 0.0f) {   g.r = 0.0f;}
  if (g.g < 0.0f) {   g.g = 0.0f;}
  if (g.b < 0.0f) {   g.b = 0.0f;}
  if (g.a < 0.0f) {   g.a = 0.0f;}
   */
  //System.out.println("changing Geom of type " + g.getClass() + ", " + g.getColor());
  }
}
//  float range_r, range_g, range_b, range_a;
//  float offset_r, offset_g, offset_b, offset_a = 0f;
//  
//  public BehaviorRGBA(long startNano, long lengthMS, LoopEnum loopBehavior,
//          float r_min, float g_min, float b_min, float a_min, 
//          float r_max, float g_max, float b_max, float a_max,
//					float startPercent)
//  {
//    init(startNano, lengthMS, loopBehavior, r_min, g_min, b_min, a_min, r_max, g_max, b_max, a_max, startPercent);
//  }
//
//	public BehaviorRGBA(long startNano, long lengthMS, LoopEnum loopBehavior,
//          float r_min, float g_min, float b_min, float a_min, 
//          float r_max, float g_max, float b_max, float a_max)
//  {
//    init(startNano, lengthMS, loopBehavior, r_min, g_min, b_min, a_min, r_max, g_max, b_max, a_max, 0f);
//  }
//
//  public BehaviorRGBA(long startNano, long lengthMS, LoopEnum loopBehavior,
//          float r_max, float g_max, float b_max, float a_max,
//          float startPercent)
//  {
//    init(startNano, lengthMS, loopBehavior, 0f, 0f, 0f, 0f, r_max, g_max, b_max, a_max, startPercent);
//  }
//
//   public void init(long startNano, long lengthMS, LoopEnum loopBehavior, 
//           float r_min, float g_min, float b_min, float a_min,
//           float r_max, float g_max, float b_max, float a_max,
//           float startPercent)
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
//    this.range_r = r_max - r_min;
//    this.range_g = g_max - g_min;
//    this.range_b = b_max - b_min;
//    this.range_a = a_max - a_min;
//
//    this.loopBehavior = loopBehavior;
//
//    setAccelerationPoints();
//
//		relativeStartNano = 0L;
//		relativeEndNano = lengthNano;
//  }
//
//	@Override
//	public void resetOffsets()
//  {
//		offset_r = 0f;
//    offset_g = 0f;
//    offset_b = 0f;
//    offset_a = 0f;
//  }
//	 
//	@Override
//  public void change(Geom g)
//  {
//    if (isActive == true)
//    {
//      g.r += offset_r;
//      g.g += offset_g;
//      g.b += offset_b;
//      g.a += offset_a;
//    }
//	
//		/*
//		if (g.r > 1.0f) {   g.r = 1.0f;}
//		if (g.g > 1.0f) {   g.g = 1.0f;}
//		if (g.b > 1.0f) {   g.b = 1.0f;}
//		if (g.a > 1.0f) {   g.a = 1.0f;}
//		if (g.r < 0.0f) {   g.r = 0.0f;}
//		if (g.g < 0.0f) {   g.g = 0.0f;}
//		if (g.b < 0.0f) {   g.b = 0.0f;}
//		if (g.a < 0.0f) {   g.a = 0.0f;}
//		*/
//		//System.out.println("changing Geom of type " + g.getClass() + ", " + g.getColor());
//	}
// 
//		@Override
//	protected void addToOffsets(float percentage, int direction)
//	{
//			offset_r += (range_r * percentage * direction);
//      offset_g += (range_g * percentage * direction);
//      offset_b += (range_b * percentage * direction);
//      offset_a += (range_a * percentage * direction);
//    
//		}	
//	
//	@Override
//	protected void subtractFromOffsets(float percentage, int direction)
//	{
//    	offset_r -= (range_r * percentage * direction);
//			offset_g -= (range_g * percentage * direction);
//			offset_b -= (range_b * percentage * direction);
//			offset_a -= (range_a * percentage * direction);
//    
//	}	
//
//  /*
//	@Override
//	public void tick(long currentNano)
//	{
//		//System.out.println("brgba tick : now(" + Utils.nanosToMillis(now) + ") ... len(" + Utils.nanosToMillis(lengthNano) + ")");
//		//System.out.println("stt / ett = " + Utils.nanosToMillis(relativeStartNano) + " / " + Utils.nanosToMillis(relativeEndNano));
//
//		super.tick(currentNano);
//	}
//	*/
//  
//  public void oldtick(long currentNano)
//  //public void change(Geom g, long currentNano)
//  {
//		resetOffsets();
//		
//		if (currentNano < startTime) { return; } //not ready yet
//
//    now = currentNano - startNano;
//		//System.out.println("now = " + Utils.nanosToMillis(now));
//    if (now >= lengthNano) //changing direction, etc.
//    {
//			//System.out.println("...a");
//      
//			if (loopBehavior == LoopEnum.ONCE)
//      {
//        if (!Main.viz.isStepping)
//				{
//					this.isDone = true;
//				}
//				lastCheck = now;
//        return;
//      }
//			
//			percentage = getPercentage(lengthNano) - getPercentage(lastCheck);
//
//      offset_r += (range_r * percentage * direction);
//      offset_g += (range_g * percentage * direction);
//      offset_b += (range_b * percentage * direction);
//      offset_a += (range_a * percentage * direction);
//     
//      if (loopBehavior == LoopEnum.ONCE)
//      {
//        if (!Main.viz.isStepping)
//				{
//					this.isDone = true;
//				}
//				lastCheck = now;
//        return;
//      }
//      else if (loopBehavior == LoopEnum.REVERSE)
//      {
//		    //we've gone too far, go in the other direction however far we've overshot...
//        
//        //push startNano forward so that we begin anew with percentage = 0f
//        startNano += lengthNano; //add length of behavior to starting time
//        //so then we also need to push out lastCheck into the past
//        lastCheck = now - lengthNano;
//        
//        direction *= -1; //switch direction
//      
//        //getting remainder of flipped percentage...
//        percentage = getPercentage(lastCheck);
//      }
//    }
//		else if (now < 0) //testing for step through...
//		{
//			//System.out.println("...b");
//		
//			switch (loopBehavior)
//			{
//				case REVERSE:
//				//	System.out.println("reverse...");
//					percentage = 1f - (getPercentage(lengthNano) - getPercentage(lastCheck));
//
//					//System.out.println("percentage offset_x A = " + percentage);
//					offset_r -= (range_r * percentage * direction);
//					offset_g -= (range_g * percentage * direction);
//					offset_b -= (range_b * percentage * direction);
//					offset_a -= (range_a * percentage * direction);
//					//offset_x -= (range_x * percentage * direction);
//					//offset_y -= (range_y * percentage * direction);
//					//offset_z -= (range_z * percentage * direction);
//
//					startNano -= lengthNano; //add length of behavior to starting time
//					//so then we also need to push out lastCheck into the past
//					lastCheck = lengthNano + now; //now - lengthNano;
//
//					percentage = 1f - getPercentage(lastCheck);
//					direction *= -1; //switch direction
//				default: //do nothing - return without updating
//					lastCheck = now;
//					return;
//			}
//		}
//    else //(now < lengthNano)
//    {
//      //getting normal percentage...
//      percentage = (getPercentage(now) - getPercentage(lastCheck));
//      lastCheck = now;  
//    }
// 
//    offset_r += (range_r * percentage * direction);
//    offset_g += (range_g * percentage * direction);
//    offset_b += (range_b * percentage * direction);
//    offset_a += (range_a * percentage * direction);
//      
//  }
//
//  /* 
//  public void changeNO(Geom g) //no parent
//  {
//    if (this.remove == true)
//    {
//      return;
//    }
//  
//    
//    float timePrev = getPercentage(lastCheck);
//    long now = (System.nanoTime())- startNano;
//    
//    if (now < 0L)
//    {
//      return;
//    }
//    
//    this.percentage = getPercentage(now);
//
//    //System.out.println("percentage = " + percentage);
//    //percentage = getPercentage(startNano, lengthNano);
//    
//    if (percentage >= 1.0f)
//    {
//      handleLoopBehavior(g);
//      return;
//    }
//    else
//    {
//      updateAttributes(g);
//    }
//  }
//  */
//  /*
//  public void handleLoopBehavior(Geom g)
//  {
//    switch (loopBehavior)
//    {
//      case ONCE:
//        g.r = (int) (r_max);
//        g.g = (int) (g_max);
//        g.b = (int) (b_max);
//        g.a = (int) (a_max);
//
//        updateAttributes(g);
//        this.isDone = true;
//        //this.remove = true;
//        return;
//      case LOOP:
//        startNano += lengthNano;
//        
//        //c.x += w_radius * (float) (min);
//        //c.y += h_radius * (float) (min);
//        //c.z += d_radius * (float) (min);
//        g.r = (int) (r_min);
//        g.g = (int) (g_min);
//        g.b = (int) (b_min);
//        g.a = (int) (a_min);
//        return;
//      case REVERSE:
//        startNano += lengthNano;
//        
//        if (direction == -1)
//        {
//          //c.x += w_radius * (float) (min);
//          //c.y += h_radius * (float) (min);
//          //c.z += d_radius * (float) (min);
//          g.r = (int) (r_min);
//          g.g = (int) (g_min);
//          g.b = (int) (b_min);
//          g.a = (int) (a_min);
//        }
//        else
//        {
//          //c.x += w_radius * (float) (max);
//          //c.y += h_radius * (float) (max);
//          //c.z += d_radius * (float) (max);
//          g.r = (int) (r_max);
//          g.g = (int) (g_max);
//          g.b = (int) (b_max);
//          g.a = (int) (a_max);
//        }
//        direction *= -1;
//        return;
//      default:
//        break;
//    }
//  }
//  
//  public void updateAttributes(Geom g)
//  {
//    float range_r = r_max - r_min;
//    float range_g = g_max - g_min;
//    float range_b = b_max - b_min;
//    float range_a = a_max - a_min;
//    
//    float offset_r = (range_r * percentage);
//    float offset_g = (range_g * percentage);
//    float offset_b = (range_b * percentage);
//    float offset_a = (range_a * percentage);
//    
//    if (direction == -1)
//    {
//      offset_r = (float) (r_max - (offset_r));
//      offset_g = (float) (g_max - (offset_g));
//      offset_b = (float) (b_max - (offset_b));
//      offset_a = (float) (a_max - (offset_a));
//    }
//    else
//    {
//      offset_r = (float) (r_min + (offset_r));
//      offset_g = (float) (g_min + (offset_g));
//      offset_b = (float) (b_min + (offset_b));
//      offset_a = (float) (a_min + (offset_a));
//    }
//    
//    g.r = (offset_r);
//    g.g = (offset_g);
//    g.b = (offset_b);
//    g.a = (offset_a);
//    
//    //System.out.println("in updateAttributes() : c.rgb = " + c.r + "/" + c.g + "/" + c.b + " percentage = " + percentage);
//    return;
//  }
//  
//  */
//  
//}
//
