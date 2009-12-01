/* TextureFBO.java ~ Jun 4, 2009 */
package behaviorism.textures;

import behaviorism.Behaviorism;
import behaviorism.utils.RenderUtils;
import com.sun.opengl.util.texture.TextureIO;
import org.grlea.log.SimpleLogger;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static behaviorism.utils.RenderUtils.*;

/**
 *
 * @author angus
 */
public class TextureFBO extends Texture
{

  public int offScreenWidth;
  public int offScreenHeight;
  public int fboId = -1;
  public int rboId = -1;

  public static final SimpleLogger log = new SimpleLogger(TextureFBO.class);

  public TextureFBO(int offScreenWidth, int offScreenHeight)
  {
    log.entry("in TextureFBO("+ offScreenWidth + ", " + offScreenHeight + ") constructor");
    this.offScreenWidth = offScreenWidth;
    this.offScreenHeight = offScreenHeight;
    log.exit("out TextureFBO() constructor");
  }

  public boolean updateTexture()
  {
    log.entry("TextureFBO : in updateTexture()");

    if (isDone() == true)
    {
      log.debug("isDone = true, needs to be disposed of...");
      disposeTexture();
      log.exit("out updateTexture()");
      return false;
    }

    //if (Behaviorism.renderer.boundsHaveChanged == true || texture == null) //texture needs to be intialized
    if (texture == null) //texture needs to be intialized
    {
      log.debug("texture = null, so we need to initialize it.");
      initializeTexture();
    }

    log.exit("TextureFBO : out updateTexture()");
    return true;
  }

  public void bindFBO()
  {
    log.entry("in bindFBO()");
    GL2 gl = getGL();
    //0. bind our FBO and start drawing on it
    gl.glBindFramebuffer(GL_FRAMEBUFFER, fboId);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
//    int www =  RenderUtils.getViewport()[2];
//    int hhh =  RenderUtils.getViewport()[3];

    //gl.glViewport(0, 0, www, hhh);
    gl.glViewport(0, 0, offScreenWidth, offScreenHeight);
    log.exit("out bindFBO()");
  }

  public void unbindFBO()
  {
    log.entry("in unbindFBO()");
    GL2 gl = getGL();
    // we are finished drawing to our FBO, so unbind it and return to our original viewport, etc
    gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    
    int www =  RenderUtils.getViewport()[2];
    int hhh =  RenderUtils.getViewport()[3];
    
    gl.glViewport(0, 0, www, hhh);
    log.exit("out unbindFBO()");
  }

  /**
   * This is just a template,
   * obviously you need to override this and make it actually draw something into the FBO!
   */
  public void applyFBO()
  {
    log.warn("in applyFBO() : uh-oh... this method should be overridden!");
    GL2 gl = getGL();

    bindFBO();

    gl.glPushMatrix();
    {
      //here's where you draw stuff into FBO...
    }
    gl.glPopMatrix();

    unbindFBO();
  }

  protected void initializeTexture()
  {
    log.entry("in initializeTexture()");
    GL2 gl = getGL();

    if (!generateFBO())
    {
      log.error("in TextureFBO : couldn't create FBO");
      //System.exit(0);
      //return;
    }

    log.exit("in initializeTexture()");
  }

  protected void copyDataToTexture(){}
  
  protected void disposeTexture()
  {
    log.entry("in disposeTexture()");

    GL2 gl = getGL();

    if (this.fboId >= 0)
    {
      log.info("deleting FBO");
      gl.glDeleteFramebuffers(1, new int[]
        {
          this.fboId
        }, 0);

      this.fboId = -1;
    }

    if (this.rboId >= 0)
    {
      log.info("deleting RBO");
      gl.glDeleteRenderbuffers(1, new int[]
        {
          this.rboId
        }, 0);

      this.rboId = -1;
    }

    if (texture != null)
    {
      log.info("disposing of GL texture...");
      texture.destroy(gl);
      texture = null;
    }

    log.exit("out disposeTexture()");
  }

  public boolean generateFBO()
  {
    GL2 gl = getGL();
    log.entry("in generateFBO()");
    boolean fboUsed;

    this.texture = TextureIO.newTexture(GL_TEXTURE_2D);
    this.texture.bind();

    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    //gl.glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE); // automatic mipmap
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, offScreenWidth, offScreenHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
    //gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, offScreenWidth, offScreenHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
    gl.glBindTexture(GL_TEXTURE_2D, 0);

    // create a renderbuffer object to store depth info
    int[] rboBindId = new int[1];
    gl.glGenRenderbuffers(1, rboBindId, 0);
    this.rboId = rboBindId[0];
    gl.glBindRenderbuffer(GL_RENDERBUFFER, this.rboId);
    gl.glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, offScreenWidth, offScreenHeight);
    gl.glBindRenderbuffer(GL_RENDERBUFFER, 0);

    // create a framebuffer object and attach the color texture and depth renderbuffer
    int[] fboBindId = new int[1];
    gl.glGenFramebuffers(1, fboBindId, 0);
    this.fboId = fboBindId[0];
    gl.glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);

    int textureLevel = 0; //not using mipmaps so only first level is available
    // attach the texture to FBO color attachment point
    gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getTextureObject(), textureLevel);
    // attach the renderbuffer to depth attachment point
    gl.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, this.rboId);

    // check FBO status
    int status = gl.glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE)
    {
      fboUsed = false;
      log.warn("GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n");
    }
    else
    {
      fboUsed = true;
      log.info("GL_FRAMEBUFFER_COMPLETE_EXT OK, using FBO");
      log.info("fbo offScreenWidth/Height = " + offScreenWidth + "/" + offScreenHeight);
      log.info("fbo texture id=" + this.texture.getTextureObject());
      log.info("fbo id=" + fboId);
      log.info("fbo's rbo id="+rboId);
    }

    // switch back to window-system-provided framebuffer
    gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    log.exit("out generateFBO()");

    return fboUsed;
  }
}
