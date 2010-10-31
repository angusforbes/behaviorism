/* EasingBounce.java ~ Jun 30, 2009 */
package behaviorism.behaviors.easing;

/**
 *
 * @author angus
 */
public class EasingElastic extends EasingBounce
{

  public EasingElastic()
  {
    super(15, .5f, .5f);
  }

  public EasingElastic(int bounce, float decay)
  {
    super(bounce, decay);
  }


  public EasingElastic(EasingEnum ease)
  {
    super(ease, 15, .5f, .5f);
  }

  public EasingElastic(EasingEnum ease, int bounce, float decay)
  {
    super(ease, bounce, decay);
  }

  @Override
  public float out(float perc)
  {
    float place = a * .5f;
    float prevPlace = place;
    float sp = 1f;
    float width = place;
    if (perc <= place)
    {
      //System.out.println("n 0");
      sp = (float) Math.sin((-Math.PI / 2) + perc * 1 / width * (Math.PI / 2)) + 1f;
    }
    else
    {
      for (int i = 1; i < bounce; i++)
      {
        width = power(decayWidth, i) * a;

        place = prevPlace + width;

        if (perc >= prevPlace && perc < place)
        {
          if (i % 2 == 0)
          {
            sp = (float) Math.sin(-Math.PI + (perc - prevPlace) * 1 / width * Math.PI) + 1f;

          }
          else
          {
            sp = (float) Math.sin(Math.PI - (perc - prevPlace) * 1 / width * Math.PI) + 1f;
          }

          sp = 1f - ((1f - sp) * power(decayHeight, i));
        }

        prevPlace = place;

      }
    }
    return sp;
  }
}
