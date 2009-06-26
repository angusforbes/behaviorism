/* GeomText.java ~ Oct 6, 2008 */
package geometry.text;

import behaviorism.Behaviorism;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomRect;
import handlers.FontHandler;
import java.awt.font.LineMetrics;
import java.util.List;
import renderers.Renderer;
import utils.MatrixUtils;
import utils.RenderUtils;

public class GeomText extends GeomRect
{

  public String text = "";
  public List<TextRenderer> textRenderers = FontHandler.getInstance().textRenderers;
  public int justifyX = 0; //0 center, -1 left, +1 right
  public int justifyY = 0; //0 center, -1 bottom, +1 top
  public float insetX = 0f;
  public float insetY = 0f;
  public float outsetX = 0f;
  public float outsetY = 0f;
  public Colorf backgroundColor = null; //background of entire bounds
  public boolean exactPixelBounds = false;
  public boolean useNonDynamicTextRenderer = false;
  public float paddingLeft = 0f;
  public float paddingRight = 0f;
  public float paddingBottom = 0f;
  public float paddingTop = 0f;
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
  public TextBuilder builder = null;
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

    if (builder.usePixelAnchor == true)
    {
      calculatePixel();
    }
    else
    {
      calculateWorld();
    }
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
      initializePadding(builder.paddingLeft, builder.paddingRight, builder.paddingBottom, builder.paddingTop);
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

    if (builder.exactPadding == true && pixelPadding == true)
    {
      //since padding is in pixels, we need to make it in world
      Point3f worldPaddingLL = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 + builder.paddingLeft,
        Behaviorism.getInstance().canvasHeight / 2 - builder.paddingBottom);
      Point3f worldPaddingUR = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 + builder.paddingRight,
        Behaviorism.getInstance().canvasHeight / 2 - builder.paddingTop);

      initializePadding(worldPaddingLL.x, worldPaddingLL.y, worldPaddingUR.x, worldPaddingUR.y);
    }
    else
    {
      initializePadding(builder.paddingLeft, builder.paddingRight, builder.paddingBottom, builder.paddingTop);
    }

    useX = this.paddingLeft;
    useY = this.paddingBottom;

    if (builder.adjustDescent == true)
    {
      useY += (metrics.getDescent() * scaleVal);
    }

    if (pixelPadding == true)
    {
      Point3f worldBox = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 + builder.boxWidth,
        Behaviorism.getInstance().canvasHeight / 2 - builder.boxHeight);

      builder.boxWidth = worldBox.x;
      builder.boxHeight = worldBox.y;
    }

    initializeJustification();

    useX -= transX;
    useY += transY;
  }

  private void initializePixelPadding()
  {
    //determine padding
    initializePadding(builder.paddingLeft, builder.paddingRight, builder.paddingBottom, builder.paddingTop);

    //transform pixels to world coordinates & update translate point
     Point3f upperright = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 + this.w,
        Behaviorism.getInstance().canvasHeight / 2 + this.h);

     this.w = upperright.x;
     this.h = -upperright.y;

    if (builder.usePadding == true)
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
    if (builder.usePadding == true)
    {
      if (builder.exactPadding == true)
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

  private float calculateWorldJustificationBoxX()
  {
    switch (justifyX)
    {
      case 0:
        return ((builder.boxWidth * .5f) + (this.w * .5f));
      case 1:
        return this.w;
      case -1:
        return (builder.boxWidth);
      default:
        return 0f;
    }
  }

  private float calculateWorldJustificationBoxY()
  {
    switch (justifyY)
    {
      case 0:
        return -(builder.boxHeight * .5f) - (this.h * .5f);
      case 1:
        return -this.h;
      case -1:
        return -(builder.boxHeight);
      default:
        return 0f;
    }
  }

  private float calculateWorldJustificationX()
  {
    switch (justifyX)
    {
      case 0:
        return this.w * .5f;
      case 1:
        return this.w;
      case -1:
      default:
        return 0f;
    }
  }

  private float calculateWorldJustificationY()
  {
    switch (justifyY)
    {
      case 0:
        return -(this.h * .5f);
      case 1:
        return -this.h;
      case -1:
      default:
        return 0f;
    }
  }

  private void initializeJustification()
  {
    if (builder.fitInBox == true)
    {
      transX = (calculateWorldJustificationBoxX());
      transY = (calculateWorldJustificationBoxY());
    }
    else
    {
      transX = (calculateWorldJustificationX());
      transY = (calculateWorldJustificationY());
    }
  }

  private void initializePixelJustification()
  {
    if (builder.fitInBox == true)
    {
      Point3f worldBox = MatrixUtils.pixelToWorld(
        Behaviorism.getInstance().canvasWidth / 2 +
        builder.boxWidth,
        Behaviorism.getInstance().canvasHeight / 2 -
        builder.boxHeight);

      this.h += worldBox.y; //in the pixel version have to account for reverse Y

      switch (justifyX)
      {
        case 0:
          this.transX -= ((worldBox.x * .5f) - (this.w * .5f));
          break;
        case 1:
          this.transX -= ((worldBox.x) - this.w);
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.transY -= (worldBox.y * .5f) - (this.h * .5f);
          break;
        case 1:
          this.translate.y -= (worldBox.y) - this.h;
          break;
        case -1:
          break;
      }
    }
    else
    {
      switch (justifyX)
      {
        case 0:
          transX += this.w * .5f;
          break;
        case 1:
          transX += this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          transY -= (this.h * .5f);
          break;
        case 1:
          transY -= this.h;
          break;
        case -1:
          break;
      }
    }
  }

  private void initialize(TextBuilder builder)
  {
    this.builder = builder;

    setColor(builder.textColor);
    this.backgroundColor = builder.backgroundColor;

    this.text = builder.text;
    this.exactPixelBounds = builder.exactPixelBounds;

    this.justifyX = builder.justifyX;
    this.justifyY = builder.justifyY;

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
      if (builder.width <= 0f && builder.height <= 0f)
      {
        //this is illegal, default to a width and height of 2fx1f, or 100x50 if using pixels
        if (builder.usePixelAnchor == true)
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
      else if (builder.width <= 0f && builder.height > 0f)
      {
        setWidthByHeight(builder.height);
      }
      else if (builder.width > 0f && builder.height <= 0f)
      {
        setHeightByWidth(builder.width);
      }
      else if (builder.width > 0f && builder.height > 0f)
      {
        setWidthAndHeight(builder.width, builder.height);
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

  @Override
  public void drawPickingBackground(GL gl)
  {
    gl.glColor4f(0f, 0f, 0f, 0f);
    drawRect(gl, 0f);
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
    frc = tr.getFontRenderContext();
    font = tr.getFont();

    if (exactPixelBounds == true)
    {
      GlyphVector gv = font.createGlyphVector(frc, this.text);
      return gv.getPixelBounds(null, 0f, 0f);
    }
    else
    {
      return font.getStringBounds(this.text, frc);
    }
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

  public void setText(String text)
  {
    //builder.text = text;
    this.text = text; //and should we recalculate everything here too?
    isRecalculated = true;
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
}
