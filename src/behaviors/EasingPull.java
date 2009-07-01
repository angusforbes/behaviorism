/* EasingSine.java ~ Jun 30, 2009 */
package behaviors;

import static behaviors.Easing.EasingEnum.*;

/**
 *
 * @author angus
 */
public class EasingPull extends Easing
{
  float pull = 5f; //1.70158f;
  public float getPercentage(float t)
  {
    float sp;

    switch (ease)
    {
      case IN:
        sp = t*t*((pull+1)*t - pull);
        break;

      case OUT:
        t = 1f - t;
        sp = 1f - (t*t*((pull+1)*t - pull));
        break;
    
      case INOUT:
      default:

        t /= .5f;
        float s = pull * 1.525f;
        if (t < 1)
        {
          sp = .5f * (t * t * ((s + 1) * t - s));
        }
        else
        {
          t -= 2;
          sp = .5f * (t * t * ((s + 1) * t + s) + 2);
        }

        break;
    }

    System.out.println("rawPercentage = " + t + " sinPerc = " + sp);

    return sp;
  }

}
