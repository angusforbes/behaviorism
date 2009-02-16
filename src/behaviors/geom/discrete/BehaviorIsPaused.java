/*
 * BehaviorIsActive.java
 * Created on May 12, 2007, 7:04 PM
 */
package behaviors.geom.discrete;

import behaviors.Behavior;
import geometry.Geom;
import java.util.ArrayList;
import java.util.List;
import utils.Utils;

/**
 * The idea of this class is to pause a Behavior or Behaviors for some period of time, either specific
 * that we pass in, or all behaviors associated with a particular geom.
 * @author angus
 */
public class BehaviorIsPaused extends BehaviorGeomDiscrete
{
  protected boolean isBehaviorActive  = false;
  boolean pauseSpecifiedBehaviors = false;
  List<Behavior> behaviorsToPause = new ArrayList<Behavior>();
  boolean pauseChildren = true;

  public static BehaviorIsPaused pauseAtNano(Geom geom, long baseNano, List<Behavior> behaviorsToPause, boolean pauseChildren)
  {
    DiscreteBehaviorBuilder dbb = new DiscreteBehaviorBuilder(baseNano).loop(LoopEnum.ONCE);
    BehaviorIsPausedBuilder bipb = new BehaviorIsPausedBuilder(behaviorsToPause, pauseChildren);

    BehaviorIsPaused bia = new BehaviorIsPaused(dbb, bipb);
    geom.attachBehavior(bia);

    return bia;
  }
  
  public static BehaviorIsPaused pauseAtMillis(Geom geom, long baseNano, long onMS, List<Behavior> behaviorsToPause, boolean pauseChildren)
  {
    DiscreteBehaviorBuilder dbb = new DiscreteBehaviorBuilder(baseNano, onMS).loop(LoopEnum.ONCE);
    BehaviorIsPausedBuilder bipb = new BehaviorIsPausedBuilder(behaviorsToPause, pauseChildren);

    BehaviorIsPaused bia = new BehaviorIsPaused(dbb, bipb);
    geom.attachBehavior(bia);
    return bia;
  }

  public static BehaviorIsPaused pauseBetweenMillis(Geom geom, long baseNano, long onMS, long offMS, List<Behavior> behaviorsToPause, boolean pauseChildren)
  {
    List<Long> mss = new ArrayList<Long>();
    Utils.addTo(mss, onMS, offMS);

    DiscreteBehaviorBuilder dbb = new DiscreteBehaviorBuilder(baseNano, mss).loop(LoopEnum.ONCE);
    BehaviorIsPausedBuilder bipb = new BehaviorIsPausedBuilder(behaviorsToPause, pauseChildren);

    BehaviorIsPaused bia = new BehaviorIsPaused(dbb, bipb);
    geom.attachBehavior(bia);
    return bia;
  }
  
  public static BehaviorIsPaused pauseBetweenMillis(Geom geom, long baseNano, List<Long> mss, List<Behavior> behaviorsToPause, boolean pauseChildren)
  {
    DiscreteBehaviorBuilder dbb = new DiscreteBehaviorBuilder(baseNano, mss).loop(LoopEnum.LOOP);
    BehaviorIsPausedBuilder bipb = new BehaviorIsPausedBuilder(behaviorsToPause, pauseChildren);

    BehaviorIsPaused bia = new BehaviorIsPaused(dbb, bipb);
    geom.attachBehavior(bia);
    return bia;
  }

  public static class BehaviorIsPausedBuilder
  {
    private boolean pauseSpecifiedBehaviors = false;
    private List<Behavior> behaviorsToPause = new ArrayList<Behavior>();
    private boolean pauseChildren = true;

    //fill them yourself
    public BehaviorIsPausedBuilder()
    {
    }

    public BehaviorIsPausedBuilder(List<Behavior> behaviorsToPause, boolean pauseChildren)
    {
      this.pauseChildren = pauseChildren;

      if (behaviorsToPause == null || behaviorsToPause.size() == 0)
      {
        this.pauseSpecifiedBehaviors = false;
      }
      else
      {
        this.behaviorsToPause.addAll(behaviorsToPause);
        this.pauseSpecifiedBehaviors = true;
      }
    }

    /*
    public BehaviorIsPausedBuilder(Behavior behaviorToPause, boolean pauseChildren)
    {
      this.behaviorsToPause.add(behaviorToPause);
      this.pauseSpecifiedBehaviors = true;
      this.pauseChildren = pauseChildren;
    }

    public BehaviorIsPausedBuilder(boolean pauseChildren)
    {
      this.pauseSpecifiedBehaviors = false;
      this.pauseChildren = pauseChildren;
    }
    */

    public BehaviorIsPausedBuilder behaviorToPause(Behavior behaviorToPause)
    {
      this.behaviorsToPause.add(behaviorToPause);
      this.pauseSpecifiedBehaviors = true;
      return this;
    }

    public BehaviorIsPausedBuilder behaviorsToPause(List<Behavior> behaviorsToPause)
    {
      this.behaviorsToPause.addAll(behaviorsToPause);
      this.pauseSpecifiedBehaviors = true;
      return this;
    }

    public BehaviorIsPausedBuilder pauseChildren(boolean pauseChildren)
    {
      this.pauseChildren = pauseChildren;
      return this;
    }
  }


  public BehaviorIsPaused(DiscreteBehaviorBuilder superbuilder, BehaviorIsPausedBuilder builder)
  {
    super(superbuilder);
    initBehaviorIsPaused(builder);
  }

   public void initBehaviorIsPaused(BehaviorIsPausedBuilder builder)
  {
      this.behaviorsToPause = builder.behaviorsToPause;
      this.pauseSpecifiedBehaviors = builder.pauseSpecifiedBehaviors;
      this.pauseChildren = builder.pauseChildren;
  }


  @Override
	public void updateGeom(Geom g)
	{
    /*
    System.out.println("in BehaviorIsPaused.change() : \n" +
            "pauseSpecifiedBehaviors = " + pauseSpecifiedBehaviors + "\n" +
            "pauseChildren = " + pauseChildren);
    */
    if (pauseSpecifiedBehaviors == true)
    {
      for (Behavior b : behaviorsToPause)
      {
        if (b == this) //don't pause the pause controller!
        {
          continue;
        }

        b.isPaused = isBehaviorActive;
      }
    }
    else
    {
      for (Behavior b : g.behaviors)
      {
        if (b == this) //don't pause the pause controller!
        {
          continue;
        }

        b.isPaused = isBehaviorActive;
      }

      if (pauseChildren == true)
      {
          for (Geom child : g.geoms)
          {
            updateGeom(child);
          }

      }
    }
	}

  /*
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
  */

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
    return "in BehaviorIsPaused : class = " + getClass();
  }
}