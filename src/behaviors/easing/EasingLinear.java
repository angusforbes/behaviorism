/* EasingSine.java ~ Jun 30, 2009 */

package behaviors.easing;

/**
 *
 * @author angus
 */
public class EasingLinear extends Easing
{
  public float getPercentage(float rawPercentage)
  {
    return rawPercentage;
  }
}
