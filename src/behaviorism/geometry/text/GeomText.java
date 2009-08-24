/* GeomText.java ~ Oct 6, 2008 */
package behaviorism. geometry.text;

import behaviorism.Behaviorism;
import behaviorism.geometry.Colorf;
import behaviorism.geometry.Geom;
import behaviorism.geometry.GeomRect;
import behaviorism.handlers.FontHandler;
import behaviorism.renderers.Renderer;
import behaviorism.utils.MatrixUtils;
import behaviorism.utils.RenderUtils;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import java.awt.font.LineMetrics;
import java.util.List;

public class GeomText extends GeomRect
{

  public String text = "";
  public List<TextRenderer> textRenderers = FontHandler.getInstance().textRenderers;
  public float justifyX = 0; //0 center, -1 left, +1 right
  public float justifyY = 0; //0 center, -1 bottom, +1 top
  public Colorf backgroundColor = null; //background of entire bounds
  public boolean usePixelAnchor = false;
  public boolean exactPixelBounds = false;
  public boolean useNonDynamicTextRenderer = false;
  public boolean fitInBox = false;
  public boolean adjustDescent = false;
  public boolean usePadding = false;
  public boolean exactPadding = true; //otherwise percentage padding
  public float marginLeft = 0f;
  public float marginRight = 0f;
  public float marginBottom = 0f;
  public float marginTop = 0f;
  public float width = 0f;
  public float height = 0f;
  public float boxWidth = 0f;
  public float boxHeight = 0f;
  protected float paddingLeft = 0f;
  protected float paddingRight = 0f;
  protected float paddingBottom = 0f;
  protected float paddingTop = 0f;
  protected Rectangle2D stringBounds = null;
  protected TextRenderer textRenderer = null;
  protected Font font = null;
  protected FontRenderContext frc = null;
  protected float scaleVal = 1f;
  protected int prevPxWidth = 0;
  protected int prevPxHeight = 0;
  protected int pxX = 0;
  protected int pxY = 0;
  protected int pxWidth = 0;
  protected int pxHeight = 0;
  protected float prevW = 0f;
  protected float prevH = 0f;
  protected float tempypos = 0f;
  protected float useY = 0f;
  protected float useX = 0f;
  protected float transX = 0f;
  protected float transY = 0f;
  protected LineMetrics metrics;
  public boolean isRecalculated = false;

  public GeomText()
  {
  } //temp while we are cleaning stuff up... remove this soon! TODO

  public GeomText(Point3f anchorPt, TextBuilder builder)
  {
    super(anchorPt, 0, 0); //we calculate the width/height ourselves
    initialize(builder);
    calculateWorld();
  }

  public GeomText(int pxX, int pxY, boolean pixelAnchorUpperLeft, TextBuilder builder)
  {
    super(pixelAnchorUpperLeft, pxX, pxY, 0, 0);  //we calculate the width/height ourselves
    initialize(builder);
    calculatePixel();
  }

  public void recalculate()
  {
    System.out.println("in recalculate()");
    this.w = 0;
    this.h = 0;
    useX = 0;
    useY = 0;
    transX = 0;
    transY = 0;
    paddingLeft = 0f;
    paddingRight = 0f;
    paddingBottom = 0f;
    paddingTop = 0f;

    if (usePixelAnchor == true)
    {
      calculatePixel();
    }
    else
    {
      calculateWorld();
    }

    isTransformed = true;
  }

  public void calculateWorld()
  {
    if (useNonDynamicTextRenderer == true)
    {
      initializeNonDynamic(false);
    }
    else
    {
      initializeConstraints();
      initializePadding(marginLeft, marginRight, marginBottom, marginTop);
      initializeJustification();
    }
    isTransformed = true;
  }

  public void calculatePixel()
  {
    if (useNonDynamicTextRenderer == true)
    {
      initializeNonDynamic(true);
    }
    else
    {
      initializeConstraints();
      initializePixelPadding(); //this also transforms pixel to world coords
      initializePixelJustification();
    }
    isTransformed = true;
  }

  private void initializeNonDynamic(boolean pixelPadding)
  {
    //return w/h in world coords
    calculateBoundsIgnoringLOD();

    if (exactPadding == true && pixelPadding == true)
    {
      //since padding is in pixels, we need to make it in world
      Point3f worldPaddingLL = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 + marginLeft,
        Behaviorism.getInstance().canvasHeight / 2 - marginBottom);
      Point3f worldPaddingUR = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 + marginRight,
        Behaviorism.getInstance().canvasHeight / 2 - marginTop);

      initializePadding(worldPaddingLL.x, worldPaddingLL.y, worldPaddingUR.x, worldPaddingUR.y);
    }
    else
    {
      initializePadding(marginLeft, marginRight, marginBottom, marginTop);
    }

    useX = this.paddingLeft;
    useY = this.paddingBottom;

    if (adjustDescent == true)
    {
      useY += (metrics.getDescent() * scaleVal);
    }

    if (pixelPadding == true)
    {
      initializePixelJustification();
      useX -= transX;
      useY += transY;
    }
    else
    {
      initializeJustification();

      useX -= transX;
      useY += transY;
    }
  }

  private void initializePixelPadding()
  {
    //determine padding
    initializePadding(marginLeft, marginRight, marginBottom, marginTop);

    //transform pixels to world coordinates & update translate point
    Point3f upperright = MatrixUtils.pixelToWorld(
      Behaviorism.getInstance().canvasWidth / 2 + this.w,
      Behaviorism.getInstance().canvasHeight / 2 + this.h);

    this.w = upperright.x;
    this.h = -upperright.y;

    if (usePadding == true)
    {
      //this works if the GeomText is directly attached to world...
      //Test when attached to other things, especially when parents are scaled/rotated, etc.
      Point3f worldPaddingLL = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 +
        this.paddingLeft,
        Behaviorism.getInstance().canvasHeight / 2 -
        this.paddingBottom);
      this.paddingLeft = worldPaddingLL.x;
      this.paddingBottom = worldPaddingLL.y;

      Point3f worldPaddingUR = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 +
        this.paddingRight,
        Behaviorism.getInstance().canvasHeight / 2 -
        this.paddingTop);
      this.paddingRight = worldPaddingUR.x;
      this.paddingTop = worldPaddingUR.y;
    }
  }


  private void calculateBoundsIgnoringLOD()
  {
    frc = textRenderer.getFontRenderContext();
    font = textRenderer.getFont();

    Rectangle2D bounds;

    if (exactPixelBounds == true)
    {
      GlyphVector gv1 = font.createGlyphVector(frc, this.text);
      this.stringBounds = gv1.getPixelBounds(null, 0f, 0f);
    }
    else
    {
      this.stringBounds = font.getStringBounds(this.text, frc);
    }

    bounds = this.stringBounds;

    float worldHeight = Renderer.screenBoundsInWorldCoords.height; //Behaviorism.world.getWorldRect().h;
    this.scaleVal = ((worldHeight / (float) Behaviorism.getInstance().canvasHeight));

    this.metrics = font.getLineMetrics(text, frc);

    //initialize width
    this.w = (float) bounds.getWidth() * scaleVal;
    this.h = (float) bounds.getHeight() * scaleVal;
  }

  private void initializePadding(float left, float right, float bottom, float top)
  {
    if (usePadding == true)
    {
      if (exactPadding == true)
      {
        this.paddingLeft = left; //builder.paddingLeft;
        this.paddingRight = right; //builder.paddingRight;
        this.paddingBottom = bottom; //builder.paddingBottom;
        this.paddingTop = top; //builder.paddingTop;
      }
      else //percentage padding
      {
        this.paddingLeft = this.w * left; //builder.paddingLeft;
        this.paddingRight = this.w * right; //builder.paddingRight;
        this.paddingBottom = this.h * bottom; //builder.paddingBottom;
        this.paddingTop = this.h * top; //builder.paddingTop;
      }

      this.w += (this.paddingLeft + this.paddingRight);
      this.h += (this.paddingBottom + this.paddingTop);
    }
  }

  private float calculateWorldJustificationBoxX(float boxx)
  {
    return -((boxx * .5f) + (justifyX * (boxx * .5f)) - ((this.w * .5f) + (justifyX * (this.w * .5f))));
  }

  private float calculateWorldJustificationBoxY(float boxy, float h)
  {
    return -(boxy * .5f) + (-justifyY * (boxy * .5f)) + ((h * .5f) - (-justifyY * (h * .5f)));
  }

  private float calculateWorldJustificationPixelBoxY(float boxy, float h)
  {
    return -(boxy * .5f) - (-justifyY * (boxy * .5f)) - ((h * .5f) - (-justifyY * (h * .5f)));
  }

  private float calculateWorldJustificationX()
  {
    //System.out.println("justifyX = " + justifyX);
    return (this.w * .5f) + (justifyX * (this.w * .5f));
  }

  private float calculateWorldJustificationY()
  {
    return -((this.h * .5f) + (justifyY * (this.h * .5f)));
  }

  private void initializeJustification()
  {
    if (fitInBox == true)
    {
      transX += (calculateWorldJustificationBoxX(boxWidth));
      transY -= (calculateWorldJustificationBoxY(boxHeight, this.h));
    }
    else
    {
      transX += (calculateWorldJustificationX());
      transY += (calculateWorldJustificationY());
    }
  }

  private void initializePixelJustification()
  {
    if (fitInBox == true)
    {
      Point3f worldBox = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 +
        boxWidth,
        Behaviorism.getInstance().canvasHeight / 2 -
        boxHeight);

      transX += calculateWorldJustificationBoxX(worldBox.x);
      transY += calculateWorldJustificationPixelBoxY(worldBox.y, this.h);
    }
    else
    {
      transX += calculateWorldJustificationX();
      transY += calculateWorldJustificationY();
    }
  }

  /**
   * Initializes this GeomText by copying over all pertinent information from the TextBuilder.
   * After this is done, we have no need of the TextBuilder. (this is no completely done! TO DO:
   * copy over paddingL/R/B/T info),
   * @param builder The TextBuilder that we are initializing the GeomText with.
   */
  private void initialize(TextBuilder builder)
  {
    setColor(builder.textColor);
    this.backgroundColor = builder.backgroundColor;

    this.text = builder.text;
    this.exactPixelBounds = builder.exactPixelBounds;

    this.exactPadding = builder.exactPadding;
    this.usePadding = builder.usePadding;
    this.marginLeft = builder.paddingLeft;
    this.marginRight = builder.paddingRight;
    this.marginBottom = builder.paddingBottom;
    this.marginTop = builder.paddingTop;
    this.justifyX = builder.justifyX;
    this.justifyY = builder.justifyY;
    this.adjustDescent = builder.adjustDescent;
    this.width = builder.width;
    this.height = builder.height;
    this.boxWidth = builder.boxWidth;
    this.boxHeight = builder.boxHeight;
    this.fitInBox = builder.fitInBox;
    this.usePixelAnchor = builder.usePixelAnchor;

    if (builder.nonDynamicTextRenderer != null)
    {
      this.useNonDynamicTextRenderer = true;
      this.textRenderer = builder.nonDynamicTextRenderer;
    }
    else
    {
      if (builder.textRenderers != null)
      {
        this.textRenderers = builder.textRenderers;
      }
      else
      {
        this.textRenderers = FontHandler.getInstance().getDefaultFontFamily();
      }
    }
  }

  public void initializeConstraints()
  {
    if (width <= 0f && height <= 0f)
    {
      //this is illegal, default to a width and height of 2fx1f, or 100x50 if using pixels
      if (usePixelAnchor == true)
      {
        this.w = 100;
        this.h = 50;
      }
      else
      {
        this.w = 2f;
        this.h = 1f;
      }
    }
    else if (width <= 0f && height > 0f)
    {
      setWidthByHeight(height);
    }
    else if (width > 0f && height <= 0f)
    {
      setHeightByWidth(width);
    }
    else if (width > 0f && height > 0f)
    {
      setWidthAndHeight(width, height);
    }
  }

  protected void calculateUnrotatedPixelWidthAndHeight(GL gl)
  {
    Point3f lowerleft = MatrixUtils.toPoint3f(
      MatrixUtils.getGeomPointInWorldCoordinates(
      MatrixUtils.toPoint3d(new Point3f(0f, 0f, 0f)), modelview,
      RenderUtils.getCamera().modelview));
    Point3f upperright = MatrixUtils.toPoint3f(
      MatrixUtils.getGeomPointInWorldCoordinates(
      MatrixUtils.toPoint3d(new Point3f(w, h, 0f)), modelview,
      RenderUtils.getCamera().modelview));

    float avgdist = (lowerleft.z + upperright.z) / 2f;

    //double[] temp_mv = new double[16];
    //System.arraycopy(RenderUtils.getCamera().modelview, 0, temp_mv, 0, 16);
    double[] temp_mv = RenderUtils.getCamera().modelview;

    //add up scale from root of tree to this Geom
    Geom upp = this;
    while (upp.parent != null)
    {
      temp_mv = MatrixUtils.scale(temp_mv, upp.scale.x, upp.scale.y, upp.scale.z);
      upp = upp.parent;
    }

    temp_mv = MatrixUtils.translate(temp_mv, 0f, 0f, (float) avgdist);

    double[] temp_pj = RenderUtils.getCamera().projection;
    int[] temp_vp = RenderUtils.getCamera().viewport;

    this.pxWidth = (int) (RenderUtils.getWidthOfObjectInPixels(
      this, this.paddingLeft + this.paddingRight, temp_mv, temp_pj, temp_vp));
    this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(
      this, this.paddingBottom + this.paddingTop, temp_mv, temp_pj, temp_vp));

    boolean debug = false;
    if (debug)
    {
      System.out.println("pxW/pxH = " + pxWidth + "/" + pxHeight);
      gl.glPushMatrix();
      temp_mv = MatrixUtils.translate(temp_mv, -w / 2f, -h / 2f, 0f);
      gl.glLoadMatrixd(temp_mv, 0);
      gl.glColor4f(0f, 0f, 1f, 1f);
      drawRect(gl, 0f);
      gl.glPopMatrix();
    }
  }


  //DebugTimer timer = new DebugTimer();
  @Override
  public void draw(GL gl)
  {
    if (isRecalculated)
    {
      recalculate();
    }

    if (this.backgroundColor != null)
    {
      gl.glColor4fv(backgroundColor.array(), 0);
      drawRect(gl, offset);
    }

    if (useNonDynamicTextRenderer == true)
    {
      drawText();
    }
    else
    {
      //timer.resetTime();
      calculateUnrotatedPixelWidthAndHeight(gl);
      //System.out.println("time to calc = " + timer.resetTime());
      {
        if (isRecalculated || this.pxWidth != this.prevPxWidth || this.pxHeight != this.prevPxHeight || this.textRenderer == null)
        {
          //System.out.println("pxWidth/pxHeight = " + pxWidth + "/" + pxHeight);

          this.prevPxWidth = this.pxWidth;
          this.prevPxHeight = this.pxHeight;

          //timer.resetTime();
          chooseFontSize();

          useX = paddingLeft - (transX);
          useY = transY + (float) (paddingBottom + ((stringBounds.getHeight() + stringBounds.getY()) * scaleVal));
        }
      }

      drawText();
    }
    //System.out.println("time to draw = " + timer.resetTime());

    isRecalculated = false;
  }

  private void drawText()
  {
    textRenderer.begin3DRendering();
    textRenderer.setColor(color.toJavaColor());
    textRenderer.draw3D(text,
      useX,
      useY,
      offset,
      scaleVal);
    textRenderer.end3DRendering();

  //textRenderer.flush();
  }

  private void drawRect(GL gl, float offset)
  {
    float x = -transX;
    float y = transY;
    gl.glBegin(gl.GL_QUADS);
    gl.glVertex3f(x, y, offset);
    gl.glVertex3f(x + w, y, offset);
    gl.glVertex3f(x + w, y + h, offset);
    gl.glVertex3f(x, y + h, offset);
    gl.glEnd();
  }

  //have to think about this...
  @Override
  public void drawPickingBackground(GL gl)
  {
//    gl.glColor4f(0f, 0f, 0f, 0f);
//    drawRect(gl, 0f);
  }

  /*
  public void calculateBoundsUsingSpecficTextRenderer(TextRenderer textRenderer)
  {
  //System.out.println("in calculateBoundsUsingSpecficTextRenderer()");
  FontRenderContext frc = textRenderer.getFontRenderContext();
  Font font = textRenderer.getFont();

  if (exactPixelBounds == true)
  {
  GlyphVector gv1 = font.createGlyphVector(frc, this.text);
  this.stringBounds = gv1.getPixelBounds(null, 0f, 0f);
  }
  else
  {
  this.stringBounds = font.getStringBounds(this.text, frc);
  }

  this.scaleVal = (this.w - (paddingX * 2f)) / (float) this.stringBounds.getWidth();
  }
   */
  public Rectangle2D getStringWidthUsingTextRenderer(TextRenderer tr)
  {
    Rectangle2D rect = null;
    frc = tr.getFontRenderContext();
    font = tr.getFont();

    if (exactPixelBounds == true)
    {
      GlyphVector gv = font.createGlyphVector(frc, this.text);
      rect = gv.getPixelBounds(null, 0f, 0f);
    }
    else
    {
      rect = font.getStringBounds(this.text, frc);
    }

    return rect;
  }

  /**
   * Chooses the font size appropriate to the pixel size of this Geom.
   */
  public void chooseFontSize()
  {
    this.textRenderer = textRenderers.get(0);
    this.stringBounds = getStringWidthUsingTextRenderer(this.textRenderer);
    float curWidth = (float) stringBounds.getWidth();

    for (int i = 1; i < textRenderers.size(); i++)
    {
      Rectangle2D nextBounds = getStringWidthUsingTextRenderer(textRenderers.get(i));
      float nextWidth = (float) nextBounds.getWidth();

      if (pxWidth < (curWidth + nextWidth) / 2f)
      {
        this.scaleVal = (this.w - (paddingLeft + paddingRight)) / curWidth;
        return;
      }

      //try next one
      this.textRenderer = textRenderers.get(i);
      this.stringBounds = nextBounds;
      curWidth = nextWidth;
    }

    //use biggest one
    this.scaleVal = (this.w - (paddingLeft + paddingRight)) / curWidth;
    return;

  }

  @Override
  public String toString()
  {
    return super.toString() + ", [" + text + "]";
  }

  public void setWidthAndHeight(float maxw, float maxh)
  {
    TextRenderer fi = textRenderers.get(textRenderers.size() - 1); //** TO DO: these aren't necessarily sorted?? !*/

    frc = fi.getFontRenderContext();
    font = fi.getFont();

    Rectangle2D bounds;
    if (exactPixelBounds == true)
    {
      GlyphVector gv1 = font.createGlyphVector(frc, this.text);
      bounds = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
    }
    else
    {
      bounds = font.getStringBounds(this.text, frc);
    }
    float tw = ((float) ((bounds.getWidth()) * maxh) / (float) bounds.getHeight());

    if (tw > maxw)
    {
      float th = ((float) ((bounds.getHeight()) * maxw) / (float) bounds.getWidth());
      this.w = maxw;
      this.h = th;
    }
    else
    {
      this.h = maxh;
      this.w = tw;
    }
  }

  public void setWidthByHeight(float h)
  {
    this.h = h;

    TextRenderer fi = textRenderers.get(textRenderers.size() - 1); //** TO DO: these aren't necessarily sorted?? !*/

    frc = fi.getFontRenderContext();
    font = fi.getFont();

    Rectangle2D bounds;
    if (exactPixelBounds == true)
    {
      GlyphVector gv1 = font.createGlyphVector(frc, this.text);
      bounds = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
    }
    else
    {
      bounds = font.getStringBounds(this.text, frc);
    }
    this.w = ((float) ((bounds.getWidth()) * this.h) / (float) bounds.getHeight());
  }

  public void setHeightByWidth(float w)
  {
    this.w = w;

    TextRenderer fi = textRenderers.get(textRenderers.size() - 1); //** TO DO: these aren't necessarily sorted?? !*/

    frc = fi.getFontRenderContext();
    font = fi.getFont();

    Rectangle2D bounds;

    if (exactPixelBounds == true)
    {
      GlyphVector gv1 = font.createGlyphVector(frc, this.text);
      bounds = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
    }
    else
    {
      bounds = font.getStringBounds(this.text, frc);
    }

    this.h = ((float) ((bounds.getHeight()) * this.w) / (float) bounds.getWidth());
  }

  /*
  public static GeomText2 createGeomTextWithQuotes(String text)
  {
  return createGeomTextWithQuotes(0f, 0f, 0f, 2f, 1f, text);
  }

  public static GeomText2 createGeomTextWithQuotes(
  float x, float y, float z, float w, float h,
  String text)
  {
  GeomText2 gtf = new GeomText2(x, y, z, w, h, text);
  gtf.setColor(1f, 1f, 1f, 1f);
  //gtf.backgroundColor= new Colorf();
  gtf.determineRotateAnchor(RotateEnum.CENTER);
  gtf.determineScaleAnchor(ScaleEnum.CENTER);

  GeomText2 gtf2 = new GeomText2(-.4f, .4f, 0f, .36f, .5f, "\u201C");
  gtf2.setColor(1f, 1f, 1f, 1f);
  //gtf2.backgroundColor= new Colorf();
  gtf.addGeom(gtf2, true);

  GeomText2 gtf3 = new GeomText2(gtf.w + .05f, .4f, 0f, .36f, .5f, "\u201D");
  gtf3.setColor(1f, 1f, 1f, 1f);
  gtf.addGeom(gtf3, true);

  //gtf2.registerDraggableObject(gtf);
  //gtf3.registerDraggableObject(gtf);
  gtf2.registerSelectableObject(gtf);
  gtf3.registerSelectableObject(gtf);
  return gtf;
  }
   */
  @Override
  public void dispose()
  {
    super.dispose();
//    if (textRenderer != null)
//    {
//      textRenderer = null;
//    }
  }

    public void setMargins(float left, float right, float bottom, float top)
  {
    this.marginLeft = left;
    this.marginRight = right;
    this.marginBottom = bottom;
    this.marginTop = top;
    isRecalculated = true;
  }
  public void setJustify(float x, float y)
  {
    this.justifyX = x;
    this.justifyY = y;
    isRecalculated = true;
  }

  public void setFont(String fontString, int fontStyle, int fontSize)
  {
    this.textRenderer = FontHandler.getInstance().getFont(fontString, fontStyle, fontSize);
    useNonDynamicTextRenderer = true;
    isRecalculated = true;
  }

  public void setFont(String fontString, int fontStyle)
  {
    this.textRenderers = FontHandler.getInstance().getFontFamily(fontString, fontStyle);
    useNonDynamicTextRenderer = false;
    isRecalculated = true;
  }

  public void setFont(List<TextRenderer> trs)
  {
    this.textRenderers = trs;
    useNonDynamicTextRenderer = false;
    isRecalculated = true;
  }

  public void setText(String text)
  {
    this.text = text;
    isRecalculated = true;
  }

}
