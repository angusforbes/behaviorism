/* EasingSine.java ~ Jun 30, 2009 */
package behaviors;

import java.util.Arrays;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.SplineInterpolator;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealInterpolator;
import utils.Utils;
import static behaviors.Easing.EasingEnum.*;

/**
 *
 * @author angus
 */
public class EasingSpline extends Easing
{
  UnivariateRealFunction function = null;

  public EasingSpline()
  {
    super();
    int pts = 5;
    double x[] = { 0.0, .25, .5, .75, 1.0 };
    double y[] = { 0.0, .7, .5, .1, 1.0 };

//    double x[] = new double[pts ];//{ 0.0, .25, .5, .75, 1.0 };
//    double y[] = new double[pts ]; //{  };
//
//
//    x[0] = 0;
//    y[0] = 0;
//    for (int i = 1; i < pts-1; i++)
//    {
//      x[i] = i * (1/(double)(pts-1));
//      y[i] = Utils.random(-1,1);
//      System.out.println("x[i] = " + x[i]);
//    }
//    x[pts-1] = 1;
//    y[pts-1] = 1;

    System.out.println("x pts.. " + Arrays.toString(x));

    UnivariateRealInterpolator interpolator = new SplineInterpolator();

    try
    {
    this.function = interpolator.interpolate(x, y);
    }
    catch (MathException me)
    {
      me.printStackTrace();
    }

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
    double iX = perc;
    double iY = perc;
    try
    {
       iY = function.value(iX);
    }
    catch(FunctionEvaluationException fee)
    {
      fee.printStackTrace();
    }
    //System.out.println("f(" + iX + ") = " + iY);
    return (float)iY;
  }

  public float out(float perc)
  {
    return 1 - in(1 - perc);
  }

  public float inout(float perc)
  {
    	if (perc < .5f)
      {
        return in(perc*2) * .5f;
      }
      else
      {
        return out(perc*2f-1f) * .5f + .5f;
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
