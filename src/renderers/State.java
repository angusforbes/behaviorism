/* State.java (created on November 25, 2007, 2:03 PM) */

package renderers;

import javax.media.opengl.GL;
import renderers.RendererJogl;

public class State
{
  public boolean DEPTH_TEST = true;
  public boolean BLEND = true;

  /** create a new State with default values */
  public State()
  {
  }
  
  /** create a new State with the values of an existing State */
  public State(State parentState)
  {
    this.DEPTH_TEST = parentState.DEPTH_TEST;
    this.BLEND = parentState.BLEND;
  }
  
  public void setState(GL gl)
  {
    if (DEPTH_TEST)
    {
      gl.glEnable(GL.GL_DEPTH_TEST);
    }
    else
    {
      gl.glDisable(GL.GL_DEPTH_TEST);
    }

    if (BLEND)
    {
      gl.glEnable(GL.GL_BLEND);
    }
    else
    {
      gl.glDisable(GL.GL_BLEND);
    }
  }

  public static void printCurrentState(GL gl)
  {
    boolean blend = RendererJogl.getBoolean(gl, GL.GL_BLEND);
    boolean depthTest = RendererJogl.getBoolean(gl, GL.GL_DEPTH_TEST);
    
    System.out.println("BLEND : " + blend);
    System.out.println("DEPTH TEST : " + depthTest);
  }
}