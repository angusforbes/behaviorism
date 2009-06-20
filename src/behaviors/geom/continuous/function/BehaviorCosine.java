package behaviors.geom.continuous.function;

import behaviors.BehaviorContinuous.ContinuousBehaviorBuilder;
import behaviors.geom.continuous.BehaviorGeomContinuous;
import geometry.Geom;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class BehaviorCosine extends BehaviorGeomContinuous
{
  float prevY = 0f;
  float amplitude = 1f;

  public static BehaviorCosine cosine(
    long startTime, 
    long lengthMS, 
    float min, float max)
  {
    return new BehaviorCosine(
      new ContinuousBehaviorBuilder(startTime, lengthMS).
      range(min, max).loop(LoopEnum.REVERSE) );
  }

  public static BehaviorCosine cosine(
    Geom g,
    long startTime, 
    long lengthMS, 
    float min, float max)
  {
    BehaviorCosine bsw = new BehaviorCosine(
      new ContinuousBehaviorBuilder(startTime, lengthMS).
      range(min, max).loop(LoopEnum.REVERSE) );
  
    g.attachBehavior(bsw);

    return bsw;
  }
 
  float offset_val;
  public BehaviorCosine(ContinuousBehaviorBuilder builder)
  {
    super(builder);
    this.offset_val = (float) ( (Math.cos(Math.toRadians(minvals[0] + (startPercent * ranges[0]) ))) );
      
  }
 
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      float curY = getValFromCosineWave(percentage); 
      
      g.translate.x += (curY - prevY);

      prevY = curY;
    }
  }
  

 
  
  public void tick(long currentNano)
  {
    isActive = false;
 
    if (currentNano < startTime) { return; } //not ready yet

    if (isInterrupted == true && interruptNano <= currentNano)
    {
      this.isDone = true;
      //call disposals?

      return;
    }

    isActive = true;

    now = currentNano - startNano;
    
    if (now >= relativeEndNano) //changing direction, etc.
    {
      if (lastCheck < relativeEndNano)
      {
        lastCheck = now;
        
        switch (loopBehavior)
        {
          case ONCE:
            percentage = getPercentage(lengthNano);
            this.isDone = true;
            relativeStartNano = lengthNano;
            return;
          case LOOP:
            if (direction == 1)
            {
              percentage = getPercentage((now - relativeStartNano - lengthNano));
            }
            else if (direction == -1)
            {
              percentage = getPercentage(lengthNano - (now - relativeStartNano - lengthNano));
            }
            relativeStartNano += lengthNano;
            relativeEndNano += lengthNano;
            return;
          case REVERSE:
            if (direction == 1)
            {
              percentage = getPercentage(lengthNano - (now - relativeStartNano - lengthNano));
            }
            else
            {
              percentage = getPercentage((now - relativeStartNano - lengthNano));
            }
            relativeStartNano += lengthNano;
            relativeEndNano += lengthNano;
            direction *= -1;
            return;
        }
      }
    }
    else
    {
      //getting normal percentage...

      if (direction == 1)
      {
        percentage = getPercentage(now - relativeStartNano);
      }
      else if (direction == -1)
      {
        percentage = 1f - getPercentage(now - relativeStartNano);
      }
        
      lastCheck = now;

      return;
    }
  }


/*
      
        
    if (now >= lengthNano) //changing direction, etc.
    {
      //System.out.println("\n direction: " + direction);
      //System.out.println("in BehaviorLine3D.change() : now = " + Utils.nanosToMillis(now));
      percentage = getPercentage(lengthNano) - getPercentage(lastCheck);

      //curY = getValFromSineWave(getPercentage(lengthNano));
      

      if (direction == -1 || loopBehavior == LoopEnum.CONTINUE)
      {
        curY = getValFromSineWave(0f);
        
      //  System.out.println("curY = " + curY);
        offset_y += (curY - prevY);
      //  System.out.println("offset_y = " + offset_y);
        
        prevY = curY;
      }
      else
      {
        curY = getValFromSineWave(1f);
        
      //  System.out.println("curY = " + curY);
        offset_y += (curY - prevY);
      //  System.out.println("offset_y = " + offset_y);
        
        prevY = curY;
        //prevY = getValFromSineWave(1f);
      }
      //System.out.println("new prevY = " + prevY);
      
      //offset_x += (range_x * percentage * direction);
      
      //offset_y += (range_y * percentage * direction);
      //offset_z += (range_z * percentage * direction);

      if (loopBehavior == LoopEnum.ONCE)
      {
        this.isDone = true;
        return;
      }
      else //REVERSE OR CONTINUE OR LOOP
      {
        if (loopBehavior == LoopEnum.CONTINUE)
        {
          prevY = -getValFromSineWave(1f);
          System.out.println("prevY = " + prevY);
        }
     
        //we've gone too far, go in the other direction however far we've overshot...

        //push startNano forward so that we begin anew with percentage = 0f
        startNano += lengthNano; //add length of behavior to starting time
        //so then we also need to push out lastCheck into the past

        lastCheck = now - lengthNano;


        //getting remainder of flipped percentage...
        percentage = getPercentage(lastCheck);

        //prevY = getValFromSineWave(percentage);

        if (loopBehavior == LoopEnum.REVERSE)
        {
          direction *= -1; //switch direction
        }

        if (direction == -1)
        {
          curY = getValFromSineWave(1f - percentage);
        }
        else
        {
          curY = getValFromSineWave(percentage);
        }

      }
    }
    else //(now < lengthNano)
    {
      //getting normal percentage...
      percentage = (getPercentage(now) - getPercentage(lastCheck));

      lastCheck = now;
     
      if (direction == 1)
      {
        curY = getValFromSineWave(getPercentage(now));
      }
      else if (direction == -1)
      {
        curY = getValFromSineWave(1f - getPercentage(now));
      }
    }

    //offset_x += (range_x * percentage * direction);
    offset_y += (curY - prevY);
    //offset_z = 0f;

    prevY = curY;
  }
  */

  
  private float getValFromCosineWave(float perc)
  {
    double angle = (double) (minvals[0] + (perc * ranges[0]));
    double val = Math.cos(Math.toRadians(angle));
    return (float) (val - offset_val) * amplitude;
    //return (float) (val * amplitude);
  }

}
