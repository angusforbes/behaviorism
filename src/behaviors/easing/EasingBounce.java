/* EasingBounce.java ~ Jun 30, 2009 */
package behaviors.easing;

import static behaviors.easing.Easing.EasingEnum.*;

/**
 *
 * @author angus
 */
public class EasingBounce extends Easing
{
  public int bounce;
  public float decayWidth;
  public float decayHeight;

  protected float a; //width of first n curve

  public EasingBounce()
  {
    super();
    setBounce(15);
    setDecayWidth(.7f);
    setDecayHeight(.5f);
    setCurveWidth(25, .7f);
  }

  public EasingBounce(int bounce, float decay)
  {
    super();
    setBounce(bounce);
    setDecayWidth(decay);
    setDecayHeight(decay);
    setCurveWidth(bounce, decay);
  }

  public EasingBounce(int bounce, float decayW, float decayH)
  {
    super();
    setBounce(bounce);
    setDecayWidth(decayW);
    setDecayHeight(decayH);
    setCurveWidth(bounce, decayW);
  }

  protected void setCurveWidth(float n, float mu)
  {
    // solving for "a", "n" = # bounces, "mu" = dampening decay :
    //  .5*a + mu^1*a + mu^2*a + ... + mu^n*a = 1

    float coeff = .5f;
    for (int i = 1; i < n; i++)
    {
      coeff += power(mu,i);
    }
    
    this.a = 1f / coeff;
  }

  public void setBounce(int bounce)
  {
    this.bounce = bounce;
  }

  public void setDecayWidth(float decay)
  {
    this.decayWidth = decay;
  }

  public void setDecayHeight(float decay)
  {
    this.decayHeight = decay;
  }

  protected float power(float perc, int exponent)
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
    return 1f - out(1f - perc);
  }

  public float out(float perc)
  {
    float place = a * .5f;
    float prevPlace = place;
    float sp = 1f;
    float width = place;
    if ( perc <= place )
    {
      sp = (float) Math.sin((-Math.PI/2) + perc * 1/width * (Math.PI/2)) + 1f;
    }
    else
    {
      for (int i = 1; i < bounce; i++)
      {
        width = power(decayWidth, i) * a;

        place = prevPlace + width;

        if ( perc >= prevPlace  && perc < place)
        {
          sp = (float) Math.sin(-Math.PI + (perc-prevPlace) * 1/width * Math.PI) + 1f;
          sp = 1f - ((1f - sp) * power(decayHeight, i));
        }

        prevPlace = place;
        
      }
    }

    return sp;
  }

  public float inout(float perc)
  {
    
    if (perc < .5f)
    {
      return .5f * out(perc * 2f);
    }
    else
    {
      return .5f + .5f * in((perc - .5f) * 2f);
    }
  }

  public float outin(float perc)
  {

    if (perc < .5f)
    {
      return .5f * in(perc * 2f);
    }
    else
    {
      return .5f + .5f * out((perc - .5f) * 2f);
    }
  }

}
