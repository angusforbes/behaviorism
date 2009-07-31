/*
 * KeyboardHandler.java
 * Created on March 12, 2007, 4:56 PM
 */
package handlers;

import behaviorism.Behaviorism;
import renderers.SceneGraph;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import renderers.Renderer;
import renderers.cameras.Cam;
import utils.RenderUtils;

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
  
  public float rotationInc = 1f;
  public float rotateAnchorInc = .0125f;
  public float scaleInc = .05f;
  public float scaleAnchorInc = .0125f;
  public float translateInc = .025f;

  public enum ModeEnum
  {

    MODE_ZOOM, MODE_ROTATE, MODE_SCALE,
    MODE_ROTATE_ANCHOR, MODE_SCALE_ANCHOR,
    MODE_FONT, MODE_PACK, MODE_DEBUG, MODE_CAM, MODE_STEP
  }

  public ModeEnum g_keyMode = ModeEnum.MODE_ZOOM;
  private static KeyboardHandler instance = null;

  /**
   * Gets (or creates then gets) the singleton MouseHandler.getInstance() object.
   * @return the singleton MouseHandler.getInstance()
   */
  public static KeyboardHandler getInstance()
  {
    if (instance == null)
    {
      instance = new KeyboardHandler();
    }

    return instance;
  }

  private KeyboardHandler()
  {
  }

  /* This method is called from the openGL Renderer display loop */
  public /*static*/ void processKeyboard()
  {
    checkKeys();
  }

  @Override
  public void keyPressed(KeyEvent evt)
  {
    if (evt.getKeyCode() > keys.length)
    {
      return;
    }

    System.out.println("pressed : " + KeyEvent.getKeyText(evt.getKeyCode()));
    keys[evt.getKeyCode()] = true;
  }

  @Override
  public void keyTyped(KeyEvent evt)
  {
  }

  @Override
  public void keyReleased(KeyEvent evt)
  {
    if (evt.getKeyCode() > keys.length)
    {
      return;
    }
    keys[evt.getKeyCode()] = false;
    keysPressing[evt.getKeyCode()] = false;
  }

  public /*static*/ void setMode()
  {
    if (keys[KeyEvent.VK_F1])
    {
      g_keyMode = ModeEnum.MODE_ZOOM;
    }
    else if (keys[KeyEvent.VK_F2])
    {
      g_keyMode = ModeEnum.MODE_ROTATE;
    }
    else if (keys[KeyEvent.VK_F3])
    {
      g_keyMode = ModeEnum.MODE_SCALE;
    }
    else if (keys[KeyEvent.VK_F4])
    {
      g_keyMode = ModeEnum.MODE_PACK;
    }
    else if (keys[KeyEvent.VK_F5])
    {
      g_keyMode = ModeEnum.MODE_DEBUG;
    }
    else if (keys[KeyEvent.VK_F6])
    {
      g_keyMode = ModeEnum.MODE_CAM;
    }
    else if (keys[KeyEvent.VK_F7])
    {
      g_keyMode = ModeEnum.MODE_ROTATE_ANCHOR;
    }
    else if (keys[KeyEvent.VK_F8])
    {
//g_keyMode = ModeEnum.MODE_SCALE_ANCHOR;
      g_keyMode = ModeEnum.MODE_STEP;
    }
    else if (keys[KeyEvent.VK_F9])
    {
      g_keyMode = ModeEnum.MODE_FONT;

    }


  }

  /**
   * This method checks the following types of keyboard events in the following order:
   * 1) Global keys (ie, quit, reset, pause, change mode) -- there aren't too many of these
   * 2) World-specific keys (optionally defined in each world by overriding checkKeys(boolean[] keys, boolean[] keysPressed)
   * 3) Mode-specific keys, which are keys that are only checked if we are in a particular mode
   * (usually by pressing the F# function keys).
   */
  public /*static*/ void checkKeys()
  {

   // System.out.println("in checkKeys...");
    //check global keys. if any global keys were pressed, return so that we don't try to
    //apply that key to a world-specific or mode-specifc action.
//    if (keys[KeyEvent.VK_C])
//    {
//      System.err.println("********* RESET **********");
//      RenderUtils.getWorld().reset();
//      return;
//    }
    if (keys[KeyEvent.VK_ESCAPE])
    {
      Behaviorism.getInstance().shutDown();
      //RendererJogl.animator.stop();
      //System.exit(0);
      return;
    }
    if (keys[KeyEvent.VK_SPACE])
    {
      if (keysPressing[KeyEvent.VK_SPACE] == false)
      {
        SceneGraph.getInstance().isStepping = !SceneGraph.getInstance().isStepping;

        g_keyMode = ModeEnum.MODE_STEP;

        //BehaviorismDriver.renderer.currentWorld.isPaused = !BehaviorismDriver.renderer.currentWorld.isPaused;
        keysPressing[KeyEvent.VK_SPACE] = true;
      }
      return;
    }

    if (keys[KeyEvent.VK_C])
    {
      if (keysPressing[KeyEvent.VK_C] == false)
      {
        keysPressing[KeyEvent.VK_C] = true;
        Behaviorism.getInstance().toggleCursor();
      }
      return;
    }
    if (keys[KeyEvent.VK_F])
    {
      if (keysPressing[KeyEvent.VK_F] == false)
      {
        keysPressing[KeyEvent.VK_F] = true;
        Renderer.getInstance().togglingFullScreen.set(true);
      }
      return;
    }

    if (keys[KeyEvent.VK_R])
    {
      if (keysPressing[KeyEvent.VK_R] == false)
      {
        SceneGraph.getInstance().reverse = true;
        keysPressing[KeyEvent.VK_R] = true;
      }
      return;
    }

    //check world-specific keys. if any world-specific keys were pressed, return so that we don't try to
    //apply that key to a mode-specific action.
    if (RenderUtils.getWorld() != null)
    {
      if (RenderUtils.getWorld().checkKeys(keys, keysPressing))
      {
        return;
      }
    }

    //check if a new mode was selected
    setMode();

    //check mode-specific keys
    switch (g_keyMode)
    {
      case MODE_ZOOM:
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

  //public /*static*/ boolean prev_keys[] = new boolean[1024];
  //public /*static*/ boolean clicking;
  //public /*static*/ boolean dragging;
  public /*static*/ void check_keyPressedFont()
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

  public /*static*/ void check_keyPressedCam()
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

  public /*static*/ void check_keyPressedDebug()
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

  public /*static*/ void check_keyPressedTranslate()
  {
    //float inc = .00125f;
    //float inc = .005f;

    if (MouseHandler.getInstance().getInstance().selectedGeom == null)
    {
      /*if(keys[KeyEvent.VK_LEFT])
      BehaviorismDriver.xzoom+= inc;

      if(keys[KeyEvent.VK_RIGHT])
      BehaviorismDriver.xzoom-= inc;

      if(keys[KeyEvent.VK_DOWN])
      BehaviorismDriver.yzoom+= inc;

      if(keys[KeyEvent.VK_UP])
      BehaviorismDriver.yzoom-= inc;

      if(keys[KeyEvent.VK_PAGE_DOWN])
      BehaviorismDriver.zzoom-= inc * 8f;

      if(keys[KeyEvent.VK_PAGE_UP])
      BehaviorismDriver.zzoom+= inc * 8f;*/
    }
    else
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        MouseHandler.getInstance().selectedGeom.translateX(-translateInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        MouseHandler.getInstance().selectedGeom.translateX(+translateInc);
      //MouseHandler.getInstance().selectedGeom.translate.x += translateInc;
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.translateY(-translateInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        MouseHandler.getInstance().selectedGeom.translateY(+translateInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.translateZ(-translateInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        MouseHandler.getInstance().selectedGeom.translateZ(+translateInc);
      }

    }
  }

  public /*static*/ void check_keyPressedRotate()
  {
    System.out.println("in check_keyPressedRotate...");
    if (MouseHandler.getInstance().selectedGeom == null)
    {
      System.out.println("selectedGeom = null!");
      /*if(keys[KeyEvent.VK_LEFT])
      BehaviorismDriver.zrot-=rotationInc;

      if(keys[KeyEvent.VK_RIGHT])
      BehaviorismDriver.zrot+=rotationInc;

      if(keys[KeyEvent.VK_DOWN])
      BehaviorismDriver.yrot-=rotationInc;

      if(keys[KeyEvent.VK_UP])
      BehaviorismDriver.yrot+=rotationInc;

      if(keys[KeyEvent.VK_PAGE_DOWN])
      BehaviorismDriver.xrot-=rotationInc;

      if(keys[KeyEvent.VK_PAGE_UP])
      BehaviorismDriver.xrot+=rotationInc;*/
    }
    else
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        MouseHandler.getInstance().selectedGeom.rotateY(-rotationInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        System.out.println("rotating right...");
        MouseHandler.getInstance().selectedGeom.rotateY(+rotationInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.rotateX(+rotationInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        MouseHandler.getInstance().selectedGeom.rotateX(-rotationInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.rotateZ(-rotationInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        MouseHandler.getInstance().selectedGeom.rotateZ(+rotationInc);
      }
    }

  }

  public /*static*/ void check_keyPressedStep()
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

  public /*static*/ void check_keyPressedScale()
  {
    if (MouseHandler.getInstance().selectedGeom != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        MouseHandler.getInstance().selectedGeom.scaleX(-scaleInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        MouseHandler.getInstance().selectedGeom.scaleX(+scaleInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.scaleY(-scaleInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        MouseHandler.getInstance().selectedGeom.scaleY(+scaleInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        //MouseHandler.getInstance().selectedGeom.scale.z-=scaleInc;
        MouseHandler.getInstance().selectedGeom.scaleY(-scaleInc);
        MouseHandler.getInstance().selectedGeom.scaleX(-scaleInc);
      }

      if (keys[KeyEvent.VK_PAGE_UP])
      {
        //MouseHandler.getInstance().selectedGeom.scale.z+=scaleInc;
        MouseHandler.getInstance().selectedGeom.scaleY(+scaleInc);
        MouseHandler.getInstance().selectedGeom.scaleX(+scaleInc);
      }

    }
  }

  public /*static*/ void check_keyPressedScaleAnchor()
  {
    if (MouseHandler.getInstance().selectedGeom != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        MouseHandler.getInstance().selectedGeom.scaleAnchorX(-scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        MouseHandler.getInstance().selectedGeom.scaleAnchorX(+scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.scaleAnchorY(-scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        MouseHandler.getInstance().selectedGeom.scaleAnchorY(+scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.scaleAnchorZ(-scaleAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        MouseHandler.getInstance().selectedGeom.scaleAnchorZ(+scaleAnchorInc);
      }
    }
  }

  public /*static*/ void check_keyPressedRotateAnchor()
  {
    if (MouseHandler.getInstance().selectedGeom != null &&
      MouseHandler.getInstance().selectedGeom.rotateAnchor != null)
    {
      if (keys[KeyEvent.VK_LEFT])
      {
        MouseHandler.getInstance().selectedGeom.rotateAnchorX(-rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_RIGHT])
      {
        MouseHandler.getInstance().selectedGeom.rotateAnchorX(+rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.rotateAnchorY(-rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_UP])
      {
        MouseHandler.getInstance().selectedGeom.rotateAnchorY(+rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_DOWN])
      {
        MouseHandler.getInstance().selectedGeom.rotateAnchorZ(-rotateAnchorInc);
      }
      if (keys[KeyEvent.VK_PAGE_UP])
      {
        MouseHandler.getInstance().selectedGeom.rotateAnchorZ(+rotateAnchorInc);
      }
    /*
    if (keys[KeyEvent.VK_LEFT])
    {
    MouseHandler.getInstance().selectedGeom.rotateAnchor.translate.x -= rotateAnchorInc;
    }
    if (keys[KeyEvent.VK_RIGHT])
    {
    MouseHandler.getInstance().selectedGeom.rotateAnchor.translate.x += rotateAnchorInc;
    }
    if (keys[KeyEvent.VK_DOWN])
    {
    MouseHandler.getInstance().selectedGeom.rotateAnchor.translate.y -= rotateAnchorInc;
    }
    if (keys[KeyEvent.VK_UP])
    {
    MouseHandler.getInstance().selectedGeom.rotateAnchor.translate.y += rotateAnchorInc;
    }
    if (keys[KeyEvent.VK_PAGE_DOWN])
    {
    MouseHandler.getInstance().selectedGeom.rotateAnchor.translate.z -= rotateAnchorInc;
    }
    if (keys[KeyEvent.VK_PAGE_UP])
    {
    MouseHandler.getInstance().selectedGeom.rotateAnchor.translate.z += rotateAnchorInc;
    }
     */
    }
  }
}

