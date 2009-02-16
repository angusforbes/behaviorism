/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gregoryshear
 */
public class PracticeData {

   float seedRadius = 0;
   public String practice;
   public int totalHits;
   String biggestProgram;
   public int biggestProgramCounter = 0;
   public List<ProgramData> programList = new ArrayList<ProgramData>();
   public Multiset<String> programs = new HashMultiset<String>();
   public double percentOfAllPractices = -1;

   /*
   public PracticeData(String practice, int totalHits)
   {
   this.practice = practice;
   this.totalHits= totalHits;
   }
    */
   public PracticeData(String practice) {
      this.practice = practice;
      totalHits = 0;
   }

   public void countProgramsAndInitDatas() {
      for (Multiset.Entry<String> entry : this.programs.entrySet()) {
         ProgramData pd = new ProgramData(entry.getElement(), entry.getCount());
          System.out.println("program/count " + entry.getElement() + "/" + entry.getCount());
         programList.add(pd);
         totalHits += entry.getCount();
         if (biggestProgramCounter < entry.getCount()) {
            biggestProgramCounter = entry.getCount();
            biggestProgram = entry.getElement();
         }
      }
   }

   public void addProgram(String prog, int c) {
      ProgramData pd = new ProgramData(prog, c);
      programList.add(pd);
      this.totalHits += c;
      if (c > biggestProgramCounter) {
         biggestProgramCounter = c;
         biggestProgram = prog;
      }
   }
}
