/* TextureFBO.java ~ Jun 4, 2009 */
package textures;

import behaviorism.BehaviorismDriver;
import com.sun.opengl.util.texture.TextureIO;
import geometry.Colorf;
import geometry.Geom;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL.*;

/**
 *
 * @author angus
 */
public class TextureFBO extends TextureImage
{
  public int offScreenWidth;
  public int offScreenHeight;
  public int fboId = -1;
  public int rboId = -1;

  public TextureFBO(int offScreenWidth, int offScreenHeight)
  {
    this.offScreenWidth = offScreenWidth;
    this.offScreenHeight = offScreenHeight;
  }

  @Override
  public boolean updateTexture()
  {
    //if (BehaviorismDriver.renderer.boundsHaveChanged == true || texture == null) //texture needs to be intialized
    if ( texture == null) //texture needs to be intialized
    {
      System.out.println("reinitialize!!!!!!!!!");
      //dispose(); //if we are recreating because bounds have changed
      initializeTexture();
    }

    //if (isTextureWaiting == true) //texture needs to be updated
    {
      //System.out.println("texture is waiting");
      applyImage();
    }

    return true;
  }

  /**
   * This is just a template,
   * obviously you need to override this and make it actually draw something into the FBO!
   */
  @Override
  public void applyImage()
  {
    GL gl = GLU.getCurrentGL();

    //0. bind our FBO and start drawing on it
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboId);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    gl.glViewport(0, 0, offScreenWidth, offScreenHeight);

    gl.glPushMatrix();
    {
      //here's where you draw stuff into FBO...
    }
    gl.glPopMatrix();

    // we are finished drawing to our FBO, so unbind it and return to our original viewport, etc
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight); //(int) renderHeight);
  }

  @Override
  public void initializeTexture()
  {
    GL gl = GLU.getCurrentGL();

    if (!generateFBO(gl))
    {
      System.err.println("in TextureFBO : couldn't create FBO");
      System.exit(0);
      return;
    }
  }

  public void dispose()
  {
    System.err.println("disposing of FBO!!!");
    GL gl = GLU.getCurrentGL();

    if (!(this.fboId < 0))
    {
      System.err.println("deleting FBO");
    gl.glDeleteFramebuffersEXT(1, new int[]
      {
        this.fboId
      }, 0);

      this.fboId = -1;
    }
    if (!(this.rboId < 0))
    {
      System.err.println("deleting RBO");
    gl.glDeleteRenderbuffersEXT(1, new int[]
      {
        this.rboId
      }, 0);

    this.rboId = -1;
    }
    System.err.println("disposed of FBO!!!");
  }

  public boolean generateFBO(GL gl)
  {
    System.err.println("(********* in generateFBO...");
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
    gl.glGenRenderbuffersEXT(1, rboBindId, 0);
    this.rboId = rboBindId[0];
    gl.glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, this.rboId);
    gl.glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT, offScreenWidth, offScreenHeight);
    gl.glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, 0);

    // create a framebuffer object and attach the color texture and depth renderbuffer
    int[] fboBindId = new int[1];
    gl.glGenFramebuffersEXT(1, fboBindId, 0);
    this.fboId = fboBindId[0];
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.fboId);

    int textureLevel = 0; //not using mipmaps so only first level is available
    // attach the texture to FBO color attachment point
    gl.glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, this.texture.getTextureObject(), textureLevel);
    // attach the renderbuffer to depth attachment point
    gl.glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, this.rboId);

    // check FBO status
    int status = gl.glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
    if (status != GL_FRAMEBUFFER_COMPLETE_EXT)
    {
      fboUsed = false;
      System.out.println("GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n");
    }
    else
    {
      fboUsed = true;
      System.out.printf("GL_FRAMEBUFFER_COMPLETE_EXT OK, using FBO\n");
      System.out.printf("fbo offScreenWidth =%d\n", offScreenWidth);
      System.out.printf("fbo offScreenHeight =%d\n", offScreenHeight);
      System.out.printf("fbo texture id=%d\n", this.texture.getTextureObject());
      System.out.printf("fbo id=%d\n", fboId);
      System.out.printf("fbo's rbo id=%d\n", rboId);
    }

    // switch back to window-system-provided framebuffer
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

    return fboUsed;
  }
}
