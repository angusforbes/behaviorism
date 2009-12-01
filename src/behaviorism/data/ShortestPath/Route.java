package behaviorism.data.ShortestPath;

import java.util.ArrayList;

/**
 * This class models a route. A route has the following properties:
 * <ul>
 * <li> a list of cities, maybe empty
 * <li> a length, its number of stops
 * <li> a distance, the total distance of all its segments
 * </ul>
 * Route instances are created by the {@link RouteBuilder}.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: Route.java 2368 2007-08-20 22:08:03Z renaud $
 */

public final class Route
	implements Cloneable
{
	// we need its concrete type to successfully clone this field
	private ArrayList<City> cities = new ArrayList<City>();
	
	private int distance = 0;
	
	/**
	 * Instances of this class are created by the {@link RouteBuilder}.
	 */	
	Route()
	{
	}
	
	public Route clone()
	{
		Route newInstance = null;
		
		try
		{
			 newInstance = (Route) super.clone();	
		}
		catch (CloneNotSupportedException cnfe)
		{
			// we are Cloneable: this should never happen
			assert false : cnfe;
		}
		
		newInstance.cities = (ArrayList<City>) cities.clone();
		
		return newInstance;
	}
	
	/**
	 * Add a new stop to this route with the given distance.
	 * If this is the first stop (i.e. the starting point), the
	 * <code>distance</code> argument is meaningless.
	 * 
	 * @param stop the next city on this route.
	 * @param distance the distance between the previous city and this one.
	 */
	void addStop(City stop, int distance)
	{
		if (!cities.isEmpty())
		{
			this.distance += distance;		
		}
		
		cities.add(stop);
	}
	
	/**
	 * @return the total distance of this route.
	 */
	public int getDistance()
	{
		return distance;	
	}
	
	/**
	 * @return the number of stops on this route. The starting city is not
	 * considered a stop and thus is not counted.
	 */
	public int getLength()
	{
		return (cities.isEmpty()) ? 0 : cities.size() - 1;
	}
	
	/**
	 * @return the last stop on this route. The last stop may be the
	 * starting point if there are no other stops, or NULL is this route
	 * has no stops.
	 */
	public City getLastStop()
	{
		if (cities.isEmpty())
		{
			return null;
		}
		else
		{
			return cities.get(cities.size() - 1);
		}
	}
	
	/**
	 * @return whether this route goes through the given city.
	 */
	public boolean hasCity(City city)
	{
		return cities.contains(city);
	}
	
	public String toString()
	{
		StringBuffer temp = new StringBuffer();
		
		temp.append("l=").append( getLength() )
			.append(" d=").append( getDistance() )
			.append(" ").append(cities);
			
		return temp.toString();
	}
}
