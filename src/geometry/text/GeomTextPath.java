/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geometry.text;

import geometry.*;
import behaviorism.Behaviorism;
import com.sun.opengl.util.j2d.TextRenderer;
import handlers.FontHandler;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import renderers.Renderer;
import utils.GeomUtils;
import utils.MatrixUtils;
import utils.RenderUtils;

/**
 * GeomTextPath arranges the text along a specified base path... class is still being built
 * @author angus
 */
public class GeomTextPath extends GeomPoly
{

	String text = "";
	protected int prevPxWidth = 0;
	protected int prevPxHeight = 0;
	protected int pxWidth = 0;
	protected int pxHeight = 0;
	protected TextRenderer textRenderer = null;
	protected Rectangle2D stringBounds = null;
	protected float scaleVal = 1f;
	protected float xpos = 0f;
	protected float ypos = 0f;
	public List<GeomPoint> pathVertices = new ArrayList<GeomPoint>();
	List<Chunk> chunks = new ArrayList<Chunk>();

	public GeomTextPath(String text, GeomPoint... geomPoints)
	{
		this.pathVertices = Arrays.asList(geomPoints);
		this.text = text;

		float lengthOfPath = 0f;

		for (int i = 0; i < pathVertices.size() - 1; i++)
		{
			float length = GeomUtils.getDistanceBetweenPoints(pathVertices.get(i), pathVertices.get(i + 1));
			float angle = (float) Math.toDegrees(GeomUtils.getAngleBetweenPoints(pathVertices.get(i), pathVertices.get(i + 1)));
			chunks.add(new Chunk(pathVertices.get(i), length, lengthOfPath, lengthOfPath + length, angle));
			lengthOfPath += length;
		}

		this.w = lengthOfPath;
		setHeightByWidth(this.w); //don't care about this right now....

		System.out.println("w = " + w);
		System.out.println("h = " + h);

		this.vertices = makePolyVerticesFromPath(pathVertices, h);
		System.out.println("vertices size : " + vertices.size());
	}

	private List<GeomPoint> makePolyVerticesFromPath(List<GeomPoint> pathVertices, float height)
	{

		List<GeomPoint> gps = new ArrayList<GeomPoint>();
		gps.addAll(pathVertices);

		for (int i = pathVertices.size() - 1; i >= 1; i--)
		{
			GeomPoint gp1 = pathVertices.get(i);
			GeomPoint gp2 = pathVertices.get(i - 1);

			Point3f p1 = GeomUtils.turnDegrees(90f, this.h, GeomUtils.toPoint3f(gp2),
							GeomUtils.toPoint3f(gp1));
			Point3f p2 = GeomUtils.turnDegrees(-90f, this.h, GeomUtils.toPoint3f(gp1),
							GeomUtils.toPoint3f(gp2));

			gps.add(new GeomPoint(p1));
			gps.add(new GeomPoint(p2));

		//System.out.println("p1 = " + p1);
		//System.out.println("p2 = " + p2);

		}

		/*
		Point3f p1 = GeomUtils.turnDegrees(90f, this.h, GeomUtils.toPoint3f(pathVertices.get(0)),
		GeomUtils.toPoint3f(pathVertices.get(1)) );
		Point3f p2 = GeomUtils.turnDegrees(-90f, this.h, GeomUtils.toPoint3f(pathVertices.get(1)),
		GeomUtils.toPoint3f(pathVertices.get(0)) );
		
		System.out.println("p1 = " + p1);
		System.out.println("p2 = " + p2);
		
		gps.add(new GeomPoint(p1));
		gps.add(new GeomPoint(p2));
		 */
		return gps;
	}

	public void setHeightByWidth(float w)
	{
		System.out.println("in setHeightByWidth!");
		this.w = w;
		TextRenderer fi = FontHandler.getInstance().getLargestTextRenderer();

		FontRenderContext frc = fi.getFontRenderContext();
		Font font = fi.getFont();

		GlyphVector gv1 = font.createGlyphVector(frc, this.text);
		Rectangle2D bounds1 = gv1.getPixelBounds(null, 0f, 0f); /* FontRenderContext renderFRC, */
		this.h = ((float) ((bounds1.getHeight()) * this.w) / (float) bounds1.getWidth());
	}

	/*
	public GeomTextPath(String text, List<GeomPoint> vertices)
	{
	super(vertices);
	this.text = text;
	
	float lengthOfPath = 0f;
	
	for (int i = 0; i < vertices.size() - 1; i++)
	{
	lengthOfPath += GeomUtils.getDistanceBetweenPoints(vertices.get(i), vertices.get(i + 1));
	
	}
	
	this.w = lengthOfPath;
	this.h = 2f; //don't care about this right now....
	}
	 */
	private class Chunk
	{

		GeomPoint p;
		float length, angle, x, w;

		public Chunk(GeomPoint p, float length, float x, float w, float angle)
		{
			this.p = p;
			this.length = length;
			this.x = x;
			this.w = w;
			this.angle = angle;
		}
	}

	@Override
	public void draw(GL gl)
	{
    GLU glu = Renderer.getInstance().glu;
    
		//gl.glColor4f(1f, 0f, 0f, .5f);
		//drawConvex(gl, glu, offset);
		this.color.r = 1f;
		this.color.g = 0f;
		this.color.b = 0f;
		this.color.a = .3f;
		//drawTesselated(gl, glu, offset);
		this.color.r = 1f;
		this.color.g = 1f;
		this.color.b = 1f;
		this.color.a = 1f;

		calculateUnrotatedPixelWidthAndHeight(gl);


		if (FontHandler.getInstance().fontsReady.get() == true || this.pxWidth != this.prevPxWidth || this.pxHeight != this.prevPxHeight ||
						this.textRenderer == null)
		{
			//System.out.printf("prev = %d/%d, cur = %d/%d\n", this.prevPxWidth, this.prevPxHeight, this.pxWidth, this.pxHeight);

			if (FontHandler.getInstance().fontsReady.get() == true)
			{
				setHeightByWidth(this.w); //don't care about this right now....
				this.vertices = makePolyVerticesFromPath(pathVertices, h);
				
				//need to recalculate pixel bounds if changing geom bounds! (otherwise chooseFont gets called twice, which looks jerky)
				calculateUnrotatedPixelWidthAndHeight(gl);
			}

			chooseFont();
			justifyText();

			this.prevPxWidth = this.pxWidth;
			this.prevPxHeight = this.pxHeight;
		}


		renderInvisiblePickingBackground(gl, glu, offset);

		Chunk chunk = chunks.get(0);
		float pos = 0;
		float fullpos = 0;

		for (int i = 0; i < text.length(); i++)
		{
			String ch = "" + text.charAt(i);

			FontRenderContext frc2 = textRenderer.getFontRenderContext();
			Font font2 = textRenderer.getFont();
			//GlyphVector gv2 = font2.createGlyphVector(frc2, ch);
			//Rectangle2D bounds2 = gv2.getPixelBounds(null, 0f, 0f);
			Rectangle2D bounds2 = font2.getStringBounds(ch, frc2);
			float strw2 = (float) (bounds2.getWidth());
			float strh2 = (float) (bounds2.getHeight());

			gl.glPushMatrix();
			gl.glTranslatef(chunk.p.translate.x, chunk.p.translate.y, chunk.p.translate.z);
			//System.out.println("chunk.p = " + chunk.p);
			gl.glRotatef(chunk.angle, 0f, 0f, 1f);

			textRenderer.begin3DRendering();
			textRenderer.setColor(this.color.r, this.color.g, this.color.b, this.color.a);
			textRenderer.draw3D(ch, pos, this.ypos, offset, this.scaleVal);
			textRenderer.end3DRendering();

			pos += (strw2 * scaleVal);
			fullpos += (strw2 * scaleVal);
			if (pos > chunk.length)
			{
				for (Chunk test : chunks)
				{
					if (test.w > fullpos)
					{
						//use this chunk
						chunk = test;
						pos = fullpos - chunk.x;
						//pos = 0f;
						break;
					}
				}
			}
			//System.out.println(i + ", pos = " + pos + " scaled = " + (pos * scaleVal));
			gl.glPopMatrix();
		}
	}

	public void renderInvisiblePickingBackground(GL gl, GLU glu, float offset)
	{
		boolean depthTest = RenderUtils.getBoolean(gl, GL.GL_DEPTH_TEST);
		//if (depthTest == false && isSelectable == true)
		if (isSelectable == true)
		{
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glColor4f(0f, 0f, 0f, 0f);

			drawConvex(gl, glu, offset);

			/*
			float bgc_z = -offset;
			
			gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f(0f, 0f, bgc_z);
			gl.glVertex3f(this.w, 0f, bgc_z);
			gl.glVertex3f(this.w, this.h, bgc_z);
			gl.glVertex3f(0f, this.h, bgc_z);
			gl.glEnd();
			 */
			gl.glDisable(GL.GL_DEPTH_TEST);
		}
	}

	private void justifyText()
	{
		this.xpos = 0f;
		float ttx = (float) stringBounds.getX() * scaleVal;
		float ttw = (float) stringBounds.getWidth() * scaleVal;
		this.xpos -= ttx;

		this.ypos = 0f;
		float tty = (float) stringBounds.getY() * scaleVal;
		float tth = (float) stringBounds.getHeight() * scaleVal;
		float diffy = tth + tty;
		this.ypos += diffy;
	}

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
			//newp = new Point3d(rotateAnchor.translate.x, rotateAnchor.translate.y, 0f);
			newp = new Point3d(w * .5f, h * .5f, 0f);
			world_z = (float) (MatrixUtils.getGeomPointInWorldCoordinates(newp, modelview, RenderUtils.getCamera().modelview)).z;

		}
		else
		{
			newp = new Point3d(parent.w * .5f, parent.h * .5f, 0f);
			//newp = new Point3d();
			world_z = (float) (MatrixUtils.getGeomPointInWorldCoordinates(newp, parent.modelview, RenderUtils.getCamera().modelview)).z;
		//world_z = (float) (MatrixUtils.getGeomPointInGeomCoordinates(newp, modelview, parent.modelview)).z;
		//System.out.println("world_z for child = " + world_z);
		}

//		Renderer.getInstance().resetPerspective3D();

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

		//this.pxWidth = (int) (RenderUtils.getWidthOfObjectInPixels(this, this.insetX, temp_mv, temp_pj, temp_vp));
		//this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(this, this.insetY, temp_mv, temp_pj, temp_vp));
		this.pxWidth = (int) (RenderUtils.getWidthOfObjectInPixels(this, 0, temp_mv, temp_pj, temp_vp));
		this.pxHeight = (int) (RenderUtils.getHeightOfObjectInPixels(this, 0, temp_mv, temp_pj, temp_vp));

		/*
		
		//if (MouseHandler.selectedGeom == this)
		{
		//this should stay the same during x/y translations and rotations
		System.out.println("pxWidth / pxHeight = " + this.pxWidth + "/" + this.pxHeight);
		
		//to debug -- if all is well this should be in the center of the
		//  screen and should NOT translate at all during x/y translations rotations
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

	public void chooseFont()
	{
		float scaleValX, scaleValY;
		TextRenderer textRendererX = null, textRendererY = null;
		Rectangle2D stringBoundsX = null, stringBoundsY = null;

		//get correct font size for the current pixel w/h of the Geom
		for (int i = 1; i < FontHandler.getInstance().textRenderers.size(); i++)
		{
			boolean readyToBreakX = false;
			boolean readyToBreakY = false;
			scaleValX = 1f;
			scaleValY = 1f;

			TextRenderer tr1 = (FontHandler.getInstance().textRenderers.get(i - 1));
			FontRenderContext frc1 = tr1.getFontRenderContext();
			Font font1 = tr1.getFont();
			GlyphVector gv1 = font1.createGlyphVector(frc1, this.text);
			Rectangle2D bounds1 = gv1.getPixelBounds(null, 0f, 0f);
			float strw1 = (float) (bounds1.getWidth());
			float strh1 = (float) (bounds1.getHeight());

			TextRenderer tr2 = (FontHandler.getInstance().textRenderers.get(i));
			FontRenderContext frc2 = tr2.getFontRenderContext();
			Font font2 = tr2.getFont();
			GlyphVector gv2 = font2.createGlyphVector(frc2, this.text);
			Rectangle2D bounds2 = gv2.getPixelBounds(null, 0f, 0f);
			float strw2 = (float) (bounds2.getWidth());
			float strh2 = (float) (bounds2.getHeight());

			//System.out.println("trying " + tr1.getFont());
			//System.out.printf("strw avg / strh avg = %f / %f \n", ((strw1 + strw2) / 2f), ((strh1 + strh2) / 2f));
			//if (strw2 > pxWidth)
			if ((strw1 + strw2) / 2f > pxWidth)
			{
				textRendererX = tr1;
				stringBoundsX = bounds1;
				//scaleValX = (this.w * (1f - insetX)) / (float) (bounds1.getWidth());
				scaleValX = (this.w * (1f)) / (float) (bounds1.getWidth());
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
				scaleValY = (this.h * (1f)) / (float) bounds1.getHeight();
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

			if (i == FontHandler.getInstance().textRenderers.size() - 1) //use biggest one
			{
				//System.out.println("using biggest available");
				//this.textRenderer = tr2;
				//this.stringBounds = bounds2;
				//this.scaleVal = (this.w) / (float) bounds2.getHeight();

				this.textRenderer = tr2;
				//this.stringBoundsX = bounds2;
				this.stringBounds = bounds2;
				//this.scaleVal = (this.w * (1f - insetX)) / (float) (bounds2.getWidth());
				this.scaleVal = (this.w * (1f)) / (float) (bounds2.getWidth());

				//TEMP!!!
				return;

			//font = font2;
			//frc = frc2;
			}
		}

	}
}
