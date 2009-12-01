/*
 * KeyboardHandler.java
 * Created on March 12, 2007, 4:56 PM
 */
package behaviorism.handlers;

import behaviorism.Behaviorism;
import behaviorism.geometry.Geom;
import behaviorism.renderers.SceneGraph;
import behaviorism.renderers.cameras.Cam;
import behaviorism.utils.RenderUtils;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;
import java.awt.event.KeyListener;
import org.grlea.log.SimpleLogger;

/** 
 * KeyboardHandler is a wrapper for KeyListener supporting various modes
 * and the ability to be used from within a openGL display loop.
 * 
 * @author angus
 */
public class KeyboardHandler implements KeyListener
{

  public final boolean keys[] = new boolean[1024];  // Array Used For The Keyboard Routine
  public final boolean keysPressing[] = new boolean[1024];  // Used if an action should only be activated once per press
  public boolean controlPressing = false;

  public float rotationInc = 1f;
  public float rotateAnchorInc = .0125f;
  public float scaleInc = .05f;
  public float scaleAnchorInc = .0125f;
  public float translateInc = .025f;

  private enum ModeEnum
  {

    MODE_TRANSLATE, MODE_ROTATE, MODE_SCALE,
    MODE_ROTATE_ANCHOR, MODE_SCALE_ANCHOR,
    MODE_FONT, MODE_PACK, MODE_DEBUG, MODE_CAM, MODE_STEP
  }

  private ModeEnum mode = ModeEnum.MODE_TRANSLATE;
  private static final KeyboardHandler instance = new KeyboardHandler();

  public static final SimpleLogger log = new SimpleLogger(KeyboardHandler.class);

  /**
   * Gets (or creates then gets) the singleton MouseHandler.getInstance() object.
   * @return the singleton MouseHandler.getInstance()
   */
  public static KeyboardHandler getInstance()
  {
    return instance;
  }

  private KeyboardHandler()
  {
  }

  /**
   * This method is called from the openGL Renderer display loop.
   * It checks the following types of keyboard events in the following order:
   * 1) Global keys (ie, quit, reset, pause, change mode) -- there aren't too many of these, they are in combo with CTRL or CMD (on mac)
   * 2) World-specific keys (optionally defined in each world by overriding checkKeys(boolean[] keys, boolean[] keysPressed)
   * 3) Mode-specific keys, which are keys that are only checked if we are in a particular mode
   * (usually by pressing the F# function keys).
   */
  public void processKeyboard()
  {
//    if (checkGlobalKeys()) //CTRL/CMD keys and ESC
//    {
//      return;
//    }

    if (checkWorldKeys()) //Custom keys can overwrite function and mode keys
    {
      return;
    }

    checkChangeModeKeys(); //Function keys set the mode

    checkModeKeys(); //keys specfic to a particular mode
  }

  public void keyPressed(KeyEvent evt)
  {

    log.entry("in keyPressed() : code=" + evt.getKeyCode() + ", text=" + getKeyText(evt.getKeyCode()));

    if (evt.getKeyCode() > keys.length)
    {
      return;
    }

    keys[evt.getKeyCode()] = true;
    log.exit("out keyPressed()");

    checkGlobalKeys(); //CTRL/CMD keys and ESC

  }

  public void keyTyped(KeyEvent evt)
  {
  }

  public void keyReleased(KeyEvent evt)
  {
    log.entry("in keyReleased() : releasing " + evt.getKeyCode() + ", contorlPressing = " + controlPressing );
    if (evt.getKeyCode() > keys.length)
    {
      return;
    }
    
    if (evt.getKeyCode() == VK_META || evt.getKeyCode() == VK_CONTROL)
    {
      controlPressing = false;
      for (int k=0; k < keys.length; k++)
      {
        keys[k] = false;
        keysPressing[k] = false;
      }
      return;
    }

    if (controlPressing == false)
    {
      keys[evt.getKeyCode()] = false;
      keysPressing[evt.getKeyCode()] = false;
    }
    log.exit("out keyReleased()");
  }

  /**
   * Checks to see if a function key was pressed and sets the current mode accordingly.
   */
  private void checkChangeModeKeys()
  {
    if (keys[KeyEvent.VK_F1])
    {
      mode = ModeEnum.MODE_TRANSLATE;
    }
    else if (keys[KeyEvent.VK_F2])
    {
      mode = ModeEnum.MODE_ROTATE;
    }
    else if (keys[KeyEvent.VK_F3])
    {
      mode = ModeEnum.MODE_SCALE;
    }
    else if (keys[KeyEvent.VK_F4])
    {
      mode = ModeEnum.MODE_PACK;
    }
    else if (keys[KeyEvent.VK_F5])
    {
      mode = ModeEnum.MODE_DEBUG;
    }
    else if (keys[KeyEvent.VK_F6])
    {
      mode = ModeEnum.MODE_CAM;
    }
    else if (keys[KeyEvent.VK_F7])
    {
      mode = ModeEnum.MODE_ROTATE_ANCHOR;
    }
    else if (keys[KeyEvent.VK_F8])
    {
      //g_keyMode = ModeEnum.MODE_SCALE_ANCHOR;
      mode = ModeEnum.MODE_STEP;
    }
    else if (keys[KeyEvent.VK_F9])
    {
      mode = ModeEnum.MODE_FONT;
    }
  }

  /**
   * Checks to see if any keys were pressed that are associated with the current mode
   */
  private void checkModeKeys()
  {

     //check mode-specific keys
    switch (mode)
    {
      case MODE_TRANSLATE:
        check_keyPressedTranslate();
        break;
      case MODE_ROTATE:
        check_keyPressedRotate();
        break;
      case MODE_ROTATE_ANCHOR:
        check_keyPressedRotateAnchor();
        break;
      case MODE_SCALE:
        check_keyPressedScale();
        break;
      case MODE_SCALE_ANCHOR:
        check_keyPressedScaleAnchor();
        break;
      case MODE_FONT:
        check_keyPressedFont();
        break;
      case MODE_CAM:
        check_keyPressedCam();
        break;
      case MODE_PACK:
        //check_keyPressedPack();
        break;
      case MODE_STEP:
        check_keyPressedStep();
        break;
      case MODE_DEBUG:
        check_keyPressedDebug();
        break;
    }

  }

  /**
   * Checks the basic keys that will be available to all applications (and that cannot be overridden
   * by the custom defined keyboard actions in a World).
   * These include CTRL-F to toggle between fullscreen mode, CTRL-C to turn the cursor on or off,
   * and CTRL-Q or ESC to quit the program.
   * @return true if a global key was pressed, false otherwise.
   */
  private boolean checkGlobalKeys()
  {
    if (keys[KeyEvent.VK_ESCAPE])
    {
      Behaviorism.getInstance().shutDown();

      return true;
    }

    if (keys[VK_CONTROL] || keys[VK_META])
    {
      controlPressing = true;
    }

    if (controlPressing == true)
    {
      if (keys[VK_C])
      {
        if (keysPressing[VK_C] == false)
        {
          keysPressing[VK_C] = true;
          Behaviorism.getInstance().toggleCursor();
        }
        return true;
      }

      if (keys[VK_F])
      {
        if (keysPressing[VK_F] == false)
        {
          keysPressing[VK_F] = true;

          Behaviorism.getInstance().toggleFullScreen();
        }
        return true;
      }
    }

    //below are just temp for testing!!
    if (keys[KeyEvent.VK_SPACE])
    {
      if (keysPressing[KeyEvent.VK_SPACE] == false)
      {
        SceneGraph.getInstance().isStepping = !SceneGraph.getInstance().isStepping;

        mode = ModeEnum.MODE_STEP;

        //BehaviorismDriver.renderer.currentWorld.isPaused = !BehaviorismDriver.renderer.currentWorld.isPaused;
        keysPressing[KeyEvent.VK_SPACE] = true;
      }
      return true;
    }


    if (keys[KeyEvent.VK_R])
    {
      if (keysPressing[KeyEvent.VK_R] == false)
      {
        SceneGraph.getInstance().reverse = true;
        keysPressing[KeyEvent.VK_R] = true;
      }
      return true;
    }

    return false;
  }

  /**
   * Checks any custom defined keys associated with the current World.
   * @return true if a key defined in the current World was pressed, false otherwise.
   */
  private boolean checkWorldKeys()
  {
   //check world-specific keys. if any world-specific keys were pressed, return so that we don't try to
    //apply that key to a mode-specific action.
    if (RenderUtils.getWorld() != null)
    {
      if (RenderUtils.getWorld().checkKeys(keys, keysPressing))
      {
        return true;
      }
    }
    return false;
  }

  
  private void check_keyPressedFont()
  {
    //Need to think of best way to do this...
    /*
    //switch fonts...
    if (keys[KeyEvent.VK_F])
    {
    //FontHandler.textRenderers.clear();
    //FontHandler.fontIndex = (FontHandler.fontIndex + 1) % FontHandler.fontNames.size();
    keys[KeyEvent.VK_F] = false;
    FontHandler.getInstance().changeFonts.set(true);
    //FontHandler.fontsReady = true;
    }
    if (keys[KeyEvent.VK_1])
    {
    keys[KeyEvent.VK_1] = false;
    FontHandler.getInstance().nextFont(1);
    }
    if (keys[KeyEvent.VK_2])
    {
    keys[KeyEvent.VK_2] = false;
    FontHandler.getInstance().nextFont(2);
    }
    if (keys[KeyEvent.VK_3])
    {
    keys[KeyEvent.VK_3] = false;
    FontHandler.getInstance().nextFont(3);
    }
    if (keys[KeyEvent.VK_4])
    {
    keys[KeyEvent.VK_4] = false;
    FontHandler.getInstance().nextFont(4);
    }
    if (keys[KeyEvent.VK_5])
    {
    keys[KeyEvent.VK_5] = false;
    FontHandler.getInstance().nextFont(5);
    }
    if (keys[KeyEvent.VK_6])
    {
    keys[KeyEvent.VK_6] = false;
    FontHandler.getInstance().nextFont(6);
    }
    if (keys[KeyEvent.VK_7])
    {
    keys[KeyEvent.VK_7] = false;
    FontHandler.getInstance().nextFont(7);
    }
    if (keys[KeyEvent.VK_8])
    {
    keys[KeyEvent.VK_8] = false;
    FontHandler.getInstance().nextFont(8);
    }
    if (keys[KeyEvent.VK_9])
    {
    keys[KeyEvent.VK_9] = false;
    FontHandler.getInstance().nextFont(9);
    }
     */
  }

  private void check_keyPressedCam()
  {
    //MouseHandler.getInstance().handleCam = true;
    Cam cam = RenderUtils.getCamera();

    if (keys[KeyEvent.VK_UP])
    {
      cam.changePitch(0.5f);
    }

    if (keys[KeyEvent.VK_DOWN])
    {
      cam.changePitch(-0.5f);
    }

    if (keys[KeyEvent.VK_LEFT])
    {
      cam.changeHeading(-0.5f);
    }

    if (keys[KeyEvent.VK_RIGHT])
    {
      cam.changeHeading(0.5f);
    }

    if (keys[KeyEvent.VK_HOME])
    {
      cam.changeYaw(0.5f);
    }

    if (keys[KeyEvent.VK_END])
    {
      cam.changeYaw(-0.5f);
    }

    if (keys[KeyEvent.VK_PAGE_DOWN])
    {
      cam.translateZ(.1f);
    }
    if (keys[KeyEvent.VK_PAGE_UP])
    {
      cam.translateZ(-.1f);
    }
    if (keys[KeyEvent.VK_1])
    {
      cam.translateX(-.1f);
    }
    if (keys[KeyEvent.VK_2])
    {
      cam.translateX(.1f);
    }
    if (keys[KeyEvent.VK_3])
    {
      //          BehaviorismDriver.renderer.cam.m_StrafeY += 0.1f;
      cam.translateY(-.1f);
    }

    if (keys[KeyEvent.VK_4])
    {
      //BehaviorismDriver.renderer.cam.m_StrafeY -= 0.1f;
      cam.translateY(.1f);
    }
  }

  private void check_keyPressedDebug()
  {
    //toggle ALL debugs
    if (keys[KeyEvent.VK_0])
    {
      if (keysPressing[KeyEvent.VK_0] == false) //all off
      {
        SceneGraph.drawDebugGrid = false;
        SceneGraph.drawDebugGeom = false;
        SceneGraph.drawDebugMouseMovedPoint = false;
        SceneGraph.drawDebugMouseDraggedPoint = false;
        SceneGraph.drawDebugFrameRate = false;
        keysPressing[KeyEvent.VK_0] = true;
      }
    }

    if (keys[KeyEvent.VK_9])
    {
      if (keysPressing[KeyEvent.VK_9] == false) //all on
      {
        SceneGraph.drawDebugGrid = true;
        SceneGraph.drawDebugGeom = true;
        SceneGraph.drawDebugMouseMovedPoint = true;
        SceneGraph.drawDebugMouseDraggedPoint = true;
        SceneGraph.drawDebugFrameRate = true;
        keysPressing[KeyEvent.VK_9] = true;
      }
    }


    if (keys[KeyEvent.VK_1])
    {
      if (keysPressing[KeyEvent.VK_1] == false)
      {
        SceneGraph.drawDebugGrid = !SceneGraph.drawDebugGrid;
        keysPressing[KeyEvent.VK_1] = true;
      }
    }

    if (keys[KeyEvent.VK_2])
    {
      if (keysPressing[KeyEvent.VK_2] == false)
      {
        SceneGraph.drawDebugGeom = !SceneGraph.drawDebugGeom;
        keysPressing[KeyEvent.VK_2] = true;
      }
    }

    if (keys[KeyEvent.VK_3])
    {
      if (keysPressing[KeyEvent.VK_3] == false)
      {
        SceneGraph.drawDebugMouseDraggedPoint = !SceneGraph.drawDebugMouseDraggedPoint;
        keysPressing[KeyEvent.VK_3] = true;
      }
    }

    if (keys[KeyEvent.VK_4])
    {
      if (keysPressing[KeyEvent.VK_4] == false)
      {
        SceneGraph.drawDebugFrameRate = !SceneGraph.drawDebugFrameRate;
        keysPressing[KeyEvent.VK_4] = true;
      }
    }
  }

  private void check_keyPressedTranslate()
  {
    Geom geom = MouseHandler.getInstance().selectedGeom;
    if (geom != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        geom.translateX(-translateInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        geom.translateX(+translateInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        geom.translateY(-translateInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        geom.translateY(+translateInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        geom.translateZ(-translateInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        geom.translateZ(+translateInc);
      }

    }
  }

  private void check_keyPressedRotate()
  {
    log.entry("in check_keyPressedRotate()");
    Geom geom = MouseHandler.getInstance().selectedGeom;
    if (geom != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        geom.rotateY(-rotationInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        geom.rotateY(+rotationInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        geom.rotateX(+rotationInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        geom.rotateX(-rotationInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        geom.rotateZ(-rotationInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        geom.rotateZ(+rotationInc);
      }
    }
    log.exit("check_keyPressedRotate()");
  }

  private void check_keyPressedStep()
  {
    if (keys[KeyEvent.VK_ENTER])
    {
      if (keysPressing[KeyEvent.VK_ENTER] == false)
      {
        SceneGraph.getInstance().isStepping = !SceneGraph.getInstance().isStepping;
        keysPressing[KeyEvent.VK_ENTER] = true;
      }
    }
    else
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        SceneGraph.getInstance().step = -1; //-= 1;
      }
      else
      {
        if (keys[KeyEvent.VK_RIGHT])
        {
          SceneGraph.getInstance().step = 1; //+= 1;
        }
        else
        {
          if (keys[KeyEvent.VK_UP])
          {
            //if (keysPressing[KeyEvent.VK_UP] == false)
            {
              SceneGraph.getInstance().stepSize += 1L;
              //keysPressing[KeyEvent.VK_UP] = true;
            }
          }
          else
          {
            if (keys[KeyEvent.VK_DOWN])
            {
              //if (keysPressing[KeyEvent.VK_DOWN] == false)
              {
                SceneGraph.getInstance().stepSize -= 1L;
                if (SceneGraph.getInstance().stepSize <= 1L)
                {
                  SceneGraph.getInstance().stepSize = 1L;
                }

                //keysPressing[KeyEvent.VK_DOWN] = true;
              }
            }
            else
            {
              SceneGraph.getInstance().step = 0;
            }
          }
        }
      }
    }
  }

  private void check_keyPressedScale()
  {
    Geom geom = MouseHandler.getInstance().selectedGeom;
    if (geom != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        geom.scaleX(-scaleInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        geom.scaleX(+scaleInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        geom.scaleY(-scaleInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        geom.scaleY(+scaleInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        //geom.scale.z-=scaleInc;
        geom.scaleY(-scaleInc);
        geom.scaleX(-scaleInc);
      }

      if (keys[KeyEvent.VK_PAGE_UP])
      {
        //geom.scale.z+=scaleInc;
        geom.scaleY(+scaleInc);
        geom.scaleX(+scaleInc);
      }

    }
  }

  private void check_keyPressedScaleAnchor()
  {
    Geom geom = MouseHandler.getInstance().selectedGeom;

    if (geom != null && geom.scaleAnchor != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        geom.scaleAnchorX(-scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        geom.scaleAnchorX(+scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        geom.scaleAnchorY(-scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        geom.scaleAnchorY(+scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        geom.scaleAnchorZ(-scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        geom.scaleAnchorZ(+scaleAnchorInc);
      }
    }
  }

  private void check_keyPressedRotateAnchor()
  {
    Geom geom = MouseHandler.getInstance().selectedGeom;

    if (geom != null && geom.rotateAnchor != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        geom.rotateAnchorX(-rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        geom.rotateAnchorX(+rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        geom.rotateAnchorY(-rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        geom.rotateAnchorY(+rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        geom.rotateAnchorZ(-rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        geom.rotateAnchorZ(+rotateAnchorInc);
      }

    }
  }
}

