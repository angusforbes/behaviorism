/* TextureManager.java ~ Aug 16, 2009 */
package behaviorism. textures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.grlea.log.SimpleLogger;

/**
 *
 * @author angus
 */
public class TextureManager
{
  private List<TextureImage> textures;
  private static TextureManager instance = null;

 public static final SimpleLogger log = new SimpleLogger(TextureManager.class);

  /**
   * Gets (or creates then gets) the singleton TextureManager object.
   * @return the singleton TextureManager
   */
  public static TextureManager getInstance()
  {
    if (instance != null)
    {
      return instance;
    }

    instance = new TextureManager();

    return instance;
  }

  private TextureManager()
  {
    textures = new CopyOnWriteArrayList<TextureImage>();
  }

  /**
   * This should only be called from within the Render loop.
   */
  public void updateTextures()
  {
    log.entry("in updateTextures() : textures.size = " + textures.size());
    List<TextureImage> keeps = new ArrayList<TextureImage>();

    for (TextureImage t : textures)
    {
      t.updateTexture();
      if (t.isDone() == false)
      {
        keeps.add(t);
      }
    }

    textures.clear();
    textures.addAll(keeps);

    log.exit("out updateTextures() : textures.size = " + textures.size());
  }

  public void addTexture(TextureImage ti)
  {
    log.debug("in addTexture() : adding texture of type " + ti.getClass());
    this.textures.add(ti);

  }

  public void removeTexture(TextureImage ti)
  {
    log.debug("in removeTexture() : removing texture of type " + ti.getClass());
    if (ti != null)
    {
      ti.destroy();
    }
    //this.textures.remove(ti);
  }

  public int numActiveTextures()
  {
    return this.textures.size();
  }
}
