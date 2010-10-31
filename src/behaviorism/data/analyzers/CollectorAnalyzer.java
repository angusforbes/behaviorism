/* CollectorAnalyzer.java ~ Oct 20, 2009 */

package behaviorism.data.analyzers;

import behaviorism.data.collectors.Collector;

/**
 * Base class to analyze data collected by a specified Collector.
 * @author angus
 */
abstract public class CollectorAnalyzer
{
  public Collector collector = null;
  
  public CollectorAnalyzer()
  {
  }


  public CollectorAnalyzer(Collector collector)
  {
    setCollector(collector);
  }

  public void setCollector(Collector collector)
  {
    this.collector = collector;
  }

  public Collector getCollector()
  {
    return this.collector;
  }

  public void analyze(Collector collector)
  {
    setCollector(collector);
    analyze();
  }
  //Also attaches it to the Collector

  abstract public void analyze();
  
}
