/* GeomVertexBufferObject.java (created on Aug 6, 2008) */

package geometry;

import com.sun.opengl.util.BufferUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import utils.Utils;


/**
 *
 * @author Angus Forbes
 */
public class GeomVertexBufferObject extends Geom 
{
	int numTriangles = 50;
	int numPoints = numTriangles * 3;


	int stride = 7;
	//float[] v_arr = new float[ (numPoints * 3) + (numPoints * 3)];
	float[] v_arr = new float[ (numPoints * 3) + (numPoints * 4)];
	
	private FloatBuffer bigArraySystem;

	private FloatBuffer bigArray;
	
	private ByteBuffer bigArrayVBOBytes;
	private FloatBuffer bigArrayVBO;

	FloatBuffer vs, cs, bs;

	VBOBuffer vboBuffer;
	
	private int bigBufferObject;

	int bufsize; 

	
	static class VBOBuffer
	{
		public FloatBuffer vertices;
		public FloatBuffer colors;
		public int vertexOffset;
		public int colorOffset;
	}
	public GeomVertexBufferObject()
	{
		for (int i = 0; i < v_arr.length; i+=stride)
		{
			//vertices
			v_arr[i] = Utils.randomFloat();
			v_arr[i + 1] = Utils.randomFloat();
			v_arr[i + 2] = 0f;
		
			//colors
			v_arr[i + 3] = 0f; //Utils.randomFloat();
			v_arr[i + 4] = 0f; //Utils.randomFloat();
			v_arr[i + 5] = 1f; //Utils.randomFloat();
			if (stride == 7)
			{
				v_arr[i + 6] = .5f; //Utils.randomFloat();
			}
			
		}

		 bufsize = v_arr.length * BufferUtil.SIZEOF_FLOAT;
		 System.out.println("bufsize = " + bufsize);
	
		 
		
		 /*
		 // Fill a buffer with vertex coordinates 
			ByteBuffer bb = ByteBuffer.allocateDirect(bufsize * BufferUtil.SIZEOF_FLOAT);
			bb.order(ByteOrder.nativeOrder());
			bs = bb.asFloatBuffer();
	
			
			bs.put(v_arr); 
			bs.rewind(); 
			
			
			vs = sliceBuffer(bs, 0, (bufsize));
			cs = sliceBuffer(vs, 3, vs.limit() - 3 );
			*/
	}

	private void allocateBigArray(GL gl)
	{
		bigArraySystem = setupBuffer(ByteBuffer.allocateDirect(bufsize));

		int[] tmp = new int[1];
		gl.glGenBuffers(1, tmp, 0);
		bigBufferObject = tmp[0];
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bigBufferObject);
		// Initialize data store of buffer object
		gl.glBufferData(GL.GL_ARRAY_BUFFER, bufsize, (Buffer) null, GL.GL_DYNAMIC_DRAW);
		bigArrayVBOBytes = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY);
		bigArrayVBO = setupBuffer(bigArrayVBOBytes);
		gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
		// Unbind buffer; will be bound again in main loop
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

		float megabytes = (bufsize / 1000000.f);
		System.err.println("Allocated " + megabytes + " megabytes of fast memory");
	
		vboBuffer = new VBOBuffer();
	
		bigArray = bigArrayVBO;
	}

	
	private void setupBuffers()
	{
		System.out.println("in sliceBuffer()");
			vboBuffer.vertices = sliceBuffer(bigArray, 0, bufsize / BufferUtil.SIZEOF_FLOAT);
			vboBuffer.colors = sliceBuffer(vboBuffer.vertices, 3,
							vboBuffer.vertices.limit() - 3);
			vboBuffer.vertexOffset = 0;
			vboBuffer.colorOffset = 3 * BufferUtil.SIZEOF_FLOAT;
	}

	
	private FloatBuffer setupBuffer(ByteBuffer buf)
	{
		buf.order(ByteOrder.nativeOrder());
		return buf.asFloatBuffer();
	}

	
	private FloatBuffer sliceBuffer(FloatBuffer array,
																	int sliceStartIndex, int sliceLength)
	{
		
		array.position(sliceStartIndex);
		FloatBuffer ret = array.slice();
		System.out.println("ret capacity = " + ret.capacity());
		System.out.println("slice length = " + sliceLength);
		array.position(0);
		ret.limit(sliceLength);
		return ret;
	}

	boolean firstTime = true;
	int[] fVBO = new int[1];
	int vertexOffset = 0;
	int colorOffset = 3 * BufferUtil.SIZEOF_FLOAT;

	boolean immediateMode = false; //true;
	
	float posx = -2f;
	float posy = 2f;
	public void draw(GL gl)
	{

		//immediate mode
		if (immediateMode == true)
		{
		gl.glBegin(GL.GL_TRIANGLES);

			
		for (int i = 0; i < v_arr.length; i+=stride)
		{	
			v_arr[i] += Utils.randomFloat(.0001f, .0005f);
			v_arr[i + 1] += Utils.randomFloat(.0001f, .0005f);
			
		  if (stride == 7)
			{
				gl.glColor4f(v_arr[i + 3], v_arr[i + 4], v_arr[i + 5], v_arr[i + 6]);
			}
			else if (stride == 6)
			{
				gl.glColor4f(v_arr[i + 3], v_arr[i + 4], v_arr[i + 5], 1f);
			}
	
			
			gl.glVertex3f(v_arr[i + 0], v_arr[i + 1], v_arr[i + 2]);

		}
		
		gl.glEnd();
		return;
		}
		
		//if (1 == 1) return;
		

		if (firstTime == true)
		{
			System.out.println("allocating...");
			allocateBigArray(gl);
			setupBuffers();

			gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
	
			
			bigArray.put(v_arr); 
			bigArray.rewind(); 
			
			/*
			gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL.GL_COLOR_ARRAY);

			gl.glGenBuffers(1, fVBO, 0); 
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, fVBO[0]); 
			gl.glBufferData(GL.GL_ARRAY_BUFFER, bufsize, bigArray, GL.GL_STATIC_DRAW); 

			gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		  gl.glDisableClientState(GL.GL_COLOR_ARRAY);
			*/
		
		//gl.glBindBufferARB(GL.GL_ARRAY_BUFFER, bigBufferObject);
		//gl.glVertexPointer(3, GL.GL_FLOAT, stride * BufferUtil.SIZEOF_FLOAT, vboBuffer.vertexOffset);
		//	gl.glColorPointer(4, GL.GL_FLOAT, stride * BufferUtil.SIZEOF_FLOAT, vboBuffer.colorOffset);

			firstTime = false;
			
		}

		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);

		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER, bigBufferObject);

		
		ByteBuffer tmp = gl.glMapBufferARB(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY);
		if (tmp == null)
		{
					throw new RuntimeException("Unable to map vertex buffer object");
		}
		
		if (tmp != bigArrayVBOBytes)
		{
			System.out.println("recreating 1...");
			bigArrayVBOBytes = tmp;
			bigArrayVBO = setupBuffer(tmp);
		}
			
		if (bigArray != bigArrayVBO)
		{
			System.out.println("recreating 2...");
					bigArray = bigArrayVBO;
					setupBuffers();
		}

			gl.glColorPointer(4, GL.GL_FLOAT, stride * BufferUtil.SIZEOF_FLOAT, vboBuffer.colorOffset);
			gl.glVertexPointer(3, GL.GL_FLOAT, stride * BufferUtil.SIZEOF_FLOAT, vboBuffer.vertexOffset);

			/*
			FloatBuffer v = vboBuffer.vertices;
			for (int vi = 0; vi < v.capacity(); vi += (stride))
			{
				v.put(vi, v.get(vi) + + Utils.randomFloat(.001f, .005f));
				v.put(vi + 1, v.get(vi + 1) + Utils.randomFloat(.001f, .005f));

				v.put(vi + 2, 0f);

				v.put(vi + 3, 1f); 
				v.put(vi + 4, 0f); 
				v.put(vi + 5, 0f); 
			}
			 */
			
			gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
			//UNMAPPED...
	
			gl.glDrawArrays(GL.GL_TRIANGLES, 0, numPoints);
		
			gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL.GL_COLOR_ARRAY);
	
		if (1 == 1) return;
		if (firstTime == true)
		{
		
			//bs = FloatBuffer.allocate(bufsize); 
			
		
			// Create and bind a new VBO 
			gl.glGenBuffers(1, fVBO, 0); 
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, fVBO[0]); 
			gl.glBufferData(GL.GL_ARRAY_BUFFER_ARB, bufsize ,bs, GL.GL_STATIC_DRAW); 

		
			// Write the data to the VBO 
	
			firstTime = false;
			
			System.out.println("bs capacity = " + bs.capacity());
			System.out.println("cs capacity = " + cs.capacity());
			System.out.println("vs capacity = " + vs.capacity());
			//Utils.sleep(1000);

		
			return;
		}
		
		// Enable vertex arrays 
    gl.glEnableClientState(GL.GL_VERTEX_ARRAY); 
    gl.glEnableClientState(GL.GL_COLOR_ARRAY); 
    // Bind the VBO 
  
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, fVBO[0]); 
		
		//gl.glInterleavedArrays(GL.GL_C3F_V3F, 6 * BufferUtil.SIZEOF_FLOAT, colorOffset);
			gl.glColorPointer(4, GL.GL_FLOAT, stride * BufferUtil.SIZEOF_FLOAT, colorOffset);
			gl.glVertexPointer(3, GL.GL_FLOAT, stride * BufferUtil.SIZEOF_FLOAT, vertexOffset);

    gl.glDrawArrays(GL.GL_LINES, 0, numPoints);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
	

		//reg vertex arrays
		/*
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
	
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, vb);
		gl.glColorPointer(3, GL.GL_FLOAT, 0, cb);
		
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, numPoints);
	
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		*/
	}
	
}
