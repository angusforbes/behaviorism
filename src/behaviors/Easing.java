/* Easing.java ~ Jun 30, 2009 */

package behaviors;

/**
 *
 * @author angus
 */
public abstract class Easing
{
  public enum EasingEnum
  {
    IN, OUT, INOUT, OUTIN
  };

  public EasingEnum ease = EasingEnum.INOUT;

  abstract public float getPercentage(float rawPercentage);
}
