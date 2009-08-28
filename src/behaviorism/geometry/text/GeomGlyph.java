/* GeomGlyph.java ~ Sep 14, 2008 */

package behaviorism. geometry.text;

import behaviorism.geometry.Geom;
import behaviorism.utils.RenderUtils;
import com.sun.opengl.util.awt.TextRenderer;
import javax.media.opengl.GL2;
import javax.vecmath.Point3f;

/**
 * NEED to merge CharPosition/CharState info into this class. This is so that we can apply behaviors to GeomGlyphs.
 * Because calling the "begin3DRendereing" method is quite slow, we don't actually draw this class directly,
 * but rather draw it via a parent class (as is currently being tested in GeomTextFlowForcedFontSize).
 * 
 * Still working out details... Should have an option to draw itself directly or to be called via a parent container.
 * 
 * @author angus
 */
public class GeomGlyph extends Geom
{
  public String glyph = null;
  public TextRenderer textRenderer = null;
  public float scaleVal = 0f;
  
  //create an unitintialized GeomGlyph
  public GeomGlyph(String glyph)
  {
    this.glyph = glyph;
  }
  
  //create an initialized GeomGlyph
  public GeomGlyph(String glyph, Point3f p3f, TextRenderer textRenderer, float scaleVal)
  {
    super(p3f);
    this.glyph = glyph;
    this.textRenderer = textRenderer;
    this.scaleVal = scaleVal;
  }

  public void draw()
  {
    GL2 gl = RenderUtils.getGL();
    /*
    if (textRenderer == null) return;
    
    textRenderer.begin3DRendering();
        
    textRenderer.setColor(r, g, b, a);
    //textRenderer.draw3D(glyph, anchor.x, anchor.y, anchor.z, this.scaleVal);
    textRenderer.draw3D(glyph, 0f,0f,0.01f, this.scaleVal);
  
    textRenderer.end3DRendering();
    */
  }
}
