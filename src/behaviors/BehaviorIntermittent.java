/* BehaviorIntermittent.java ~ Aug 18, 2008 */

package behaviors;

import java.util.List;
import utils.RenderUtils;

/**
 *
 * @author angus
 */
public class BehaviorIntermittent extends BehaviorDiscrete
{
  public BehaviorIntermittent(IntermittentBehaviorBuilder builder)
  {
    //initIntermittentBehavior(builder);
    super(builder);
  }
  
  @Override
  public void tick(long currentNano)
  {
   
    if (currentNano < startTime)
    {
       isActive = false;
       return;
    } //not ready yet

    if (isInterrupted == true && interruptNano <= currentNano)
    {
      isActive = false;
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
      System.out.println("nextIndex = " + nextIndex + ", currentIndex = " + curIndex);

      isActive = !isActive;
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
          System.out.println("onceing... ");
          this.isDone = true;
          this.isActive = false;
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

    System.out.println("...end of tick... curIndex = " + curIndex);
  }

  
  public static class IntermittentBehaviorBuilder extends DiscreteBehaviorBuilder
  {
    private boolean startOn = false;
    private long startNano = 0L;
    private long startTime = 0L;
    private long lengthNano = 0L;
    private List<Long> lengthNanos;
    private long waitTime = 0L;
    private LoopEnum loopBehavior = LoopEnum.ONCE;

    public IntermittentBehaviorBuilder(long startTime)
    {
      super(startTime);
      /*
      System.out.println("new DiscreteBehaviorBuilder...");
      this.startNano = startTime;
      this.startTime = startTime;

      List<Long> lengthMSs = new ArrayList<Long>();
      lengthMSs.add(0L);
      initLengthMSs(lengthMSs);
       */
    }
    
    public IntermittentBehaviorBuilder(long startTime, long onMS)
    {
      super(startTime, onMS);
      /*
      this.startNano = startTime;
      this.startTime = startTime;

      List<Long> lengthMSs = new ArrayList<Long>();
      lengthMSs.add(onMS);
      initLengthMSs(lengthMSs);
       */
    }

    public IntermittentBehaviorBuilder(long startTime, long onMS, long offMS)
    {
      super(startTime, onMS, offMS);
      /*
      this.startNano = startTime;
      this.startTime = startTime;

      List<Long> lengthMSs = new ArrayList<Long>();
      lengthMSs.add(onMS);
      lengthMSs.add(offMS);
      
      initLengthMSs(lengthMSs);
       */
    }

    public IntermittentBehaviorBuilder(long startTime, List<Long> lengthMSs)
    {
      super(startTime, lengthMSs);
      /*
      this.startNano = startTime;
      this.startTime = startTime;
      initLengthMSs(lengthMSs);
       */
    }

    /*
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

    public DiscreteBehaviorBuilder loop(LoopEnum loopBehvaior)
    {
      this.loopBehavior = loopBehvaior;
      return this;
    }

    public DiscreteBehaviorBuilder startOn(boolean startOn)
    {
      this.startOn = startOn;
      return this;
    }
     */
  }

  public void initIntermittentBehavior(IntermittentBehaviorBuilder builder)
  {
    System.out.println("in initIntermittentBehavior()...");
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
    //RenderUtils.getWorld().registerBehavior(this);

  //reverseLengthNanos(lengthNano, lengthNanos);
  }

}
