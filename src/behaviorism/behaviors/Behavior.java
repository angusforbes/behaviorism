package behaviorism.behaviors;

import behaviorism.data.Node;
import behaviorism.geometry.Geom;
import behaviorism.utils.RenderUtils;
import behaviorism.utils.Utils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

abstract public class Behavior
{

   /**
   * A name for this Behavior. Does not need to be unique.
   */
  public String name = null;
  /**
   * A unique id for this Behavior.
   */
  public String id = null; //not currently used...

  public static boolean debugBehaviors = false;    //public boolean remove = false;
//  public AtomicBoolean isActive = new AtomicBoolean(false); //update() won't be called unless this is true
//  public AtomicBoolean isDone = new AtomicBoolean(false);
//  public AtomicBoolean isScheduled = new AtomicBoolean(false);
  public boolean isActive = false; //update() won't be called unless this is true
  public boolean isDone = false;
  public boolean isScheduled = false;
  public boolean isPaused = false;
  public boolean isInterrupted = false;

  public boolean autoRemove = true;

  public long interruptNano = -1L;

  public long nextTime = 0L;

  ///TESTING THIS
  /**
   * A List of all Behaviors that are affecting this behavior.
   */
  public List<Behavior> behaviors = new CopyOnWriteArrayList<Behavior>();

  public List<Geom> attachedGeoms = new CopyOnWriteArrayList<Geom>();
  public List<Node> attachedNodes = new CopyOnWriteArrayList<Node>();
  public List<Behavior> attachedBehaviors = new CopyOnWriteArrayList<Behavior>();

  /*
  public void attachBehavior(Behavior b)
  {
    attachedBehaviors.add(b);
    schedule(true);
  }
  */

  public void update()
  {
    //generic updater... can be override for meta-type behavior. or not.
  }

  //returns if it was already scheduled.
  //the idea is that this one is for manually scheduling,
  //so autoRemove is turned off.

  /*
  public boolean schedule(boolean autoRemove)
  {

    if (isScheduled == false)
    {
      this.autoRemove = autoRemove;
      RenderUtils.getWorld().scheduleBehavior(this);
      isScheduled = true;
      return true;
    }

    return false;
  }
  */
  /*
  public void attachGeom(Geom g)
  {
    attachedGeoms.add(g);
    schedule(true);
  }
  */

  public Behavior(long startTime)
  {
    this.nextTime = startTime;
  }

  public Behavior()
  {
    // RenderUtils.getWorld().scheduleBehavior(this);
  }
  ///DONE TESTING

  /*
  public void addDisposal(Disposal disposal)
  {
    disposals.add(disposal);
  }

  protected void dispose()
  {
    for (Disposal d : disposals) {
      d.dispose();
    }
  }
  */
  //optional method to clean up something when behavior is done
  public void dispose()
  {

  }


//  public void interruptImmediately()
//  {
//    isDone = (true);
//    isActive =(false);
//    //interrupt(System.nanoTime());
//    interrupt(0); //System.nanoTime());
//  }
//

  public void interrupt(long nano)
  {

    isInterrupted = true;
    interruptNano = nano;
    //System.err.println("setting interrupt to " + isInterrupted + " at " + interruptNano);
  }

//  public void interruptNowPlusMillis(long millis)
//  {
//    interrupt(Utils.nowPlusMillis(millis));
//  }
//
//  public void interruptNanoPlusMillis(long baseNano, long millis)
//  {
//    interrupt(Utils.nanoPlusMillis(baseNano, millis));
//  }

  public abstract void tick();
  
  /**
   * Convenience wrapper for a BehaviorIsPaused behavior.
   * That is, they pause ALL behaviors for the Geom for some specified amount of time.
   * If need to have more control of pausing, use the BehaviorIsPaused constructors directly.
   *
   */


  //  public static BehaviorIsPaused pauseAtNano(Geom geom, long baseNano, List<Behavior> behaviorsToPause, boolean pauseChildren)

  // i think this is pause now for lengthMS
  public void pauseNow(Geom geom, long lengthMS)
  {

//    BehaviorIsPaused.pauseBetweenMillis(geom, System.nanoTime(), 0L, lengthMS, null, true);

  }

  //public static BehaviorIsPaused pauseAtNano(Geom geom, long baseNano, List<Behavior> behaviorsToPause, boolean pauseChildren)

  public void pauseNano(Geom geom, long baseNano, long lengthMS)
  {
//    BehaviorIsPaused.pauseBetweenMillis(geom, baseNano, 0L, lengthMS, null, true);

  }

  public void pauseNanoPlusMillis(Geom geom, long baseNano, long baseMillis, long lengthMS)
  {
//    BehaviorIsPaused.pauseBetweenMillis(geom, (Utils.nanoPlusMillis(baseNano, baseMillis)), 0L, lengthMS, null, true);
  }

  public void pause()
  {
    pause(true);
  }

  public void unpause()
  {
    pause(false);
  }

  /**
   * Pause or unpause this behavior as specified by the isPaused parameter. 
   * @param isPaused Pauses this behavior if true, unpauses it if false.
   */
  public void pause(boolean isPaused)
  {
    this.isPaused = isPaused;
  }

  /** 
   * Correctly pauses the behavior so that percentages aren't messed up. If a behavior is paused,
   * then this method is called at every loop of the display loop.
   */
  public void pause(long nano)
  {
    //this.startNano = nano - lastCheck;
  }


  //HMM THINK AOBUT THIS>..
  public void step(long pauseNano, long stepNano)
  {
//    pause(pauseNano);
//    tick(pauseNano + stepNano);
  }
  

  public void reverse()
  {
  }
  /**
   * reverse causes the behavior to reverse what it was doing.
   * seems to mess up the stepper, rethink at some later time...
   */
   /*
  public void reverse()
  {
    //System.out.println("!!!! in reverse: lastCheck was = " + Utils.nanosToMillis(lastCheck));
    this.direction *= -1; //switch direction
    //this.lastCheck = relativeStartNano + (lengthNano - lastCheck);
    //long inc = relativeEndNano - lastCheck;
    //System.out.println("inc = " + inc);

    relativeEndNano = lastCheck + (lastCheck - relativeStartNano);
    relativeStartNano = relativeEndNano - lengthNano;

    //this.startNano -= (lengthNano - lastCheck) - lastCheck;
    //this.percentage = 1.0f - getPercentage(lastCheck);
    //this.lastCheck += (lengthNano - lastCheck) - lastCheck;
    System.out.println("in reverse: lastCheck is = " + Utils.nanosToMillis(lastCheck));
  }
  */

  /**
   * changeSpeed increases or decreases the speed of the Behavior by a specified
   * percentage amount. Using a negative amount causes reverse() to be called first. 
   * If the amount is zero, then to avoid a divide by zero error amount is changed 
   * to a very tiny value. (to do? throw an error in these cases).
   * 
   * seems to mess up the stepper, think about this sometime.
   * @param amount
   */
  public void changeSpeed(float amount) //i.e. 2f doubles, .5f halves
  {}
  /*
  public void changeSpeed(float amount) //i.e. 2f doubles, .5f halves
  {
    if (amount == 0f) {
      amount = .00000001f;
    }
    if (amount < 0f) {
      amount = Math.abs(amount);
      reverse();
    }
    float ratio = 1f / amount;

    this.lengthNano *= ratio;
    this.relativeEndNano = (long) (lastCheck + ((relativeEndNano - lastCheck) * ratio));
    this.relativeStartNano = (long) (lastCheck - ((lastCheck - relativeStartNano) * ratio));
  }
  */
  /**
   * Gets the time (in nanoseconds) of the next scheduled flip,
   * or in the case where loopBehavior = LoopEnum.ONCE, the scheduled time
   * of the removal of the behavior. If the pause() method is called,
   * then getFlipNano() will need to be called again to maintain an accurate timing.
   * This method can be used to schedule actions that occur as a particular behavior
   * reaches a particular value.
   */
  public long getFlipNano()
  {
  return -1;
  }
  /*
  public long getFlipNano()
  {
    return startNano + lengthNano;
  }
  */
  
  /** 
   * This creates a set of behaviors to rotate around a center point with a varying radius.
   * So you can create a spiraling effect. Like in the video game "Tempest".
   * Pass in a rotation offset (from the starting position of the rotating object),
   * a BehaviorRotation3D, and a BehaviorTranslate. The BehaviorTranslate controls the changing radius.
   * Note that you need to make sure everything set up (ie, accelerationPoints, direction, etc)
   * <b>before</b> you call this method, since it creates a behavior for the rotateAnchor as well,
   * which is made by cloning the BehaviorTranslate and reversing the x, y, z values.
   * <p>
   * Example arguments:
   * <p>
   * rotateOffset = new Point3f(1f, 0f, 0f);
   * <br>
   * br = new BehaviorRotation3D(0L, 1000L, LoopEnum.CONTINUE, 0f, 0f, 360f, 0f); 
   * <br>       
   * bl = new BehaviorTranslate(0L, 4000L, LoopEnum.REVERSE, 1f, 0f, 0f, .0f);
   * <p>
   * Internally, a behavior is attached to the Geom's rotateAnchor which is exactly the same as
   * "bl", except that the x range = -1f instead of 1f.
   */
  //This method should be put somewhere else! Also, need to handle the rotation anchor prpoerly,
  //since I removed the determineRotateAnchor method!
  //also I made rotateAnchor be a Point3f instead of a GeomPoint
  /*
  public static void compositeSpiralSpringBehavior(
    Point3f rotateOffset, BehaviorContinuous br, BehaviorContinuous bl, BehaviorContinuous bs,
    Geom geom) //should work for any Geom
  {
    
    //geom.determineRotateAnchor(rotateOffset);

    Behavior rotateAnchorBehaviorLine3D = behaviors.geom.continuous.BehaviorTranslate.translate(
      bl.nextTime,
      Utils.nanosToMillis(bl.lengthNano),
      //bl.loopBehavior,
      new Point3f(-bl.ranges[0], -bl.ranges[1], -bl.ranges[2])); //range of line
      //bl.startPercent
     // );

    rotateAnchorBehaviorLine3D.aps = bl.aps;

    
//    geom.rotateAnchor.scale = new Point3f(1f, 1f, 1f);
//    BehaviorScale rotateAnchorBehaviorScale3D = new BehaviorScale(
//    bs.nextTime,
//    Utils.nanosToMillis(bs.lengthNano),
//    bs.loopBehavior,
//    1f, 1f, 0f, //range of line
//    1f/(bs.range_x + 1f), 1f/(bs.range_y + 1f), 0f //range of line
//    );
//    rotateAnchorBehaviorScale3D.aps = bs.aps;
     
    geom.attachBehavior(br);
    geom.attachBehavior(bl);
    if (bs != null) {
      geom.attachBehavior(bs);
    }
    geom.rotateAnchor.attachBehavior(rotateAnchorBehaviorLine3D);
  //geom.rotateAnchor.attachBehavior(rotateAnchorBehaviorScale3D);
     
  }
  */
   @Override
  public String toString()
  {
    String str = "" + getClass() + " : ";
    if (name != null)
    {
      str += "name=" + name + " : ";
    }
    if (id != null)
    {
      str += "id=" + id + " : ";
    }

//    str += "lengthMS=" + Utils.nanosToMillis(lengthNano) + ", percentage="+percentage + ", dir="+ direction +
//      " loop="+loopBehavior;
    return str;
  }

}
