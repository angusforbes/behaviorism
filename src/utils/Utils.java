package utils;
/*
 * Utils.java
 * Created on April 23, 2007, 7:37 PM
 */

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;

/** 
 * This class contains static utility methods for a number of common but miscellaneous "chores",
 * such as timing, random numbers, and image processing.
 */
public class Utils
{

  public static NumberFormat decimalFormatter = new DecimalFormat("0.000");
  //public static NumberFormat decimalFormatter = new DecimalFormat();

  private Utils()
  {
  }

  /**
   * format a double or float into a readable String with three decimal places
   * @param num
   * @return readable String representation of double or float
   */
  public static String decimalFormat(double num)
  {
    return decimalFormatter.format(num);
  }

  /**
   * just a convenience method for when I need to add some milliseconds to
   * a timestamp that's in nanoseconds.
   * @param nano
   * @param millis
   * @return
   */
  public static long nanoPlusMillis(long nano, long millis)
  {
    return nano + millisToNanos(millis);
  }

  /**
   * A convenience method for when I need to add some milliseconds to
   * the current time (which is in nanoseconds).
   * @param millis
   * @return
   */
  public static long nowPlusMillis(long millis)
  {
    return System.nanoTime() + millisToNanos(millis);
  }

  /**
   * A convenience method for when I need to add a random number of milliseconds bewteen a specified range to
   * the current time (which is in nanoseconds).
   * @param minMillis
   * @param maxMillis
   * @return
   */
  public static long nowPlusMillis(long minMillis, long maxMillis)
  {
    return System.nanoTime() + millisToNanos(randomLong(minMillis, maxMillis));
  }

  public static long now()
  {
    return System.nanoTime();
  }

  public static long nowMillis()
  {
    return nanosToMillis(now());
  }

  /**
   * Normalizes a set of values with an aribitray total
   * so that their total is an specified value.
   *
   * @param totalRange
   * @param vals
   * @return the normalized array of values
   */
  public static double[] normalizeValues(double totalRange, double... vals)
  {
    double[] times = new double[vals.length];

    double totalVal = 0;
    for (double v : vals)
    {
      totalVal += v;
    }

    int idx = 0;
    for (double v : vals)
    {
      double percentage = v / totalVal;
      times[idx] = (totalRange * percentage);

      idx++;
    }
    return times;
  }

  /**
   * Normalizes a set of time values with an aribitray total
   * so that their total is a specified time.
   * For example, if the input parameter totalTime is 10000L and the input parameter vals are {15.0, 2.0, 3.0}
   * then the output would be the array {7500L, 1000L, 1500L}.
   * @param totalTime The time we are normalizing into.
   * @param vals A list of unnormalized doubles representing some amount of time in relation to the other vals.
   * @return the normalized array of times.
   */
  public static long[] normalizeTimes(long totalTime, double... vals)
  {
    long[] times = new long[vals.length];

    double totalVal = 0;
    for (double v : vals)
    {
      totalVal += v;
    }

    int idx = 0;
    for (double v : vals)
    {
      double percentage = v / totalVal;
      times[idx] = (long) (totalTime * percentage);
      idx++;
    }
    return times;
  }

  /**
   * a convenience method that wraps Thread.sleep(long milliseconds) without
   * requiring the try/catch block and which checks for invalid values.
   * @param ms
   */
  public static void sleep(long ms)
  {
    if (ms < 0L)
    {
      ms = 0L;
    }

    try
    {
      Thread.sleep(ms);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Sleeps for a random time within the specified range.
   * @param min
   * @param max
   */
  public static void sleep(long min, long max)
  {
    sleep(randomLong(min, max));
  }

  /**
   * sleeps until the specified nanosecond.
   * @param nano
   */
  public static void sleepUntilNano(long nano)
  {
    long sleepNano = nano - System.nanoTime();

    if (sleepNano > 0L)
    {
      sleep(nanosToMillis(sleepNano));
    }
  }

  /**
   * sleeps until the specified millisecond.
   * @param millisecond
   */
  public static void sleepUntilMillis(long millisecond)
  {
    long ms = millisecond - System.currentTimeMillis();

    System.out.println("in sleepUntilMillis() : ms = " + ms);
    if (ms > 0L)
    {
      System.out.println("sleeping for " + ms + " milliseconds...");
      sleep(ms);
      System.out.println("now i am awake...");
    }
  }

  /**
   * wrapper for Math.random.
   * @return
   */
  public static double random()
  {
    return (Math.random());
  }

  /**
   * creates a double between or equal to the specified values.
   * @param min
   * @param max
   * @return a double that is greater than or equal to min and less than or equal to max
   */
  public static double random(double min, double max)
  {
    return (random() * (max - min)) + min;
  }

  /**
   * creates a random Point3f between the specified x and y values, where the z value is always 0f.
   * @param minx
   * @param miny
   * @param maxx
   * @param maxy
   * @return the random Point3f
   */
  public static Point3f randomPoint3f(float minx, float miny, float maxx, float maxy)
  {
    return new Point3f(randomFloat(minx, maxx), randomFloat(miny, maxy), 0f);
  }

  public static Point3f randomPoint3f()
  {
    return new Point3f(randomFloat(-1f, 1f), randomFloat(-1f, 1f), 0f);
  }

  public static Point3f randomPoint3f(float rangeX, float rangeY)
  {
    return new Point3f(randomFloat(-rangeX, rangeX), randomFloat(-rangeY, rangeY), 0f);
  }

  public static Point3f randomPoint3f(float rangeX, float rangeY, float rangeZ)
  {
    return new Point3f(randomFloat(-rangeX, rangeX), randomFloat(-rangeY, rangeY), randomFloat(-rangeZ, rangeZ));
  }

  public static Point3f randomPoint3f(float range)
  {
    return new Point3f(randomFloat(-range, range), randomFloat(-range, range), 0f);
  }

  /**
   * creates a random Point3f between the specified x, y, and z values.
   * @param minx
   * @param miny
   * @param maxx
   * @param maxy
   * @return the random Point3f
   */
  public static Point3f randomPoint3f(
    float minx, float miny, float minz,
    float maxx, float maxy, float maxz)
  {
    return new Point3f(randomFloat(minx, maxx), randomFloat(miny, maxy), randomFloat(minz, maxz));
  }

  /**
   * creates a random Point3f within the cuboid, rectangle, or line defined by the diagonal
   * (or endpoints in the case of a line) specified by two points.
   * @param p1
   * @param p2
   * @return the random Point3f
   */
  public static Point3f randomPoint3f(Point3f p1, Point3f p2)
  {
    return new Point3f(randomFloat(p1.x, p2.x), randomFloat(p1.y, p2.y), randomFloat(p1.z, p2.z));
  }

  /**
   * creates a float between or equal to the specified values.
   * @param min
   * @param max
   * @return a float that is greater than or equal to min and less than or equal to max
   */
  public static float randomFloat(double min, double max)
  {
    return (float) ((randomFloat() * (max - min)) + min);
  }

  /**
   * creates a random float between 0f and 1f.
   * @return a random float between 0f and 1f.
   */
  public static float randomFloat()
  {
    return (float) (random());
  }

  /**
   * creates an int between or equal to the specified values.
   * @param min
   * @param max
   * @return an int that is greater than or equal to min and less than or equal to max
   */
  public static int randomInt(int min, int max)
  {
    //return (int) ( (random() * ((long)max - (long)min)) + (long)min );
    return ((int) (Math.round(random() * (double) (max - min)))) + min;
  }

  public static Integer[] randomArrayOfInts(int min, int max, int length)
  {
    List<Integer> ints = new ArrayList<Integer>();
    for (int i = 0; i < length; i++)
    {
      ints.add(randomInt(min, max));
    }
    return (Integer[]) ints.toArray(new Integer[ints.size()]);
  }

  public static Integer[] shuffledArrayOfInts(int length)
  {
    List<Integer> ints = new ArrayList<Integer>();
    for (int i = 0; i < length; i++)
    {
      ints.add(i);
    }
    Collections.shuffle(ints);
    // String[] sl = (String[]) list.toArray(new String[0]);
    //int[] yo = ints.toArray(new Integer[0]);
    Integer[] array = (Integer[]) ints.toArray(new Integer[ints.size()]);
    return array;
  }

  /**
   * Creates a List of numbers indicating a position with in a specified range, where the distance between
   * consecutive numbers is always greater than the specified minSize and less than the specified
   * maxSize. If it is not possible to fulfill the constraints specified by the minSize and maxSize, then
   * this method will throw an error. The numElements refers to the number of numbers, not the number of spaces,
   * so, for example to specify 4 spaces, numElements should be 5. The first number in the List will always be
   * rangeStart, and the last number will always be rangeEnd.
   * @param rangeStart The first number in the List.
   * @param rangeEnd The last number in the List.
   * @param numElements The number of numbers to be returned in the List.
   * @param minSize The minimum distance of one number to the previous or subsequent number.
   * @param maxSize The maximum distance of one number to the previous or subsequent number.
   * @return A consecutive List of numbers starting with rangeStart and ending with rangeEnd
   * where each consecutive number is no more than maxSize and no less than minSize
   * from the previous and subsequent numbers in the List.
   */
  public static List<Float> randomBoundedPositions(float rangeStart, float rangeEnd,
    int numElements, float minSize, float maxSize)
  {
    float range = rangeEnd - rangeStart;

    if ((numElements - 1) * minSize > range)
    {
      System.err.println("in randomBoundedPositions() : error impossible to configure, min too big!");
      return null; //throw an error!
    }
    else if ((numElements - 1) * maxSize < range)
    {
      System.err.println("in randomBoundedPositions() : error impossible to configure, max too small!");
      return null; //throw an error!
    }

    List<Float> positions = new ArrayList<Float>();

    //create the middle spaces
    float spaceLeft = range;
    for (int i = 1; i < numElements - 1; i++)
    {
      float legalMin = Math.max(minSize, spaceLeft - ((numElements - (i + 1)) * maxSize));
      float legalMax = Math.min(maxSize, spaceLeft - ((numElements - (i + 1)) * minSize));
      float tryVal = randomFloat(legalMin, legalMax);
      positions.add(tryVal);
      spaceLeft -= tryVal;
    }

    //add in the last space
    positions.add(spaceLeft);

    //shuffle the spaces
    Collections.shuffle(positions);

    //arrange the spaces consecutively within the range
    float prevVal = rangeStart;

    for (int i = 0; i < positions.size(); i++)
    {
      float val = positions.get(i) + prevVal;
      positions.set(i, val);
      prevVal = val;
    }

    //now add in the first point at rangeStart
    positions.add(0, rangeStart);

    return positions;
  }

  /**
   * creates a long between or equal to the specified values.
   * @param min
   * @param max
   * @return a long that is greater than or equal to min and less than or equal to max
   */
  public static long randomLong(long min, long max)
  {
    //return (long) ( (Math.random() * (max - min)) + min );
    return ((long) (Math.round(random() * (double) (max - min)))) + min;
  }

  /* usage: arg1 = num elements in array, arg2-->numElements*2+1 = min and max random nums.
   * Eg, randomCell(2, -1f, 1f, 10f, 20f) returns a float array of size 2
   * where the first element is between -1 and 1, and the second element is between 10 and 20
   */
  public static float[] randomCell(int numElements, float... info)
  {
    if (info.length != numElements * 2)
    {
      System.err.println("wrong usage of Utils.randomCell()!");
      return null;
    }

    float cell[] = new float[numElements];

    for (int i = 0; i < numElements; i++)
    {
      cell[i] = Utils.randomFloat(info[i * 2], info[(i * 2) + 1]);
    }

    return cell;
  }

  /**
   * creates a random String of letters and punctuation with a length that is between a specified minimium and maximum number of characters.
   * @param min
   * @param max
   * @return a random String of letters and punctuation
   */
  public static String randomString(int min, int max)
  {
    return randomString(randomInt(min, max));
  }

  /**
   * creates a random String of letters and punctuation of a specified length
   * @param len
   * @return a random String of letters and punctuation
   */
  public static String randomString(int len)
  {
    String str = "";
    for (int i = 0; i < len; i++)
    {
      //str += "" + ((char)randomInt(60,110) );
      str += "" + ((char) randomInt(97, 122));
    }

    return str;
  }

  public static String randomVowels(int min, int max)
  {
    return randomVowels(randomInt(min, max));
  }

  public static String randomVowels(int len)
  {
    char[] vowels = new char[]
    {
      'a', 'e', 'i', 'o', 'u', 'y'
    };
    String str = "";
    for (int i = 0; i < len; i++)
    {
      //str += "" + ((char)randomInt(60,110) );
      str += "" + vowels[randomInt(0, vowels.length - 1)];
    }

    return str;
  }

  public static List<String> randomStrings(int numStrings, int min, int max)
  {
    List<String> strings = new ArrayList<String>();

    for (int i = 0; i < numStrings; i++)
    {
      strings.add(randomString(min, max));
    }

    return strings;
  }

  public static List<Float> randomAngles(int numAngles, boolean evenlySpaced)
  {
    return randomAngles(numAngles, 0f, 360f, evenlySpaced);
  }

  public static List<Float> randomAngles(int numAngles, float min, float max, boolean evenlySpaced)
  {
    List<Float> angles = new ArrayList<Float>();

    if (min > max)
    {
      max += 360f;
    }

    if (!evenlySpaced) //just get some angles!
    {
      for (int i = 0; i < numAngles; i++)
      {
        angles.add(randomFloat(min, max) % 360f);
      }
      return angles;
    }

    float startAngle = randomFloat(min, max);
    float range = max - min;
    float spacing = range / numAngles;

    //System.out.println("spacing = " + spacing);
    for (int i = 0; i < numAngles; i++)
    {
      float nextangle = min + (startAngle + (i * spacing)) % range;
      //System.out.println("next angle (" + i + ") = " + nextangle);
      angles.add(nextangle);
    }

    return angles;
  }

  public static long millisToNanos(double val)
  {
    return MILLISECONDS.toNanos((long) val);
  }

  public static long nanosToMillis(double val)
  {
    return NANOSECONDS.toMillis((long) val);
  }

  public static double nanosToSeconds(double val)
  {
    return val / 1000000000.0;
  }

  public static long secondsToNanos(double val)
  {
    return (long) (val * 1000000000.0);
  }

  /**
   * Adds some number of elements to a list.
   *
   * Ex. addTo(myList, element1, element2); //add as many as you want!
   * @param list
   * @param objs
   */
  //public static <T> void addTo(List<T> list, T ... objs)
  //public static <T> void addTo(Collection<T> list, T ... objs)
  public static <T> void addTo(Collection<T> list, T... objs)
  {
    for (T t : objs)
    {
      list.add(t);
    }
  }

  /**
   * Removes some number of elements from a list.
   *
   * Ex. removeFrom(myList, element1, element2); //remove as many as you want!
   * @param list
   * @param objs
   */
  public static void removeFrom(List list, Object... objs)
  {
    for (Object o : objs)
    {
      list.remove(o);
    }
  }

  public static void removeAllFrom(List list, Object... objs)
  {
    for (Object o : objs)
    {
      list.removeAll((List) o);
    }
  }

  /**
   * Same as addTo, except assumes all elements are in fact Lists.
   * @param list
   * @param objs
   */
  public static <T> void addAllTo(List<T> list, List<? extends T>... listsToAdd)
  {
    for (List<? extends T> listToAdd : listsToAdd)
    {
      list.addAll((List<? extends T>) listToAdd);
    }
  }

  /**
   * Returns a set containing elements that are either in set1 or in set2, but not both. 
   * @param <T>
   * @param set1
   * @param set2
   * @return
   */
  public static <T> Set xorSets(Set<T> set1, Set<T> set2)
  {
    Set<T> xorSet = new HashSet<T>(set1);
    xorSet.addAll(set2);
    Set<T> tmp = new HashSet<T>(set1);
    tmp.retainAll(set2);
    xorSet.removeAll(tmp);
    return xorSet;
  }

  /**
   * Returns a set containing elements in set1 that are *also* in set2.
   * @param <T>
   * @param set1
   * @param set2
   * @return
   */
  public static <T> Set intersectSets(Set<T> set1, Set<T> set2)
  {
    Set<T> intersectionSet = new HashSet<T>(set1);
    intersectionSet.retainAll(set2);
    return intersectionSet;
  }

  /**
   * Returns a set containing all elements in set1 that are *not* also in set2.
   * @param <T>
   * @param set1
   * @param set2
   * @return
   */
  public static <T> Set complementSets(Set<T> set1, Set<T> set2)
  {
    Set<T> complementSet = new HashSet<T>(set1);
    complementSet.removeAll(set2);
    return complementSet;
  }

  public static BufferedImage toRGBBufferedImage(Image img)
  {
    if (img == null)
    {
      return null;
    }
    // Create an RGB buffered image
    BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
    //BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);


    // Copy non-RGB image to the RGB buffered image
    Graphics2D g = bimg.createGraphics();
    g.drawImage(img, 0, 0, null);
    g.dispose();

    return bimg;
  }

  public static BufferedImage toBufferedImage(Image image)
  {
    if (image instanceof BufferedImage)
    {
      return (BufferedImage) image;
    }

    // This code ensures that all the pixels in the image are loaded
    //image = new ImageIcon(image).getImage();
    image = new GarbageCollectedImageIcon(image).getImage();

    if (image.getWidth(null) <= 0 || image.getHeight(null) <= 0)
    {
      System.out.println("error! this image has no size!");
      return null;
    }
    // Determine if the image has transparent pixels; for this method's
    // implementation, see e661 Determining If an Image Has Transparent Pixels
    //boolean hasAlpha = hasAlpha(image);
    boolean hasAlpha = false; //hasAlpha(image);

    // Create a buffered image with a format that's compatible with the screen
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try
    {
      // Determine the type of transparency of the new buffered image
      int transparency = Transparency.OPAQUE;
      if (hasAlpha)
      {
        transparency = Transparency.BITMASK;
      }

      // Create the buffered image
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(
        image.getWidth(null), image.getHeight(null), transparency);
    }
    catch (HeadlessException e)
    {
      // The system does not have a screen
    }

    if (bimage == null)
    {
      // Create a buffered image using the default color model
      int type = BufferedImage.TYPE_INT_RGB;
      if (hasAlpha)
      {
        type = BufferedImage.TYPE_INT_ARGB;
      }
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    }

    // Copy image to buffered image
    Graphics g = bimage.createGraphics();

    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();

    return bimage;
  }

  public static ColorModel getColorModel(Image image)
  {
    // If buffered image, the color model is readily available
    if (image instanceof BufferedImage)
    {
      BufferedImage bimage = (BufferedImage) image;
      return bimage.getColorModel();
    }

    // Use a pixel grabber to retrieve the image's color model;
    // grabbing a single pixel is usually sufficient
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try
    {
      pg.grabPixels();
    }
    catch (InterruptedException e)
    {
    }
    ColorModel cm = pg.getColorModel();
    return cm;
  }

  public static void sortTemplate(List<Integer> list)
  {
    Collections.sort(list, new Comparator()
    {

      public int compare(Object a, Object b)
      {
        int int1 = (Integer) a;
        int int2 = (Integer) b;
        if (int1 > int2)
        {
          return -1;
        }
        else if (int1 < int2)
        {
          return 1;
        }
        else
        {
          return 0;
        }
      }
    });
  }

  public static String tabs(int num)
  {
    String s = "";
    for (int i = 0; i < num; i++)
    {
      s += "\t";
    }
    return s;
  }

  public static void print(Collection c)
  {
    Iterator i = c.iterator();

    System.out.println("\tcollection has size " + c.size());
    while (i.hasNext())
    {
      System.out.println("<" + i.next() + "> ");
    }

    System.out.println("");
  }

  public static void printMemory()
  {
    int mb = 1024 * 1024;

    //Getting the runtime reference from system
    Runtime runtime = Runtime.getRuntime();

    System.out.println("##### Heap utilization statistics [MB] #####");

    //Print used memory
    System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);

    //Print free memory
    System.out.println("Free Memory:" + runtime.freeMemory() / mb);

    //Print total available memory
    System.out.println("Total Memory:" + runtime.totalMemory() / mb);

    //Print Maximum available memory
    System.out.println("Max Memory:" + runtime.maxMemory() / mb);
  }
}

class GarbageCollectedImageIcon extends ImageIcon
{

  protected final MediaTracker tracker = null;
  protected final Component component = null;

  public GarbageCollectedImageIcon(URL url)
  {
    super(url);
  }

  public GarbageCollectedImageIcon(Image img)
  {
    super(img);
  }
}
