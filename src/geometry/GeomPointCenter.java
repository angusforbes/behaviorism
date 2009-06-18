package geometry;

import behaviorism.BehaviorismDriver;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import renderers.RendererJogl;
//TODO - GeomPointCenter should not exist!

@Deprecated
public class GeomPointCenter extends GeomPoint
{
	public GeomPointCenter()
	{
	}
	
	public void draw(GL gl)
  {
		//Point3f c1n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(RendererJogl.canvasWidth / 2,
		//				RendererJogl.canvasHeight / 2, .99, RendererJogl.modelviewMatrix);
		Point3f c1n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(BehaviorismDriver.canvasWidth / 2,
						BehaviorismDriver.canvasHeight / 2, .99, RendererJogl.modelviewMatrix);

    gl.glColor4f(r, g, b, a);
    gl.glPointSize(this.pointSize);
    
    gl.glBegin(gl.GL_POINTS);
    //gl.glVertex3f(anchor.x, anchor.y , anchor.z);  //draws the point, it should be the point plus anchor
    gl.glVertex3f(c1n.x, c1n.y, c1n.z);  //draws the point, it should be the point plus anchor
    gl.glEnd();
  }
}

