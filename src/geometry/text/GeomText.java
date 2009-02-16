/*
 * GeomText.java
 * Created on April 21, 2007, 9:59 PM
 */

package geometry.text;

import geometry.*;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import handlers.FontHandler;

@Deprecated
public class GeomText extends Geom
{
  String text = "";
  
  int fitChars = 5;
  public int justifyX = 0; //0 center, -1 left, +1 right
  public int justifyY = 0;
  
  //these are the bounds of the actual text, as opposed to it's max width...
  public int numChars = 5;
  public float text_x = -1f;
  public float text_y = -1f;
  public double text_w = -1f;
  public float text_h = -1f;
  
  public GeomText()
  {
    
  }
  
  public GeomText(String text, int fitChars)
  {
    this.text = text;
    this.fitChars = fitChars;
    this.scale.x = 1f; //default, should be decided programatically
    this.scale.y = 1f; //think about how to get the real height in here
    this.numChars = text.length();
  }
  
  public GeomText(float x, float y, float z, float w, String text, int fitChars)
  {
    this.anchor.x = x; this.anchor.y = y; this.anchor.z = z;
    this.scale.x = w;
    this.scale.y = 1f;
    this.text = text;
    this.fitChars = fitChars;
    this.numChars = text.length();
  }

  public void draw(GL gl, GLU glu, float offset)
  {
    TextRenderer textRenderer = getAppropriateFontInfo(gl, glu, this, numChars);
    
    if (textRenderer == null)
    {
      return;
    }

    
    float www = (float) getWidestCharacter(textRenderer);
    
    Rectangle2D strBounds = textRenderer.getFont().getStringBounds(this.text, textRenderer.getFontRenderContext());
    
    float scaleVal = (float) ((this.scale.x / (float) this.numChars) / www);
    
    
    float xpos = this.anchor.x;
    
    if (this.justifyX == 0) //0 is center
    {
      float wholeWidth = (float) www * (float) this.numChars;
      float strW = (float) strBounds.getWidth();
      float centerIncX = (strW * .5f) / (wholeWidth * .5f);
      xpos = (float) ((this.scale.x * -.5f) + this.anchor.x + (.5f * this.scale.x) - (centerIncX * this.scale.x * .5f));
      
      this.text_x = (xpos);
      this.text_w = (centerIncX * this.scale.x);
      this.text_y = this.anchor.y;
      this.text_h = (float) (strBounds.getHeight() ) * scaleVal;
      
      //System.out.println("c.world_e = " + c.text_x);
      
    }
    else if (this.justifyX == 1) //right justify
    {
      float wholeWidth = www * (float) this.numChars;
      float strW = (float) strBounds.getWidth();
      float rightIncX = (strW * .5f) / (wholeWidth * .5f);
      
      //float centerIncX = (strW * .5f) / (wholeWidth * .5f);
      //xpos = c.x + (.5f * c.w) - (rightIncX  * c.w);
      xpos = (float) (this.anchor.x - (rightIncX  * this.scale.x));
      //xpos = c.x - strW;
      
      this.text_x = (xpos);
      this.text_w = (this.scale.x - this.text_x); // - c.text_w?
      this.text_y = this.anchor.y;
      this.text_h = (float) (strBounds.getHeight() ) * scaleVal;
    }
    else //left justify...
    {
      this.text_x = (xpos);
      this.text_w = (this.scale.x - this.text_x); // - c.text_w?
      this.text_y = this.anchor.x;
      this.text_h = (float) (strBounds.getHeight() ) * scaleVal;
    }
    
    //System.out.println("c.text_h = " + c.text_h + ", strBounds = " + strBounds.getHeight() + " scaleVal = " + scaleVal);
    
    textRenderer.begin3DRendering();
    textRenderer.setColor(r, g, b, a);
    textRenderer.draw3D(text, xpos, anchor.y, anchor.z, scaleVal);
    textRenderer.end3DRendering();
 
  
  }

  /*
  public Point3f draw(GL gl, GLU glu, Point3f p)
  {
        return p;
  } 
   */ 
   
  
 public static double getWidestCharacter(TextRenderer tr)
 {
   Font f = tr.getFont();
   FontRenderContext frc = tr.getFontRenderContext();
   Rectangle2D bounds = f.getMaxCharBounds(frc); //ie bounds of largest character-- capital "O" or whatever
   return bounds.getWidth();
 }
  
 public TextRenderer getAppropriateFontInfo(GL gl, GLU glu, GeomText geomText, int numChars)
  {
    int pxWidth = getWidthOfObjectInPixels(gl, glu, geomText);
    
    
    TextRenderer tr = (FontHandler.getInstance().textRenderers.get(0));
    
    //System.out.println("font size = " + Main.fonts.size());
    for (int i = 1; i < FontHandler.getInstance().textRenderers.size(); i++)
    {
      TextRenderer f_one = FontHandler.getInstance().textRenderers.get(i - 1);
      TextRenderer f_two = FontHandler.getInstance().textRenderers.get(i);

      //System.out.println("is " + pxWidth + " > " + f_one.w + " + " + f_two.w + "? " + ((f_one.w + f_two.w) / 2));
      if (pxWidth / numChars > (getWidestCharacter(f_one) + getWidestCharacter(f_two)) / 2.0f)
      {
      }
      else
      {
        return f_one;
        //font = f_one;
        //System.out.println("no... use font size " + f_one.font.getSize());
        //break;
      }
    }
    
    //tr = font.textRenderer;
    
    //System.out.println("PIXEL WIDTH = " + pxWidth + " and FONT WIDTH = " + font.w + " and FONT = " + font.font.getSize2D() );
    
    //couldn't find an appropriate one, so return smallest
    return tr;
  }
  
  //this this is only working for TEXT right now... look into...
  public int getWidthOfObjectInPixels(GL gl, GLU glu, Geom g)
  {
    double modelview[] = new double[16];;
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double windowCoords[] = new double[3];
    
    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    
    glu.gluProject(g.anchor.x, g.anchor.y, g.anchor.z,
            modelview, 0,
            projection, 0,
            viewport, 0,
            windowCoords, 0);
    
    double x1 = windowCoords[0];
    
    glu.gluProject(g.anchor.x + g.scale.x, g.anchor.y, g.anchor.z,
            modelview, 0,
            projection, 0,
            viewport, 0,
            windowCoords, 0);
    double x2 = windowCoords[0];
    
    return (int) (x2 - x1);
    
    
    
  }
 
  
  public void setText(String text)
  {
    this.text = text;
  }
  
  public String toString()
  {
    return "GeomText [" + text + "] : x/y/z/w = " + anchor.x + "/" + anchor.y + "/" + anchor.z + "/" + scale.x + ", justify = " + justifyX;
    
  }
}
