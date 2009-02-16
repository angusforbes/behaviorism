/* GreenData.java (created on February 18, 2008, 6:27 PM) */

package data;


import geometry.BorderEnum;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomRect;
import geometry.text.GeomText2;
import geometry.RotateEnum;
import java.util.List;

//hjhj
public class GreenData extends Data
{
  int num; 
  public GreenData(int num)
  {
    this.dataType = DataEnum.GREEN;
    this.num = num;
  }

  public static GreenData getGreenDataByNum(List<? extends Data> ids, int num)
  {
    for (Data id : ids)
    {
      if (id.dataType != DataEnum.GREEN)
      {
        return null;
      }
      
      if ( ((GreenData)id).num == num)
      {
        return (GreenData)id;
      }
    }
   
    return null;
  }

	public Geom makeShape(BorderEnum borderType)
	{
		if (borderType == null)
		{
			return makeShape();
		}
		
		Colorf green = new Colorf(0f, 1f, 0f, 1f);
 
		GeomText2 gt2 = (GeomText2) makeShape();
		gt2.setColor(1f, 1f, 1f, 1f);
		gt2.backgroundColor = null;
		Geom geom = null;
		
		switch(borderType)
		{
			case RECTANGLE:
				gt2.backgroundColor = new Colorf(0f,0f,0f,1f);
				geom = GeomRect.createBorderGeom(gt2, borderType, .1f, green);
				geom.determineRotateAnchor(RotateEnum.CENTER);
				//geom.setColor(0f,0f,0f,0f);	
				break;
			case ELLIPSE:
				geom = GeomRect.createBorderGeom(gt2, borderType, .1f, green);
				geom.determineRotateAnchor(RotateEnum.CENTER);
				geom.setColor(0f,0f,0f,0f);	
				break;
		}
		
	  return geom;
    	
	}
	
	public Geom makeShape()
  {
		Colorf green = new Colorf(0f, 1f, 0f, 1f);
    GeomText2 gt2 = new GeomText2(0f, 0f, 0f, .5f, .5f, "g" + num);
    gt2.backgroundColor = green;
		gt2.determineRotateAnchor(RotateEnum.CENTER);
    gt2.setColor(1f,1f,1f,1f);
    return gt2;
  }
  
	public String toString()
  {
    return "g" + num;
  }
}
