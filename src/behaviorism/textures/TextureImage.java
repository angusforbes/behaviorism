/* TextureImage.java ~ May 19, 2009 */
package behaviorism.textures;

import com.sun.opengl.util.texture.TextureIO;
import com.sun.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.grlea.log.SimpleLogger;

/**
 *
 * @author angus
 */
public class TextureImage extends Texture
{

  public com.sun.opengl.util.texture.TextureData textureData = null;
  public static final SimpleLogger log = new SimpleLogger(TextureImage.class);
  public boolean mipmapping = false;

  public Object tempTextureDataHackObject = null;

  public TextureImage()
  {
  }

  public TextureImage(URL url)
  {
    //generateTextureData(url, false);
    tempTextureDataHackObject = url;
  }

  public TextureImage(File file)
  {
    //generateTextureData(file, false);
    tempTextureDataHackObject = file;
  }

  public TextureImage(InputStream is)
  {
    //generateTextureData(file, false);
    tempTextureDataHackObject = is;
  }


  public TextureImage(BufferedImage bi, boolean mipmapping)
  {
    //generateTextureData(file, false);
    tempTextureDataHackObject = bi;
    this.mipmapping = mipmapping;
  }

  protected void initializeTexture() //defualt mipmapping = false
  {
    initializeTexture(false);
  }
  /**
   * Creates a new texture from the textureData. Assumes that textureData is indeed available.
   */
  protected void initializeTexture(boolean mipmapping)
  {
    log.entry("in initializeTexture()");

    if (tempTextureDataHackObject instanceof File)
    {
      log.debug("trying to load texture from file " + ((File) tempTextureDataHackObject));
      generateTextureData((File)tempTextureDataHackObject, mipmapping);
    }
    
    else if (tempTextureDataHackObject instanceof URL)
    {
      log.debug("trying to load texture from URL " + ((URL) tempTextureDataHackObject));
      generateTextureData((URL)tempTextureDataHackObject, mipmapping);
    }

    else if (tempTextureDataHackObject instanceof InputStream)
    {
      log.debug("trying to load texture from InputStream " + ((InputStream) tempTextureDataHackObject));
      generateTextureData((InputStream)tempTextureDataHackObject, mipmapping);
    }

    else if (tempTextureDataHackObject instanceof BufferedImage)
    {
      log.debug("trying to load texture from InputStream " + ((BufferedImage) tempTextureDataHackObject));
      generateTextureData((BufferedImage)tempTextureDataHackObject, mipmapping);
    }
    else
    {
      log.debug("what the hell is this? image is of type: " + tempTextureDataHackObject.getClass());
      System.err.println("what the hell is this? image is of type: " + tempTextureDataHackObject.getClass());
      return;
    }

    this.texture = TextureIO.newTexture(textureData);
    log.exit("out initializeTexture()");
  }

  public boolean updateTexture()
  {
    log.entry("in updateTexture()");
    if (isDone() == true)
    {
      log.debug("textureImage isDone = true, needs to be disposed of...");
      disposeTexture();
      log.exit("out updateTexture()");
      return false;
    }
//
//    if (textureData == null) //no data loaded
//    {
//      log.exit("in updateTexture() : GL textureData has not been loaded yet!");
//      return false;
//    }

    //if (texture == null && textureData != null) //texture needs to be intialized
    if (texture == null)// && textureData != null) //texture needs to be intialized
    {
      log.debug("GL texture = null, needs to be initialized");
      initializeTexture(mipmapping);
    
      copyDataToTexture();
      log.debug("we no longer need the TextureData, so lets flush it now...");
      textureData.flush();
      textureData = null;
    }
    
    log.exit("out updateTexture()");
    return true;
  }

  /**
   * Applies the new textureData to the texture, by default using the updateImage() method. This method
   * can be overwritten to specify alternative application behavior, for instance to use
   * updateSubImage(...) to handle tiling or reflection, etc.
   */
  protected void copyDataToTexture()
  {
    texture.updateImage(textureData);
  }

  protected void disposeTexture()
  {
    log.entry("in disposeTexture()");

    if (textureData != null)
    {
      log.info("flushing textureData...");
      textureData.flush();
      textureData = null;
    }
    if (texture != null)
    {
      log.info("disposing of GL texture...");
      texture.dispose();
      texture = null;
    }

    log.exit("out disposeTexture()");
  }

  /**
   * Initializes or updates the texture as necessary if new textureData is available.
   * @return true if the texture is ready, false otherwise.
   */
//  public boolean updateTexture()
//  {
//    log.entry("in updateTexture()");
//    if (isDone.get() == true)
//    {
//      log.debug("textureImage isDone = true, needs to be disposed of...");
//      disposeTexture();
//      log.exit("out updateTexture()");
//      return false;
//    }
//
//    if (textureData == null) //no data loaded
//    {
//      log.exit("in updateTexture() : textureData = false!");
//      return false;
//    }
//
//    if (texture == null) //texture needs to be intialized
//    {
//      log.debug("textureData = null, needs to be initialized");
//      initializeTexture();
//    }
//
//    if (isTextureWaiting == true) //texture needs to be updated
//    {
//      log.debug("texture is waiting");
//      applyImage();
//    }
//    //else
//    {
//      //System.err.println("texture is not waiting... don't apply image");
//    }
//
//    log.exit("out updateTexture()");
//    return true;
//  }
//
//
//  public boolean isReady()
//  {
//    if (this.texture != null && this.isDone.get() == false)
//    {
//      return true;
//    }
//    return false;
//  }
//
//  public boolean isDone()
//  {
//    return this.isDone.get();
//  }
//
//  public void destroy()
//  {
//    this.isDone.set(true);
//  }
//
//  protected void disposeTexture()
//  {
//    //System.out.println("TextureImage is being disposed...");
//    //todo - ADD the disposal for TextureImage IN!!!
//    if (textureData != null)
//    {
//      textureData.flush();
//      textureData = null;
//    }
//    if (texture != null)
//    {
//      texture.disposeTexture();
//      texture = null;
//    }
//  }
  /**
   * Creates a new texture from the textureData. Assumes that textureData is indeed available.
   * (probably we need to make this a private method!)
   */
//  public void initializeTexture()
//  {
//    log.entry("in initializeTexture()");
//    this.texture = TextureIO.newTexture(textureData);
//    this.isTextureWaiting = false;
//    log.exit("out initializeTexture()");
//  }
  public void generateTextureData(URL url, boolean useMipMaps)
  {
    this.name = url.toString();
    //setColor(1f, 1f, 1f, 1f);
    //String fileType = TextureIO.JPG;

    try
    {
      //this.textureData = TextureIO.newTextureData(url, useMipMaps, imageType);
      this.textureData = TextureIO.newTextureData(url, useMipMaps, null);
      this.w = textureData.getWidth();
      this.h = textureData.getHeight();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }


  }

  public void generateTextureData(InputStream is, boolean useMipMaps)
  {
    this.name = is.toString();
    //setColor(1f, 1f, 1f, 1f);
    //String fileType = TextureIO.JPG;

    try
    {
      //this.textureData = TextureIO.newTextureData(url, useMipMaps, imageType);
      this.textureData = TextureIO.newTextureData(is, useMipMaps, null);
      this.w = textureData.getWidth();
      this.h = textureData.getHeight();

      //close this because we have loaded it in completely.
      is.close();

    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();

    }


  }

  public void generateTextureData(File file, boolean useMipMaps)
  {
    this.name = file.toString();
    //setColor(1f, 1f, 1f, 1f);
    //String fileType = TextureIO.JPG;

    try
    {
      //System.err.println("file = " + file);
      //this.textureData = TextureIO.newTextureData(file, useMipMaps, fileType);
      this.textureData = TextureIO.newTextureData(file, useMipMaps, null);

      this.w = textureData.getWidth();
      this.h = textureData.getHeight();
    }
    catch (IOException ioe)
    {
      //System.err.println("in generateTextureData() : PROBLEM with FILE " + file);
      ioe.printStackTrace();
    }


    if (this.textureData == null)
    {
      //System.out.println("in generateTextureData() : textureData = null so DELETING " + file);
      //file.delete();
    }

  }

  
  public void generateTextureData(BufferedImage bi, boolean useMipMaps)
  {
    this.textureData = AWTTextureIO.newTextureData(bi, useMipMaps);
    this.w = textureData.getWidth();
    this.h = textureData.getHeight();
  }
  

  @Override
  public String toString()
  {
    return getClass() + "name = " + name;
  }
}
