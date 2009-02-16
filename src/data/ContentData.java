/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import geometry.Colorf;
import geometry.Geom;
import geometry.GeomRect;
import geometry.text.GeomText2;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3f;
import renderers.State;

/**
 *
 * @author gregoryshear
 */
public class ContentData extends Data{
   
   public String content;
	 public int contentId = -1;
   public int totalHits = 0;
   
   public GeomRect contentGeom;
 
        
   
   public List<ProgramData> programList = new ArrayList();
   public Multiset<String> programs = new HashMultiset<String>();
	 public double percentOfAllContents = -1;
	 
	 /*
	 public ContentData(String content, int totalHits) 
	 {
		this.content = content;
		this.totalHits = totalHits;
   }
		*/

   
   public ContentData(int contentId, String content, int totalHits)
   {
     this.contentId = contentId;
     this.content = content;
     this.totalHits = totalHits;
   }
   

   
	 public ContentData(int contentId, String content) 
   {
     this.contentId = contentId;
     this.content = content;
   }
   
	 public ContentData(String cont) {
   content = cont;
   totalHits=0;
   }
   
   public void addProgramData(ProgramData programData)
   {
    programList.add(programData);
   }
   
   public void addProgram(String program)
   {
    programs.add(program);
   }
   public void addProgram (String prog, int c) {
      ProgramData pd = new ProgramData(prog, c);
      programList.add(pd);
      this.totalHits += c;
   }

   
  public Geom makeShape()
  {
    Colorf color1 = Colorf.newColorRGB(239, 238, 212 );
    Colorf color2 = Colorf.newColorRGB(93,93,92);   
    float middleInsetPerc = .8f;
    float bottomInsetPerc = .05f;
    
    
    
    //top
    GeomText2 top = GeomText2.newGeomTextConstrainedByHeight(content, 
            new Point3f(), .1f);
    top.state = new State();
    top.state.BLEND = false;
    top.setColor(1f, 1f,1f,1f);
    top.backgroundColor = new Colorf(0f, 0f, 0f, 1f);
    
    //middle 
    float insetMiddle = top.h * middleInsetPerc;
    GeomRect middle = new GeomRect(0f, 0f, 0f, top.w + (insetMiddle * 2f), top.h + (insetMiddle * 2f));
    middle.state = new State();
    middle.state.BLEND = false;
   
    middle.setColor(0f, 0f,0f,1f);
    top.anchor.x = insetMiddle;
    top.anchor.y = insetMiddle;
    middle.addGeom(top, false);
    
    //bottom 
    float insetBottom = middle.h * bottomInsetPerc;
    GeomRect bottom = new GeomRect(0f, 0f, 0f, middle.w + (insetBottom * 2f), middle.h + (insetBottom * 2f));
    bottom.state = new State();
    bottom.state.BLEND = true;
  
    bottom.setColor(1f, 1f,1f,1f);
    middle.anchor.x = insetBottom;
    middle.anchor.y = insetBottom;
    bottom.addGeom(middle, false);
   

    top.selectableObject = bottom;
    middle.selectableObject = bottom;
    /*
    gt2.state = new State();
    gt2.state.BLEND = false;
    
    gt2.backgroundColor = color2;  
    gt2.setColor(1,0,0,255);
    gt2.anchor.x = inset2 * .5f;
    gt2.anchor.y = inset2 * .5f;
    gt2.anchor.z = .005f;
    
    GeomRect gr2 = new GeomRect(0f, 0f, .005f, gt2.w + inset2, gt2.h + inset2);
    gr2.state = new State();
    gr2.state.BLEND = false;
    gr2.setColor(color2);
    gr2.anchor.x = (inset1 * .5f) ;
    gr2.anchor.y = (inset1 * .5f) ;
    gr2.addGeom(gt2, false);
    
    
    GeomRect gr1 = new GeomRect(0f, 0f, .005f, gr2.w + inset1, gr2.h + inset1);
    gr1.state = new State();
    gr1.state.BLEND = false;
    gr1.setColor(color1);
    gr1.addGeom(gr2, false);
    */
    return bottom;
  }
  

}
