///* GeomVideo.java ~ Dec 14, 2008 */
package geometry.media;

import textures.TextureVideo;
import com.sun.opengl.util.texture.TextureCoords;
import geometry.GeomRect;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class GeomVideo extends GeomRect
{

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
  
  public GeomVideo(TextureVideo ti, Point3f p3f, float w, float h)
  {
    super(p3f, w, h);
    attachTexture(ti);
  }

  public GeomVideo(TextureVideo ti)
  {
    super(new Point3f(), 1f, 1f);
    attachTexture(ti);
  }

  @Override
  public void draw(GL gl)
  {
    if (!updateTextures())
    {
      gl.glColor4f(r, g, b, a);
      this.textures.get(0).texture.bind();
      //this.texture.bind();

      gl.glEnable(GL.GL_TEXTURE_2D);
     
      //gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
      gl.glBegin(gl.GL_QUADS);

      TextureCoords textureCoords = this.textures.get(0).texture.getImageTexCoords();

      float x = translate.x;
      float y = translate.y;

      gl.glTexCoord2f(textureCoords.left(), textureCoords.bottom());
      gl.glVertex2f(x, y);
      gl.glTexCoord2f(textureCoords.right(), textureCoords.bottom());
      gl.glVertex2f(x + w, y);
      gl.glTexCoord2f(textureCoords.right(), textureCoords.top());
      gl.glVertex2f(x + w, y + h);
      gl.glTexCoord2f(textureCoords.left(), textureCoords.top());
      gl.glVertex2f(x, y + h);

      gl.glEnd();
      gl.glDisable(GL.GL_TEXTURE_2D);
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
