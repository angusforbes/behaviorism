/* YouTubeHandler.java (created on July 30, 2007, 8:27 PM) */

package handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.SAXException;

import be.roam.drest.service.common.HttpRestService;
import be.roam.drest.service.youtube.YouTubeComment;
import be.roam.drest.service.youtube.YouTubeUserProfile;
import be.roam.drest.service.youtube.YouTubeVideo;
import be.roam.drest.service.youtube.xml.YouTubeXmlParser;
import be.roam.util.CollectionsUtil;
import data.CommentData;
import data.Data;
import data.VideoData;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * YouTubeHandler is a wrapper to the drest library, which is itself a wrapper of
 * the YouTubeAPI. Videos, comments, users, etc that are available via the YouTubeAPI
 * are accessible through this class.
 * 
 * More information about the youTube library can be be found at:
 * http://code.google.com/p/drest/
 * 
 * @author angus
 */
public class YouTubeHandler extends HttpRestService
{
    private static final String YOU_TUBE_URL = "http://www.youtube.com/api2_rest?method=youtube.";
    private static final String developerId = "YicFkZ52898";

    public YouTubeHandler(String developerId)
    {
      super();
    }

    public static YouTubeHandler createInstance()
    {
      return new YouTubeHandler(developerId);
    }

    public void addCommentDatasToVideoDatas(List<VideoData> vds)
    {
      for (VideoData vd : vds)
      {
        addCommentDatasToVideoData(vd);
      }
    }
    
    public void addCommentDatasToVideoData(VideoData vd)
    {
      String ytv_id = vd.youtube_id;
      YouTubeVideo ytv = null;
      try
      {
        ytv = getVideoDetails(ytv_id);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }

      if (ytv != null)
      {
         if (ytv.getCommentList() != null)
        {
          //System.out.println("\tComments list size: "+ video.getCommentList().size());

          for (YouTubeComment ytc : ytv.getCommentList())
          {
            //System.out.println("\t\tComment by " + ytc.getAuthor() + ": " + ytc.getText());
            CommentData cd = new CommentData(ytc.getAuthor(), ytc.getText());
            Data.intertwineData(cd, vd);
          }
        }

        //YouTubeHandler.printVideo(ytv);
      }
    }

    public InputStream getUserProfileRaw(String userName) throws IOException
    {
        String youtubeurl = YOU_TUBE_URL + "users.get_profile&dev_id=" + developerId + "&user=" + userName;
        System.out.println("youtubeurl = " + youtubeurl);
        GetMethod method = createGetMethod(youtubeurl);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    public YouTubeUserProfile getUserProfile(String userName) throws IOException, SAXException
    {
      System.out.println("in getUserProfile()");
        InputStream inputStream = this.getUserProfileRaw(userName);
        if (inputStream == null) {
            return null;
        }
        YouTubeUserProfile userProfile = YouTubeXmlParser.parseUserProfile(inputStream);
        inputStream.close();
        if (userProfile != null) {
            userProfile.setUserName(userName);
        }
        return userProfile;
    }

    public InputStream getFavoriteVideosRaw(String userName) throws IOException {
        GetMethod method = createGetMethod(YOU_TUBE_URL + "users.list_favorite_videos&dev_id=" + developerId + "&user=" + userName);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    public List<YouTubeVideo> getFavoriteVideos(String userName) throws IOException, SAXException {
        InputStream inputStream = getFavoriteVideosRaw(userName);
        if (inputStream == null) {
            return null;
        }
        List<YouTubeVideo> list = YouTubeXmlParser.parseVideoList(inputStream);
        inputStream.close();
        return list;
    }

    public InputStream getFriendListRaw(String userName) throws IOException {
        GetMethod method = createGetMethod(YOU_TUBE_URL + "users.list_friends&dev_id=" + developerId + "&user=" + userName);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    /**
     * Retrieves the list of friends for the given user.
     * <p>
     * The response of YouTube is limited to the user name, number of uploaded videos, number
     * of favorite videos and the number of friends.
     * </p>
     *
     * @param userName name of the user
     *
     * @return list of user profiles for the friends
     *
     * @throws IOException
     * @throws SAXException
     */
    public List<YouTubeUserProfile> getFriendList(String userName) throws IOException, SAXException {
        InputStream inputStream = getFriendListRaw(userName);
        if (inputStream == null) {
            return null;
        }
        List<YouTubeUserProfile> list = YouTubeXmlParser.parseFriendList(inputStream);
        inputStream.close();
        return list;
    }

    public InputStream getVideoDetailsRaw(String videoId) throws IOException {
        GetMethod method = createGetMethod(YOU_TUBE_URL + "videos.get_details&dev_id=" + developerId + "&video_id=" + videoId);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    public YouTubeVideo getVideoDetails(String videoId) throws IOException, SAXException {
        InputStream inputStream = getVideoDetailsRaw(videoId);
        if (inputStream == null) {
            return null;
        }
        List<YouTubeVideo> list = YouTubeXmlParser.parseVideoList(inputStream);
        inputStream.close();
        if (CollectionsUtil.isNullOrEmpty(list)) {
            return null;
        }
        YouTubeVideo tubeVideo = list.get(0);
        // youtube doesn't put the video id in the XML... but we want it in there for consistancy.
        tubeVideo.setId(videoId);
        return tubeVideo;
    }

    public InputStream getVideoListByTagRaw(String tag, Integer pageNr, Integer resultsPerPage) throws IOException {
        String url = YOU_TUBE_URL + "videos.list_by_tag&dev_id=" + developerId + "&tag=" + tag;
        if (pageNr != null) {
            url += "&page=" + pageNr;
        }
        if (resultsPerPage != null) {
            url += "&per_page=" + resultsPerPage;
        }
        GetMethod method = createGetMethod(url);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    public List<YouTubeVideo> getVideoListByTag(String tag, Integer pageNr, Integer resultsPerPage) throws IOException, SAXException {
        InputStream inputStream = getVideoListByTagRaw(tag, pageNr, resultsPerPage);
        if (inputStream == null) {
            return null;
        }
        List<YouTubeVideo> list = YouTubeXmlParser.parseVideoList(inputStream);
        inputStream.close();
        return list;
    }

    public InputStream getVideosByUserRaw(String userName, int page, int perPage) throws IOException
    {
      String url = YOU_TUBE_URL + "videos.list_by_user&dev_id=" + developerId
                + "&user=" + userName
                + "&page=" + page
                + "&per_page=" + perPage;
      System.out.println("url = "  +url);
        GetMethod method = createGetMethod(url);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    public List<YouTubeVideo> getVideosByUser(String userName, int page, int perPage) throws IOException, SAXException
    {
        InputStream inputStream = getVideosByUserRaw(userName, page, perPage);
        if (inputStream == null)
        {
            return null;
        }
        List<YouTubeVideo> list = YouTubeXmlParser.parseVideoList(inputStream);
        inputStream.close();
        return list;
    }

    public InputStream getFeaturedVideosRaw() throws IOException {
        GetMethod method = createGetMethod(YOU_TUBE_URL + "videos.list_featured&dev_id=" + developerId);
        client.executeMethod(method);
        return method.getResponseBodyAsStream();
    }

    public List<YouTubeVideo> getFeaturedVideos() throws IOException, SAXException {
        InputStream inputStream = getFeaturedVideosRaw();
        if (inputStream == null) {
            return null;
        }
        List<YouTubeVideo> list = YouTubeXmlParser.parseVideoList(inputStream);
        inputStream.close();
        return list;
    }



   public String downloadVideo(String downloadURL, String filename) throws IOException
   {
      URL u = new URL(downloadURL);
      return downloadVideo(u, filename);
   }
   
  public String downloadVideo(URL u, String filename) throws IOException
  {
    
    String outputFile = "data/youtube/" + filename + ".flv";
      if (u == null)
      {
        System.err.println("Error in download video : couldn't find URL for " + outputFile);
        return null;
      }
     
    File checkFile = new File(outputFile);
    //System.out.println("get absolute file : " + checkFile.getAbsoluteFile());
    System.out.println("does  " + outputFile + " already exist?");
    if (checkFile.exists())
    {
      System.out.println("yes, file " + filename + " already loaded....");
      return "file:" + outputFile;
    }
     
    System.out.println("no, downloading id " + filename + " to " + outputFile);
            
    URLConnection uc = u.openConnection();
    String contentType = uc.getContentType();
    int contentLength = uc.getContentLength();
    
    InputStream raw = uc.getInputStream();
    InputStream in = new BufferedInputStream(raw);
    byte[] data = new byte[contentLength];
    int bytesRead = 0;
    int offset = 0;
    while (offset < contentLength)
    {
      bytesRead = in.read(data, offset, data.length - offset);
      if (bytesRead == -1)
      {
        break;
      }
      offset += bytesRead;
      //System.out.println("offest = " + offset);
    }
    in.close();
    
    if (offset != contentLength)
    {
      throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
    }
    
    FileOutputStream out = new FileOutputStream(outputFile);
    out.write(data);
    out.flush();
    out.close();
  
    return "file:" + outputFile;
     
  }
 
    
  public static void printVideo(YouTubeVideo video)
  {
    if (video == null)
    {
      System.out.println("No video");
    }
    else
    {
      System.out.println("Video: " + video.getTitle() + " (length: " + video.getLengthInSeconds() + ")");
      System.out.println("\tURL: " + video.getThumbnailUrl() + " id: " + video.getId());
      System.out.println("\tTags: " + video.getTags());
      System.out.println("\tDescription: " + video.getDescription());
      System.out.println("\tNumber of Comments: "+ video.getNrComments());
      if (video.getCommentList() != null)
      {
        System.out.println("\tComments list size: "+ video.getCommentList().size());
        
        for (YouTubeComment ytc : video.getCommentList())
        {
          System.out.println("\t\tComment by " + ytc.getAuthor() + ": " + ytc.getText());
        }
      }
    }
  }
  
}
