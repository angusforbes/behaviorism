/* EasingSine.java ~ Jun 30, 2009 */
package behaviors.easing;

/**
 *
 * @author angus
 */
public class EasingPull extends Easing
{

  float pull = 5f; //1.70158f;

  public float in(float t)
  {
    return t * t * ((pull + 1) * t - pull);
  }

  public float out(float t)
  {
    t = 1f - t;
    return 1f - (t * t * ((pull + 1) * t - pull));
  }

  public float inout(float t)
  {
    t /= .5f;
    float s = pull * 1.525f;
    if (t < 1)
    {
      return .5f * (t * t * ((s + 1) * t - s));
    }
    else
    {
      t -= 2;
      return .5f * (t * t * ((s + 1) * t + s) + 2);
    }
  }

  public float outin(float t)
  {
    //haven't done this one yet...
    return inout(t);
  }
}
