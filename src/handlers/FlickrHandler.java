package handlers;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.interestingness.InterestingnessInterface;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.SearchParameters;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.aetrion.flickr.tags.TagsInterface;
import data.FlickrData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import utils.Utils;

/** 
 * FlickrHandler is a wrapper for the flickrj library 
 * used to connect to and interact with Flickr webservices.
 * 
 * More information about the flickrj library can be be founds at:
 * http://sourceforge.net/projects/flickrj/
 */
public class FlickrHandler
{
  //angus-- these are the api keys for CellTango....
  private static String apikey = "af2c1f4c67cad5a9bec8a23d7fab5176";
  private static String secret = "fa71ba642fc8d800";

  private String frob = "72157607043914050-a96b436f1ad3f583-660307";
  //private String frob = "72157606048452845-e91edb57946f45d9-135799"; //in US?
 
  private String token = "72157606048413085-750017610872cc48";
  //private String token = "72157606048413085-750017610872cc48";
  private String nsid = "8789744@N06";
  //final static String apikey = "fc5851bfac67f1aabb36a580f2c760a3";
  //final static String secret = "4c4e439b408d4461";
  private String restHost = "www.flickr.com";
  private Flickr f;
  private REST rest;
  private RequestContext requestContext;
  private InterestingnessInterface interInter = null;
  private PhotosInterface photoInter = null;
  private TagsInterface tagInter = null;
  private PeopleInterface peopleInter = null;
  private PhotosetsInterface photosetsInter = null;
  private static FlickrHandler instance = null;

  public boolean isReady = false;
  
  public static void main(String[] args)
  {
    try
    {
      Flickr f = new Flickr(apikey, secret, new REST());
      createAuthenticationInformation(f);
    }
    catch(Exception e)
    {
      System.out.println("problem building authentication info...");
      e.printStackTrace();
    }
  }
  /**
   * This class is a singleton that is instantiated with the first call
   * to getInstance(). All further calls to getInstance() return the same
   * instance.
   * 
   * @throws javax.xml.parsers.ParserConfigurationException
   * @throws java.io.IOException
   * @throws org.xml.sax.SAXException
   */
  private FlickrHandler() throws ParserConfigurationException, IOException, SAXException, FlickrException
  {
    rest = new REST();
    rest.setHost(restHost);

    f = new Flickr(apikey, secret, rest);

    if (f == null)
    {
      return;
    }
    
    if (!authenticate())
    {
      return;
    }
    
    initialize();
    isReady = true;
  }

  private void initialize()
  {
    interInter = f.getInterestingnessInterface();
    photoInter = f.getPhotosInterface();
    tagInter = f.getTagsInterface();
    peopleInter = f.getPeopleInterface();
    photosetsInter = f.getPhotosetsInterface();
  }

  /**
   * method to get or create the sigleton FlickrHandler object.
   * @return the singleton FlickrHandler
   */
  public static FlickrHandler getInstance()
  {
    if (instance != null)
    {
      return instance;
    }

    try
    {
      System.out.println("in FlickrHandler : getInstance() : Connecting to Flickr...");
    
      instance = new FlickrHandler();
    }
    catch (FlickrException fe)
    {
      System.out.println("in FlickrHandler : getInstance() : FlickrException : Couldn't connect to Flickr!");
      fe.printStackTrace();
    }
    catch (UnknownHostException uhe)
    {
      System.out.println("in FlickrHandler : getInstance() : UnknownHostException : Couldn't connect to Flickr!");
      uhe.printStackTrace();
    }
    catch (ParserConfigurationException pce)
    {
      System.out.println("in FlickrHandler : getInstance() ParserConfigurationException : Couldn't connect to Flickr!");
      pce.printStackTrace();
    }
    catch (IOException ioe)
    {
      System.out.println("in FlickrHandler : getInstance() IOException : Couldn't connect to Flickr!");
      ioe.printStackTrace();
    }
    catch (SAXException saxe)
    {
      System.out.println("in FlickrHandler : getInstance() SAXException : Couldn't connect to Flickr!");
      saxe.printStackTrace();
    }
    
    return instance;
  }

  /**
   * sets the authentication token so that we have full
   * read/write/delete permissions on our Flickr account.
   */
  private boolean authenticate() throws UnknownHostException, SAXException, IOException, FlickrException
  {
    requestContext = RequestContext.getRequestContext();
    requestContext.setSharedSecret(secret);

    AuthInterface authInterface = f.getAuthInterface();

    //try
    {
      System.out.println("token ");
      Auth auth = authInterface.checkToken(token);
      printAuth(auth);
      requestContext.setAuth(auth);
    }
    /*
    catch (UnknownHostException uhe)
    {
      System.err.println("UnknownHostException!");
      //uhe.printStackTrace();
      return false;
    }
    catch (SAXParseException spe)
    {
      System.err.println("SAXParseException!");
      //spe.printStackTrace();
      return false;
    
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
     */
    return true;
  }

  /**
   * Creates the token which can then be used to
   * authenticate with full permissions. Exits immediately
   * so that you remember to set the instance variable named "token"
   * properly. 
   * 
   * Basically, you use this method the first time to generate
   * a token. Then you edit this class so that the 
   * constructor calls the normal authenticate() method. 
   * Recompile and you will have full permissions thereafter. 
   */
  private static void createAuthenticationInformation(Flickr f)
  {
    RequestContext requestContext = RequestContext.getRequestContext();
    //requestContext.setSharedSecret(secret);

    AuthInterface authInterface = f.getAuthInterface();

    String frob = "unknown";
    try
    {
      frob = authInterface.getFrob();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    System.out.println("frob: " + frob);

    URL url = null;
    try
    {
      url = authInterface.buildAuthenticationUrl(Permission.DELETE, frob);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    System.out.println("Press return after you granted access at this URL:");
    System.out.println(url.toExternalForm());

    try
    {

      BufferedReader infile =
        new BufferedReader(new InputStreamReader(System.in));
      String line = infile.readLine();

      Auth auth = authInterface.getToken(frob);

      printAuth(auth);
    }
    catch (Exception e)
    {
      System.out.println("Authentication failed");
      e.printStackTrace();
    }

    System.exit(0);
  }

  private static void printAuth(Auth auth)
  {
    System.out.println("Authentication success");
    // This token can be used until the user revokes it.
    System.out.println("Token: " + auth.getToken() + " -- save for next time!!!");
    System.out.println("nsid: " + auth.getUser().getId());
    System.out.println("Realname: " + auth.getUser().getRealName());
    System.out.println("Username: " + auth.getUser().getUsername());
    System.out.println("Permission: " + auth.getPermission().getType());
  }

  public void deleteGIFsForUser(String username)
  {
    PhotoList photoList = null;
    List<String> photosToBeDeleted = new ArrayList<String>();

    String id = usernameToUserId(username);

    if (id != null)
    {
      int page_num = 1;
      while (true) //loop through every photo
      {
        try
        {
          //perpage, page
          photoList = peopleInter.getPublicPhotos(id, 1, page_num);

          if (photoList.size() == 0)
          {
            System.out.println("done...");
            break;
          }

          Photo photo = (Photo) photoList.get(0);

          //this is necessary, else photo won't have complete info
          photo = photoInter.getPhoto(photo.getId());

          String originalUrl = photo.getOriginalUrl().toLowerCase();

          if (originalUrl.endsWith("gif"))
          {
            String flickrId = photo.getId();

            System.out.println("id " + flickrId + " is a GIF!!!");
            photosToBeDeleted.add(photo.getId());
          }
          else
          {
            //System.out.println("jpg... ");
          }
        }
        catch (Exception e)
        {
          e.printStackTrace();

          System.out.println("error at page_num " + page_num);
          break;
        }

        page_num++;
      }

      //delete the gifs...
      for (String delete_photoId : photosToBeDeleted)
      {
        try
        {
          System.out.println("deleting " + delete_photoId);
          photoInter.delete(delete_photoId);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.out.println("couldn't delete!!!");
        }
      }
    }

    System.out.println("out deleteGIFsForUser()");
  }

  
  public FlickrData getPhotoByPhotoId(String photoId, boolean specialTagHandler)
  {
      try
      {
        //this is necessary, else photo won't have complete info
        Photo photo = photoInter.getPhoto(photoId);

        //System.out.println("photo title = " + photo.getTitle());
        //System.out.println("photo.getSmallUrl() = " + photo.getSmallUrl());
        //this will automatically unpack all tags, etc as well
        return new FlickrData(photo, specialTagHandler);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
  }
  
  public FlickrData getPhotoByPhotoId(String photoId)
  {
      try
      {
        //this is necessary, else photo won't have complete info
        Photo photo = photoInter.getPhoto(photoId);

        //this will automatically unpack all tags, etc as well
        return new FlickrData(photo);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
  }
  
  public List<FlickrData> unpackPhotoList(com.aetrion.flickr.photos.PhotoList photoList)
  {
    List<FlickrData> flickrs = new ArrayList<FlickrData>();

    for (int i = 0; i < photoList.size(); i++)
    {
      Photo photo = (Photo) photoList.get(i);

      try
      {
        //this is necessary, else photo won't have complete info
        photo = photoInter.getPhoto(photo.getId());

        //this will automatically unpack all tags, etc as well
        flickrs.add(new FlickrData(photo));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    return flickrs;
  }

  public String emailToUserId(String email)
  {
    String id = null;

    try
    {
      User user = peopleInter.findByEmail(email);
      id = user.getId();

    //System.out.println("username for " + email + " = [" + user.getUsername() + "]");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return id;
  }

  public String usernameToUserId(String username)
  {
    String id = null;

    try
    {
      User user = peopleInter.findByUsername(username);
      id = user.getId();
      System.out.println("found id " + id + " for username [" + username + "]");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return id;
  }

  public List<FlickrData> searchByEmail(String email, int numToReturn)
  {
    PhotoList photoList = null;
    List<FlickrData> flickrs = new ArrayList<FlickrData>();

    String id = emailToUserId(email);
    if (id != null)
    {
      try
      {
        photoList = peopleInter.getPublicPhotos(id, numToReturn, 1);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

      flickrs.addAll(unpackPhotoList(photoList));
    }
    return flickrs;
  }

  public List<String> getPhotoIdsForUser(String username)
  {
    List<String> photoIds = new ArrayList<String>();

    PhotoList photoList = null;

    String id = usernameToUserId(username);
    if (id != null)
    {
      for (int a = 0; a < 1000; a++) //prob won't have more than 100,000 photos
      {
        try
        {
          photoList = peopleInter.getPublicPhotos(id, 100, a);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

        if (photoList.size() == 0)
        {
          break;
        }

        for (int i = 0; i < photoList.size(); i++)
        {
          Photo photo = (Photo) photoList.get(i);
      System.out.println("here... we already know photo title = " + photo.getTitle());
      System.out.println("here... we already know photo.getSmallUrl() = " + photo.getSmallUrl());
        
          photoIds.add(photo.getId());
        }
      }
    }
    return photoIds;
  }

  //assuming maxPhotos < 100 (which is the max that can be returned in one page.)
  public List<Photo> getNPhotosForUser(String username, int maxPhotos)
  {
    List<Photo> photos = new ArrayList<Photo>();

    PhotoList photoList = null;

    String id = usernameToUserId(username);
    if (id != null)
    {
      for (int a = 1; a < 2; a++) //prob won't have more than 100,000 photos
      {
        try
        {
          photoList = peopleInter.getPublicPhotos(id, maxPhotos, a);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

        if (photoList.size() == 0)
        {
          break;
        }

        for (int i = 0; i < photoList.size(); i++)
        {
          Photo photo = (Photo) photoList.get(i);
          System.out.println("photo = " + photo.getTitle());
          photos.add(photo);
        }
        
      }
    }
    return photos;
  }

  //assuming maxPhotos < 100 (which is the max that can be returned in one page.)
  public List<Photo> getSomeNewPhotosAndSomeOldPhotosForUser(String username, int numNew, int numOld)
  {
    List<Photo> photos = new ArrayList<Photo>();

    PhotoList photoList = null;

    String id = usernameToUserId(username);
    if (id != null)
    {
      for (int a = 1; a < 100; a++) //prob won't have more than 10,000 photos
      {
        try
        {
          photoList = peopleInter.getPublicPhotos(id, 100, a);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

        if (photoList.size() == 0)
        {
          break;
        }

        for (int i = 0; i < photoList.size(); i++)
        {
          Photo photo = (Photo) photoList.get(i);
          System.out.println("photo = " + photo.getTitle());
          photos.add(photo);
        }
        
      }
    }

    System.out.println("in getSomeNewPhotosAndSomeOldPhotosForUser() : photos.size = " + photos.size() + ", numOld = " + numOld + ", numNew = " + numNew);
    if (photos.size() < numNew) //jsut get all of them
    {
      photos = photos.subList(0,photos.size());
    }
    else
    {
      List<Photo> newPhotos;
      List<Photo> oldPhotos;
    
      newPhotos = new ArrayList<Photo>(photos.subList(0,numNew));
      oldPhotos = new ArrayList<Photo>(photos.subList(numNew, photos.size()));
      Collections.shuffle(oldPhotos);
      photos = newPhotos;
      
      if (oldPhotos.size() <= numOld)
      {
        photos.addAll(oldPhotos);
      }
      else
      {
        photos.addAll(oldPhotos.subList(0, numOld));
      }
    }
    
    return photos;
  }

  public List<Photo> getPhotosForUser(String username)
  {
    List<Photo> photos = new ArrayList<Photo>();

    PhotoList photoList = null;

    String id = usernameToUserId(username);
    if (id != null)
    {
      for (int a = 1; a < 1000; a++) //prob won't have more than 100,000 photos
      {
        try
        {
          photoList = peopleInter.getPublicPhotos(id, 100, a);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }

        if (photoList.size() == 0)
        {
          break;
        }

        for (int i = 0; i < photoList.size(); i++)
        {
          Photo photo = (Photo) photoList.get(i);
          System.out.println("photo = " + photo.getTitle());
          photos.add(photo);
        }
        
      }
    }
    return photos;
  }

  public List<FlickrData> searchByUsername(String username, int numToReturn)
  {
    PhotoList photoList = null;
    List<FlickrData> flickrs = new ArrayList<FlickrData>();

    String id = usernameToUserId(username);
    if (id != null)
    {
      try
      {
        photoList = peopleInter.getPublicPhotos(id, numToReturn, 1);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

      flickrs.addAll(unpackPhotoList(photoList));
    }
    return flickrs;
  }

  
  public List<Photoset> getPhotosetsByUserID(String id)
  {
    List<Photoset> photosets = new ArrayList<Photoset>();

    try
    {
      Collection c = photosetsInter.getList(id).getPhotosets();

      for (Object o : c)
      {
        photosets.add((Photoset) o);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return photosets;
  }

  public List<Photoset> getPhotosetsByUsername(String username)
  {
    List<Photoset> photosets = new ArrayList<Photoset>();

    String id = usernameToUserId(username);

    try
    {
      Collection c = photosetsInter.getList(id).getPhotosets();

      for (Object o : c)
      {
        photosets.add((Photoset) o);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return photosets;
  }

  public List<FlickrData> getPhotosInPhotoset(String id, int numToReturn)
  {
    PhotoList photoList = null;
    try
    {
      //to do -- implement paging scheme
      photoList = photosetsInter.getPhotos(id, numToReturn, 1); //perpage, pagenum

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    List<FlickrData> flickrs = new ArrayList<FlickrData>();
    flickrs.addAll(unpackPhotoList(photoList));

    return flickrs;
  }

  
  public List<FlickrData> searchByEmailAndTags(String email, List<String> tags, int numToReturn)
  {
    //PhotoList photoList = null;
    //List<FlickrData> flickrs = new ArrayList<FlickrData>();

    String id = emailToUserId(email);
    System.out.println("id = " + id);
    if (id != null)
    {
      String[] tagArray = (String[]) tags.toArray(new String[tags.size()]);

      SearchParameters sps = new SearchParameters();
      sps.setUserId(id);
      sps.setTags(tagArray);
      sps.setTagMode("all"); //ie INTERSECTION of tags
      //sps.setTagMode("AND"); //ie INTERSECTION of tags
      //sps.setSort(SearchParameters.INTERESTINGNESS_DESC);

      return search(sps, numToReturn);
    }

    return null;
  }

  public FlickrData getNewRandomPhotoByTags(Set<FlickrData> existingFlickrDatas, List<String> tags)
  {
    int total = countByTags(tags);

    Integer[] shuffledInts = Utils.shuffledArrayOfInts(total);

    for (int i = 0; i < shuffledInts.length; i++)
    {
      List<FlickrData> fds = searchByTags(tags,
        1, i);

      boolean addedSuccessfully = existingFlickrDatas.add(fds.get(0));

      //if (addedSuccessfully == true || i == maxAttempts)
      if (addedSuccessfully == true)
      {
        fds.get(0).id = existingFlickrDatas.size();

        return fds.get(0);
      }
    }
    return null; //error

  }

  public FlickrData getNewRandomPhotoByEmailAndTags(Set<FlickrData> existingFlickrDatas, String email, List<String> tags)
  {
    int maxAttempts = 3;

    for (int i = 0; i < maxAttempts + 1; i++)
    {
      int total = countByEmailAndTags("angus_forbes@yahoo.com", tags);

      if (total == 0)
      {
        System.out.println("\t\t\ttotal = 0, returning null");
        return null;
      }

      List<FlickrData> fds = searchByEmailAndTags("angus_forbes@yahoo.com", tags, 1,
        Utils.randomInt(0, total));

      if (fds.size() == 0)
      {
        System.out.println("\t\t\treturn data size = 0, returning null");
        return null;
      }

      boolean addedSuccessfully = existingFlickrDatas.add(fds.get(0));

      System.out.println("\t\t\ton count #" + i + " added successfully? " + addedSuccessfully);
      //if (addedSuccessfully == true || i == maxAttempts)
      if (addedSuccessfully == true)
      {
        System.out.println("\t\t\tsuccess or last one...");
        fds.get(0).id = existingFlickrDatas.size();

        return fds.get(0);
      }

    }
    return null; //error

  }

  public FlickrData getRandomPhotoByEmail(String email)
  {
    List<String> emptyList = new ArrayList<String>();
    int total = countByEmailAndTags("angus_forbes@yahoo.com", emptyList);

    List<FlickrData> fds = searchByEmailAndTags("angus_forbes@yahoo.com", emptyList, 1,
      Utils.randomInt(0, total));

    return fds.get(0);
  }

  public FlickrData getRandomPhotoByEmailAndTags(String email, List<String> tags)
  {
    int total = countByEmailAndTags("angus_forbes@yahoo.com", tags);

    List<FlickrData> fds = searchByEmailAndTags("angus_forbes@yahoo.com", tags, 1,
      Utils.randomInt(0, total));

    return fds.get(0);
  }

  public List<FlickrData> searchByEmailAndTags(String email, List<String> tags, int perPage, int page)
  {
    String id = emailToUserId(email);
    if (id != null)
    {
      String[] tagArray = (String[]) tags.toArray(new String[tags.size()]);

      SearchParameters sps = new SearchParameters();
      sps.setUserId(id);
      sps.setTags(tagArray);
      sps.setTagMode("all"); //ie INTERSECTION of tags

      return search(sps, perPage, page);
    }

    return null;
  }

  public int countByTags(List<String> tags)
  {
    String[] tagArray = (String[]) tags.toArray(new String[tags.size()]);
    SearchParameters sps = new SearchParameters();
    sps.setTags(tagArray);
    sps.setTagMode("all"); //ie INTERSECTION of tags

    return count(sps);
  }

  public int countByEmailAndTags(String email, List<String> tags)
  {
    String id = emailToUserId(email);
    if (id != null)
    {
      String[] tagArray = (String[]) tags.toArray(new String[tags.size()]);
      SearchParameters sps = new SearchParameters();
      sps.setUserId(id);
      sps.setTags(tagArray);
      sps.setTagMode("all"); //ie INTERSECTION of tags

      return count(sps);
    }

    return -1; //error

  }

  public List<FlickrData> searchByUsernameAndTags(String username, List<String> tags, int numToReturn)
  {
    String id = usernameToUserId(username);
    if (id != null)
    {
      String[] tagArray = (String[]) tags.toArray(new String[tags.size()]);

      SearchParameters sps = new SearchParameters();
      sps.setUserId(id);
      sps.setTags(tagArray);
      sps.setTagMode("all"); //ie INTERSECTION of tags
      //sps.setTagMode("AND"); //ie INTERSECTION of tags
      //sps.setSort(SearchParameters.INTERESTINGNESS_DESC);

      return search(sps, numToReturn);
    }

    return null;
  }

  
  public List<FlickrData> searchByTags(List<String> strings, int perPage, int page)
  {
    String[] tagArray = (String[]) strings.toArray(new String[strings.size()]);

    SearchParameters sps = new SearchParameters();
    sps.setTags(tagArray);
    sps.setTagMode("all"); //ie INTERSECTION of tags
    //sps.setTagMode("AND"); //ie INTERSECTION of tags
    //sps.setSort(SearchParameters.INTERESTINGNESS_DESC);

    return search(sps, perPage, page);
  }

  public List<FlickrData> searchByTag(String tag, int numToReturn)
  {
    String[] tagArray = new String[]{tag}; 

    SearchParameters sps = new SearchParameters();
    sps.setTags(tagArray);
    sps.setTagMode("all"); //ie INTERSECTION   of tags
    //sps.setTagMode("AND"); //ie INTERSECTION of tags
    //sps.setSort(SearchParameters.INTERESTINGNESS_DESC);

    return search(sps, numToReturn);
  }

  public List<FlickrData> searchByTags(List<String> strings, int numToReturn)
  {
    String[] tagArray = (String[]) strings.toArray(new String[strings.size()]);

    SearchParameters sps = new SearchParameters();
    sps.setTags(tagArray);
    sps.setTagMode("all"); //ie INTERSECTION of tags
    //sps.setTagMode("AND"); //ie INTERSECTION of tags
    //sps.setSort(SearchParameters.INTERESTINGNESS_DESC);

    return search(sps, numToReturn);
  }

  public int count(SearchParameters params)
  {
    PhotoList photoList = null;
    try
    {
      photoList = photoInter.search(params, 1, 1); //default vals to return one entry, is enough to get total count

      return photoList.getTotal();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return -1; //error
  }

  public List<FlickrData> search(SearchParameters params, int numToReturn)
  {
    PhotoList photoList = null;
    try
    {
      //to do -- implement paging scheme
      photoList = photoInter.search(params, numToReturn, 1); //perpage, pagenum

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    List<FlickrData> flickrs = new ArrayList<FlickrData>();
    flickrs.addAll(unpackPhotoList(photoList));
    return flickrs;
  }

  public List<FlickrData> search(SearchParameters params, int perPage, int page)
  {
    PhotoList photoList = null;
    try
    {
      //to do -- implement paging scheme
      photoList = photoInter.search(params, perPage, page);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    List<FlickrData> flickrs = new ArrayList<FlickrData>();
    flickrs.addAll(unpackPhotoList(photoList));
    return flickrs;
  }
}
