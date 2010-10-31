/* BehaviorSimple.java ~ Jun 28, 2009 */

package behaviorism.behaviors;

import static behaviorism.utils.RenderUtils.*;

/**
 * BehaviorSimple is a basic Behavior that updates the Geom every frame.
 * There is no notion of interpolation or time. The behavior will
 * run as long as the current time
 * is after the start time and it hasn't been explicitly interrupted.
 * @author angus
 */
public abstract class BehaviorSimpleThread extends Behavior
{

  public BehaviorSimpleThread(long startTime)
  {
    super(startTime);
  }

  public void tick()
  {
    isActive = false;

    if (isInterrupted == true && interruptNano <= getTick())
    {
      this.isDone = true;
      return;
    }

    if (getTick() < nextTime)
    {
      return;
    }

    isActive = true;

    // isDone must be set explicitly by the thread!

    //isDone = true;
  }

   @Override
  public void changeSpeed(float increase)
  {
    nextTime = getTick() + (long)((nextTime - getTick()) * (1f/increase));
  }

}
