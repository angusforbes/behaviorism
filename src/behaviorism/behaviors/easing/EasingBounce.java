/* EasingBounce.java ~ Jun 30, 2009 */
package behaviorism.behaviors.easing;

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
    super(EasingEnum.OUT);
    initBounce(15, .7f, .5f);
}

  public EasingBounce(EasingEnum ease)
  {
    super(ease);
    initBounce(15, .7f, .5f);
  }

  public EasingBounce(int bounce, float decay)
  {
    super(EasingEnum.OUT);
    initBounce(bounce, decay, decay);
  }
  
  public EasingBounce(EasingEnum ease, int bounce, float decay)
  {
    super(ease);
    initBounce(bounce, decay, decay);
  }

  public EasingBounce(int bounce, float decayW, float decayH)
  {
    super(EasingEnum.OUT);
    initBounce(bounce, decayW, decayH);
  }

  public EasingBounce(EasingEnum ease, int bounce, float decayW, float decayH)
  {
    super(ease);
    initBounce(bounce, decayW, decayH);
  }

  public void initBounce(int bounce, float decayW, float decayH)
  {
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

    System.out.println("in setCurveWidth : this.a = " + this.a + ", coeff = " + coeff);
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
    float place = this.a * .5f;
    float prevPlace = place;
    float sp = 1f;
    float width = place;

  //  System.out.println("init place = " + place);
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

   // System.out.println("sp = " + sp + " place/pp = " + place + "/" + prevPlace);
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
