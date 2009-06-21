/* GeomCircle.java (created on October 26, 2007, 8:18 PM) */
package geometry;
//hohoho this is MASTER
import behaviorism.BehaviorismDriver;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.vecmath.Point3f;
import handlers.MouseHandler;
import textures.TextureImage;
import utils.RenderUtils;

public class GeomCircle extends GeomPoly
{

  public float innerRadius = 0f;
  public float outerRadius = 1f;
  public int resolution = 32;
  public float startAngle = 0f;
  public float endAngle = 360f;

  public GeomCircle(float x, float y, float z, float outerRadius)
  {
    super(x, y, z);
    initialize(0f, outerRadius, 0f, 360f, 32);
  }

  public GeomCircle(Point3f centerPt, float radius)
  {
    super(centerPt);
    initialize(0f, radius, 0f, 360f, 32);
  }

  public GeomCircle(Point3f centerPt, float radius, int resolution)
  {
    super(centerPt);
    initialize(0f, radius, 0f, 360f, resolution);
  }

  public GeomCircle(float x, float y, float z, float radius, int resolution)
  {
    super(x, y, z);
    initialize(0f, radius, 0f, 360f, resolution);
  }

  public GeomCircle(Point3f centerPt, float innerRadius, float outerRadius, int resolution)
  {
    super(centerPt);
    initialize(innerRadius, outerRadius, 0f, 360f, resolution);
  }

  public GeomCircle(Point3f centerPt, float innerRadius, float outerRadius, int resolution, TextureImage ti)
  {
    super(centerPt);
    initialize(innerRadius, outerRadius, 0f, 360f, resolution);
    attachTexture(ti);
  }

  public GeomCircle(float x, float y, float z, float innerRadius, float outerRadius, float startAngle, float endAngle, int resolution)
  {
    super(x, y, z);
    initialize(innerRadius, outerRadius, startAngle, endAngle, resolution);
  }

  public GeomCircle(Point3f centerPt, float innerRadius, float outerRadius, float startAngle, float endAngle, int resolution)
  {
    super(centerPt);
    initialize(innerRadius, outerRadius, startAngle, endAngle, resolution);
  }

  public void initialize(float innerRadius, float outerRadius, float startAngle, float endAngle, int resolution)
  {
    this.isSelectable = true;
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
    //this.h = h;
    //this.scale.x = 1f;
    //this.scale.y = 1f;
    //this.scale.z = 1f;
    this.scale.x = 1;
    this.scale.y = 1;
    this.scale.z = 1;
    this.resolution = resolution;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
    setVerts();
  }

  public void setVerts()
  {
    double inc;
    double angle;
    vertices.clear();

    angle = startAngle;
    inc = (endAngle - startAngle) / (double) resolution;

    if (startAngle == 0f && endAngle == 360f) //Circle
    {
      inc = (endAngle - startAngle) / (double) resolution;

      for (int i = 0; i <= resolution; i++)
      {
        this.vertices.add(new GeomPoint((float) (0 + (outerRadius * Math.cos(Math.toRadians(angle)))),
          (float) (0 + (outerRadius * Math.sin(Math.toRadians(angle)))),
          translate.z //z
          ));
        angle += inc;

      }
      angle -= inc;
      for (int i = resolution; i >= 0; i--)
      {
        this.vertices.add(new GeomPoint((float) (0 + (innerRadius * Math.cos(Math.toRadians(angle)))),
          (float) (0 + (innerRadius * Math.sin(Math.toRadians(angle)))),
          translate.z //z
          ));
        angle -= inc;
      }
    }
    else //Disk
    {
      inc = (endAngle - startAngle) / (double) resolution;

      for (int i = 0; i <= resolution; i++)
      {
        this.vertices.add(new GeomPoint((float) (0 + (outerRadius * Math.cos(Math.toRadians(angle)))),
          (float) (0 + (outerRadius * Math.sin(Math.toRadians(angle)))),
          translate.z //z
          ));
        angle += inc;

      }
      angle -= inc;

      if (innerRadius > 0f) //reverse through points along inner radius
      {
        for (int i = resolution; i >= 0; i--)
        {
          this.vertices.add(new GeomPoint((float) (0 + (innerRadius * Math.cos(Math.toRadians(angle)))),
            (float) (0 + (innerRadius * Math.sin(Math.toRadians(angle)))),
            translate.z //z
            ));
          angle -= inc;
        }
      }
      else //just add the one center point
      {
        this.vertices.add(new GeomPoint(0f, 0f, 0f));
      }
    }
  }

  public void draw(GL gl)
  {
    if (this == MouseHandler.selectedGeom)
    {
      //System.out.println("offset = " + offset);
    }

    if (!updateTextures())
    {
      return;
    }

    GLU glu = BehaviorismDriver.renderer.glu;

    //get Tesselator object
    GLUtessellator tobj = BehaviorismDriver.renderer.tessellationObject;

    if (tobj == null)
    {
      //error-- tesselationObjbect not ready yet, or initialized wrong (in RendererJogl!)
      return;
    }

    //get state variables
    boolean depthTest = RenderUtils.getBoolean(gl, GL.GL_DEPTH_TEST);
    if (depthTest == false && isSelectable == true)
    {
      //then we need to render it invisibly with DEPTH_TEST on so that we can pick it
      gl.glEnable(GL.GL_DEPTH_TEST);

      if (startAngle == 0f && endAngle == 360f)
      {
        gl.glColor4f(0f, 0f, 0f, 0f);
        drawCircleTesselation(gl, glu, tobj, offset);
      }
      else
      {
        gl.glColor4f(0f, 0f, 0f, 0f);
        drawDiskTesselation(gl, glu, tobj, offset);
      }

      gl.glDisable(GL.GL_DEPTH_TEST);
    }

    if (this.textures != null)
    {
      bindTexture(0);
      //this.textures.get(0).texture.bind();
      //gl.glBindTexture(GL_TEXTURE_2D, 0);

      gl.glEnable(GL_TEXTURE_2D);
    }
//    gl.glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
//    gl.glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT );
//    gl.glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

    gl.glColor4fv(color.array(), 0);

    //gl.glColor4f(r, g, b, a);
    gl.glPushMatrix();
    gl.glScalef(1f, -1f, 1f);

    if (startAngle == 0f && endAngle == 360f)
    {
      drawCircleTesselation(gl, glu, tobj, offset);
    }
    else
    {
      drawDiskTesselation(gl, glu, tobj, offset);
    }
    gl.glPopMatrix();

    if (this.textures != null)
    {

      gl.glDisable(GL_TEXTURE_2D);
    }
  }

  private void drawDiskTesselation(GL gl, GLU glu, GLUtessellator tobj, float offset)
  {
    //glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);

    glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
    glu.gluTessBeginPolygon(tobj, null);

    glu.gluTessBeginContour(tobj);

    for (int i = 0; i < vertices.size(); i++)
    {
      GeomPoint gp = vertices.get(i);
      double[] dubArr = new double[]
      {
        (gp.translate.x),
        (gp.translate.y),
        (gp.translate.z + offset),
        (gp.translate.x / (outerRadius * 2) + .5),
        (gp.translate.y / (outerRadius * 2) + .5)
      //Utils.random(), Utils.random(), Utils.random()
      };

      glu.gluTessVertex(tobj, dubArr, 0, dubArr);
    }

    glu.gluTessEndContour(tobj);
    glu.gluTessEndPolygon(tobj);
  }

  private void drawCircleTesselation(GL gl, GLU glu, GLUtessellator tobj, float offset)
  {

    //glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);
    glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
    glu.gluTessBeginPolygon(tobj, null);


    glu.gluTessBeginContour(tobj);
    for (int i = 0; i < vertices.size() / 2; i++)
    {
      GeomPoint gp = vertices.get(i);
      System.out.println("gp = " + gp);
      double[] dubArr = new double[]
      {
        gp.translate.x,
        gp.translate.y,
        gp.translate.z + offset,
        (gp.translate.x / (outerRadius * 2) + .5),
        (gp.translate.y / (outerRadius * 2) + .5)
      //Utils.random(), Utils.random(), Utils.random()
      };

      glu.gluTessVertex(tobj, dubArr, 0, dubArr);
    }
    glu.gluTessEndContour(tobj);


    if (innerRadius > 0f)
    {
      float perc = innerRadius / outerRadius;
      glu.gluTessBeginContour(tobj);

      //for (int i =0; i < vertsSize(); i++)
      for (int i = vertices.size() / 2; i < vertices.size(); i++)
      {
        GeomPoint p3f = vertices.get(i);

        double[] dubArr = new double[]
        {
          (p3f.translate.x),
          (p3f.translate.y),
          ((p3f.translate.z) + offset),
          (p3f.translate.x / (outerRadius * 2) + .5),
          (p3f.translate.y / (outerRadius * 2) + .5)
        };

        glu.gluTessVertex(tobj, dubArr, 0, dubArr);
      }


      glu.gluTessEndContour(tobj);

    }

    glu.gluTessEndPolygon(tobj);
  //glu.gluDeleteTess(tobj); //what does this do?

  }

  /*
  public void determineRotateAnchor(RotateEnum rotatePosition)
  {
    switch (rotatePosition)
    {
      case CENTER:
        this.rotateAnchor = new GeomPoint(0f, 0f, 0f);
        break;
    }

  }
   */
  /*
  public void handleDoubleClick(MouseEvent me)
  {
  System.out.println("DOUBLE CLICK!!!!");
  }
   */
}
