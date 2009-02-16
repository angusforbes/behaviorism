/* CommentData.java (created on January 31, 2008, 3:55 PM) */

package data;

import geometry.Colorf;
import geometry.Geom;
import geometry.text.GeomTextFlow;

public class CommentData extends Data
{
  public String author = "";
  public String comment = "";
  public CommentData(String author, String comment)
  {
    this.author = author;
		this.comment = comment;
    this.dataType = DataEnum.COMMENT;
  }

  
	public Geom makeShape()
  {
    String text = "" + author + " writes \"" + comment + "\"";
    //GeomText2 gt2 = new GeomText2(0f, 0f, 0f, 1.5f, .3f, text);
    GeomTextFlow gt2 = new GeomTextFlow(0f, 0f, 0f, 1.5f, 1.5f, text);
    //gt2.backgroundColor = new Colorf(); 
    gt2.setColor(1f,1f,1f,1f);
    return gt2;
  }
  
  public String toString()
  {
    return "" + author + ": \"" + comment + "\"";
  }
}
