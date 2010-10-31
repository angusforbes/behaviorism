/* EasingSine.java ~ Jun 30, 2009 */
package behaviorism.behaviors.easing;

/**
 *
 * @author angus
 */
public class EasingSine extends Easing
{
 public EasingSine(EasingEnum ease)
  {
    super(ease);
  }

  public EasingSine()
  {
    super(EasingEnum.OUT);
  }

  public float in(float perc)
  {
    return (float) -Math.cos(perc * (Math.PI * .5)) + 1;
  }

  public float out(float perc)
  {
    return (float) Math.sin(perc * Math.PI * .5);
  }

  public float inout(float perc)
  {
    return (float) (-.5 * (Math.cos(Math.PI * perc) - 1));
  }

  public float outin(float perc)
  {
    if (perc < .5f)
    {
      return (float) (.5 * Math.sin(Math.PI * perc));
    }
    else
    {
      return 1f - (float) (.5 * Math.sin(Math.PI * perc));
    }

  }

}
