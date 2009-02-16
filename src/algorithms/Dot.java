/*
 * Dot.java
 * Created on April 4, 2007, 7:55 PM
 */

package algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Dot extends Point2D.Double
{
  public double dist = 0f;
  public double xdist = 0f;
  public double ydist = 0f;
  
  public Dot(double x, double y, double xdist, double ydist)
  {
    super(x, y);
    this.xdist = xdist;
    this.ydist = ydist;
  }
  
  public static void removeIllegalDots(List<Dot> dots, double flub)
  {
    
    for (int j = 0; j < dots.size(); j++)
    {
      Dot d1 = dots.get(j);
    
      for (int jj = dots.size() - 1; jj > j; jj--)
      {
        Dot d2 = dots.get(jj);
        
        //System.out.println("checking dot # " + j + " against dot # " + jj);
        if (d1.xdist <= d2.xdist && d1.ydist <= d2.ydist)
        {
          dots.remove(jj);
        }
      }
    }
  }
  
  public static void removeIllegalDots(List<Dot> xdots, List<Dot> ydots, double flub)
  {
    List<Dot> culledxdots = new ArrayList<Dot>();
    List<Dot> culledydots = new ArrayList<Dot>();
    
    double maxydist = 100f;
    for (int j = 0; j < xdots.size(); j++)
    {
      Dot dx1 = xdots.get(j);
      
      if (dx1.ydist < maxydist - flub)
      {
        culledxdots.add(dx1);
        maxydist = dx1.ydist;
      }
    }
     
    //xdots = culledxdots;
    xdots.clear();
    xdots.addAll(culledxdots);
    
    double maxxdist = 100f;
    Dot prevYDot = null;
    for (int j = 0; j < ydots.size(); j++)
    {
      Dot dy1 = ydots.get(j);
      
      //not sure if this check with prev dot makes sense
      if (prevYDot != null)
      {
        
        if (prevYDot.y == dy1.y)
        {
          prevYDot.xdist = Math.min(prevYDot.xdist, dy1.xdist);
          maxxdist = prevYDot.xdist;
          prevYDot = dy1;
          continue;
        }
      }
              
      
      System.out.println("is dy1.xdist < maxxdist " + dy1.xdist + " < " + maxxdist);
      if (dy1.xdist < maxxdist - flub)
      {
        System.out.println("yes, add it...");
        culledydots.add(dy1);
        maxxdist = dy1.xdist;
        prevYDot = dy1;
          
      }
      
    }
    
    //ydots = culledydots;
    ydots.clear();
    ydots.addAll(culledydots);
  
    System.out.println("ydots size here = " + ydots.size());
  }
  
  /*
  public static void removeIllegalDots(java.util.List<Dot> xdots, java.util.List<Dot> ydots, double flub)
  {
    for (int j = 0; j < ydots.size() - 1; j++)
    {
      Dot dy1 = ydots.get(j);
      
      for (int jj = ydots.size() - 1; jj > j; jj--)
      {
        Dot dy2 = ydots.get(jj);
        
        if (dy2.ydist >= dy1.ydist)
        {
          ydots.remove(dy2);
        }
      }
    }
    
    for (int j = 0; j < xdots.size() - 1; j++)
    {
      Dot dx1 = xdots.get(j);
      
      for (int jj = xdots.size() - 1; jj > j; jj--)
      {
        Dot dx2 = xdots.get(jj);
        
        if (dx2.xdist >= dx1.xdist)
        {
          xdots.remove(dx2);
        }
      }
    }
    
    for (int j = ydots.size() - 1; j >= 0; j--)
    {
      Dot dy = ydots.get(j);
      
      if (dy.ydist < flub)
      {
        ydots.remove(dy);
      }
    }
    
    for (int j = xdots.size() - 1; j >= 0; j--)
    {
      Dot dx = xdots.get(j);
      
      if (dx.xdist < flub)
      {
        xdots.remove(dx);
      }
    }
  }
  */
  
  //should sort by closest distance in the first in list
  public static void sortDots(java.util.List<Dot> dots, final String type)
  {
    Collections.sort(dots, new Comparator()
    {
      public int compare(Object a, Object b)
      {
        Dot dot1 = (Dot)a;
        Dot dot2 = (Dot)b;
        
        if (type.equals("x"))
        {
          if (dot1.xdist > dot2.xdist)
          {
            return 1;
          }
          else if (dot1.xdist < dot2.xdist)
          {
            return -1;
          }
          
          return 0;
        }
        
        else if (type.equals("y"))
        {
          //System.out.println("am i sorting y dots????");
          if (dot1.ydist > dot2.ydist)
          {
            return 1;
          }
          else if (dot1.ydist < dot2.ydist)
          {
            return -1;
          }
          
          return 0;
        }
        /*
        if (type.equals("x"))
        {
          if (dot1.ydist < dot2.ydist)
          {
            return -1;
          }
          else if (dot1.ydist > dot2.ydist)
          {
            return 1;
          }
          else
          {
            if (dot1.xdist < dot2.xdist)
            {
              return -1;
            }
            else if (dot1.xdist > dot2.xdist)
            {
              return 1;
            }
          }
          return 0;
        }
         
        else if (type.equals("y"))
        {
          if (dot1.xdist < dot2.xdist)
          {
            return -1;
          }
          else if (dot1.xdist > dot2.xdist)
          {
            return 1;
          }
          else
          {
            if (dot1.ydist < dot2.ydist)
            {
              return -1;
            }
            else if (dot1.ydist > dot2.ydist)
            {
              return 1;
            }
          }
          return 0;
        }
         */
        else
        {
          System.out.println("ERROR : sortDot type not implemented!");
          return 0;
        }
      }
    });
  }
  public static void sortDotsByClosestToCorner(java.util.List<Dot> dots)
  {
    Collections.sort(dots, new Comparator()
    {
      public int compare(Object a, Object b)
      {
        Dot dot1 = (Dot)a;
        Dot dot2 = (Dot)b;

        double dist1 = (double) Math.sqrt( Math.pow(dot1.xdist, 2) + Math.pow(dot1.ydist, 2) );
        double dist2 = (double) Math.sqrt( Math.pow(dot2.xdist, 2) + Math.pow(dot2.ydist, 2) );
        
          if (dist2 > dist1)
          {
            return -1;
          }
          else if (dist1 > dist2)
          {
            return 1;
          }
          
          return 0;
     }
    });
  }
        
  
  
  public String toString()
  {
    String s = "Dot (" +this.x+ ", " +this.y+ ") : xdist = " +xdist+ " ydist = " +ydist;
    return s;
  }
  
  
  public static void printDots(java.util.List<Dot> dots)
  {
    printDots(dots, "");
  }
  public static void printDots(java.util.List<Dot> dots, String word)
  {
    
    for (int idx = 0; idx < dots.size(); idx++)
    {
      Dot d = dots.get(idx);
      
      System.out.println(word + "("+idx+") " + d);
    }
  }
}
