/* GeomSimpleText.java ~ Feb 10, 2009 */
package geometry.text;

import behaviorism.Behaviorism;
import com.sun.opengl.util.j2d.TextRenderer;
import geometry.Colorf;
import geometry.GeomRect;
import handlers.FontHandler;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import renderers.Renderer;

/**
 * GeomSimpleText is a simple wrapper for displaying text in a specified font.
 * The width and height are automatically calculated based on the font size.
 * If the font has not already been loaded it will attempt to load it on-the-fly.
 * If it cannot find the font, it will load the default font instead.
 * The optimal dispay of the text occurs only within the current viewport and the current camera position.
 * No attempt is made to adjust the text according to the camera position or screen bounds,
 * and no level-of-detail algorithm is implemented. The text looks great.
 * @author angus
 */
public class GeomSimpleText extends GeomRect
{

  public Colorf backgroundColor = null;
  public float leftMarginPx = -1f;
  public float rightMarginPx = -1f;
  public float bottomMarginPx = 0f;
  public float topMarginPx = 0f;
  String text;
  TextRenderer textRenderer;
  private float scaleVal;
  private LineMetrics metrics;

  /**
   * Constructs a GeomSimpleText object.
   * @param p3f
   * @param text
   * @param fontName
   * @param fontStyle
   * @param fontSize
   */
  public GeomSimpleText(Point3f p3f, String text, String fontName, int fontStyle, float fontSize)
  {
    super(p3f, 0f, 0f);
    this.text = text;
    setFont(fontName, fontStyle, fontSize);
  }

  public GeomSimpleText(Point3f p3f, String text, TextRenderer textRenderer)
  {
    super(p3f, 0f, 0f);
    this.text = text;
    this.textRenderer = textRenderer;
    calculateBounds();
  }

  public GeomSimpleText(int x, int y, String text, String fontName, int fontStyle, float fontSize)
  {
    super(x, y, 0, 0);
    this.text = text;
    setFont(fontName, fontStyle, fontSize);
  }

  public GeomSimpleText(int x, int y, String text, TextRenderer textRenderer)
  {
    super(x, y, 0, 0);
    this.text = text;
    this.textRenderer = textRenderer;
    calculateBounds();
  }

  /**
   * Sets the font for this text object, if it can't be found then uses the default font.
   * @param fontName
   * @param fontStyle
   * @param fontSize
   */
  public void setFont(String fontName, int fontStyle, float fontSize)
  {
    this.textRenderer = FontHandler.getInstance().getFont(fontName, fontStyle, fontSize);
    calculateBounds();
  }

  /**
   * Sets the specific TextRenderer for this text object.
   * @param textRenderer
   */
  public void setTextRenderer(TextRenderer textRenderer)
  {
    this.textRenderer = textRenderer;
    calculateBounds();
  }

  /**
   * Sets the margins for this text object. Margins may be positive or negative.
   * @param left
   * @param right
   * @param bottom
   * @param top
   */
  public void setMargins(float left, float right, float bottom, float top)
  {
    this.leftMarginPx = left;
    this.bottomMarginPx = bottom;
    this.rightMarginPx = right;
    this.topMarginPx = top;
    calculateBounds();
  }

  /**
   * Sets the text string for this text object.
   * @param text
   */
  public void setText(String text)
  {
    this.text = text;
    calculateBounds();
  }

  /**
   * Calcuates the and scale value and position of the 3D text based on the
   * current viewport, font size, and margins.
   */
  private void calculateBounds()
  {
    FontRenderContext frc = textRenderer.getFontRenderContext();
    Font font = textRenderer.getFont();

    Rectangle2D bounds;
    //if (exactPixelBounds == true)
//    {
//      GlyphVector gv1 = font.createGlyphVector(frc, this.text);
//    //bounds = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
//    }
//    //else
    //	{
    bounds = font.getStringBounds(this.text, frc);

    float worldHeight = Renderer.screenBoundsInWorldCoords.height; //Behaviorism.world.getWorldRect().h;
    this.scaleVal = ((worldHeight / (float) Behaviorism.getInstance().canvasHeight));

    metrics = font.getLineMetrics(text, frc);

    this.w = ((float) bounds.getWidth() + (leftMarginPx + rightMarginPx)) * scaleVal;
    this.h = (float) ((bottomMarginPx + topMarginPx) + bounds.getHeight()) * scaleVal;//  - (metrics.getHeight() - metrics.getAscent())/*+ metrics.getLeading()*/ ) * scaleVal;

    if (this.rotateAnchor != null)
    {
      //this.rotateAnchor.translate.x = this.w * .5f;
      //this.rotateAnchor.translate.y = this.h * .5f;
      this.rotateAnchor.x = this.w * .5f;
      this.rotateAnchor.y = this.h * .5f;
    }
  }

  private void drawBounds(GL gl)
  {
    gl.glBegin(gl.GL_QUADS);
    float x = translate.x;
    float y = translate.y;
    float z = translate.z;

    gl.glVertex3f(x, y, z);
    gl.glVertex3f(x + w, y, z);
    gl.glVertex3f(x + w, y + h, z);
    gl.glVertex3f(x, y + h, z);
    gl.glEnd();
  }

  @Override
  public void drawPickingBackground(GL gl)
  {
    gl.glColor4f(0f, 0f, 0f, 0f);
    drawBounds(gl);
  }

  @Override
  public void draw(GL gl)
  {
    //calculateBounds();

    if (backgroundColor != null)
    {
      gl.glColor4fv(backgroundColor.array(), 0);
      drawBounds(gl);
    }

    textRenderer.begin3DRendering();
    textRenderer.setColor(color.r, color.g, color.b, color.a);

    textRenderer.draw3D(text, leftMarginPx * scaleVal, (bottomMarginPx * scaleVal) + (metrics.getDescent() * scaleVal), (offset), scaleVal);
    textRenderer.end3DRendering();
  }
}
