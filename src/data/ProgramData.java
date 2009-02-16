/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import geometry.GeomRect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 *
 * @author gregoryshear
 */
public class ProgramData extends Data
{
   
   public String program;
   public int programId = -1;
	
   public int totalHits;

   public List<ContentData> contentList = new ArrayList();
   public Multiset<String> contents = new HashMultiset<String>();
	
   public GeomRect programGeom;
   
   public ProgramData(int programId, String program, int totalHits)
   {
     this.programId = programId;
     this.program = program;
     this.totalHits = totalHits;
   }
     
   public void addContentData(ContentData contentData)
   {
    contentList.add(contentData);
   }
   
   public void addContent(String content)
   {
    contents.add(content);
   }
   public ProgramData(String program, int totalHits) 
	 {
		this.program = program;
		this.totalHits = totalHits;
   }

   public String toString()
   {
     return "ProgramData : " + program + " has " + totalHits + " hits.";
   }
   
  public static void sortByTotalHits(List<ProgramData> list)
  {
      Collections.sort(list, new Comparator<ProgramData>()
      {
        public int compare(ProgramData a, ProgramData b)
        {
          int int1 = a.totalHits;
          int int2 = b.totalHits;
          if (int1 > int2)
          {
            return -1;
          }
          else if (int1 < int2)
          {
            return 1;
          }
          else
          {
            return 0;
          }
        }
      });
  }

}
