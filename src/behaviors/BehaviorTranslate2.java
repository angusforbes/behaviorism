package behaviors;

import behaviors.geom.GeomUpdater;
import geometry.Geom;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

/**
 *
 * @author angus
 */
public class BehaviorTranslate2 extends BehaviorRange implements GeomUpdater
{
 
  public BehaviorTranslate2(long startTime, long lengthMS, Point3f p3f)
  {
    super(startTime, lengthMS, MatrixUtils.toArray(p3f));
  }
  
  @Override
  public void updateGeom(Geom g)
  {
    if (isActive == true)
    {
      g.translate(offsets[0], offsets[1], offsets[2]);
    }
  }
}
