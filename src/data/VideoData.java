/* VideoData.java (created on November 16, 2007, 2:13 PM) */

package data;

import geometry.BorderEnum;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomRect;
import geometry.media.GeomRectVideoFobs;
import geometry.RotateEnum;
import geometry.ScaleEnum;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Point3f;

public class VideoData extends Data
{
  public int id = -1;
  public String youtube_id = "";
  public String title = "";
  public String description = "";
  public String owner = ""; //ie candidate
  public URL url = null;
  //public URL imageurl = null;
  //public URL cached = null;
  public List<String> tags = new ArrayList<String>();
  
  public VideoData()
  {
    dataType = DataEnum.VIDEO;
  }

  public void setUrl(String urlstr)
  {
    try
    {
      this.url = new URL(urlstr);
    }
    catch(MalformedURLException mue)
    {
      mue.printStackTrace();
    }
  }

  public String toString()
  {
    String str = "";
    str += id + ", " + owner + ", " + title.trim() + ", " + url + ", tags: " + 
            Arrays.toString(tags.toArray()) ;

    return str;
  }

  public static List<VideoData> getVideoDataByOwner(List<VideoData> vds, String owner)
  {
    List<VideoData> ownerVideoData = new ArrayList<VideoData>();
		
    for (VideoData vd : vds)
    {
      
      if (((VideoData)vd).owner.equals(owner))
      {
        ownerVideoData.add(vd);
      }
    }
    return ownerVideoData;
  }

  @Override
  public Geom makeShape()
  {
      Colorf myColor = new Colorf( 0.6f, 0.3f, 0.6f, 1f );
      
    Geom g1 = new GeomRect(0,0,0,.2f,.2f);
    g1.setColor( myColor );
    
    Geom g = GeomRectVideoFobs.makeVideoHolder(new Point3f(0f,0f,0f), this, BorderEnum.RECTANGLE, .1f, myColor );
    g.determineRotateAnchor(RotateEnum.CENTER);
    g.determineScaleAnchor(ScaleEnum.CENTER);
    g1.addGeom(g,true);  
    return g1;
  }   
}
