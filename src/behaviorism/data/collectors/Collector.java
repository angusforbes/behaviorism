/* Collector.java ~ Oct 15, 2009 */
package behaviorism.data.collectors;

import behaviorism.data.BreakCondition;
import behaviorism.data.Node;
import behaviorism.data.NodeFilterer;
import behaviorism.data.TraversalFilterer;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author angus
 */
public class Collector extends Node
{
  public Node startNode = null;
  public int maxDepth;
  public int maxSize;
  TraversalFilterer traversalFilter = null;
  NodeFilterer collectionFilter = null;
  BreakCondition condition;
  Set<Node> visitedNodes = new HashSet<Node>();
  Set<Node> collectedNodes = new HashSet<Node>();

  public Collector(
    Node startNode,
    int maxDepth,
    int maxSize
    )
  {
    this.startNode = startNode;
    this.traversalFilter = null;
    this.collectionFilter = null;
    this.maxDepth = maxDepth;
    this.maxSize = maxSize;
  }

  public Collector(
    Node startNode,
    TraversalFilterer traversalFilter,
    int maxDepth,
    int maxSize
    )
  {
    this.startNode = startNode;
    this.traversalFilter = traversalFilter;
    this.collectionFilter = null;
    this.maxDepth = maxDepth;
    this.maxSize = maxSize;
  }

  public Collector(
    Node startNode,
    NodeFilterer collectionFilter,
    int maxDepth,
    int maxSize
    )
  {
    this.startNode = startNode;
    this.traversalFilter = null;
    this.collectionFilter = collectionFilter;
    this.maxDepth = maxDepth;
    this.maxSize = maxSize;
  }
  public Collector(
    Node startNode,
    TraversalFilterer traversalFilter,
    NodeFilterer collectionFilter,
    int maxDepth,
    int maxSize
    )
  {
    this.startNode = startNode;
    this.traversalFilter = traversalFilter;
    this.collectionFilter = collectionFilter;
    //this.breakCondition = condition;
    this.maxDepth = maxDepth;
    this.maxSize = maxSize;
  }

  private void traverseDepthFirst(Node currentNode)
  {
    traverseDepthFirst(currentNode, 0);
  }

  /**
   * Traverses specified node. If this node passes the collection filter it will be added to the
   * collectionNodes. If it passes the traversalFilter
   * @param currentNode
   * @param currentDepth
   */
  private void traverseDepthFirst(Node currentNode, int currentDepth)
  {
    //depth first

    System.err.println("checking out node " + currentNode.name + " at depth = " + currentDepth);
    if (collectedNodes.size() >= maxSize)
    {
      System.err.println("in traverseDepthFirst() : collectedNodes.size() = " + collectedNodes.size() + " > " + maxSize);
      return;
    }

    if (visitedNodes.add(currentNode) == false) //then we have alreaady visited this node
    {
      System.err.println("\toops, already visited...");
      return;
    }

    if (collectionFilter == null || collectionFilter.filter(currentNode) == true)
    {
      collectedNodes.add(currentNode);
    }

    if (currentDepth == maxDepth)
    {
      System.err.println("in traverseDepthFirst() : currentDepth == maxDepth == " + maxDepth);
      System.err.println("in traverseDepthFirst() : collectedNodes.size() = " + collectedNodes.size());
      return;
    }

    for (Node childNode : currentNode.getData())
    {
      if (traversalFilter == null || traversalFilter.filter(currentNode, childNode) == true)
      {
        traverseDepthFirst(childNode, currentDepth + 1);
      }
    }
  }

  private void traverseBreadthFirst(Node rootNode)
  {
    Set<Node> nodes = new HashSet<Node>();
    nodes.add(rootNode);
    traverseBreadthFirst(nodes, 0);
  }

  private void traverseBreadthFirst(Set<Node> nodes, int currentDepth)
  {
    Set<Node> childNodes = new HashSet<Node>();

    for (Node currentNode : nodes)
    {
      System.err.println("checking out node " + currentNode.name + " at depth = " + currentDepth);

      if (visitedNodes.add(currentNode) == false) //then we have already visited this node
      {
        System.err.println("in traverseBreadthFirst() : " + currentNode.name + " already visited!");
        return;
      }

      if (collectionFilter == null || collectionFilter.filter(currentNode) == true)
      {
        System.err.println("in traverseBreadthFirst() : \t successfully collected " + currentNode.name + "!");

        collectedNodes.add(currentNode);
      }

      if (collectedNodes.size() >= maxSize)
      {
        System.err.println("in traverseBreadthFirst() : collectedNodes.size() = " + collectedNodes.size() + " > " + maxSize);
        break;
      }

      if (currentDepth == maxDepth)
      {
      System.err.println("in traverseBreadthFirst() : currentDepth == maxDepth == " + maxDepth);
      System.err.println("in traverseBreadthFirst() : collectedNodes.size() = " + collectedNodes.size());
        continue;
      }

      for (Node childNode : currentNode.getData())
      {
        if (traversalFilter == null || traversalFilter.filter(currentNode, childNode) == true)
        {
          System.err.println("in traverseBreadthFirst() : \t we will try to investigate " + childNode.name);
          if (!visitedNodes.contains(childNode) && !nodes.contains(childNode) )
          {
            childNodes.add(childNode);
          }
          else
          {
            System.err.println("in traverseBreadthFirst() : \t\t oops, "+ childNode.name+" has already been visited " +
              "(or is about to be visited).");
          }
        }
      }
    }

    if (currentDepth != maxDepth && collectedNodes.size() < maxSize)
    {
      System.err.println("in traverseBreadthFirst() : currentDepth = " + currentDepth + ", collectedNodes.size() = " + collectedNodes.size());
      traverseBreadthFirst(childNodes, currentDepth + 1);
    }
  }

  public void setStartNode(Node startNode)
  {
    this.startNode = startNode;
  }

  public Node getStartNode()
  {
    return startNode;
  }

  public Set<Node> collect(Node startNode)
  {
    setStartNode(startNode);
    return collect();
  }

  public Set<Node> collect()
  {
    this.visitedNodes.clear();
    this.collectedNodes.clear();


    traverseBreadthFirst(startNode);
    //traverseDepthFirst(startNode);

    //handle adding all the collected nodes to the graph...


    //also let's return a new Set that copies this set in case it's needed directly
    return new HashSet<Node>(collectedNodes);
  }
}
