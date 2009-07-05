/* EasingSine.java ~ Jun 30, 2009 */

package behaviors.easing;

/**
 *
 * @author angus
 */
public class EasingLinear extends Easing
{
  public EasingLinear()
  {
    super();
  }

  public EasingLinear(EasingEnum ease)
  {
    super(ease);
  }

  public float in(float perc)
  {
    return perc;
  }
  public float out(float perc)
  {
    return perc;
  }
  public float inout(float perc)
  {
    return perc;
  }
  public float outin(float perc)
  {
    return perc;
  }

}
