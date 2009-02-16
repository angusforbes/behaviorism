/*
 * GeomVideo.java
 * Created on July 26, 2007, 2:37 PM
 */
package geometry.media;

import geometry.*;
import com.omnividea.FobsConfiguration;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.Point;
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
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.util.BufferToImage;

/* MUST hava jmf.properties, jmf.jar, fobs4jmf.dll, fobs4jmf.jar in classpath/library path!!! */
public class GeomRectVideo extends GeomRect implements Renderer, ControllerListener
{
	//Geom variables

	private Processor p = null;
	private int[] waitSync = new int[0];
	private boolean stateTransOK = true;
	private String urlname = "";
	private URL url = null;
	private float volume = .5f;
	public GainControl gainControl;

	//Renderer variables

	public BufferedImage bi = null;
	//private RGBFormat videoFormat;

	private VideoFormat videoFormat;
	private BufferedImage lastImage = null;
	boolean isReady = false;
	boolean isPlaying = false;

  
	public GeomRectVideo(String urlname)
	{
		this.urlname = urlname;
	}

	public GeomRectVideo(URL url)
	{
		this.url = url;
	}

	public void draw(GL gl, GLU glu)
	{
		if (this.textureData == null)
		{
		//not ready yet
		} else
		{
			if (this.texture == null)
			{
				this.texture = TextureIO.newTexture(this.textureData);
			} else
			{
				this.texture.updateImage(this.textureData);
			}

			this.texture.bind();
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glBegin(gl.GL_POLYGON);

			//temp
			//float tew = w;
			///w = h;
			//h = tew;
			
			float fliph = this.h;
			//float w = this.w;
			float x = this.anchor.x;
			float y = this.anchor.y;
			float z = this.anchor.z;

			if (this.texture.getMustFlipVertically())
			{
				fliph *= -1.0f;
			}

          
    gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(0f, 0f, 0f);
    gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(w, 0f, 0f);
    gl.glTexCoord2f(1.0f, 1.0f);
      gl.glVertex3f(w, h, 0f);
    gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(0f, h, 0f);

			/*
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(x + -w, y + -fliph, z);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(x + w, y + -fliph, z);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(x + w, y + fliph, z);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(x + -w, y + fliph, 0.0f);
			*/
       gl.glEnd();
      
			gl.glDisable(GL.GL_TEXTURE_2D);
		}
	}

	/*
  public Point3f draw(GL gl, GLU glu, Point3f p)
  {
        return p;
  }  
  */

	/*
  public void drawGeomRectVideo(GL gl, GLU glu, GeomRectVideo g)
  {
    if (this.textureData == null)
    {
      //not ready yet
    }
    else
    {
      if (g.texture == null)
      {
        System.out.println("new texture...");
        g.texture = TextureIO.newTexture(g.textureData);
      }
      else
      {
        g.texture.updateImage(g.textureData);
      }

      g.texture.bind();
      gl.glEnable(GL.GL_TEXTURE_2D);
      gl.glBegin(gl.GL_POLYGON);

      float h = g.h;
      float w = g.w;
      float x = g.anchor.x;
      float y = g.anchor.y;
      float z = g.anchor.z;
      if (g.texture.getMustFlipVertically())
      {
        h *= -1.0f;
      }

      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x + -w, y + -h, z);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x + w, y + -h, z);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x + w, y + h, z);
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x + -w, y + h, 0.0f);
      gl.glEnd();
      gl.glDisable(GL.GL_TEXTURE_2D);
    }
  }
  */
	public void load()
	{
		System.out.println("loading ml");
		MediaLocator ml = new MediaLocator(this.urlname);
		//MediaLocator ml = new MediaLocator(this.url);
		System.out.println("done loading ml");
		//open(ml);
		isReady = open(ml);
	}

	public boolean open(MediaLocator ml)
	{
		System.out.println("in open " + ml);

		try
		{
			System.out.println("creating p");
			p = Manager.createProcessor(ml);
			System.out.println("created p");
		} catch (Exception ex)
		{
			System.out.println("failed to create a processor for movie " + ml);
			ex.printStackTrace();
			return false;
		}

		p.addControllerListener(this);
		System.out.println("opened " + ml);

		p.configure();

		if (!waitForState(p.Configured))
		{
			System.out.println("Failed to configure the processor");
			return false;
		}

		// use processor as a player
		p.setContentDescriptor(null);

		// obtain the track control
		TrackControl[] tc = p.getTrackControls();

		if (tc == null)
		{
			System.out.println("Failed to get the track control from processor");
			return false;
		}

		TrackControl videoTrackControl = null;
		//how to control audio???

		for (int i = 0; i < tc.length; i++)
		{
			//System.out.println("tc format = " + tc[i].getFormat());

			if (tc[i].getFormat() instanceof VideoFormat)
			{
				videoTrackControl = tc[i];
				break;
			}
		}

		if (videoTrackControl == null)
		{
			System.out.println("can't find video track");
			return false;
		}

		try
		{
			videoTrackControl.setRenderer(this);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println("the processor does not support effect");
			return false;
		}

		//p.start(); //starts playback...
    //System.out.println("p start");

		// prefetch
		p.prefetch();

		if (!waitForState(p.Prefetched))
		{
			System.out.println("Failed to prefech the processor");
			return false;
		}

		Object[] cs = videoTrackControl.getControls();
		for (int j = 0; j < cs.length; j++)
		{
			System.out.println("cs[" + j + "] : " + (Control) cs[j]);
		}

		this.gainControl = p.getGainControl();
		//System.out.println("end of prefetch");

		//gainControl.setLevel(.01f);
    //between -80f and 6f it seems don't know why'
		setVolume(1f);
		System.out.println("gc = " + this.gainControl.getLevel() + "," + this.gainControl.getDB());

		
		return true;
	}

  public boolean play()
  {
    //System.out.println("HERE??????????????/");
    if (isReady == true)
    {
      //p.start(); //starts playback...
      System.out.println("yo yo yo");
      System.out.println("p start");
      isPlaying = true;
      return true;
    }
    else
    {
      return false;
    }

    
  }
  
	public boolean waitForState(int state)
	{
		synchronized (waitSync)
		{
			try
			{
				while (p.getState() != state && stateTransOK)
				{
					//System.out.println("waiting... state = " + p.getState() + " == " + state + "?");
					waitSync.wait();
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
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
		} else
		{
			if (evt instanceof ResourceUnavailableEvent)
			{
				synchronized (waitSync)
				{
					stateTransOK = false;
					waitSync.notifyAll();
				}
			} else
			{
				if (evt instanceof EndOfMediaEvent)
				{
					p.setMediaTime(new Time(0));

					setVolume(getVolume() - .5f);

					p.start();

					//p.stop();
       //p.close();
       //p.deallocate();
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
		} else
		{
			if (val < 0.001f)
			{
				volume = 0f;
				this.gainControl.setLevel(0f);
			} else
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
		System.out.println("Fobs TextureRenderer: setInputFormat");
		FobsConfiguration.videoFrameFormat = FobsConfiguration.RGBA;

		//videoFormat = (RGBFormat) format;
		videoFormat = (VideoFormat) format;

		int formatWidth = (int) videoFormat.getSize().getWidth();
		int formatHeight = (int) videoFormat.getSize().getHeight();

		System.out.printf("format w/h = %d/%d\n", formatWidth, formatHeight);
		//normalizeSizeByWidth(formatWidth, formatHeight, 1f);
		normalizeSizeByWidth(formatWidth, formatHeight, 1f);
		System.out.printf("now this w/h = %f/%f\n", this.w, this.h);

		btoi = new BufferToImage(videoFormat);

		return format;
	}

	public void start()
	{
	//System.out.println("Fobs Java2DRenderer: start");
	}

	public void stop()
	{
	//System.out.println("Fobs Java2DRenderer: stop");
	}

	public int process(Buffer buffer)
	{

		//bi = Utils.toRGBBufferedImage(btoi.createImage(buffer));


		bi = bufferToImage(buffer);
		System.out.printf("bi w/h = %d/%d\n", bi.getWidth(null), bi.getHeight(null));
		if (bi != null)
		{
			//note (mipmapping=true) slows this down by quite a bit!
			textureData = TextureIO.newTextureData(bi, false); //mipmapping=false
		}

		return Renderer.BUFFER_PROCESSED_OK;
	}
	DirectColorModel dcm;
	Object data;
	int rMask, gMask, bMask, aMask;
	RGBFormat format;
	//VideoFormat format;

	int[] masks = new int[3];
	DataBuffer db;
	SampleModel sm;
	WritableRaster wr;
	BufferToImage btoi;

	private BufferedImage bufferToImage(Buffer buffer)
	{
		//System.out.println("buffer foramt = " + buffer.getFormat());
		format = (RGBFormat) buffer.getFormat();
		//format = (VideoFormat)videoFormat;
		data = buffer.getData();

		rMask = format.getRedMask();
		gMask = format.getGreenMask();
		bMask = format.getBlueMask();
		//aMask = format.getBlueMask();
		masks[0] = rMask;
		masks[1] = gMask;
		masks[2] = bMask;
		//masks[3] = aMask;

		db = new DataBufferInt((int[]) data,
													 //format.getLineStride() *
													 format.getSize().height *
													 format.getSize().width);

		sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT,
																					format.getSize().width,
																					//format.getLineStride(),
																					format.getSize().height,
																					masks);
		wr = Raster.createWritableRaster(sm, db, new Point(0, 0));

		dcm = new DirectColorModel(24, rMask, gMask, bMask);
		return new BufferedImage(dcm, wr, true, null);
		
	}

	public String getName()
	{
		return "Fobs Java2DRenderer";
	}

	public void open() throws ResourceUnavailableException
	{
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

	public Object getControl(String arg)
	{
		return null;
	/*
    Object rc = null;
    if(arg.equals("javax.media.control.FrameGrabbingControl")) rc = this;
    return rc;
    */
	}
}  
  

