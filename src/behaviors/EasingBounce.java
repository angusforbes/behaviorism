/* EasingSine.java ~ Jun 30, 2009 */
package behaviors;

import static behaviors.Easing.EasingEnum.*;

/**
 *
 * @author angus
 */
public class EasingBounce extends Easing
{
  public int exponent;

  public EasingBounce()
  {
    super();
    setExponent(2);
  }

  public EasingBounce(int power)
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
    //from a book on flash-- would rather the user could select a bounce method
		if (perc < 1/2.75)
    {
			return (7.5625f*perc*perc);
		}
    else if (perc < 2/2.75)
    {
      perc -= 1.5/2.75;
			return (7.5625f * perc * perc + .75f);
		}
    else if (perc < 2.5/2.75)
    {
      perc -= 2.25/2.75;
			return (7.5625f * perc * perc + .9375f);
		}
    else
    {
      perc -= 2.625/2.75;
			return (7.5625f * perc * perc + .984375f);
		}
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
