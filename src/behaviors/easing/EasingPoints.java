/* EasingSine.java ~ Jun 30, 2009 */
package behaviors.easing;

import utils.Utils;

/**
 * Linear interpolation between a set of temporal points and their
 * @author angus
 */
public class EasingPoints extends Easing
{

  double[] t;
  double[] y;

  /**
   * Set up the linear interpolation based off of a set of functions of time.
   * The arrays holding temporal points and their values must be the same length.
   * @param t The array of temporal points, normalized so that they increase from 0f to 1f.
   * @param y The values of those points (generally t[0] = 0f and t[n] = 1f.
   */
  public EasingPoints(double[] t, double[] y)
  {
    new EasingPoints(EasingEnum.OUT, t, y);
  }

  public EasingPoints(EasingEnum ease, double[] t, double[] y)
  {
    super(ease);
    this.t = t;
    this.y = y;
  }

  /**
   * Create an interplator from a set of random values between minY and maxY.
   * @param pts
   * @param minY
   * @param maxY
   */
  public EasingPoints(int pts, double minY, double maxY)
  {
    new EasingPoints(EasingEnum.OUT, pts, minY, maxY);
  }

  public EasingPoints(EasingEnum ease, int pts, double minY, double maxY)
  {
    super(ease);

    t = new double[pts];
    y = new double[pts];

    t[0] = 0;
    y[0] = 0;
    for (int i = 1; i < pts-1; i++)
    {
      t[i] = i * (1/(double)(pts-1));
      y[i] = Utils.random(minY, maxY);
    }
    t[pts-1] = 1;
    y[pts-1] = 1;
  }

  //could use a binary search if it seems necessary.
  public float out(float perc)
  {
    if (perc == 0f)
    {
      return 0f;
    }

    double prevt = 0f;
    double prevy = 0f;
    for (int i = 1; i < t.length; i++)
    {
      if (perc < t[i])
      {
        double width = t[i] - prevt;
        double height = y[i] - prevy;
        return (float) (prevy + (((perc - prevt) / width) * height));
      }

      prevt = t[i];
      prevy = y[i];
    }

    return 1f;
  }

  public float in(float perc)
  {
    return 1 - out(1 - perc);
  }

  public float outin(float perc)
  {
    if (perc < .5f)
    {
      return out(perc * 2) * .5f;
    }
    else
    {
      return in(perc * 2f - 1f) * .5f + .5f;
    }
  }

  public float inout(float perc)
  {
    if (perc < .5f)
    {
      return in(perc * 2) * .5f;
    }
    else
    {
      return out(perc * 2f - 1f) * .5f + .5f;
    }
     
  }
}
