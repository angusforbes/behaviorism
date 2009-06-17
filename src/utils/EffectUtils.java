package utils;

import behaviors.Behavior;
//import behaviors.BehaviorIsDone;
import behaviors.geom.continuous.BehaviorRGBA;
import behaviors.geom.continuous.BehaviorScale;
import behaviors.geom.continuous.BehaviorTranslate;
import behaviors.geom.discrete.BehaviorIsActive;
import behaviors.geom.discrete.BehaviorIsDone;
import geometry.Colorf;
import geometry.Geom;
import java.util.List;
import javax.vecmath.Point3f;

/**
 * EffectUtils is a simple effects library containing methods
 * in which multiple Behaviors are attached to a particular Geom
 * 
 * @author angus
 */
public class EffectUtils {



    public static void effectFadeAndZoomIn(Geom g, long startNano, long lengthMS, boolean includeChildren) {
        Behavior b1 = BehaviorScale.scale(startNano, lengthMS,
                new Point3f((float) g.scale.x * 1f, (float) g.scale.y * 1f, 0f));

        g.attachBehavior(b1);


        Behavior b2 = BehaviorRGBA.colorChange(startNano, lengthMS,
                new Colorf(g.r, g.g, g.b, g.a));

        g.attachBehavior(b2);
        g.setColor(0f, 0f, 0f, 0f);

        g.scale.x = 0f;
        g.scale.y = 0f;
        g.scale.z = 0f;

        //BehaviorGeom b2 = new BehaviorIsActive(startNano, 0L); //simple
        //g.attachBehavior(b2);
        BehaviorIsActive.activateAtNano(g, startNano);

        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectFadeAndZoomIn(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

    public static void effectZoomIn(Geom g, long startNano, long lengthMS, boolean includeChildren) {
        Behavior b = BehaviorScale.scale(startNano, lengthMS,
                new Point3f((float) g.scale.x * 1f, (float) g.scale.y * 1f, 0f));

        g.attachBehavior(b);

        g.scale.x = 0f;
        g.scale.y = 0f;
        g.scale.z = 0f;

        //BehaviorGeom b2 = new BehaviorIsActive(startNano, 0L); //simple
        //g.attachBehavior(b2);
        BehaviorIsActive.activateAtNano(g, startNano);

        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectZoomIn(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

        public static void effectFadeAndZoomOut(Geom g, long startNano, long lengthMS, boolean includeChildren) {
        //Behavior b = BehaviorScale.scale(startNano, lengthMS,
        //        new Point3f((float) -g.scale.x, (float) -g.scale.y, 0f));
        Behavior b1 = BehaviorScale.scale(startNano, lengthMS,
                new Point3f((float)-1f, -1f, 0f));
        g.attachBehavior(b1);

        Behavior b2 = BehaviorRGBA.colorChange(startNano, lengthMS,
                Colorf.distance(g.r, g.g, g.b, g.a,
                0f, 0f, 0f, 0f));
        g.attachBehavior(b2);


        //BehaviorIsActive.activateAtNano(g, startNano);

        //BehaviorIsDone.destroyAtMillis(g, Utils.nanoPlusMillis(startNano, lengthMS), 0, false);

        //BehaviorGeom b2 = new BehaviorIsDone(startNano, lengthMS);
        //g.attachBehavior(b2);

        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectFadeAndZoomOut(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

    public static void effectZoomOut(Geom g, long startNano, long lengthMS, boolean includeChildren) {
        //Behavior b = BehaviorScale.scale(startNano, lengthMS,
        //        new Point3f((float) -g.scale.x, (float) -g.scale.y, 0f));
        Behavior b = BehaviorScale.scale(startNano, lengthMS,
                new Point3f((float)-1f, -1f, 0f));
        g.attachBehavior(b);


        //BehaviorIsActive.activateAtNano(g, startNano);

        //BehaviorIsDone.destroyAtMillis(g, Utils.nanoPlusMillis(startNano, lengthMS), 0, false);

        //BehaviorGeom b2 = new BehaviorIsDone(startNano, lengthMS);
        //g.attachBehavior(b2);

        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectZoomOut(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

    public static void effectFadeOut(Geom g, long startNano, long lengthMS, boolean includeChildren) {
        Behavior b = BehaviorRGBA.colorChange(startNano, lengthMS,
                Colorf.distance(g.r, g.g, g.b, g.a,
                0f, 0f, 0f, 0f));
        g.attachBehavior(b);


        //BehaviorIsDone.destroyAtMillis(g, Utils.nanoPlusMillis(startNano, lengthMS), 0, false);

        //BehaviorGeom b2 = new BehaviorIsDone(startNano, lengthMS);
        //g.attachBehavior(b2);
        //BehaviorIsActive.activateAtNano(g, startNano);


        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectFadeOut(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

    public static void effectFadeIn(Geom g, long startNano, long lengthMS, boolean includeChildren) {

        Behavior b = BehaviorRGBA.colorChange(startNano, lengthMS,
                new Colorf(g.r, g.g, g.b, g.a));

        g.attachBehavior(b);
        g.setColor(0f, 0f, 0f, 0f);


        //BehaviorGeom b2 = new BehaviorIsActive(startNano, 0L); //simple 
        //g.attachBehavior(b2);

        BehaviorIsActive.activateAtNano(g, startNano);

        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectFadeIn(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

     public static void effectFadeIn(Geom g, long startNano, long lengthMS, 
             Colorf color, boolean includeChildren) {

        Behavior b = BehaviorRGBA.colorChange(startNano, lengthMS,
                new Colorf(color.r, color.g, color.b, color.a));

        g.attachBehavior(b);
        g.setColor(0f, 0f, 0f, 0f);


        //BehaviorGeom b2 = new BehaviorIsActive(startNano, 0L); //simple 
        //g.attachBehavior(b2);

        //BehaviorIsActive.activateAtNano(g, startNano);

        if (includeChildren == true) {
            for (Geom cg : g.geoms) {
                effectFadeIn(cg, startNano, lengthMS, includeChildren);
            }
        }
    }

  
    public static void effectFallDown(List<Geom> geomList, long startNano, long lengthMS) {
        GeomUtils.sortGeomsByDistanceToLine(geomList,
                new Point3f(-10f, -10f, 0f), new Point3f(+10f, -10f, 0f),
                1);

        long incSpeed = 2000L / geomList.size();
        long incTime = lengthMS / geomList.size();
        int idx = 0;

        for (Geom g : geomList) {
            BehaviorTranslate.translate(g, Utils.nanoPlusMillis(startNano, incTime * idx), 2100L - (incSpeed * idx),
                    new Point3f(0f, -10f - g.anchor.y, 0f));

            BehaviorIsDone.destroyAtMillis(g, Utils.nanoPlusMillis(startNano, (incTime * idx) + 2100L - (incSpeed * idx)), 0, true);
            idx++;
        }
    }
}


 