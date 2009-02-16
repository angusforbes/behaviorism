///* GeomVideoJMC.java ~ Dec 14, 2008 */
package geometry.media;

import behaviors.Behavior;
import behaviors.geom.continuous.BehaviorRotate;
import behaviors.geom.continuous.BehaviorScale;
import com.sun.media.jmc.MediaProvider;
import com.sun.media.jmc.control.AudioControl;
import com.sun.media.jmc.control.MediaControl;
import com.sun.media.jmc.control.TrackControl;
import com.sun.media.jmc.control.VideoRenderControl;
import com.sun.media.jmc.event.VideoRendererEvent;
import com.sun.media.jmc.event.VideoRendererListener;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import geometry.BorderEnum;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomCircle;
import geometry.GeomPoint;
import geometry.GeomRect;
import geometry.text.GeomText2;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import renderers.State;
import utils.Utils;

/**
 *
 * @author angus
 */
public class GeomVideoJMC extends GeomRect
{

  TrackControl tc;
  AudioControl ac;
  VideoRenderControl vrc;
  MediaProvider mp = null;
  BufferedImage bufferedImage;
  Graphics2D g2d = null;
  private boolean isTextureWaiting = false;
  public Colorf backgroundColor = null; //background of entire bounds
  public Behavior stopBehavior = null;
  public Behavior startBehavior = null;
  public boolean isPlaying = false;

  //other commands to try...
  //mp.setStartTime(30);
  //mp.getMediaTime() -- gets the current time of playback in sec.ms
  //mp.getDuration() --gets total time of playback in sec.ms

  public GeomVideoJMC(URL url)
  {
    try
    {
      initializeVideo(url.toURI());
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  public GeomVideoJMC(URI uri)
  {
    initializeVideo(uri);
  }

  public GeomVideoJMC(File file)
  {
    initializeVideo(file.toURI());
  }

  public GeomVideoJMC(String filename)
  {
    initializeVideo( (new File(filename)).toURI() );
  }

  public void initializeVideo(URI uri)
  {
    this.mp = new MediaProvider(uri);

     for (MediaControl mc : mp.getControls())
    {
      System.out.println(mc.getName());
    }

    ac = mp.getControl(AudioControl.class);
    vrc = mp.getControl(VideoRenderControl.class);
    final Dimension videoSize = vrc.getFrameSize();
    bufferedImage = new BufferedImage((int)videoSize.getWidth(), (int)videoSize.getHeight(), BufferedImage.TYPE_INT_RGB);
    g2d = bufferedImage.createGraphics();
    System.out.println("videoSize = " + videoSize);
    normalizeSize(videoSize, 1f);


    //mp.setStartTime(10);

     this.mp.play();

 
     //this.mp.pause();

    vrc.addVideoRendererListener(new VideoRendererListener()
    {
      public void videoFrameUpdated(VideoRendererEvent videorendererevent)
      {
        vrc.paintVideoFrame(g2d, new Rectangle(0, 0, (int)videoSize.getWidth(), (int)videoSize.getHeight()));

        //System.out.println("frame: " + videorendererevent.getFrameNumber());

        //note (mipmapping=true) slows this down by quite a bit!
        textureData = TextureIO.newTextureData(bufferedImage, false); //mipmapping=false
        isTextureWaiting = true;

        //System.out.println("current time... " + format(mp.getMediaTime()));
      }
    });

    /*
    this.url = url;
    this.urlname = urlname;
    this.isSelectable = true;
    //this.w = .5f;
    //this.h = .375f;
     */

    this.backgroundColor = new Colorf(1f, 0f, 0f, .5f);
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
  public void draw(GL gl, GLU glu, float offset)
  {
    if (this.textureData == null)
    {
      //not ready yet
    }
    else
    {

      if (this.texture == null)
      {
        this.texture = TextureIO.newTexture(this.textureData);
      }
      else
      {
        if (isTextureWaiting == true)
        {
          this.texture.updateImage(this.textureData);
          isTextureWaiting = false;
        }
      }

      //System.out.println("inset = " + inset);
      //gl.glDisable(gl.GL_BLEND);
      gl.glColor4f(1f, 1f, 1f, 1f);

      this.texture.bind();

      float fliph = this.h;
      //float x = this.anchor.x;
      //float y = this.anchor.y;
      //float z = this.anchor.z;

      if (this.texture.getMustFlipVertically())
      {
        //fliph *= -1.0f;
        // don't seem to need this if explicitly grabbing texture coords...
      }

      gl.glEnable(GL.GL_TEXTURE_2D);

      //gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

      gl.glBegin(gl.GL_POLYGON);
      //gl.glBegin(gl.GL_QUADS);

      TextureCoords tc = texture.getImageTexCoords();
      //System.out.printf("getImageTexCoords %f/%f/%f%f\n", tc.bottom(),
      // 									tc.top(), tc.left(), tc.right());

      gl.glTexCoord2f(tc.left(), tc.bottom());
      gl.glVertex3f(0f, 0f, offset);
      //gl.glVertex3f(inset, inset, offset);
      gl.glTexCoord2f(tc.right(), tc.bottom());
      gl.glVertex3f(w, 0f, offset);
      //gl.glVertex3f(w - (inset * 1f), inset, offset);
      gl.glTexCoord2f(tc.right(), tc.top());
      gl.glVertex3f(w, h, offset);
      //gl.glVertex3f(w - (inset * 1f), h - (inset * 1f), offset);
      gl.glTexCoord2f(tc.left(), tc.top());
      gl.glVertex3f(0f, h, offset);
      //gl.glVertex3f(inset, h - (inset * 1f), offset);

      gl.glEnd();
      gl.glDisable(GL.GL_TEXTURE_2D);

      //gl.glEnable(gl.GL_BLEND);
    }
  }

  @Override
  public void doubleClickAction(MouseEvent me)
  {
    System.out.println("in GeomRectVideoFobs: handleDoubleClick");

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
  

  /** This static method creates a video from the passed-in VideoData
   and attaches it to the passed-in GeomPoint. The GeomPoint's position
   is the center of the video.
   */
  public static Geom makeVideoHolder(Point3f p3f, String movStr, BorderEnum borderType, float insetPerc)
  {
    return makeVideoHolder(p3f, movStr, borderType, insetPerc, new Colorf());
  }
  public static Geom makeVideoHolder(Point3f p3f, String movStr, BorderEnum borderType, float insetPerc, Colorf ccc)
  {
    Geom returnGeom;

    GeomVideoJMC v1 = new GeomVideoJMC(movStr);

    /*
    v1.load();

    //we don't know the dimensions until it is loaded
    while (!v1.isReady && v1.isLoading)
    {
      Utils.sleep(10);
    }
    */
    float t_w, t_h, t_x, t_y;
    GeomText2 gt2;

    float rad = (float)(Math.hypot(v1.w, v1.h) * .5);

    //make borderGeom and attach actual video to it
    Geom gc2;
    float insetw, inseth;
    switch (borderType)
    {
      case RECTANGLE:

        insetw = (v1.w * insetPerc) ;
        inseth = (v1.h * insetPerc) ;
        gc2 = new GeomRect(p3f.x + (-v1.w * .5f) - insetw, p3f.y + (-v1.h * .5f) - inseth, 0f,
                v1.w + (insetw * 2f), v1.h + (inseth * 2f));
        //gc2 = new GeomRect(p3f.x + (v1.w * .5f) - insetw, p3f.y + (-v1.h * .5f) - inseth, 0f,
        //        v1.w + (insetw * 2f), v1.h + (inseth * 2f));
        gc2.setColor(ccc);
        gc2.state = new State();
        gc2.state.DEPTH_TEST = false;
        gc2.state.BLEND = false;


        v1.setPos(insetw, inseth, 0f);

        gc2.addGeom(v1, true);

        gc2.isSelectable = true; //true;


        v1.registerSelectableObject(gc2);
        gc2.registerClickableObject(v1);
        //gc2.registerClickableObject(gc2);

        v1.registerDraggableObject(gc2);
        gc2.registerSelectableObject(v1);




        //add text
        t_w = v1.w;
        t_h = .35f * v1.h * .5f;
        t_x = 0f;
        t_y = 0f;
        //gt2 = new GeomText2(t_x, t_y, 0f, t_w, t_h, "" + vd.title);
        gt2 = new GeomText2(t_x, t_y, 0f, t_w, t_h, movStr);
        gt2.backgroundColor = v1.backgroundColor; //background of entire bounds
        gt2.setColor(1f,1f,1f,1f);
        gt2.registerSelectableObject(gc2);

        v1.addGeom(gt2, true);



        returnGeom = gc2;


        break;

      case CIRCLE:

        gc2 = new GeomCircle(p3f, 0f, rad, 0f, 360f, 64);
        gc2.state = new State();
        //gc2.state.DEPTH_TEST = false;
        //gc2.state.BLEND = false;

        gc2.setColor(0f, 0f, 0f,0f);
        v1.setPos(-v1.w * .5f, -v1.h * .5f, 0f);
        gc2.addGeom(v1, true);


        //add text
        t_w = v1.w ; //- .05f;
        t_h = .20f * v1.h * .5f;
        gt2 = new GeomText2(-v1.w * .5f, -v1.h * .5f , 0f, t_w, t_h, "A MOVIE2");

        gt2.justifyX = 0;
        gt2.justifyY = -1;
        gt2.backgroundColor = new Colorf(.7f, .7f, .7f, 1f);
        gt2.setColor(0f, 0f, 0f, 1f); //background of entire bounds
        gt2.registerSelectableObject(gc2);
        gc2.addGeom(gt2, true);



        GeomCircle borderCircle = new GeomCircle(0f, 0f, 0f, rad - (rad * insetPerc), rad, 0f, 360f, 64);
        gc2.addGeom(borderCircle, true);

        /*
        gt2.backgroundColor = new Colorf(borderCircle.r, borderCircle.g,
                borderCircle.b, borderCircle.a); //background of entire bounds
        */

        gc2.isSelectable = true; //true;
        borderCircle.isSelectable = true; //true;
        v1.isSelectable = true; //true;

        v1.registerSelectableObject(gc2);
        v1.registerDraggableObject(gc2);
        borderCircle.registerSelectableObject(gc2);
        borderCircle.registerDraggableObject(gc2);
        gc2.registerClickableObject(v1);

        returnGeom = gc2;
        break;

      case NONE:
      default:
        insetw = (v1.w * .0f) ;
        inseth = (v1.h * .0f) ;
        gc2 = new GeomRect(-v1.w * .5f - insetw, -v1.h * .5f - inseth, 0f,
                v1.w + (insetw * 2f), v1.h + (inseth * 2f));
        v1.setPos(insetw, inseth, 0f);
        gc2.addGeom(v1, true);
        gc2.isSelectable = true; //true;

        v1.registerSelectableObject(gc2);
        gc2.registerClickableObject(v1);
        v1.registerDraggableObject(gc2);

        //add text
        t_w = v1.w;
        t_h = .35f * v1.h * .5f;
        t_x = 0f;
        t_y = 0f;
        gt2 = new GeomText2(t_x, t_y, 0f, t_w, t_h, "A MOVIE 3");
        //gt2.backgroundColor = v1.backgroundColor; //background of entire bounds
        gt2.registerSelectableObject(gc2); //THIS IS CORRECT-- uncomment...
        v1.addGeom(gt2, true);

        returnGeom = gc2;
        break;
        //Hmm, I should be able to forgo the above and just do the following two lines:
      /*
      v1.isSelectable = true;
      return v1;
       */
    }


    returnGeom.rotateAnchor = new GeomPoint(returnGeom.w/2f, returnGeom.h/2f, 0f);

    return returnGeom;
    /*
    //gc2.registerSelectableObject(grv1);
    //grv1.addGeom(gc2, true);
    gc2.addGeom(grv1, true);
    owner_gt2.addGeom(gc2, true);
    //owner_gt2.addGeom(grv1, true);


      float t_w = grv1.w;
      float t_h = .15f * grv1.h * .5f;
      //float t_h = grv1.h * .15f;
      float t_x = 0f;
      //float t_y = -t_h;
      float t_y = 0f;

      GeomText2 gt2 = new GeomText2(t_x, t_y, .0001f, t_w, t_h, vd.title);
      //gt2.backgroundColor = grv1.backgroundColor; //background of entire bounds

      grv1.addGeom(gt2);

      gt2.isSelectable = false;
      gt2.isActive = true;

      grv1.determineRotateAnchor(RotateEnum.CENTER);
     */
                        /*
       grv1.attachBehavior(new BehaviorScale(Utils.nowPlusMillis(1000L), 5000L,
              LoopEnum.REVERSE,
              3f, 3f, 0f,
              0f));
                         */
       /*
       grv1.attachBehavior(new BehaviorScale(Utils.nowPlusMillis(1000L), 5000L,
              LoopEnum.REVERSE,
              2f, 1f, 0f,
              0f));
        */
    //Utils.sleep(1000);
    //grv1.start();


  }

  public void normalizeSize(Dimension dim, float size) {
        int tw = (int)dim.getWidth();
        int th = (int)dim.getHeight();

        //System.out.println("tw / th = " + tw + "/" + th);
        if (tw >= th) {
            normalizeSizeByWidth(tw, th, size);
        } else {
            normalizeSizeByHeight(tw, th, size);
        }
    }


}
