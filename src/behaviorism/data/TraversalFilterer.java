/* CategoryFilterer.java ~ Oct 14, 2009 */

package behaviorism.data;

import behaviorism.data.filterers.RelationshipFilterer;

/**
 *
 * @author angus
 */
public class TraversalFilterer
{
  public NodeFilterer nodeFilter = null;
  public RelationshipFilterer relationshipFilter = null;

  public TraversalFilterer(NodeFilterer nodeFilter)
  {
    this.nodeFilter = nodeFilter;
  }

  public TraversalFilterer(RelationshipFilterer relationshipFilter)
  {
    this.relationshipFilter = relationshipFilter;
  }

  public TraversalFilterer(NodeFilterer nodeFilter, RelationshipFilterer relationshipFilter)
  {
    this.nodeFilter = nodeFilter;
    this.relationshipFilter = relationshipFilter;
  }

  //we want to know if we can traverse from the parentNode to the childNode
  //1. check to see if childNode passes the nodeFilter (if there is no nodeFilter, then it passes automatically).
  //2. check to see if the relationships from the parentNode to the childNode passes
  //  the relationshipFilter (if there is no relationshipFilter, then it passes automatically).
  public boolean filter(Node parentNode, Node childNode)
  {
    if ((nodeFilter == null || nodeFilter.filter(childNode) == true) &&
      (relationshipFilter == null || relationshipFilter.filter(parentNode.relationshipMap.get(childNode)) == true))
    {
      return true;
    }
    return false;
  }
}
