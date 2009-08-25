/* TextureManager.java ~ Aug 16, 2009 */
package behaviorism.textures;

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

  private List<Texture> textures;
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
    textures = new CopyOnWriteArrayList<Texture>();
  }

  /**
   * This should only be called from within the Render loop.
   */
  public void updateTextures()
  {
    log.entry("in updateTextures() : textures.size = " + textures.size());
    List<Texture> keeps = new ArrayList<Texture>();

    for (Texture t : textures)
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

  /**
   * Disposes all textures immediately. This gets called when the application shuts down. To remove textures
   * during the life of the application use removeTexture().
   */
  public void disposeTextures()
  {
    for (Texture t : textures)
    {
      t.disposeTexture();
    }

    textures.clear();
  }

  public void addTexture(Texture ti)
  {
    log.debug("in addTexture() : adding texture of type " + ti.getClass());
    this.textures.add(ti);


  }

  public void removeTexture(Texture ti)
  {
    log.debug("in removeTexture() : removing texture of type " + ti.getClass());
    if (ti != null)
    {
      ti.destroy();
    }
  }

  public int numActiveTextures()
  {
    return this.textures.size();
  }
}
