/*
 * GeomRect.java
 *
 * Created on July 20, 2007, 1:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package behaviorism.geometry;

import behaviorism.algorithms.Corner;
import behaviorism.renderers.Renderer;
import behaviorism.utils.GeomUtils;
import behaviorism.utils.MatrixUtils;
import java.awt.geom.Rectangle2D;
import javax.vecmath.Point3f;
import java.util.Vector;
import javax.vecmath.Point3d;

import behaviorism.utils.RenderUtils;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static behaviorism.utils.RenderUtils.*;


public class GeomRect extends Geom
{
  //ANGUS - note may need to add these back in
  //temporarily added to superclass Geom
  //public float w = 1f;
  //public float h = 1f;

  //Don't think I need this...
  @Deprecated
  public Rectangle2D rectangle; //this is the *bounds* of the text
  //used for the packing algorithm...
  //maybe this class should extend from GeomRect or a Geom2D
  //since any 2d shape could conceivably be "packed"
  //float offsetFactor = Utils.randomFloat(5f, 15f);
  //public java.util.List<Corner> corners = Collections.synchronizedList(new ArrayList<Corner>());
  //GET RID OF THIS!
  @Deprecated
  public java.util.List<Corner> corners = new Vector<Corner>();


  //temp while we are cleaning stuff up... shouldn't be here...
  public GeomRect()
  {
    this.isSelectable = true;
  }

  public GeomRect(Rectangle2D rect)
  {
    super((float) rect.getX(), (float) rect.getY(), 0f);

    this.w = (float) rect.getWidth();
    this.h = (float) rect.getHeight();

    this.isSelectable = true;
  }

  //turn pixel width and height into world width and height,
  //it also adds h to the y position, and then makes sure that the h is postive.
  //(this is so that specifying (0,0,100,100) will turn into something like
  //(0f, -1f, 1f, 1f). That is, so that the height will still be positive.
  //otherwise we would have (0f, 0f, 1f, -1f)
  public void adjustPixelSize(boolean upperLeft, float x, float y, float w, float h)
  {
    Point3f upperright;
    if (upperLeft == true)
    {
      upperright = MatrixUtils.pixelToWorld(x + w, y + h);
    }
    else
    {
       int www =  RenderUtils.getViewport()[2];
    int hhh =  RenderUtils.getViewport()[3];

      upperright = MatrixUtils.pixelToWorld(
        x + www / 2 + w,
        y + hhh / 2 + h);
    }
    this.w = upperright.x - translate.x;
    this.h = upperright.y - translate.y;

    translate.y += this.h;
    this.h = -this.h;
    translate.z = 0f;

    System.out.println("in adjustWidth A : x/y/w/h = " + x + "/" + y + "/" + w + "/" + h + " name: " + name);
    System.out.println("in adjustWidth B : this.w/this.h = " + translate.x + "/" + translate.y + "/" + this.w + "/" + this.h + " name: " + name);
  }

  public GeomRect(int x, int y, int w, int h)
  {
    super(x, y);

    adjustPixelSize(true, x, y, w, h);

    this.isSelectable = true;
  }

  public GeomRect(boolean upperLeft, int x, int y, int w, int h)
  {
    super(upperLeft, x, y);

    adjustPixelSize(upperLeft, x, y, w, h);

    this.isSelectable = true;
  }

  public GeomRect(float x, float y, float z, float w, float h)
  {
    super(x, y, z);
    this.isSelectable = true;
    this.w = w;
    this.h = h;
  }

  public GeomRect(Point3f p3f, float w, float h)
  {
    super(p3f);
    this.isSelectable = true;
    this.w = w;
    this.h = h;
  }

  public void debugPackingAlgorithm(GL2 gl)
  {
    gl.glEnable(GL_BLEND);

    synchronized (corners)
    {
      for (Corner c : corners)
      {
        gl.glBegin(GL_POINTS);
        {
          //Point3d test = new Point3d(c.mark.getX(), c.mark.getY(), this.z);
          Point3d test = new Point3d(c.mark.getX(), c.mark.getY(), this.translate.z);
          //Point3d p3d = MatrixUtils.getWorldPointInGeomCoordinates(test, RenderUtils.getCamera().modelview, modelview);
          Point3d p3d = MatrixUtils.getWorldPointInGeomCoordinates(test, RenderUtils.getCamera().modelview, modelview);
          gl.glColor4f(1f, 0f, 0f, 1f);
        //gl.glVertex3dv(MatrixUtils.toArray(p3d), 0);
        }
        gl.glEnd();

        for (Rectangle2D r2f : c.possibleRectangles)
        {
          gl.glColor4f(0f, 0f, 1f, .1f);

          gl.glBegin(GL_POLYGON);
          {
//          gl.glVertex3f(r2f.x, r2f.y, this.z);
//          gl.glVertex3f(r2f.x + r2f.width, r2f.y, this.z);
//          gl.glVertex3f(r2f.x + r2f.width, r2f.y + r2f.height, this.z);
//          gl.glVertex3f(r2f.x, r2f.y + r2f.height, this.z);

            //float cz = this.z;
            float cz = this.translate.z;


            Point3d p3d = MatrixUtils.getWorldPointInGeomCoordinates(new Point3d(r2f.getX(), r2f.getY(), cz), RenderUtils.getCamera().modelview, modelview);
            gl.glVertex3dv(MatrixUtils.toArray(p3d), 0);
            p3d = MatrixUtils.getWorldPointInGeomCoordinates(new Point3d(r2f.getX() + r2f.getWidth(), r2f.getY(), cz), RenderUtils.getCamera().modelview, modelview);
            gl.glVertex3dv(MatrixUtils.toArray(p3d), 0);
            p3d = MatrixUtils.getWorldPointInGeomCoordinates(new Point3d(r2f.getX() + r2f.getWidth(), r2f.getY() + r2f.getHeight(), cz), RenderUtils.getCamera().modelview, modelview);
            gl.glVertex3dv(MatrixUtils.toArray(p3d), 0);
            p3d = MatrixUtils.getWorldPointInGeomCoordinates(new Point3d(r2f.getX(), r2f.getY() + r2f.getHeight(), cz), RenderUtils.getCamera().modelview, modelview);
            gl.glVertex3dv(MatrixUtils.toArray(p3d), 0);

          }

          gl.glEnd();
        }
      }
    }
    gl.glDisable(GL_BLEND);
  }

  @Override
  public void draw()
  {
    GL2 gl = getGL();
    //this should go into the inivs. picking method
    /*
    boolean depthTest = RenderUtils.getBoolean(gl, GL.GL_DEPTH_TEST);

    if (depthTest == false && isSelectable == true)
    {
    gl.glEnable(GL.GL_DEPTH_TEST);

    gl.glColor4f(0f, 0f, 0f, 0f);

    drawRect(gl, 0f, 0f, offset, w, h);

    gl.glDisable(GL.GL_DEPTH_TEST);
    }
     */

    gl.glColor4fv(color.array(), 0);

    drawRect(gl, 0f, 0f, offset, w, h);
  }

  public void drawRect(GL2 gl, float x, float y, float z, float w, float h)
  {
    gl.glBegin(gl.GL_QUADS);

    gl.glVertex3f(x, y, z);
    gl.glVertex3f(x + w, y, z);
    gl.glVertex3f(x + w, y + h, z);
    gl.glVertex3f(x, y + h, z);

    gl.glEnd();
  }

  public void drawRect(GL2 gl, float x, float y, float z, float w, float h,
    float left, float right, float bottom, float top)
  {
    gl.glBegin(gl.GL_QUADS);

    gl.glTexCoord2f(left, bottom);
    gl.glVertex3f(x, y, z);
    gl.glTexCoord2f(right, bottom);
    gl.glVertex3f(x + w, y, z);
    gl.glTexCoord2f(right, top);
    gl.glVertex3f(x + w, y + h, z);
    gl.glTexCoord2f(left, top);
    gl.glVertex3f(x, y + h, z);

    gl.glEnd();
  }


  /**
   * Scales the current width and height so that the largest size is maxSize.
   * @param maxSize
   */
  public void normalizeSize(float maxSize)
  {
    normalizeSize(this.w, this.h, maxSize);
  }

  /**
   * Scales and sets the width and height so that the largest size is maxSize. 
   * @param fw
   * @param fh
   * @param maxSize
   */
 public void normalizeSize(float fw, float fh, float maxSize)
  {
    if (fw == fh)
    {
      this.w = maxSize;
      this.h = maxSize;
    }
    else if (fw > fh)
    {
      normalizeSizeByWidth(fw, fh, maxSize);
    }
    else
    {
      normalizeSizeByHeight(fw, fh, maxSize);
    }
  }

  public void normalizeSizeByWidth(float fw, float fh, float normalized)
  {
    this.w = normalized;
    this.h = (fh / fw) * normalized;
  }

  public void normalizeSizeByHeight(float fw, float fh, float normalized)
  {
    this.h = normalized;
    this.w = (fw / fh) * normalized;
  }

  // get this and all packing algorithm bullshit the fuck out of this class!!!!!!!!!!
  //from now on rectangle coords are always in absolute coordinates...
  public Rectangle2D makeRectangle2DFromRect()
  {
    /*
    //Path2D.Float p2d = BehaviorismDriver.renderer.getScreenShapeForWorldCoords(this);
    Rectangle2D.Float r2d = BehaviorismDriver.renderer.getScreenRectangleForWorldCoords(this);
    this.rectangle = r2d;
     */

    this.rectangle = new Rectangle2D.Double(this.translate.x, this.translate.y, w, h);
    this.area = GeomUtils.area(this.rectangle);

    return rectangle;
  }

  @Deprecated
  public boolean setCoordFromRectangle() //use already existing rectangle
  {
    if (this.rectangle == null)
    {
      isActive = false;
    }
    else
    {
      // this.x = (float)this.rectangle.getX();
      // this.y = (float)this.rectangle.getY();
      this.translate.x = (float) this.rectangle.getX();
      this.translate.y = (float) this.rectangle.getY();
      this.w = (float) this.rectangle.getWidth();
      this.h = (float) this.rectangle.getHeight();

      /*
      List<Float> floats = BehaviorismDriver.renderer.getScreenRectInGeomCoordnates(this, this.rectangle);
      
      this.translate.x = floats.get(0);
      this.translate.y = floats.get(1);
      this.w = floats.get(2);
      this.h = floats.get(3);
       */

      if (this.rectangle.isEmpty())
      {
        isActive = false;
      //isActive = true;
      }
      else
      {
        isActive = true;
      //isActive = false;
      }
    }

    return isActive;
  }

  //public void setCoordFromRectangle(Rectangle2D.Float bounds)
  public void setCoordFromRectangle(Rectangle2D bounds)
  {

    /*
    List<Float> floats = BehaviorismDriver.renderer.getScreenRectInGeomCoordnates(this, this.rectangle);

    this.translate.x = floats.get(0);
    this.translate.y = floats.get(1);
    this.w = floats.get(2);
    this.h = floats.get(3);
     */

    //this.x = (float)bounds.getX();
    //this.y = (float)bounds.getY();
    this.translate.x = (float) bounds.getX();
    this.translate.y = (float) bounds.getY();
    this.w = (float) bounds.getWidth();
    this.h = (float) bounds.getHeight();
//    this.translate.x = this.x;
//   this.translate.y = this.y;

    scaleAnchor = new Point3f(this.w * .5f, this.h * .5f, 0f);
    //EffectUtils.effectZoomIn(this, System.nanoTime(), 200L, true);

    this.rectangle = bounds;
  }

  /*
  public Point3f getCenterAnchor()
  {

  return new Point3f(translate.x + w * .5f, translate.y + h * .5f, translate.z + d * .5f);
  }
   */

  /*
  public Point3f getCenter()
  {
  return new Point3f(w * .5f, h * .5f, d * .5f);
  }
   */
  public void setRect(Point3f p3f, float w, float h)
  {
    setTranslate(p3f);
    this.w = w;
    this.h = h;
  }

  /*
  public static Geom createBorderGeomWithExactInset(Geom geom, BorderEnum borderType, 
  float exactInset, Colorf color)
  {
  Geom returnGeom = null;
  Point3f p3f = new Point3f();
  
  Geom gc2;

  switch (borderType)
  {
  case RECTANGLE:

  returnGeom = new GeomRect(p3f.x + (-geom.w * .5f) - exactInset, p3f.y + (-geom.h * .5f) - exactInset, 0f,
  geom.w + (exactInset * 2f), geom.h + (exactInset * 2f));

  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;
  returnGeom.setColor(color);

  geom.setTranslate(exactInset, exactInset, 0f);

  returnGeom.addGeom(geom, true);
  returnGeom.isSelectable = true; //true;

  break;
  }

  geom.registerSelectableObject(returnGeom);
  returnGeom.registerClickableObject(geom);

  geom.registerDraggableObject(returnGeom);
  returnGeom.registerSelectableObject(geom);
  return returnGeom;
  }
  public static Geom createBorderGeom(Geom geom, BorderEnum borderType, float insetPerc, Colorf color)
  {
  Geom returnGeom = null;
  Point3f p3f = new Point3f();
  
  Geom gc2;
  float insetw, inseth;

  switch (borderType)
  {
  case RECTANGLE:

  insetw = (geom.w * insetPerc);
  inseth = (geom.h * insetPerc);

  //trying exact size, not percentage...
  float exactInset = 0f;
  if (insetw < inseth)
  {
  inseth = insetw;
  }
  else
  {
  insetw = inseth;
  }
  returnGeom = new GeomRect(p3f.x + (-geom.w * .5f) - insetw, p3f.y + (-geom.h * .5f) - inseth, 0f,
  geom.w + (insetw * 2f), geom.h + (inseth * 2f));


  //trying percentage, not exact size...
  //        if (insetw < inseth)
  //        {
  //          inseth = insetw;
  //        }
  //        else
  //        {
  //          insetw = inseth;
  //        }

  //        returnGeom = new GeomRect(p3f.x + (-origGeom.w * .5f) - insetw, p3f.y + (-origGeom.h * .5f) - inseth, 0f,
  //          origGeom.w + (insetw * 2f), origGeom.h + (inseth * 2f));
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;
  returnGeom.setColor(color);

  geom.setTranslate(insetw, inseth, 0f);

  returnGeom.addGeom(geom, true);
  returnGeom.isSelectable = true; //true;

  //returnGeom = gc2;
  break;

  case ELLIPSE:
  insetw = (geom.w * insetPerc);
  inseth = (geom.h * insetPerc);
  float useinset = inseth;
  if (insetw < inseth)
  {
  useinset = insetw;
  }

  float radW = (float) (Math.hypot(geom.w, geom.w) * .5);
  float radH = (float) (Math.hypot(geom.h, geom.h) * .5);

  returnGeom = new GeomEllipse(p3f, radW, radH, 0f);
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;

  //returnGeom.setColor(1f, 0f, 0f, 0f);
  returnGeom.setColor(color);
  geom.setTranslate(-geom.w * .5f, -geom.h * .5f, 0f);
  returnGeom.addGeom(geom, true);




  //border is on the outside of the ellipse (could also make it the inside-- offer this option...)
  GeomEllipse borderCircle2 = new GeomEllipse(0f, 0f, 0f, radW + useinset, radH + useinset, useinset);
  returnGeom.addGeom(borderCircle2, true);
  borderCircle2.setColor(color);



  //gt2.backgroundColor = new Colorf(borderCircle.r, borderCircle.g,
  //        borderCircle.b, borderCircle.a); //background of entire bounds


  returnGeom.isSelectable = true; //true;
  borderCircle2.isSelectable = true; //true;
  geom.isSelectable = true; //true;

  geom.registerSelectableObject(returnGeom);
  geom.registerDraggableObject(returnGeom);
  borderCircle2.registerSelectableObject(returnGeom);
  borderCircle2.registerDraggableObject(returnGeom);
  returnGeom.registerClickableObject(geom);
  return returnGeom;

  case CIRCLE:
  float rad = (float) (Math.hypot(geom.w, geom.h) * .5);

  returnGeom = new GeomCircle(p3f, 0f, rad, 0f, 360f, 64);
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;

  returnGeom.setColor(0f, 0f, 0f, 0f);
  geom.setTranslate(-geom.w * .5f, -geom.h * .5f, 0f);
  returnGeom.addGeom(geom, true);




  //putting border on outside boundary
  GeomCircle borderCircle = new GeomCircle(0f, 0f, 0f, rad, rad + (rad * insetPerc), 0f, 360f, 64);
  returnGeom.addGeom(borderCircle, true);
  borderCircle.setColor(color);

  //gt2.backgroundColor = new Colorf(borderCircle.r, borderCircle.g,
  //        borderCircle.b, borderCircle.a); //background of entire bounds


  returnGeom.isSelectable = true; //true;
  borderCircle.isSelectable = true; //true;
  geom.isSelectable = true; //true;

  geom.registerSelectableObject(returnGeom);
  geom.registerDraggableObject(returnGeom);
  borderCircle.registerSelectableObject(returnGeom);
  borderCircle.registerDraggableObject(returnGeom);
  returnGeom.registerClickableObject(geom);
  return returnGeom;
  //break;
  }


  geom.registerSelectableObject(returnGeom);
  returnGeom.registerClickableObject(geom);
  //gc2.registerClickableObject(gc2);

  geom.registerDraggableObject(returnGeom);
  returnGeom.registerSelectableObject(geom);
  return returnGeom;
  }
   */
  /** 
   * Creates a composite Geom by adding the origGeom on top of two GeomRects to build
   * a double inset around the origGeom. Generally used to pad text by setting the innerInsetColor
   * the same color as the text background, and then using the outerInsetColor as the actual border.
   * The inset values are absolute sizes away from the origGeom. Thus, if you want to set two 
   * borders, one .1f in size and the other .2f in size, you would set the outerExactInset to .3f, and
   * the innerExactInset to .2f.
   * @param origGeom
   * @param borderType
   * @param outerExactInset
   * @param outerInsetColor
   * @param innerExactInset
   * @param innerInsetColor
   * @return
   */

  /*
  public static Geom createBorderGeomWithExactInset(Geom origGeom, BorderEnum borderType, 
  float outerExactInset, Colorf outerInsetColor, float innerExactInset, Colorf innerInsetColor)
  {
  Geom returnGeom = null;
  Point3f p3f = new Point3f();
  float ox = origGeom.translate.x;
  float oy = origGeom.translate.y;

  switch (borderType)
  {
  case RECTANGLE:

  returnGeom = new GeomRect(p3f.x + (-origGeom.w * .5f) - outerExactInset, p3f.y + (-origGeom.h * .5f) - outerExactInset, 0f,
  origGeom.w + (outerExactInset * 2f), origGeom.h + (outerExactInset * 2f));
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;
  returnGeom.setColor(outerInsetColor);

  GeomRect borderRect2 = new GeomRect(-p3f.x + outerExactInset - innerExactInset, -p3f.y + outerExactInset - innerExactInset, 0f,
  origGeom.w + (innerExactInset * 2f), origGeom.h + (innerExactInset * 2f));
  returnGeom.addGeom(borderRect2, true);
  borderRect2.setColor(innerInsetColor);

  origGeom.setTranslate(outerExactInset, outerExactInset, 0f);

  returnGeom.addGeom(origGeom, true);
  returnGeom.isSelectable = true;

  origGeom.registerSelectableObject(returnGeom);
  origGeom.registerClickableObject(returnGeom);
  origGeom.registerDraggableObject(returnGeom);

  borderRect2.registerSelectableObject(returnGeom);
  borderRect2.registerClickableObject(returnGeom);
  borderRect2.registerDraggableObject(returnGeom);


  //returnGeom.translate(ox, oy, origGeom.z);
  returnGeom.setTranslate(ox, oy, origGeom.translate.z);
  //returnGeom.translate(ox - returnGeom.translate.x, oy - returnGeom.translate.y, origGeom.z);

  return returnGeom;

  }
  return null; //error
  }
   */
  /*
  public static GeomRect createBorderGeomWithExactWidthAndHeightInset(Geom origGeom, BorderEnum borderType,
  float outerExactInsetW, float outerExactInsetH, Colorf outerInsetColor,
  float innerExactInsetW, float innerExactInsetH, Colorf innerInsetColor)
  {
  GeomRect returnGeom = null;
  Point3f p3f = new Point3f();
  float ox = origGeom.translate.x;
  float oy = origGeom.translate.y;

  switch (borderType)
  {
  case RECTANGLE:

  returnGeom = new GeomRect(p3f.x + (-origGeom.w * .5f) - outerExactInsetW, p3f.y + (-origGeom.h * .5f) - outerExactInsetH, 0f,
  origGeom.w + (outerExactInsetW * 2f), origGeom.h + (outerExactInsetH * 2f));
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;
  returnGeom.setColor(outerInsetColor);

  GeomRect borderRect2 = new GeomRect(-p3f.x + outerExactInsetW - innerExactInsetW, -p3f.y + outerExactInsetH - innerExactInsetH, 0f,
  origGeom.w + (innerExactInsetW * 2f), origGeom.h + (innerExactInsetH * 2f));
  returnGeom.addGeom(borderRect2, true);
  borderRect2.setColor(innerInsetColor);

  origGeom.setTranslate(outerExactInsetW, outerExactInsetH, 0f);

  returnGeom.addGeom(origGeom, true);
  returnGeom.isSelectable = true;

  origGeom.registerSelectableObject(returnGeom);
  origGeom.registerClickableObject(returnGeom);
  origGeom.registerDraggableObject(returnGeom);

  borderRect2.registerSelectableObject(returnGeom);
  borderRect2.registerClickableObject(returnGeom);
  borderRect2.registerDraggableObject(returnGeom);


  //returnGeom.translate(ox, oy, origGeom.z);
  returnGeom.setTranslate(ox, oy, origGeom.translate.z);
  //returnGeom.translate(ox - returnGeom.translate.x, oy - returnGeom.translate.y, origGeom.z);

  return returnGeom;

  }
  return null; //error
  }
   */
  /*
  public static Geom createBorderGeom2_isActiveFalse(Geom geom, BorderEnum borderType, float insetPerc, Colorf color, float insetPerc2, Colorf color2)
  {
  Geom returnGeom = null;
  Point3f p3f = new Point3f();

  float ox = geom.translate.x;
  float oy = geom.translate.y;

  float insetw, inseth;
  float insetw2, inseth2;

  switch (borderType)
  {
  case RECTANGLE:

  insetw = (geom.w * insetPerc);
  inseth = (geom.h * insetPerc);

  if (insetw < inseth)
  {
  inseth = insetw;
  }
  else
  {
  insetw = inseth;
  }

  returnGeom = new GeomRect(p3f.x + (-geom.w * .5f) - insetw, p3f.y + (-geom.h * .5f) - inseth, 0f,
  geom.w + (insetw * 2f), geom.h + (inseth * 2f));
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;
  returnGeom.setColor(color);

  insetw2 = (geom.w * insetPerc2);
  inseth2 = (geom.h * insetPerc2);
  if (insetw2 < inseth2)
  {
  inseth2 = insetw2;
  }
  else
  {
  insetw2 = inseth2;
  }
  GeomRect borderRect2 = new GeomRect(-p3f.x + insetw - insetw2, -p3f.y + inseth - inseth2, 0f,
  geom.w + (insetw2 * 2f), geom.h + (inseth2 * 2f));

  returnGeom.addGeom(borderRect2, false);
  borderRect2.setColor(color2);

  geom.setTranslate(insetw, inseth, 0f);

  returnGeom.addGeom(geom, false);
  returnGeom.isSelectable = true; //true;

  geom.registerSelectableObject(returnGeom);
  geom.registerClickableObject(returnGeom);
  geom.registerDraggableObject(returnGeom);

  borderRect2.registerSelectableObject(returnGeom);
  borderRect2.registerClickableObject(returnGeom);
  borderRect2.registerDraggableObject(returnGeom);

  //returnGeom.translate(ox, oy, geom.z);
  returnGeom.setTranslate(ox, oy, geom.translate.z);

  return returnGeom;
  }
  return geom;
  }
   */
  /*
  public static Geom createBorderGeom2(Geom geom, BorderEnum borderType, float insetPerc, Colorf color, float insetPerc2, Colorf color2)
  {
  Geom returnGeom = null;
  Point3f p3f = new Point3f();

  float ox = geom.translate.x;
  float oy = geom.translate.y;

  float t_w,
  t_h,
  t_x,
  t_y;

  Geom gc2;
  float insetw, inseth;
  float insetw2, inseth2;

  switch (borderType)
  {
  case RECTANGLE:

  insetw = (geom.w * insetPerc);
  inseth = (geom.h * insetPerc);

  if (insetw < inseth)
  {
  inseth = insetw;
  }
  else
  {
  insetw = inseth;
  }

  returnGeom = new GeomRect(p3f.x + (-geom.w * .5f) - insetw, p3f.y + (-geom.h * .5f) - inseth, 0f,
  geom.w + (insetw * 2f), geom.h + (inseth * 2f));
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;
  returnGeom.setColor(color);

  insetw2 = (geom.w * insetPerc2);
  inseth2 = (geom.h * insetPerc2);
  if (insetw2 < inseth2)
  {
  inseth2 = insetw2;
  }
  else
  {
  insetw2 = inseth2;
  }
  GeomRect borderRect2 = new GeomRect(-p3f.x + insetw - insetw2, -p3f.y + inseth - inseth2, 0f,
  geom.w + (insetw2 * 2f), geom.h + (inseth2 * 2f));

  returnGeom.addGeom(borderRect2, true);
  borderRect2.setColor(color2);

  geom.setTranslate(insetw, inseth, 0f);

  returnGeom.addGeom(geom, true);
  returnGeom.isSelectable = true; //true;

  geom.registerSelectableObject(returnGeom);
  geom.registerClickableObject(returnGeom);
  geom.registerDraggableObject(returnGeom);

  borderRect2.registerSelectableObject(returnGeom);
  borderRect2.registerClickableObject(returnGeom);
  borderRect2.registerDraggableObject(returnGeom);


  //returnGeom.translate(ox, oy, geom.z);
  returnGeom.setTranslate(ox, oy, geom.translate.z);
  //returnGeom.translate(ox - returnGeom.translate.x, oy - returnGeom.translate.y, origGeom.z);

  return returnGeom;

  //break;

  case ELLIPSE:
  insetw = (geom.w * insetPerc);
  inseth = (geom.h * insetPerc);
  float useinset = inseth;
  insetw2 = (geom.w * insetPerc2);
  inseth2 = (geom.h * insetPerc2);
  float useinset2 = inseth2;
  if (insetw < inseth)
  {
  useinset = insetw;
  useinset2 = insetw2;

  }

  float radW = (float) (Math.hypot(geom.w, geom.w) * .5);
  float radH = (float) (Math.hypot(geom.h, geom.h) * .5);

  returnGeom = new GeomEllipse(p3f, radW, radH, 0f);
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;

  //returnGeom.setColor(1f, 0f, 0f, 0f);
  returnGeom.setColor(color);
  geom.setTranslate(-geom.w * .5f, -geom.h * .5f, 0f);
  returnGeom.addGeom(geom, true);




  //border is on the outside of the ellipse (could also make it the inside-- offer this option...)
  GeomEllipse borderCircle2 = new GeomEllipse(0f, 0f, 0f, radW + useinset, radH + useinset, useinset);
  returnGeom.addGeom(borderCircle2, true);
  borderCircle2.setColor(color);

  //new
  GeomEllipse borderCircle3 = new GeomEllipse(0f, 0f, .001f, radW + useinset2, radH + useinset2, useinset2);
  returnGeom.addGeom(borderCircle3, true);
  borderCircle3.setColor(color2);

  //gt2.backgroundColor = new Colorf(borderCircle.r, borderCircle.g,
  //        borderCircle.b, borderCircle.a); //background of entire bounds


  returnGeom.isSelectable = true; //true;
  borderCircle2.isSelectable = true; //true;
  geom.isSelectable = true; //true;

  geom.registerSelectableObject(returnGeom);
  geom.registerDraggableObject(returnGeom);
  geom.registerClickableObject(returnGeom);
  borderCircle2.registerSelectableObject(returnGeom);
  borderCircle2.registerDraggableObject(returnGeom);
  borderCircle2.registerClickableObject(returnGeom);
  //returnGeom.registerClickableObject(origGeom);
  return returnGeom;

  case CIRCLE:
  float rad = (float) (Math.hypot(geom.w, geom.h) * .5);

  returnGeom = new GeomCircle(p3f, 0f, rad, 0f, 360f, 64);
  returnGeom.state = new State();
  returnGeom.state.DEPTH_TEST = false;
  returnGeom.state.BLEND = false;

  returnGeom.setColor(0f, 0f, 0f, 0f);
  geom.setTranslate(-geom.w * .5f, -geom.h * .5f, 0f);
  returnGeom.addGeom(geom, true);




  //putting border on outside boundary
  GeomCircle borderCircle = new GeomCircle(0f, 0f, 0f, rad, rad + (rad * insetPerc), 0f, 360f, 64);
  returnGeom.addGeom(borderCircle, true);
  borderCircle.setColor(color);

  //gt2.backgroundColor = new Colorf(borderCircle.r, borderCircle.g,
  //        borderCircle.b, borderCircle.a); //background of entire bounds


  returnGeom.isSelectable = true; //true;
  borderCircle.isSelectable = true; //true;
  geom.isSelectable = true; //true;

  geom.registerSelectableObject(returnGeom);
  geom.registerDraggableObject(returnGeom);
  borderCircle.registerSelectableObject(returnGeom);
  borderCircle.registerDraggableObject(returnGeom);
  returnGeom.registerClickableObject(geom);
  return returnGeom;

  }


  geom.registerSelectableObject(returnGeom);
  returnGeom.registerClickableObject(geom);
  //gc2.registerClickableObject(gc2);

  geom.registerDraggableObject(returnGeom);
  returnGeom.registerSelectableObject(geom);
  return returnGeom;
  }
   */
  @Override
  public boolean checkIsCompletelyVisible()
  {
    //Point3f c1 = BehaviorismDriver.renderer.projectPoint(new Point3f(0f, 0f, 0f), modelview);
    //Point3f c2 = BehaviorismDriver.renderer.projectPoint(new Point3f(w, 0f, 0f), modelview);
    //Point3f c3 = BehaviorismDriver.renderer.projectPoint(new Point3f(w, h, 0f), modelview);
    //Point3f c4 = BehaviorismDriver.renderer.projectPoint(new Point3f(0f, h, 0f), modelview);
    Point3f c1 = MatrixUtils.project(new Point3f(0f, 0f, 0f), modelview);
    Point3f c2 = MatrixUtils.project(new Point3f(w, 0f, 0f), modelview);
    Point3f c3 = MatrixUtils.project(new Point3f(w, h, 0f), modelview);
    Point3f c4 = MatrixUtils.project(new Point3f(0f, h, 0f), modelview);


    //System.out.println("lower left: " + MatrixUtils.toString(c1));
    //System.out.println("lower right: " + MatrixUtils.toString(c2));
    //System.out.println("upper right: " + MatrixUtils.toString(c3));
    //System.out.println("upper left: " + MatrixUtils.toString(c4));
    return GeomUtils.checkIfPolygonIsCompletelyContainedInView(Renderer.screenBounds, c1, c2, c3, c4);
  }

  /** This version puts the Geom points in screen (pixel) coordinates and checks against the window dimensions (x,y) */
  @Override
  public boolean checkIsVisible()
  {
    System.out.println("\n\nin checkIsVisible");
//    Point3f c1 = BehaviorismDriver.renderer.projectPoint(new Point3f(0f, 0f, 0f), modelview);
//    Point3f c2 = BehaviorismDriver.renderer.projectPoint(new Point3f(w, 0f, 0f), modelview);
//    Point3f c3 = BehaviorismDriver.renderer.projectPoint(new Point3f(w, h, 0f), modelview);
//    Point3f c4 = BehaviorismDriver.renderer.projectPoint(new Point3f(0f, h, 0f), modelview);
    Point3f c1 = MatrixUtils.project(new Point3f(0f, 0f, 0f), modelview);
    Point3f c2 = MatrixUtils.project(new Point3f(w, 0f, 0f), modelview);
    Point3f c3 = MatrixUtils.project(new Point3f(w, h, 0f), modelview);
    Point3f c4 = MatrixUtils.project(new Point3f(0f, h, 0f), modelview);


    //System.out.println("lower left: " + MatrixUtils.toString(c1));
    //System.out.println("lower right: " + MatrixUtils.toString(c2));
    //System.out.println("upper right: " + MatrixUtils.toString(c3));
    //System.out.println("upper left: " + MatrixUtils.toString(c4));

    this.isVisible = GeomUtils.checkIfPolygonIsInView(Renderer.screenBounds, c1, c2, c3, c4);
    return this.isVisible;

  /*
  if (GeomUtils.checkIfPolygonIsInView(Renderer.screenBounds, c1, c2, c3, c4) == false)
  {
  isVisible = false;
  //System.out.println("using checkIfRect... [" + this + "] is not in view");
  return false; 
  }
  else
  {
  isVisible = true;
  //System.out.println("using checkIfRect... [" + this + "] is in view");
  return true;
  }
   */
  }

  public void drawSquare(GL2 gl, float x, float y, float w, float h)
  {
    gl.glBegin(GL_QUADS);

    gl.glTexCoord2f(0, 0);
    gl.glVertex2f(x, y);
    gl.glTexCoord2f(1, 0);
    gl.glVertex2f(x + w, y);
    gl.glTexCoord2f(1, 1);
    gl.glVertex2f(x + w, y + h);
    gl.glTexCoord2f(0, 1);
    gl.glVertex2f(x, y + h);

    gl.glEnd();
  }

  public String toString()
  {

    return super.toString() + ", w/h = " + w + "/" + h;
  }
  /** this version puts the Geom points in world Coords and checks against the frustum.
   * This version is flawed, although it is the usual method of doing things.
   * For 3D objects i will probably use this with sphere bounding 
  public boolean checkIsVisible()
  {
  Point3d ppp1 = MatrixUtils.getGeomPointInWorldCoordinates(
  new Point3d(0,0,0), modelview, RenderUtils.getCamera().modelview);
  Point3d ppp2 = MatrixUtils.getGeomPointInWorldCoordinates(
  new Point3d(w,0,0), modelview, RenderUtils.getCamera().modelview);
  Point3d ppp3 = MatrixUtils.getGeomPointInWorldCoordinates(
  new Point3d(w,h,0), modelview, RenderUtils.getCamera().modelview);
  Point3d ppp4 = MatrixUtils.getGeomPointInWorldCoordinates(
  new Point3d(0,h,0), modelview, RenderUtils.getCamera().modelview);
  
  //first check points. If any points are in frustrum, then the origGeom is visible
  if (Renderer.arePointsInFrustum(ppp1, ppp2, ppp3, ppp4))
  {
  this.isVisible = true;
  return true;
  }
  
  //second check to see if lines between points are in frustrum, if yes then origGeom is visible
  if (Renderer.areLinesInFrustum(ppp1, ppp2, ppp3, ppp4) == true)
  {
  this.isVisible = true;
  return true;
  }
  //then its not in the frustrum!
  this.isVisible = false;
  return false;
  }
   */
}

