/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geometry.media;

import com.sun.opengl.util.texture.Texture;
import geometry.*;
import com.sun.opengl.util.texture.TextureCoords;
import javax.media.opengl.GL;
import javax.vecmath.Point3f;
import textures.TextureImage;

/**
 * GeomImage applies a single texture to GeomRect. Versions that extend from this
 * allow for multitexturing, etc.
 * @author angus
 */
public class GeomImage extends GeomRect
{
  //float maxSize = 1f;
  //TextureData textureData =null;

  /*
  @Deprecated
  public GeomImage(TextureImage ti, Point3f p3f, float w, float h)
  {
    super(p3f, w, h);
    attachTexture(ti);
  }
  */

  //without texture
  public GeomImage(Point3f p3f, float w, float h)
  {
    super(p3f, w, h);
  }
  public GeomImage(Point3f p3f, float w, float h, TextureImage ti)
  {
    super(p3f, w, h);
    setTexture(ti);
  }

  //set the w/h using dimensions of texture
  public GeomImage(Point3f p3f, float maxSize, TextureImage ti)
  {
    super(p3f, 1f, 1f);
    setTexture(ti);

    normalizeSize(ti.w, ti.h, maxSize);
  }


  //assuming there is only one texture attached...
  public void setTexture(TextureImage ti2)
  {
    if (this.textures == null || this.textures.size() == 0)
    {
      attachTexture(ti2);
    }
    else
    {
      TextureImage ti1 = this.textures.get(0);
      attachTexture(ti2);
      detachTexture(ti1);
    }
  }

  public TextureImage getTexture()
  {
    if (this.textures == null)
    {
      return null;
    }
    return this.textures.get(0);
  }
  
  @Override
  public void draw(GL gl)
  {
    if (!updateTextures())
    {
      //textures are not ready... maybe draw withtout texture?
      return;
    }

    /*
    boolean depthTest = RenderUtils.getBoolean(gl, GL.GL_DEPTH_TEST);

    if (depthTest == false && isSelectable == true)
    {
      gl.glColor4f(0f, 0f, 0f, 0f);

      gl.glEnable(GL.GL_DEPTH_TEST);

      drawRect(gl, 0f, 0f, offset, w, h);
     
      gl.glDisable(GL.GL_DEPTH_TEST);
    }
    */
    gl.glColor4fv(color.array(), 0);

    TextureImage texImage = getTexture();
    if (texImage == null)
    {
      return;
    }

    Texture tex = texImage.texture;

    tex.bind() ;
    gl.glEnable(GL.GL_TEXTURE_2D);
    TextureCoords tc = this.textures.get(0).texture.getImageTexCoords();

    drawRect(gl,
      0f, 0f, offset, w, h,
      tc.left(), tc.right(), tc.bottom(), tc.top()
      );

    gl.glDisable(GL.GL_TEXTURE_2D);
  }
}
