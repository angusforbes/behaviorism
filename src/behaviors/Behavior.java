package behaviors;

import geometry.Geom;
import java.util.*;
import javax.vecmath.Point3f;
import utils.Utils;
import behaviors.geom.discrete.BehaviorIsPaused;

abstract public class Behavior
{

  public static boolean debugBehaviors = false;    //public boolean remove = false;
  public boolean isDone = false;
  public boolean isPaused = false;
  public boolean isActive = false; //change() won't be called unless this is true
  public boolean isInterrupted = false;
  public long interruptNano = -1L;
  //public float[] ranges;
  //public float[] offsets;
  public int loopCount = 0;

  public enum LoopEnum
  {
    // a negative number means "do it forever"
    NONE(0), ONCE(1), REVERSE(-1), CONTINUE(-1), LOOP(-1);

    public int howManyTimes = 1;
    public LoopEnum howManyTimes(int times)
    {
      howManyTimes = times;
      return this;
    }

    LoopEnum(int times)
    {
      howManyTimes = times;
    }
  }

  protected long relativeStartNano = 0L;
  protected long relativeEndNano = 0L;
  public long startTime = 0L;
  public long startNano = 0L;
  public long lengthNano = 0L;
  public float percentage = 0f;
  public float startPercent = 0f;
  public long now = 0L;
  public long lastCheck = 0L;
  public LoopEnum loopBehavior = LoopEnum.NONE;
  protected int direction = 1; //this needs to be protected, set only on initialization! -- this is because changing it in midstream would mess everything up!
  public List<AccelerationPoint> aps = new ArrayList<AccelerationPoint>();
  //public List<Disposal> disposals = new ArrayList<Disposal>();

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

  public void interruptImmediately()
  {
    isDone = true;
    isActive = false;
    //interrupt(System.nanoTime());
    interrupt(0); //System.nanoTime());
  }

  public void interrupt(long nano)
  {
    isInterrupted = true;
    interruptNano = nano;
  }

  public void interruptNowPlusMillis(long millis)
  {
    interrupt(Utils.nowPlusMillis(millis));
  }

  public void interruptNanoPlusMillis(long baseNano, long millis)
  {
    interrupt(Utils.nanoPlusMillis(baseNano, millis));
  }

  public abstract void tick(long currentNano);
  
  /*
  protected void resetOffsets() {
  }
  ; //shoudl be abstract... to do
  
  protected void addToOffsets(float percentage, int direction) {
  }
  
  protected void subtractFromOffsets(float percentage, int direction) {
  }
   */
  /*
  public void tick(long currentNano)
  {
    isActive = false;

    resetOffsets();

    if (currentNano < startTime) {
      return;
    } //not ready yet

    if (isInterrupted == true && interruptNano <= currentNano) {
      this.isDone = true;
      //call disposals?

      return;
    }

    isActive = true;

    now = currentNano - startNano;

    if (now >= relativeEndNano) //changing direction, etc.
    {
      //System.out.println("A0");
      if (lastCheck < relativeEndNano) {
        //System.out.println("A1");
        percentage = getPercentage(lengthNano) - getPercentage(lastCheck - relativeStartNano);
        addToOffsets(percentage, direction);
      }

      lastCheck = now;

      switch (loopBehavior) {
        case ONCE:
          this.isDone = true;
          relativeStartNano = lengthNano;
          return;

        case LOOP:
          subtractFromOffsets(1.0f, direction);

          relativeStartNano += lengthNano;
          relativeEndNano += lengthNano;
          percentage = getPercentage(lastCheck - relativeStartNano);
          addToOffsets(percentage, direction);
          return;

        case REVERSE:
          relativeStartNano += lengthNano;
          relativeEndNano += lengthNano;

          //getting remainder of flipped percentage...
          percentage = getPercentage(lastCheck - relativeStartNano);

          direction *= -1; //switch direction
          addToOffsets(percentage, direction);
          return;
      }
    } else if (now <= relativeStartNano) {
      //System.out.println("A4");
      if (lastCheck > relativeStartNano) {
        percentage = getPercentage(lastCheck - relativeStartNano);
        subtractFromOffsets(percentage, direction);
      }

      lastCheck = now;
      switch (loopBehavior) {
        case ONCE: //do nothing - return without updating
          relativeStartNano = 0L;
          return;

        case LOOP:
          if (relativeStartNano - lengthNano >= 0L) {
            relativeStartNano -= lengthNano;
            relativeEndNano -= lengthNano;
          } else {
            return;
          }

          addToOffsets(1.0f, direction);

          //getting remainder of flipped percentage...
          percentage = getPercentage(lastCheck - relativeEndNano);

          addToOffsets(percentage, direction);
          return;

        case REVERSE:
          if (relativeStartNano - lengthNano >= 0L) {
            relativeStartNano -= lengthNano;
            relativeEndNano -= lengthNano;
          } else {
            return;
          }

          //getting remainder of reversed percentage...
          percentage = getPercentage(relativeEndNano - lastCheck);
          direction *= -1; //switch direction
          addToOffsets(percentage, direction);
          return;
      }
    } else {
      //getting normal percentage...
      if (lastCheck - relativeStartNano < 0) {
        percentage = getPercentage(now - relativeStartNano);
      } else {
        percentage = (getPercentage(now - relativeStartNano) - getPercentage(lastCheck - relativeStartNano));
      }
      lastCheck = now;

      addToOffsets(percentage, direction);
      return;
    }
  }
  */
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

    BehaviorIsPaused.pauseBetweenMillis(geom, System.nanoTime(), 0L, lengthMS, null, true);

//    BehaviorIsPaused bip = new BehaviorIsPaused(System.nanoTime(), lengthMS,
//      this);
//    geom.attachBehavior(bip);
  }

  //public static BehaviorIsPaused pauseAtNano(Geom geom, long baseNano, List<Behavior> behaviorsToPause, boolean pauseChildren)

  public void pauseNano(Geom geom, long baseNano, long lengthMS)
  {
    BehaviorIsPaused.pauseBetweenMillis(geom, baseNano, 0L, lengthMS, null, true);

//    BehaviorIsPaused bip = new BehaviorIsPaused(baseNano, lengthMS,
//      this);
//    geom.attachBehavior(bip);
  }

  public void pauseNanoPlusMillis(Geom geom, long baseNano, long baseMillis, long lengthMS)
  {
    BehaviorIsPaused.pauseBetweenMillis(geom, (Utils.nanoPlusMillis(baseNano, baseMillis)), 0L, lengthMS, null, true);

//    BehaviorIsPaused bip = new BehaviorIsPaused(
//      Utils.nanoPlusMillis(baseNano, baseMillis),
//      lengthMS, this);
//
//    geom.attachBehavior(bip);
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
    this.startNano = nano - lastCheck;
  }
  
  public void step(long pauseNano, long stepNano)
  {
    pause(pauseNano);
    tick(pauseNano + stepNano);
  }
  

  /**
   * reverse causes the behavior to reverse what it was doing.
   * seems to mess up the stepper, rethink at some later time...
   */
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
    return startNano + lengthNano;
  }

  /**
   * Gets the percentage of time that has passed between the start of this
   * behavior and the end of the behavior. This method does not take in to account
   * at all the values of the AccelrationPoints. It is simply the raw percentage
   * value of time passed.
   */
  public float getRawPercentage(long currentNano)
  {
    return (float) ((float) currentNano / (float) lengthNano); //% of raw time elapsed
  }

  /** 
   * Gets the percentage of time that has passed between the start of this
   * behavior and the end of the behavior, appropriately adjusted by the 
   * AccelelrationPoints attached to the behavior
   */
  public float getPercentage(long currentNano)
  {
    float perc_time = 0f;
    float perc_dist = 0f;

    float percentageAtMillis = getRawPercentage(currentNano);

    //System.out.println("in getPercentage(long): ( " + currentNano + "/" + lengthNano+ " ) init raw percentageAtMillis... = " + percentageAtMillis);

    if (percentageAtMillis < 0f || percentageAtMillis > 1f) {
      System.out.println("!!!!!!!!!!!!!!!!!!!!!in getPercentage(long): exiting: weird percentage! = " + percentageAtMillis);
    /* Come up with better way to indicate this... this shoudl never happen except perhaps at very beginning when loading */
    //System.exit(0);
    }

    //adjust the raw percentage if there are acceleration points
    float diff_dist, diff_time, part_dist;

    for (int i = 0; i < aps.size(); i++) {
      AccelerationPoint ap = aps.get(i);

      if (percentageAtMillis < ap.percentage_time) {
        diff_dist = ap.percentage_dist - perc_dist;
        diff_time = ap.percentage_time - perc_time;
        part_dist = (diff_dist * (percentageAtMillis - perc_time)) / diff_time;
        percentageAtMillis = (perc_dist + part_dist);
        break;
      } else {
        perc_time = ap.percentage_time;
        perc_dist = ap.percentage_dist;
      }
    }

    //System.out.println("in getPercentage(long): returning percentageAtMillis... = " + percentageAtMillis);

    return (float) percentageAtMillis;
  }

  protected void setAccelerationPoints()
  {
    setAccelerationPoints(null);
  }

  /** 
   * Sets the AccelerationPoints for this Behavior. If the input argument is null or empty,
   * then sets a single default AccelerationPoint of (1.0f, 1.0f) so that checking on the 
   * AccelerationPoints won't cause a problem.
   */
  public void setAccelerationPoints(List<AccelerationPoint> aps) //just default for now
  {
    if (aps == null || aps.size() == 0) {
      this.aps.add(new AccelerationPoint(1.0f, 1.0f));
    } else {
      this.aps = aps;
    }
  }

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
  public static void compositeSpiralSpringBehavior(
    Point3f rotateOffset, BehaviorContinuous br, BehaviorContinuous bl, BehaviorContinuous bs,
    Geom geom) //should work for any Geom
  {
    geom.determineRotateAnchor(rotateOffset);

    Behavior rotateAnchorBehaviorLine3D = behaviors.geom.continuous.BehaviorTranslate.translate(
      bl.startTime,
      Utils.nanosToMillis(bl.lengthNano),
      //bl.loopBehavior,
      new Point3f(-bl.ranges[0], -bl.ranges[1], -bl.ranges[2])); //range of line
      //bl.startPercent
     // );

    rotateAnchorBehaviorLine3D.aps = bl.aps;

    /*
    geom.rotateAnchor.scale = new Point3f(1f, 1f, 1f);
    BehaviorScale rotateAnchorBehaviorScale3D = new BehaviorScale(
    bs.startTime, 
    Utils.nanosToMillis(bs.lengthNano),
    bs.loopBehavior, 
    1f, 1f, 0f, //range of line
    1f/(bs.range_x + 1f), 1f/(bs.range_y + 1f), 0f //range of line
    );
    
    
    rotateAnchorBehaviorScale3D.aps = bs.aps;
     */
    geom.attachBehavior(br);
    geom.attachBehavior(bl);
    if (bs != null) {
      geom.attachBehavior(bs);
    }
    geom.rotateAnchor.attachBehavior(rotateAnchorBehaviorLine3D);
  //geom.rotateAnchor.attachBehavior(rotateAnchorBehaviorScale3D);
  }

  public String toString()
  {
    return "this is a Behavior of type " + getClass();
  }
}
