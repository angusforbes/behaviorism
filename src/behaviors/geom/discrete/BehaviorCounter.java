/* BehaviorCounter.java ~ Aug 18, 2008 */

package behaviors.geom.discrete;

import geometry.Geom;
import geometry.text.GeomText2;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author angus
 */
public class BehaviorCounter extends BehaviorGeomDiscrete
{
  List<?> counts = null;
  int countIndex = 0;
  LoopEnum countBehavior;
  
  public static BehaviorCounter counter(List<?> counts, long baseNano)
  {
    /*
    BehaviorCounter bc = new BehaviorCounter
      (
        new DiscreteBehaviorBuilder(baseNano).loop(LoopEnum.ONCE), counts  
      );
     
    return bc;
     */
   return null;
  }
  
  /*
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
  */
  public static BehaviorCounter counter(List counts, long baseNano, List<Long> mss)
  {
    BehaviorCounter bc = new BehaviorCounter
      (
        new CounterBuilder(baseNano, mss).counts(counts).countBehavior(LoopEnum.REVERSE)
        //counts(counts)
      );
      
    return bc;
  }
  
  public BehaviorCounter(CounterBuilder builder)
  {
    super(builder);
    this.loopBehavior = LoopEnum.LOOP;
    this.counts = builder.counts;
    this.countBehavior = builder.countBehavior;
  }
  
  public static class CounterBuilder extends DiscreteBehaviorBuilder
  {
    private List counts = null;
    private LoopEnum countBehavior = LoopEnum.LOOP;
    
    public CounterBuilder(long startTime, List<Long> lengthMSs)
    {
      super(startTime, lengthMSs);
    }

    public CounterBuilder counts(List counts)
    {
      this.counts = counts;
      return this;
    }
   
    public CounterBuilder countBehavior(LoopEnum countBehavior)
    {
      this.countBehavior = countBehavior;
      return this;
    }
   
  }
  
  @Override
  public void updateGeom(Geom geom)
  {
    System.out.println("count index = " + countIndex);
    if (geom instanceof GeomText2)
    {
      ((GeomText2)geom).text = "" + counts.get(  countIndex % (counts.size() ) );

      countIndex++;
     
      if (countIndex == counts.size())
      {
        if (countBehavior == LoopEnum.ONCE)
        {
          isActive = false;
          isDone = true;
        }
        else if (countBehavior == LoopEnum.REVERSE)
        {
          Collections.reverse(counts);
          countIndex = 1; //will start at second index so we don't get end of the list a seconde time (when it gets moved to the beginning)
        }
        else if (countBehavior == LoopEnum.LOOP)
        {
          countIndex = 0;
        }
        
      }
    }
  }


}
