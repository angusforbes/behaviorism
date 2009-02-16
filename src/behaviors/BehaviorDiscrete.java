/* BehaviorDiscrete.java ~ Aug 14, 2008 */
package behaviors;

import behaviorism.BehaviorismDriver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import utils.Utils;

/**
 *
 * @author angus
 */
abstract public class BehaviorDiscrete extends Behavior
{

  public boolean isTimeToChange = false;
  public boolean startOn;
  public long waitTime;
  public List<Long> lengthNanos;

  public int curIndex = -1;

  public BehaviorDiscrete(DiscreteBehaviorBuilder builder)
  {
    initDiscreteBehavior(builder);
  }

/*
    if (index % 2 == 0) //even
    {
      if (startOn == true)
      {
        isGeomActive = true;
      }
      else
      {
        isGeomActive = false;
      }
    }
    else //odd
    {
      if (startOn == true)
      {
        isGeomActive = false;
      }
      else
      {
        isGeomActive = true;
      }
    }

 */

  
  
  @Override
  public void tick(long currentNano)
  {
    isActive = false;

    if (currentNano < startTime)
    {
      return;
    } //not ready yet

    if (isInterrupted == true && interruptNano <= currentNano)
    {
      this.isDone = true;
    //call disposals?
    }


    now = currentNano - startNano;
    //System.out.println("" + isGeomActive + " : now(" + Utils.nanosToMillis(now) + ") ... len(" + Utils.nanosToMillis(lengthNano) + ")");

    //System.out.println("now = " + now + ", lengthNano = " + lengthNano);
    //System.out.println("lengthsNanos are... " + Arrays.toString(lengthNanos.toArray() ));
    int nextIndex = getIndexAtNano(lengthNanos, now);

    //if we are at a different step in time, then we need to update the Geom
    //Otherwise, it is the same as before and nothing needs to be done...
    if (nextIndex != curIndex)
    {
      //System.out.println("nextIndex = " + nextIndex + ", currentIndex = " + curIndex);

      isActive = true;
      curIndex = nextIndex;
    }
    else
    {
      //System.out.println("nothing new...");
      return; //right?
    }

    if (curIndex < 0)
    {
      System.out.println("why are we here???");
      lastCheck = now;
      //before anything...
      //isGeomActive = false;
      isActive = true;
      //should toggle to whatever startOn is equal to

      //System.out.println("before anything... index = " + index);
      return;
    }

    //remove if this is the last one (and loopBehavior == ONCE)
    if (curIndex == lengthNanos.size() - 1)
    {
      switch (loopBehavior)
      {
        case ONCE:
          //System.out.println("onceing... ");
          this.isDone = true;
          lastCheck = now;
          break;
        case LOOP:
          //System.out.println("looping...");
          //startNano += lengthNano; //add length of behavior to starting time
          //so then we also need to push out lastCheck into the past
          //lastCheck = now - lengthNano;
          lastCheck = now;
          lengthNanos = loopLengthNanos(lengthNanos, lengthNano, 1, waitTime);

          curIndex = getIndexAtNano(lengthNanos, lastCheck);
          break;
        case REVERSE:
          //System.out.println("reversing...");
          lastCheck = now;
          lengthNanos = reverseLengthNanos(lengthNanos, lengthNano, 1, waitTime);
          if (lengthNanos.size() % 2 != 0)
          {
            startOn = !startOn;
          }

          curIndex = getIndexAtNano(lengthNanos, lastCheck);
          break;
      }
    }
    else
    {
      lastCheck = now;
    }

    //System.out.println("...end of tick... curIndex = " + curIndex);
  }

  final protected List<Long> loopLengthNanos(List<Long> lengthNanos, Long lengthNano, int dir, Long waitTime)
  {
    if (dir == -1 && lengthNanos.get(0) - lengthNano < waitTime)
    {
      //can't rewind any further!
      return lengthNanos;
    }

    List<Long> temp = new ArrayList<Long>();

    for (Long ln : lengthNanos)
    {
      temp.add(ln + (lengthNano * dir));
    }

    return temp;
  }

  final protected int getIndexAtNano(List<Long> lengthNanos, long now)
  {
    int index = Collections.binarySearch(lengthNanos, now);

    if (index < 0)
    {
      index = -index - 2; //prev
    }

    return index;
  }

  final protected List<Long> reverseLengthNanos(List<Long> lengthNanos, Long lengthNano, int dir, Long waitTime)
  {
    if (dir == -1 && lengthNanos.get(0) - lengthNano < waitTime)
    {
      //can't rewind any further!
      return lengthNanos;
    }

    List<Long> temp = new ArrayList<Long>();

    //long sn = lengthNanos.get(0);
//		long sn = lengthNanos.get(lengthNanos.size() - 1);

    long sn = lengthNanos.get(0) + (lengthNano * dir);
    temp.add(sn);
    for (int i = lengthNanos.size() - 1; i >= 1; i--)
    {
      long cur = lengthNanos.get(i);
      long prev = lengthNanos.get(i - 1);
      sn += ((cur - prev) * dir);
      temp.add(sn);
    }

    return temp;
  }
  
  public static class DiscreteBehaviorBuilder
  {
    private boolean startOn = false;
    private long startNano = 0L;
    private long startTime = 0L;
    private long lengthNano = 0L;
    private List<Long> lengthNanos;
    private long waitTime = 0L;
    private LoopEnum loopBehavior = LoopEnum.ONCE;

    public DiscreteBehaviorBuilder(long startTime)
    {
      //System.out.println("new DiscreteBehaviorBuilder...");
      this.startNano = startTime;
      this.startTime = startTime;

      List<Long> lengthMSs = new ArrayList<Long>();
      lengthMSs.add(0L);
      initLengthMSs(lengthMSs);
    }
    
    public DiscreteBehaviorBuilder(long startTime, long onMS)
    {
      this.startNano = startTime;
      this.startTime = startTime;

      List<Long> lengthMSs = new ArrayList<Long>();
      lengthMSs.add(onMS);
      initLengthMSs(lengthMSs);
    }

    public DiscreteBehaviorBuilder(long startTime, long onMS, long offMS)
    {
      this.startNano = startTime;
      this.startTime = startTime;

      List<Long> lengthMSs = new ArrayList<Long>();
      lengthMSs.add(onMS);
      lengthMSs.add(offMS);
      
      initLengthMSs(lengthMSs);
    }

    public DiscreteBehaviorBuilder(long startTime, List<Long> lengthMSs)
    {
      this.startNano = startTime;
      this.startTime = startTime;
      initLengthMSs(lengthMSs);
    }

    private void initLengthMSs(List<Long> lengthMSs)
    {
      this.lengthNanos = new ArrayList<Long>();

      for (Long ms : lengthMSs)
      {
        lengthNanos.add(Utils.millisToNanos(ms));
      }

      //make sure in sorted order!
      Collections.sort(lengthNanos);

      this.lengthNano = lengthNanos.get(lengthNanos.size() - 1) - lengthNanos.get(0);

      this.waitTime = lengthNanos.get(0);
    }

    public DiscreteBehaviorBuilder loop(LoopEnum loopBehavior)
    {
      this.loopBehavior = loopBehavior;
      return this;
    }

    public DiscreteBehaviorBuilder startOn(boolean startOn)
    {
      this.startOn = startOn;
      return this;
    }
  }

  public void initDiscreteBehavior(DiscreteBehaviorBuilder builder)
  {
    //System.out.println("in initDiscreteBehavior()...");
    this.loopBehavior = builder.loopBehavior;
    this.lengthNano = builder.lengthNano;
    this.startNano = builder.startNano;
    this.startTime = builder.startNano; //nothing will happen before the startTime
    this.startOn = builder.startOn;
    this.waitTime = builder.waitTime;
    this.lengthNanos = builder.lengthNanos;

    /*
    lengthNanos = new ArrayList<Long>();
    
    for (Long ms : lengthMSs)
    {
    lengthNanos.add(Utils.millisToNanos(ms));
    }
    
    //make sure in sorted order!
    Collections.sort(lengthNanos);
    
    this.lengthNano = lengthNanos.get(lengthNanos.size() - 1) - lengthNanos.get(0);
    
    this.waitTime = lengthNanos.get(0);
    
    this.startNano = startNano;
    this.startTime = startNano; //nothing will happen before the startTime
     */
    
    //System.out.println("I am registered...");
    //BehaviorismDriver.renderer.currentWorld.registerBehavior(this);

  //reverseLengthNanos(lengthNano, lengthNanos);
  }

  /*
  public void initBehavior(DiscreteBehaviorBuilder builder)
  {
  System.out.println("in initBehavior : builder version");
  if (builder.startPercent < 0f || builder.startPercent > 1f) {
  System.err.println("startPercent must be between 0f and 1f!");
  }
  
  this.startTime = builder.startTime; //nothing will happen before the startTime
  this.lengthNano = builder.lengthNano; //length of one loop of the behavior
  this.startPercent = builder.startPercent; //where in the loop the behavior begins
  this.loopBehavior = builder.loopBehavior; //the looping behvior
  this.ranges = builder.ranges;
  this.offsets = builder.offsets;
  
  System.out.println("in initBehavior : builder : range x = " + ranges[0]);
  //determine what will happen after startTime... ie for startPercent != 0f
  this.startNano = this.startTime - (long) (this.lengthNano * this.startPercent);
  this.lastCheck = (long) (this.lengthNano * startPercent);
  
  relativeStartNano = 0L;
  relativeEndNano = lengthNano;
  
  setAccelerationPoints(builder.accelerationPoints);
  
  BehaviorismDriver.renderer.currentWorld.registerBehavior(this);
  }
   */
}
