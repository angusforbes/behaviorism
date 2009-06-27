package behaviors;

import behaviors.Behavior.LoopEnum;
import behaviors.BehaviorGeom.BehaviorBuilder;
import java.util.List;
import utils.Utils;

/**
 *
 * @author angus
 */
abstract public class BehaviorContinuous extends Behavior
{

  public float[] minvals; //not needed for most... (see BehaviorSineWave, etc)
  public float[] ranges;
  public float[] offsets;

  public BehaviorContinuous()
  {}
  
  public BehaviorContinuous(ContinuousBehaviorBuilder builder)
  {
    initContinuousBehavior(builder);
  }

  @Override
  public void pause(long nano)
  {
    super.pause(nano); 
    resetOffsets();
  }


  //temp this should be final!
  final public float[] getOffsets()
  //public float[] getOffsets()
  {
    return offsets;
  }

  final public float[] getRanges()
  {
    return ranges;
  }

  final protected void resetOffsets()
  {
    for (int i = 0; i < ranges.length; i++)
    {
      offsets[i] = 0f;
    }
  }

  final protected void addToOffsets(float percentage, int direction)
  {

    for (int i = 0; i < ranges.length; i++)
    {
      //System.out.println("in adding to offsets : range " + i + " : " + ranges[i]);
      offsets[i] += ranges[i] * percentage * direction;
    //System.out.println("adding to offsets : " + i + " : " + offsets[i]);
    }
  }

  final protected void subtractFromOffsets(float percentage, int direction)
  {
    for (int i = 0; i < ranges.length; i++)
    {
      offsets[i] -= ranges[i] * percentage * direction;
    }
  }

  //probably should be protected.
  //or better yet should be final protected. who else would need to call him?
  //maybe when being paused... look into...
  @Override
  public void tick(long currentNano)
  {
    isActive = false;

    resetOffsets();

    if (isInterrupted == true && interruptNano <= currentNano)
    {
      this.isDone = true;
      //call disposals?
      return;
    }

    if (currentNano < startTime)
    {
      return;
    } //not ready yet

    //if loopCount < 0 then loop forever
    if (loopBehavior.howManyTimes > 0 && loopCount >= loopBehavior.howManyTimes)
    {
      this.isDone = true;
      //call disposals?
      return;
    }

    isActive = true;

    now = currentNano - startNano;
    if (now >= relativeEndNano) //changing direction, etc.
    {
      //System.out.println("A0");
      if (lastCheck < relativeEndNano)
      {
        //System.out.println("A1");
        percentage = getPercentage(lengthNano) - getPercentage(lastCheck - relativeStartNano);
        addToOffsets(percentage, direction);
      }

      lastCheck = now;

      switch (loopBehavior)
      {
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
          loopCount++;

          return;
      }
    }
    else if (now <= relativeStartNano)
    {
      //System.out.println("A4");
      if (lastCheck > relativeStartNano)
      {
        percentage = getPercentage(lastCheck - relativeStartNano);
        subtractFromOffsets(percentage, direction);
      }

      lastCheck = now;
      switch (loopBehavior)
      {
        case ONCE: //do nothing - return without updating
          relativeStartNano = 0L;
          return;

        case LOOP:
          if (relativeStartNano - lengthNano >= 0L)
          {
            relativeStartNano -= lengthNano;
            relativeEndNano -= lengthNano;
          }
          else
          {
            return;
          }

          addToOffsets(1.0f, direction);

          //getting remainder of flipped percentage...
          percentage = getPercentage(lastCheck - relativeEndNano);

          addToOffsets(percentage, direction);
          return;

        case REVERSE:
          if (relativeStartNano - lengthNano >= 0L)
          {
            relativeStartNano -= lengthNano;
            relativeEndNano -= lengthNano;
          }
          else
          {
            return;
          }

          //getting remainder of reversed percentage...
          percentage = getPercentage(relativeEndNano - lastCheck);
          direction *= -1; //switch direction
          addToOffsets(percentage, direction);
          return;
      }
    }
    else
    {
      //getting normal percentage...
      if (lastCheck - relativeStartNano < 0)
      {
        percentage = getPercentage(now - relativeStartNano);
      }
      else
      {
        percentage = (getPercentage(now - relativeStartNano) - getPercentage(lastCheck - relativeStartNano));
      }
      lastCheck = now;

      //System.out.println("X: lastCheck = " + Utils.nanosToMillis(lastCheck));
      //System.out.println("X: percentage = " + percentage);

      addToOffsets(percentage, direction);
      return;
    }
  }

  public static class ContinuousBehaviorBuilder extends BehaviorBuilder
  {
    private float minvals[] = null;
    private float ranges[] = null;
    private float offsets[] = null;
    private List<AccelerationPoint> accelerationPoints = null;

    public ContinuousBehaviorBuilder(long lengthMS)
    {
      super(lengthMS);
    }

    public ContinuousBehaviorBuilder(long startTime, long lengthMS)
    {
      super(startTime, lengthMS);
    }

    //i think the two above are the only ones i really want to keep!



    public ContinuousBehaviorBuilder(long lengthMS, float[] ranges)
    {
      super(lengthMS);
      ranges(ranges);
    }

    public ContinuousBehaviorBuilder(long lengthMS, float range)
    {
      super(lengthMS);
      range(range);
    }

    public ContinuousBehaviorBuilder(long startTime, long lengthMS, float[] ranges)
    {
      super(startTime, lengthMS);
      ranges(ranges);
    }

    public ContinuousBehaviorBuilder(long startTime, long lengthMS, float range)
    {
      super(startTime, lengthMS);
      range(range);
    }
    //let's get rid of these above constructors-- kind of overkill...


    public ContinuousBehaviorBuilder loop(LoopEnum loopBehavior)
    {
      this.loopBehavior = loopBehavior;
      return this;
    }

    public ContinuousBehaviorBuilder startPercent(long startPercent)
    {
      this.startPercent = startPercent;
      return this;
    }

    public ContinuousBehaviorBuilder ranges(float ranges[])
    {
      this.ranges = ranges;
      this.minvals = new float[ranges.length];
      this.offsets = new float[ranges.length];
      
      return this;
    }

    public ContinuousBehaviorBuilder range(float range)
    {
      return ranges( new float[]
        {
          range
        });
    }
       
    public ContinuousBehaviorBuilder range(float min, float max)
    {
      this.ranges = new float[]
        {
          max - min
        };
      this.minvals = new float[]
        {
          min
        };
     
      this.offsets = new float[1];
      return this;
    }

    public ContinuousBehaviorBuilder ranges(float[] min, float max[])
    {
      if (max.length != min.length)
      {
        System.out.println("ERROR!!!");
      //handle this later...
      }

      this.ranges = new float[max.length];
      this.minvals = new float[max.length];
      for (int i = 0; i < ranges.length; i++)
      {
        this.ranges[i] = max[i] - min[i];
        this.minvals[i] = min[i];
      }

      this.offsets = new float[ranges.length];

      return this;
    }

    public ContinuousBehaviorBuilder accelerationPoints(List<AccelerationPoint> accelerationPoints)
    {
      this.accelerationPoints = accelerationPoints;
      return this;
    }
  }

  final public void initContinuousBehavior(ContinuousBehaviorBuilder builder)
  {
    if (builder.startPercent < 0f || builder.startPercent > 1f)
    {
      System.err.println("startPercent must be between 0f and 1f!");
    }

    this.startTime = builder.startTime; //nothing will happen before the startTime
    this.lengthNano = builder.lengthNano; //length of one loop of the behavior
    this.startPercent = builder.startPercent; //where in the loop the behavior begins
    this.loopBehavior = builder.loopBehavior; //the looping behvior
    this.ranges = builder.ranges;
    this.offsets = builder.offsets;
    this.minvals = builder.minvals;
    
    //determine what will happen after startTime... ie for startPercent != 0f
    this.startNano = this.startTime - (long) (this.lengthNano * this.startPercent);
    this.lastCheck = (long) (this.lengthNano * startPercent);

    relativeStartNano = 0L;
    relativeEndNano = lengthNano;

    setAccelerationPoints(builder.accelerationPoints);

    /* I don't think we need to register behaviors globally */
    //Behaviorism.renderer.currentWorld.registerBehavior(this);
  }
}
