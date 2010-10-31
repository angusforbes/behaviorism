/* BehaviorSimpleGeom.java ~ Dec 11, 2009 */

package behaviorism.behaviors.geom;

import behaviorism.behaviors.BehaviorSimple;
import behaviorism.geometry.Geom;

/**
 * This is meant to be overriden for simple Behaviors define as anonymous classes, etc.
 * @author angus
 */
public class BehaviorSimpleGeom extends BehaviorSimple implements GeomUpdater
{
  public BehaviorSimpleGeom(long startTime)
  {
    super(startTime);
  }

  public void updateGeom(Geom geom)
  {

  }
}
