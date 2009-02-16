/* SineWave.java (created on September 13, 2007, 10:32 PM) */

package functions;

public class SineWave implements Function
{
  float minAng;
  float maxAng;
  float amplitude;
  float phase;
  
  float range_ang;
  
  public SineWave(float minAng, float maxAng, float amplitude)
  {
    this.minAng = minAng;
    this.maxAng = maxAng;
    this.amplitude = amplitude;
    this.phase = 0f;

    this.range_ang = maxAng - minAng;
  }

  public float getValFromSineWave(float perc)
  {
    double angle = (double) (minAng + (perc * range_ang));
    double val = Math.sin(Math.toRadians(angle));
    return (float) (val * amplitude);
  }
  
  public float getValFromSineWave(float perc, float offset)
  {
    double angle = (double) (minAng + (perc * range_ang));
    double val = Math.sin(Math.toRadians(angle));
    return (float) (val - offset) * amplitude;
  }

  @Override
  public float function(float ... vals)
  {
    if (vals.length != 1)
    {
      throw new UnsupportedOperationException("Error in SineWave function : Expecting 1 value");
    }

    return getValFromSineWave(vals[0]);
  }
}
