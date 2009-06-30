/* BehaviorSimple.java ~ Jun 28, 2009 */

package behaviors;

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

  public void tick(long currentNano)
  {
    isActive = false;

    if (isInterrupted == true && interruptNano <= currentNano)
    {
      this.isDone = true;
      return;
    }

    if (currentNano < startTime)
    {
      return;
    }

    isActive = true;
  }

}
