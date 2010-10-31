/* Texture.java ~ Aug 25, 2009 */

package behaviorism.textures;

import behaviorism.geometry.Geom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.grlea.log.SimpleLogger;

/**
 *
 * @author angus
 */
abstract public class Texture
{
 String name = "none"; //either original url or file
  public List<Geom> attachedGeoms = new ArrayList<Geom>();
  public com.sun.opengl.util.texture.Texture texture = null;
  private AtomicBoolean isDone = new AtomicBoolean(false);
  public int w; //texture width in pixels
  public int h; //texture height in pixels

 public static final SimpleLogger log = new SimpleLogger(Texture.class);

 public Texture()
 {}

 /**
  * Initializes or updates the texture as necessary if new textureData is available.
  * @return true if the texture is ready, false otherwise.
  */

  public abstract boolean updateTexture();
  
  protected abstract void copyDataToTexture();
  protected abstract void disposeTexture();
  protected abstract void initializeTexture();

  public boolean isReady()
  {
    if (this.texture != null && this.isDone.get() == false)
    {
      return true;
    }
    return false;
  }

  public boolean isDone()
  {
    return this.isDone.get();
  }

  public void destroy()
  {
    this.isDone.set(true);
  }



  /**
   * Applies the new textureData to the texture, by default using the updateImage() method. This method
   * can be overwritten to specify alternative application behavior, for instance to use
   * updateSubImage(...) to handle tiling or reflection, etc.
   */
//  public void applyImage()
//  {
//    texture.updateImage(textureData);
//    isTextureWaiting = false;
//  }

}
