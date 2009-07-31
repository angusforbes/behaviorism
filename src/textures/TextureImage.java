/* TextureImage.java ~ May 19, 2009 */
package textures;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import geometry.Geom;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author angus
 */
public class TextureImage
{
  String name = "none"; //either original url or file
  public List<Geom> attachedGeoms = new ArrayList<Geom>();
  public TextureData textureData = null;
  public Texture texture = null;
  public boolean isTextureWaiting = false;
  public int w; //texture width in pixels
  public int h; //texture height in pixels

  public TextureImage(){}
  public TextureImage(URL url)
  {
    generateTextureData(url, false);
  }
  public TextureImage(File file)
  {
    generateTextureData(file, false);
  }

 /**
  * Initializes or updates the texture as necessary if new textureData is available.
  * @return true if the texture is ready, false otherwise.
  */
  public boolean updateTexture()
  {
    if (textureData == null) //no data loaded
    {
      //System.out.println("textureData = false!");
      return false;
    }

    if (texture == null) //texture needs to be intialized
    {
      //System.out.println("textureData = null");
      initializeTexture();
    }

    if (isTextureWaiting == true) //texture needs to be updated
    {
      //System.out.println("texture is waiting");
      applyImage();
    }

    return true;
  }


  public void dispose()
  {
    //todo - ADD the disposal for TextureImage IN!!!
  }

  /**
   * Creates a new texture from the textureData. Assumes that textureData is indeed available.
   * (probably we need to make this a private method!)
   */
  public void initializeTexture()
  {
    this.texture = TextureIO.newTexture(textureData);
    this.isTextureWaiting = false;
  }

  /**
   * Applies the new textureData to the texture, by default using the updateImage() method. This method
   * can be overwritten to specify alternative application behavior, for instance to use
   * updateSubImage(...) to handle tiling or reflection, etc.
   */
  public void applyImage()
  {
    texture.updateImage(textureData);
    isTextureWaiting = false;
  }

  public void generateTextureData(URL url, boolean useMipMaps)
  {
    this.name = url.toString();
    //setColor(1f, 1f, 1f, 1f);
    String imageType = TextureIO.JPG;

    try
    {
      this.textureData = TextureIO.newTextureData(url, useMipMaps, imageType);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }

    this.w = textureData.getWidth();
    this.h = textureData.getHeight();
  //	normalizeSize(this.w);
  //normalizeSize(maxSize);
  }


  public void generateTextureData(File file, boolean useMipMaps)
  {
    this.name = file.toString();
    //setColor(1f, 1f, 1f, 1f);
    String imageType = TextureIO.JPG;

    try
    {
      System.err.println("file = " + file);
      this.textureData = TextureIO.newTextureData(file, useMipMaps, imageType);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }

    this.w = textureData.getWidth();
    this.h = textureData.getHeight();
  //	normalizeSize(this.w);
  //normalizeSize(maxSize);
  }

//  //no mipmaps by default...
//  public static TextureData generateTextureData(BufferedImage bi)
//  {
//    return TextureIO.newTextureData(bi, false);
//  }

  public void generateTextureData(BufferedImage bi, boolean useMipMaps)
  {
    //setColor(1f, 1f, 1f, 1f);

    this.textureData = TextureIO.newTextureData(bi, useMipMaps);
  //normalizeSize(maxSize);
  }


  /*
  public void normalizeSize(float size)
  {
    if (textureData == null)
    {
      System.out.println("ERROR, no textureData!");
      return;
    }
    int tw = textureData.getWidth();
    int th = textureData.getHeight();

    //System.out.println("tw / th = " + tw + "/" + th);
    if (tw >= th)
    {
      normalizeSizeByWidth(tw, th, size);
    }
    else
    {
      normalizeSizeByHeight(tw, th, size);
    }
  }
   */

  public String toString()
  {
    return name;
  }
}
