/* GeomTextOutset.java ~ Oct 6, 2008 */
package geometry.text;

import renderers.RendererJogl;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import behaviorism.BehaviorismDriver;
import geometry.Colorf;
import geometry.GeomPoint;
import geometry.GeomRect;
import geometry.RotateEnum;
import geometry.ScaleEnum;
import handlers.FontHandler;
import java.util.List;
import utils.DebugTimer;
import utils.MatrixUtils;

public class GeomTextOutset extends GeomRect
{
  //public String useFont=null;
  public List<TextRenderer> textRenderers = FontHandler.getInstance().textRenderers;

  public String text = "";
  public GeomPoint centerPoint = new GeomPoint();
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
  protected Rectangle2D stringBoundsX = null;
  protected Rectangle2D stringBoundsY = null;
  protected TextRenderer textRendererX = null;
  protected TextRenderer textRendererY = null;
  protected Rectangle2D.Float boundsInsets = new Rectangle2D.Float();
  protected Rectangle2D.Float boundsTextBackground = new Rectangle2D.Float();
  public float maxw = 1f;
  public float maxh = 1f;
  protected float xpos = 0f;
  protected float ypos = 0f;
  protected float scaleValX = 1f;
  protected float scaleValY = 1f;
  public int justifyX = 0; //0 center, -1 left, +1 right
  public int justifyY = 0; //0 center, -1 bottom, +1 top
  public float insetX = 0f;
  public float insetY = 0f;
  public float outsetX = 0f;
  public float outsetY = 0f;
  public Colorf insetColor = null; //background of insets
  public Colorf backgroundColor = null; //background of entire bounds
  public Colorf textBackgroundColor = null; //background of exact text bounds
  //public TextRenderer nonDynamicTextRenderer = null;
  public boolean isFirstTime = true;
  public boolean exactPixelBounds = false;
  public boolean useNonDynamicTextRenderer = false;
  public float paddingX = 0f;
  public float paddingY = 0f;

  /*
  things we have to worry about:
  
  font-family
  
  background color / text color
  padding --> exact or percentage
  
  tight bounds or regualr bounds
  
  constrain by height/width
  
  justify x/y
   */
  public static class GeomTextBuilder
  {

    private String text;
    private Point3f anchorPt = new Point3f();
    private float width = 1f;
    private float height = 1f;
    private int justifyX = 0;
    private int justifyY = 0;
    private boolean usePadding = false;
    private boolean exactPadding = false;
    private float paddingX = 0f;
    private float paddingY = 0f;
    private Colorf backgroundColor = new Colorf();
    private Colorf textColor = new Colorf();
    private TextRenderer nonDynamicTextRenderer = null;
    private boolean exactPixelBounds = false;
    private float boxWidth = -1f;
    private float boxHeight = -1f;
    private boolean fitInBox = false;
    private String fontString = null;

    public GeomTextBuilder(String text)
    {
      this.text = text;
    }

    public GeomTextBuilder font(String font)
    {
      fontString = font;
      return this;
    }

    public GeomTextBuilder fitInBox(float bw, float bh)
    {
      this.boxWidth = bw;
      this.boxHeight = bh;
      this.fitInBox = true;
      return this;
    }

    public GeomTextBuilder anchor(Point3f anchorPt)
    {
      this.anchorPt = anchorPt;
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

    public GeomTextOutset build()
    {
      GeomTextOutset gto = new GeomTextOutset(this);
      /*
      if (usePadding == true)
      {
      GeomRect gr;
      if (this.exactPadding == true)
      {
      gr = new GeomRect(-paddingX, -paddingY, -.01f, gto.w + (paddingX * 2f), gto.h + (paddingY * 2f));
      }
      else //percentange padding
      {
      float padX = gto.w * paddingX;
      float padY = gto.h * paddingY;
      gr = new GeomRect(-padX, -padY, -.01f, gto.w + (padX * 2f), gto.h + (padY * 2f));
      }
      
      
      gr.setColor(gto.backgroundColor);
      gr.state = new State();
      gr.state.DEPTH_TEST = false;
      gr.state.BLEND = false;
      
      //gto.state = new State();
      //gto.state.DEPTH_TEST = false;
      
      gr.registerObject(gto);
      gto.addGeom(gr);
      
      
      }
       */
      return gto;
    }
  }


  public void setFont(String fontString)
  {
    this.textRenderers = FontHandler.getInstance().fontFamilyMap.get(fontString);
    FontHandler.getInstance().fontsReady.set(true);
  }

  public GeomTextOutset(GeomTextBuilder builder)
  {
    super(builder.anchorPt, builder.width, builder.height);

    setColor(builder.textColor);
    this.backgroundColor = builder.backgroundColor;

    this.text = builder.text;
    this.exactPixelBounds = builder.exactPixelBounds;

    if (builder.nonDynamicTextRenderer != null)
    {
      this.useNonDynamicTextRenderer = true;
      this.textRenderer = builder.nonDynamicTextRenderer;
      System.out.println("here, nonDynamicTextRenderer = " + builder.nonDynamicTextRenderer);
      System.out.println("so textRenderer = " + textRenderer);
    }

    this.justifyX = builder.justifyX;
    this.justifyY = builder.justifyY;

    if (builder.fontString != null)
    {
      setFont(builder.fontString);
    }
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
          this.anchor.x += (builder.boxWidth * .5f) - (this.w * .5f);
          break;
        case 1:
          this.anchor.x += (builder.boxWidth) -= this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.anchor.y += (builder.boxHeight * .5f) - (this.h * .5f);
          break;
        case 1:
          this.anchor.y += (builder.boxHeight) -= this.h;
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
          this.anchor.x -= this.w * .5f;
          break;
        case 1:
          this.anchor.x -= this.w;
          break;
        case -1:
          break;
      }
      switch (justifyY)
      {
        case 0:
          this.anchor.y -= this.h * .5f;
          break;
        case 1:
          this.anchor.y -= this.h;
          break;
        case -1:
          break;
      }

    }


  }
  //ANGUS-- need to handle quotes properly
  //they are children of the main GT2 and their
  //rotate point is actually the dist to the center of their parent.
  //show some rect to see what is going on...

  /** This method calucaltes the pixel width and height without taking into
   * consideration any rotations. Looks good, except the scale part might be a bit funny.
   * Should investigate later!
   *
   * @param gl
   */
  protected void calculateUnrotatedPixelWidthAndHeight(GL gl)
  {
    gl.glPushMatrix();

    //System.out.println("rotateanchor = " + rotateAnchor);
    Point3d newp;
    float world_z;

    if (parent == null)
    {
      //newp = new Point3d(rotateAnchor.anchor.x, rotateAnchor.anchor.y, 0f);
      newp = new Point3d(w * .5f, h * .5f, 0f);
      world_z = (float) (MatrixUtils.getGeomPointInWorldCoordinates(newp, modelview, RendererJogl.modelviewMatrix)).z;

    }
    else
    {
      newp = new Point3d(parent.w * .5f, parent.h * .5f, 0f);
      //newp = new Point3d();
      world_z = (float) (MatrixUtils.getGeomPointInWorldCoordinates(newp, parent.modelview, RendererJogl.modelviewMatrix)).z;
    //world_z = (float) (MatrixUtils.getGeomPointInGeomCoordinates(newp, modelview, parent.modelview)).z;
    //System.out.println("world_z for child = " + world_z);
    }

    BehaviorismDriver.renderer.resetPerspective3D();

    translate(gl, -w / 2f, -h / 2f, (float) (world_z));

    //Scaling might be a bit wonky!!!!!!! it is for sure...
    if (parent != null)
    {
      parent.scale(gl);
    }
    scale(gl);

    double[] temp_mv = MatrixUtils.getIdentity();
    double[] temp_pj = MatrixUtils.getIdentity();
    int[] temp_vp = new int[4];

    gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, temp_mv, 0);
    gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, temp_pj, 0);
    gl.glGetIntegerv(gl.GL_VIEWPORT, temp_vp, 0);

    //this.pxWidth = (int) (BehaviorismDriver.renderer.getWidthOfObjectInPixels(this, this.insetX, temp_mv, temp_pj, temp_vp));
    this.pxWidth = (int) (BehaviorismDriver.renderer.getWidthOfObjectInPixels(this, this.paddingX * 2f, temp_mv, temp_pj, temp_vp));
    //this.pxHeight = (int) (BehaviorismDriver.renderer.getHeightOfObjectInPixels(this, this.insetY, temp_mv, temp_pj, temp_vp));
    this.pxHeight = (int) (BehaviorismDriver.renderer.getHeightOfObjectInPixels(this, this.paddingY * 2f, temp_mv, temp_pj, temp_vp));

//    int pxWidth2 = (int) (BehaviorismDriver.renderer.getWidthOfObjectInPixels(this, 0f, temp_mv, temp_pj, temp_vp));
//		int pxHeight2 = (int) (BehaviorismDriver.renderer.getHeightOfObjectInPixels(this, 0f, temp_mv, temp_pj, temp_vp));
//
//    System.out.println("inset x " + insetX + ", inset diff X = " + (pxWidth - pxWidth2));
//    System.out.println("inset y " + insetY + ", inset diff Y = " + (pxHeight - pxHeight2));
//    
    this.pxX = (int) (BehaviorismDriver.renderer.getXOfObjectInPixels(this, temp_mv, temp_pj, temp_vp));
    this.pxY = (int) (BehaviorismDriver.renderer.getYOfObjectInPixels(this, temp_mv, temp_pj, temp_vp));

    gl.glPopMatrix();
  }
  float tempypos = 0f;
  float tty = 0f;
  float tth = 0f;
  float useY = 0f;
  float useX = 0f;

  //DebugTimer timer = new DebugTimer();
  @Override
  public void draw(GL gl, GLU glu, float offset)
  {
    //timer.resetTime();
    calculateUnrotatedPixelWidthAndHeight(gl);
    //System.out.println("time to calc = " + timer.resetTime());

    {
      if (FontHandler.getInstance().fontsReady.get() == true || this.pxWidth != this.prevPxWidth || this.pxHeight != this.prevPxHeight ||
        this.textRenderer == null)
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
    renderInvisiblePickingBackground(gl, offset);

    //dont' really want this here anymore, but it is good for testing.
    //real way shoudl be to make a composite object.. think about...
    if (this.backgroundColor != null)
    {
      gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);

      gl.glBegin(gl.GL_POLYGON);
      gl.glVertex3f(xpos, ypos, offset);
      gl.glVertex3f(xpos + w, ypos, offset);
      gl.glVertex3f(xpos + w, ypos + h, offset);
      gl.glVertex3f(xpos, ypos + h, offset);
      gl.glEnd();

    /*
    float bgc_z = offset;
    gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
    
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(0f - (outsetX * .5f), 0f - (outsetY * .5f), bgc_z);
    gl.glVertex3f(this.w + (outsetX * .5f), 0f - (outsetY * .5f), bgc_z);
    gl.glVertex3f(this.w + (outsetX * .5f), this.h + (outsetY * .5f), bgc_z);
    gl.glVertex3f(0f - (outsetX * .5f), this.h + (outsetY * .5f), bgc_z);
    gl.glEnd();
     */
    }

    /*
    if (this.insetColor != null)
    {
    gl.glColor4f(insetColor.r, insetColor.g, insetColor.b, insetColor.a);
    
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(boundsInsets.x, boundsInsets.y, 0f);
    gl.glVertex3f(boundsInsets.x + boundsInsets.width, boundsInsets.y, 0f);
    gl.glVertex3f(boundsInsets.x + boundsInsets.width, boundsInsets.y + boundsInsets.height, 0f);
    gl.glVertex3f(boundsInsets.x, boundsInsets.y + boundsInsets.height, 0f);
    gl.glEnd();
    }
    
    if (textBackgroundColor != null)
    {
    gl.glColor4f(textBackgroundColor.r, textBackgroundColor.g, textBackgroundColor.b, textBackgroundColor.a);
    
    gl.glBegin(gl.GL_POLYGON);
    gl.glVertex3f(boundsTextBackground.x, boundsTextBackground.y, 0f);
    gl.glVertex3f(boundsTextBackground.x + boundsTextBackground.width, boundsTextBackground.y, 0f);
    gl.glVertex3f(boundsTextBackground.x + boundsTextBackground.width, boundsTextBackground.y + boundsTextBackground.height, 0f);
    gl.glVertex3f(boundsTextBackground.x, boundsTextBackground.y + boundsTextBackground.height, 0f);
    gl.glEnd();
    }
     */


    //System.out.println("using textRenderer: " + textRenderer.getFont());
    //float useY = paddingY + diffY;
    //textRenderer.setUseVertexArrays(true);
    //textRenderer.setUseVertexArrays(false);
    //gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);


    textRenderer.begin3DRendering();
    textRenderer.setColor(this.r, this.g, this.b, this.a);
    textRenderer.draw3D(this.text, useX, useY, offset, this.scaleVal);
    textRenderer.end3DRendering();

//    textRenderer.flush();
   // System.out.println("time to draw = " + timer.resetTime());

  }

  /** render invisibly for mouse picking */
  public void renderInvisiblePickingBackground(GL gl, float offset)
  {
    //if (depthTest == false && isSelectable == true)
    if (isSelectable == true)
    {
      gl.glEnable(GL.GL_DEPTH_TEST);

      float bgc_z = -offset;
      //gl.glColor4f(0f, 1f, 0f, 1f);
      gl.glColor4f(0f, 0f, 0f, 0f);

      gl.glBegin(gl.GL_POLYGON);
      gl.glVertex3f(xpos, ypos, offset);
      gl.glVertex3f(xpos + w, ypos, offset);
      gl.glVertex3f(xpos + w, ypos + h, offset);
      gl.glVertex3f(xpos, ypos + h, offset);
      gl.glEnd();

      /*
      gl.glBegin(gl.GL_POLYGON);
      gl.glVertex3f(0f, 0f, bgc_z);
      gl.glVertex3f(this.w, 0f, bgc_z);
      gl.glVertex3f(this.w, this.h, bgc_z);
      gl.glVertex3f(0f, this.h, bgc_z);
      gl.glEnd();
       */
      gl.glDisable(GL.GL_DEPTH_TEST);
    }
  }

  public void calculateBoundsUsingSpecficTextRenderer(TextRenderer textRenderer)
  {
    System.out.println("in calculateBoundsUsingSpecficTextRenderer()");
    FontRenderContext frc1 = textRenderer.getFontRenderContext();
    Font font1 = textRenderer.getFont();

    Rectangle2D bounds1;

    if (exactPixelBounds == true)
    {
      GlyphVector gv1 = font1.createGlyphVector(frc1, this.text);
      this.stringBounds = gv1.getPixelBounds(null, 0f, 0f);
    }
    else
    {
      this.stringBounds = font1.getStringBounds(this.text, frc1);
    }

    scaleValX = (this.w - (paddingX * 2f)) / (float) this.stringBounds.getWidth();
    scaleValY = (this.h - (paddingY * 2f)) / (float) this.stringBounds.getHeight();

    if (scaleValX < scaleValY)
    {
      this.scaleVal = scaleValX;
    }
    else
    {
      this.scaleVal = scaleValY;
    }
  }

  public void chooseFont()
  {
    //get correct font size for the current pixel w/h of the Geom
    //for (int i = 1; i < FontHandler.getInstance().textRenderers.size(); i++)
    for (int i = 1; i < textRenderers.size(); i++)
    {
      boolean readyToBreakX = false;
      boolean readyToBreakY = false;
      scaleValX = 1f;
      scaleValY = 1f;

      //TextRenderer tr1 = (FontHandler.getInstance().textRenderers.get(i - 1));
      TextRenderer tr1 = (textRenderers.get(i - 1));
      FontRenderContext frc1 = tr1.getFontRenderContext();
      Font font1 = tr1.getFont();

      Rectangle2D bounds1;

      if (exactPixelBounds == true)
      {
        GlyphVector gv1 = font1.createGlyphVector(frc1, this.text);
        bounds1 = gv1.getPixelBounds(null, 0f, 0f);
      }
      else
      {
        bounds1 = font1.getStringBounds(this.text, frc1);
      }
      float strw1 = (float) (bounds1.getWidth());
      float strh1 = (float) (bounds1.getHeight());

      //TextRenderer tr2 = (FontHandler.getInstance().textRenderers.get(i));
      TextRenderer tr2 = (textRenderers.get(i));
      FontRenderContext frc2 = tr2.getFontRenderContext();
      Font font2 = tr2.getFont();

      Rectangle2D bounds2;
      if (exactPixelBounds == true)
      {
        GlyphVector gv2 = font2.createGlyphVector(frc2, this.text);
        bounds2 = gv2.getPixelBounds(null, 0f, 0f);
      }
      else
      {
        bounds2 = font2.getStringBounds(this.text, frc1);
      }
      //GlyphVector gv2 = font2.createGlyphVector(frc2, this.text);
      //Rectangle2D bounds2 = gv2.getPixelBounds(null, 0f, 0f);
      float strw2 = (float) (bounds2.getWidth());
      float strh2 = (float) (bounds2.getHeight());

      //System.out.println("trying " + tr1.getFont());
      //System.out.printf("strw avg / strh avg = %f / %f \n", ((strw1 + strw2) / 2f), ((strh1 + strh2) / 2f));
      //if (strw2 > pxWidth)
      if ((strw1 + strw2) / 2f > pxWidth)
      {
        textRendererX = tr1;
        stringBoundsX = bounds1;
        scaleValX = (this.w - (paddingX * 2f)) / (float) (bounds1.getWidth());
        //scaleValX = (this.w * (1f - insetX)) / (float) (bounds1.getWidth());
        //scaleValX = ((this.w - .1f) * (1f)) / (float) (bounds1.getWidth());

        //System.out.printf("this.w = %f insetX = %f b.w = %f\n", this.w, insetX, bounds1.getWidth());
        //System.out.printf("(%f * (1f - %f) / %f  :  %f / %f\n", this.w, insetX, bounds1.getWidth(), (this.w * (1f - insetX)) , bounds1.getWidth());
        //System.out.println("scaleVal = " + scaleValX + " WIDTH");
        readyToBreakX = true;
      }
      //if (strh2 > pxHeight)
      if ((strh1 + strh2) / 2f > pxHeight)
      {
        textRendererY = tr1;
        stringBoundsY = bounds1;
        //scaleValY = (this.h * (1f - insetY)) / (float) bounds1.getHeight();
        scaleValY = (this.h - (paddingY * 2f)) / (float) bounds1.getHeight();
        //scaleValY = ((this.h - .1f) * (1f)) / (float) bounds1.getHeight();
        //System.out.println("scaleVal = " + scaleValY + " HEIGHT");
        readyToBreakY = true;
      }

      if (readyToBreakX == true && readyToBreakY == false)
      {
        //System.out.println("X true");
        this.scaleVal = scaleValX;
        this.textRenderer = textRendererX;
        this.stringBounds = stringBoundsX;
        break;
      }
      else if (readyToBreakX == false && readyToBreakY == true)
      {
        //System.out.println("Y true");
        this.scaleVal = scaleValY;
        this.textRenderer = textRendererY;
        this.stringBounds = stringBoundsY;
        break;
      }
      else if (readyToBreakX == true && readyToBreakY == true)
      {
        //System.out.println("XY true");
        if (scaleValX < scaleValY)
        {
          this.scaleVal = scaleValX;
          this.textRenderer = textRendererX;
          this.stringBounds = stringBoundsX;
        //System.out.println("using X");
        }
        else
        {
          this.scaleVal = scaleValY;
          this.textRenderer = textRendererY;
          this.stringBounds = stringBoundsY;
        //System.out.println("using Y");
        }
        break;

      }


      //should actually check whether to use w or h here...
      //if (i == FontHandler.getInstance().textRenderers.size() - 1) //use biggest one
      if (i == textRenderers.size() - 1) //use biggest one
      {
        this.textRenderer = tr2;
        this.stringBounds = bounds2;

        //this.scaleVal = (this.w * (1f - insetX)) / (float) (bounds2.getWidth());
        //this.scaleVal = (this.w) / (float) (bounds2.getWidth());
        //this.scaleVal = (this.w - (paddingX * 2f)) / (float) (bounds2.getWidth());
        this.scaleVal = (this.w - (paddingX * 2f)) / (float) (bounds2.getWidth());

        //System.out.println("we are here!");
        break;
      //return;
      }

      if (textRendererX != null)
      {
        //textRendererX.dispose();
        textRendererX = null;
      }
      if (textRendererY != null)
      {
        //textRendererY.dispose();
        textRendererY = null;
      }


    }

  //debug
  //System.out.println("using font " + this.textRenderer.getFont());
  }

  /*
  //private setScaleVal(
  private void justifyText()
  {
  //get exact X position based on justification and exact glyphs
  if (this.justifyX == 1) //right justify
  {
  float strW = (float) stringBounds.getWidth() * scaleVal;
  //xpos = hx + this.w - strW;
  this.xpos = 0f + this.w - strW;
  
  //Perc vs Abs justify...
  this.xpos -= (this.w * (paddingX / 2f)); //perc
  //this.xpos -= (insetX / 2f); //abs
  
  }
  else if (this.justifyX == 0) //center justify
  {
  System.out.println("CENTER justify X");
  float strW = (float) stringBounds.getWidth() * scaleVal;
  float centerIncX = (strW * .5f);
  //this.xpos = ((this.w * .5f) - (centerIncX) - paddingX);
  //this.xpos = this.x - (this.w * .5f);
  
  //this.anchor.x -= (this.w * .5f);
  }
  else //left justify
  {
  //xpos = hx;
  this.xpos = 0f;
  this.xpos += (this.w * (insetX / 2f));
  
  }
  
  
  float ttx = (float) stringBounds.getX() * scaleVal;
  float ttw = (float) stringBounds.getWidth() * scaleVal;
  
  //what is this???
  //    System.out.println("ttx?? = " + ttx);
  //    this.xpos -= ttx;
  
  //get exact Y position based on justification and exact glyphs
  if (this.justifyY == 1) //top justify
  {
  float strH = (float) stringBounds.getHeight() * scaleVal;
  //ypos = this.anchor.y + this.h - strH;
  //ypos = hy + this.h - strH;
  this.ypos = this.h - strH;
  this.ypos -= (this.h * (insetY / 2f));
  }
  else
  {
  if (this.justifyY == 0) //center height
  {
  float strH = (float) stringBounds.getHeight() * scaleVal;
  float centerIncY = (strH * .5f);
  //ypos = (hy + (this.h * .5f) - (centerIncY) );
  //this.ypos = (0f + (this.h * .5f) - (centerIncY));
  this.ypos = (0f + (this.h * .5f) - (centerIncY) - paddingY);
  }
  else
  {
  //ypos = hy;
  this.ypos = 0f;
  this.ypos += (this.h * (insetY / 2f));
  }
  }
  
  float tty = (float) stringBounds.getY() * scaleVal;
  float tth = (float) stringBounds.getHeight() * scaleVal;
  float diffy = tth + tty;
  this.ypos += diffy;
  
  //this.boundsInsets.setRect((this.w * insetX) / 2f, (this.h * insetY) / 2f,
  //				this.w * (1f - insetX), this.h * (1f - insetY));
  this.boundsInsets.setRect(this.w - (insetX / 2f), this.h - (insetY / 2f),
  this.w - insetX, this.h - insetY);
  this.boundsTextBackground.setRect(this.xpos + ttx, this.ypos - diffy,
  ttw, tth);
  
  }
   */
  public void setText(String text)
  {
    this.text = text;
  }

  public String toString()
  {
    return "GeomText2 [" + text + "] : x/y/z/w = " + anchor.x + "/" + anchor.y + "/" + anchor.z + "/" + scale.x + ", justify = " + justifyY;
  }

  public void setWidthAndHeight(float maxw, float maxh)
  {
    TextRenderer fi = FontHandler.getInstance().getLargestTextRenderer();

    FontRenderContext frc = fi.getFontRenderContext();
    Font font = fi.getFont();

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

    TextRenderer fi = FontHandler.getInstance().getLargestTextRenderer();

    FontRenderContext frc = fi.getFontRenderContext();
    Font font = fi.getFont();

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
    //this shouldn't happen-- textRenderers is somehow not being synchronized properly
    //if (FontHandler.textRenderers.size() == 0)
    {
      //return;
    }

    this.w = w;
    //TextRenderer fi = FontHandler.getInstance().textRenderers.get(FontHandler.getInstance().textRenderers.size() - 1); //ie largest one
    TextRenderer fi = textRenderers.get(FontHandler.getInstance().textRenderers.size() - 1); //ie largest one
    FontRenderContext frc = fi.getFontRenderContext();
    Font font = fi.getFont();

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

  @Override
  public void dispose()
  {
    super.dispose();

    if (textRendererX != null)
    {
      textRendererX = null;
    }
    if (textRendererY != null)
    {
      textRendererY = null;
    }
    if (textRenderer != null)
    {
      textRenderer = null;
    }
  }
}
