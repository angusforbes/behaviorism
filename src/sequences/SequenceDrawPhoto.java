
package sequences;

import data.FlickrData;
import geometry.Geom;
import geometry.GeomRect;
import java.util.ArrayList;
import java.util.List;
import utils.Utils;
import worlds.World;

/**
 *
 * @author angus
 */
public class SequenceDrawPhoto extends Sequence
{

	FlickrData currentPhoto = null; //getRecentOrRandomPhotoFromUser(flickr);
	FlickrData prevPhoto = null;
	Geom prevGeomImage = null;
	
	public SequenceDrawPhoto(World world, long baseNano,
					FlickrData currentPhoto, FlickrData prevPhoto, 
					Geom prevGeomImage)
	{
		super(world, baseNano);

		this.currentPhoto = currentPhoto;
		this.prevPhoto = prevPhoto;
		this.prevGeomImage = prevGeomImage;
	}

	@Override
	public void execute()
	{
		System.out.println("execute!");
		GeomRect gr = new GeomRect(0f, 0f, 0f, 1f, 1f);
		gr.setColor(1f, 0f, 0f, 1f);
		
		List<Long> timesMSs = new ArrayList<Long>();
		Utils.addTo(timesMSs, 1000L, 1800L, 2000L );
		
		//this.world.addGeom(gr, this.baseNano, timesMSs, LoopEnum.LOOP);
		this.world.addGeom(gr, true);

		GeomRect gr1 = new GeomRect(-3f, 0f, -1f, .1f, .1f);
		gr1.setColor(1f, 0f, 0f, 1f);
		this.world.addGeom(gr1, true);
		GeomRect gr2 = new GeomRect(0f, 0f, 0f, .1f, .1f);
		gr2.setColor(1f, 0f, 0f, 1f);
		this.world.addGeom(gr2, true);
		
		//BehaviorIsActive bia = new BehaviorIsActive(this.baseNano, true, timesMSs, LoopEnum.LOOP);
		//gr.attachBehavior(bia);
 
    /*
			BehaviorGeom bl3D = new BehaviorScale(Utils.nanoPlusMillis(this.baseNano, 0), 3000,
							LoopEnum.REVERSE,
							1f, 1f, 0f,
							0f, 0f, 0f
							);

			gr.attachBehavior(bl3D);			

					BehaviorRGBA brgba = new BehaviorRGBA(Utils.nanoPlusMillis(this.baseNano, 0), 3000,
							LoopEnum.REVERSE,
							1f, 0f, 0f, 1f,
							.2f, 0f, 0f, 1f,
							0f);

			//gr.attachBehavior(brgba);			

			//System.out.println("about to sleep...");
			//Utils.sleepUntil(Utils.nanoPlusMillis(this.baseNano, 2500));
			//System.out.println("awake!");
			//bl3D.reverse();
		Sequence s = new SequenceDrawPhoto(this.world, Utils.nowPlusMillis(6000L),
						null, null, null);
		*/
	}
	
}
