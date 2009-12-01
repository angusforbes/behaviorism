/* CollectorAnalysis.java ~ Oct 20, 2009 */

package behaviorism.data.analyzers;

import behaviorism.data.*;
import java.util.Set;

/**
 * Base class to analyze one or a bunch of Data Sets directly, without using a collector.
 * @author angus
 */
abstract public class SetAnalyzer
{
  Set<Node> set = null;

  public SetAnalyzer()
  {
  }

  public SetAnalyzer(Set<Node> set)
  {
    setSet(set);
  }

  public void setSet(Set<Node> set)
  {
    this.set = set;
  }

  public Set<Node> getSet()
  {
    return set;
  }
  
  public void analyze(Set<Node> set)
  {
    setSet(set);
    analyze();
  }
  //Also attaches it to the Collector

  abstract public void analyze();


}
