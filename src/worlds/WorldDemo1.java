/* WorldDemo1.java (created on November 14, 2007, 12:47 PM) */
package worlds;

import behaviors.Behavior;
import behaviors.geom.continuous.BehaviorTranslate;
import behaviors.geom.continuous.BehaviorRGBA;
import geometry.BorderEnum;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomCircle;
import geometry.GeomEllipse;
import geometry.GeomRect;
import geometry.media.GeomRectVideoFobs;
import geometry.text.GeomText2;
import geometry.text.GeomTextFlow;
import geometry.GeomVertexBufferObject;
import geometry.RotateEnum;
import geometry.media.GeomVideo;
import renderers.State;
import java.net.MalformedURLException;
import java.net.URL;
import javax.vecmath.Point3f;
import handlers.YouTubeHandler;
import handlers.FontHandler;
import java.awt.event.KeyEvent;
import renderers.cameras.CamBasic;
import textures.TextureVideo;
import utils.FileUtils;
import utils.Utils;

//TODO: delete this line as well!
public class WorldDemo1 extends World {

   private YouTubeHandler youTubeHandler = null;

   public WorldDemo1() {
   }

   @Override
   public void setUpWorld() {
      //set default camera
      setCamera(new CamBasic(-1f, 0f, -5f));

//      testGeomVertexArray();

      /*
      BehaviorGeom startBehavior = new BehaviorRotate(Utils.nowPlusMillis(50L), 8000L,
      LoopEnum.REVERSE,
      0f, 0f, 0f,
      0f);
      
      cam.attachBehavior(startBehavior);
      
      BehaviorGeom startBehavior2 = new BehaviorRotate(Utils.nowPlusMillis(50L), 900L,
      LoopEnum.REVERSE,
      0f, 0f, 180f,
      0f);
      //cam.attachBehavior(startBehavior2);
       */

      //System.out.println("d = " + d);
      //if (1 == 1) System.exit(0);


      //this.state = new State();
      //this.state.BLEND = true;
      //this.state.DEPTH_TEST = false;
      //testDrawRect();
      //testOffsetCircles();
      //Utils.sleep(10000);
      //testLoadingCandidateVideosFromLocalRepository(126);
      //testLoadingCandidateVideosFromLocalRepositoryWithFakeVideoData();

      //testVideoLoading();
      //testRemoteLoadingOfVideos();
      //testGettingAngles();
      //testPointsInSpace();
      //testDataGenerics();

      //HERE!!!

      testGeomTexts();
   // testLinesBetweenParentAndChild();
   //buildWorldData();

   //just testing GeomTextFlow
   //testGeomTextFlow();

   //just testing image geoms!
   //testGeomImage();

   //testGeomEllipse();
   //testUnrotate();

   //testTimeStepper();
   //testFastAction();

   //GeomText2 gt2 = new GeomText2(0f, 0f, 0f, 1f, 1f, "hello");
   //addGeom(gt2, true);
   //GeomText2 gtf = new GeomText2(0f, 0f, 0f, 5f, 1f, "hello asldkjf aldkdj aldsdjfaslkfj adkdj alk alkdd ");
   //addGeom(gtf, true);

   //QuoteData qd = new QuoteData("me", "lajfla ladkjf lajdjsf lasj flaksj flaksd fj");
   //addGeom(qd.makeShape(), true);

   /*
   Geom gt2 = GeomText2.createGeomTextWithQuotes("hi there lakdjf aldsjf asdjf askdjf aksjhfh !");
   gt2.setColor( 1f, 1f, 1f, 1f );
   addGeom(gt2, true);
    */

      testJMC();
   }

   GeomRect vh = null;
   public void testJMC()
   {
     String mov3 = "file:/Users/angus/Projects/behaviorism/slave.flv";
     String mov1 = "file:/Users/angus/Projects/JMCTest/kung.mov";
     String mov2 = "file:/Users/angus/Projects/JMCTest/kfc.mov";
     //we need a more fully feature video geom player...
//     vh = (GeomRect) GeomRectVideoFobs.makeVideoHolder(new Point3f(), mov2, BorderEnum.RECTANGLE, .2f);
//     addGeom(vh);


     Geom g = new GeomVideo(new TextureVideo(FileUtils.toURI(mov1)));
     addGeom(g);
//     Geom g2 = new GeomVideo(mov2);
//     addGeom(g2);
   }

   public boolean checkKeys(boolean[] keys, boolean[] keysPressing)
   {
    if (keys[KeyEvent.VK_1])
    {
     System.out.println("in world checkKeys() : you pressed 1");

      if (keysPressing[KeyEvent.VK_1] == false)
      {
        if (vh != null)
        {
          //vh.testStuff();
        }
      }

      return true;
    }

    return false;
   }

   public void testGeomVertexArray() {
      state.BLEND = true;
      state.DEPTH_TEST = false;
      GeomVertexBufferObject gva = new GeomVertexBufferObject();
      addGeom(gva, true);

   }

   private void testFastAction() {
      GeomRect g1 = new GeomRect(1f, 0f, 0f, .1f, .1f);
      addGeom(g1, true);
      g1.setColor(1f, 1f, 0f, 1f);

      GeomRect g2 = new GeomRect(-4f, 0f, 0f, .1f, .1f);
      addGeom(g2, true);
      g2.setColor(1f, 1f, 0f, 1f);

      GeomRect gr = new GeomRect(1f, 0f, 0f, 1f, 1f);
      addGeom(gr, true);
      gr.setColor(1f, 0f, 0f, 1f);
      System.out.println(gr);

      long time = 800L;
      Behavior bl3D = BehaviorTranslate.translate(Utils.nowPlusMillis(500L), time,
              new Point3f(-3f, 0f, 0f));

      gr.attachBehavior(bl3D);

      time = 600L;
      Behavior bl3D2 = BehaviorTranslate.translate(Utils.nowPlusMillis(500L), time,
              new Point3f(-24f, 0f, 0f));
      gr.attachBehavior(bl3D2);

      time = 700L;
      Behavior bl3D3 = BehaviorTranslate.translate(Utils.nowPlusMillis(500L), time,
              new Point3f(+22f, 0f, 0f));
      gr.attachBehavior(bl3D3);

      Utils.sleep(800L + 1000L);
      System.out.println(gr);
   }

   private void testTimeStepper() {
      GeomRect g1 = new GeomRect(1f, 0f, 0f, .1f, .1f);
      addGeom(g1, true);
      g1.setColor(1f, 1f, 0f, 1f);

      GeomRect g2 = new GeomRect(-4f, 0f, 0f, .1f, .1f);
      addGeom(g2, true);
      g2.setColor(1f, 1f, 0f, 1f);

      GeomRect gr = new GeomRect(1f, 0f, 0f, 1f, 1f);
      addGeom(gr, true);
      gr.setColor(1f, 0f, 0f, 1f);

      Behavior bl3D = BehaviorTranslate.translate(Utils.nowPlusMillis(000L), 4000L,
              //LoopEnum.REVERSE,
              new Point3f(Utils.randomFloat(-5f, -5f), 0f, 0f));

      gr.attachBehavior(bl3D);


      Behavior bc = BehaviorRGBA.colorChange(Utils.nowPlusMillis(000L), 4000L,
        Colorf.distance(      
          gr.r, gr.g, gr.b, 1f,
          .2f, 0f, 0f, 1f)
          );

      gr.attachBehavior(bc);
   /*
   Utils.sleep(1000);
   long now = System.nanoTime();
   long sss = 0L;
   isPaused = true;
   for (int i = 0; i < 100; i++)
   {
   Utils.sleep(1000L);
   
   sss += 100L;
   bl3D.step(now, Utils.millisToNanos(sss));
   }
    */
   /*
   
   for (int i = 0; i < 1; i++)
   {
   Utils.sleep(1000L);
   //Utils.sleep(Utils.randomLong(500L, 3000L));
   bl3D.changeSpeed(Utils.randomFloat(.5f, 2.5f));
   //bl3D.changeSpeed(0f);
   //Utils.sleep(50);
   //bl3D.reverse();
   
   }
    */

   /*
   Utils.sleep(1500L);
   bl3D.changeSpeed(5f);
   
   
   Utils.sleep(1500L);
   bl3D.changeSpeed(.1f);
   Utils.sleep(1500L);
   bl3D.changeSpeed(.1f);
   //bl3D.reverse();
   //b.reverse();
    */
   }

  
   private void testGeomEllipse() {
      GeomEllipse ge = new GeomEllipse(0f, 0f, 0f, 2f, 1f, .05f);
      ge.determineRotateAnchor(RotateEnum.CENTER);
      addGeom(ge, true);
   }

   private void testGeomImage() {
      URL u = null;
      try {
         //u = new URL("file:data/photos/me.jpg");
         u = new URL("http://www.mat.ucsb.edu/~a.forbes/portfolio2007/disfarmer/disfarmer.png");
      } catch (MalformedURLException mue) {
         mue.printStackTrace();
      }

      if (u != null) {
         //Geom gi = new GeomImage(u,0f,0f,0f,1f,1f);
         Geom gi = new GeomText2(0f, 0f, 0f, 3f, 1f, "H", FontHandler.getInstance().textRenderers.get(
                 FontHandler.getInstance().textRenderers.size() - 1));
         Utils.sleep(1000);
         //addGeom(gi, true);
         Geom ggg = GeomRect.createBorderGeom(gi, BorderEnum.ELLIPSE, .15f, new Colorf(1f, 0f, 0f, 0f));
         //Geom ggg = GeomRect.createBorderGeom(gi, BorderEnum.CIRCLE, .15f, new Colorf(1f, 0f, 0f, 0f));
         //Geom ggg = GeomRect.createBorderGeom(gi, BorderEnum.RECTANGLE, .15f, new Colorf(1f, 0f, 0f, 0f));
         //ggg.setColor(0f, 1f, 0f, 1f);
         ggg.determineRotateAnchor(RotateEnum.CENTER);
         addGeom(ggg, true);

      }
   }

   private void testGeomTextFlow() {
      GeomTextFlow gtf = new GeomTextFlow(0f, 0f, 0f, 3f, 2f, "hello hello ajdf adkj aldfjakl 123 jj !! #&^$*&^");
      //GeomTextFlow gtf = GeomTextFlow.createGeomTextFlowWithQuotes("hello hello ajdf adkj aldfjakl 123 jj !! #&^$*&^" +
      //        " asldkjf lakdsjf alsjfd lajsds flaj faldskjfj aslskfj ");
      Geom ggg = GeomRect.createBorderGeom(gtf, BorderEnum.CIRCLE, .05f, new Colorf(1f, 0f, 0f, 0f));
      ggg.determineRotateAnchor(RotateEnum.CENTER);
      addGeom(ggg, true);
   }

   public void testOffsetCircles() {
      for (int i = 0; i < 2; i++) {
         Point3f rp = Utils.randomPoint3f(-1f, -1f, 1f, 1f);
         GeomCircle gc = new GeomCircle(rp,
                 0f, 1f,
                 10f, 270f, 64);
         gc.state = new State();
         gc.state.BLEND = true;

         gc.determineRotateAnchor(RotateEnum.CENTER);
         addGeom(gc, true);
         for (int ii = 0; ii < 2; ii++) {
            rp = Utils.randomPoint3f(-1f, -1f, 1f, 1f);
            GeomCircle gc2 = new GeomCircle(rp,
                    .2f, .3f,
                    0f, 360f, 64);

            gc2.determineRotateAnchor(RotateEnum.CENTER);

            gc.addGeom(gc2, true);
         }
      }
   }

   public void testVideoLoading() {
      try {
         //1. get information about video from database


         //String url1 = "file:data/DaddyCool.flv";
         //String url1 = "file:data/cats.flv";

         //String url1 = "file:data/1SdCg389bsQ.flv";
         //URL url1 = new URL("http://128.111.87.213/vid/gravel/videos/1SdCg389bsQ.flv");

         //URL url1 = new URL("http", "128.111.87.213", "/vid/gravel/videos/1SdCg389bsQ.flv");

         //URL url1 = new URL("http://mat.ucsb.edu/~a.forbes/test.flv");
         URL url1 = new URL("http://mat.ucsb.edu/~a.forbes/sheep1.mov");
         //URL url1 = new URL("http://movies.apple.com/movies/ifc_films/protagonist/protagonist-h.ref.mov");
         //URL url1 = new URL("http://images.apple.com/movies/paramount/1-18-08/1-18-08-tlr_h.320.mov");
         //URL url1 = new URL("http://images.apple.com/movies/paramount/1-18-08/1-18-08-tlr_h.320.mov");
         //String url1 = "file:data/trailer.mov";

         //System.out.println("file : " + url1.getFile());
         //URL url1 = new URL("http://www.openquicktime.org/files/sheep.mov");
//String url1 = "file:data/sheep0.mov";
         //String url1 = "file:data/arctcat.mpg";
         //String url1 = "file://data/youtube/hillaryclintondotcom4.flv";

         //File f = new File("data/cats.flv");
         //File f = new File("data/DaddyCool.flv");
         //File f = new File("data/arctcat.mpg");
         //URL url = f.toURL();
         //System.out.println("url = " + url);
         //GeomRectVideo grv1 = new GeomRectVideo(url);


         GeomRectVideoFobs grv1 = new GeomRectVideoFobs(url1);

         //grv1.load();
         addGeom(grv1);

         //Utils.sleep(3000);

         grv1.isActive = true;

      /*
      grv1.rotateAnchor = new GeomPoint(0f, 0f, 0f);
      grv1.attachBehavior(new BehaviorRotate(Utils.nowPlusMillis(1000L), 5000L,
      LoopEnum.REVERSE,
      0f, 0f, 360f,
      0f));
      grv1.attachBehavior(new BehaviorScale3D(Utils.nowPlusMillis(1000L), 5000L,
      LoopEnum.REVERSE,
      4f, 4f, 0f,
      0f));
       */
      /*
      Utils.sleep(1000);
      grv1.start();
      Utils.sleep(10000);
      grv1.stop();
      Utils.sleep(4000);
      grv1.start();
      Utils.sleep(2000);
      grv1.stop();
       */
      } catch (Exception e) {
         e.printStackTrace();
      }
   /*
   String url2 = "file://data/sheep1.mov";
   GeomRectVideo grv2 = new GeomRectVideo(url2);
   addGeom(grv2);
   grv2.isActive = true;
   //grv2.load();
   grv2.attachBehavior(new BehaviorTranslate(System.nanoTime(), 1000L, BehaviorTranslate.REVERSE, true, 0f, 0f, 0f, -.3f, 2f, 0f, 0f));
   
   
   String url3 = "file://data/heatdeath1.avi";
   GeomRectVideo grv3 = new GeomRectVideo(url3);
   addGeom(grv3);
   grv3.isActive = true;
   //grv3.load();
   grv3.attachBehavior(new BehaviorTranslate(System.nanoTime(), 1000L, BehaviorTranslate.REVERSE, true, 0f, 0f, 0f, 1f, 1f, 0f, 0f));
    */
   }
   public void testGeomTexts() 
   {
      GeomRect gr = new GeomRect(2f,0f,0f,1f,1f);
      addGeom(gr);
      GeomRect gr2 = new GeomRect(-2f,0f,0f,.1f,.1f);
      addGeom(gr2);
      BehaviorTranslate.translate(gr, System.nanoTime(), 1000L, new Point3f(-4f, 0f, 0f));
   }
   



  
   
}
