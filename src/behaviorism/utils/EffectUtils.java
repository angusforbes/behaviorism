package behaviorism.utils;

//import behaviors.BehaviorIsDone;
import behaviorism.behaviors.geom.BehaviorActivateGeom;
import behaviorism.behaviors.geom.BehaviorColor;
import behaviorism.behaviors.geom.BehaviorRemoveGeom;
import behaviorism.behaviors.geom.BehaviorScale;
import behaviorism.geometry.Colorf;
import behaviorism.geometry.Geom;
import java.util.List;
import javax.vecmath.Point3f;

/**
 * EffectUtils is a simple effects library containing methods
 * in which multiple Behaviors are attached to a particular Geom
 * 
 * @author angus
 */
public class EffectUtils
{

  private static void activate(Geom g, long startNano)
  {
    BehaviorActivateGeom.activateAtNano(g, startNano);
  }

  private static void remove(Geom g, long startNano)
  {
    BehaviorRemoveGeom.removeAtNano(g, startNano);
  }

  private static void fadeIn(Geom g, long startNano, long lengthMS)
  {
    BehaviorColor.color(g, startNano, lengthMS,
      new Colorf(0f, 0f, 0f, 1f));
    g.setColor(g.color.r, g.color.g, g.color.b, 0f);
  }

  private static void fadeOut(Geom g, long startNano, long lengthMS)
  {
    BehaviorColor.color(g, startNano, lengthMS,
      new Colorf(0f, 0f, 0f, -1f));
  }

  private static void zoomIn(Geom g, long startNano, long lengthMS)
  {
    g.scale.x = 0f;
    g.scale.y = 0f;
    g.scale.z = 0f;

    BehaviorScale.scale(g, startNano, lengthMS,
      new Point3f(1f, 1f, 0f));
  }

  private static void zoomOut(Geom g, long startNano, long lengthMS)
  {
    BehaviorScale.scale(g, startNano, lengthMS,
      new Point3f(-1f, -1f, 0f));
  }

  public static void effectFadeAndZoomIn(Geom g, long startNano, long lengthMS, boolean includeChildren)
  {
    fadeIn(g, startNano, lengthMS);
    zoomIn(g, startNano, lengthMS);
    activate(g, startNano);

    if (includeChildren == true)
    {
      for (Geom cg : g.geoms)
      {
        effectFadeAndZoomIn(cg, startNano, lengthMS, includeChildren);
      }
    }
  }

  public static void effectZoomIn(Geom g, long startNano, long lengthMS, boolean includeChildren)
  {
    zoomIn(g, startNano, lengthMS);
    activate(g, startNano);

    if (includeChildren == true)
    {
      for (Geom cg : g.geoms)
      {
        effectZoomIn(cg, startNano, lengthMS, includeChildren);
      }
    }

  }

  public static void effectFadeAndZoomOut(Geom g, long startNano, long lengthMS, boolean includeChildren)
  {
    fadeOut(g, startNano, lengthMS);
    zoomOut(g, startNano, lengthMS);
    remove(g, Utils.nanoPlusMillis(startNano, lengthMS));

    if (includeChildren == true)
    {
      for (Geom cg : g.geoms)
      {
        effectFadeAndZoomOut(cg, startNano, lengthMS, includeChildren);
      }
    }
  }

  public static void effectZoomOut(Geom g, long startNano, long lengthMS, boolean includeChildren)
  {
    zoomOut(g, startNano, lengthMS);
    remove(g, Utils.nanoPlusMillis(startNano, lengthMS));

    if (includeChildren == true)
    {
      for (Geom cg : g.geoms)
      {
        effectZoomOut(cg, startNano, lengthMS, includeChildren);
      }
    }
  }

  public static void effectFadeOut(Geom g, long startNano, long lengthMS, boolean includeChildren)
  {
    fadeOut(g, startNano, lengthMS);
    remove(g, Utils.nanoPlusMillis(startNano, lengthMS));

    if (includeChildren == true)
    {
      for (Geom cg : g.geoms)
      {
        effectFadeOut(cg, startNano, lengthMS, includeChildren);
      }
    }
  }

  public static void effectFadeIn(Geom g, long startNano, long lengthMS, boolean includeChildren)
  {

    fadeIn(g, startNano, lengthMS);
    activate(g, startNano);

    if (includeChildren == true)
    {
      for (Geom cg : g.geoms)
      {
        effectFadeIn(cg, startNano, lengthMS, includeChildren);
      }
    }
  }

  public static void effectFallDown(List<Geom> geomList, long startNano, long lengthMS)
  {
    /*
    GeomUtils.sortGeomsByDistanceToLine(geomList,
    new Point3f(-10f, -10f, 0f), new Point3f(+10f, -10f, 0f),
    1);

    long incSpeed = 2000L / geomList.size();
    long incTime = lengthMS / geomList.size();
    int idx = 0;

    for (Geom g : geomList)
    {
    BehaviorTranslate.translate(g, Utils.nanoPlusMillis(startNano, incTime * idx), 2100L - (incSpeed * idx),
    new Point3f(0f, -10f - g.translate.y, 0f));

    BehaviorIsDone.destroyAtMillis(g, Utils.nanoPlusMillis(startNano, (incTime * idx) + 2100L - (incSpeed * idx)), 0, true);
    idx++;
    }
     */
  }
}


 