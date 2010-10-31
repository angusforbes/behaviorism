package behaviorism.data.ShortestPath;

/**
 * A city is identified by its name, a single uppercase character.
 * Conversions to/from characters are handled by 
 * {@link #getName()} and {@link #valueOf(char)}, respectively.
 * <p>
 * Package members are also given access to an identity relationship between
 * cities and numbers: they can converts between <code>City</code>
 * instances and numbers using {@link #valueOf(int)} and {@link #getIndex()}.
 * This special relationship is used by the
 * {@link com.waldura.tw.DenseRoutesMap DensesRoutesMap} 
 * to store cities in an array.
 * 
 * @author Renaud Waldura &lt;renaud+tw@waldura.com&gt;
 * @version $Id: City.java 2367 2007-08-20 21:47:25Z renaud $
 */

public final class City implements Comparable<City>
{
	/**
	 * The largest possible number of cities.
	 */
	public static final int MAX_NUMBER = 26;
	
	private static final City[] cities = new City[MAX_NUMBER];

	static
	{
        // initialize all City objects
		for (char c = 'A'; c <= 'Z'; c++)
		{
			cities[getIndexForName(c)] = new City(c);
		}		
	}
		
	private static int getIndexForName(char name)
	{
		return name - 'A';
	}	
	
	private static char getNameForIndex(int index)
	{
		return (char)('A' + index);
	}	
	
	public static final City A = City.valueOf('A');
	public static final City B = City.valueOf('B');
	public static final City C = City.valueOf('C');
	public static final City D = City.valueOf('D');
	public static final City E = City.valueOf('E');
    public static final City F = City.valueOf('F');
	
	public static City valueOf(char name)
	{
		if (name < 'A' || name > 'Z')
		{
			throw new IllegalArgumentException("Invalid city name: " + name);	
		}
		
		return cities[getIndexForName(name)];
	}
	
    /*
     * Package members only.
     */
	static City valueOf(int n)
	{
		if (n < 0 || n > 25)
		{
			throw new IllegalArgumentException("Invalid city number: " + n);
		}
		
		return valueOf( getNameForIndex(n) );		
	}

	private final char name;
	
    /**
     * Private constructor.
     * @param name
     */
	private City(char name)
	{
		this.name = name;	
	}

	public char getName()
	{
		return name;	
	}
	
    /*
     * Package members only.
     */
	int getIndex()
	{
		return getIndexForName(name);
	}	
	
    /**
     * @see java.lang.Object#toString()
     */
	public String toString()
	{
		return String.valueOf(name);
	}

    /**
     * Two cities are considered equal if they are the same object,
     * or their names are the same.
     * 
     * @see java.lang.Object#equals(Object)
     */    
    public boolean equals(Object o)
    {
        return this == o || equals((City) o);
    }
    
    private boolean equals(City c)
    {
        return this.name == c.name;
    }

    /**
     * Compare two cities by name.
     * 
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(City c)
    {
        return this.name - c.name;
    }
}
