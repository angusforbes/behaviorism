/* EasingSine.java ~ Jun 30, 2009 */
package behaviors.easing;

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

 
  public float in(float perc)
  {
    return power(perc, exponent);
  }

  public float out(float perc)
  {
    perc = 1f - perc;
    return 1f - power(perc, exponent);
  }

  public float outin(float perc)
  {
    //haven't done this one yet...
    return inout(perc);
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

}
