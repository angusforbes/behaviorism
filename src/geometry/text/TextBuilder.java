/* TextBuilder.java ~ Jun 24, 2009 */

package geometry.text;

import com.sun.opengl.util.j2d.TextRenderer;
import geometry.Colorf;
import handlers.FontHandler;
import java.awt.Font;
import java.util.List;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
 public class TextBuilder
  {

    public String text;
    public Point3f anchorPt = new Point3f();
    public int anchorPxX = 0;
    public int anchorPxY = 0;
    public boolean usePixelAnchor = false;
    public boolean pixelAnchorUpperLeft = false;
    public float width = 0f;
    public float height = 0f;
    public float justifyX = 0;
    public float justifyY = 0;
    public boolean usePadding = false;
    public boolean exactPadding = false;
    //public float paddingX = 0f;
    //public float paddingY = 0f;
    public float paddingLeft = 0f;
    public float paddingRight = 0f;
    public float paddingBottom = 0f;
    public float paddingTop = 0f;
    public Colorf backgroundColor = null; //new Colorf();
    public Colorf textColor = new Colorf();
    public TextRenderer nonDynamicTextRenderer = null;
    public boolean adjustDescent = true;
    public boolean exactPixelBounds = false;
    public float boxWidth = -1f;
    public float boxHeight = -1f;
    public boolean fitInBox = false;
    public List<TextRenderer> textRenderers = null;

    public TextBuilder()
    {
    }

    public TextBuilder(String text)
    {
      this.text = text;
    }

    public TextBuilder text(String text)
    {
      this.text = text;
      return this;
    }

    /**
     * LOD
     * @param font
     * @param fontStyle
     * @return
     */
    public TextBuilder font(String font, int fontStyle)
    {
      this.textRenderers = FontHandler.getInstance().getFontFamily(font, fontStyle);
      return this;
    }


    /**
     * LOD
     * @param font
     * @return
     */
    public TextBuilder font(String font)
    {
      this.textRenderers = FontHandler.getInstance().getFontFamily(font, Font.PLAIN);
      return this;
    }

    /**
     * LOD
     * @param textRenderers
     * @return
     */
    public TextBuilder font(List<TextRenderer> textRenderers)
    {
      this.textRenderers = textRenderers;
      return this;
    }

    /**
     * non dynamic
     * @param fontName
     * @param fontStyle
     * @param fontSize
     * @return
     */
    public TextBuilder font(String fontName, int fontStyle, float fontSize)
    {
      this.nonDynamicTextRenderer = FontHandler.getInstance().getFont(fontName, fontStyle, fontSize);
      return this;
    }

    /**
     * non dynamic
     * @param nonDynamicTextRenderer
     * @return
     */
    public TextBuilder font(TextRenderer nonDynamicTextRenderer)
    {
      this.nonDynamicTextRenderer = nonDynamicTextRenderer;
      return this;
    }

    /**
     * A convenience method that positions the text within the given box area.
     * Justifications are based on the
     * boundaries of the box, as opposed to the translate point (as with the "contstrain" methods).
     * The box's x and y coordinates are the translate of this object.
     * Note that this box is simply for the initial placement. The bounds of the box
     * are not preserved once the GeomText object is created. If you want the box, you should
     * make it with a GeomRect or something.
     * This method makes no sense if used in conjuction with a non dynamic text renderer.
     * @param bw
     * @param bh
     * @return The TextBuilder we are building.
     */
    public TextBuilder fitInBox(float width, float height)
    {
      this.boxWidth = width;
      this.boxHeight = height;
      this.width = width;
      this.height = height;
      this.fitInBox = true;
      return this;
    }

    public TextBuilder anchor(Point3f anchorPt)
    {
      this.anchorPt = anchorPt;
      this.usePixelAnchor = false;
      return this;
    }

    public TextBuilder anchor(float x, float y, float z)
    {
      this.anchorPt = new Point3f(x,y,z);
      this.usePixelAnchor = false;
      return this;
    }


    public TextBuilder anchor(int x, int y)
    {
      this.anchorPxX = x;
      this.anchorPxY = y;
      this.pixelAnchorUpperLeft = false;
      this.usePixelAnchor = true;
      return this;
    }

    public TextBuilder anchor(int x, int y, boolean upperLeft)
    {
      this.anchorPxX = x;
      this.anchorPxY = y;
      this.pixelAnchorUpperLeft = upperLeft;
      this.usePixelAnchor = true;
      return this;
    }

    public TextBuilder adjustDescent(boolean adjustDescent)
    {
      this.adjustDescent = adjustDescent;
      return this;
    }

    public TextBuilder justify(float justifyX, float justifyY)
    {
      this.justifyX = justifyX;
      this.justifyY = justifyY;
      return this;
    }

    public TextBuilder exactBounds(boolean exactPixelBounds)
    {
      this.exactPixelBounds = exactPixelBounds;
      return this;
    }

    public TextBuilder exactPadding(float paddingX, float paddingY)
    {
      return padding(paddingX, paddingY, true);
    }

    public TextBuilder percentagePadding(float paddingX, float paddingY)
    {
      return padding(paddingX, paddingY, false);
    }

    public TextBuilder exactPadding(float paddingL, float paddingR, float paddingB, float paddingT)
    {
      return padding(paddingL, paddingR, paddingB, paddingT, true);
    }

    public TextBuilder percentagePadding(float paddingL, float paddingR, float paddingB, float paddingT)
    {
      return padding(paddingL, paddingR, paddingB, paddingT, false);
    }

    public TextBuilder padding(float paddingX, float paddingY, boolean exactPadding)
    {
      return padding(paddingX, paddingX, paddingY, paddingY, exactPadding);
    }

    public TextBuilder padding(float left, float right, float bottom, float top, boolean exactPadding)
    {
      this.paddingLeft = left;
      this.paddingRight = right;
      this.paddingBottom = bottom;
      this.paddingTop = top;
      this.exactPadding = exactPadding;
      this.usePadding = true;
      return this;
    }

    public TextBuilder backgroundColor(Colorf backgroundColor)
    {
      this.backgroundColor = backgroundColor;
      return this;
    }

    public TextBuilder textColor(Colorf textColor)
    {
      this.textColor = textColor;
      return this;
    }

    public TextBuilder constrainByHeight(float height)
    {
      this.height = height;
      this.width = -1f;
      return this;
      }

    public TextBuilder constrainByWidth(float width)
    {
      this.height = -1f;
      this.width = width;
      return this;
    }

    public TextBuilder constrain(float width, float height)
    {
      this.height = height;
      this.width = width;
      return this;
    }

    private void validatePixel()
    {
//        if (width <= 0 && height <= 0)
//        {
//          this.width = 100;
//          this.height = 50;
//        }
//        else
//        {
//          this.w = 2f;
//          this.h = 1f;
//        }
    }

    private void validateWorld()
    {

    }
    public GeomText build()
    {
      if (this.usePixelAnchor == true)
      {
        validatePixel();
        return new GeomText(anchorPxX, anchorPxY, pixelAnchorUpperLeft, this);
      }
      else
      {
        validateWorld();
        return new GeomText(anchorPt, this);
      }
    }
  }


