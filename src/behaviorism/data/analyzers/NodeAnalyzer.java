/* NodeAnalyzer.java ~ Oct 19, 2009 */

package behaviorism.data.analyzers;

import behaviorism.data.*;
import java.util.Set;

/**
 *
 * @author angus
 */
abstract public class NodeAnalyzer extends Node
{
  public NodeAnalyzer()
  {
  }

  abstract public void analyze(Node data);

  
}
