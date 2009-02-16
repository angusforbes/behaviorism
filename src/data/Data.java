/* Data.java (created on December 12, 2007, 5:39 PM) */
package data;

import geometry.BorderEnum;
import geometry.Geom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import utils.Utils;

/** This class provides a general data structure whereby a particular piece
of data can have pointers to other types of data. The types of data available are 
specified by a DataEnum identifier and the actual data is stored in a List whose type 
is a subclass of Data. For instance,
the subclass CandidateData might need pointers to a List of VideoData and
IssueData. We can add a placeholder for, say, a list of VideoData by calling: 
<p>addList(DataEnum.VIDEO, new ArrayList<VideoData>()); <p>
We can then add a particular video to that list like so: <p>
addDataToList(DataEnum.VIDEO, someVideoDataObject);
<p>
and remove it like so:
<p>
removeDataFromList(DataEnum.VIDEO, someVideoDataObject);
<p>
 */
public class Data
{
  public DataEnum dataType = null;
  public float weight = 1.0f;
  //should this map actually map a key to a SET (instead of a list)???
  Map<DataEnum, List<Data>> dataMap = new HashMap<DataEnum, List<Data>>();

  public List<Data> getRelated(Data d)
  {
    return new ArrayList<Data>();
  }

  /** add an entire List to the dataMap,
  eg, addList(listOfVideoData);*/
  public void addList(List<? extends Data> list)
  {
    for (Data d : list)
    {
      addElement(d);
    }
  }

  /** creates a new empty list whose type is appropriate to the DataEnum */
  public List<Data> createList(DataEnum key)
  {
    List<Data> list = new ArrayList<Data>();
    dataMap.put(key, list);
    return list;
  }

  /** remove the entire List from the dataMap */
  public void removeList(DataEnum key)
  {
    dataMap.remove(key);
  }

  public void removeList(List<? extends Data> list)
  {
    for (Data d : list)
    {
      removeElement(d);
    }
  }

  public List<Data> getList(DataEnum key)
  {
    return dataMap.get(key);
  }

  /** add an element to the appropriate List, creating it if necessary */
  public void addElement(Data element)
  {
    List<Data> list = getList(element.dataType);

    if (list == null)
    {
      list = createList(element.dataType);
    }

    if (!list.contains(element)) //can't add the same data twice

    {
      list.add(element);
    }
  }

  /** remove a specific element from the list */
  public void removeElement(Data element)
  {
    List<Data> list = getList(element.dataType);

    if (list != null)
    {
      list.remove(element);
    }
  }

  public Data getRandomElementRelatedTo(Data... relatedData)
  {
    /*
    List<Data> list = getList(key);
    
    if (list != null)
    {	
    List<Data> tmpList = new ArrayList<Data>(list);
    tmpList.removeAll(Arrays.asList(excludedData));
    return getRandomElement(tmpList);
    }
     */
    return null;
  }

  public Data getRandomElementRelatedTo(DataEnum key, Data... excludedData)
  {
    List<Data> list = getList(key);

    if (list != null)
    {
      List<Data> tmpList = new ArrayList<Data>(list);
      tmpList.removeAll(Arrays.asList(excludedData));
      return getRandomElement(tmpList);
    }

    return null;
  }

  public Data getRandomElementExcluding(DataEnum key, Data... excludedData)
  {
    List<Data> list = getList(key);

    if (list != null)
    {
      List<Data> tmpList = new ArrayList<Data>(list);
      tmpList.removeAll(Arrays.asList(excludedData));
      return getRandomElement(tmpList);
    }

    return null;
  }

  /** Get a random element from ANY list in the dataMap */
  public Data getRandomElement()
  {
    List listOfKeys = new ArrayList(dataMap.keySet());

    DataEnum key = (DataEnum) listOfKeys.get(Utils.randomInt(0, listOfKeys.size()));

    return getRandomElement(key);
  }

  /** Get a random element from a list specified with a particular key */
  public Data getRandomElement(DataEnum key)
  {
    List<Data> list = getList(key);

    if (list != null)
    {
      return getRandomElement(list);
    }

    return null;
  }

  /** Get a random element from a specified list */
  public static Data getRandomElement(List<Data> list)
  {
    if (list.size() == 0)
    {
      return null;
    }

    return list.get(Utils.randomInt(0, list.size() - 1));
  }

  /** attach some random elements from the source list to elements in the target list */
  /** 
  Loop through each element of the target list, and attach some number of elements to it 
  which are chosen at random from the source list
   */
  public static void addRandomData(List<? extends Data> target, List<? extends Data> source, int min, int max)
  {
    for (Data d : target)
    {
      Collections.shuffle(source);

      for (int i = 0; i < Utils.randomInt(min, max); i++)
      {
        d.addElement(source.get(i));
      }
    }
  }

  /** This method returns all the data from every list into a single List */
  public List<Data> getAllData()
  {
    List<Data> combinedDataList = new ArrayList<Data>();

    Iterator it = dataMap.keySet().iterator();
    while (it.hasNext())
    {
      Object key = it.next();
      List<Data> ds = getList((DataEnum) key);

      combinedDataList.addAll(ds);
    }

    return combinedDataList;
  }

  /** 
  Loop through each element of the target list, and link it up with some number of elements chosen at random
  from the source list
   */
  public static void intertwineRandomData(List<? extends Data> target, List<? extends Data> source, int min, int max)
  {
    for (Data d : target)
    {
      Collections.shuffle(source);
      int rand = Utils.randomInt(min, max);
      System.out.println("rand = " + rand);
      for (int i = 0; i < rand; i++)
      {
        intertwineData(d, source.get(i));
        System.out.println("rand = " + rand + " d = " + d + " src = " + source.get(i));
      }
    }
  }

  public static void intertwineData(Data d1, Data d2)
  {
    d1.addElement(d2);
    d2.addElement(d1);
  }

  public void printData(DataEnum key, int maxLevel)
  {
    int level = 0;

    List<Data> ds = getList(key);

    if (ds == null)
    {
      return;
    }

    for (Data d : ds)
    {
      System.out.print(Utils.tabs(level) + d.dataType.name().toLowerCase() + ": ");
      System.out.println(d);

      d.printData(level + 1, maxLevel);
    }
  }

  public void printData(int maxLevel)
  {
    printData(0, maxLevel);
  }

  public void printData(int level, int maxLevel)
  {
    if (level > maxLevel)
    {
      return; //stop it from repeating... 

    }

    Iterator it = dataMap.keySet().iterator();

    while (it.hasNext())
    {
      Object key = it.next();

      List<Data> ds = getList((DataEnum) key);

      for (Data d : ds)
      {
        System.out.print(Utils.tabs(level) + d.dataType.name().toLowerCase() + ": ");
        System.out.println(d);

        if (level < maxLevel)
        {
          d.printData(level + 1, maxLevel);
        }

      }
    }

  }

  //the makeShapes should probably be abstract
  public Geom makeShape() //to be overwritten by Data classes that can draw themselves

  {
    System.out.println("ERROR: in parent makeShape!");
    return null;
  }

  public Geom makeShape(BorderEnum borderType) //to be overwritten by Data classes that can draw themselves

  {
    return makeShape(); //temp
  //return null;

  }
}
