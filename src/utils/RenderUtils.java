/* RenderUtils.java ~ Jun 18, 2009 */
package utils;

import behaviorism.BehaviorismDriver;
import geometry.Geom;
import geometry.GeomPoint;
import geometry.GeomPoly;
import geometry.GeomRect;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import renderers.Renderer;
import renderers.cameras.Cam;

/**
 *
 * @author angus
 */
public class RenderUtils
{

  /**
   * getWorldCoordsForScreenCoord transforms a mouse point (in screen coordinates)
   * into "world" coordinates (ie, the coordinates after the camera has been positioned).
   *
   * //assumes you are picking on an object with some depth
   * //otherwise uses far edge of clipping plane (ie z=100f)
   * //which obviously distorts true world coords.
   * (TO DO-- add a check for this!)
   *
   * @param x
   * @param y
   * @return - a double array holding the x, y, z world coordinate of the screen point
   */
  public static double[] getWorldCoordsForScreenCoord(int x, int y)
  {
    Renderer rj = getRenderer();

    double modelview[] = new double[16];
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double worldCoords[] = new double[3];

    //projection = MatrixUtils.perspective(rj.cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, Renderer.nearPlane, Renderer.farPlane);
    projection = rj.cam.projection; //MatrixUtils.perspective(rj.cam.fovy, (float) BehaviorismDriver.canvasWidth / BehaviorismDriver.canvasHeight, Renderer.nearPlane, Renderer.farPlane);
    //modelview = rj.cam.perspective();
    modelview = rj.cam.modelview;
    viewport = RenderUtils.getCamera().viewport;

    //invert y value properly
    y = (int) ((float) viewport[3] - (float) y);

    //get z value from scene
    FloatBuffer zBuf = FloatBuffer.allocate(1);
    rj.gl.glReadPixels(x, (int) y, 1, 1, GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, zBuf);
    float z = zBuf.get();

    //unproject mouse coords into world coords!
    rj.glu.gluUnProject((double) x, (double) y, (double) z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      worldCoords, 0);

    return worldCoords;
  }

  /**
   * the z variable is between 0 and 1 (the near plane and the far plane!)
   */
  public static Point3f getWorldCoordsForScreenCoord(int x, int y, double z, double[] modelview)
  {
    Renderer rj = getRenderer();

    double projection[] = new double[16];
    int viewport[] = new int[4];
    double worldCoords[] = new double[3];

    rj.gl.glGetDoublev(rj.gl.GL_PROJECTION_MATRIX, projection, 0);
    rj.gl.glGetIntegerv(rj.gl.GL_VIEWPORT, viewport, 0);

    //unproject mouse coords into world coords!
    rj.glu.gluUnProject((double) x, (double) y, (double) z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      worldCoords, 0);

    return new Point3f((float) worldCoords[0], (float) worldCoords[1], (float) worldCoords[2]);
  }

  public static Rectangle2D.Float getScreenRectangleForWorldCoords(GeomRect gr)
  {
    Path2D.Float p2d = getScreenShapeForWorldCoords(gr);
    return GeomUtils.pathToRect(p2d);
  }

  //gluProject maps object coords to screen coords
  //public Shape getScreenShapeForWorldCoords(GL gl, GLU glu, Geom g)
  public static Path2D.Float getScreenShapeForWorldCoords(Geom g)
  {
    Renderer rj = getRenderer();

    Path2D.Float p2f = null;
    double projection[] = new double[16];
    int viewport[] = new int[4];

    projection = RenderUtils.getCamera().projection;
    viewport = RenderUtils.getCamera().viewport;

    if (g instanceof GeomRect)
    {
      p2f = projectGeomRect((GeomRect) g, g.modelview, projection, viewport);
    }
    else if (g instanceof GeomPoly)
    {
      p2f = projectGeomPoly((GeomPoly) g, g.modelview, projection, viewport);
    }


    return p2f;
  }

  public static Renderer getRenderer()
  {
    return BehaviorismDriver.renderer;
  }

  public static Cam getCamera()
  {
    return BehaviorismDriver.renderer.currentWorld.cam;
  }


  public static List<Float> getScreenRectInGeomCoordnates(Geom g, Rectangle2D.Float r2f)
  {

    return getScreenRectInGeomCoordnates(g, (int) r2f.x, (int) r2f.y, (int) r2f.width, (int) r2f.height);
  }

  public static List<Float> getScreenRectInGeomCoordnates(Geom g, int x, int y, int w, int h)
  {

    List<Float> geomPts = new ArrayList<Float>();

    Point3d p3f_xy = rayIntersect(g, (int) x, (int) y);
    Point3d p3f_wh = rayIntersect(g, (int) (x + w), (int) (y + h));

    geomPts.add((float) p3f_xy.x);
    geomPts.add((float) p3f_xy.y);
    geomPts.add((float) (p3f_wh.x - p3f_xy.x));
    geomPts.add((float) (p3f_wh.y - p3f_xy.y));

    return geomPts;
  }

  public static List<Point3f> getScreenPointsInGeomCoordnates(Geom g, List<Point3f> screenPts)
  {
    List<Point3f> geomPts = new ArrayList<Point3f>();

    for (Point3f s_p3f : screenPts)
    {
      Point3f g_p3f = MatrixUtils.toPoint3f(rayIntersect(g, (int) s_p3f.x, (int) s_p3f.y));
      geomPts.add(g_p3f);
    }

    return geomPts;
  }

  public static Point3d rayIntersect(Geom g, int x, int y)
  {
    return rayIntersect(g, x, y, new Point3d());
  }

  public static Point3d rayIntersect(Geom g, int x, int y, Point3d offsetPt)
  {
    Renderer rj = getRenderer();

    //1. get values of ray (at screen coords) at near and far points (in world coordinates)
    //2. get Geom in world coordinates
    //3. get percentage where ray intersects geom's z-value
    //4. use that percentage to calculate x and y offset
    //5. transfrom point into parent's coordinates
    //6. return point

    //double modelview[] = new double[16];
    double projection[] = new double[16];
    int viewport[] = new int[4];
    double wcsN[] = new double[3];
    double wcsF[] = new double[3];
    //double offsets[] = new double[3];

    projection = RenderUtils.getCamera().projection;
    viewport = RenderUtils.getCamera().viewport;

    //invert y value properly
    y = (int) ((float) viewport[3] - (float) y);

    rj.glu.gluUnProject((double) x, (double) y, 0.0, //-1?
      RenderUtils.getCamera().modelview, 0,
      projection, 0,
      viewport, 0,
      wcsN, 0);

    rj.glu.gluUnProject((double) x, (double) y, 1.0,
      RenderUtils.getCamera().modelview, 0,
      projection, 0,
      viewport, 0,
      wcsF, 0);

    Point3d nearPt = new Point3d(wcsN[0], wcsN[1], wcsN[2]);
    Point3d farPt = new Point3d(wcsF[0], wcsF[1], wcsF[2]);

    //now get Geom in world coords
    Point3d geomPt_wc;

    Point3d geomPt = new Point3d(g.anchor.x + offsetPt.x, g.anchor.y + offsetPt.y, g.anchor.z + offsetPt.z);
    if (g.parent != null)
    {
      geomPt_wc = MatrixUtils.getGeomPointInWorldCoordinates(geomPt, g.parent.modelview, RenderUtils.getCamera().modelview);
    }
    else
    {
      geomPt_wc = geomPt;
    }

    double maxz = GeomUtils.euclidianDistance(nearPt.z, farPt.z);
    double tryz = GeomUtils.euclidianDistance(nearPt.z, geomPt_wc.z);
    double perc = tryz / maxz;

    double rangex = GeomUtils.euclidianDistance(nearPt.x, farPt.x);
    double addx = rangex * perc;
    double finalx;

    if (farPt.x < nearPt.x)
    {
      finalx = nearPt.x - addx;
    }
    else
    {
      finalx = nearPt.x + addx;
    }

    double rangey = GeomUtils.euclidianDistance(nearPt.y, farPt.y);
    double addy = rangey * perc;
    double finaly;
    if (farPt.y < nearPt.y)
    {
      finaly = nearPt.y - addy;
    }
    else
    {
      finaly = nearPt.y + addy;
    }

    Point3d returnPt = new Point3d(finalx, finaly, geomPt_wc.z);

    if (g.parent != null)
    {
      returnPt = MatrixUtils.getWorldPointInGeomCoordinates(returnPt, RenderUtils.getCamera().modelview, g.parent.modelview);
    }

    return new Point3d(returnPt.x - offsetPt.x, returnPt.y - offsetPt.y, returnPt.z - offsetPt.z);
  }

  /**
   * currently not being used... investigate...
   * @param gl
   * @param mv
   * @param g
   * @return
   */
  /*
  public static double[] transformWithoutRotation(GL gl, double[] mv, Geom g)
  {
  gl.glPushMatrix(); ////PUSH current matrix onto stack

  gl.glLoadIdentity();
  gl.glMultMatrixd(mv, 0);

  // translate command
  gl.glTranslatef(g.anchor.x, g.anchor.y, g.anchor.z);

  // scale commands
  gl.glTranslatef(g.scaleAnchor.x, g.scaleAnchor.y, g.scaleAnchor.z);
  gl.glScalef((float) g.scale.x, (float) g.scale.y, (float) g.scale.z);
  gl.glTranslatef(-g.scaleAnchor.x, -g.scaleAnchor.y, -g.scaleAnchor.z);


  double transformedMV[] = new double[16];
  gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, transformedMV, 0);

  gl.glPopMatrix();
  return transformedMV;
  }
   */
  //TODO - replace gluProject with MatrixUtils.project
  public static int getWidthOfObjectInPixels(Geom g, float inset)
  {
    Renderer rj = getRenderer();

    //setPerspective3D();
    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double screenCoords[] = new double[3];

    //gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
    //gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    rj.glu.gluProject(0f, 0f, 0f,
      g.modelview, 0,
      RenderUtils.getCamera().projection, 0,
      RenderUtils.getCamera().viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];
    double y1 = screenCoords[1];

    rj.glu.gluProject(g.w, 0f, 0f,
      g.modelview, 0,
      RenderUtils.getCamera().projection, 0,
      RenderUtils.getCamera().viewport, 0,
      screenCoords, 0);

    double x2 = screenCoords[0];
    double y2 = screenCoords[1];

    int dist = (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
    return dist;
  }

  public static Point geomPointToScreenPoint(Point3f geomPoint, double[] modelview, double[] projection, int[] viewport)
  {
    Renderer rj = getRenderer();

    double screenCoords[] = new double[3];
    rj.glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];
    double y1 = screenCoords[1];

    return new Point((int) x1, (int) y1);
  }

  public static int getXOfObjectInPixels(Geom g, double[] modelview, double[] projection, int[] viewport)
  {
    Renderer rj = getRenderer();

    double screenCoords[] = new double[3];
    rj.glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];

    return (int) x1;
  }

  public static int getYOfObjectInPixels(Geom g, double[] modelview, double[] projection, int[] viewport)
  {
    Renderer rj = getRenderer();

    double screenCoords[] = new double[3];
    rj.glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double y1 = screenCoords[1];

    return (int) y1;
  }

  public static int getWidthOfObjectInPixels(Geom g, float inset, double[] modelview, double[] projection, int[] viewport)
  {
    Renderer rj = getRenderer();

    //setPerspective3D();
    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double screenCoords[] = new double[3];

    //gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
    //gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    rj.glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x1 = screenCoords[0];
    double y1 = screenCoords[1];

    rj.glu.gluProject(g.w, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x2 = screenCoords[0];
    double y2 = screenCoords[1];

    rj.glu.gluProject(inset, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);

    double x3 = screenCoords[0];
    double y3 = screenCoords[1];

    int insetdist = (int) (GeomUtils.euclidianDistance(x1, y1, x3, y3));
    //int dist = (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
    int dist = (int) (GeomUtils.euclidianDistance(x1, y1, x2, y2));
    return dist - insetdist;
  }

  public static int getHeightOfObjectInPixels(Geom g, float inset)
  {
    Renderer rj = getRenderer();

    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double windowCoords[] = new double[3];

//		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
//		gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    rj.glu.gluProject(0f, 0f, 0f,
      g.modelview, 0,
      RenderUtils.getCamera().projection, 0,
      RenderUtils.getCamera().viewport, 0,
      windowCoords, 0);

    double x1 = windowCoords[0];
    double y1 = windowCoords[1];

    rj.glu.gluProject(0f, g.h, 0f,
      g.modelview, 0,
      RenderUtils.getCamera().projection, 0,
      RenderUtils.getCamera().viewport, 0,
      windowCoords, 0);
    double x2 = windowCoords[0];
    double y2 = windowCoords[1];

    return (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
  }

  public static int getHeightOfObjectInPixels(Geom g, float inset, double[] modelview, double[] projection, int[] viewport)
  {
    Renderer rj = getRenderer();

    //double projectionMatrix[] = new double[16];
    //int viewportBounds[] = new int[4];
    double windowCoords[] = new double[3];

//		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
//		gl.glGetIntegerv(GL.GL_VIEWPORT, viewportBounds, 0);

    rj.glu.gluProject(0f, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);

    double x1 = windowCoords[0];
    double y1 = windowCoords[1];

    rj.glu.gluProject(0f, g.h, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);
    double x2 = windowCoords[0];
    double y2 = windowCoords[1];


    rj.glu.gluProject(inset, 0f, 0f,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);

    double x3 = windowCoords[0];
    double y3 = windowCoords[1];

    int insetdist = (int) (GeomUtils.euclidianDistance(x1, y1, x3, y3));

    //return (int) ((GeomUtils.euclidianDistance(x1, y1, x2, y2)) * (1f - inset));
    int dist = (int) (GeomUtils.euclidianDistance(x1, y1, x2, y2));

    return dist - insetdist;
  }

  public static Path2D.Float projectGeomRect(GeomRect g,
    double[] modelview, double[] projection, int[] viewport)
  {

    Renderer rj = getRenderer();

    Path2D.Float p2f = new Path2D.Float();

    double hx = 0.0;
    double hy = 0.0;
    double hz = 0.0;
    //double hx = (double)g.anchor.x;
    //double hy = (double)g.anchor.y;
    //double hz = (double)g.anchor.z;
    double screenCoords[] = new double[3];

    //System.out.print("screenCoords[2] : ");

    rj.glu.gluProject(hx, hy, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.moveTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    rj.glu.gluProject(hx + g.w, hy, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.lineTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    rj.glu.gluProject(hx + g.w, hy + g.h, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.lineTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    rj.glu.gluProject(hx, hy + g.h, hz,
      modelview, 0,
      projection, 0,
      viewport, 0,
      screenCoords, 0);
    p2f.lineTo((float) screenCoords[0], (float) (BehaviorismDriver.canvasHeight - screenCoords[1]));
    //System.out.print(" " + screenCoords[2]);

    p2f.closePath();

    //System.out.println("");
    return p2f;
  }

  public static Path2D.Float projectGeomPoly(GeomPoly g,
    double[] modelview, double[] projection, int[] viewport)
  {
    Renderer rj = getRenderer();

    Path2D.Float p2f = new Path2D.Float();

    double hx = 0.0; //(double)g.anchor.x;
    double hy = 0.0; //(double)g.anchor.y;
    double hz = 0.0; //(double)g.anchor.z;

    double windowCoords[] = new double[3];

    GeomPoint gp = g.vertices.get(0);
    //GeomPoint gp = g.selectableBoundary.get(0);

    rj.glu.gluProject(hx + gp.anchor.x, hy + gp.anchor.y, hz + gp.anchor.z,
      modelview, 0,
      projection, 0,
      viewport, 0,
      windowCoords, 0);
    p2f.moveTo((float) windowCoords[0], (float) (BehaviorismDriver.canvasHeight - windowCoords[1]));

    //System.out.println("g.verts.size = " + g.selectableBoundary.size());
    for (int i = 1; i < g.vertices.size(); i++) //for (int i = 1; i < g.selectableBoundary.size(); i++)
    {
      gp = g.vertices.get(i);
      //gp = g.selectableBoundary.get(i);

      rj.glu.gluProject(hx + gp.anchor.x, hy + gp.anchor.y, hz + gp.anchor.z,
        modelview, 0,
        projection, 0,
        viewport, 0,
        windowCoords, 0);
      p2f.lineTo((float) windowCoords[0], (float) (BehaviorismDriver.canvasHeight - windowCoords[1]));
    }

    p2f.closePath();

    //GeomUtils.printPath(p2f);

    return p2f;
  }

  public static boolean getBoolean(GL gl, int param)
  {
    ByteBuffer bb = ByteBuffer.allocate(1);
    gl.glGetBooleanv(param, bb);

    if (bb.get(0) == 0)
    {
      return false;
    }
    return true;
  }

  public static void extractFrustum()
  {
    Renderer rj = getRenderer();

    double[][] frustum = new double[6][4];

    double[] modl = RenderUtils.getCamera().modelview;
    double[] proj = RenderUtils.getCamera().projection;
    double[] clip = new double[16];
    double t;

    /* Combine the two matrices (multiply projectionMatrix by modelview) */
    clip[ 0] = modl[ 0] * proj[ 0] + modl[ 1] * proj[ 4] + modl[ 2] * proj[ 8] + modl[ 3] * proj[12];
    clip[ 1] = modl[ 0] * proj[ 1] + modl[ 1] * proj[ 5] + modl[ 2] * proj[ 9] + modl[ 3] * proj[13];
    clip[ 2] = modl[ 0] * proj[ 2] + modl[ 1] * proj[ 6] + modl[ 2] * proj[10] + modl[ 3] * proj[14];
    clip[ 3] = modl[ 0] * proj[ 3] + modl[ 1] * proj[ 7] + modl[ 2] * proj[11] + modl[ 3] * proj[15];

    clip[ 4] = modl[ 4] * proj[ 0] + modl[ 5] * proj[ 4] + modl[ 6] * proj[ 8] + modl[ 7] * proj[12];
    clip[ 5] = modl[ 4] * proj[ 1] + modl[ 5] * proj[ 5] + modl[ 6] * proj[ 9] + modl[ 7] * proj[13];
    clip[ 6] = modl[ 4] * proj[ 2] + modl[ 5] * proj[ 6] + modl[ 6] * proj[10] + modl[ 7] * proj[14];
    clip[ 7] = modl[ 4] * proj[ 3] + modl[ 5] * proj[ 7] + modl[ 6] * proj[11] + modl[ 7] * proj[15];

    clip[ 8] = modl[ 8] * proj[ 0] + modl[ 9] * proj[ 4] + modl[10] * proj[ 8] + modl[11] * proj[12];
    clip[ 9] = modl[ 8] * proj[ 1] + modl[ 9] * proj[ 5] + modl[10] * proj[ 9] + modl[11] * proj[13];
    clip[10] = modl[ 8] * proj[ 2] + modl[ 9] * proj[ 6] + modl[10] * proj[10] + modl[11] * proj[14];
    clip[11] = modl[ 8] * proj[ 3] + modl[ 9] * proj[ 7] + modl[10] * proj[11] + modl[11] * proj[15];

    clip[12] = modl[12] * proj[ 0] + modl[13] * proj[ 4] + modl[14] * proj[ 8] + modl[15] * proj[12];
    clip[13] = modl[12] * proj[ 1] + modl[13] * proj[ 5] + modl[14] * proj[ 9] + modl[15] * proj[13];
    clip[14] = modl[12] * proj[ 2] + modl[13] * proj[ 6] + modl[14] * proj[10] + modl[15] * proj[14];
    clip[15] = modl[12] * proj[ 3] + modl[13] * proj[ 7] + modl[14] * proj[11] + modl[15] * proj[15];

    /* Extract the numbers for the RIGHT plane */
    frustum[0][0] = clip[ 3] - clip[ 0];
    frustum[0][1] = clip[ 7] - clip[ 4];
    frustum[0][2] = clip[11] - clip[ 8];
    frustum[0][3] = clip[15] - clip[12];

    /* Normalize the result */
    t = Math.sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2]);
    //frustum[0][0] /= t;
    //frustum[0][1] /= t;
    //frustum[0][2] /= t;
    //frustum[0][3] /= t;

    /* Extract the numbers for the LEFT plane */
    frustum[1][0] = clip[ 3] + clip[ 0];
    frustum[1][1] = clip[ 7] + clip[ 4];
    frustum[1][2] = clip[11] + clip[ 8];
    frustum[1][3] = clip[15] + clip[12];

    /* Normalize the result */
    t = Math.sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2]);
    //frustum[1][0] /= t;
    //frustum[1][1] /= t;
    //frustum[1][2] /= t;
    //frustum[1][3] /= t;

    /* Extract the BOTTOM plane */
    frustum[2][0] = clip[ 3] + clip[ 1];
    frustum[2][1] = clip[ 7] + clip[ 5];
    frustum[2][2] = clip[11] + clip[ 9];
    frustum[2][3] = clip[15] + clip[13];

    /* Normalize the result */
    t = Math.sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2]);
    //frustum[2][0] /= t;
    //frustum[2][1] /= t;
    //frustum[2][2] /= t;
    //frustum[2][3] /= t;

    /* Extract the TOP plane */
    frustum[3][0] = clip[ 3] - clip[ 1];
    frustum[3][1] = clip[ 7] - clip[ 5];
    frustum[3][2] = clip[11] - clip[ 9];
    frustum[3][3] = clip[15] - clip[13];

    /* Normalize the result */
    t = Math.sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2]);
    //frustum[3][0] /= t;
    //frustum[3][1] /= t;
    //frustum[3][2] /= t;
    //frustum[3][3] /= t;

    /* Extract the FAR plane */
    frustum[4][0] = clip[ 3] - clip[ 2];
    frustum[4][1] = clip[ 7] - clip[ 6];
    frustum[4][2] = clip[11] - clip[10];
    frustum[4][3] = clip[15] - clip[14];

    /* Normalize the result */
    t = Math.sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2]);
    //frustum[4][0] /= t;
    //frustum[4][1] /= t;
    //frustum[4][2] /= t;
    //frustum[4][3] /= t;

    /* Extract the NEAR plane */
    frustum[5][0] = clip[ 3] + clip[ 2];
    frustum[5][1] = clip[ 7] + clip[ 6];
    frustum[5][2] = clip[11] + clip[10];
    frustum[5][3] = clip[15] + clip[14];

    /* Normalize the result */
    t = Math.sqrt(frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1] + frustum[5][2] * frustum[5][2]);
    //frustum[5][0] /= t;
    //frustum[5][1] /= t;
    //frustum[5][2] /= t;
    //frustum[5][3] /= t;

    rj.frustum = frustum;
  }

  public static boolean isLineInFrustum(Point3d p3d1, Point3d p3d2)
  {
    return isLineInFrustum((float) p3d1.x, (float) p3d1.y, (float) p3d1.z,
      (float) p3d2.x, (float) p3d2.y, (float) p3d2.z);
  }

  //this isn't a perfect test, it returns some false positives.
  //I check to make sure that the line crosses two planes,
  //but it still could return results that are actually outside of the
  //frustum!
  public static boolean isLineInFrustum(float x1, float y1, float z1,
    float x2, float y2, float z2)
  {
    Renderer rj = getRenderer();
    //System.out.printf("in isLineInFrustum... (%f/%f/%f), (%f/%f/%f)\n", x1, y1, z1, x2,y2, z2);
    int crossings = 0;
    double top,
      bot,
      u;
    for (int p = 0; p < 6; p++)
    {
      top = rj.frustum[p][0] * x1 + rj.frustum[p][1] * y1 + rj.frustum[p][2] * z1 + rj.frustum[p][3];
      bot = rj.frustum[p][0] * (x1 - x2) + rj.frustum[p][1] * (y1 - y2) + rj.frustum[p][2] * (z1 - z2);
      u = top / bot;

      //System.out.println("u = " + u);
      if (u > 0 && u < 1)
      {
        /*
        System.out.println("p = " + p + ": intersects plane! " + frustum[p][0] + " " +
        frustum[p][1] + " " +
        frustum[p][2] + " " +
        frustum[p][3]);
         */
        crossings++;
      }
    }

    if (crossings > 1)
    {
      return true;
    }
    //System.out.println("crossings = " + crossings);
    return false;
  }

  /** Assuming that lines are between each point, e.g. p1-->p2, and p2-->p3, then p3-->p1 */
  public static boolean areLinesInFrustum(Point3d... pts)
  {
    for (int i = 0; i < pts.length - 1; i++)
    {
      if (isLineInFrustum(pts[i], pts[i + 1]) == true)
      {
        return true;
      }
    }

    if (pts.length > 2) //check last line
    {
      if (isLineInFrustum(pts[pts.length - 1], pts[0]) == true)
      {
        return true;
      }
    }

    return false;
  }

  public static boolean arePointsInFrustum(Point3d... pts)
  {
    for (Point3d pt : pts)
    {
      if (isPointInFrustum((float) pt.x, (float) pt.y, (float) pt.z))
      {
        return true;
      }
    }

    return false;
  }

  public static boolean isPointInFrustum(Point3d p3d)
  {
    return isPointInFrustum((float) p3d.x, (float) p3d.y, (float) p3d.z);
  }

  public static boolean isPointInFrustum(float x, float y, float z)
  {
    Renderer rj = getRenderer();
    for (int p = 0; p < 6; p++)
    {
      if (rj.frustum[p][0] * x + rj.frustum[p][1] * y + rj.frustum[p][2] * z + rj.frustum[p][3] <= 0)
      {
        return false;
      }
    }
    return true;
  }
}
