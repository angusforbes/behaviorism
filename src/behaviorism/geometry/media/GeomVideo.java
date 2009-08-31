///* GeomVideo.java ~ Dec 14, 2008 */
package behaviorism. geometry.media;

import behaviorism.geometry.GeomRect;
import behaviorism.textures.TextureImage;
import behaviorism.textures.TextureVideo;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import javax.vecmath.Point3f;
import static behaviorism.utils.RenderUtils.*;
/**
 *
 * @author angus
 */
//mayeb should extends from a superclass called GeomTexture?
public class GeomVideo extends GeomRect
{

   public float maxSize = -1f;

//  TrackControl tc;
//  AudioControl ac;
//  VideoRenderControl vrc;
//  MediaProvider mp = null;
//  BufferedImage bufferedImage;
//  Graphics2D g2d = null;
//  public Colorf backgroundColor = null; //background of entire bounds
//  public Behavior stopBehavior = null;
//  public Behavior startBehavior = null;
//  public boolean isPlaying = false;
//
//  TextureVideo jmc;

  //other commands to try...
  //mp.setStartTime(30);
  //mp.getMediaTime() -- gets the current time of playback in sec.ms
  //mp.getDuration() --gets total time of playback in sec.ms
  
  public GeomVideo(Point3f p3f, float w, float h, TextureVideo ti)
  {
    super(p3f, w, h);
    setTexture(ti);

   
  }

  public GeomVideo(TextureVideo ti)
  {
    super(new Point3f(), 1f, 1f);
    setTexture(ti);


  }

  //set the w/h using dimensions of texture
  public GeomVideo(Point3f p3f, float maxSize, TextureVideo ti)
  {
    super(p3f, 1f, 1f);
    setTexture(ti);
    this.maxSize = maxSize;
  }


  //assuming there is only one texture attached...
  public void setTexture(TextureImage ti2)
  {
    if (this.textures == null || this.textures.size() == 0)
    {
      attachTexture(ti2);
    }
    else
    {
      TextureImage ti1 = this.textures.get(0);
      attachTexture(ti2);
      detachTexture(ti1);
    }
  }

  public TextureImage getTexture()
  {
    if (this.textures == null)
    {
      return null;
    }
    return this.textures.get(0);
  }

  @Override
  public void draw()
  {
    GL2 gl = getGL();
    TextureImage texImage = getTexture();
    if (texImage == null)
    {
      System.err.println("returning because texImage == null");
      return;
    }

    Texture tex = texImage.texture;
    if (tex == null)
    {
      System.err.println("returning because tex == null");
      return;
    }


    //if (updateTextures())
    {
      if (maxSize > 0f)
      {
        normalizeSize(getTexture().w, getTexture().h, maxSize);
      }

      gl.glColor4fv(color.array(), 0);
      tex.bind();
      TextureCoords tc = this.textures.get(0).texture.getImageTexCoords();

      gl.glEnable(GL_TEXTURE_2D);

      drawRect(gl, 0f, 0f, 0f, w, h, tc.left(), tc.right(), tc.bottom(), tc.top());
//
//      //gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
//      gl.glBegin(gl.GL_QUADS);
//
//      TextureCoords textureCoords = this.textures.get(0).texture.getImageTexCoords();
//
//      float x = translate.x;
//      float y = translate.y;
//
//      gl.glTexCoord2f(textureCoords.left(), textureCoords.bottom());
//      gl.glVertex2f(x, y);
//      gl.glTexCoord2f(textureCoords.right(), textureCoords.bottom());
//      gl.glVertex2f(x + w, y);
//      gl.glTexCoord2f(textureCoords.right(), textureCoords.top());
//      gl.glVertex2f(x + w, y + h);
//      gl.glTexCoord2f(textureCoords.left(), textureCoords.top());
//      gl.glVertex2f(x, y + h);
//
//      gl.glEnd();
      gl.glDisable(GL_TEXTURE_2D);
    }
    
  }

  /*
  @Override
  public void doubleClickAction(MouseEvent me)
  {
    System.out.println("in GeomVideo: handleDoubleClick");
    System.out.println("mp.isPlaying = " + mp.isPlaying());

    if (mp.isPlaying() == true)
    {

      //System.out.println("about to call this.stop!");
      stopBehavior();
      this.mp.pause();

      for (int i = 0; i < 1; i++)
      {
        //this.mp.setMediaTime(this.mp.getMediaTime() - 3);
        //this.ac.setVolume(this.ac.getVolume() - .1f);
        //this.ac.setMute(!this.ac.isMuted());
        }
    }
    else //this.isPlaying == false
    {
      System.out.println("about to call this.start!");
      this.mp.play();
      mp.setRate(1f);

      startBehavior();
    }

  }

  public void stopBehavior()
  {
    System.out.println("stopping......");
    stopBehavior = BehaviorScale.scale(Utils.nowPlusMillis(0L), 150L,
      new Point3f(-2f, -2f, 0f));
    selectableObject.attachBehavior(stopBehavior);
  }

  public void startBehavior()
  {
    //isPlaying = true;

    startBehavior = BehaviorScale.scale(Utils.nowPlusMillis(0L), 150L,
      new Point3f(2f, 2f, 0f));
    selectableObject.attachBehavior(startBehavior);

    startBehavior = BehaviorRotate.rotate(Utils.nowPlusMillis(50L), 350L,
      new Point3f(0f, 720f, 0f));
  //selectableObject.attachBehavior(startBehavior);
  }

  
  public void normalizeSize(Dimension dim, float size)
  {
    int tw = (int) dim.getWidth();
    int th = (int) dim.getHeight();

    //System.out.println("tw / th = " + tw + "/" + th);
    if (tw >= th)
    {
      normalizeSizeByWidth(tw, th, size);
    }
    else
    {
      normalizeSizeByHeight(tw, th, size);
    }
  }
   */
}
