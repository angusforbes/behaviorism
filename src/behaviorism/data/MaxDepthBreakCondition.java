/* MaxDepthBreakCondition.java ~ Oct 15, 2009 */

package behaviorism.data;

import behaviorism.data.collectors.Collector;

/**
 *
 * @author angus
 */
public class MaxDepthBreakCondition 
{
  int maxDepth;

  public MaxDepthBreakCondition(int maxDepth)
  {
    this.maxDepth = maxDepth;
  }

  public boolean stop(Collector collector)
  {
    if (maxDepth >= collector.maxDepth)
    {
      return true;
    }
    return false;
  }
}
