/* JarUtils.java ~ Sep 13, 2009 */

package behaviorism.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.grlea.log.SimpleLogger;

/**
 * A collection of utility functions that deal with JAR files.
 * @author angus
 */
public class JarUtils 
{
  public static final SimpleLogger log = new SimpleLogger(JarUtils.class);

  private static boolean checkIfFilenameIsInPath(String name, String path)
  {
    log.verbose("in checkIfFilenameIsInPath() : checking if the name \"" + name + "\" contains the path \"" + path + "\"");

    if (name.startsWith(path) && !name.equals(path))
    {
      return true;
    }

    return false;
  }

  /**
   * List directory contents for a resource folder. Not recursive.
   * This is basically a brute-force implementation.
   * Works for regular files and also JARs.
   * Original code by Greg Briggs, modified by Angus Forbes to
   * work with remote jar files.
   *
   * @author Greg Briggs
   * @param clazz Any java class that lives in the same place as the resources you want.
   * @param path Should end with "/", but not start with one.
   * @return Just the name of each member item, not the full paths.
   * @throws URISyntaxException
   * @throws IOException
   */
  public static List<String> getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException
  {
    log.entry("in getResourceListing(" + clazz + ", " + path + ")");
   
    URL dirURL = clazz.getClassLoader().getResource(path);
    log.verbose("URL of resource folder is " + dirURL);

    Set<String> validNames = new HashSet<String>(); //avoid duplicates in case it is a subdirectory

    if (dirURL != null && dirURL.getProtocol().equals("file"))
    {
      log.verbose("the resource URL points to a normal file path.");
      for (String s : new File(dirURL.toURI()).list())
      {
        String name = "/" + path + "/" + s;
        log.verbose("found the file " + s + " under resource folder /" + path + "/");
        validNames.add(name);
      }

      log.exit("out getResourceListing()");
      return new ArrayList<String>(validNames);
    }

    if (dirURL == null)
    {
      System.err.println("URL or resource folder is null, try to get it from the class.");
      /*
       * In case of a jar file, we can't actually find a directory.
       * Have to assume the same jar as clazz.
       */
      String me = clazz.getName().replace(".", "/") + ".class";
      dirURL = clazz.getClassLoader().getResource(me);
    }

    //IS IT A JAR?
    if (dirURL.getProtocol().equals("jar"))
    {
      String rawPath = dirURL.getPath();
      log.verbose("the resource URL points to the jar path " + dirURL.getPath());

      String protocol = null;
      if (rawPath.startsWith("file:"))
      {
        rawPath = rawPath.substring(5, rawPath.length());
        protocol = "file";
      }
      else if (rawPath.startsWith("http://"))
      {
        protocol = "http";
      }

      String jarPath = rawPath.split("!")[0]; //dirURL.getPath().indexOf("!")); //strip out only the JAR file

      log.verbose("jarPath = " + jarPath + ", protocol = " + protocol);

      if (protocol.equals("file"))
      {
        //LOCAL FILE
        log.verbose("examining resources from jar in local file system...");
        JarFile jar = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
        while (entries.hasMoreElements())
        {
          String name = entries.nextElement().getName();

          if (checkIfFilenameIsInPath(name, path) == true)
          {
            log.verbose("adding /" + name + " to list of valid resources.");
            validNames.add("/" + name);
          }

        }
      }
      else if (protocol.equals("http"))
      {
        //REMOTE FILE
        log.verbose("examining resources from jar in remote file system (over http)...");

        JarInputStream in = new JarInputStream((new URL(jarPath)).openStream());

        Manifest mf = in.getManifest();
        Map<String, Attributes> mfentries = mf.getEntries();
        for (Map.Entry<String, Attributes> entry : mfentries.entrySet())
        {
          String name = entry.getKey();

          if (checkIfFilenameIsInPath(name, path) == true)
          {
            log.verbose("adding /" + name + " to list of valid resources.");
            validNames.add("/" + name);
          }
        }
      }

      log.exit("out getResourceListing()");
      return new ArrayList<String>(validNames);
    }

    UnsupportedOperationException uoe = new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    log.warnException(uoe);
    throw uoe;
  }



  public static List<InputStream> getInputStreamsFromJar(List<String> files)
  {
    log.entry("in getInputStreamsFromJar()");
    List<InputStream> streams = new ArrayList<InputStream>();

    for (String f : files)
    {
      log.verbose("loading " + f);

      InputStream is = getInputStreamFromJar(f);
      if (is != null)
      {
        streams.add(is);
      }
    }

    log.exit("out getInputStreamFromJar()");
    return streams;
  }

  public static InputStream getInputStreamFromJar(String filename)
  {
    return FileUtils.class.getResourceAsStream(filename);
  }
 
  public static String getStringFromJarFile(String filename)
  {
    try
    {
      System.err.println("trying to read in " + filename);
      InputStream is = JarUtils.class.getResourceAsStream(filename);
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null)
      {
        sb.append(line + "\n");
      }
      br.close();
      isr.close();
      is.close();
      return sb.toString();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }

    return null;
  }

  public static List<String> getStringsFromJarFile(String filename)
  {
    List<String> strings = new ArrayList<String>();

    try
    {
      System.err.println("trying to read in " + filename);
      InputStream is = JarUtils.class.getResourceAsStream(filename);
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      while ((line = br.readLine()) != null)
      {
        strings.add(line);
      }
      br.close();
      isr.close();
      is.close();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }

    return strings;
  }

}
