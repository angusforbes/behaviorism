/* BehaviorIsDone.java ~ Aug 31, 2008 */

package behaviors.geom.discrete;

import geometry.Geom;

/**
 *
 * @author angus
 */
public class BehaviorIsDone extends BehaviorGeomDiscrete
{
  public boolean destroyChildren = false;
  
  public static BehaviorIsDone destroyAtNano(Geom geom, long baseNano, boolean destroyChildren)
  {
    BehaviorIsDone bia = new BehaviorIsDone
      (
        new DiscreteBehaviorBuilder(baseNano).loop(LoopEnum.ONCE), destroyChildren
      );
      
    geom.attachBehavior(bia);

    return bia;
  }
  
   public static BehaviorIsDone destroyAtMillis(Geom geom, long baseNano, long millis, 
     boolean destroyChildren)
  {
    BehaviorIsDone bia = new BehaviorIsDone
      (
        new DiscreteBehaviorBuilder(baseNano, millis).loop(LoopEnum.ONCE), destroyChildren 
      );
      
    geom.attachBehavior(bia);
    return bia;
  }

  public BehaviorIsDone(DiscreteBehaviorBuilder builder, boolean destroyChildren)
  {
    super(builder);
    this.destroyChildren = destroyChildren;
  }
 
   @Override
  public void updateGeom(Geom g)
  {
   
    if (destroyChildren == true)
    {
      for (Geom child : g.geoms)
      {
        updateChildGeom(child);
      }
    }
  
    g.isDone = true;
   
  }

  public void updateChildGeom(Geom g)
  {
    for (Geom child : g.geoms)
    {
      updateChildGeom(child);
    }
    g.isDone = true;
  }

}
