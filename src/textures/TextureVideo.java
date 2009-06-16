/* TextureVideo.java ~ May 17, 2009 */
package textures;

import com.sun.media.MediaPlayer;
import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.AudioControl;
import com.sun.media.jmc.control.MediaControl;
import com.sun.media.jmc.control.TrackControl;
import com.sun.media.jmc.control.VideoRenderControl;
import com.sun.media.jmc.event.VideoRendererEvent;
import com.sun.media.jmc.event.VideoRendererListener;
import com.sun.opengl.util.texture.TextureIO;
import geometry.Colorf;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URI;

/**
 * Transfers a video frame rendered via JMC to a TextureData.
 * Contains methods for controlling the video in various ways (stop, start, setRate, setVolume, etc).
 * Default behaviors can be overwritten for custom behavior (see paintVideoOnImage()).
 * @author angus
 */
public class TextureVideo extends TextureImage
{

  TrackControl tc;
  AudioControl ac;
  public VideoRenderControl vrc;
  public MediaProvider mp = null;
  BufferedImage bufferedImage;
  public Graphics2D g2d = null;
  public Colorf backgroundColor = null; //background of entire bounds
  public boolean isPlaying = false;
  //public TextureData textureData = null;
  public Dimension videoSize;
  //int w = 0;
  //int h = 0;

  /**
   * Use FileUtils.toURI to pass in Files or URLs.
   * @param uri The URI of the Video
   */
  public TextureVideo(URI uri)
  {
    System.out.println("in TextureVideo constructor");
    initializeVideo(uri);
  }

  public void initializeVideo(URI uri)
  {
    this.mp = new MediaProvider(uri);

    for (MediaControl mc : mp.getControls())
    {
      System.out.println(mc.getName());
    }

    mp.setRate(4f);
    ac = mp.getControl(AudioControl.class);
    vrc = mp.getControl(VideoRenderControl.class);
//    videoSize = vrc.getFrameSize();
//    bufferedImage = new BufferedImage((int) videoSize.getWidth(), (int) videoSize.getHeight(), BufferedImage.TYPE_INT_RGB);
//    g2d = bufferedImage.createGraphics();
    //System.out.println("videoSize = " + videoSize);
    //normalizeSize(videoSize, 1f);

    //mp.setStartTime(10);

    this.mp.play();
    //this.mp.pause();

    vrc.addVideoRendererListener(new VideoRendererListener()
    {

      public void videoFrameUpdated(VideoRendererEvent videorendererevent)
      {
        if (bufferedImage == null || videoSize == null || w <= 0 || h <= 0)
        {
          videoSize = vrc.getFrameSize();
          bufferedImage = new BufferedImage((int) videoSize.getWidth(), (int) videoSize.getHeight(), BufferedImage.TYPE_INT_RGB);
          w = (int) videoSize.getWidth();
          h = (int) videoSize.getHeight();


          return;
        }

        g2d = bufferedImage.createGraphics();
        vrc.paintVideoFrame(g2d, new Rectangle(0, 0, w, h));
        g2d.dispose();

        paintVideoOnImage();

        //System.out.println("frame: " + videorendererevent.getFrameNumber());

        //note (mipmapping=true) slows this down by quite a bit!
        textureData = TextureIO.newTextureData(bufferedImage, false); //mipmapping=false
        isTextureWaiting = true;

      //System.out.println("current time... " + format(mp.getMediaTime()));
      }
    });

  //this.backgroundColor = new Colorf(1f, 0f, 0f, .5f);
  }

  /**
   * Override paintVideoOnImage for non-standard effects. For example to tile videos, or to just display part of the frame.
   */
  public void paintVideoOnImage()
  {
    vrc.paintVideoFrame(g2d, new Rectangle(0, 0, (int) videoSize.getWidth(), (int) videoSize.getHeight()));
  }

  public Dimension getVideoSize()
  {
    return vrc.getFrameSize();
  }

  private String format(int val, int places)
  {
    String result = "" + val;
    while (result.length() < places)
    {
      result = "0" + result;
    }
    return result;
  }

  private String format(double val)
  {
    int minutes = (int) (val / 60);
    int seconds = (int) val % 60;
    int milli = (int) (val * 1000) % 1000;

    return format(minutes, 2) + ":" + format(seconds, 2) + "." + format(milli, 3);
  }
}

