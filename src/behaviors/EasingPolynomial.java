/* EasingSine.java ~ Jun 30, 2009 */
package behaviors;

import static behaviors.Easing.EasingEnum.*;

/**
 *
 * @author angus
 */
public class EasingPolynomial extends Easing
{
  public int exponent;

  public EasingPolynomial()
  {
    super();
    setExponent(2);
  }

  public EasingPolynomial(int power)
  {
    super();
    setExponent(power);
  }

  public void setExponent(int power)
  {
    this.exponent = power;
  }

  private float power(float perc, int exponent)
  {
    float tmp = perc;
    for (int i = 1; i < exponent; i++)
    {
      tmp *= perc;
    }

    return tmp;
  }

  public float in(float perc)
  {
    return power(perc, exponent);
  }

  public float out(float perc)
  {
    perc = 1f - perc;
    return 1f - power(perc, exponent);
  }

  public float inout(float perc)
  {
    perc /= .5f;
    if (perc < 1)
    {
      return .5f * power(perc, exponent);
    }
    else
    {
      perc -= 2;

      if (exponent % 2 == 0)
      {
        return -.5f * (power(perc, exponent) - 2);
      }
      else
      {
        return .5f * (power(perc, exponent) + 2);
      }
    }
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

      case INOUT:
      default:
        sp = inout(t);
        break;
    }

    //System.out.println("rawPercentage = " + t + " sinPerc = " + sp);

    return sp;
  }
}
