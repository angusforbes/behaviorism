/* AAA.java ~ Jan 4, 2009 */

package worlds;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

/**
 * This program demonstrates point parameters and their effect on point
 * primitives. 250 points are randomly generated within a 10 by 10 by 40 region,
 * centered at the origin. In some modes (including the default), points that
 * are closer to the viewer will appear larger.<br>
 * <br>
 * Pressing the 'l', 'q', and 'c' keys switch the point parameters attenuation
 * mode to linear, quadratic, or constant, respectively. <br>
 * Pressing the 'f' and 'b' keys move the viewer forward and backwards. In
 * either linear or quadratic attenuation mode, the distance from the viewer to
 * the point will change the size of the point primitive. <br>
 * Pressing the '+' and '-' keys will change the current point size. In this
 * program, the point size is bounded, so it will not get less than 2.0, nor
 * greater than GL_POINT_SIZE_MAX.
 *
 * @author Kiet Le (Java port)
 */
public class AAA extends JFrame implements GLEventListener//
    , KeyListener //
// , MouseListener //
// , MouseMotionListener //
// , MouseWheelListener
{
  private GLCapabilities caps;

  private GLCanvas canvas;

  private GLU glu;

  private GLUT glut;


  private static float psize = 7.0f; //7f

  private static float pmax[] = new float[1];

  private static float constant[] = { 1.0f, 0.0f, 0.0f };

  //private static float linear[] = { 0.0f, 0.12f, 0.0f };
  private static float linear[] = { 0.0001f, 0.12f, 0.0f };

  //private static float quadratic[] = { 0.0f, 0.0f, 0.01f };
  private static float quadratic[] = { 0.000f, -0.0f, .001f };

  private KeyEvent key;


  public AAA() {
    //super("piontp");

    caps = new GLCapabilities();
    caps.setSampleBuffers(true);

    canvas = new GLCanvas(caps);
    canvas.addGLEventListener(this);
    canvas.addKeyListener(this);

    add(canvas);
  }

  public void run() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 500);
    setLocationRelativeTo(null);
    setVisible(true);
    canvas.requestFocusInWindow();
  }

  public static void main(String[] args) {
    new AAA().run();
  }

  public void init(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();
    glu = new GLU();
    glut = new GLUT();
    //
    gl.glNewList(1, GL.GL_COMPILE);
    gl.glBegin(GL.GL_POINTS);

    float sx = -5f;
    float xinc = 10f / 20f;

    for (int i = 0; i < 20; i++) {
      gl.glColor3f(1.0f, (float) Math.random(), (float) Math.random());
      gl.glVertex3f(sx + (xinc * i), 1f, -2.05f);
    }
    for (int i = 0; i < 20; i++) {
      gl.glColor3f(1.0f, (float) Math.random(), (float) Math.random());
      gl.glVertex3f(sx + (xinc * i), 0f, 0f);
    }
    for (int i = 0; i < 20; i++) {
      gl.glColor3f(1.0f, (float) Math.random(), (float) Math.random());
      gl.glVertex3f(sx + (xinc * i), -1f, 2.05f);
    }
    /*
       * randomly generated vertices: -5 < x < 5; -5 < y < 5; -5 < z < -45
//       */
//      gl.glVertex3f((float) ((Math.random() * (5 - -5)) - 5.0f),//
//          (float) ((Math.random() * (5 - -5)) - 5.0f),//
//          (float) (((Math.random() * (5 - -5)) - 5.0f)));
//    }
    gl.glEnd();
    gl.glEndList();

    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glEnable(GL.GL_POINT_SMOOTH);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glPointSize(psize);
    gl.glGetFloatv(GL.GL_POINT_SIZE_MAX, pmax, 0);

    gl.glPointParameterfv(GL.GL_POINT_DISTANCE_ATTENUATION, quadratic, 0);
    gl.glPointParameterf(GL.GL_POINT_FADE_THRESHOLD_SIZE, 2.0f);

  }

  public void display(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    if (key != null)
      switch (key.getKeyChar()) {
      case 'b':
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glTranslatef(0.0f, 0.0f, -0.05f);

        break;
      case 'c':
        gl.glPointParameterfv(GL.GL_POINT_DISTANCE_ATTENUATION,
            constant, 0);

        break;
      case 'f':
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glTranslatef(0.0f, 0.0f, 0.05f);

        break;
      case 'l':
        //gl.glPointParameterfv(GL.GL_DISTANCE_ATTENUATION_EXT,
        gl.glPointParameterfv(GL.GL_POINT_DISTANCE_ATTENUATION,
            linear, 0);

        break;
      case 'q':
        //gl.glPointParameterfv(GL.GL_DISTANCE_ATTENUATION_EXT,
        gl.glPointParameterfv(GL.GL_POINT_DISTANCE_ATTENUATION,
            quadratic, 0);

        break;
      case '+':
        if (psize < (pmax[0] + 1.0f))
          psize = psize + 1.0f;
        gl.glPointSize(psize);

        break;
      case '-':
        if (psize >= 2.0f)
          psize = psize - 1.0f;
        gl.glPointSize(psize);

        break;

      }

    gl.glCallList(1);
    gl.glFlush();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    GL gl = drawable.getGL();
    //
    gl.glViewport(0, 0, w, h);
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(35, (float) w / h, 0.25, 200);
    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glTranslatef(0.0f, 0.0f, -10.0f);

  }

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged) {
  }

  public void keyTyped(KeyEvent key) {
  }

  public void keyPressed(KeyEvent key) {
    this.key = key;
    switch (key.getKeyChar()) {

    case KeyEvent.VK_ESCAPE:
      System.exit(0);
      break;

    default:
      break;
    }
    canvas.display();
  }

  public void keyReleased(KeyEvent key) {
  }


}