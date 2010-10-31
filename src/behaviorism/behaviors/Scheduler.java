/* Scheduler.java ~ Dec 15, 2009 */

package behaviorism.behaviors;

import behaviorism.data.Data;
import behaviorism.data.Node;
import behaviorism.geometry.Geom;
import behaviorism.utils.Utils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Each Node and Geom has a list of the behaviors that are affecting it.
 * 
 * Each Behavior has a list of the Nodes and Geoms that it is affecting.
 * 
 * Each Behavior also has a list of the Behaviors it is affecting, 
 * and also each Behavior has a list of the Behaviors that are affecting it.
 *
 * Geom --> behaviors
 * Node --> behaviors
 * Behavior --> behaviors, and ...
 *  attachedGeoms
 *  attachedNodes
 *  attachedBehaviors
 *
 * @author angus
 */
public class Scheduler 
{
  private static final Scheduler instance = new Scheduler();

  public List<Behavior> behaviors = new CopyOnWriteArrayList<Behavior>();

  /**
   * Gets the singleton Scheduler.
   * @return the singleton Scheduler.
   */
  public static Scheduler getInstance()
  {
    return instance;
  }

  private Scheduler()
  {}

  /**
   * 
   * @param b The Behavior we are scheduling
   * @param autoRemove Whether or not the behavior should be removed when it is done (why would we
   * ever NOT do this???)
   * @return false if this behavior was already scheduled, true if it was successfully scheduled
   */
  public boolean schedule(Behavior b, boolean autoRemove)
  {
    if (b.isScheduled == false)
    {
      b.autoRemove = autoRemove;
      this.behaviors.add(b);
      b.isScheduled = true;
      return true; //was successfully scheduled
    }

    return false; //was already scheduled
  }

  public void schedule(Behavior b)
  {
    schedule(b, true);
  }

  public void unschedule(Behavior b)
  {
    //1. remove the links from the elements effected by this behavior to the behavior itself
    for (Geom g : b.attachedGeoms)
    {
      g.behaviors.remove(b);
    }
    for (Behavior b2 : b.attachedBehaviors)
    {
      b2.behaviors.remove(b);
    }
    for (Node n : b.attachedNodes)
    {
      n.behaviors.remove(b);
    }

    //2. remove the behavior from the scheduler
    this.behaviors.remove(b);

    //3. dispose of the behavior (this is optional, dispose method by default doesn't do anything)
    b.isActive = (false);
    b.dispose();
  }

  public void detachGeom(Behavior b, Geom ... gs)
  {
    for (Geom g : gs)
    {
      b.attachedGeoms.remove(g);
      g.behaviors.remove(b);
    }

    //remove g from the behavior's list of geoms
    //remove b from the geom's list of behaviors
  }

  public void attachGeom(Behavior b, Geom ... gs)
  {
    for (Geom g : gs)
    {
      b.attachedGeoms.add(g);
      g.behaviors.add(b);
    }
    //add g to the behavior's list of geoms
    //add b to the geom's list of behaviors that are affecting it

    //schedule behavior (if it is not already scheduled)

    //attachedGeoms.add(g);
    schedule(b, true);
  }

  public void detachBehavior(Behavior parent, Behavior ... children)
  {
    for (Behavior b : children) {
      parent.attachedBehaviors.remove(b);
      b.behaviors.remove(parent);
    }
    //remove child from the parent's list of behaviors
    //remove parent from the child's list of behaviors that are affecting it
  }

  public void attachBehavior(Behavior parent, Behavior ... children)
  {
    for (Behavior b : children)
    {
      parent.attachedBehaviors.add(b);
      b.behaviors.add(parent);
    }
    //add bchild to the parent's list of attached behaviors
    //add bparent to the child's list of behaviors that are affecting it

    //schedule behavior (if it is not already scheduled)
    schedule(parent, true);
  }

  public void detachData(Data d, Behavior parent)
  {
    //remove d from the behavior's list of datas
    //remove b from the data's list of behaviors
  }

  public void attachData(Data d, Behavior parent)
  {
    //add d to the behavior's list of datas
    //add b to the data's list of behaviors that are affecting it

    //schedule behavior (if it is not already scheduled)
  }

  public void interruptImmediately(Behavior b)
  {
    b.interrupt(0);
  }

  public void interrupt(Behavior b, long nano)
  {
    b.interrupt(nano);
  }

  public void interruptNowPlusMillis(Behavior b, long millis)
  {
    b.interrupt(Utils.nowPlusMillis(millis));
  }

  public void interruptNanoPlusMillis(Behavior b, long baseNano, long millis)
  {
    b.interrupt(Utils.nanoPlusMillis(baseNano, millis));
  }


}
