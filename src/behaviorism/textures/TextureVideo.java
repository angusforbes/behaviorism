/* TextureVideo.java ~ May 17, 2009 */
package behaviorism.textures;

import behaviorism.geometry.Colorf;
import behaviorism.utils.Utils;
import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.AudioControl;
import com.sun.media.jmc.control.PlayControl;
import com.sun.media.jmc.control.TrackControl;
import com.sun.media.jmc.control.VideoRenderControl;
import com.sun.media.jmc.event.BufferDownloadListener;
import com.sun.media.jmc.event.BufferDownloadedProgressChangedEvent;
import com.sun.media.jmc.event.VideoRendererEvent;
import com.sun.media.jmc.event.VideoRendererListener;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URI;
import org.grlea.log.SimpleLogger;

/**
 * Transfers a video frame rendered via JMC to a TextureData.
 * Contains methods for controlling the video in various ways (stop, start, setRate, setVolume, etc).
 * Default behaviors can be overwritten for custom behavior (see paintVideoOnImage()).
 * @author angus
 */
public class TextureVideo extends TextureImage
  implements VideoRendererListener, BufferDownloadListener
{

  public boolean isTextureWaiting = false;

  TrackControl tc;
  AudioControl ac;
  public VideoRenderControl vrc;
  public MediaProvider mp = null;
  BufferedImage bufferedImage;
  public Graphics2D g2d = null;
  public Colorf backgroundColor = null; //background of entire bounds
  public Dimension videoSize;
  //int w = 0;
  //int h = 0;
  public double rate = 1f; //have to store rate because MediaProvider is not storing it properly
  public boolean isBouncing = false;
  public int numBounces = PlayControl.REPEAT_FOREVER;
  public int bounces = 1;
  public double bounceStart;
  public double bounceStop;
  public boolean forward = true;
  public boolean stopVideoAfterBouncing = true;
  public double progress = 0f;
  public int imageType = BufferedImage.TYPE_INT_RGB;

  public static final SimpleLogger log = new SimpleLogger(TextureVideo.class);

  /**
   * Use FileUtils.toURI to pass in Files or URLs.
   * @param uri The URI of the Video
   */
  public TextureVideo(URI uri)
  {
    initializeVideo(uri);
  }

  public TextureVideo(URI uri, boolean autoStart)
  {
    initializeVideo(uri);

    if (autoStart == true)
    {
      play();
      mp.setPlayCount(PlayControl.REPEAT_FOREVER);
    }
  }

  public void initializeVideo(URI uri)
  {
    this.mp = new MediaProvider();
    setVideo(uri);

    ac = mp.getControl(AudioControl.class);
    vrc = mp.getControl(VideoRenderControl.class);

    vrc.addVideoRendererListener(this);
    mp.addBufferDownloadListener(this);
  }

  public void mediaDownloadProgressChanged(BufferDownloadedProgressChangedEvent bde)
  {
    //System.out.println("dbe progess = " + bde.getProgress());
    this.progress = bde.getProgress();
  }

  public void videoFrameUpdated(VideoRendererEvent rendererEvent)
  {
    if (!isPlaying() || !isReady())
    {
      return;
    }

//    System.out.println("playbackPercentage = " + getPlaybackPercentage());
//    System.out.println("now/duration = " + getCurrentTime() +"/"+ getDuration());

    if (isBouncing == true)
    {
      handleBouncingBehavior();
    }

    paintBufferedImage();

//    if (textureData != null)
//    {
//      textureData.flush();
//    }

    textureData = TextureIO.newTextureData(bufferedImage, false); //mipmapping=false
    isTextureWaiting = true;

    //bufferedImage.flush();
  }

  public void paintBufferedImage()
  {
    if (bufferedImage == null || videoSize == null || w <= 0 || h <= 0)
    //if ( videoSize == null || w <= 0 || h <= 0)
    {
      videoSize = vrc.getFrameSize();
      w = (int) videoSize.getWidth();
      h = (int) videoSize.getHeight();

      bufferedImage = new BufferedImage(
        (int) videoSize.getWidth(),
        (int) videoSize.getHeight(),
        imageType);
      return;
    }

    g2d = bufferedImage.createGraphics();
    paintVideoOnImage();
    g2d.dispose();
  }

  public void handleBouncingBehavior()
  {
    if (this.isBouncing == true && forward == true && getCurrentTime() >= bounceStop)
    {
      rate *= -1;
      setRate(rate);
      forward = false;
      bounces++;
    }
    else if (this.isBouncing == true && forward == false && getCurrentTime() <= bounceStart)
    {
      rate *= -1;
      setRate(rate);
      forward = true;
      bounces++;
    }

    if (numBounces != PlayControl.REPEAT_FOREVER && bounces > numBounces)
    {
      this.isBouncing = false;

      if (stopVideoAfterBouncing == true)
      {
        stop();
      }
    }
  }

  /**
   * Sets the imageType of the bufferedImage that the frame is painted on.
   * @param imageType
   */
  public void setImageType(int imageType)
  {
    if (this.imageType != imageType)
    {
      this.imageType = imageType;
      this.bufferedImage = null; //signals that the image needs to be recreated
    }
  }

  public int getImageType()
  {
    return this.imageType;
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

  /**
   * Gets a nicely formatted version of the current playback position of the video.
   * @return The playback position.
   */
  public String getTimeString()
  {
    return format(getCurrentTime());
  }

  /**
   * Gets a nicely formatted version of the duration of the video.
   * @return The duration of the video.
   */
  public String getDurationString()
  {
    return format(getDuration());
  }

  public String getProgressString()
  {
    return format(getProgress());
  }

  /**
   * Gets info about the loop settings.
   * @return The loop settings.
   */
  public String getLoopString()
  {
    if (isBouncing == true)
    {
      return "bounce " + bounces + " of " + numBounces + ", " +
        "bouncing between " + bounceStart + " and " + bounceStop;
    }
    else
    {
      return "loop " + currentLoop() + " of " + totalLoops() + ", " +
        "looping between " + getStartTime() + " and " + getStopTime();
    }
  }

  public double getStartTime()
  {
    return mp.getStartTime();
  }

  public void setStartTime(double start)
  {
    mp.setStartTime(start);
  }

  public double getStopTime()
  {
    return mp.getStopTime();
  }

  public void setStopTime(double stop)
  {
    mp.setStopTime(stop);
  }

  /**
   * Gets the duration of the video.
   * @return The duration of the video.
   */
  public double getDuration()
  {
    waitUntilReady();
    return this.mp.getDuration();
  }

  public double getProgress()
  {
    return progress;
  }

  /**
   * Increase or decrease the rate of video playback by a specified amount.
   * @param amt Amount by which to change the rate.
   */
  public void changeRate(double amt)
  {
    this.rate = rate + amt;
    waitUntilReady();
    mp.setRate(rate);
  }

  /**
   * Sets the rate of video playback
   * @param rate The rate to set the playback to.
   */
  public void setRate(double rate)
  {
    this.rate = rate; //need to store the rate here because MediaProvider's getRate() is broken!
    waitUntilReady();
    mp.setRate(rate);
  }

  /**
   * Gets the current rate of video playback.
   * @return The current rate.
   */
  public double getRate() //this doesn't seem to return the correct value! *always* 1.0
  {
    return this.rate;
    //return mp.getRate(); //MediaProvider's getRate seems to be broken (in javafx1.2)
  }

  /**
   * Pauses the video playback.
   */
  public void pause()
  {
    mp.pause();
  }

  /**
   * Begins video playback.
   */
  public void play()
  {
    loop(1);
  }

  public void play(double start, double stop)
  {
    loop(1, start, stop);
  }

  /**
   * Begins video playback, looping forever
   */
  public void loop()
  {
    loop(PlayControl.REPEAT_FOREVER);
  }

  public void loop(int numRepeats)
  {
    mp.play();
    mp.setPlayCount(numRepeats);
  }

  public void loop(double start, double stop)
  {
    loop(PlayControl.REPEAT_FOREVER, start, stop);
  }

  public void loop(int numRepeats, double start, double stop)
  {
    mp.setStartTime(start);
    mp.setStopTime(stop);
    loop(numRepeats);
  }

  /**
   * Begins video playback, playing forwards and then backwards forever if backward playback is supported.
   */
  public void bounce()
  {
    bounce(PlayControl.REPEAT_FOREVER);
  }

  public void bounce(int numBounces)
  {
    this.isBouncing = true;
    this.numBounces = numBounces;

    mp.play();
    waitUntilReady();

    this.bounceStart = .5;
    setCurrentTime(bounceStart);
    this.bounceStop = (getDuration() - .5);

  }

  public void bounce(double bounceStart, double bounceStop)
  {
    bounce(PlayControl.REPEAT_FOREVER, bounceStart, bounceStop);
  }

  public void bounce(int numBounces, double bounceStart, double bounceStop)
  {
    this.isBouncing = true;
    this.numBounces = numBounces;
    this.bounceStart = bounceStart;
    this.bounceStop = bounceStop;

    waitUntilReady();

    //mp.play();
    setCurrentTime(bounceStart);
  }

  public void waitUntilReady()
  {
    while (!isReady())
    {
      Utils.sleep(10);
    }
  }

  public boolean isReady()
  {
    if (mp.getDuration() == PlayControl.TIME_ETERNITY ||
      mp.getDuration() == PlayControl.TIME_UNKNOWN)
    {
      return false;
    }

    if (progress < getCurrentTime())
    {
      return false;
    }

    return true;
  }

  /**
   * Determine if the video is currently playing.
   * @return A boolean indicating whether or not the video is currently playing.
   */
  public boolean isPlaying()
  {
    return mp.isPlaying();
  }

  /**
   * Increase or decrease the volume by a specified amount.
   * @param amt Amount by which to change the volume.
   */
  public void changeVolume(float amt)
  {
    setVolume(getVolume() + amt);
  }

  /**
   * Sets the volume to the specified volume.
   * @param vol
   */
  public void setVolume(float vol)
  {
    waitUntilReady();
    this.ac.setVolume(vol);
  }

  /**
   * Gets the current volume.
   * @return The current volume.
   */
  public float getVolume()
  {
    waitUntilReady();
    return this.ac.getVolume();
  }

  /**
   * Mutes the video.
   */
  public void setMute(boolean mute)
  {
    if (isMuted() != mute)
    {
      this.ac.setMute(mute);
    }
  }

  /**
   * Determine if the video is muted or not.
   * @return A boolean indicating if the video is muted.
   */
  public boolean isMuted()
  {
    waitUntilReady();
    return this.ac.isMuted();
  }

  /**
   * Make the video mute if it is not, or unmutes it if it is.
   */
  public void toggleMute()
  {
    this.ac.setMute(!this.ac.isMuted());
  }

  public void changeBalance(float amt)
  {
    setBalance(getBalance() + amt);
  }

  public void setBalance(float balance)
  {
    this.ac.setBalance(balance);
  }

  public float getBalance()
  {
    return this.ac.getBalance();
  }

  //this doesn't seem to do anything...
  public void setFader(float fader)
  {
    this.ac.setFader(fader);
  }

  //this returns the value set by setFader, but doesn't appear to do anything...
  public float getFader()
  {
    return this.ac.getFader();
  }

  /**
   * Gets the current video playback position.
   * @return The current time.
   */
  public double getCurrentTime()
  {
    return this.mp.getMediaTime();
  }

  /**
   * Sets the current video playback position.
   * @param time
   */
  public void setCurrentTime(double time)
  {
    waitUntilReady();
    if (time < progress - .01)
    {
      this.mp.play();
      this.mp.setMediaTime(time);
    }
  }

  /**
   * Gets the position of the video playback as a percentage between the
   * start time and the end time of the video.
   * @return A number between 0.0 and 1.0, where 0.0 is the start of the video and 1.0 is the end of the video.
   */
  public double getPlaybackPercentage()
  {
    return getCurrentTime() / getDuration();
  }

  /**
   * Sets the position of the video playback using a percentage between the
   * start time and the end time of the video.
   * @param perc A number between 0.0 and 1.0, where 0.0 is the start of the video and 1.0 is the end of the video.
   */
  public void setPlaybackPercentage(double perc)
  {
    setCurrentTime(getDuration() * perc);
  }

  /**
   * Determines whether or not the video is looping.
   * @return A boolean indicating whether or not the video is looping.
   */
  public boolean isLooping()
  {
    if (totalLoops() > 1)
    {
      return true;
    }
    return false;
  }

  /**
   * Gets the total number of loops that has been set.
   * @return The total number of loops set.
   */
  public int totalLoops()
  {
    return mp.getPlayCount();
  }

  /**
   * Gets the current loop iteration.
   * @return The current loop number.
   */
  public int currentLoop()
  {
    return mp.getCurrentPlayCount();
  }

  /**
   * Stops and rewinds the movie to the beginning.
   */
  public void stop() //Stop the movie, and rewind.
  {
    this.pause();
    this.setCurrentTime(0);
  }

  @Override
  public boolean updateTexture()
  {
    log.entry("in updateTexture()");
    if (isDone() == true)
    {
      log.debug("textureImage isDone = true, needs to be disposed of...");
      disposeTexture();
      log.exit("out updateTexture()");
      return false;
    }

    if (textureData == null) //no data loaded
    {
      log.exit("in updateTexture() : GL textureData has not been loaded yet!");
      return false;
    }

    if (texture == null) //texture needs to be intialized
    {
      log.debug("GL texture = null, needs to be initialized");
      initializeTexture();
    }

    if (isTextureWaiting == true) //texture needs to be updated
    {
      log.debug("texture is waiting");
      copyDataToTexture();
    }

    log.exit("out updateTexture()");
    return true;
  }

  protected void copyDataToTexture()
  {
    texture.updateImage(textureData);
    isTextureWaiting = false;
  }

  /**
   * Dispose of native resources.
   */
  @Override
  protected void disposeTexture()
  {
    log.entry("in disposeTexture()");

    log.info("disposing of JMC video " + this);

    pause();

    if (vrc != null)
    {
      vrc.removeVideoRendererListener(this);
    }

    if (mp != null)
    {
      mp.removeBufferDownloadListener(this);
      this.progress = 0.0;
      mp.setSource(null); //this will call mp.close()
    }

    if (bufferedImage != null)
    {
      bufferedImage.flush();
      bufferedImage = null;
    }

    super.disposeTexture();
    log.exit("out disposeTexture()");

  }

  public void setVideo(URI uri)
  {
    mp.setSource(uri);
  }
}

