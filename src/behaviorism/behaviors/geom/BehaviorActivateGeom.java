/*
 * BehaviorActivateGeom.java
 * Created on May 12, 2007, 7:04 PM
 */
package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorSimple;
import behaviorism.behaviors.Scheduler;
import behaviorism.geometry.Geom;
import behaviorism.utils.Utils;

public class BehaviorActivateGeom extends BehaviorSimple implements GeomUpdater
{

  public boolean updateChildren = true;

  /**
   * Sets the specified Geom to be activated at the specfied time. The Behavior
   * is returned so that it can be interrupted if necessary.
   * @param geom
   * @param baseNano
   * @return The BehaviorActivateGeom
   */
  public static BehaviorActivateGeom activateAtNano(Geom geom, long baseNano)
  {
    BehaviorActivateGeom bia = new BehaviorActivateGeom(baseNano);
    //bia.attachGeom(geom);

    Scheduler.getInstance().attachGeom(bia, geom);

    return bia;
  }

  public static BehaviorActivateGeom activateAtMillis(Geom geom, long baseNano, long onMS)
  {
    BehaviorActivateGeom bia = new BehaviorActivateGeom(baseNano + Utils.millisToNanos(onMS));
    //bia.attachGeom(geom);
    Scheduler.getInstance().attachGeom(bia, geom);

    return bia;
  }


  /*
  public static BehaviorActivateGeom activateBetweenMillis(Geom geom, long baseNano, long onMS, long offMS)
  {
  geom.isActive = false;

  List<Long> mss = new ArrayList<Long>();
  Utils.addTo(mss, onMS, offMS);

  BehaviorActivateGeom bia = new BehaviorActivateGeom(
  new DiscreteBehaviorBuilder(baseNano, mss).loop(LoopEnum.LOOP));

  geom.attachBehavior(bia);
  return bia;
  }

  public static BehaviorActivateGeom activateBetweenMillis(Geom geom, long baseNano, List<Long> mss)
  {
  geom.isActive = false;

  BehaviorActivateGeom bia = new BehaviorActivateGeom(
  new DiscreteBehaviorBuilder(baseNano, mss).loop(LoopEnum.LOOP));

  geom.attachBehavior(bia);
  return bia;
  }
   */
  public BehaviorActivateGeom(long startTime)
  {
    super(startTime);

    //System.out.println("we will activate in " + Utils.nanosToMillis((startTime - Utils.now())));
  }

  public void updateGeom(Geom g)
  {
    System.err.println("IS ACTIVE = " + isActive);
    if (isActive == true) //that is, if BEHAVIOR is active (i think)
    //if (isActive == false) //huh?
    {
      activate(g);
    }
  }

  public void activate(Geom g)
  {
    //System.out.println("WE HAVE BEEN ACTIVATED!");
    g.isActive = true;
    g.isDone = false;

    if (updateChildren == true)
    {
      for (Geom child : g.geoms)
      {
        activate(child);
      }
    }

  }

  /*
  //might want to put this in super Behavior class, or maybe a BehaviorUtils
  protected void updateChildGeom(Geom g, boolean isParentActive)
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
  public String toString()
  {
    return "in BehaviorIsActive : class = " + getClass();
  }
}
