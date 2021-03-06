/* EasingSine.java ~ Jun 30, 2009 */
package behaviorism.behaviors.easing;

import behaviorism.utils.Utils;
import java.util.Arrays;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.SplineInterpolator;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealInterpolator;

/**
 *
 * @author angus
 */
public class EasingSpline extends Easing
{
  UnivariateRealFunction function = null;
  double t[];
  double y[];


  public EasingSpline(double[] t, double[] y)
  {
    super(EasingEnum.OUT);
    this.t = t;
    this.y = y;
    initSpline();
  }

  public EasingSpline(int pts, double minY, double maxY)
  {
    super(EasingEnum.OUT);
    initRandomPoints(pts, minY, maxY);
    initSpline();
  }

  public EasingSpline(EasingEnum ease, double[] t, double[] y)
  {
    super(ease);
    this.t = t;
    this.y = y;
    initSpline();
  }

  public EasingSpline(EasingEnum ease, int pts, double minY, double maxY)
  {
    super(ease);

    initRandomPoints(pts, minY, maxY);
    initSpline();
  }

  public void initRandomPoints(int pts, double minY, double maxY)
  {
    t = new double[pts];
    y = new double[pts];

    t[0] = 0;
    y[0] = 0;
    for (int i = 1; i < pts-1; i++)
    {
      t[i] = i * (1/(double)(pts-1));
      y[i] = Utils.random(minY, maxY);
      //System.out.println("t[i] = " + t[i]);
    }
    t[pts-1] = 1;
    y[pts-1] = 1;

  }

  public void initSpline()
  {
    System.out.println("x pts.. " + Arrays.toString(t));

    UnivariateRealInterpolator interpolator = new SplineInterpolator();

    try
    {
    this.function = interpolator.interpolate(t, y);
    }
    catch (MathException me)
    {
      me.printStackTrace();
    }

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
  
}
