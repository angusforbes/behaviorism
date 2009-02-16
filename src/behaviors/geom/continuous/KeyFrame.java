/* KeyFrame.java (created on August 15, 2007, 4:30 PM) */

package behaviors.geom.continuous;

import java.util.List;
import javax.vecmath.Point3f;

public class KeyFrame
{
  Point3f point;
  float percentage;
  
  public KeyFrame(Point3f point, float percentage)
  {
    this.point = point;
    this.percentage = percentage;
  }

  public static void printKeyFrames(List<KeyFrame> keyFrames)
  {
    for (KeyFrame kf : keyFrames)
    {
      System.out.println("keyframe : " + kf.percentage + ": " + kf.point);
    }
  }
}
