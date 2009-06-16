/*
 * Colorf.java
 * Created on July 18, 2007, 1:20 PM
 */

package geometry;

import java.awt.Color;
import utils.*;

public class Colorf //extends Color
{
  public float r;
  public float g;
  public float b;
  public float a;

	public static Colorf newRandomColor(float alpha)
	{
		Colorf rc = new Colorf();
		rc.a = alpha;
		return rc;
	}
	
  public static Colorf newRandomColor(Colorf min, Colorf max)
  {
    return new Colorf(
      Utils.randomFloat(min.r, max.r), 
      Utils.randomFloat(min.g, max.g), 
      Utils.randomFloat(min.b, max.b), 
      Utils.randomFloat(min.a, max.a)
      );
  }

  public static Colorf newColorRGBString(String colorStr)
	{
    String[] colors = colorStr.split(",");
    if (colors.length == 3)
    {
      return Colorf.newColorRGB(
        Integer.parseInt(colors[0].trim()),
        Integer.parseInt(colors[1].trim()),
        Integer.parseInt(colors[2].trim())
        );
    }
    else if (colors.length == 4)
    {
      return Colorf.newColorRGB(
        Integer.parseInt(colors[0].trim()),
        Integer.parseInt(colors[1].trim()),
        Integer.parseInt(colors[2].trim()),
        Integer.parseInt(colors[3].trim())
        );

    }
    else if (colors.length == 1)
    {
      //if charAt 0 == #, then decode,
      //else assume it is a black and white val

      return Colorf.newColorRGB(
        Integer.parseInt(colors[0].trim())
        );
    }

    return null;
	}

  public static Colorf newColorRGB(int bw)
	{
		return new Colorf((float) bw / 255f, (float) bw / 255f , (float) bw / 255f, 1f);
	}

	public static Colorf newColorRGB(int red, int green, int blue)
	{
		return new Colorf((float) red / 255f, (float) green / 255f , (float) blue / 255f, 1f);
	}


	public static Colorf newColorRGB(int red, int green, int blue, int alpha)
	{
		return new Colorf((float) red / 255f, (float) green / 255f , (float) blue / 255f, (float) alpha / 255f);
	}

	public static Colorf newRandomColor(float min, float max, float alpha)
	{
		float red = Utils.randomFloat(min, max);
		float green = Utils.randomFloat(min, max);
		float blue = Utils.randomFloat(min, max);
		Colorf rc = new Colorf(red, green, blue, alpha);
		rc.a = alpha;
		return rc;
	}

	public static Colorf newRandomColor()
	{
		return new Colorf();
	}
	
	public static Colorf newRandomGrayscale(float min, float max, float alpha)
	{
		float val = Utils.randomFloat(min, max);
		Colorf rgs = new Colorf(val, val, val, alpha);
		return rgs;
	}
	
	public static Colorf newRandomGrayscale()
	{
		float val = Utils.randomFloat();
		Colorf rgs = new Colorf(val, val, val, Utils.randomFloat());
		return rgs;
	}

  public static Colorf invert(Colorf c)
  {
    return new Colorf(1f - c.r, 1f - c.g, 1f - c.b, c.a);
  }

  public Colorf invert()
  {
    r = 1f - r;
    g = 1f - g;
    b = 1f - b;

    return this;
  }

  public static Colorf desaturate(Colorf saturatedColor)
  {
    float gs = (saturatedColor.r + saturatedColor.g + saturatedColor.b) / 3f;
    return new Colorf(gs, gs, gs, saturatedColor.a);
  }

  public Colorf desaturate()
  {
    float gs = (r + g + b) / 3f;
    r = gs;
    g = gs;
    b = gs;
    return this;
  }

  public Colorf(Color javaColor)
  {
    this.r = javaColor.getRed() / 255f;
    this.g = javaColor.getGreen() / 255f;
    this.b = javaColor.getBlue() / 255f;
    this.a = javaColor.getAlpha() / 255f;
  }
  
  public Colorf(int r, int g, int b, int a)
  {
    this.r = r / 255f;
    this.g = g / 255f;
    this.b = b / 255f;
    this.a = a / 255f;
  }
  
  public Colorf(float r, float g, float b, float a)
  {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
  
  public Colorf(float r, float g, float b)
  {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = 1f;
  }

   public Colorf()
   {
    //super(r, g, b, 1f);
    this.r = Utils.randomFloat();
    this.g = Utils.randomFloat();
    this.b = Utils.randomFloat();
    this.a = Utils.randomFloat();
  }

	 public Colorf(Colorf color)
	 {
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	 }
	 /** 
		* Represent this color as a float array. Can be used to simplify a call to glColor4fv for example:
		* gl.glColor4fv(myColor.array(), 0);
		* @return a float array of size 4 to hold the r,g,b,a data represented by this object.
		*/
   public float[] array()
   {
    return new float[]{r, g, b, a};
   }

   //how to get from color1 to color2...
   /**
    * Determine the appropriate values to transform color1 into color2. 
    * For example, if color1 = (.2f, .7f, .5f, 1f) and color 2 = (.5f, .5f, .5f, 1f),
    * then then return color will be (.3f, -.2f, 0f, 0f). 
    * @param c1
    * @param c2
    * @return
    */
   public static Colorf distance(Colorf c1, Colorf c2)
   {
     float nr = c2.r - c1.r;
     float ng = c2.g - c1.g;
     float nb = c2.b - c1.b;
     float na = c2.a - c1.a;
    
     return new Colorf(nr, ng, nb, na);
   }

   public static Colorf distance(
     float r1, float g1, float b1, float a1,
     float r2, float g2, float b2, float a2)
   {
     float nr = r2 - r1;
     float ng = g2 - g1;
     float nb = b2 - b1;
     float na = a2 - a1;
    
     return new Colorf(nr, ng, nb, na);
   }

    public static String colorToHex(Colorf color)
    {
      String h0 = "#";
      String h1 = Integer.toHexString((int) (color.r * 255.f));
      if (h1.length() == 1)
      {
        h1 += "0";
      }
      //System.out.println("h1 = " + h1);
      String h2 = Integer.toHexString((int) (color.g * 255.f));
      if (h2.length() == 1)
      {
        h2 += "0";
      }
      //System.out.println("h2 = " + h2);
      
      String h3 = Integer.toHexString((int) (color.b * 255.f));
      if (h3.length() == 1)
      {
        h3 += "0";
      }
      //System.out.println("h3 = " + h3);
      
      return h0 + h1 + h2 + h3;
    }
    
    public static Colorf hexToColor(String h)
    {
      if (h.charAt(0) != '#')
      {
        h = "#" + h;
      }
      return new Colorf(Color.decode(h));
    }
      
	@Override public String toString()
	 {
		return "color = " 
						+ Utils.decimalFormat(this.r) + "/" 
						+ Utils.decimalFormat(this.a) + "/" 
						+ Utils.decimalFormat(this.b) + "/" 
						+ Utils.decimalFormat(this.a); 
	 }
}
