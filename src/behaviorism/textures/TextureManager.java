/* TextureManager.java ~ Aug 16, 2009 */
package behaviorism.textures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.grlea.log.SimpleLogger;

/**
 *
 * @author angus
 */
public class TextureManager
{

  private final List<Texture> textures = Collections.synchronizedList(new ArrayList<Texture>());
  //private List<Texture> textures;
  private static TextureManager instance = new TextureManager(); //null;
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
   */
  public void updateTextures()
  {
    log.entry("in updateTextures() : textures.size = " + textures.size());
    synchronized(textures)
    {
    System.err.println("in TextureManager : updateTextures(), size = " + textures.size());
    List<Texture> keeps = new ArrayList<Texture>();

    Iterator<Texture> i = textures.iterator();

    while(i.hasNext())
    {
      Texture t = i.next();

      t.updateTexture();

      if (t.isDone() == true)
      {
        i.remove();
      }
    }

//    for (Texture t : textures)
//    {
//      if (!(t instanceof TextureImage))
//      {
//        System.err.println("texture is a " + t.getClass());
//      }
//      t.updateTexture();
//      if (t.isDone() == false)
//      {
//        keeps.add(t);
//      }
//      else
//      {
//        System.err.println("" + t.getClass() + " is DONE");
//      }
//    }
//
//    log.debug("keeps size = " + keeps.size());
//
//    textures.clear();
//    textures.addAll(keeps);
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
    System.err.println("in addTexture() : adding texture of type " + ti.getClass());
    this.textures.add(ti);
  }

  public void removeTexture(Texture ti)
  {
    System.err.println("in removeTexture() : removing texture of type " + ti.getClass());
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
