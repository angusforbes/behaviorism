/* NodeAnalyzer.java ~ Oct 19, 2009 */

package behaviorism.data.analyzers;

import behaviorism.data.*;
import java.util.Set;

/**
 *
 * @author angus
 */
abstract public class RelationshipAnalyzer extends Node
{
  public RelationshipAnalyzer()
  {
  }

  abstract public void analyze(Relationship relationship);

  
}
