/* Easing.java ~ Jun 30, 2009 */

package behaviorism.behaviors.easing;

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

  public EasingEnum ease;


  public Easing()
  {
    ease(EasingEnum.OUT);
  }

  public Easing(EasingEnum ease)
  {
    ease(ease);
  }

  final public void ease(EasingEnum ease)
  {
    this.ease = ease;
  }

  public float getPercentage(float t)
  {
    float sp;

    switch (ease)
    {
      case IN:
        sp = in(t);
        break;

      case OUT:
        sp = out(t);
        break;

      case OUTIN:
        sp = outin(t);
        break;
    
      case INOUT:
      default:
        sp = inout(t);
        break;
    }

    //System.out.printf("rawPercentage = %f, sinPerc = %f \n", t, sp);

    return sp;
  }

  abstract public float in(float perc);
  abstract public float out(float perc);
  abstract public float inout(float perc);
  abstract public float outin(float perc);

  protected float power(float perc, int exponent)
  {
    float tmp = perc;
    for (int i = 1; i < exponent; i++)
    {
      tmp *= perc;
    }

    return tmp;
  }

}
