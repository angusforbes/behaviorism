/*
 * GeomTag.java
 *
 * Created on July 9, 2007, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package geometry.text;

import geometry.*;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import handlers.FontHandler;

/** get rid of me! */
@Deprecated
public class GeomTag extends GeomText2
{
  public int count;

  public static GeomTag newGeomTextConstrainedByHeight(String text,
																												 float h)
	{
    return null;
  }

  public GeomTag(String txt, int cnt)
  {
    this.text = txt;
    this.count = cnt;
  }

  public GeomTag deepCopy()
  {
    GeomTag tt = new GeomTag(this.text, this.count);
    tt.w = this.w;
    tt.anchor = this.anchor;
    tt.h = this.h;
    return tt;
  }

  public void setPos(float x, float y, float z, float w)
  {
    this.anchor.x = x;
    this.anchor.y = y;
    this.anchor.z = z;
    //this.scaleFctr.x = w;

    this.w = w;
    TextRenderer fi = FontHandler.getInstance().textRenderers.get(FontHandler.getInstance().textRenderers.size() - 1);
    FontRenderContext frc = fi.getFontRenderContext();
    Font font = fi.getFont();

    Rectangle2D bounds = font.getStringBounds(this.text, frc);
    LineMetrics lm = font.getLineMetrics(this.text, frc);
    float descent = (float) lm.getDescent();
    //this.h = (((float)(bounds.getHeight() - (descent * 1f)) * this.w) / (float)bounds.getWidth() );

    GlyphVector gv1 = font.createGlyphVector(frc, this.text);
    //Shape shp = gv1.getOutline();
    //Rectangle2D bounds1 = shp.getBounds();
    Rectangle2D bounds1 = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
    //bounds1.setRect(bounds1.getX(), 0, bounds1.getWidth() + 500, bounds1.getHeight() + 500);
    this.h = ((float) ((bounds1.getHeight()) * this.w) / (float) bounds1.getWidth());
    //this.h *= 1.2f;
    //this.w *= 1.2f;
    //System.out.println("this height = " + this.h);
    //System.out.println("shape bounds for <"+ this.text +"> = " + bounds);

    justifyX = 0;
    justifyY = 0;
    insetX = 0f;
    insetY = 0f;
    insetColor = new Colorf();
    backgroundColor = new Colorf();
    textBackgroundColor = new Colorf(1f, 0f, 0f, .5f);
    //insetColor = new Colorf(1f, 0f, 0f, 1f);
  }

  public static void sortTagsByCount(List<GeomTag> list)
  {
    Collections.sort(list, new Comparator()
    {
      public int compare(Object a, Object b)
      {
        GeomTag tag1 = (GeomTag) a;
        GeomTag tag2 = (GeomTag) b;
        /*
        int int1 = tag1.count;
        int int2 = tag2.count;
        if (int1 > int2)
        {
          return -1;
        }
        else if (int1 < int2)
        {
          return 1;
        }
        else
        {
          return 0;
        }
        */
        return tag2.count - tag1.count;
      }
    });
  }
}
