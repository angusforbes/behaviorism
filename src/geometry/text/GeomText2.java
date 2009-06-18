/*
 * GeomText2.java
 * Created on July 16, 2007, 11:25 AM
 */
package geometry.text;

import geometry.*;
import renderers.RendererJogl;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import behaviorism.BehaviorismDriver;
import handlers.FontHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import utils.MatrixUtils;

public class GeomText2 extends GeomRect
{
  public String useFont = null;
  public List<TextRenderer> textRenderers = FontHandler.getInstance().textRenderers;

	public String text = "";
	public GeomPoint centerPoint = new GeomPoint();
	protected Rectangle2D stringBounds = null;
	protected TextRenderer textRenderer = null;
	protected Font font = null;
	protected FontRenderContext frc = null;
	protected float scaleVal = 1f;
	protected int prevPxWidth = 0;
	protected int prevPxHeight = 0;
	protected int pxX = 0;
	protected int pxY = 0;
	protected int pxWidth = 0;
	protected int pxHeight = 0;
	protected float prevW = 0f;
	protected float prevH = 0f;
	protected Rectangle2D stringBoundsX = null;
	protected Rectangle2D stringBoundsY = null;
	protected TextRenderer textRendererX = null;
	protected TextRenderer textRendererY = null;
	protected Rectangle2D.Float boundsInsets = new Rectangle2D.Float();
	protected Rectangle2D.Float boundsTextBackground = new Rectangle2D.Float();
	protected float xpos = 0f;
	protected float ypos = 0f;
	protected float scaleValX = 1f;
	protected float scaleValY = 1f;
	public int justifyX = 0; //0 center, -1 left, +1 right
	public int justifyY = 0; //0 center, -1 bottom, +1 top
	public float insetX = 0f;
	public float insetY = 0f;
	public Colorf insetColor = null; //background of insets
	public Colorf backgroundColor = null; //background of entire bounds
	public Colorf textBackgroundColor = null; //background of exact text bounds
	public TextRenderer nonDynamicTextRenderer = null;
	public boolean isFirstTime = true;

	public boolean exactPixelBounds = false;

  public void setFont(String fontString)
  {
       //textRenderers = FontHandler.getInstance().fontFamilyMap.get("Universe55,0");
       this.textRenderers = FontHandler.getInstance().fontFamilyMap.get(fontString);
        FontHandler.getInstance().fontsReady.set(true);
  }

	/**
	 * Static factory to create a GeomText2 of a specified height and an arbitrary width (based on the height).
	 * This is probably the most useful way to use a GeomText-- the height is specified and the width is 
	 * as long as need be. This is similar to setting a font size, which also constrains the height of the text. 
	 * @param text
	 * @param x
	 * @param y
	 * @param z
	 * @param h
	 * @return a new GeomText2 constrained by height
	 */
	public static GeomText2 newGeomTextConstrainedByHeight(String text,
																												 Point3f p3f,
																												 float h)
	{
		GeomText2 gt2 = new GeomText2(p3f, h, 0f, text);
    gt2.exactPixelBounds = true;
    //gt2.insetX = 0f;
    //gt2.insetY = 0f;
		gt2.setWidthByHeight(h);
		
    //System.out.println("insetX/Y = " + gt2.insetX + "/" + gt2.insetY);
    return gt2;
	}

	public static GeomText2 newGeomTextConstrainedByHeight(String text,
																												 Point3f p3f,
																												 float h,
                                                         boolean exactPixelBounds)
	{
		GeomText2 gt2 = new GeomText2(p3f, h, 0f, text);
    //gt2.insetX = .1f;
    //gt2.insetY = .1f;
    
    gt2.exactPixelBounds = exactPixelBounds;
		gt2.setWidthByHeight(h);
		return gt2;
	}

	
	/**
	 * Static factory to create a GeomText2 of a specified width and an arbitrary height (based on the width).
	 * @param text
	 * @param x
	 * @param y
	 * @param z
	 * @param h
	 * @return a new GeomText2 constrained by width
	 */
	public static GeomText2 newGeomTextConstrainedByWidth(String text,
																												 Point3f p3f,
																												 float w)
	{
		GeomText2 gt2 = new GeomText2(p3f, 0f, w, text);
		gt2.setHeightByWidth(w);
		return gt2;
	}
	
	public GeomText2()
	{
	}

	public GeomText2(Point3f p3f, float w, float h, String text)
	{
		super(p3f, w, h);
		this.text = text;
	}

	public GeomText2(float x, float y, float z, float w, float h, String text)
	{
		super(x, y, z, w, h);
		this.text = text;
	}

	public GeomText2(Point3f p3f, float w, float h, String text, TextRenderer nonDynamicTextRenderer)
	{
		super(p3f, w, h);
		this.text = text;
		this.nonDynamicTextRenderer = nonDynamicTextRenderer;
		setUpNonDynamicTextRenderer();
	}

	public GeomText2(float x, float y, float z, float w, float h, String text, TextRenderer nonDynamicTextRenderer)
	{
		super(x, y, z, w, h);
		this.text = text;

		this.nonDynamicTextRenderer = nonDynamicTextRenderer;
		setUpNonDynamicTextRenderer();
	}

	public void printFrustum()
	{
		Point3f c1n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, 0, 0, RendererJogl.modelviewMatrix);
		Point3f c1f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, 0, 1, RendererJogl.modelviewMatrix);
		//Point3f c2n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(RendererJogl.canvasWidth, 0, 0, RendererJogl.modelviewMatrix);
		//Point3f c2f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(RendererJogl.canvasWidth, 0, 1, RendererJogl.modelviewMatrix);
		//Point3f c3n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(RendererJogl.canvasWidth, RendererJogl.canvasHeight, 1, RendererJogl.modelviewMatrix);
		//Point3f c3f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(RendererJogl.canvasWidth, RendererJogl.canvasHeight, 100, RendererJogl.modelviewMatrix);
		Point3f c2n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(BehaviorismDriver.canvasWidth, 0, 0, RendererJogl.modelviewMatrix);
		Point3f c2f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(BehaviorismDriver.canvasWidth, 0, 1, RendererJogl.modelviewMatrix);
		Point3f c3n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight, 1, RendererJogl.modelviewMatrix);
		Point3f c3f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(BehaviorismDriver.canvasWidth, BehaviorismDriver.canvasHeight, 100, RendererJogl.modelviewMatrix);
		//Point3f c4n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, RendererJogl.canvasHeight, 1, RendererJogl.modelviewMatrix);
		//Point3f c4f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, RendererJogl.canvasHeight, 100, RendererJogl.modelviewMatrix);
		Point3f c4n = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, BehaviorismDriver.canvasHeight, 1, RendererJogl.modelviewMatrix);
		Point3f c4f = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, BehaviorismDriver.canvasHeight, 100, RendererJogl.modelviewMatrix);

		System.out.println("screen in world c1n: " + c1n);
		System.out.println("screen in world c2n: " + c2n);
		System.out.println("screen in world c1f: " + c1f);
		System.out.println("screen in world c2f: " + c2f);
	}

	public void setUpNonDynamicTextRenderer()
	{
		System.out.println("here!");
		this.pxWidth = (int) (BehaviorismDriver.renderer.getWidthOfObjectInPixels(this, this.insetX));
		this.pxHeight = (int) (BehaviorismDriver.renderer.getHeightOfObjectInPixels(this, this.insetY));

		textRenderer = nonDynamicTextRenderer;
		FontRenderContext frc1 = textRenderer.getFontRenderContext();
		Font font1 = textRenderer.getFont();
		GlyphVector gv1 = font1.createGlyphVector(frc1, this.text);
		Rectangle2D bounds1 = gv1.getPixelBounds(null, 0f, 0f);
		float strw1 = (float) (bounds1.getWidth());
		float strh1 = (float) (bounds1.getHeight());

		scaleValX = (this.w * (1f - insetX)) / (float) bounds1.getWidth();
		scaleValY = (this.h * (1f - insetY)) / (float) bounds1.getHeight();
		
		this.scaleVal = scaleValY;
		if (scaleValX < scaleValY)
		{
			this.scaleVal = scaleValX;
		}

		this.stringBounds = bounds1;



		//System.out.printf("pxWidth/pxHeight = %d/%d\n", this.pxWidth, this.pxHeight);

		//System.out.println("stringBounds = " + stringBounds);
		//System.out.println("was... w/h = " + w + "/" + h);

		this.w = (float) ((this.w * (float) stringBounds.getWidth()) / (float) this.pxWidth);
		this.h = (float) ((this.h * (float) stringBounds.getHeight()) / (float) this.pxHeight);
		//this.w = this.w * scaleValX;
		//this.h = this.h * scaleValY;
		justifyText();

		//System.out.println("now... w/h = " + w + "/" + h);
	}
	//ANGUS-- need to handle quotes properly
	//they are children of the main GT2 and their
	//rotate point is actually the dist to the center of their parent.
	//show some rect to see what is going on...
	/** This method calucaltes the pixel width and height without taking into
	 * consideration any rotations. Looks good, except the scale part might be a bit funny.
	 * Should investigate later!
	 *
	 * @param gl
	 */
	protected void calculateUnrotatedPixelWidthAndHeight(GL gl)
	{
		gl.glPushMatrix();

		//System.out.println("rotateanchor = " + rotateAnchor);
		Point3d newp;
		float world_z;

		if (parent == null)
		{
			//newp = new Point3d(rotateAnchor.anchor.x, rotateAnchor.anchor.y, 0f);
			newp = new Point3d(w * .5f, h * .5f, 0f);
			world_z = (float) (MatrixUtils.getGeomPointInWorldCoordinates(newp, modelview, RendererJogl.modelviewMatrix)).z;

		}
		else
		{
			newp = new Point3d(parent.w * .5f, parent.h * .5f, 0f);
			//newp = new Point3d();
			world_z = (float) (MatrixUtils.getGeomPointInWorldCoordinates(newp, parent.modelview, RendererJogl.modelviewMatrix)).z;
		//world_z = (float) (MatrixUtils.getGeomPointInGeomCoordinates(newp, modelview, parent.modelview)).z;
		//System.out.println("world_z for child = " + world_z);
		}

		BehaviorismDriver.renderer.resetPerspective3D();

		translate(gl, -w / 2f, -h / 2f, (float) (world_z));

		//Scaling might be a bit wonky!!!!!!! it is for sure...
		if (parent != null)
		{
			parent.scale(gl);
		}
		scale(gl);

		double[] temp_mv = MatrixUtils.getIdentity();
		double[] temp_pj = MatrixUtils.getIdentity();
		int[] temp_vp = new int[4];

		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, temp_mv, 0);
		gl.glGetDoublev(gl.GL_PROJECTION_MATRIX, temp_pj, 0);
		gl.glGetIntegerv(gl.GL_VIEWPORT, temp_vp, 0);
	
    this.pxWidth = (int) (BehaviorismDriver.renderer.getWidthOfObjectInPixels(this, this.insetX, temp_mv, temp_pj, temp_vp));
		this.pxHeight = (int) (BehaviorismDriver.renderer.getHeightOfObjectInPixels(this, this.insetY, temp_mv, temp_pj, temp_vp));
		
//    int pxWidth2 = (int) (BehaviorismDriver.renderer.getWidthOfObjectInPixels(this, 0f, temp_mv, temp_pj, temp_vp));
//		int pxHeight2 = (int) (BehaviorismDriver.renderer.getHeightOfObjectInPixels(this, 0f, temp_mv, temp_pj, temp_vp));
//
//    System.out.println("inset x " + insetX + ", inset diff X = " + (pxWidth - pxWidth2));
//    System.out.println("inset y " + insetY + ", inset diff Y = " + (pxHeight - pxHeight2));
//    
		//this.pxX = (int) (BehaviorismDriver.renderer.getXOfObjectInPixels(this, temp_mv, temp_pj, temp_vp));
		//this.pxY = (int) (BehaviorismDriver.renderer.getYOfObjectInPixels(this, temp_mv, temp_pj, temp_vp));
    
		/*
		
		//if (MouseHandler.selectedGeom == this)
		{
		//this should stay the same during x/y translations and rotations
		System.out.println("pxWidth / pxHeight = " + this.pxWidth + "/" + this.pxHeight);
		
		//to debug -- if all is well this should be in the center of the
		//  screen and should NOT move at all during x/y translations rotations
		gl.glColor4f(1f,1f,1f,.5f);
		gl.glBegin(gl.GL_POLYGON);
		gl.glVertex3f(0f, 0f, 0f);
		gl.glVertex3f(this.w, 0f, 0f);
		gl.glVertex3f(this.w, this.h, 0f);
		gl.glVertex3f(0f, this.h, 0f);
		gl.glEnd();
		}
		 */


		gl.glPopMatrix();
	}

	@Override public void draw(GL gl)
	{

  
		/*
		if (KeyboardHandler.keys[KeyEvent.VK_F11] == true)
		{
		Point3d nwn = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, 0, 0);
		Point3d nwf = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, 0, 1);
		Point3d swn = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, BehaviorismDriver.canvasHeight, 0);
		Point3d swf = BehaviorismDriver.renderer.getWorldCoordsForScreenCoord(0, BehaviorismDriver.canvasHeight, 1);
		
		System.out.println("");
		System.out.println("nwn = " + MatrixUtils.toString(nwn));
		System.out.println("nwf = " + MatrixUtils.toString(nwf));
		//System.out.println("swn = " + swn);
		//System.out.println("swf = " + swf);
		}
		 */


		calculateUnrotatedPixelWidthAndHeight(gl);


		if (nonDynamicTextRenderer != null)
		{
			if (isFirstTime == true)
			{

				//Rectangle2D.Float r2f = new Rectangle2D.Float();
				//r2f.setRect(this.stringBounds);
				//setCoordFromRectangle(r2f);


				//setCoordFromRectangle(new Rectangle2D.Float(this.stringBounds.getX(), this.stringBounds.getY(),
				//				this.stringBounds.getWidth(), this.stringBounds.getHeight()));
				isFirstTime = false;
			}
		}
		//otherwise we are determining the font size dynamically
		else
		{
			if (FontHandler.getInstance().fontsReady.get() == true || this.pxWidth != this.prevPxWidth || this.pxHeight != this.prevPxHeight ||
							this.textRenderer == null)
			{

				//System.out.println("have to readjust " + this.text + " !!! " + System.nanoTime());


				//System.out.printf("adjusting... %d/%d\n", this.pxWidth, this.pxHeight);

				//System.out.printf("prev = %d/%d, cur = %d/%d\n", this.prevPxWidth, this.prevPxHeight, this.pxWidth, this.pxHeight);
				this.prevPxWidth = this.pxWidth;
				this.prevPxHeight = this.pxHeight;

				chooseFont();
				justifyText();


			//System.out.printf("pxWidth/pxHeight = %d/%d\n", pxWidth, pxHeight);
			//System.out.println("using font in textRenderer : " + textRenderer.getFont());
			}		//gl.glDisable(GL.GL_BLEND);
		//gl.glDisable(GL.GL_DEPTH_TEST);
		//render invisibly for mouse picking ... hmmm may want this off for composite geoms
		}

    if (this.scaleVal <= 0f)
    {
     // return; //too small to draw-- actually it should never be less than 0f-- investigate
    }

		renderInvisiblePickingBackground(gl, offset);



		//dont' really want this here anymore, but it is good for testing.
		//real way shoudl be to make a composite object.. think about...
		if (this.backgroundColor != null)
		{
			//float bgc_z = 0f;
			//float bgc_z = -offset; //hmm
			float bgc_z = offset;
			gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);

			gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f(0f, 0f, bgc_z);
			gl.glVertex3f(this.w, 0f, bgc_z);
			gl.glVertex3f(this.w, this.h, bgc_z);
			gl.glVertex3f(0f, this.h, bgc_z);
			gl.glEnd();
		}
		//gl.glEnable(GL.GL_BLEND);
		//gl.glEnable(GL.GL_DEPTH_TEST);



		if (this.insetColor != null)
		{
			gl.glColor4f(insetColor.r, insetColor.g, insetColor.b, insetColor.a);

			gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f(boundsInsets.x, boundsInsets.y, 0f);
			gl.glVertex3f(boundsInsets.x + boundsInsets.width, boundsInsets.y, 0f);
			gl.glVertex3f(boundsInsets.x + boundsInsets.width, boundsInsets.y + boundsInsets.height, 0f);
			gl.glVertex3f(boundsInsets.x, boundsInsets.y + boundsInsets.height, 0f);
			gl.glEnd();
		}

		if (textBackgroundColor != null)
		{
			gl.glColor4f(textBackgroundColor.r, textBackgroundColor.g, textBackgroundColor.b, textBackgroundColor.a);

			gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f(boundsTextBackground.x, boundsTextBackground.y, 0f);
			gl.glVertex3f(boundsTextBackground.x + boundsTextBackground.width, boundsTextBackground.y, 0f);
			gl.glVertex3f(boundsTextBackground.x + boundsTextBackground.width, boundsTextBackground.y + boundsTextBackground.height, 0f);
			gl.glVertex3f(boundsTextBackground.x, boundsTextBackground.y + boundsTextBackground.height, 0f);
			gl.glEnd();
		}


		// gl.glDisable(GL.GL_BLEND);
		// gl.glDisable(GL.GL_DEPTH_TEST);
		//gl.glEnable(GL.GL_BLEND);
		//gl.glDisable(GL.GL_DEPTH_TEST);


		//render to display
		//System.out.println("font = " + textRenderer.getFont());

    //textRenderer.setUseVertexArrays(true);
    //textRenderer.setUseVertexArrays(false);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
  
		textRenderer.begin3DRendering();
		textRenderer.setColor(this.r, this.g, this.b, this.a);
		textRenderer.draw3D(this.text, this.xpos, this.ypos, offset, this.scaleVal);
    if (this.scaleVal < 0f)
    {
      //System.out.println("GT2 " + this.text + " scaleVal = " + this.scaleVal);
    }
		textRenderer.end3DRendering();

    textRenderer.flush();
   
    //temp!!!

    //debugPackingAlgorithm(gl);
    
    //end temp
	

	/*
	if (drawDebugCorners)
	{
	//gl.glPointSize(5f);
	
	//System.out.println("g.corners.size = " + g.corners.size());
	for (int ccc = 0; ccc < this.corners.size(); ccc++)
	{
	Corner corner = this.corners.get(ccc);
	//System.out.println("corner has " + corner.possibleRectangles.size()+ "  possRects");
	gl.glColor4f( 1f, 1f, 1f, 1f);
	
	gl.glBegin(GL.GL_POINTS);
	gl.glVertex3f(corner.mark.x,  corner.mark.y, this.z);
	gl.glEnd();
	
	for (int ddd = 0; ddd < corner.possibleRectangles.size(); ddd++)
	{
	Rectangle2D.Float r2f = corner.possibleRectangles.get(ddd);
	
	gl.glColor4f( 0f, 0f, 1f, .1f);
	
	gl.glBegin(GL.GL_POLYGON);
	gl.glVertex3f(r2f.x, r2f.y, this.z);
	gl.glVertex3f(r2f.x + r2f.width, r2f.y, this.z);
	gl.glVertex3f(r2f.x + r2f.width, r2f.y + r2f.height, this.z);
	gl.glVertex3f(r2f.x, r2f.y + r2f.height, this.z);
	gl.glEnd();
	
	
	}
	}
	}
	 */
	}

	/** render invisibly for mouse picking */
	public void renderInvisiblePickingBackground(GL gl, float offset)
	{
		//boolean depthTest = RendererJogl.getBoolean(gl, GL.GL_DEPTH_TEST);
		//if (depthTest == false && isSelectable == true)
		if (isSelectable == true)
		{
			gl.glEnable(GL.GL_DEPTH_TEST);

			float bgc_z = -offset;
			gl.glColor4f(0f, 0f, 0f, 0f);

			gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f(0f, 0f, bgc_z);
			gl.glVertex3f(this.w, 0f, bgc_z);
			gl.glVertex3f(this.w, this.h, bgc_z);
			gl.glVertex3f(0f, this.h, bgc_z);
			gl.glEnd();

			gl.glDisable(GL.GL_DEPTH_TEST);
		}
	}

	
	public void chooseFont()
	{
		//get correct font size for the current pixel w/h of the Geom
				//for (int i = 1; i < FontHandler.getInstance().textRenderers.size(); i++)
				for (int i = 1; i < textRenderers.size(); i++)
				{
					boolean readyToBreakX = false;
					boolean readyToBreakY = false;
					scaleValX = 1f;
					scaleValY = 1f;

					//TextRenderer tr1 = (FontHandler.getInstance().textRenderers.get(i - 1));
					TextRenderer tr1 = textRenderers.get(i - 1);
					FontRenderContext frc1 = tr1.getFontRenderContext();
					Font font1 = tr1.getFont();
					
					Rectangle2D bounds1;

					if (exactPixelBounds == true)
					{
						GlyphVector gv1 = font1.createGlyphVector(frc1, this.text);
						bounds1 = gv1.getPixelBounds(null, 0f, 0f);
					}
					else
					{
						bounds1 = font1.getStringBounds(this.text, frc1);
					}
					float strw1 = (float) (bounds1.getWidth());
					float strh1 = (float) (bounds1.getHeight());

					//TextRenderer tr2 = (FontHandler.getInstance().textRenderers.get(i));
					TextRenderer tr2 = textRenderers.get(i);
					FontRenderContext frc2 = tr2.getFontRenderContext();
					Font font2 = tr2.getFont();
					
					Rectangle2D bounds2;
					if (exactPixelBounds == true)
					{
						GlyphVector gv2 = font2.createGlyphVector(frc2, this.text);
						bounds2 = gv2.getPixelBounds(null, 0f, 0f);
					}
					else
					{
						bounds2 = font2.getStringBounds(this.text, frc1);
					}
					//GlyphVector gv2 = font2.createGlyphVector(frc2, this.text);
					//Rectangle2D bounds2 = gv2.getPixelBounds(null, 0f, 0f);
					float strw2 = (float) (bounds2.getWidth());
					float strh2 = (float) (bounds2.getHeight());

					//System.out.println("trying " + tr1.getFont());
					//System.out.printf("strw avg / strh avg = %f / %f \n", ((strw1 + strw2) / 2f), ((strh1 + strh2) / 2f));
					//if (strw2 > pxWidth)
					if ((strw1 + strw2) / 2f > pxWidth)
					{
						textRendererX = tr1;
						stringBoundsX = bounds1;
						scaleValX = (this.w * (1f - insetX)) / (float) (bounds1.getWidth());
						//scaleValX = ( (this.w - .1f) * (1f )) / (float) (bounds1.getWidth());
						
            //System.out.printf("this.w = %f insetX = %f b.w = %f\n", this.w, insetX, bounds1.getWidth());
						//System.out.printf("(%f * (1f - %f) / %f  :  %f / %f\n", this.w, insetX, bounds1.getWidth(), (this.w * (1f - insetX)) , bounds1.getWidth());
						//System.out.println("scaleVal = " + scaleValX + " WIDTH");
						readyToBreakX = true;
					}
					//if (strh2 > pxHeight)
					if ((strh1 + strh2) / 2f > pxHeight)
					{
						textRendererY = tr1;
						stringBoundsY = bounds1;
						//scaleValY = (this.h * (1f - insetY)) / (float) bounds1.getHeight();
						scaleValY = ((this.h - .1f) * (1f )) / (float) bounds1.getHeight();
						//System.out.println("scaleVal = " + scaleValY + " HEIGHT");
						readyToBreakY = true;
					}

					if (readyToBreakX == true && readyToBreakY == false)
					{
						//System.out.println("X true");
						this.scaleVal = scaleValX;
						this.textRenderer = textRendererX;
						this.stringBounds = stringBoundsX;
						break;
					}
					else
					{
						if (readyToBreakX == false && readyToBreakY == true)
						{
							//System.out.println("Y true");
							this.scaleVal = scaleValY;
							this.textRenderer = textRendererY;
							this.stringBounds = stringBoundsY;
							break;
						}
						else
						{
							if (readyToBreakX == true && readyToBreakY == true)
							{
								//System.out.println("XY true");
								if (scaleValX < scaleValY)
								{
									this.scaleVal = scaleValX;
									this.textRenderer = textRendererX;
									this.stringBounds = stringBoundsX;
								//System.out.println("using X");
								}
								else
								{
									this.scaleVal = scaleValY;
									this.textRenderer = textRendererY;
									this.stringBounds = stringBoundsY;
								//System.out.println("using Y");
								}
								break;
							}
						}
					}

					//if (i == FontHandler.getInstance().textRenderers.size() - 1) //use biggest one
					if (i == textRenderers.size() - 1) //use biggest one
					{
						//System.out.println("using biggest available");
						//this.textRenderer = tr2;
						//this.stringBounds = bounds2;
						//this.scaleVal = (this.w) / (float) bounds2.getHeight();

						this.textRenderer = tr2;
						//this.stringBoundsX = bounds2;
						this.stringBounds = bounds2;
						
            this.scaleVal = (this.w * (1f - insetX)) / (float) (bounds2.getWidth());
            //this.scaleVal = (this.w - .1f) / (float) (bounds2.getWidth());

						//TEMP!!!
						return;

					//font = font2;
					//frc = frc2;
					}
				}

	}
	/*
	public Point3f draw(GL gl, GLU glu, Point3f parent)
	{
	float hx = anchor.x + parent.x;
	float hy = anchor.y + parent.y;
	float hz = anchor.z + parent.z;
	Point3f hp = new Point3f(hx, hy, hz);
	
	setColor(r, g, b, a);
	
	int pxWidth = (int) (getWidthOfObjectInPixels2(gl, glu, this));
	int pxHeight = (int) (getHeightOfObjectInPixels(gl, glu, this));
	
	Rectangle2D stringBounds = null;
	TextRenderer textRenderer = null;
	Font font = null;
	FontRenderContext frc = null;
	float scaleVal = 1f;
	
	for (int i = 1; i < BehaviorismDriver.fonts.size(); i++)
	{
	boolean readyToBreakX = false;
	boolean readyToBreakY = false;
	float scaleValX = 1f;
	float scaleValY = 1f;
	
	
	
	Rectangle2D stringBoundsX = null;
	Rectangle2D stringBoundsY = null;
	TextRenderer textRendererX = null;
	TextRenderer textRendererY = null;
	
	TextRenderer tr1 = (BehaviorismDriver.fonts.get(i - 1)).textRenderer;
	FontRenderContext frc1 = tr1.getFontRenderContext();
	Font font1 = tr1.getFont();
	GlyphVector gv1 = font1.createGlyphVector(frc1, this.text);
	Rectangle2D bounds1 = gv1.getPixelBounds(null, 0f, 0f);
	float strw1 = (float) (bounds1.getWidth());
	float strh1 = (float) (bounds1.getHeight());
	
	TextRenderer tr2 = (BehaviorismDriver.fonts.get(i)).textRenderer;
	FontRenderContext frc2 = tr2.getFontRenderContext();
	Font font2 = tr2.getFont();
	GlyphVector gv2 = font2.createGlyphVector(frc2, this.text);
	Rectangle2D bounds2 = gv1.getPixelBounds(null, 0f, 0f);
	float strw2 = (float) (bounds2.getWidth());
	float strh2 = (float) (bounds2.getHeight());
	
	//if (strw2 > pxWidth)
	if ((strw1 + strw2) / 2f > pxWidth)
	{
	textRendererX = tr1;
	stringBoundsX = bounds1;
	scaleValX = (this.w * (1f - insetX)) / (float)(bounds1.getWidth());
	//System.out.println("scaleVal = " + scaleValX + " WIDTH");
	readyToBreakX = true;
	}
	//if (strh2 > pxHeight)
	if ((strh1 + strh2) / 2f > pxHeight)
	{
	textRendererY = tr1;
	stringBoundsY = bounds1;
	scaleValY = (this.h * (1f - insetY)) / (float)bounds1.getHeight();
	//System.out.println("scaleVal = " + scaleValY + " HEIGHT");
	readyToBreakY = true;
	}
	
	if (readyToBreakX == true && readyToBreakY == false)
	{
	//System.out.println("X true");
	scaleVal = scaleValX;
	textRenderer = textRendererX;
	stringBounds = stringBoundsX;
	break;
	}
	else if (readyToBreakX == false && readyToBreakY == true)
	{
	//System.out.println("Y true");
	scaleVal = scaleValY;
	textRenderer = textRendererY;
	stringBounds = stringBoundsY;
	break;
	}
	else if (readyToBreakX==true && readyToBreakY==true)
	{
	//System.out.println("XY true");
	if (scaleValX < scaleValY)
	{
	scaleVal = scaleValX;
	textRenderer = textRendererX;
	stringBounds = stringBoundsX;
	//System.out.println("using X");
	}
	else
	{
	scaleVal = scaleValY;
	textRenderer = textRendererY;
	stringBounds = stringBoundsY;
	//System.out.println("using Y");
	}
	break;
	}
	
	if (i == BehaviorismDriver.fonts.size() - 1) //use biggest one
	{
	textRenderer = tr2;
	stringBounds = bounds2;
	scaleVal = (this.h) / (float)bounds2.getHeight();
	//font = font2;
	//frc = frc2;
	}
	}
	
	float xpos, ypos;
	
	//get exact X position based on justification and exact glyphs
	if (this.justifyX == 1) //right justify
	{
	float strW = (float) stringBounds.getWidth() * scaleVal;
	//xpos = anchor.x + this.w - strW;
	xpos = hx + this.w - strW;
	xpos -= (this.w * (insetX / 2f));
	
	}
	else if (this.justifyX == 0) //center justify
	{
	float strW = (float) stringBounds.getWidth() * scaleVal;
	float centerIncX = (strW * .5f);
	//xpos = (anchor.x + (this.w * .5f) - (centerIncX));
	xpos = (hx + (this.w * .5f) - (centerIncX));
	}
	else //left justify
	{
	//xpos = this.anchor.x;
	xpos = hx;
	xpos += (this.w * (insetX / 2f));
	
	}
	
	float ttx = (float)stringBounds.getX() * scaleVal;
	float ttw = (float)stringBounds.getWidth() * scaleVal;
	xpos -= ttx;
	//xpos += xinset
	//get exact Y position based on justification and exact glyphs
	if (this.justifyY == 1) //top justify
	{
	float strH = (float) stringBounds.getHeight() * scaleVal;
	//ypos = this.anchor.y + this.h - strH;
	ypos = hy + this.h - strH;
	ypos -= (this.h * (insetY / 2f));
	}
	else if (this.justifyY == 0) //center height
	{
	float strH = (float) stringBounds.getHeight() * scaleVal;
	float centerIncY = (strH * .5f);
	//ypos = (this.anchor.y + (this.h * .5f) - (centerIncY) );
	ypos = (hy + (this.h * .5f) - (centerIncY) );
	}
	else
	{
	//ypos = this.anchor.y;
	ypos = hy;
	ypos += (this.h * (insetY / 2f));
	}
	
	float tty = (float)stringBounds.getY() * scaleVal;
	float tth = (float)stringBounds.getHeight() * scaleVal;
	float diffy = tth + tty;
	ypos += diffy;
	
	if (this.backgroundColor != null)
	{
	gl.glColor4f(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
	gl.glBegin(gl.GL_POLYGON);
	gl.glVertex3f(hp.x, hp.y, hp.z);
	gl.glVertex3f(hp.x + this.w, hp.y, hp.z);
	gl.glVertex3f(hp.x + this.w, hp.y + this.h, hp.z);
	gl.glVertex3f(hp.x, hp.y + this.h, hp.z);
	gl.glEnd();
	}
	if (this.insetColor != null)
	{
	gl.glColor4f(insetColor.r, insetColor.g, insetColor.b, insetColor.a);
	float p2w = (this.w * (1f - insetX));
	float p2x = hp.x + ( (this.w * insetX) / 2f );
	float p2h = (this.h * (1f - insetY));
	float p2y = hp.y + ( (this.h * insetY) / 2f );
	
	gl.glBegin(gl.GL_POLYGON);
	gl.glVertex3f(p2x, p2y, hz);
	gl.glVertex3f(p2x + p2w, p2y, hz);
	gl.glVertex3f(p2x + p2w, p2y + p2h, hz);
	gl.glVertex3f(p2x, p2y + p2h, hz);
	gl.glEnd();
	}
	
	if (textBackgroundColor != null)
	{
	gl.glColor4f(textBackgroundColor.r, textBackgroundColor.g, textBackgroundColor.b, textBackgroundColor.a);
	
	gl.glBegin(gl.GL_POLYGON);
	gl.glVertex3f(xpos + ttx, ypos - diffy , anchor.z);
	gl.glVertex3f(xpos + ttx + ttw, ypos - diffy, anchor.z);
	gl.glVertex3f(xpos + ttx + ttw, ypos + tth - diffy, anchor.z);
	gl.glVertex3f(xpos + ttx, ypos + tth - diffy, anchor.z);
	gl.glEnd();
	}
	
	
	//render to display
	textRenderer.begin3DRendering();
	//System.out.println("scaleVal = " + scaleVal + ", fontInfo.w " + fontInfo.w );
	textRenderer.setColor(this.r, this.g, this.b, this.a);
	//textRenderer.draw3D(this.text, xpos, ypos, anchor.z, scaleVal);
	textRenderer.draw3D(this.text, xpos, ypos, hp.z, scaleVal);
	textRenderer.end3DRendering();
	
	
	}
	
	
	return hp; //parent;
	}
	 */

	private void justifyText()
	{
		//get exact X position based on justification and exact glyphs
		if (this.justifyX == 1) //right justify
		{
			float strW = (float) stringBounds.getWidth() * scaleVal;
			//xpos = hx + this.w - strW;
			this.xpos = 0f + this.w - strW;
			
      //Perc vs Abs justify...
      this.xpos -= (this.w * (insetX / 2f)); //perc
			//this.xpos -= (insetX / 2f); //abs

		}
		else
		{
			if (this.justifyX == 0) //center justify
			{
				float strW = (float) stringBounds.getWidth() * scaleVal;
				float centerIncX = (strW * .5f);
				//xpos = (hx + (this.w * .5f) - (centerIncX));
				this.xpos = ((this.w * .5f) - (centerIncX));
			}
			else //left justify
			{
				//xpos = hx;
				this.xpos = 0f;
				this.xpos += (this.w * (insetX / 2f));

			}
		}

		float ttx = (float) stringBounds.getX() * scaleVal;
		float ttw = (float) stringBounds.getWidth() * scaleVal;
		this.xpos -= ttx;
		//xpos += xinset
		//get exact Y position based on justification and exact glyphs
		if (this.justifyY == 1) //top justify
		{
			float strH = (float) stringBounds.getHeight() * scaleVal;
			//ypos = this.anchor.y + this.h - strH;
			//ypos = hy + this.h - strH;
			this.ypos = this.h - strH;
			this.ypos -= (this.h * (insetY / 2f));
		}
		else
		{
			if (this.justifyY == 0) //center height
			{
				float strH = (float) stringBounds.getHeight() * scaleVal;
				float centerIncY = (strH * .5f);
				//ypos = (hy + (this.h * .5f) - (centerIncY) );
				this.ypos = (0f + (this.h * .5f) - (centerIncY));
			}
			else
			{
				//ypos = hy;
				this.ypos = 0f;
				this.ypos += (this.h * (insetY / 2f));
			}
		}

		float tty = (float) stringBounds.getY() * scaleVal;
		float tth = (float) stringBounds.getHeight() * scaleVal;
		float diffy = tth + tty;
		this.ypos += diffy;

		/*
		float p2w = (this.w * (1f - insetX));
		float p2x =  ( (this.w * insetX) / 2f );
		float p2h = (this.h * (1f - insetY));
		float p2y =  ( (this.h * insetY) / 2f );
		 */

		//this.boundsInsets.setRect((this.w * insetX) / 2f, (this.h * insetY) / 2f,
		//				this.w * (1f - insetX), this.h * (1f - insetY));
		this.boundsInsets.setRect(this.w - (insetX/2f), this.h - (insetY / 2f),
						this.w - insetX, this.h - insetY);
		this.boundsTextBackground.setRect(this.xpos + ttx, this.ypos - diffy,
						ttw, tth);

	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String toString()
	{
		return "GeomText2 [" + text + "] : x/y/z, w/h = " + anchor.x + "/" + anchor.y + "/" + anchor.z +
      ", " + w + "/" + h + ", justify = " + justifyY;
	}
  

	public void setWidthByHeight(float h)
	{
		this.h = h;

		TextRenderer fi = FontHandler.getInstance().getLargestTextRenderer();
		
		FontRenderContext frc = fi.getFontRenderContext();
		Font font = fi.getFont();

		Rectangle2D bounds;
		if (exactPixelBounds == true)
		{
			GlyphVector gv1 = font.createGlyphVector(frc, this.text);
			bounds = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
		}
		else
		{
			bounds = font.getStringBounds(this.text, frc);
		}
		this.w = ((float) ((bounds.getWidth()) * this.h) / (float) bounds.getHeight());
	}

	public void setHeightByWidth(float w)
	{
		//this shouldn't happen-- textRenderers is somehow not being synchronized properly
		//if (FontHandler.textRenderers.size() == 0)
		{
			//return;
		}
	
		this.w = w;
		//TextRenderer fi = FontHandler.getInstance().textRenderers.get(FontHandler.getInstance().textRenderers.size() - 1); //ie largest one
		TextRenderer fi = textRenderers.get(FontHandler.getInstance().textRenderers.size() - 1); //ie largest one
		FontRenderContext frc = fi.getFontRenderContext();
		Font font = fi.getFont();

		Rectangle2D bounds;

		if (exactPixelBounds == true)
		{
			GlyphVector gv1 = font.createGlyphVector(frc, this.text);
			bounds = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
		}
		else
		{
			bounds = font.getStringBounds(this.text, frc);
		}	
			
		this.h = ((float) ((bounds.getHeight()) * this.w) / (float) bounds.getWidth());
	}
	
	public static GeomText2 createGeomTextWithQuotes(String text)
	{
		return createGeomTextWithQuotes(0f, 0f, 0f, 2f, 1f, text);
	}

	public static GeomText2 createGeomTextWithQuotes(
					float x, float y, float z, float w, float h,
					String text)
	{
		GeomText2 gtf = new GeomText2(x, y, z, w, h, text);
		gtf.setColor(1f, 1f, 1f, 1f);
		//gtf.backgroundColor= new Colorf();
		gtf.determineRotateAnchor(RotateEnum.CENTER);
		gtf.determineScaleAnchor(ScaleEnum.CENTER);

		GeomText2 gtf2 = new GeomText2(-.4f, .4f, 0f, .36f, .5f, "\u201C");
		gtf2.setColor(1f, 1f, 1f, 1f);
		//gtf2.backgroundColor= new Colorf();
		gtf.addGeom(gtf2, true);

		GeomText2 gtf3 = new GeomText2(gtf.w + .05f, .4f, 0f, .36f, .5f, "\u201D");
		gtf3.setColor(1f, 1f, 1f, 1f);
		gtf.addGeom(gtf3, true);

		//gtf2.registerDraggableObject(gtf);
		//gtf3.registerDraggableObject(gtf);
		gtf2.registerSelectableObject(gtf);
		gtf3.registerSelectableObject(gtf);
		return gtf;
	}
}
