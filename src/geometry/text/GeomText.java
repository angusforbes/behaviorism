/* GeomText.java ~ Oct 6, 2008 */
package geometry.text;

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
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector3f;
import utils.MatrixUtils;
import utils.RenderUtils;

public class GeomText extends GeomRect
{
  public String text = "";
  public List<TextRenderer> textRenderers = FontHandler.getInstance().textRenderers;

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
  protected float tty = 0f;
  protected float tth = 0f;
  protected float useY = 0f;
  protected float useX = 0f;

  public int justifyX = 0; //0 center, -1 left, +1 right
  public int justifyY = 0; //0 center, -1 bottom, +1 top
  public float insetX = 0f;
  public float insetY = 0f;
  public float outsetX = 0f;
  public float outsetY = 0f;
  public Colorf insetColor = null; //background of insets
  public Colorf backgroundColor = null; //background of entire bounds
  public Colorf textBackgroundColor = null; //background of exact text bounds

  public boolean exactPixelBounds = false;
  public boolean useNonDynamicTextRenderer = false;
  public float paddingX = 0f;
  public float paddingY = 0f;

  //public boolean isFirstTime = true;

  public static class GeomTextBuilder
  {

    private String text;
    private Point3f anchorPt = new Point3f();
    private int anchorPxX = 0;
    private int anchorPxY = 0;
    private boolean usePixelAnchor = false;
    private boolean pixelAnchorUpperLeft = false;
    private float width = 1f;
    private float height = 1f;
    private int justifyX = 0;
    private int justifyY = 0;
    private boolean usePadding = false;
    private boolean exactPadding = false;
    private float paddingX = 0f;
    private float paddingY = 0f;
    private Colorf backgroundColor = null; //new Colorf();
    private Colorf textColor = new Colorf();
    private TextRenderer nonDynamicTextRenderer = null;
    private boolean exactPixelBounds = false;
    private float boxWidth = -1f;
    private float boxHeight = -1f;
    private boolean fitInBox = false;
    //private String fontString = null;
    //private int fontStyle = -1;
    private List<TextRenderer> textRenderers = null;

    public GeomTextBuilder(String text)
    {
      this.text = text;
    }

    public GeomTextBuilder font(String font, int fontStyle)
    {
      this.textRenderers = FontHandler.getInstance().getFontFamily(font, fontStyle);
      return this;
    }

    public GeomTextBuilder font(String font)
    {
      this.textRenderers = FontHandler.getInstance().getFontFamily(font, Font.PLAIN);
      return this;
    }

    public GeomTextBuilder font(List<TextRenderer> textRenderers)
    {
      this.textRenderers = textRenderers;
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
     * @param bw
     * @param bh
     * @return The GeomTextBuilder we are building.
     */
    public GeomTextBuilder fitInBox(float width, float height)
    {
      this.boxWidth = width;
      this.boxHeight = height;
      this.width = width;
      this.height = height;
      this.fitInBox = true;
      return this;
    }

    public GeomTextBuilder anchor(Point3f anchorPt)
    {
      this.anchorPt = anchorPt;
      this.usePixelAnchor = false;
      return this;
    }

    public GeomTextBuilder anchor(int x, int y)
    {
      this.anchorPxX = x;
      this.anchorPxY = y;
      this.pixelAnchorUpperLeft = false;
      this.usePixelAnchor = true;
      return this;
    }

    public GeomTextBuilder anchor(int x, int y, boolean upperLeft)
    {
      this.anchorPxX = x;
      this.anchorPxY = y;
      this.pixelAnchorUpperLeft = upperLeft;
      this.usePixelAnchor = true;
      return this;
    }
    public GeomTextBuilder nonDynamicTextRenderer(TextRenderer nonDynamicTextRenderer)
    {
      this.nonDynamicTextRenderer = nonDynamicTextRenderer;
      return this;
    }

    public GeomTextBuilder justify(int justifyX, int justifyY)
    {
      this.justifyX = justifyX;
      this.justifyY = justifyY;
      return this;
    }

    public GeomTextBuilder exactBounds(boolean exactPixelBounds)
    {
      this.exactPixelBounds = exactPixelBounds;
      return this;
    }

    public GeomTextBuilder exactPadding(float paddingX, float paddingY)
    {
      return padding(paddingX, paddingY, true);
    }

    public GeomTextBuilder percentagePadding(float paddingX, float paddingY)
    {
      return padding(paddingX, paddingY, false);
    }

    public GeomTextBuilder padding(float paddingX, float paddingY, boolean exactPadding)
    {
      this.paddingX = paddingX;
      this.paddingY = paddingY;
      this.exactPadding = exactPadding;
      this.usePadding = true;
      return this;
    }

    public GeomTextBuilder backgroundColor(Colorf backgroundColor)
    {
      this.backgroundColor = backgroundColor;
      return this;
    }

    public GeomTextBuilder textColor(Colorf textColor)
    {
      this.textColor = textColor;
      return this;
    }

    public GeomTextBuilder constrainByHeight(float height)
    {
      this.height = height;
      this.width = -1f;
      return this;
    }

    public GeomTextBuilder constrainByWidth(float width)
    {
      this.height = -1f;
      this.width = width;
      return this;
    }

    public GeomTextBuilder constrain(float width, float height)
    {
      this.height = height;
      this.width = width;
      return this;
    }

    public GeomText build()
    {
      if (this.usePixelAnchor == true)
      {
        System.out.println("HERE");
        return new GeomText(anchorPxX, anchorPxY, pixelAnchorUpperLeft, this);
      }
      else
      {
        return new GeomText(anchorPt, this);
      }
    }
  }

  public void setFont(String fontString, int fontStyle)
  {
    this.textRenderers = FontHandler.getInstance().getFontFamily(fontString, fontStyle);
  }

  public void setFont(List<TextRenderer> trs)
  {
    this.textRenderers = trs;
  }

  public GeomText()
  {
  } //temp while we are cleaning stuff up... remove this soon! TODO

  public GeomText(Point3f anchorPt, GeomTextBuilder builder)
  {
    super(anchorPt, builder.width, builder.height);

    initializeConstraints(builder);
  }

  //this is broken-- need to think about width and height...
  public GeomText(int pxX, int pxY, boolean pixelAnchorUpperLeft, GeomTextBuilder builder)
  {
    super(pixelAnchorUpperLeft, pxX, pxY, (int)builder.width, (int)builder.height);

    name = "TEST";
    initializePixelConstraints(builder);
  }

  private void initializeConstraints(GeomTextBuilder builder)
  {
    initialize(builder);

  //constraints-- give a better name, width and height are confusing!
    if (builder.width <= 0f && builder.height <= 0f)
    {
      //this is illegal, default to a width and height of 1f
      this.w = 1f;
      this.h = 1f;
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

   
    
    if (builder.usePadding == true)
    {
      if (builder.exactPadding == true)
      {
        //System.out.println("exactPadding = true");
        this.paddingX = builder.paddingX;
        this.paddingY = builder.paddingY;
      }
      else //percentage padding
      {
        //System.out.println("exactPadding = false");
        this.paddingX = this.w * builder.paddingX;
        this.paddingY = this.h * builder.paddingY;
      }

      //System.out.println("orig w/h = " + this.w + "/" + this.h);
      this.w += this.paddingX * 2f;
      this.h += this.paddingY * 2f;

    // System.out.println("w/h = " + this.w + "/" + this.h);
    // System.out.println("padx/y = " + this.paddingX + "/" + this.paddingY);

    }

    if (builder.fitInBox == true)
    {
      switch (justifyX)
      {
        case 0:
          this.translate.x += (builder.boxWidth * .5f) - (this.w * .5f);
          break;
        case 1:
          this.translate.x += (builder.boxWidth) -= this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.translate.y += (builder.boxHeight * .5f) - (this.h * .5f);
          break;
        case 1:
          this.translate.y += (builder.boxHeight) -= this.h;
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
          this.translate.x -= this.w * .5f;
          break;
        case 1:
          this.translate.x -= this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.translate.y -= this.h * .5f;
          break;
        case 1:
          this.translate.y -= this.h;
          break;
        case -1:
          break;
      }
    }
     
  }


  private void initializePixelConstraints(GeomTextBuilder builder)
  {
    initialize(builder);

    if (builder.width <= 0f && builder.height <= 0f)
    {
      //this is illegal, default to a width and height of 1f
      this.w = 1f;
      this.h = 1f;
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

    adjustWidth(false, builder.anchorPxX, builder.anchorPxY, w, h);

 /*
    if (builder.usePadding == true)
    {
      if (builder.exactPadding == true)
      {
        //System.out.println("exactPadding = true");
        this.paddingX = builder.paddingX;
        this.paddingY = builder.paddingY;
      }
      else //percentage padding
      {
        //System.out.println("exactPadding = false");
        this.paddingX = this.w * builder.paddingX;
        this.paddingY = this.h * builder.paddingY;
      }

      //System.out.println("orig w/h = " + this.w + "/" + this.h);
      this.w += this.paddingX * 2f;
      this.h += this.paddingY * 2f;

    // System.out.println("w/h = " + this.w + "/" + this.h);
    // System.out.println("padx/y = " + this.paddingX + "/" + this.paddingY);

    }

    if (builder.fitInBox == true)
    {
      switch (justifyX)
      {
        case 0:
          this.translate.x += (builder.boxWidth * .5f) - (this.w * .5f);
          break;
        case 1:
          this.translate.x += (builder.boxWidth) -= this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.translate.y += (builder.boxHeight * .5f) - (this.h * .5f);
          break;
        case 1:
          this.translate.y += (builder.boxHeight) -= this.h;
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
          this.translate.x -= this.w * .5f;
          break;
        case 1:
          this.translate.x -= this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.translate.y -= this.h * .5f;
          break;
        case 1:
          this.translate.y -= this.h;
          break;
        case -1:
          break;
      }
    }
  */
  }


  private void initialize(GeomTextBuilder builder)
  {
    setColor(builder.textColor);
    this.backgroundColor = builder.backgroundColor;

    this.text = builder.text;
    this.exactPixelBounds = builder.exactPixelBounds;

    if (builder.nonDynamicTextRenderer != null)
    {
      this.useNonDynamicTextRenderer = true;
      this.textRenderer = builder.nonDynamicTextRenderer;
      System.out.println("here nonDynamicTextRenderer = " + builder.nonDynamicTextRenderer);
      System.out.println("so textRenderer = " + textRenderer);
    }

    this.justifyX = builder.justifyX;
    this.justifyY = builder.justifyY;

    if (builder.textRenderers != null)
    {
      setFont(builder.textRenderers);
    }
    else
    {
      setFont(FontHandler.getInstance().getDefaultFontFamily());
    }
    
  }
 
  /** This method calucaltes the pixel width and height without taking into
   * consideration any rotations. Looks good, except the scale part might be a bit funny.
   * Should investigate later!
   *
   * @param gl
   */
  private void calculateUnrotatedPixelWidthAndHeightBILLBOARD(GL gl) //billboardSphericalBegin(
  //	float camX, float camY, float camZ,
  //	float objPosX, float objPosY, float objPosZ) {
  {
    //float[] lookAt = new float[3];
    //float[] objToCamProj = new float[3];
    //float[] upAux = new float[3];


    float angleCosine;

    //Cam cam = Behaviorism.renderer.cam;
    double[] temp_mv = Arrays.copyOf(RenderUtils.getCamera().modelview, 16);
    double[] temp_pj = RenderUtils.getCamera().projection;
    int[] temp_vp = RenderUtils.getCamera().viewport;



    //glPushMatrix();

// objToCamProj is the vector in world coordinates from the
// local origin to the camera projected in the XZ plane
//	objToCamProj[0] = cam.translate.x - this.translate.x ;
//	objToCamProj[1] = 0;
//	objToCamProj[2] = cam.translate.z - this.translate.z ;

    Vector3f objToCamProj = new Vector3f(RenderUtils.getCamera().translate.x - this.translate.x, 0f, RenderUtils.getCamera().translate.z - this.translate.z);
// This is the original lookAt vector for the object
// in world coordinates
    Vector3f lookAt = new Vector3f(0f, 0f, 1f);
//  lookAt[0] = 0;
//	lookAt[1] = 0;
//	lookAt[2] = 1;


    objToCamProj.normalize();
// normalize both vectors to get the cosine directly afterwards
    //mathsNormalize(objToCamProj);

// easy fix to determine wether the angle is negative or positive
// for positive angles upAux will be a vector pointing in the
// positive y direction, otherwise upAux will point downwards
// effectively reversing the rotation.

    Vector3f upAux = new Vector3f();
    upAux.cross(lookAt, objToCamProj);
    //mathsCrossProduct(upAux,lookAt,objToCamProj);

// compute the angle
    //angleCosine = mathsInnerProduct(lookAt,objToCamProj);
    angleCosine = lookAt.dot(objToCamProj);
// perform the rotation. The if statement is used for stability reasons
// if the lookAt and objToCamProj vectors are too close together then
// |angleCosine| could be bigger than 1 due to lack of precision
//   if ((angleCosine < 0.99990) && (angleCosine > -0.9999))
//      glRotatef(acos(angleCosine)*180/3.14,upAux[0], upAux[1], upAux[2]);


    temp_mv = MatrixUtils.rotate(temp_mv, Math.toDegrees(Math.acos(angleCosine)), upAux.x, upAux.y, upAux.z);

// so far it is just like the cylindrical billboard. The code for the
// second rotation comes now
// The second part tilts the object so that it faces the camera

// objToCam is the vector in world coordinates from
// the local origin to the camera
//	objToCam[0] = camX - objPosX;
//	objToCam[1] = camY - objPosY;
//	objToCam[2] = camZ - objPosZ;
    Vector3f objToCam = new Vector3f(RenderUtils.getCamera().translate.x - translate.x, RenderUtils.getCamera().translate.y - translate.y, RenderUtils.getCamera().translate.z - translate.z);

// Normalize to get the cosine afterwards
    objToCam.normalize();
//	mathsNormalize(objToCam);

// Compute the angle between objToCamProj and objToCam,
//i.e. compute the required angle for the lookup vector

    //angleCosine = mathsInnerProduct(objToCamProj,objToCam);
    angleCosine = objToCamProj.dot(objToCam);

    if (objToCam.x < 0)
    {
      temp_mv = MatrixUtils.rotate(temp_mv, Math.toDegrees(Math.acos(angleCosine)), 1, 0, 0);
    }
    else
    {
      temp_mv = MatrixUtils.rotate(temp_mv, Math.toDegrees(Math.acos(angleCosine)), -1, 0, 0);
    }


    gl.glPushMatrix();
    temp_mv = MatrixUtils.translate(temp_mv, translate.x, translate.y, translate.z);

    gl.glLoadMatrixd(temp_mv, 0);
    gl.glColor4f(0f, 0f, 1f, 1f);
    drawRect(gl, 0f);
    gl.glPopMatrix();


    this.pxWidth = (int) (RenderUtils.getWidthOfObjectInPixels(this, this.paddingX * 2f, temp_mv, temp_pj, temp_vp));
    //this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(this, this.insetY, temp_mv, temp_pj, temp_vp));
    this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(this, this.paddingY * 2f, temp_mv, temp_pj, temp_vp));
    this.pxX = (int) (RenderUtils.getXOfObjectInPixels(this, temp_mv, temp_pj, temp_vp));
    this.pxY = (int) (RenderUtils.getYOfObjectInPixels(this, temp_mv, temp_pj, temp_vp));

    //System.out.println("pxX / pxY = " + pxX + "/" + pxY);

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

    Geom upp = this;
    while (upp.parent != null)
    {
      temp_mv = MatrixUtils.scale(temp_mv, upp.scale.x, upp.scale.y, upp.scale.z);
      upp = upp.parent;
    }

    temp_mv = MatrixUtils.translate(temp_mv, 0f, 0f, (float) avgdist);

    double[] temp_pj = RenderUtils.getCamera().projection;
    int[] temp_vp = RenderUtils.getCamera().viewport;

    this.pxWidth = (int) (RenderUtils.getWidthOfObjectInPixels(this, this.paddingX * 2f, temp_mv, temp_pj, temp_vp));
    this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(this, this.paddingY * 2f, temp_mv, temp_pj, temp_vp));

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
    //timer.resetTime();
    calculateUnrotatedPixelWidthAndHeight(gl);
    //System.out.println("time to calc = " + timer.resetTime());
    {
      if (this.pxWidth != this.prevPxWidth || this.pxHeight != this.prevPxHeight || this.textRenderer == null)
      {
        //System.out.println("pxWidth/pxHeight = " + pxWidth + "/" + pxHeight);

        this.prevPxWidth = this.pxWidth;
        this.prevPxHeight = this.pxHeight;

        if (useNonDynamicTextRenderer == true)
        {
          calculateBoundsUsingSpecficTextRenderer(this.textRenderer);
        }
        else
        {
          //timer.resetTime();
          chooseFont();
        //System.out.println("time to chooeFont = " + timer.resetTime());
        }
        tty = (float) stringBounds.getY() * scaleVal;
        tth = (float) (stringBounds.getHeight()) * scaleVal;

        useY = paddingY + tth + tty;
        useX = paddingX;

      }
    }

    if (this.backgroundColor != null)
    {
      gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);

      drawRect(gl, offset);
    }

    //System.out.println("using textRenderer: " + this.textRenderer.getFont());
    textRenderer.begin3DRendering();
    textRenderer.setColor(color.toJavaColor());
    textRenderer.draw3D(this.text, useX, useY, offset, this.scaleVal);
    textRenderer.end3DRendering();

  //textRenderer.flush();
  //System.out.println("time to draw = " + timer.resetTime());

  }

  private void drawRect(GL gl, float offset)
  {
    gl.glBegin(gl.GL_QUADS);
    gl.glVertex3f(0f, 0f, offset);
    gl.glVertex3f(w, 0f, offset);
    gl.glVertex3f(w, 0f + h, offset);
    gl.glVertex3f(0f, 0f + h, offset);
    gl.glEnd();
  }

  @Override
  public void drawPickingBackground(GL gl)
  {
    gl.glColor4f(0f, 0f, 0f, 0f);
    drawRect(gl, 0f);
  }

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

  public void chooseFont()
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
        this.scaleVal = (this.w - (paddingX * 2f)) / curWidth;
        return;
      }

      //try next one
      this.textRenderer = textRenderers.get(i);
      this.stringBounds = nextBounds;
      curWidth = nextWidth;
    }

    //use biggest one
    this.scaleVal = (this.w - (paddingX * 2f)) / curWidth;
    return;

  }

  public void setText(String text)
  {
    this.text = text;
  }

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
