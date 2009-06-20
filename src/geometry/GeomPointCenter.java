package geometry;

import behaviorism.BehaviorismDriver;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import renderers.Renderer;
import utils.RenderUtils;
//TODO - GeomPointCenter should not exist!

@Deprecated
public class GeomPointCenter extends GeomPoint
{
	public GeomPointCenter()
	{
	}
	
	public void draw(GL gl)
  {
		//Point3f c1n = RenderUtils.getWorldCoordsForScreenCoord(Renderer.canvasWidth / 2,
		//				Renderer.canvasHeight / 2, .99, RenderUtils.getCamera().modelview);
		Point3f c1n = RenderUtils.getWorldCoordsForScreenCoord(BehaviorismDriver.canvasWidth / 2,
						BehaviorismDriver.canvasHeight / 2, .99, RenderUtils.getCamera().modelview);

    gl.glColor4f(r, g, b, a);
    gl.glPointSize(this.pointSize);
    
    gl.glBegin(gl.GL_POINTS);
    //gl.glVertex3f(translate.x, translate.y , translate.z);  //draws the point, it should be the point plus translate
    gl.glVertex3f(c1n.x, c1n.y, c1n.z);  //draws the point, it should be the point plus translate
    gl.glEnd();
  }
}

