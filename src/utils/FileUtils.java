/* FileUtils.java ~ Aug 29, 2008 */
package utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author angus
 */
public class FileUtils
{

  public static String toCrossPlatformFilename(String filename)
  {
    return filename.replaceAll("//", File.separator);
  }

  public static List<String> getFilenamesFromDirectory(String dirName)
  {
    File dir = new File(dirName); //e.g., "/data/celltango" 

    String children[] = dir.list();

    if (children != null)
    {
      return Arrays.asList(children);
    }
    return Collections.emptyList();
  }

  public static List<String> getFilenamesFromDirectoryMatching(String dirName, final String match)
  {
    File dir = new File(dirName); //e.g., "/data/images/celltango" 

    FilenameFilter filter = new FilenameFilter()
    {

      public boolean accept(File dir, String name)
      {
        if (!name.matches(match) || name.startsWith("."))
        {
          return false;
        }
        return true;
      }
    };

    String children[] = dir.list(filter);

    if (children != null)
    {
      return Arrays.asList(children);
    }

    return new ArrayList<String>(); //empty list
  }

  public static List<String> getFilenamesFromDirectory(String dirName, final String filetype)
  {
    File dir = new File(dirName); //e.g., "/data/images/celltango" 

    FilenameFilter filter = new FilenameFilter()
    {

      public boolean accept(File dir, String name)
      {
        if (!name.endsWith(filetype) || name.startsWith("."))
        {
          return false;
        }
        return true;
      }
    };

    String children[] = dir.list(filter);

    return Arrays.asList(children);
  }

  public static List<File> getFilesFromDirectory(String dirName)
  {
    File dir = new File(dirName); //e.g., "/data/images/celltango" 

    System.out.println("in getFilesFromDirectory: the dir is : " + dir);
    File[] files = dir.listFiles();

    if (files != null && files.length > 0)
    {
      return Arrays.asList(files);
    }
    return null;
  }

  public static List<File> getFilesFromDirectory(String dirName, final String filetype)
  {
    File dir = new File(dirName); //e.g., "/data/images/celltango" 

    FilenameFilter filter = new FilenameFilter()
    {

      public boolean accept(File dir, String name)
      {
        if (!name.endsWith(filetype) || name.startsWith("."))
        {
          return false;
        }
        return true;
      }
    };

    File[] files = dir.listFiles(filter);

    if (files != null && files.length > 0)
    {
      return Arrays.asList(files);
    }
    return null;
  }

  public static List<File> getFilesFromDirectoryMatching(String dirName, final String match)
  {
    //System.out.println("in getFilesFromDirectoryMatching() : dirName = " + dirName);
    File dir = new File(dirName); //e.g., "/data/images/celltango" 

    FilenameFilter filter = new FilenameFilter()
    {

      public boolean accept(File dir, String name)
      {
        System.out.print("dir = " + dir.getAbsolutePath() + ", filename = " + name);
        if (!name.matches(match) || name.startsWith("."))
        {
          System.out.println("... no does not match...");
          return false;
        }

        //System.out.println("... yes matches...");
        return true;
      }
    };

    File[] files = dir.listFiles(filter);

    if (files != null && files.length > 0)
    {
      return Arrays.asList(files);
    }
    return null;

  }

  public static void sortFilesByLastModified(List<File> files)
  {

    Collections.sort(files, new Comparator<File>()
    {

      public int compare(File o1, File o2)
      {
        return (int) (o2.lastModified() - o1.lastModified());
      }
    });

  }

  public static void sortFilesAlphabetically(List<File> files)
  {

    Collections.sort(files, new Comparator<File>()
    {

      public int compare(File o1, File o2)
      {
        return (int) (o2.getName().compareToIgnoreCase(o1.getName()));
      }
    });

  }

  public static BufferedImage loadBufferedImageFromFile(String filename)
  {
    return Utils.toBufferedImage(loadImageFromFile(toCrossPlatformFilename(filename)));
  }

  public static Image loadImageFromFile(String filename)
  {
    System.out.println("in loadImageFromFile... trying to load " + filename + "...");
    Image img = new ImageIcon(toCrossPlatformFilename(filename)).getImage();
    System.out.println("in loadImageFromFile... we loaded " + filename + " successfully...");
    return img;
  }

  public static BufferedImage loadBufferedImageFromFile(File file)
  {
    return Utils.toBufferedImage(loadImageFromFile(file));
  }

  public static Image loadImageFromFile(File file)
  {
    return new ImageIcon(file.toString()).getImage();
  }

  public static BufferedImage loadBufferedImageFromURL(String urlstr)
  {
    return Utils.toBufferedImage(loadImageFromURL(urlstr));
  }

  public static Image loadImageFromURL(String urlstr)
  {
    URL url = null;
    try
    {
      url = new URL(urlstr);
    }
    catch (MalformedURLException mue)
    {
      mue.printStackTrace();
    }

    System.out.println("loading image " + url);
    Image img = Toolkit.getDefaultToolkit().getImage(url);
    System.out.println("done loading image " + url);
    return img;
  //return new MyImageIcon(url).getImage();
  }

  public static BufferedImage loadBufferedImageFromURL(URL url)
  {
    return Utils.toBufferedImage(loadImageFromURL(url));
  }

  public static Image loadImageFromURL(URL url)
  {
    System.out.println("loading image " + url);
    Image img = Toolkit.getDefaultToolkit().getImage(url);
    System.out.println("done loading image " + url);
    return img;


  //return new ImageIcon(url).getImage();

  }

  public static URI toURI(URL url)
  {
    try
    {
      return url.toURI();
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }

    return null;
  }

  public static URI toURI(String filename)
  {
    return (new File(filename)).toURI();
  }

  public static URI toURI(File file)
  {
    return file.toURI();
  }

  public static URL toURL(URI uri)
  {
    try
    {
      return uri.toURL();
    }
    catch (MalformedURLException mue)
    {
      mue.printStackTrace();
    }

    return null;

  }

  public static URL toURL(String filename)
  {
    return toURL(new File(filename));
  }

  public static URL toURL(File file)
  {
    try
    {
      return file.toURI().toURL();
    }
    catch (MalformedURLException mue)
    {
      mue.printStackTrace();
    }
    return null;
  }

  public static void saveImageToFile(BufferedImage bi, String filename, String format)
  {
    try
    {
      File file = new File(filename + "." + format);
      ImageIO.setUseCache(false);
      ImageIO.write(bi, format, file);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
/*
if (children != null)
{
for (int i = 0; i < children.length; i++)
{
String videoId = children[i].substring(0, children[i].lastIndexOf("."));
System.out.println("filename = " + videoId);

//VideoData vd = ConnectorPostgres.getVideoDataById(Integer.parseInt(videoId));
VideoData vd = getFakeVideoDataFromTextFile(videoId);

if (vd == null)
{
System.out.println("error... text file not in local path!");
continue;
}

//vd.setUrl("file://data/youtube/"+videoId+".flv");
vd.setUrl("file:data/youtube/" + videoId + ".flv");
vds.add(vd);

System.out.println(vd);
if (i >= 15)
{
break;
}
}
}
else
{
//error! couldn't get files from directory
}

 */
