package behaviorism.data.ShortestPath;

import java.util.List;

/**
 * This interface defines the object storing the graph of all routes in the
 * system.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: RoutesMap.java 2367 2007-08-20 21:47:25Z renaud $
 */

public interface RoutesMap
{
	/**
	 * Enter a new segment in the graph.
	 */
	public void addDirectRoute(City start, City end, int distance);
	
	/**
	 * Get the value of a segment.
	 */
	public int getDistance(City start, City end);
	
	/**
	 * Get the list of cities that can be reached from the given city.
	 */
	public List<City> getDestinations(City city); 
	
	/**
	 * Get the list of cities that lead to the given city.
	 */
	public List<City> getPredecessors(City city);
	
	/**
	 * @return the transposed graph of this graph, as a new RoutesMap instance.
	 */
	public RoutesMap getInverse();
}
