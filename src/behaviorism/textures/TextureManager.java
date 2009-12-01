/* TextureManager.java ~ Aug 16, 2009 */
package behaviorism.textures;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.grlea.log.SimpleLogger;

/**
 *
 * @author angus
 */
public class TextureManager
{

  //private final List<Texture> textures = Collections.synchronizedList(new ArrayList<Texture>());
  private final Set<Texture> textures = Collections.synchronizedSet(new HashSet<Texture>());

  //private List<Texture> textures;
  private static final TextureManager instance = new TextureManager(); //null;
  public static final SimpleLogger log = new SimpleLogger(TextureManager.class);

  /**
   * Gets (or creates then gets) the singleton TextureManager object.
   * @return the singleton TextureManager
   */
  public static TextureManager getInstance()
  {
    return instance;
    /*
    if (instance != null)
    {
    return instance;
    }

    instance = new TextureManager();

    return instance;
     */
  }

  private TextureManager()
  {
    //this seems to be causing problems! no good way to remove elements. using synchronized list instead!
    //textures = new CopyOnWriteArrayList<Texture>();
  }

  /**
   * This should only be called from within the Render loop.
   * Using a synchronizedList instead of a CopyOnWriteArrayList because
   * the latter does not seem to work with my removal strategy.
   * May need to rethink if the synchornization causes slowness
   * (after all this gets called every single frame!). But it may not be an issue at all.
   */
  public void updateTextures()
  {
    log.entry("in updateTextures() : textures.size = " + textures.size());
    synchronized (textures)
    {
      Iterator<Texture> i = textures.iterator();

      while (i.hasNext())
      {
        Texture t = i.next();

        t.updateTexture();

        if (t.isDone() == true)
        {
          i.remove();
        }
      }

    }
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
