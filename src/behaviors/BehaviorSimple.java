/* BehaviorSimple.java ~ Jun 28, 2009 */

package behaviors;

import static utils.RenderUtils.*;

/**
 * BehaviorSimple is a basic Behavior that updates the Geom every frame.
 * There is no notion of interpolation or time. The behavior will
 * run as long as the current time
 * is after the start time and it hasn't been explicitly interrupted.
 * @author angus
 */
public abstract class BehaviorSimple extends Behavior
{

  public BehaviorSimple(long startTime)
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
    isDone = true;
  }

   @Override
  public void changeSpeed(float increase)
  {
    nextTime = getTick() + (long)((nextTime - getTick()) * (1f/increase));
  }

}
