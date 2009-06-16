/* TextureFBO.java ~ Jun 4, 2009 */
package fbos;

import behaviorism.BehaviorismDriver;
import textures.*;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import shaders.Program;
import static javax.media.opengl.GL.*;

/**
 *
 * @author angus
 */
public class FboPingPong
{

  public int fboWidth = -1;
  public int fboHeight = -1;
  public int fboId = -1;
  public int rboId = -1;
  int readTextureId;
  int writeTextureId;
  int writeAttachment; // = GL_COLOR_ATTACHMENT0_EXT;
  int readAttachment; // = GL_COLOR_ATTACHMENT1_EXT;
  public Texture texture1;
  public Texture texture2;
  public Texture inputTexture;
  public Texture outputTexture; //pointer to output texture
  List<Program> programs = null;

  public FboPingPong()
  {

  }

  public FboPingPong(int fboWidth, int fboHeight)
  {
    this.fboWidth = fboWidth;
    this.fboHeight = fboHeight;
  }

  public FboPingPong(TextureImage inputTexture, List<Program> programs)
  {
    this.inputTexture = inputTexture.texture;
    this.programs = programs;
  }

  public FboPingPong(Texture inputTexture, List<Program> programs)
  {
    this.inputTexture = inputTexture;
    this.programs = programs;
  }

  public FboPingPong(Texture inputTexture, int fboWidth, int fboHeight, List<Program> programs)
  {
    this.fboWidth = fboWidth;
    this.fboHeight = fboHeight;
    this.inputTexture = inputTexture;
    this.programs = programs;
  }

  public boolean bind(GL gl)
  {
    //if first time, generate FBO
    if (fboId < 0)
    {
      System.out.println("GENERATE...");
      if (!generateFBO(gl))
      {
        System.err.println("couldn't generate FBO!");
        return false;
      }
    }

    writeAttachment = GL_COLOR_ATTACHMENT0_EXT;
    readAttachment = GL_COLOR_ATTACHMENT1_EXT;

    readTextureId = texture2.getTextureObject();
    writeTextureId = texture1.getTextureObject();

    //bind fbo
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboId);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    gl.glViewport(0, 0, fboWidth, fboHeight);

   return true;
  }

  public Texture process(GL gl, Texture input)
  {
    //use first program to process inputTexture into texture2
    drawTextureToOffScreenTexture(input.getTextureObject(),
      writeAttachment);

    swapPingPong(); //make the write texture our new *read* texture

    if (readTextureId == texture1.getTextureObject())
    {
      return texture1;
    }
    else
    {
      return texture2;
    }
  }

  public void unbind(GL gl)
  {
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);
  }

  /**
   * Puts the input texture through the Program chain, returning the processed output Texture
   * which can then be placed on a Geom, etc.
   * @param gl
   * @return A Texture that's been processed by the attached shader Programs
   */
  public Texture apply(GL gl)
  {
    //sanity check
    if (programs.size() == 0)
    {
      return inputTexture;
    }

    //if first time, generate FBO
    if (fboId < 0)
    {
      System.out.println("GENERATE...");
      if (!generateFBO(gl))
      {
        System.err.println("couldn't generate FBO!");
        return null;
      }
    }

    //if first time, install shaders
    for (Program program : this.programs)
    {
      if (program.programId <= 0)
      {
        program.install(gl);
      }
    }

    writeAttachment = GL_COLOR_ATTACHMENT0_EXT;
    readAttachment = GL_COLOR_ATTACHMENT1_EXT;

    readTextureId = texture2.getTextureObject();
    writeTextureId = texture1.getTextureObject();
    //readTexture = texture2;
    //writeTexture = texture1;


    //bind fbo
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboId);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    gl.glViewport(0, 0, fboWidth, fboHeight);

    //use first program to process inputTexture into texture2
    drawTextureToOffScreenTextureUsingShader(inputTexture.getTextureObject(),
      writeAttachment, programs.get(0));

    swapPingPong(); //make the write texture our new *read* texture

    //loop through the rest of our shaders
    for (int i = 1; i < programs.size(); i++)
    {
      drawTextureToOffScreenTextureUsingShader(readTextureId, writeAttachment, programs.get(i));
      swapPingPong();
    }

    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight);

    if (readTextureId == texture1.getTextureObject())
    {
      return texture1;
    }
    else
    {
      return texture2;
    }
  }

  public void swapPingPong()
  {
    int tmp = readAttachment;
    readAttachment = writeAttachment;
    writeAttachment = tmp;

    int tmpTex = readTextureId;
    readTextureId = writeTextureId;
    writeTextureId = tmpTex;
  }


  public void drawTextureToOffScreenTexture(int texId, int attachment)
  //public void drawTextureToOffScreenTextureUsingShader(Texture tex, int attachment, Program program)
  {
    GL gl = GLU.getCurrentGL();

    //program.bind(gl);

    gl.glBindTexture(GL_TEXTURE_2D, texId);
    //gl.glBindTexture(GL_TEXTURE_2D, tex.getTextureObject());
    gl.glDrawBuffer(attachment);
    gl.glEnable(GL_TEXTURE_2D);
    //gl.glActiveTexture(GL_TEXTURE0);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    gl.glViewport(0, 0, fboWidth, fboHeight);


    //gl.glUniform1i(program.uniform("theTexture"), 0);
    //set projection to ortho
    gl.glMatrixMode(gl.GL_PROJECTION);

    gl.glPushMatrix();
    {
      gl.glLoadIdentity();
      BehaviorismDriver.renderer.glu.gluOrtho2D(0, fboWidth, fboHeight, 0);

      gl.glMatrixMode(gl.GL_MODELVIEW);

      gl.glPushMatrix();
      {
        gl.glLoadIdentity();

        gl.glColor4f(1f, 1f, 1f, 1f);
        drawSquare(gl, 0, 0, fboWidth, fboHeight);
      }
      gl.glPopMatrix();

      gl.glMatrixMode(gl.GL_PROJECTION);
    }
    gl.glPopMatrix();

    gl.glMatrixMode(gl.GL_MODELVIEW);

    gl.glDisable(GL_TEXTURE_2D);
    //program.unbind(gl);
  }

  public void drawTextureToOffScreenTextureUsingShader(int texId, int attachment, Program program)
  //public void drawTextureToOffScreenTextureUsingShader(Texture tex, int attachment, Program program)
  {
    GL gl = GLU.getCurrentGL();

    program.bind(gl);

    gl.glBindTexture(GL_TEXTURE_2D, texId);
    //gl.glBindTexture(GL_TEXTURE_2D, tex.getTextureObject());
    gl.glDrawBuffer(attachment);
    gl.glEnable(GL_TEXTURE_2D);
    //gl.glActiveTexture(GL_TEXTURE0);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    gl.glViewport(0, 0, fboWidth, fboHeight);


    gl.glUniform1i(program.uniform("theTexture"), 0);
    //set projection to ortho
    gl.glMatrixMode(gl.GL_PROJECTION);

    gl.glPushMatrix();
    {
      gl.glLoadIdentity();
      BehaviorismDriver.renderer.glu.gluOrtho2D(0, fboWidth, fboHeight, 0);

      gl.glMatrixMode(gl.GL_MODELVIEW);

      gl.glPushMatrix();
      {
        gl.glLoadIdentity();

        gl.glColor4f(1f, 1f, 1f, 1f);
        drawSquare(gl, 0, 0, fboWidth, fboHeight);
      }
      gl.glPopMatrix();

      gl.glMatrixMode(gl.GL_PROJECTION);
    }
    gl.glPopMatrix();

    gl.glMatrixMode(gl.GL_MODELVIEW);

    gl.glDisable(GL_TEXTURE_2D);
    program.unbind(gl);
  }

  public void drawSquare(GL gl, float x, float y, float w, float h)
  {
    gl.glBegin(GL_QUADS);
    {
      gl.glTexCoord2f(0, 0);
      gl.glVertex2f(x, y);
      gl.glTexCoord2f(1, 0);
      gl.glVertex2f(x + w, y);
      gl.glTexCoord2f(1, 1);
      gl.glVertex2f(x + w, y + h);
      gl.glTexCoord2f(0, 1);
      gl.glVertex2f(x, y + h);
    }
    gl.glEnd();
  }

  /*
  public FboPingPong(int fboWidth, int fboHeight)
  {
  this.fboWidth = fboWidth;
  this.fboHeight = fboHeight;
  }
   */
  /**
   * This is just a template,
   * obviously you need to override this and make it actually draw something into the FBO!
   */
  public void applyImage()
  {
    /*
    GL gl = GLU.getCurrentGL();

    //0. bind our FBO and start drawing on it
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboId);
    gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    gl.glViewport(0, 0, fboWidth, fboHeight);

    gl.glPushMatrix();
    {
    //here's where you draw stuff into FBO...
    }
    gl.glPopMatrix();

    // we are finished drawing to our FBO, so unbind it and return to our original viewport, etc
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    gl.glViewport(0, 0, BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight); //(int) renderHeight);
     */
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
    if (fboWidth < 0 || fboHeight < 0)
    {
      fboWidth = inputTexture.getWidth();
      fboHeight = inputTexture.getHeight();
    }

    System.err.println("(********* in generateFBO...");
    boolean fboUsed;

    this.texture1 = TextureIO.newTexture(GL_TEXTURE_2D);
    this.texture1.bind();

    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    //gl.glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE); // automatic mipmap
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboWidth, fboHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
    //gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, fboWidth, fboHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
    gl.glBindTexture(GL_TEXTURE_2D, 0);

    this.texture2 = TextureIO.newTexture(GL_TEXTURE_2D);
    this.texture2.bind();

    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    //gl.glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE); // automatic mipmap
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    //gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, fboWidth, fboHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
    //gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, fboWidth, fboHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
    gl.glBindTexture(GL_TEXTURE_2D, 0);

    // create a renderbuffer object to store depth info
//    int[] rboBindId = new int[1];
//    gl.glGenRenderbuffersEXT(1, rboBindId, 0);
//    this.rboId = rboBindId[0];
//    gl.glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, this.rboId);
//    gl.glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT, fboWidth, fboHeight);
//    gl.glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, 0);

    // create a framebuffer object and attach the color texture and depth renderbuffer
    int[] fboBindId = new int[1];
    gl.glGenFramebuffersEXT(1, fboBindId, 0);
    this.fboId = fboBindId[0];

    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.fboId);

    int textureLevel = 0; //not using mipmaps so only first level is available
    // attach the texture to FBO color attachment point
    gl.glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, this.texture1.getTextureObject(), textureLevel);
    // attach the renderbuffer to depth attachment point
    //gl.glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, this.rboId);

    // attach the texture to FBO color attachment point
    gl.glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT1_EXT, GL_TEXTURE_2D, this.texture2.getTextureObject(), textureLevel);
    // attach the renderbuffer to depth attachment point
    //gl.glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, this.rboId);

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
      System.out.printf("fbo offScreenWidth =%d\n", fboWidth);
      System.out.printf("fbo offScreenHeight =%d\n", fboHeight);
      System.out.printf("fbo texture1 id=%d\n", this.texture1.getTextureObject());
      System.out.printf("fbo texture2 id=%d\n", this.texture2.getTextureObject());
      System.out.printf("fbo id=%d\n", fboId);
      System.out.printf("fbo's rbo id=%d\n", rboId);
    }

    // switch back to window-system-provided framebuffer
    gl.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

    return fboUsed;
  }
}
