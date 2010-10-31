/* FileUtils.java ~ Aug 29, 2008 */
package behaviorism.utils;

import behaviorism.textures.TextureImage;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;

/**
 *
 * @author angus
 */
public class FileUtils
{

  public static String toCrossPlatformFilename(String filename)
  {
    return filename.replaceAll("\\\\+|/+", "/");
    //return filename.replaceAll("//", File.separator);
  }

  public static String getUserDirectoryName()
  {
    return System.getProperty("user.home");
  }

  public static List<String> getFilenamesFromDirectory(String dirName)
  {
    File dir = new File(dirName); //e.g., "/data/celltango"

    return getFilenamesFromDirectory(dir);
  }

  public static List<String> getFilenamesFromDirectory(File dir)
  {
    String children[] = dir.list();

    if (children != null)
    {
      return Arrays.asList(children);
    }
    return Collections.emptyList();
  }

  public static List<String> getFilenamesFromJar(String jarname)
  {
    List<String> filenames = new ArrayList<String>();
    JarFile jarFile;

    try
    {
      jarFile = new JarFile(jarname);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return filenames; //on error return empty list
    }

    Enumeration<JarEntry> entries = jarFile.entries();

    while (entries.hasMoreElements())
    {
      JarEntry entry = entries.nextElement();
      filenames.add(entry.getName());
    }

    return filenames;
  }


  public static List<String> listFilesFromDirectoryInJar(String dirname)
  {
    List<String> filenames = new ArrayList<String>();
    try
    {
      System.err.println("trying to read in " + dirname);
      InputStream is = FileUtils.class.getResourceAsStream(dirname);
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      System.err.println("did we find anything?");
      while ((line = br.readLine()) != null)
      {
        System.err.println("line = " + line);
        filenames.add(dirname + line);
      }
      br.close();
      isr.close();
      is.close();
      return filenames;
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }

    return null;
  }


/*
  public static List<String> getFilenamesFromJarMatching(String match)
  {

    List<String> filenames = getFilenamesFromJar(jarname);
    List<String> matches = new ArrayList<String>();

    for (String name : filenames)
    {
      if (name.matches(match) && !name.startsWith("."))
      {
        matches.add(name);
      }
    }
    return matches;
  }
*/
  public static List<String> getFilenamesFromDirectoryMatching(String dirName, final String match)
  {
    return getFilenamesFromDirectoryMatching(new File(dirName), match);
  }
    
  public static List<String> getFilenamesFromDirectoryMatching(File dir, final String match)
  {
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
    return getFilenamesFromDirectory(new File(dirName), filetype);
  }

  public static List<String> getFilenamesFromDirectory(File dir, final String filetype)
  {
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

  public static void deleteEntireFolder(File folder)
  {
    List<File> files = FileUtils.getFilesFromDirectory(folder);

    for (File file : files)
    {
      //System.out.println("we are deleteing " + file.getAbsoluteFile());
      file.delete();
    }

//and then delete it...
//System.out.println("we are now going to delete " + folder);
    folder.delete();
  }

  public static List<File> getFilesFromDirectory(String dirName)
  {
    File dir = new File(toCrossPlatformFilename(dirName)); //e.g., "/data/images/celltango"
    return getFilesFromDirectory(dir);
  }

  public static List<File> getFilesFromDirectory(File dir)
  {
    //System.out.println("in getFilesFromDirectory: the dir is : " + dir);
    File[] files = dir.listFiles();

    if (files != null && files.length > 0)
    {
      return Arrays.asList(files);
    }

    return Collections.emptyList();
  }

  public static List<File> getFilesFromDirectory(String dirName, final String filetype)
  {
    return getFilesFromDirectory(new File(toCrossPlatformFilename(dirName)), filetype); //e.g., "/data/images/celltango"
  }

  public static List<File> getFilesFromDirectory(File dir, final String filetype)
  {

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

    return Collections.emptyList();
  }

  public static List<File> getFilesFromDirectoryMatching(String dirName, final String match)
  {
    File dir = new File(toCrossPlatformFilename(dirName)); //e.g., "/data/images/celltango"

    return getFilesFromDirectoryMatching(dir, match);
  }

  public static List<File> getFilesFromDirectoryMatching(File dir, final String match)
  {
    FilenameFilter filter = new FilenameFilter()
    {

      public boolean accept(File dir, String name)
      {
         //System.out.println("dir = " + dir.getAbsolutePath() + ", filename = " + name);
        if (!name.matches(match) || name.startsWith("."))
        {
           //System.out.println("... no does not match...");
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

    return Collections.emptyList();
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
        return (o2.getName().compareToIgnoreCase(o1.getName()));
      }
    });

  }

  public static List<TextureImage> loadTexturesFromDirectory(String directory, int max)
  {
    List<TextureImage> photos = new ArrayList<TextureImage>();

    List<File> files = FileUtils.getFilesFromDirectory(directory, "jpg");

    // Collections.shuffle(files);

    int idx = 0;
    for (File f : files)
    {
      //System.err.println("file = " + f);

      try
      {
        photos.add(new TextureImage(f));
        idx++;

      }
      catch (IllegalArgumentException iae)
      {
        //iae.printStackTrace();
      }

      if (idx >= max)
      {
        break;
      }

    }

    return photos;
  }

  public static BufferedImage loadBufferedImageFromFile(
    String filename)
  {
    try
    {
      ImageIO.setUseCache(false);
      return ImageIO.read(new File(toCrossPlatformFilename(filename)));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return null;
    //return Utils.toBufferedImage(loadImageFromFile(toCrossPlatformFilename(filename)));
  }

  @Deprecated
  public static Image loadImageFromFile(
    String filename)
  {
    /*
    //System.out.println("in loadImageFromFile... trying to load " + filename + "...");
    Image img = new ImageIcon(toCrossPlatformFilename(filename)).getImage();
    //System.out.println("in loadImageFromFile... we loaded " + filename + " successfully...");
    return img;
     */
    return null;
  }

  public static BufferedImage loadBufferedImageFromFile(
    File file)
  {
    try
    {
      ImageIO.setUseCache(false);
      return ImageIO.read(file);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return null;
    //return Utils.toBufferedImage(loadImageFromFile(file));
  }

  @Deprecated
  public static Image loadImageFromFile(
    File file)
  {
    /*
    System.out.println("in loadImageFromFile : " + file);

    //return new ImageIcon(file.toString()).getImage();

    try
    {
    return Toolkit.getDefaultToolkit().getImage(file.toURI().toURL());
    }
    catch(Exception e)
    {
    e.printStackTrace();
    }
     */
    return null;
  }

  @Deprecated
  public static Image loadImageFromURL(
    String urlstr)
  {
    /*
    URL url = null;
    try
    {
    url = new URL(urlstr);
    }
    catch (MalformedURLException mue)
    {
    mue.printStackTrace();
    }

    //trying ImageIcon temporarily...

    System.out.println("loading image " + url);
    //    Image img = Toolkit.getDefaultToolkit().getImage(url);
    //    //System.out.println("done loading image " + url);
    //    return img;
    //

    return new ImageIcon(url).getImage();
     */
    return null;
  }

  public static BufferedImage loadBufferedImageFromURL(
    String urlstr)
  {
    try
    {
      return loadBufferedImageFromURL(new URL(urlstr));
    }
    catch (MalformedURLException mue)
    {
      mue.printStackTrace();
    }

    return null;
    //return Utils.toBufferedImage(loadImageFromURL(urlstr));
  }

  public static BufferedImage loadBufferedImageFromURL(
    URL url)
  {
    try
    {
      ImageIO.setUseCache(false);
      return ImageIO.read(url);
    }
    catch (IllegalArgumentException iae)
    {
      System.err.println("We caught an IllegalArgumentException " + iae);
      iae.printStackTrace();
    }
    catch (IOException e)
    {
      System.err.println("We caught an IOException " + e);
      e.printStackTrace();
    }

    System.err.println("in FileUtils.loadBufferedImageFromURL() : could not load image so returning null!");
    return null;
  }

  @Deprecated
  public static Image loadImageFromURL(
    URL url)
  {
    /*
    //System.out.println("loading image " + url);
    Image img = Toolkit.getDefaultToolkit().getImage(url);
    //System.out.println("done loading image " + url);
    return img;


    //return new ImageIcon(url).getImage();
     */
    return null;
  }

  public static URL loadURL(
    String urlStr)
  {
    URL url = null;
    try
    {
      url = new URL(urlStr);
    }
    catch (MalformedURLException mue)
    {
      mue.printStackTrace();
    }

    return url;
  }

  public static URI toURI(
    URL url)
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

  public static URI toURI(
    String filename)
  {
    return (new File(filename)).toURI();
  }

  public static URI toURI(
    File file)
  {
    return file.toURI();
  }

  public static URL toURL(
    URI uri)
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

  public static URL toURL(
    String filename)
  {
    return toURL(new File(filename));
  }

  public static URL toURL(
    File file)
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

  public static void saveImageToFile(BufferedImage bi, File file, String format)
  {
    if (bi.getWidth() < 1 || bi.getHeight() < 1)
    {
      return;
    }

    try
    {
      ImageIO.setUseCache(false);
      ImageIO.write(bi, format, file);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

  }

  public static void saveImageToFile(BufferedImage bi, String filename, String format)
  {
    if (bi.getWidth() < 1 || bi.getHeight() < 1)
    {
      return;
    }

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

  public static List<String> loadTextFromFile(String filename)
  {
    return loadTextFromFile(new File(filename));
  }

  public static List<String> loadTextFromFile(File file)
  {
    List<String> lines = new ArrayList<String>();

    try {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String str;
        while ((str = in.readLine()) != null) {
            lines.add(str);
        }
        in.close();
    }
    catch (IOException e)
    {
      System.err.println("error in loadTextFromFile!");
      e.printStackTrace();
    }

    return lines;
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
