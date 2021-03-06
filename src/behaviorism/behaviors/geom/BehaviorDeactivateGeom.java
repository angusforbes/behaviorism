/*
 * BehaviorActivateGeom.java
 * Created on May 12, 2007, 7:04 PM
 */
package behaviorism.behaviors.geom;

import behaviorism.behaviors.Scheduler;
import behaviorism.geometry.Geom;
import behaviorism.utils.Utils;

public class BehaviorDeactivateGeom extends BehaviorActivateGeom implements GeomUpdater
{

  /**
   * Sets the specified Geom to be activated at the specfied time. The Behavior
   * is returned so that it can be interrupted if necessary.
   * @param geom
   * @param baseNano
   * @return The BehaviorDectivateGeom behavior
   */
  public static BehaviorDeactivateGeom deactivateAtNano(Geom geom, long baseNano)
  {
    BehaviorDeactivateGeom bia = new BehaviorDeactivateGeom(baseNano);
    //bia.attachGeom(geom);
    Scheduler.getInstance().attachGeom(bia, geom);

    return bia;
  }

  public static BehaviorDeactivateGeom deactivateAtMillis(Geom geom, long baseNano, long onMS)
  {
    BehaviorDeactivateGeom bia = new BehaviorDeactivateGeom(baseNano + Utils.millisToNanos(onMS));
    //bia.attachGeom(geom);
    Scheduler.getInstance().attachGeom(bia, geom);

    return bia;
  }

  public BehaviorDeactivateGeom(long startTime)
  {
    super(startTime);
  }

  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      deactivate(g);
    }
  }

  public void deactivate(Geom g)
  {
    //System.out.println("WE HAVE BEEN DEACTIVATED!");
    g.isActive = false;

    if (updateChildren == true)
    {
      for (Geom child : g.geoms)
      {
        deactivate(child);
      }
    }
  }

  /*
  @Override
  public void updateGeom(Geom g)
  {
  g.isActive = false;

  if (updateChildren == true)
  {
  for (Geom child : g.geoms)
  {
  updateGeom(child);
  }
  }
  }
   */
  public String toString()
  {
    return "in BehaviorDeactivate : class = " + getClass();
  }
}
