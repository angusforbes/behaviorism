/* State.java (created on November 25, 2007, 2:03 PM) */
package behaviorism.renderers;

import behaviorism.utils.RenderUtils;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;

public class State
{

  public boolean DEPTH_TEST = true;
  //public boolean BLEND = true;
  public boolean BLEND = false;

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

  //applyState?
  public void state()
  {
    GL2 gl = RenderUtils.getGL();
    if (DEPTH_TEST)
    {
      gl.glEnable(GL_DEPTH_TEST);
    }
    else
    {
      gl.glDisable(GL_DEPTH_TEST);
    }

    if (BLEND)
    {
      gl.glEnable(GL_BLEND);
    }
    else
    {
      gl.glDisable(GL_BLEND);
    }
  }

  public static void printCurrentState()
  {
    boolean blend = RenderUtils.getBoolean(GL_BLEND);
    boolean depthTest = RenderUtils.getBoolean(GL_DEPTH_TEST);

    System.out.println("BLEND : " + blend);
    System.out.println("DEPTH TEST : " + depthTest);
  }
}
