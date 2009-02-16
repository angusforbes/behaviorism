/*
 * BehaviorIsActive.java
 * Created on May 12, 2007, 7:04 PM
 */
package behaviors.geom.discrete;

import geometry.Geom;
import java.util.ArrayList;
import java.util.List;
import utils.Utils;

public class BehaviorIsActive extends BehaviorGeomDiscrete
{
  //public boolean activateChildren = false;
  public boolean activateChildren = true;
  protected boolean isGeomActive = false;

  /**
   * Sets the specified Geom to be activated at the specfied time. The Behavior
   * is returned so that it can be interrupted if necessary.
   * @param geom
   * @param baseNano
   * @return The BehaviorIsActive
   */
  public static BehaviorIsActive activateAtNano(Geom geom, long baseNano)
  {
    geom.isActive = false;
    
    BehaviorIsActive bia = new BehaviorIsActive
      (
        new DiscreteBehaviorBuilder(baseNano).loop(LoopEnum.ONCE) 
      );
      
    geom.attachBehavior(bia);

    return bia;
  }
  
  public static BehaviorIsActive activateAtMillis(Geom geom, long baseNano, long onMS)
  {
    geom.isActive = false;
    BehaviorIsActive bia = new BehaviorIsActive
      (
        new DiscreteBehaviorBuilder(baseNano, onMS).loop(LoopEnum.ONCE) 
      );
      
    geom.attachBehavior(bia);
    return bia;
  }

  public static BehaviorIsActive deactivateAtMillis(Geom geom, long baseNano, long onMS)
  {
    geom.isActive = true;
    BehaviorIsActive bia = new BehaviorIsActive
      (
        new DiscreteBehaviorBuilder(baseNano, onMS).loop(LoopEnum.ONCE)
      );

    geom.attachBehavior(bia);
    return bia;
  }

  public static BehaviorIsActive activateBetweenMillis(Geom geom, long baseNano, long onMS, long offMS)
  {
    geom.isActive = false;
    
    List<Long> mss = new ArrayList<Long>();
    Utils.addTo(mss, onMS, offMS);
  
    BehaviorIsActive bia = new BehaviorIsActive
      (
        new DiscreteBehaviorBuilder(baseNano, mss).loop(LoopEnum.LOOP) 
      );
      
    geom.attachBehavior(bia);
    return bia;
  }
  
  public static BehaviorIsActive activateBetweenMillis(Geom geom, long baseNano, List<Long> mss)
  {
    geom.isActive = false;
    
    BehaviorIsActive bia = new BehaviorIsActive
      (
        new DiscreteBehaviorBuilder(baseNano, mss).loop(LoopEnum.LOOP) 
      );
      
    geom.attachBehavior(bia);
    return bia;
  }
  
  /*
  public BehaviorIsActive(BehaviorBuilder builder)
  {
    initBehavior(builder);
  }
   */
   /*
  public BehaviorIsActive(long startNano)
  {
    List<Long> mss = new ArrayList<Long>();
    Utils.addTo(mss, 0L);
    init(startNano, startOn, mss, LoopEnum.ONCE, 0f);
  }

  public BehaviorIsActive(long startNano, long lengthMS)
  {
    List<Long> mss = new ArrayList<Long>();
    Utils.addTo(mss, 0L, lengthMS);

    init(startNano, true, mss, LoopEnum.ONCE, 0f);
  }

  public BehaviorIsActive(long startNano, long onMS, long offMS)
  {
    List<Long> mss = new ArrayList<Long>();
    Utils.addTo(mss, onMS, offMS);

    init(startNano, true, mss, LoopEnum.ONCE, 0f);
  }

  public BehaviorIsActive(long startNano, boolean startOn, List<Long> lengthMSs)
  {
    init(startNano, startOn, lengthMSs, LoopEnum.ONCE, 0f);
  }

  public BehaviorIsActive(long startNano, boolean startOn, List<Long> lengthMSs,
    LoopEnum loopBehavior)
  {
    init(startNano, startOn, lengthMSs, loopBehavior, 0f);
  }

  public BehaviorIsActive(long startNano, long lengthMS, LoopEnum loopBehavior)
  {
    init(startNano, lengthMS, loopBehavior, 0f);
  }

  public BehaviorIsActive(long startNano, long lengthMS, LoopEnum loopBehavior,
    float startPercent)
  {
    init(startNano, lengthMS, loopBehavior, startPercent);
  }

  public void init(long startNano, long lengthMS, LoopEnum loopBehavior,
    float startPercent)
  {
    lengthMSs = new ArrayList<Long>();
    lengthMSs.add(lengthMS);
    init(startNano, true, lengthMSs, loopBehavior, startPercent);
  }

  public void init(long startNano, boolean startOn, List<Long> lengthMSs,
    LoopEnum loopBehavior, float startPercent)
  {
    this.startOn = startOn;
    this.loopBehavior = loopBehavior;
    //System.out.println("INITING NEW BehaviorIsActive!!! " + lengthMS);
    //this.lengthNano = Utils.millisToNanos(lengthMS);
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
    BehaviorismDriver.renderer.currentWorld.registerBehavior(this);

  //reverseLengthNanos(lengthNano, lengthNanos);
  }
  */
  public BehaviorIsActive(DiscreteBehaviorBuilder builder)
  {
    super(builder);
  }
  
  @Override
  public void updateGeom(Geom g)
  {
    //System.out.println("in updateGeom ("+g+")... g.isActive WAS " + g.isActive);
    g.isActive = !g.isActive;//isGeomActive;
    //System.out.println("in updateGeom ("+g+")... g.isActive  IS " + g.isActive);

    if (activateChildren == true)
    {
      for (Geom child : g.geoms)
      {
        updateChildGeom(child, g.isActive);
      }
    }
  }

  public void updateChildGeom(Geom g, boolean isParentActive)
  {
    //System.out.println("isGeomActive = " + isGeomActive);
    g.isActive = isParentActive;

    //prob want to make sure children are active as well...
    for (Geom child : g.geoms)
    {
      updateChildGeom(child, isParentActive);
    }
  }

  /*
  @Override
  public void step(long pauseNano, long stepNano)
  {
    pause(pauseNano);
    long currentNano = pauseNano + stepNano;

    if (currentNano < startTime)
    {
      return;
    } //not ready yet

    now = currentNano - startNano;
    //System.out.println("" + isGeomActive + " : now(" + Utils.nanosToMillis(now) + ") ... len(" + Utils.nanosToMillis(lengthNano) + ")");
    //Utils.print(lengthNanos);

    int index = getIndexAtNano(lengthNanos, now);

    if (index < 0)
    {
      switch (loopBehavior)
      {
        case ONCE:
          lastCheck = now;
          //before anything...
          isGeomActive = false;
          //System.out.println("before anything... index = " + index);
          return;

        case LOOP:
          lastCheck = now;
          lengthNanos = loopLengthNanos(lengthNanos, lengthNano, -1, waitTime);
          index = getIndexAtNano(lengthNanos, lastCheck);
          if (index < 0)
          {
            isGeomActive = false;
            return;
          }
          break;

        case REVERSE:
          lastCheck = now;
          lengthNanos = loopLengthNanos(lengthNanos, lengthNano, -1, waitTime);
          index = getIndexAtNano(lengthNanos, lastCheck);
          if (index < 0)
          {
            isGeomActive = false;
            return;
          }
          break;

      }
    } else if (index == lengthNanos.size() - 1)
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
          lastCheck = now;
          lengthNanos = loopLengthNanos(lengthNanos, lengthNano, 1, waitTime);
          index = getIndexAtNano(lengthNanos, lastCheck);
          break;
        case REVERSE:
          //System.out.println("reversing...");
          lastCheck = now;
          lengthNanos = reverseLengthNanos(lengthNanos, lengthNano, 1, waitTime);
          if (lengthNanos.size() % 2 != 0)
          {
            startOn = !startOn;
          }
          index = getIndexAtNano(lengthNanos, lastCheck);
          break;
      }
    } else
    {
      lastCheck = now;
    }

    if (index % 2 == 0) //even
    {
      if (startOn == true)
      {
        isGeomActive = true;
      } else
      {
        isGeomActive = false;
      }
    } else //odd
    {
      if (startOn == true)
      {
        isGeomActive = false;
      } else
      {
        isGeomActive = true;
      }
    }

  //System.out.println("index = " + index);
  }
  */
    
 
  /*
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

    isActive = true;

    now = currentNano - startNano;
    //System.out.println("" + isGeomActive + " : now(" + Utils.nanosToMillis(now) + ") ... len(" + Utils.nanosToMillis(lengthNano) + ")");

    int index = getIndexAtNano(lengthNanos, now);

    if (index < 0)
    {
      lastCheck = now;
      //before anything...
      isGeomActive = false;
      //System.out.println("before anything... index = " + index);
      return;
    }

    //remove if this is the last one (and loopBehavior == ONCE)
    if (index == lengthNanos.size() - 1)
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

          index = getIndexAtNano(lengthNanos, lastCheck);
          break;
        case REVERSE:
          //System.out.println("reversing...");
          lastCheck = now;
          lengthNanos = reverseLengthNanos(lengthNanos, lengthNano, 1, waitTime);
          if (lengthNanos.size() % 2 != 0)
          {
            startOn = !startOn;
          }

          index = getIndexAtNano(lengthNanos, lastCheck);
          break;
      }
    } else
    {
      lastCheck = now;
    }

    if (index % 2 == 0) //even
    {
      if (startOn == true)
      {
        isGeomActive = true;
      } else
      {
        isGeomActive = false;
      }
    } else //odd
    {
      if (startOn == true)
      {
        isGeomActive = false;
      } else
      {
        isGeomActive = true;
      }
    }

  //System.out.println("index = " + index);

  
//  //if (now >= lengthNano) //changing direction, etc.
//  if (now >= lengthNano) //changing direction, etc.
//  {
//  isGeomActive = true;
//  //this.isDone = true;
//  }
//  else if (now < lengthNano) //testing for step through...
//  {
//  isGeomActive = false;
//  }
//   


  }
  */


  
  /*
  @Override
  public void tick(long currentNano)
  {
  //System.out.println(" " + Utils.nanosToMillis(currentNano) + " ... " + Utils.nanosToMillis(startTime) );
  
  if (currentNano < startTime) { return; } //not ready yet
  
  now = currentNano - startTime;
  System.out.println("in BehaviorIsActive : now = " + Utils.nanosToMillis(now));
  //System.out.println(" " + Utils.nanosToMillis(now) + " ... " + Utils.nanosToMillis(lengthNano) );
  if (now >= lengthNano) //changing direction, etc.
  {
  isGeomActive = true;
  if (!BehaviorismDriver.viz.isStepping)
  {
  //this.isDone = true;
  }
  }
  else if (now < 0) //testing for step through...
  {
  isGeomActive = false;
  }
  }
   */
  /*
  public void change(Geom g)
  {
  g.isDone = this.isDone;
  }
  
  public void tick(long currentNano) //no parent
  {
  
  float timePrev = getPercentage(lastCheck);
  long now = (currentNano)- startNano;
  
  if (now < 0L)
  {
  return;
  }
  
  if (lengthNano <= 0L) //ie, turn it on forever
  {
  //g.isGeomActive = true;
  
  //schedule for removal!
  this.isDone = true;
  return;
  }
  
  float timeNow = getPercentage(now);
  
  if (timeNow >= 1.0f) //time to turn off...
  {
  g.isGeomActive = false;
  //c.isGeomActive = true;
  
  //schedule for removal!
  this.isDone = true;
  }
  else
  {
  //c.isGeomActive = false;
  g.isGeomActive = true;
  }
  
  }
   */

  public String toString()
  {
    return "in BehaviorIsActive : class = " + getClass();
  }
}