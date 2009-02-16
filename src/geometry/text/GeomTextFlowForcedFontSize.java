/* GeomTextFlowForcedFontSize.java ~ Sep 4, 2008 */
package geometry.text;

import behaviors.BehaviorContinuous.ContinuousBehaviorBuilder;
import behaviors.geom.continuous.BehaviorGeomContinuous;
import com.sun.opengl.util.j2d.TextRenderer;
import geometry.text.CharLine;
import geometry.Colorf;
import geometry.Geom;
import geometry.text.GeomGlyph;
import geometry.text.GeomTextFlow;
import handlers.FontHandler;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
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
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;

/**
 *
 * @author angus
 */
public class GeomTextFlowForcedFontSize extends GeomTextFlow
{

  public static GeomTextFlow newGeomTextFlowUsingSpecifiedFontSize(
    String text, int fontSize, Point3f p3f, float w, float h, long baseNano, long lengthMS)
  {
    final GeomTextFlow gtf = new GeomTextFlowForcedFontSize(p3f, w, h, text);
    //gtf.visibleChars = 0;
    ((GeomTextFlowForcedFontSize) gtf).charLines = CharPosition.stringToCharPositions(text);

    //make a new glyph for each character
    for (CharLine charLine : gtf.charLines)
    {
      for (CharPosition charPosition : charLine.charPositions)
      {
        gtf.addGeom(charPosition.geomGlyph, false);

        //BehaviorTranslate.translate(charPosition.geomGlyph, System.nanoTime(), 20000L, new Point3f(-5f, 0f, 0f));
      }
    }

    
    gtf.fontSize = fontSize;
    gtf.visibleChars = 0;
    //gtf.numLines = numLines;
    gtf.setColor(1f, 1f, 1f, 1f);
    gtf.justifyX = -1;
    gtf.justifyY = 1;
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

    return gtf;
  }

  public static GeomTextFlow newGeomTextFlowUsingSpecifiedNumberOfLines(
    String text, int numLines, Point3f p3f, float w, float h, long baseNano, long lengthMS)
  {
    final GeomTextFlow gtf = new GeomTextFlowForcedFontSize(p3f, w, h, text);
    //gtf.visibleChars = 0;
    ((GeomTextFlowForcedFontSize) gtf).charLines = CharPosition.stringToCharPositions(text);

    gtf.visibleChars = 0;
    gtf.numLines = numLines;
    gtf.setColor(1f, 1f, 1f, 1f);
    gtf.justifyX = -1;
    gtf.justifyY = 1;
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

    return gtf;
  }

  public static GeomTextFlow newGeomTextFlowWithAppearingCharacters(
    String text, Point3f p3f, float w, float h, long baseNano, long lengthMS)
  {
    final GeomTextFlow gtf = new GeomTextFlowForcedFontSize(p3f, w, h, text);
    gtf.visibleChars = 0;
    gtf.numLines = 15;
    gtf.setColor(1f, 1f, 1f, 1f);
    gtf.justifyX = -1;
    gtf.justifyY = 1;
    //gtf.backgroundColor = new Colorf(1f, 0f, 0f, .2f);
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

    return gtf;
  }

  public GeomTextFlowForcedFontSize(Point3f p3f, float w, float h, String text)
  {
    super(p3f, w, h, text);
  }
  List<CharPosition> charPositions = new ArrayList<CharPosition>();
  int lineHeight = 0;

  @Override
  public boolean createLayout(TextRenderer renderer, boolean forceUseOfRenderer, float pxW, float pxH)
  {
    //glyphs.clear();
    //clearGeoms();


    int numLinesAvailable = this.numLines;
    lineHeight = (int) (pxH / (float) numLinesAvailable);

    charPositions.clear();

    System.out.println("in createLayout() : pxW/pxH = " + pxW + "/" + pxH + ", lineHeight = " + lineHeight);
    lines.clear();

    FontRenderContext frc_layout = renderer.getFontRenderContext();
    font_layout = renderer.getFont();

    Map attrs = new HashMap();
    attrs.put(TextAttribute.FONT, renderer.getFont());

    float curXPos = 0;
    float curYPos = 0;
    int lineIdx = 0;

    for (CharLine charLine : charLines)
    {
      if (charLine.charPositions.size() < 1)
      {
        //still increase y position!
        lineIdx++;

        continue;
      }

      AttributedString str = new AttributedString(charLine.text, attrs);
      LineBreakMeasurer measurer = new LineBreakMeasurer(str.getIterator(), frc_layout);
      int curCharOffsetForNewLine = 0;
      int nextCharOffsetForNewLine;
      curXPos = 0;

      System.out.println("charLine text = " + charLine.text);
      while (measurer.getPosition() < charLine.charPositions.size())
      {
        //CharPosition charPosition = new CharPosition(
        nextCharOffsetForNewLine = measurer.nextOffset(pxW);

        //System.out.println("nextCharOffsetForNewLine = " + nextCharOffsetForNewLine);
        String line = charLine.text.substring(curCharOffsetForNewLine, nextCharOffsetForNewLine);

        Rectangle2D lineBounds = renderer.getBounds(line);
        //System.out.println("bounds of [" + line + "] = " + lineBounds);
        GlyphVector gvec;

        Colorf useColor = this.getColor();
        Rectangle2D charBounds;
        CharPosition cpos;

        if (line.length() > 0)
        {
          gvec = font_layout.createGlyphVector(frc_layout, line);

          String theChar = "" + line.charAt(0);


          curXPos = 0; //this.pxX;
          curYPos = (int) -(lineHeight * (lineIdx + 1));


          //CharPosition cpos = new CharPosition(theChar, curXPos, curYPos, useColor);
          //charPositions.add(cpos);
          //Rectangle2D charBounds = renderer.getBounds(theChar);
          //System.out.println("bounds of [" + theChar + "] = " + charBounds);
          //curXPos += charBounds.getWidth();

          for (int i = curCharOffsetForNewLine; i < nextCharOffsetForNewLine; i++)
          //for (int i = 0; i < line.length(); i++)
          {
            CharPosition theCharPos = charLine.charPositions.get(i);
            theCharPos.xpos = curXPos;
            theCharPos.ypos = curYPos;
            //theChar = "" + line.charAt(i);
            //addGeom(
            //  new GeomGlyph(theCharPos.character, new Point3f(theCharPos.xpos * scaleVal, (theCharPos.ypos + pxH) * scaleVal, 0f), textRenderer, scaleVal));
            theCharPos.geomGlyph.setPos(new Point3f(theCharPos.xpos * scaleVal, (theCharPos.ypos + pxH) * scaleVal, 0f));
            theCharPos.geomGlyph.textRenderer = textRenderer;
            theCharPos.geomGlyph.scaleVal = scaleVal;
            theCharPos.geomGlyph.setColor(theCharPos.charState.color);
            //theCharPos.geomGlyph.isActive = true;

            /*
            if (theChar.equals("$"))
            {
            useColor = new Colorf(1f, 1f, 1f, 1f);
            continue;
            }
            else if (theChar.equals("%"))
            {
            useColor = this.getColor();
            continue;
            }
            else if (theChar.equals("^"))
            {
            curXPos = 0;
            lineIdx++;
            curYPos = (int) ((pxH - (lineHeight)) - ((lineHeight) * lineIdx));
            continue;
            }
             */
            //cpos = new CharPosition(theChar, curXPos, curYPos, useColor);
            //charPositions.add(cpos);

            //USING GLYPH METRICS gives us complete control over character and can get kerning info.
            //GlyphMetrics gmet = gvec.getGlyphMetrics(i);
            GlyphMetrics gmet = gvec.getGlyphMetrics(i - curCharOffsetForNewLine);
            curXPos += gmet.getAdvance();


          //gvec = font_layout.createGlyphVector(frc_layout, line);
          //curXPos += (float) gvec.getGlyphPixelBounds(i, frc_layout, curXPos, curYPos).getWidth();
          }


        }
        curCharOffsetForNewLine = nextCharOffsetForNewLine;
        measurer.setPosition(curCharOffsetForNewLine);

        lineIdx++;
      }
    }

    for (int i = 0; i < charPositions.size(); i++)
    {
      //System.out.println("charPosition at offset " + i + " = " + charPositions.get(i));
    }
    return true;
  }

  public boolean createLayout_real(TextRenderer renderer, boolean forceUseOfRenderer, float pxW, float pxH)
  {
    int numLinesAvailable = this.numLines;
    int lineHeight = (int) (pxH / (float) numLinesAvailable);

    charPositions.clear();

    System.out.println("in createLayout() : pxW/pxH = " + pxW + "/" + pxH + ", lineHeight = " + lineHeight);
    lines.clear();

    FontRenderContext frc_layout = renderer.getFontRenderContext();
    font_layout = renderer.getFont();

    Map attrs = new HashMap();
    attrs.put(TextAttribute.FONT, renderer.getFont());
    AttributedString str = new AttributedString(this.text, attrs);
    LineBreakMeasurer measurer = new LineBreakMeasurer(str.getIterator(), frc_layout);

    int curCharOffsetForNewLine = 0;
    int nextCharOffsetForNewLine;
    float curXPos = 0;
    float curYPos = 0;
    int lineIdx = 0;

    while (measurer.getPosition() < this.text.length())
    {
      //CharPosition charPosition = new CharPosition(
      nextCharOffsetForNewLine = measurer.nextOffset(pxW);

      //System.out.println("nextCharOffsetForNewLine = " + nextCharOffsetForNewLine);
      String line = this.text.substring(curCharOffsetForNewLine, nextCharOffsetForNewLine);

      Rectangle2D lineBounds = renderer.getBounds(line);
      //System.out.println("bounds of [" + line + "] = " + lineBounds);
      GlyphVector gvec;

      Colorf useColor = this.getColor();
      Rectangle2D charBounds;
      CharPosition cpos;

      if (line.length() > 0)
      {
        gvec = font_layout.createGlyphVector(frc_layout, line);

        String theChar = "" + line.charAt(0);


        curXPos = 0; //this.pxX;
        //curYPos = 0 + (lineHeight * lineIdx);
        curYPos = (int) ((pxH - (lineHeight)) - ((lineHeight) * lineIdx));
        //curYPos = (int) pxH; // ((pxH / 2)); // (lineHeight * 2)) - (lineHeight * lineIdx));

        //CharPosition cpos = new CharPosition(theChar, curXPos, curYPos, useColor);
        //charPositions.add(cpos);
        //Rectangle2D charBounds = renderer.getBounds(theChar);
        //System.out.println("bounds of [" + theChar + "] = " + charBounds);
        //curXPos += charBounds.getWidth();

        for (int i = 0; i < line.length(); i++)
        {
          theChar = "" + line.charAt(i);

          if (theChar.equals("$"))
          {
            useColor = new Colorf(1f, 1f, 1f, 1f);
            continue;
          }
          else if (theChar.equals("%"))
          {
            useColor = this.getColor();
            continue;
          }
          else if (theChar.equals("^"))
          {
            curXPos = 0;
            lineIdx++;
            curYPos = (int) ((pxH - (lineHeight)) - ((lineHeight) * lineIdx));
            continue;
          }

          cpos = new CharPosition(theChar, curXPos, curYPos, useColor);
          charPositions.add(cpos);
          //charBounds = renderer.getBounds(theChar);
          //curXPos += charBounds.getWidth();
          //curXPos = (int)gvec.getGlyphPosition(i).getX();

          //USING GLYPH METRICS gives us complete control over character and can get kerning info.
          GlyphMetrics gmet = gvec.getGlyphMetrics(i);
          curXPos += gmet.getAdvance();
        //curXPos += (float) gvec.getGlyphPixelBounds(i, frc_layout, curXPos, curYPos).getWidth();
        }


      }
      curCharOffsetForNewLine = nextCharOffsetForNewLine;
      measurer.setPosition(curCharOffsetForNewLine);

      lineIdx++;
    }


    for (int i = 0; i < charPositions.size(); i++)
    {
      //System.out.println("charPosition at offset " + i + " = " + charPositions.get(i));
    }
    return true;
  }

  public boolean createLayout_old(TextRenderer renderer, boolean forceUseOfRenderer, float pxW, float pxH)
  {
    charPositions.clear();

    forceUseOfRenderer = false;

    System.out.println("in createLayout() : pxW/pxH = " + pxW + "/" + pxH);
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
    int curXPos = 0;
    pxHeightTaken = 0;
    while (measurer.getPosition() < this.text.length())
    {
      //CharPosition charPosition = new CharPosition(

      //System.out.println("measurer.getPosition = " + measurer.getPosition());
      int nextPos = measurer.nextOffset(pxW);

      //System.out.println("nextCharOffsetForNewLine = " + nextCharOffsetForNewLine);
      String line = this.text.substring(curPos, nextPos);
      Rectangle2D bounds = renderer.getBounds(line);
      //lines.add(line);
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

      Rectangle2D boundsXXX = font_layout.getStringBounds("QQQQQQQQQQQQQQQQQQQ", frc_layout);
      pxMaxHeight = (boundsXXX.getHeight());
      pxMaxWidth = (boundsXXX.getWidth());
      LineMetrics lineMetricsXXX = font_layout.getLineMetrics("Q", frc_layout);
      maxDescent = lineMetricsXXX.getDescent();
      maxAscent = lineMetricsXXX.getAscent();
    }


    return true;

  /*
  if (pxMaxWidth < pxWidth * .95)
  {
  return false;
  }
  
  
  //then this font size fits, or we have chosen to force the use of this font
  return true;
   */
  }

  public void setupTextRenderer(float cw, float ch)
  {
    //this.scaleValX = (this.w * (1f)) / ((float) pxMaxWidth);
    this.scaleValX = (this.w * (1f)) / cw;
    //this.scaleValY = ((this.h / lines.size()) * 1f) / ((float) pxMaxHeight);
    //this.scaleValY = ((this.h / 7) * 1f) / ((float) pxMaxHeight);
    this.scaleValY = (this.h) / ch;
    //this.scaleValY = ((this.h) ) / ( ((float) pxHeight * 10) );
    //this.scaleValY = ((this.h / 8) * 1f) / 40f ;

    if (scaleValX < scaleValY)
    {
      this.scaleVal = scaleValX;
    }
    else
    {
      this.scaleVal = scaleValY;
    }

    this.scaleVal = 1f; //scaleValY;

  //System.out.println("scaleVal = " + scaleVal);
  }

  public void setupTextRenderer()
  {
    //this.scaleValX = (this.w * (1f)) / ((float) pxMaxWidth);
    this.scaleValX = (this.w * (1f)) / ((float) pxWidth);
    //this.scaleValY = ((this.h / lines.size()) * 1f) / ((float) pxMaxHeight);
    //this.scaleValY = ((this.h / 7) * 1f) / ((float) pxMaxHeight);
    this.scaleValY = (((this.h / numLines)) / ((float) pxHeight / numLines));
    //this.scaleValY = ((this.h) ) / ( ((float) pxHeight * 10) );
    //this.scaleValY = ((this.h / 8) * 1f) / 40f ;

    if (scaleValX < scaleValY)
    {
      this.scaleVal = scaleValX;
    }
    else
    {
      this.scaleVal = scaleValY;
    }

    this.scaleVal = scaleValY;

  //System.out.println("scaleVal = " + scaleVal);
  }

  public void draw(GL gl, GLU glu, float offset)
  {
    //temp - draw background
    if (debugDrawBackground == true || backgroundColor != null || 1 == 1)
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

    //this.pxWidth = (int) (Main.renderer.getWidthOfObjectInPixels(this, this.insetX));
    //this.pxHeight = (int) (Main.renderer.getHeightOfObjectInPixels(this, this.insetY));


    //this does NOT work with scaling!!!! what gives??? fix this!!!!!!!!!!!
    calculateUnrotatedPixelWidthAndHeight(gl);
    //System.out.println("pxWidth/pxHeight = " + pxWidth + "/" + pxHeight);

    if (FontHandler.getInstance().fontsReady.get() == true ||
      this.pxWidth != this.prevPxWidth ||
      this.pxHeight != this.prevPxHeight ||
      this.textRenderer == null || 1 == 2 //temp, obv
      )
    {
      if (FontHandler.getInstance().fontsReady.get() == true) //make sure to recalculate everything if font has changed
      {
        prevFontSize = -1f;
      }

      this.prevPxWidth = this.pxWidth;
      this.prevPxHeight = this.pxHeight;

      boolean doesItFit = false;

      int fhs = FontHandler.getInstance().textRenderers.size();
      for (int i = fhs - 1; i >= 0; i--)
      {
        TextRenderer tr1 = (FontHandler.getInstance().textRenderers.get(i));
        //TextRenderer tr1 = (FontHandler.getInstance().textRenderers.get(fhs - 1));
        float currentFontSize = tr1.getFont().getSize();

        if (fontSize > 0) //ie, we are using a specified font size
        {
          if (currentFontSize != fontSize)
          {
            continue;
          }


        }
        //System.out.println("*** \n " + tr1.getFont());
        //Rectangle2D charBounds = tr1.getFont().getMaxCharBounds(tr1.getFontRenderContext()); //ie charBounds of largest character-- capital "O" or whatever

        GlyphVector gvec = tr1.getFont().createGlyphVector(tr1.getFontRenderContext(), "Q");
        float ch = (float) gvec.getGlyphPixelBounds(0, null, 0, 0).getHeight();
//        
//        String tryText = "QQQQQQQQQQQQQQQQQQQQQ";
//        Rectangle2D bounds = tr1.getFont().getStringBounds(tryText, tr1.getFontRenderContext());
//
//        float ch = (float) (bounds.getHeight() - bounds.getY());

        //System.out.println("Q bounds = " + bounds);
        System.out.println("curFontSize = " + currentFontSize + ": is ch (" + ch + ") less than pxH / numLines (" + (pxHeight / this.numLines) + ")?");
        if ((ch < (pxHeight / this.numLines)) || i == 0 || fontSize > 0) //ie if it fits OR it is the last one
        {
          //tr1 = (FontHandler.getInstance().textRenderers.get(i + 1));
          //tr1 = (FontHandler.getInstance().textRenderers.get(i));
          if (fontSize > 0)
          {
            this.numLines = pxHeight / fontSize;
          }          //if (prevFontSize != currentFontSize)
          {
            //System.out.printf("pxWidth/pxHeight = %d/%d\n", pxWidth, pxHeight);
            //System.out.println("fpxWidth = " + (xval * w) + ", fpxHeight = " + (xval * h) );
            //System.out.println("CHANGING FONT SIZE FROM " + prevFontSize + " TO " + currentFontSize);

            //REAL
            createLayout(tr1, true, pxWidth, pxHeight);

            //TEMP
            //createLayout(tr1, true, (200 * w), (200 * h));

            prevFontSize = currentFontSize;
          }

          this.textRenderer = tr1;

          //setupTextRenderer(cw, ch);

          break;
        }

      }

      //System.out.println("USING FONT " + textRenderer.getFont());
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
    int visiblesIdx = 0;
    //System.out.println("scaleVal = " + scaleVal);
    /*
    for (CharPosition cpos : charPositions)
    {
    textRenderer.setColor(cpos.color.r, cpos.color.g, cpos.color.b, cpos.color.a);
    textRenderer.draw3D(cpos.character,
    (float) (cpos.xpos * scaleVal), (float) (cpos.ypos * scaleVal),
    offset + .01f, this.scaleVal);
    
    visiblesIdx++;
    if (visiblesIdx > visibleChars)
    {
    break;
    }
    }
     */



    textRenderer.begin3DRendering();

    /*
    for (CharLine charLine : charLines)
    {
      if (visiblesIdx > visibleChars)
      {
        break;
      }

      for (CharPosition cpos : charLine.charPositions)
      {
        if (visiblesIdx > visibleChars)
        {
          break;
        }

        Colorf cposColor = cpos.charState.color;
        textRenderer.setColor(cposColor.r, cposColor.g, cposColor.b, cposColor.a);
        textRenderer.draw3D(cpos.character,
          (float) (cpos.xpos * scaleVal), (float) (this.h + (cpos.ypos * scaleVal)),
          offset + .01f, this.scaleVal);
        
        visiblesIdx++;
      }
    }
    */

    for (Geom g : geoms)
    {
      if (!(g instanceof GeomGlyph))
      {
        continue;
      }

      GeomGlyph gg = (GeomGlyph) g;
      
      textRenderer.draw3D(gg.glyph,
          (float) (gg.anchor.x), (float) (gg.anchor.y),
          offset + .01f, this.scaleVal);
      
    }
    
    textRenderer.end3DRendering();



    /*
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
    
    this.ypos -= pxMaxHeight * scaleVal;
    
    //System.out.println("fprced xpos = " + xpos);
    xpos = 0f;
    //System.out.println("line i = " + i + " this.ypos = " + ypos + " text = " + viewLine);
    textRenderer.setColor(this.r, this.g, this.b, this.a);
    textRenderer.draw3D(viewLine, this.xpos, this.ypos, offset + .01f, this.scaleVal);
    //textRenderer.draw3D(lines.get(i), this.xpos, this.ypos, offset + .01f, this.scaleVal);
    //textRenderer.draw3D(lines.get(i), this.xpos, this.ypos, offset + .01f, 1f);
    
    totalChars += lines.get(i).length();
    
    if (totalChars >= visibleChars)
    {
    break;
    }
    }
     */



    gl.glLineWidth(1f);
    gl.glColor4f(1f, 1f, 1f, 1f);

    float inc = (float) lineHeight * this.scaleVal; //this.h / this.numLines;
    gl.glBegin(GL.GL_LINES);
    {
      for (int i = 0; i < this.numLines; i++)
      {
        gl.glVertex3f(0f, (this.h) - (inc * (i + 1)), this.z);
        gl.glVertex3f(this.w, (this.h) - (inc * (i + 1)), this.z);
      }
    }
    gl.glEnd();

  }
}

class CharPosition
{

  String character;
  float xpos = -1f;
  float ypos = -1f;
  Colorf color;
  CharState charState = null;
  GeomGlyph geomGlyph = null;

  public CharPosition(String character)
  {
    this.character = character;
    //this.color = new Colorf(1f,1f,1f,1f);
    this.charState = new CharState(); //default charState
  }

  public CharPosition(String character, CharState charState)
  {
    this.character = character;
    this.charState = charState;
    this.geomGlyph = new GeomGlyph(this.character);

  }

  public CharPosition(String character, float xpos, float ypos, Colorf color)
  {
    this.character = character;
    this.xpos = xpos;
    this.ypos = ypos;
    this.color = color;


  }

  public String toString()
  {
    return ("CharPosition: " + character + ", " + xpos + "/" + ypos + " color = " + color);
  }

  public static List<CharLine> stringToCharPositions(String unparsedText)
  {
    List<CharLine> charLines = new ArrayList<CharLine>();

    CharState curCharState = new CharState();
    CharLine curCharLine = new CharLine();

    int curCharPos = 0;

    for (int i = 0; i < unparsedText.length(); i++)
    {
      char curChar = unparsedText.charAt(i);
      if (curChar == '<')
      {
        int indexOfClosingBracket = unparsedText.indexOf(">", i);

        String tag = unparsedText.substring(i + 1, indexOfClosingBracket).trim();

        System.out.println("found a tag " + tag);
        //got a tag, now parse it... 

        //is it a newline tag?
        if (tag.equals("br"))
        {
          charLines.add(curCharLine);
          curCharLine = new CharLine();
          curCharPos = 0;
        }
        else if (tag.equals("p"))
        {
          charLines.add(curCharLine);
          curCharLine = new CharLine();
          charLines.add(curCharLine);
          curCharLine = new CharLine();

          curCharPos = 0;
        }
        else if (tag.indexOf(":") > 0) //changing state in someway... should probably implement a stack!
        {

          curCharState = new CharState(curCharState);

          String[] states = tag.split(";");

          for (int ii = 0; ii < states.length; ii++)
          {
            //System.out.println("states[" + ii + "] = " + states[ii]);

            String[] command = states[ii].split(":");

            String type = command[0];
            String value = command[1];

            //System.out.println("type:value = " + type + ":" + value);

            if (type.equals("color"))
            {
              curCharState.color = Colorf.hexToColor(value);
            }
          }
        }
        //skip ahead to end of this tag...
        i = indexOfClosingBracket;
      }
      else //normal character
      {
        CharPosition charPosition = new CharPosition("" + curChar, curCharState);
        curCharLine.charPositions.add(charPosition);
        curCharLine.text += charPosition.character;
        curCharPos++;
      }
    }


    charLines.add(curCharLine); //add the last line

    return charLines;
  }
}

class CharState
{

  Colorf color = new Colorf(0f, 0f, 1f, 1f);

  public CharState()
  {
  }

  public CharState(CharState charState)
  {
    color = charState.color;
  }
}