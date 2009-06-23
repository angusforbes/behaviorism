/* GeomTextFlow2.java ~ Sep 4, 2008 */

package geometry.text;

import behaviors.geom.continuous.BehaviorScale;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import utils.Utils;

/**
 *
 * @author angus
 */
public class GeomTextFlow2 extends GeomText
{
  public boolean debugDrawBackground = false; //true
  public List<String> lines = new ArrayList<String>();
  public int pxHeightTaken = 0;
  public double pxMaxHeight = 0;
  public double pxMaxWidth = 0;
  public float maxDescent = 0f;
  public float maxAscent = 0f;
  public Font font_layout = null;
  public float prevFontSize = -1f;
  public int visibleChars = 1000000000; //default is all chars... 

  //text, w, y, lineh
  
  public static GeomTextFlow2 newGeomTextFlowWithAppearingCharacters(
    String text, Point3f p3f, float w, float h, long baseNano, long lengthMS)
  {
    final GeomTextFlow2 gtf = new GeomTextFlow2(p3f, w, h, text);
    gtf.visibleChars = 100000;
    gtf.setColor(1f, 1f, 1f, 1f);
    gtf.justifyX = -1;
    gtf.justifyY = 1;

    /*
    BehaviorGeomContinuous charBehavior =
      new BehaviorGeomContinuous(new ContinuousBehaviorBuilder(baseNano, lengthMS).range(1f))
      {

        float cp = 0f;

        public void updateGeom(Geom g)
        {
          cp += offsets[0];
          gtf.visibleChars = (int) (cp * (float) gtf.text.length());
          //System.out.println("gtf visibleChars = " + gtf.visibleChars);

          if (cp >= 1f)
          {
            isDone = true;
          }
        }
      };

    gtf.attachBehavior(charBehavior);
    */
    return gtf;
  }

  public GeomTextFlow2(Point3f p3f, float w, float h, String text)
  {
  //  super(p3f, w, h, text);
  }

  public GeomTextFlow2(float x, float y, float z, float w, float h, String text)
  {
    //super(x, y, z, w, h, text);
  }

  @Override
  public void doubleClickAction(MouseEvent e)
  {
    //this.attachBehavior( new BehaviorScale( utils.Utils.nowPlusMillis(0), 1000L, LoopEnum.ONCE, 1, 1, 1, 2, 2, 1 ) );
    this.attachBehavior(BehaviorScale.scale(Utils.nowPlusMillis(0L), 150L, new Point3f(2f, 2f, 0f)));
  }

  public boolean createLayout(TextRenderer renderer, boolean forceUseOfRenderer, float pxW, float pxH)
  {
    //System.out.println("in createLayout()");
    lines.clear();
    pxHeightTaken = 0;
    pxMaxWidth = 0;
    pxMaxHeight = 0;
    maxDescent = 0f;
    maxAscent = 0f;

    FontRenderContext frc_layout = renderer.getFontRenderContext();
    font_layout = renderer.getFont();

    Map attrs = new HashMap();
    attrs.put(TextAttribute.FONT, renderer.getFont());
    AttributedString str = new AttributedString(this.text, attrs);
    LineBreakMeasurer measurer = new LineBreakMeasurer(str.getIterator(), frc_layout);
    int curPos = 0;
    pxHeightTaken = 0;
    while (measurer.getPosition() < this.text.length())
    {
      //System.out.println("measurer.getPosition = " + measurer.getPosition());
      int nextPos = measurer.nextOffset(pxW);

      //System.out.println("nextPos = " + nextPos);
      String line = this.text.substring(curPos, nextPos);
      Rectangle2D bounds = renderer.getBounds(line);
      lines.add(line);
      pxHeightTaken += (int) bounds.getHeight();

      //if (pxHeightTaken > pxHeight * 1.5) //then this font size is too big
      if (pxHeightTaken > pxH * 1) //then this font size is too big
      {
        //exit now unless we are forcing the use of this font
        if (forceUseOfRenderer != true)
        {
          lines.clear();
          return false;
        }
      }
      curPos = nextPos;
      measurer.setPosition(curPos);

      //GlyphVector gv1 = font_layout.createGlyphVector(frc_layout, line);
      //Rectangle2D bounds1 = gv1.getPixelBounds(null, 0f, 0f);
      Rectangle2D bounds1 = font_layout.getStringBounds(line, frc_layout);

      LineMetrics lineMetrics_layout = font_layout.getLineMetrics(line, frc_layout);

      if (bounds1.getHeight() > pxMaxHeight)
      {
        pxMaxHeight = bounds1.getHeight();
      }
      if (bounds1.getWidth() > pxMaxWidth)
      {
        //System.out.println("line : " + line + ", bounds1 = " + bounds1);
        pxMaxWidth = bounds1.getWidth();
      }

      if (lineMetrics_layout.getDescent() > maxDescent)
      {
        maxDescent = lineMetrics_layout.getDescent();
      }

      if (lineMetrics_layout.getAscent() > maxAscent)
      {
        maxAscent = lineMetrics_layout.getAscent();
      }
    }


    if (pxMaxWidth < pxWidth * .95)
    {
      return false;
    }


    //then this font size fits, or we have chosen to force the use of this font
    return true;
  }

  public void setupTextRenderer()
  {
    //commenting out while switching to GTextOutset...
    /*
    this.scaleValX = (this.w * (1f)) / ((float) pxMaxWidth);
    this.scaleValY = ((this.h / lines.size()) * 1f) / ((float) pxMaxHeight);

    if (scaleValX < scaleValY)
    {
      this.scaleVal = scaleValX;
    }
    else
    {
      this.scaleVal = scaleValY;
    }
    */
  /*
  System.out.println("fontSize = " + font_layout.getSize() +
  " ; w/h = " + w + "/" + h +
  " ; pxWidth / pxHeight = " + pxWidth + "/" + pxHeight +
  " ; pxMaxWidth / pxMaxHeight = " + pxMaxWidth + "/" + pxMaxHeight +
  " ; maxAscent = " + maxAscent +
  " ; maxDescent = " + maxDescent +
  " ; scaleVal = " + scaleVal);
  //System.out.println("svX / svY = " + scaleValX + "/" + scaleValY);
  
  System.out.println("lines size = " + lines.size());
   */
  }

  public void draw(GL gl)
  {
    //commenting out while swtiching to new geomText-- need to revisit!!!!!
    /*
    //temp - draw background
    if (debugDrawBackground == true || backgroundColor != null)
    {
      if (backgroundColor != null)
      {
        gl.glColor4fv(backgroundColor.array(), 0);
      }
      else
      {
        gl.glColor4f(1f, 0f, 0f, .5f);

      }

      gl.glBegin(gl.GL_POLYGON);
      gl.glVertex3f(0f, 0f, 0f);
      gl.glVertex3f(w, 0f, 0f);
      gl.glVertex3f(w, h, 0f);
      gl.glVertex3f(0f, h, 0f);
      gl.glEnd();
    }

    insetX = 0f;
    insetY = 0f;
    this.pxWidth = (int) (RenderUtils.getWidthOfObjectInPixels(this, this.insetX));
    this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(this, this.insetY));

    Rectangle2D rect = RenderUtils.getScreenRectangleForWorldCoords(this);
    this.pxWidth = (int) rect.getWidth();
    this.pxHeight = (int) rect.getHeight();
    int pxX = (int) rect.getX();
    int pxY = (int) rect.getY();
    //calculateUnrotatedPixelWidthAndHeight(gl);

    if (FontHandler.getInstance().fontsReady.get() == true ||
      this.pxWidth != this.prevPxWidth ||
      this.pxHeight != this.prevPxHeight ||
      this.textRenderer == null)
    {

      if (FontHandler.getInstance().fontsReady.get() == true) //make sure to recalculate everything if font has changed
      {
        prevFontSize = -1f;
      }

      this.prevPxWidth = this.pxWidth;
      this.prevPxHeight = this.pxHeight;

      boolean doesItFit = false;

      for (int i = FontHandler.getInstance().textRenderers.size() - 1; i >= 0; i--)
      {
        TextRenderer tr1 = (FontHandler.getInstance().textRenderers.get(i));

        //System.out.println("*** \n " + tr1.getFont());
        //Rectangle2D bounds = tr1.getFont().getMaxCharBounds(tr1.getFontRenderContext()); //ie bounds of largest character-- capital "O" or whatever

        Rectangle2D bounds = tr1.getFont().getStringBounds(text, tr1.getFontRenderContext());

        float cw = (float) (bounds.getWidth());
        float ch = (float) (bounds.getHeight() - bounds.getY());

        System.out.println("cw/ch = " + cw + "/" + ch);
        //REAL
        float carea = (cw * ch);
        float xval = (float) Math.sqrt(carea / (w * h));

        
//        System.out.println("characters = " + text.length());
//        System.out.printf("max area w/h = %f/%f\n", cw, ch);
//        System.out.println("carea = " + carea);
//        System.out.println("xval = " + xval);
//        System.out.println("is " + carea + " < " + (pxHeight * pxWidth) + "?");
         
        if (carea < (pxWidth * pxHeight) * 2f || i == 0) //ie if it fits OR it is the last one
        {
          float currentFontSize = tr1.getFont().getSize();
          if (prevFontSize != currentFontSize)
          {
            //System.out.printf("pxWidth/pxHeight = %d/%d\n", pxWidth, pxHeight);
            //System.out.println("fpxWidth = " + (xval * w) + ", fpxHeight = " + (xval * h) );
            //System.out.println("CHANGING FONT SIZE FROM " + prevFontSize + " TO " + currentFontSize);

            //REAL
            createLayout(tr1, true, (xval * w), (xval * h));
            
            //TEMP
            //createLayout(tr1, true, (200 * w), (200 * h));
            
            prevFontSize = currentFontSize;
          }
          this.textRenderer = tr1;
          break;
        }

      }

      setupTextRenderer();
    }


    renderInvisiblePickingBackground(gl, offset);

    //center x based on the widest line
    xpos = (float) ((this.w - (pxMaxWidth * scaleVal)) * .5f);
    //xpos = 0f; //(float) ( (this.w - (pxMaxWidth * scaleVal)) * .5f );

    //center y based on highest line, times the number of lines
    float ypos_insert = (float) ((this.h - (pxMaxHeight * lines.size() * scaleVal)));

    //TEMP
    //float ypos_insert = (float) ((this.h - (pxMaxHeight * 12 * scaleVal)) );
    //END TMEP

    float md = maxDescent * scaleVal;
    if (justifyY == 1) //top
    {
      ypos = this.h;
    }
    else if (justifyY == 0) //center
    {
      ypos = this.h + md - (ypos_insert * .5f);
    }
    else if (justifyY == -1) //bottom
    {
      ypos = this.h + md - ypos_insert;
    }

    //now render it!	
    //textRenderer.begin3DRendering();
    textRenderer = new TextRenderer(new Font("default", Font.PLAIN, 100), true, true);
    
    textRenderer.beginRendering(this.pxWidth, this.pxHeight);

    int totalChars = 0;

    //System.out.println("lines size = " + lines.size());
    for (int i = 0; i < lines.size(); i++)
    {
      String viewLine;
      int charsAvailable = visibleChars - totalChars;
      if (charsAvailable > lines.get(i).length())
      {
        viewLine = lines.get(i);
      }
      else
      {
        viewLine = lines.get(i).substring(0, charsAvailable);
      }


      textRenderer.setColor(1f,1f,1f,1f);

      textRenderer.draw("A", 0, 0);
      textRenderer.draw("B", pxWidth - 30, 0);
      
      
      this.ypos -= pxMaxHeight * scaleVal;

      //System.out.println("line i = " + i + " this.ypos = " + ypos + " text = " + viewLine);
      textRenderer.setColor(this.r, this.g, this.b, this.a);
      //textRenderer.draw3D(viewLine, this.xpos, this.ypos, offset + .01f, this.scaleVal);
      //  textRenderer.draw3D(lines.get(i), this.xpos, this.ypos, offset + .01f, this.scaleVal);
      //textRenderer.draw3D(lines.get(i), this.xpos, this.ypos, offset + .01f, 1f);

      int lineH = (int) ((float)pxHeight / 8f);
      ypos = (pxY + pxHeight) - lineH;
      //ypos = 0;
      
      int drawx = 0; //pxX;
      int drawy = (int) ypos - (i * lineH);
     
       textRenderer.draw("C", 0, pxHeight - lineH);
       textRenderer.draw("D", pxX, BehaviorismDriver.canvasHeight - pxY);
       textRenderer.draw("E", pxX, pxY + pxHeight - lineH);
    
       System.out.println("pxX/Y/W/H = " + pxX + "/" + pxY + "/" + pxWidth + "/" + pxHeight);
      System.out.println("lineH = " + lineH);
      System.out.println("drawx/drawy = " + drawx + "/" + drawy + " : " + lines.get(i));
      textRenderer.draw(lines.get(i), drawx, drawy);
      
      totalChars += lines.get(i).length();
      if (totalChars >= visibleChars)
      {
        break;
      }
    }

    textRenderer.endRendering();
    */
  }

  public static GeomTextFlow createGeomTextFlowWithQuotes(String text)
  {
    return createGeomTextFlowWithQuotes(0f, 0f, 0f, 2f, 1f, text);
  }

  public static GeomTextFlow createGeomTextFlowWithQuotes(
    float x, float y, float z, float w, float h,
    String text)
  {
    GeomTextFlow gtf = new GeomTextFlow(x, y, z, w, h, text);
    gtf.setColor(1f, 1f, 1f, 1f);
    gtf.debugDrawBackground = true;
   // gtf.determineRotateAnchor(RotateEnum.CENTER);
   // gtf.determineScaleAnchor(ScaleEnum.CENTER);

    GeomTextFlow gtf2 = new GeomTextFlow(-.35f, gtf.h / 8f, 0f, .36f, 1f, "\u201C");
    gtf2.setColor(1f, 1f, 1f, 1f);
    gtf.addGeom(gtf2, true);

    GeomTextFlow gtf3 = new GeomTextFlow(gtf.w - .01f, gtf.h / 8f, 0f, .36f, 1f, "\u201D");
    gtf3.setColor(1f, 1f, 1f, 1f);
    gtf.addGeom(gtf3, true);

    //gtf2.registerDraggableObject(gtf);
    //gtf3.registerDraggableObject(gtf);
    gtf2.registerSelectableObject(gtf);
    gtf3.registerSelectableObject(gtf);
    return gtf;
  }
}

