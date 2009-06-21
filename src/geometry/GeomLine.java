/* GeomLine.java (created on August 30, 2007, 2:19 PM) */
package geometry;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import utils.MatrixUtils;

/**  
GeomLine *requires* that the endpoints of the line are part of the scene graph hierarchy in some way,
either because they are GeomPoints that have been added directly with them addGeom method,
or because they are part of some more complicated Geom which has a real modelview. If you want to create a 
simple line for testing, etc, then you can use GeomLine. 
 */
public class GeomLine extends Geom
{

  public Geom firstGeom = null;
  public Geom secondGeom = null;
  public GeomPoint firstOffsetPt = null;
  public GeomPoint secondOffsetPt = null;
  public float lineWidth = 1f;
  public float boldLineWidth = 5f;

  public boolean activateEndPoints = true;
  float pointSize = 8f;

  public GeomLine(Geom firstGeom, Point3f p3f_offset1,
    Geom secondGeom, Point3f p3f_offset2)
  {
    this.firstGeom = firstGeom;
    this.secondGeom = secondGeom;
    this.firstOffsetPt = new GeomPoint(p3f_offset1);
    this.secondOffsetPt = new GeomPoint(p3f_offset2);
    
    //this.firstGeom.addGeomToLayer(this.firstOffsetPt, true, this.layerNum);
    //this.secondGeom.addGeomToLayer(this.secondOffsetPt, true, this.layerNum);
    this.firstGeom.addGeomToLayer(this.firstOffsetPt, true, firstGeom.layerNum );
    this.secondGeom.addGeomToLayer(this.secondOffsetPt, true, secondGeom.layerNum );
    this.activateEndPoints = true;
  }

  public GeomLine(Geom firstGeom, Point3f p3f_offset1,
    Geom secondGeom, Point3f p3f_offset2,
    boolean activateEndPoints)
  {
    this.firstGeom = firstGeom;
    this.secondGeom = secondGeom;
    this.firstOffsetPt = new GeomPoint(p3f_offset1);
    this.secondOffsetPt = new GeomPoint(p3f_offset2);
    firstOffsetPt.setColor(0f,0f,0f,0f);
    secondOffsetPt.setColor(0f,0f,0f,0f);
    firstOffsetPt.isActive = true;
    secondOffsetPt.isActive = true;
    firstOffsetPt.pointSize = 0.0001f;
    secondOffsetPt.pointSize = 0.0001f;
    //this.firstGeom.addGeomToLayer(this.firstOffsetPt, true, this.layerNum);
    //this.secondGeom.addGeomToLayer(this.secondOffsetPt, true, this.layerNum);
    this.firstGeom.addGeomToLayer(this.firstOffsetPt, activateEndPoints, firstGeom.layerNum );
    this.secondGeom.addGeomToLayer(this.secondOffsetPt, activateEndPoints, secondGeom.layerNum );
  
    this.activateEndPoints = activateEndPoints;
  }
  public GeomLine(Geom firstGeom, Geom secondGeom)
  {
    this.firstGeom = firstGeom;
    this.secondGeom = secondGeom;
    this.firstOffsetPt = new GeomPoint(0f, 0f, 0f);
    this.secondOffsetPt = new GeomPoint(0f, 0f, 0f);
  
 //   this.firstGeom.addGeom(this.firstOffsetPt, true);
 //   this.secondGeom.addGeom(this.secondOffsetPt, true);
    this.firstGeom.addGeomToLayer(this.firstOffsetPt, true, firstGeom.layerNum );
    this.secondGeom.addGeomToLayer(this.secondOffsetPt, true, secondGeom.layerNum );
    this.activateEndPoints = true;
  }

  public GeomLine(Geom firstGeom, Geom secondGeom, boolean activateEndPoints)
  {
    this.firstGeom = firstGeom;
    this.secondGeom = secondGeom;
    this.firstOffsetPt = new GeomPoint(0f, 0f, 0f);
    this.secondOffsetPt = new GeomPoint(0f, 0f, 0f);
  
    this.firstGeom.addGeomToLayer(this.firstOffsetPt, activateEndPoints, firstGeom.layerNum );
    this.secondGeom.addGeomToLayer(this.secondOffsetPt, activateEndPoints, secondGeom.layerNum );
    //this.firstGeom.addGeom(this.firstOffsetPt, activateEndPoints);
    //this.secondGeom.addGeom(this.secondOffsetPt, activateEndPoints);
    this.activateEndPoints = activateEndPoints;
  }
  
  @Override
  public void draw(GL gl)
  {
    Point3d h1 = MatrixUtils.getGeomPointInGeomCoordinates(
      new Point3d(), firstOffsetPt.modelview, this.modelview);
    Point3d h2 = MatrixUtils.getGeomPointInGeomCoordinates(
      new Point3d(), secondOffsetPt.modelview, this.modelview);

    gl.glLineWidth(this.lineWidth);

    gl.glColor4fv(color.array(), 0);

    
    gl.glBegin(gl.GL_LINES);
    {
      gl.glVertex3d(h1.x, h1.y, h1.z);
      gl.glVertex3d(h2.x, h2.y, h2.z);
    }
    gl.glEnd();

  }
  
}


