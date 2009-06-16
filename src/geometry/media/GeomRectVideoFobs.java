/*
 * GeomVideo.java
 * Created on July 26, 2007, 2:37 PM
 */
package geometry.media;

import geometry.*;
import geometry.text.GeomText2;
import behaviors.Behavior;
import renderers.State;
import behaviors.geom.continuous.BehaviorRotate;
import behaviors.geom.continuous.BehaviorScale;
import com.omnividea.FobsConfiguration;
//import net.sf.fmj.media.renderer.video.*;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import data.VideoData;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.net.URL;
import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.Control;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.GainControl;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.Renderer;
import javax.media.ResourceUnavailableEvent;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.util.BufferToImage;
import javax.vecmath.Point3f;
import utils.Utils;

public class GeomRectVideoFobs extends GeomRect implements Renderer, ControllerListener
{
   Texture texture;
  TextureData textureData;

  //Geom variables
  public Colorf backgroundColor = null; //background of entire bounds
  //public float insetPercentage = .15f;
  //public float insetPercentage = 0f;
  public String urlname = null;
  public URL url = null;
  public boolean isReady = false;
  public boolean isPlaying = false;
  public boolean isLoading = false;
  public Behavior stopBehavior = null;
  public Behavior startBehavior = null;
  
  //audio variables
  private float volume = .5f;
  public GainControl gainControl;
  
  //Renderer variables
  private Processor processor = null;
  //private Player player = null;
  private int[] waitSync = new int[0];
  private boolean stateTransOK = true;
  public BufferedImage bi = null;
  private RGBFormat videoFormat;
  private BufferedImage lastImage = null;
  private boolean isTextureWaiting = false;
  
  
  //should load in with VideoData-- and in the load method also set up title bar, controls, etc
  public GeomRectVideoFobs(String urlname)
  {
    URL u = null;
    initialize(null, urlname);
  }
  
  public GeomRectVideoFobs(URL url)
  {
    String s = null;
    initialize(url, s);
  }
  
  public void initialize(URL url, String urlname)
  {
    this.url = url;
    this.urlname = urlname;
    this.isSelectable = true;
    //this.w = .5f;
    //this.h = .375f;
    this.backgroundColor = new Colorf(1f, 0f, 0f, .5f);
  }
  
  public void draw(GL gl, GLU glu, float offset)
  {
    
    //if (1 == 1) return;
    /*
    float inset = 0f;
    if (this.w < this.h)
    {
      inset = insetPercentage * this.w * .5f;
    }
    else
    {
      inset = insetPercentage * this.h * .5f;
    }
     */
    //  gl.glDisable(gl.GL_BLEND);
    
    /*
    gl.glColor4fv(backgroundColor.array(), 0);
     
     
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(0f, 0f, offset);
    gl.glVertex3f(w - inset, 0f, offset);
    gl.glVertex3f(w - inset, inset, offset);
    gl.glVertex3f(0f, inset, offset);
    gl.glEnd();
     
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(w - inset, 0f, offset);
    gl.glVertex3f(w, 0f, offset);
    gl.glVertex3f(w, h - inset, offset);
    gl.glVertex3f(w - inset, h - inset, offset);
    gl.glEnd();
     
     
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(inset, h-inset, offset);
    gl.glVertex3f(w, h-inset, offset);
    gl.glVertex3f(w, h, offset);
    gl.glVertex3f(inset, h, offset);
    gl.glEnd();
     
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(0, inset, offset);
    gl.glVertex3f(inset, inset, offset);
    gl.glVertex3f(inset, h, offset);
    gl.glVertex3f(0, h, offset);
    gl.glEnd();
     
     */
    
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
        if(isTextureWaiting == true)
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
  
  public void load()
  {
    isLoading = true;
    final GeomRectVideoFobs me = this;
    Thread t = new Thread()
    {
      public void run()
      {
        
        System.out.println("loading ml");
        MediaLocator ml = null;
        if (me.urlname != null)
        {
          System.out.println("using String");
          ml = new MediaLocator(me.urlname);
        }
        else if (me.url != null)
        {
          System.out.println("using URL");
          ml = new MediaLocator(me.url);
        }
        System.out.println("done loading ml");
        
        System.out.println("in open " + ml);
        
        try
        {
		System.out.println("BBB");
		Utils.sleep(1000);
					
          System.out.println("creating proccesor!!!");
					
          processor = Manager.createProcessor(ml);

					Utils.sleep(1000);
          //processor = Manager.createPlayer(ml);
          System.out.println("created p");
        }
        catch (Exception ex)
        {
          System.out.println("failed to create a processor for movie " + ml);
          ex.printStackTrace();
          isReady = false;
          //return false;
        }
        
        processor.addControllerListener(me);
        System.out.println("opened " + ml);
        
        processor.configure();
        
        
        System.out.println("a");
        if (!waitForState(processor.Configured))
        {
          System.out.println("Failed to configure the processor");
          //return false;
          isReady = false;
        }
        
        System.out.println("b");
        // use processor as a player
        processor.setContentDescriptor(null);
        
        System.out.println("c");
        // obtain the track control
        TrackControl[] tc = processor.getTrackControls();
        
        if (tc == null)
        {
          System.out.println("Failed to get the track control from processor");
          //return false;
          isReady = false;
        }
        
        TrackControl videoTrackControl = null;
        TrackControl audioTrackControl = null;
        //how to control audio???
        
        for (int i = 0; i < tc.length; i++)
        {
          System.out.println("tc format = " + tc[i].getFormat());
          
          if (tc[i].getFormat() instanceof VideoFormat)
          {
            videoTrackControl = tc[i];
            //break;
          }
          if (tc[i].getFormat() instanceof AudioFormat)
          {
            audioTrackControl = tc[i];
            //break;
          }
        }
        
        if (videoTrackControl == null)
        {
          System.out.println("can't find video track");
          //return false;
          isReady = false;
        }
        
        try
        {
          //videoTrackControl.setRenderer(this);
          videoTrackControl.setRenderer(me);
          
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          System.out.println("the processor does not support effect");
          //return false;
          isReady = false;
        }
        
        // prefetch
        processor.prefetch();
        
        if (!waitForState(processor.Prefetched))
        {
          System.out.println("Failed to prefech the processor");
          isReady = false;
          //return false;
        }
        
        Object[] cs = videoTrackControl.getControls();
        System.out.println("track control length = " + cs.length);
        for (int j = 0; j < cs.length; j++)
        {
          System.out.println("cs[" + j + "] : " + (Control) cs[j]);
        }
        
        //this.gainControl = processor.getGainControl();
        me.gainControl = processor.getGainControl();
        //System.out.println("end of prefetch");
        
        //gainControl.setLevel(.01f);
        //between -80f and 6f it seems don't know why'
        setVolume(1f);
        //System.out.println("gc = " + this.gainControl.getLevel() + "," + this.gainControl.getDB());
        System.out.println("gc = " + me.gainControl.getLevel() + "," + me.gainControl.getDB());
        
        //System.out.println("processor start");
        //processor.start();
        isReady = true;
        isLoading = false;
        
      }
    };
    
    t.setPriority(Thread.MIN_PRIORITY);
    t.start();
  }
  
  public boolean waitForState(int state)
  {
    synchronized (waitSync)
    {
      try
      {
        while (processor.getState() != state && stateTransOK)
        {
          System.out.println("waiting... state = " + processor.getState() + " == " + state + "?");
          waitSync.wait();
        }
      }
      catch (Exception ex)
      {
        System.out.println("problem...");
        ex.printStackTrace();
        System.out.println("that was the problem...");
      }
    }
    return stateTransOK;
    
  }
  
  public void controllerUpdate(ControllerEvent evt)
  {
    if (evt instanceof ConfigureCompleteEvent ||
            evt instanceof RealizeCompleteEvent ||
            evt instanceof PrefetchCompleteEvent)
    {
      synchronized (waitSync)
      {
        stateTransOK = true;
        waitSync.notifyAll();
      }
    }
    else
    {
      if (evt instanceof ResourceUnavailableEvent)
      {
        synchronized (waitSync)
        {
          stateTransOK = false;
          waitSync.notifyAll();
        }
      }
      else
      {
        if (evt instanceof EndOfMediaEvent)
        {
          processor.setMediaTime(new Time(0));
          
          setVolume(getVolume() - .5f);
          
          //processor.start();
          
          //processor.stop();
          //processor.close();
          //processor.deallocate();
          //textureData = null;
          //gainControl.setLevel(gainControl.getLevel() - .2f);
          //gainControl.setDB(gainControl.getDB() - 12f); //gainControl.getLevel() + .1f);
          System.out.println("gc = " + this.gainControl.getLevel() + "," + this.gainControl.getDB());
          
        }
      }
    }
  }
  
  public float getVolume()
  {
    return volume;
  }
  
  public void setVolume(float val)
  {
    //between level 0f and .8f
    //between db -80 (silence) and 6 (loud)
    if (val > 1f)
    {
      volume = 1f;
      this.gainControl.setLevel(1f);
    }
    else
    {
      if (val < 0.001f)
      {
        volume = 0f;
        this.gainControl.setLevel(0f);
      }
      else
      {
        volume = val;
        this.gainControl.setLevel(.8f * volume);
      }
    }
    //System.out.println("volume = " + volume);
    
  }
  
  public void mute(boolean mute)
  {
    this.gainControl.setMute(mute);
  }
  
  /* part 2 */
  void setValue(Object aValue, boolean isSelected)
  {
    //System.out.println(aValue.getClass().getName());
  }
  
  public Format[] getSupportedInputFormats()
  {
    return new Format[]{new RGBFormat()};
  }
  
  public Format setInputFormat(Format format)
  {
    System.out.println("Fobs TextureRenderer: setInputFormat " + format);
    FobsConfiguration.videoFrameFormat = FobsConfiguration.RGBA;
    
    videoFormat = (RGBFormat) format;
    
    int formatWidth = (int) videoFormat.getSize().getWidth();
    int formatHeight = (int) videoFormat.getSize().getHeight();
    
    //normalizeSizeByWidth(formatWidth, formatHeight, 1f);
    //normalizeSizeByWidth(formatWidth, formatHeight, .5f);
    normalizeSizeByHeight(formatWidth, formatHeight, .5f);
    
    determineScaleAnchor(ScaleEnum.CENTER);
    btoi = new BufferToImage(videoFormat);
    
    return format;
  }
  
  public void start()
  {
    //System.out.println("Fobs Java2DRenderer: start");
    if (isReady == true)
    {
      //processor.setRate(2.0f);//doesn't work!!!
      
      isPlaying = true;
      processor.start(); //starts playback...
    }
    else
    {
      System.out.println("is NOT READY!!!");
    }
    
  }
  
  public void stop()
  {
    Thread t = new Thread()
    {
      public void run()
      {
        isPlaying = false;
        processor.stop();
      }
    };
    
    t.setPriority(Thread.MIN_PRIORITY);
    t.start();
  }
  
  public int process(Buffer buffer)
  {
    //System.out.println("processing... " + Main.viz.currentNano);
    
    //to do-- test which is faster, or if it makes a difference
    //bi = Utils.toRGBBufferedImage(btoi.createImage(buffer));
    bi = bufferToImage(buffer);
    
    
    if (bi !=  null)
    {
      //note (mipmapping=true) slows this down by quite a bit!
      textureData = TextureIO.newTextureData(bi, false); //mipmapping=false
      isTextureWaiting = true;
    }
    
    return Renderer.BUFFER_PROCESSED_OK;
  }
  
  DirectColorModel dcm;
  Object data;
  RGBFormat format;
  int[] masks = new int[3];
  DataBuffer db;
  SampleModel sm;
  WritableRaster wr;
  BufferToImage btoi;
  
  private BufferedImage bufferToImage(Buffer buffer)
  {
    format = (RGBFormat) buffer.getFormat();
    data = buffer.getData();
    
    masks[0] = format.getRedMask();
    masks[1] = format.getGreenMask();
    masks[2] = format.getBlueMask();
    
    int wsize = format.getLineStride();
    int hsize = format.getSize().height;
    //int wsize = 128;
    //int hsize = 128;
    
    db = new DataBufferInt((int[]) data,
            wsize *
            hsize);
    
    sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT,
            wsize,
            hsize,
            masks);
    
    wr = Raster.createWritableRaster(sm, db, new Point(0, 0));
    
    dcm = new DirectColorModel(24, masks[0], masks[1], masks[2]);
    return new BufferedImage(dcm, wr, true, null);
  }
  
  public String getName()
  {
    return "Fobs Java2DRenderer";
  }
  
  public void open() throws ResourceUnavailableException
  {
    System.out.println("in open()");
  }
  
  public void close()
  {
    lastImage = null;
  }
  
  public void reset()
  {
    lastImage = null;
  }
  
  public Object[] getControls()
  {
    return null;
  /*
    Object[] obj = { this };
    return obj;
   */
  }
  
  @Override
  public Object getControl(String arg)
  {
    return null;
  /*
    Object rc = null;
    if(arg.equals("javax.media.control.FrameGrabbingControl")) rc = this;
    return rc;
   */
  }
  
  @Override
  public void doubleClickAction(MouseEvent me)
  {
    System.out.println("in GeomRectVideoFobs: handleDoubleClick");
    
      
      
      if (this.isPlaying == true)
      {
        System.out.println("about to call this.stop!");
        this.stop();
        stopBehavior();
      }
      else //this.isPlaying == false
      {
        System.out.println("about to call this.start!");
        this.start();
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
    isPlaying = true;
    
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
  public static Geom makeVideoHolder(Point3f p3f, VideoData vd, BorderEnum borderType, float insetPerc)
  {
    return makeVideoHolder(p3f, vd, borderType, insetPerc, new Colorf());
  }
  public static Geom makeVideoHolder(Point3f p3f, VideoData vd, BorderEnum borderType, float insetPerc, Colorf ccc)
  {
    Geom returnGeom;
    
    GeomRectVideoFobs v1 = new GeomRectVideoFobs(vd.url);
    
    v1.load();
    
    //we don't know the dimensions until it is loaded
    while (!v1.isReady && v1.isLoading)
    {
      Utils.sleep(10);
    }
    
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
        System.out.println("title = " + vd.title);
        gt2 = new GeomText2(t_x, t_y, 0f, t_w, t_h, "" + vd.title);
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
        gt2 = new GeomText2(-v1.w * .5f, -v1.h * .5f , 0f, t_w, t_h, vd.title);
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
        gt2 = new GeomText2(t_x, t_y, 0f, t_w, t_h, vd.title);
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
  /*public static GeomGraphNode makeVideoHolderOnGraphNode(Point3f p3f, VideoData vd, BorderEnum borderType, float insetPerc, int level, GeomSpringSystem spr)
  {
    GeomRectVideoFobs v1 = new GeomRectVideoFobs(vd.url);
    v1.load();
    
    //we don't know the dimensions until it is loaded
    while (!v1.isReady && v1.isLoading)
    {
      Utils.sleep(10);
    }
    
    
    float t_w, t_h, t_x, t_y;
    GeomText2 gt2;
    
    float rad = (float)(Math.hypot(v1.w, v1.h) * .5);
    
    //make borderGeom and attach actual video to it
    GeomGraphNode gc2;
    float insetw, inseth;
        insetw = (v1.w * insetPerc) ;
        inseth = (v1.h * insetPerc) ;
        gc2 = new GeomGraphNode(level, p3f.x + (-v1.w * .5f) - insetw, p3f.y + (-v1.h * .5f) - inseth, 0f,
                v1.w + (insetw * 2f), v1.h + (inseth * 2f), spr);
        gc2.state = new State();
        gc2.state.DEPTH_TEST = false;
        gc2.state.BLEND = false;
      
        
        v1.setPos(insetw, inseth, 0f);
        gc2.addGeom(v1, true);
        gc2.isSelectable = true; //true;
        
        v1.registerSelectableObject(gc2);
        //gc2.registerClickableObject(v1);
        gc2.registerClickableObject(gc2);
        v1.registerDraggableObject(gc2);
        gc2.registerSelectableObject(v1);
        
        //add text
        t_w = v1.w;
        t_h = .35f * v1.h * .5f;
        t_x = 0f;
        t_y = 0f;
        gt2 = new GeomText2(t_x, t_y, 0f, t_w, t_h, vd.title);
        gt2.backgroundColor = v1.backgroundColor; //background of entire bounds
        gt2.registerSelectableObject(gc2);
        v1.addGeom(gt2, true);
        
        return gc2;
    }*/
}


