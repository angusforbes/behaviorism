/*
 * Viz.java
 * Created on January 27, 2007, 3:19 PM
 */
package renderers;

import renderers.RendererJogl;
import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;


//public abstract class Viz
public class Viz
{
  //protected static java.util.List<Complex> data = Collections.synchronizedList(new ArrayList<Complex>());
  RendererJogl rj;
  
  public boolean dataReady = false;
  public boolean drawDebugCorners = true;
  public boolean drawDebugLines = false;
  public boolean centerCoords = false; //this is for centering photos aound the center of the photo, rather than using x, y
  
 
  long lastTime = 0L;
  long nowTime = 0L;
  
  int frames = 0;

  public void draw(GL gl, GLU glu)
  {
    /*
    frames++;
    nowTime = (System.nanoTime() / 1000000); //System.currentTimeMillis();
    //System.out.println("time between frames = " + (nowTime - lastTime));
    if (nowTime > lastTime + 1000)
    {
      //System.out.println("" + frames + " perSec");
      lastTime = nowTime;
      frames = 0;
    }
    
    
    
    //System.out.println("world coords.size = " + world.coords.size());
    
    //update behaviors
    synchronized(Main.world.coords)
    {
      for (int i = 0; i < Main.world.coords.size(); i++)
      {
        Coord c = Main.world.coords.get(i);
        
        for (int ii = c.behaviors.size() - 1; ii >= 0; ii--)
        {
          Behavior b = (Behavior) c.behaviors.get(ii);
          //b.change(c); //behaviors now only work for geoms
          
          if (b.isDone == true)
          {
            if (c instanceof CoordRect)
            {  
              System.out.println("removing behavor <"+b.getClass()+"> from coord of type " + c.getClass());
              System.out.println("behavors size was " + c.behaviors.size()); 
            }
            c.behaviors.remove(b);
            if (c instanceof CoordRect)
            {
              System.out.println("behavors size now " + c.behaviors.size()); 
            }
           
          }
        }
      }
      
      gl.glLoadIdentity();
      gl.glTranslatef(Main.xzoom,Main.yzoom,Main.zzoom);
      
      
      List<Coord> scheduledForRemovalCoords = new ArrayList<Coord>();
      
      //draw coordinates based on updates made by behaviors.
      for (int i = 0; i < Main.world.coords.size(); i++)
      {
        Coord c = Main.world.coords.get(i);
        
        //System.out.println("here... w/h = " + c.w + "/" + c.h);
        
        drawCoord(gl, glu, c);
        
        if (c.isDone == true)
        {
          scheduledForRemovalCoords.add(c);
        }
      }
      
      
      for (Coord c : scheduledForRemovalCoords)
      {
        Main.world.coords.remove(c);
      }
     }
    
      
    //JUST TO TEST THAT *something* is HAPPENING!
    //System.out.println("x/y/z zoom = " + Main.xzoom + "/" + Main.yzoom + "/" + Main.zzoom);
    //gl.glColor4ub( (byte)255, (byte)0, (byte)0,(byte)128);
    //gl.glRectf(-1f,-1f,1f,1f);
    //drawRectangle(gl, Main.xzoom, Main.yzoom, 1f, 1f);

    //gl.glBindTexture( GL_TEXTURE_2D, g_textureID );

    gl.glTexEnvf( GL.GL_POINT_SPRITE, GL.GL_COORD_REPLACE, GL.GL_TRUE );

    gl.glPointSize(10f);
    gl.glEnable( GL.GL_POINT_SPRITE );

	gl.glBegin( GL.GL_POINTS );
    {
        for( int i = 0; i < 5; ++i )
        {
            //gl.glColor4f( Utils.randomFloat(0f, 1f), Utils.random255(), Utils.random255(), 1f);
            gl.glColor4f( Utils.randomFloat(0f, 1f), Utils.randomFloat(0f, 1f), Utils.randomFloat(0f, 1f), 1f);

	    gl.glVertex3f(Utils.randomFloat(-1f,1f), Utils.randomFloat(-1f,1f), 0f );
        
        }
    }
	gl.glEnd();

	//gl.glDisable( GL.GL_POINT_SPRITE_ARB );
	gl.glDisable( GL.GL_POINT_SPRITE );

  */
  }
   
  public void drawCoord(GL gl, GLU glu/*, Coord c*/)
  {
    /*
    setWorldCoords(gl);
    
    if (!c.isActive)
    {
      return;
    }
    
    if (c instanceof CoordJava2D)
    {
      drawJava2DCoord(gl, (CoordJava2D)c);
    }
    else if (c instanceof CoordText)
    {
      drawCoordText(gl, glu, (CoordText) c);
    }
    else if (c instanceof CoordPhoto)
    {
      drawCoordPhoto(gl, glu, (CoordPhoto) c);
    }
    else if (c instanceof CoordLine)
    {
      drawCoordLine(gl, glu, (CoordLine) c);
    }
    else if (c instanceof CoordRect)
    {
      drawCoordRect(gl, glu, (CoordRect) c);
    }
    */
  }
  
  public void setWorldCoords(GL gl)
  {
    /*
    gl.glLoadIdentity();
    gl.glTranslatef(Main.xzoom,Main.yzoom,Main.zzoom);
     */
  }
  
  public void drawCoordLine(GL gl, GLU glu /*, CoordLine c*/)
  {
    /*
    setWorldCoords(gl);
    
    c.updateLine();
    
    //System.out.println("line : " + c);
    gl.glEnable(GL.GL_LINE_SMOOTH);
    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glLineWidth(.5f);
    
    gl.glColor4f(c.r, c.g, c.b, c.a);
    
    gl.glBegin(GL.GL_LINES);
    
    if (c.c1 instanceof CoordText)
    {
      //gl.glVertex3f (((CoordText)c.c1).text_x, c.y1, c.z1);
      gl.glVertex3f(c.x1, c.y1, c.z1);
    }
    else
    {
      gl.glVertex3f(c.x1, c.y1, c.z1);
    }
    
    if (c.c2 instanceof CoordText)
    {
      //gl.glVertex3f (((CoordText)c.c2).text_x, c.y2, c.z2);
      gl.glVertex3f(c.x2, c.y2, c.z2);
    }
    else
    {
      gl.glVertex3f(c.x2, c.y2, c.z2);
    }
    
    gl.glEnd();
    */
  }
    /*

  public void drawCoordText(GL gl, GLU glu, CoordText c)
  {
    FontInfo fontInfo = getAppropriateFontInfo(gl, glu, c, c.numChars);
    TextRenderer textRenderer = fontInfo.textRenderer;
    
    Rectangle2D strBounds = fontInfo.font.getStringBounds(c.text, fontInfo.frc);
    
    float scaleVal = (c.w / (float) c.numChars) / fontInfo.w;
    
    
    float xpos = c.x;
    
    if (c.justify == 0) //0 is center
    {
      float wholeWidth = (float) fontInfo.w * (float) c.numChars;
      float strW = (float) strBounds.getWidth();
      float centerIncX = (strW * .5f) / (wholeWidth * .5f);
      xpos = (c.w * -.5f) + c.x + (.5f * c.w) - (centerIncX * c.w * .5f);
      
      c.text_x = (xpos);
      c.text_w = (centerIncX * c.w);
      c.text_y = c.y;
      c.text_h = (float) (strBounds.getHeight() ) * scaleVal;
      
      //System.out.println("c.world_e = " + c.text_x);
      
    }
    else if (c.justify == 1) //right justify
    {
      float wholeWidth = (float) fontInfo.w * (float) c.numChars;
      float strW = (float) strBounds.getWidth();
      float rightIncX = (strW * .5f) / (wholeWidth * .5f);
      
      //float centerIncX = (strW * .5f) / (wholeWidth * .5f);
      //xpos = c.x + (.5f * c.w) - (rightIncX  * c.w);
      xpos = c.x - (rightIncX  * c.w);
      //xpos = c.x - strW;
      
      c.text_x = (xpos);
      c.text_w = (c.w - c.text_x); // - c.text_w?
      c.text_y = c.y;
      c.text_h = (float) (strBounds.getHeight() ) * scaleVal;
    }
    else //left justify...
    {
      c.text_x = (xpos);
      c.text_w = (c.w - c.text_x); // - c.text_w?
      c.text_y = c.y;
      c.text_h = (float) (strBounds.getHeight() ) * scaleVal;
    }
    
    //System.out.println("c.text_h = " + c.text_h + ", strBounds = " + strBounds.getHeight() + " scaleVal = " + scaleVal);
    
    textRenderer.begin3DRendering();
    //System.out.println("scaleVal = " + scaleVal + ", fontInfo.w " + fontInfo.w );
    textRenderer.setColor(c.r, c.g, c.b, c.a);
    textRenderer.draw3D(c.text, xpos, c.y, c.z, scaleVal);
    textRenderer.end3DRendering();
    
  }
  */
  
   
  public void drawRectangle(GL gl, float x, float y, float w, float h)
  {
    /*
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(x + -w, y + -h, 0.0f);
    gl.glVertex3f(x + w, y + -h, 0.0f);
    gl.glVertex3f(x + w, y + h, 0.0f);
    gl.glVertex3f(x + -w, y + h, 0.0f);
    gl.glEnd();
    */
  }
  
  
  public void drawTexturedRectangle(GL gl, Texture t, float x, float y, float w, float h)
  {
    /*
    gl.glBegin(gl.GL_POLYGON);
    
    if (t.getMustFlipVertically())
    {
      h *= -1.0f;
    }
    
    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x + -w, y + -h, 0.0f);
    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x + w, y + -h, 0.0f);
    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x + w, y + h, 0.0f);
    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x + -w, y + h, 0.0f);
    gl.glEnd();
  */
    }

  public void drawJava2DCoord(GL gl /*, CoordJava2D c*/)
  {
    /*
    if (c.img != null)
    {
      int inset = 2;
      int w = c.img.getWidth(null);
      int h = c.img.getHeight(null);
      
      if (w < 0 || h < 0)
      {
        return; //not loaded yet
      }
      
      TextureRenderer tr = new TextureRenderer(w, h, false);
      Graphics2D g = tr.createGraphics();
      
      //g.setColor(Color.GRAY);
      //g.fillRect(0,0,w,h);
      //g.drawImage(ImageFilterHandler.filterScale(img, scale, scale), 20, 20, dsw.bufferedImage.getWidth(null) - 40, dsw.bufferedImage.getHeight(null) - 40, null);
      //g.drawImage(c.img, inset, inset, w - inset*2, h - inset*2, null);
      g.drawImage(c.img, 0, 0, w, h, null);
      g.dispose();
      //tr.sync(0, 0, w, h);
      
      //tr.beginOrthoRendering(w, h );
      //tr.drawOrthoRect(0,0, 0,0, w,h);
      //tr.endOrthoRendering();
      
      tr.begin3DRendering();
      tr.draw3DRect(c.x, c.y, c.z, 0, 0, w, h, .05f);
      tr.end3DRendering();
    }
     */
  }
  
 
 
 
  
  
  
}



