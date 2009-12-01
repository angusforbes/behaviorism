/* BreakCondition.java ~ Oct 15, 2009 */

package behaviorism.data;

import behaviorism.data.collectors.Collector;

/**
 *
 * @author angus
 */
abstract public class BreakCondition
{
  abstract public boolean stop(Collector collector);
}
