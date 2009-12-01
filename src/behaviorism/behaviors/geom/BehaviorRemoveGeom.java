/*
 * BehaviorActivateGeom.java
 * Created on May 12, 2007, 7:04 PM
 */
package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorSimple;
import behaviorism.geometry.Geom;
import behaviorism.utils.Utils;

public class BehaviorRemoveGeom extends BehaviorSimple implements GeomUpdater
{

  public boolean updateChildren = true;

  /**
   * Sets the specified Geom to be activated at the specfied time. The Behavior
   * is returned so that it can be interrupted if necessary.
   * @param geom
   * @param baseNano
   * @return The BehaviorDectivateGeom behavior
   */
  public static BehaviorRemoveGeom removeAtNano(Geom geom, long baseNano)
  {
    BehaviorRemoveGeom bia = new BehaviorRemoveGeom(baseNano);
    bia.attachGeom(geom);

    return bia;
  }

  public static BehaviorRemoveGeom removeAtMillis(Geom geom, long baseNano, long onMS)
  {
    BehaviorRemoveGeom bia = new BehaviorRemoveGeom(baseNano + Utils.millisToNanos(onMS));
    bia.attachGeom(geom);

    return bia;
  }

  public BehaviorRemoveGeom(long startTime)
  {
    super(startTime);
  }

  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
   //   System.out.println("in BehaviorRemoveGeom : in updateGeom-- removing...");
      remove(g);
    }
    else
    {
     // System.out.println("not removing...");
    }
  }

  public void remove(Geom g)
  {
    g.isActive = false;
    g.isDone = true;

    if (updateChildren == true)
    {
      for (Geom child : g.geoms)
      {
        remove(child);
      }
    }
  }

  public String toString()
  {
    return "in " + getClass();
  }
}