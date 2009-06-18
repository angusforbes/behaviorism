/* GeomDisplayList.java (created on Jul 17, 2008) */
package geometry;

import com.sun.opengl.util.BufferUtil;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import utils.Utils;

/**
 *
 * @author Angus Forbes
 */
public class GeomDisplayList extends Geom
{
	public int displayListNum = -1;
	Geom geom = null;

	public GeomDisplayList(Point3f p3f, GeomDisplayList gdl)
	{
		super(p3f);
		this.displayListNum = gdl.displayListNum;
		System.out.println("added displayListNum : " + this.displayListNum);
	}

	public GeomDisplayList(Point3f p3f, int displayListNum)
	{
		super(p3f);
		this.displayListNum = displayListNum;
	}

	public GeomDisplayList(Point3f p3f, Geom geom)
	{
		super(p3f);
		this.geom = geom;
	}	//////////////// Constants /////////////////////////

	// Number of Points in the array.
	final int nbPoints = 20000;	//////////////// Variables /////////////////////////

	// We use a buffer and an array for the vertex data
	// to more compatible with C.
	FloatBuffer points;
	float[] pointsData;
	int[] VBO = new int[1];

	private void initArrayData(GL gl)
	{



		// Create data points on the surface of a cube.
		int nbValues = nbPoints * 3;
		pointsData = new float[nbValues];

		float fff = -2f;
		for (int i = 0; i < nbPoints; i++)
		{
			pointsData[3 * i] = Utils.randomFloat();
			pointsData[3 * i + 1] = Utils.randomFloat();
			;
			//pointsData[3 * i] = fff;
			//pointsData[3 * i + 1] = 1f;
			pointsData[3 * i + 2] = 0f;

			fff += .1f;

		/*
		System.out.println("point["+i+"] = " +
		pointsData[i] + "," + 
		pointsData[i+1] + "," + 
		pointsData[i+2]); 
		 */
		}

		System.out.println("nbValues = " + nbValues + " pointsData.lenth = " + pointsData.length);
		// Points.
		points = BufferUtil.newFloatBuffer(nbValues);
		points.put(pointsData, 0, nbValues);
		points.rewind();

		/*
		// Colors.
		colors = BufferUtil.newFloatBuffer(nbValues);
		colors.put(colorsData, 0, nbValues);
		colors.rewind();
		gl.glColorPointer(3, GL.GL_FLOAT, 0, colors);
		 */

		gl.glGenBuffers(1, VBO, 0);

		// Enable same as for vertex buffers.
		//gl.glEnableClientState( GL.GL_VERTEX_ARRAY );


		// Init VBOs and transfer data.
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[0]);
		// Copy data to the server into the VBO.
		gl.glBufferData(GL.GL_ARRAY_BUFFER,
						nbValues * BufferUtil.SIZEOF_FLOAT, points,
						//nbValues, points,
						GL.GL_STATIC_DRAW);

	}

	@Override
	public void draw(GL gl)
	{
		gl.glColor4f(1f, 0f, 0f, .01f);
	gl.glPointSize(20f);
	
		if (displayListNum == -1)
		{
			//gl.glEnableClientState( GL.GL_NORMAL_ARRAY );

			initArrayData(gl);
			displayListNum = 1;
		}


		/*
		gl.glBegin(GL.GL_TRIANGLES);
		{
		for (int i = 0; i < nbPoints; i++)
		{
		gl.glVertex3f(points.get(i * 3),
		points.get(i * 3 + 1),
		points.get(i * 3 + 2));
		}
		}
		gl.glEnd();
		 */

// Draw.
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[0]);


		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
		gl.glColor4f(1f, 0f, 0f, .02f);

		gl.glDrawArrays(GL.GL_POINTS, 0, nbPoints);
		//gl.glDrawArrays(GL.GL_LINE_LOOP, 0, nbPoints);
		gl.glColor4f(0f, 0f, 1f, .09f);
		//gl.glDrawArrays(GL.GL_POINTS, 0, nbPoints);

		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	//gl.glDeleteBuffers(1, VBO, 0);

	/*
	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
	//gl.glEnableClientState( GL.GL_COLOR_ARRAY );
	
	gl.glDrawArrays(GL.GL_TRIANGLES, 0, nbPoints);
	
	gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	//gl.glDisableClientState(GL.GL_COLOR_ARRAY);
	 */
	/*
	if (displayListNum == -1)
	{
	System.out.println("here ... displayListNum = " + displayListNum );
	displayListNum = gl.glGenLists(1);
	
	if (geom == null)
	{
	return;
	}
	
	gl.glNewList(displayListNum, GL.GL_COMPILE);
	
	for (float f = -1f; f < 1f; f+= .01f)
	{
	gl.glTranslatef(.01f, 0f, 0f);
	geom.draw(gl, glu, offset);
	}
	gl.glEndList();
	
	System.out.println("here... displayListNum = " + displayListNum);
	}
	
	gl.glCallList(displayListNum);
	 */
	/*
	// Enable all client states we are using.
	gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
	gl.glEnableClientState(GL.GL_COLOR_ARRAY);
	//glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	
	// Load the data to each pointer type we need.
	gl.glVertexPointer(3, GL.GL_FLOAT, 0, SquarePoints);
	gl.glColorPointer(3, GL.GL_FLOAT, 0, SquarePointColors);
	//glTexCoordPointer(2, GL_FLOAT, 0, SquareTexCoords);
	
	// Draw the entire object.
	gl.glDrawArrays(GL.GL_QUADS, 0, 4);
	
	// Disable all the client states we enabled.
	gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	gl.glDisableClientState(GL.GL_COLOR_ARRAY);
	//gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	 */

	}
}
