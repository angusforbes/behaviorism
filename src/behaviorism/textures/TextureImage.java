/* TextureImage.java ~ May 19, 2009 */
package behaviorism.textures;

import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

  public TextureImage()
  {
  }

  public TextureImage(URL url)
  {
    generateTextureData(url, false);
  }

  public TextureImage(File file)
  {
    generateTextureData(file, false);
  }

  /**
   * Creates a new texture from the textureData. Assumes that textureData is indeed available.
   */
  protected void initializeTexture()
  {
    log.entry("in initializeTexture()");
    this.texture = TextureIO.newTexture(textureData);
    //this.isTextureWaiting = false;
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

    if (texture == null && textureData != null) //texture needs to be intialized
    {
      log.debug("GL texture = null, needs to be initialized");
      initializeTexture();
    
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
      //ioe.printStackTrace();
    }


    if (this.textureData == null)
    {
      //System.out.println("in generateTextureData() : textureData = null so DELETING " + file);
      //file.delete();
    }

  }

  public void generateTextureData(BufferedImage bi, boolean useMipMaps)
  {
    this.textureData = TextureIO.newTextureData(bi, useMipMaps);
  }

  @Override
  public String toString()
  {
    return getClass() + "name = " + name;
  }
}
