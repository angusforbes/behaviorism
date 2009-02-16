/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import geometry.GeomGrid;
import java.util.Map;
import javax.vecmath.Point3f;

/**
 *
 * @author gregoryshear
 */
public class LocalPoint {

   public String name;
   public float longitude;
   public float latitude;
   public Point3f pixel = new Point3f();
   public int index;
   public int indexX;
   public int indexY;
   
   float xDimension = 5;
   float yDimension = 2;

   public LocalPoint(float xDimension, float yDimension, float lat, float lng, int i, String n) {
      
			this.xDimension = xDimension;
			this.yDimension = yDimension;
			
			name = n;
      longitude = lng;
      latitude = lat;
      index = i;
      indexX = index % 13;
      indexY =  (index - indexX) / 13;

      pixel.x = (indexX + 1) * (xDimension / 15);
      pixel.y = this.yDimension - (indexY + 1) * (yDimension / 13);
      pixel.z = 0;

   }

   public LocalPoint(float lat, float lng, int i, String n) {
      name = n;
      longitude = lng;
      latitude = lat;
      index = i;
      indexX = index % 13;
      indexY =  (index - indexX) / 13;

      pixel.x = (indexX + 1) * (xDimension / 15);
      pixel.y = (indexY + 1) * (yDimension / 13);
      pixel.z = 0;

   }

	@Override public String toString()
	 {
			String s = "";
			s += "city : " + name + "\n";
			s += "coords: " + pixel + "\n";
			return s;
	 }
	 
   public float distanceFromYouToMe(float lng, float lat) {
      return (float) Math.sqrt(((lng - this.longitude) * (lng - this.longitude)) + ((lat - this.latitude) * (lat - this.longitude)));

   }

	 /**
		* Distorts a lng and lat to fit into a stretched out map described by a set of lngs and lats.
		* lngs and lats *must* be in sorted order from lowest to highest!
		* 
		* @param w
		* @param h
		* @param lng
		* @param lat
		* @param lngs
		* @param lats
		* @return a Point3f mapped to the distorted map
		*/
	 public static Point3f distortPix2(float w, float h, float lng, float lat, float[] lngs, float[] lats)
	 {
		 //make sure is within range described by lngs and lats
		 if (lng < lngs[0] && lng > lngs[lngs.length - 1])
		 {
			 System.err.println("in distortPix2() : error! bad longitude! ");
			 return null;
		 }
		 if (lat < lats[0] && lat > lats[lats.length - 1])
		 {
			 System.err.println("in distortPix2() : error! bad latitude! ");
			 return null;
		 }
		 
		 float uniformLng = (lngs[lngs.length - 1] - lngs[0]) / (lngs.length - 1);
		 float uniformLat = (lats[lats.length - 1] - lats[0]) / (lats.length - 1);
		 
		 //find row...
		 float newlng = lngs[0];
		 for (int i = 0; i < lngs.length - 1; i++)
		 {
			 if (lng >= lngs[i] && lng < lngs[i + 1])
			 {
					float cw = lngs[i + 1] - lngs[i];
				  float cp = lng - lngs[i];
					
					if (cp != 0f && cw != 0f)
					{
						float perc = cp / cw;
						newlng += uniformLng * perc;
					}
					
					break;
			 }
			 else
			 {
				 newlng += uniformLng; 
			 }
		 }
		 
		 //find row...
		 float newlat = lats[0];
		 for (int i = 0; i < lats.length - 1; i++)
		 {
			if (lat >= lats[i] && lat < lats[i + 1])
			 {
					float cw = lats[i + 1] - lats[i];
				  float cp = lat - lats[i];
					
					if (cp != 0 && cw != 0f)
					{
						float perc = cp / cw;
						newlat += uniformLat * perc;
					}
					break;
			 }
			 else
			 {
				 newlat += uniformLat; 
			 }
		 }
	
		 //now we have a new lng/lat
		 return GeomUtils.equiToPoint3f(lngs[0], lngs[lngs.length - 1], lats[0], lats[lats.length - 1],
						 w, h, newlng, newlat);
	 }


	 public static float[] makeLngs_northAmerica()
	 {
		 return new float[] {
			-180f, //boundary 
			-149.897568f, //anchorage
			-122.347276f, //seattle
			-118.245f, //los angeles
			//-117.163841f, //san diego
			-112.073821f, //phoenix
			-104.984722f, //denver
			-99.1307f, //mexico
			-87.624333f, //chicago
			-83.0475f, //detroit
			  -79.383333f, //toronto
			  -77.036667f, //wash dc
			-75.17f, //philadelphia
			-73.986941f, //new york
			-71.061667f, //boston
			-60f //boundary
			};
	 }

	 public static float[] makeLats_northAmerica()
	 {
		 return new float[] {
			15f, //boundary
			//-33.2627f, //santiago
			//-23.5433f, //sao paulo
			//4.6117f, //bogota	
			//19.4095f, //mexico city
			25.787676f, //miami
			34.054f, //los angeles
			38.895111f, //wash dc
			//39.953333f, //philadelpha
			40.756040f, //new york
			42.357778f, //boston
			47.508889f, //montreal
			50.508889f,
			//60.508889f, //montreal
			70f //boundary
		 };
	 }
	 
	 public static float[] makeLngs_southAmerica()
	 {
		 return new float[] {
			-90f, //boundary
			-33.2627f, //santiago
			-23.5433f, //sao paulo
			4.6117f, //bogota	
			19.4095f //mexico city //boundary
		 };
	 }
	 public static float[] makeLats_southAmerica()
	 {
		 return new float[] {
			-90f, //boundary
			-33.2627f, //santiago
			-23.5433f, //sao paulo
			4.6117f, //bogota	
			19.4095f //mexico city //boundary
		 };
	 }
	 
	  public static Point3f distortPix(float w, float h, float lng, float lat) {
      Point3f newPoint = new Point3f();
      BiMap<Integer, LocalPoint> localMap = new HashBiMap<Integer, LocalPoint>();

      LocalPoint city = new LocalPoint(w, h, 41.879535f, -87.624333f, 32, "chicago");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 47.620973f, -122.347276f, 13, "seattle");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 40.756040f, -73.986941f, 37, "new york");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 33.448263f, -112.073821f, 44, "phoenix");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 43.654670f, -70.262434f, 26, "portland");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 32.718834f, -117.163841f, 80, "san diego");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 37.775196f, -122.419204f, 39, "san francisco");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 61.1919f, -149.762097f, 0, "anchorage"); //top left
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 34.054f, -118.245f, 66, "los angeles");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 32.78f, -96.8f, 69, "dallas");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 39.739167f, -104.984722f,56, "denver");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 44.9801f, -93.251867f, 30, "minneapolis");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 42.3316f, -83.0475f, 33, "detroit");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 43.65f, -79.383333f, 21, "toronto");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 45.508889f, -73.554167f, 11, "montreal");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 42.357778f, -71.061667f, 25, "boston");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 39.953333f, -75.17f, 49, "philadelphia");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 38.895111f, -77.036667f, 61, "washington dc");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 25.787676f, -80.224145f, 86, "miami");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 19.4095f, -99.1307f, 96, "mexico city");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 19.4095f,-149.762097f,130,"bottom left corner");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 19.4095f,-71.061667f,142,"bottom right corner");
      localMap.put(city.index, city);
      city = new LocalPoint(w, h, 61.1919f,-71.061667f,12,"top right corner");
//      localMap.put(city.index, city);
//      city = new LocalPoint();
      

      LocalPoint closestCity = null;
      LocalPoint xCity = null;
      LocalPoint yCity = null;

      float d = 10000000f;

      for (Map.Entry<Integer, LocalPoint> me : localMap.entrySet()) {
         float temp = me.getValue().distanceFromYouToMe(lng, lat);
         if (temp < d) {
            d = temp;
            closestCity = me.getValue();
         }
      }

      if (lng - closestCity.longitude < 1 && lat - closestCity.latitude < 1) 
			{
         System.out.println("SAME CITY!! --------------");
				 //ie, don't need to interpolate, etc
		    return new Point3f(closestCity.pixel);
   
			
			}

      if (lng < closestCity.longitude && lat > closestCity.latitude) { //upper left
				System.out.println("upper left...");
				 int i = 1;
         int j = 0;
         while (xCity == null) {
            xCity = localMap.get((closestCity.indexY + j) * 13 + closestCity.indexX - i);
            if (i == closestCity.indexX) { //are we at the left edge?
               i = 0;
               j++;
            }
            i++;
         }
         i = 0;
         j = 1;
         while (yCity == null) {
            yCity = localMap.get((closestCity.indexY - j) * 13 + closestCity.indexX - i);
            if (j == closestCity.indexY) { //are we at the top edge?
               i++;
               j = 0;
            }
            j++;
         }
      } else if (lng > closestCity.longitude && lat > closestCity.latitude) { //upper right
				System.out.println("upper right...");
         int i = 1;
         int j = 0;
         while (xCity == null) 
				 {
						System.out.println("trying index : " + ((closestCity.indexY + j) * 13 + closestCity.indexX + i) );
            xCity = localMap.get((closestCity.indexY + j) * 13 + closestCity.indexX + i);
            if (i + closestCity.indexX == 13) { //are we at the right edge?
               System.out.println("at right edge...");
								i = 0;
               j++;
            }
            i++;
         }
         i = 0;
         j = 1;
         while (yCity == null) {
            yCity = localMap.get((closestCity.indexY - j) * 13 + closestCity.indexX + i);
            if (j == closestCity.indexY) { //are we at the top edge?
               i++;
               j = 0;
            }
            j++;
         }
      } else if (lng < closestCity.longitude && lat < closestCity.latitude) { //lower left
				System.out.println("lower left...");
         int i = 1;
         int j = 0;
         while (xCity == null) {
            xCity = localMap.get((closestCity.indexY + j) * 13 + closestCity.indexX - i);
            if (i == closestCity.indexX) { //are we at the left edge?
               i = 0;
               j++;
            }
            i++;
         }
         i = 0;
         j = 1;
         while (yCity == null) {
            yCity = localMap.get((closestCity.indexY + j) * 13 + closestCity.indexX - i);
            if (j + closestCity.indexY == 11) { //are we at the bottom edge?
               i++;
               j = 0;
            }
            j++;
         }
      } else if (lng > closestCity.longitude && lat < closestCity.latitude) { //lower right
				System.out.println("lower right...");
         int i = 1;
         int j = 0;
         while (xCity == null) {
            xCity = localMap.get((closestCity.indexY + j) * 13 + closestCity.indexX + i);
            if (i + closestCity.indexX == 13) { //are we at the right edge?
               i = 0;
               j++;
            }
            i++;
         }
         i = 0;
         j = 1;
         while (yCity == null) {
            yCity = localMap.get((closestCity.indexY + j) * 13 + closestCity.indexX + i);
            if (j + closestCity.indexY == 11) { //are we at the bottom edge?
               i++;
               j = 0;
            }
            j++;
         }
      }

			System.out.println("closestCity = " + closestCity);
			System.out.println("lng/lat = " + lng + "/" + lat);
			System.out.println("xCity = " + xCity);
      newPoint.x = lng * (closestCity.pixel.x - xCity.pixel.x) / (closestCity.longitude - xCity.longitude);
      //newPoint.y = h - (lat * (closestCity.pixel.y - xCity.pixel.y) / (closestCity.latitude - xCity.latitude));
      newPoint.y =  (lat * (closestCity.pixel.y - xCity.pixel.y) / (closestCity.latitude - xCity.latitude));
      newPoint.z = 0;
      
      return newPoint;
   }

}
