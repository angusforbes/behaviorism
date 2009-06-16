
package data;

import geometry.Geom;
import geometry.media.GeomImage;
import geometry.RotateEnum;
import geometry.ScaleEnum;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageData extends Data implements Comparator
{
  public URL url = null;
  //public BufferedImage bi = null;
  
  public ImageData()
  {
     dataType = DataEnum.IMAGE;
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

  public static List<ImageData> getImageDataByURLString(List<ImageData> vds, String urlString)
  {
    List<ImageData> imageDatas = new ArrayList<ImageData>();
		
    for (ImageData vd : vds)
    {
      if (((ImageData)vd).url.toString().equals(urlString))
      {
        imageDatas.add(vd);
      }
    }
    return imageDatas;
  }

  
  @Override
  public Geom makeShape()
  {
    return null;
    /*
    Geom g = new GeomImage(url);
    g.determineRotateAnchor(RotateEnum.CENTER);
    g.determineScaleAnchor(ScaleEnum.CENTER);
    return g;
     */
  }  

	
	public int compare(Object one, Object two)
	{
		return 0;
	}
	
	@Override
	public boolean equals(Object another)
	{
    if (another == null)
    {
      return false;
    }

    if (another instanceof ImageData)
    {
		//return this.url.equals(another.url);
		//boolean isEqual = this.url.toString().equals(((ImageData)another).url.toString());
		boolean isEqual = this.url.equals(((ImageData)another).url);


		/*
						System.out.println("is <" + this.url.toString() 
						+ "> equal to <" + ((ImageData)another).url.toString() + "?"); 

		if (isEqual == true)
		{
			System.out.println("yes!");
		}
		else
		{
			System.out.println("no!");
			
		}
		*/
		return isEqual;
    }
    return false;
	}

		//seems like this is REQUIRED if you override equals...

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 37 * hash + (this.url != null ? this.url.hashCode() : 0);
		return hash;
	}
	

}
