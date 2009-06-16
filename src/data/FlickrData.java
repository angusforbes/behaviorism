package data;

import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.tags.Tag;
import geometry.Colorf;
import geometry.Geom;
import geometry.media.GeomImage;
import geometry.text.GeomText2;
import geometry.RotateEnum;
import geometry.ScaleEnum;
import renderers.State;
import handlers.FlickrHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * FlickrData is a wrapper containing a subset of information
 * found in a flickrj.Photo object.
 */
public class FlickrData extends ImageData
{

  public com.aetrion.flickr.photos.Photo photo = null;
  public Set<String> tags = new HashSet<String>();
  public String title = "untitled";
  public String flickrId = "";
  public String username = "";
  public String photoset = "";
  public List<String> connectingTags = new ArrayList<String>();
  public GeomImage photoGeom = null;
  public int id = -1;

  public FlickrData()
  {
    dataType = DataEnum.IMAGE;
  }

  /** 
   * create a new FlickrData using a flickerj Photo object
   * @param photo
   */
  public FlickrData(com.aetrion.flickr.photos.Photo photo)
  {
    dataType = DataEnum.IMAGE;
    setPhoto(photo);
    unpackPhoto();
  }

  public FlickrData(com.aetrion.flickr.photos.Photo photo, boolean tagsFromTitle)
  {
    dataType = DataEnum.IMAGE;
    setPhoto(photo);
    unpackPhoto(tagsFromTitle);
  }

  public void setPhoto(com.aetrion.flickr.photos.Photo photo)
  {
    this.photo = photo;
  }

  public void unpackPhoto()
  {
    if (this.photo == null)
    {
      return;
    }

    //unpack url, id, username, title
    setUrl(photo.getSmallUrl());
    flickrId = photo.getId();
    username = photo.getOwner().getUsername();
    title = photo.getTitle();


    //unpack tags
    if (!(photo.getTags() == null))
    {
      List<Tag> tagList = new ArrayList<Tag>(photo.getTags());

      for (Tag tag : tagList)
      {
        this.tags.add(tag.getValue());
      }
    }
  }

  public void unpackPhoto(boolean tagsFromTitle)
  {
    if (this.photo == null)
    {
      return;
    }

    //unpack url, id, username, title
    setUrl(photo.getSmallUrl());
    flickrId = photo.getId();
    username = photo.getOwner().getUsername();
    title = photo.getTitle();

    if (tagsFromTitle == true)
    {
      if (title != null)
      {
        tags.addAll(parseTitleIntoTags(title));
      }
    }
    else //normal handling
    {

      //unpack tags
      if (!(photo.getTags() == null))
      {
        List<Tag> tagList = new ArrayList<Tag>(photo.getTags());

        for (Tag tag : tagList)
        {
          this.tags.add(tag.getValue());
        }
      }
    }
  }

  public static List<String> parseTitleIntoTags(String titleStr)
  {
    List<String> parsedTags = new ArrayList<String>();

    //does title contain punctuation?
    //if yes, split by punctuation
    //if not, split by space
    String[] splitOne = titleStr.split("\\p{Punct}");

    if (splitOne.length <= 1)
    {
      splitOne = titleStr.split(" ");
    }

    for (int i = 0; i < splitOne.length; i++)
    {
      String tag = splitOne[i];

      if (tag.length() <= 1)
      {
        continue;
      }

      tag = tag.trim().toLowerCase();
      //System.out.println("adding tag <" + tag + ">");
      parsedTags.add(tag);
    }

    return parsedTags;
  }

  @Override
  public String toString()
  {
    /*
    String s = "" + photoset + ":" + title + ", " + username + ", " + flickrId + "\n" + url;
    
    s += "\n\ttags: ";
    for (String tag : tags)
    {
    s += tag + " ";
    }
    
    return s;
     */

    return "" + url;
  }

  @Override
  public Geom makeShape()
  {
    return null;

    /*
    GeomImage g = new GeomImage(url);
    g.determineRotateAnchor(RotateEnum.CENTER);
    g.determineScaleAnchor(ScaleEnum.CENTER);
    g.state = new State();
    g.state.BLEND = false;
    g.state.DEPTH_TEST = true;

    String test = "";

    
    //test += photoset + ":" + title + ": ";
    for (String tag : tags)
    {
    //test += tag + " ";
    }
    

    test += id;

    GeomText2 gt = new GeomText2(0f, g.h, 0f, .5f, .5f, test);
    gt.justifyX = -1;
    gt.setColor(1f, 1f, 1f, 1f);
    //gt.backgroundColor = new Colorf(1f,0f,0f,1f);
    gt.registerObject(g);
    gt.state = new State();
    gt.state.BLEND = true;
    gt.state.DEPTH_TEST = true;

    g.addGeomToLayer(gt, true, 10);

    this.photoGeom = g;

    return g;
     */
  }

  /*
  public boolean equals(ImageData another)
  {
  //return this.url.equals(another.url);
  
  return this.url.toString().equals(another.url.toString());
  }
   */
}
