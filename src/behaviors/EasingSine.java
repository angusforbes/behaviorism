/* EasingSine.java ~ Jun 30, 2009 */
package behaviors;

import static behaviors.Easing.EasingEnum.*;

/**
 *
 * @author angus
 */
public class EasingSine extends Easing
{

  public float getPercentage(float rawPercentage)
  {
    float sp;

    switch (ease)
    {
      case IN:
       	sp = (float) -Math.cos(rawPercentage * (Math.PI * .5)) + 1;
        break;

      case OUT:
        sp = (float) Math.sin(rawPercentage * Math.PI * .5);
        break;

      case OUTIN:
        if (rawPercentage < .5)
        {
          sp = (float) (.5 * (Math.sin(Math.PI * (rawPercentage))));
        }
        else
        {
          sp = 1f - (float) (.5 * (Math.sin(Math.PI * (rawPercentage))));
        }
        break;

      case INOUT:
      default:
        sp = (float) (-.5 * (Math.cos(Math.PI * rawPercentage) - 1));
        break;

    }

    System.out.println("rawPercentage = " + rawPercentage + " sinPerc = " + sp);

    return sp;
  }

  
}
