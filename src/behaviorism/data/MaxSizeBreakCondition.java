/* MaxSizeBreakCondition.java ~ Oct 15, 2009 */

package behaviorism.data;

import behaviorism.data.collectors.Collector;

/**
 *
 * @author angus
 */
public class MaxSizeBreakCondition
{
  int maxSize;

  public MaxSizeBreakCondition(int maxSize)
  {
    this.maxSize = maxSize;
  }

  public boolean stop(Collector collector)
  {
    if (maxSize >= collector.getData().size())
    {
      return true;
    }
    return false;
  }
}
