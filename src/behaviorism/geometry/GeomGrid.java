/* GeomGrid.java (created on Jul 21, 2008) */
package behaviorism. geometry;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;

/**
 *
 * @author Angus Forbes
 */
public class GeomGrid extends GeomRect
{
  public int xLines = -1;
  public int yLines = -1;
  public int numCols = -1;
  public int numRows = -1;
  public float xsize = -1f;
  public float ysize = -1f;
  /**
   * Indicates which lines to highlight, if positive. Not used if less than 1.
   * For example, if majorLineStride = 3, then every 3rd line can be drawn with a different color.
   */
  int majorLineStride = -1;
  public Colorf majorLineColor = new Colorf(1f, 0f, 0f, 1f);
  public Colorf lineColor = new Colorf(.6f, .6f, .6f, 1f);

  public void initializeGrid(int numCols, int numRows, int majorLines)
  {
    this.numCols = numCols;
    this.numRows = numRows;
    this.xLines = numCols + 1;
    this.yLines = numRows + 1;

    this.xsize = this.w / (float) numCols;
    this.ysize = this.h / (float) numRows;

    this.majorLineStride = majorLines;
  }

  public GeomGrid(int numCols, int numRows, Point3f p3f, float w, float h)
  {
    super(p3f, w, h);
    initializeGrid(numCols, numRows, -1);
  }

  public GeomGrid(int numCols, int numRows, int majorLine, Point3f p3f, float w, float h)
  {
    super(p3f, w, h);
    initializeGrid(numCols, numRows, majorLine);
  }

  /**
   * Constructs the GeomGrid inside a rectangle that is defined by its lower-left and upper-right points.
   * @param numCols
   * @param numRows
   * @param ep1 the lower-left Point3f of the rectangle
   * @param ep2 the upper-left Point3f of the rectangle
   */
  public GeomGrid(int numCols, int numRows, Point3f ep1, Point3f ep2)
  {
    super(ep1.x, ep1.y, 0f, ep2.x - ep1.x, ep2.y - ep1.y);
    initializeGrid(numCols, numRows, -1);
  }

  public GeomGrid(int numCols, int numRows, int majorLine, Point3f ep1, Point3f ep2)
  {
    super(ep1.x, ep1.y, 0f, ep2.x - ep1.x, ep2.y - ep1.y);
    initializeGrid(numCols, numRows, majorLine);
  }

  @Override
  public void draw(GL gl)
  {
    //super.draw(gl, glu, offset - (offset * .5f));

    gl.glDisable(GL.GL_BLEND);
    gl.glLineWidth(.5f);

    gl.glBegin(gl.GL_LINES);

    for (int i = 0; i < (xLines); i++)
    {
      if (majorLineStride > 0 && i % majorLineStride == 0)
      {
        gl.glColor4f(majorLineColor.r, majorLineColor.g, majorLineColor.b, majorLineColor.a);
      }
      else
      {
        gl.glColor4f(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
      }

      float xpos1 = xsize * i;
      gl.glVertex3f(xpos1, 0f, translate.z + offset);
      gl.glVertex3f(xpos1, h, translate.z + offset);
    }


    for (int i = 0; i < (yLines); i++)
    {
      if (majorLineStride > 0 && i % majorLineStride == 0)
      {
        gl.glColor4f(majorLineColor.r, majorLineColor.g, majorLineColor.b, majorLineColor.a);
      }
      else
      {
        gl.glColor4f(lineColor.r, lineColor.g, lineColor.b, lineColor.a);
      }

      float ypos1 = ysize * i;
      gl.glVertex3f(0f, ypos1, translate.z + offset);
      gl.glVertex3f(w, ypos1, translate.z + offset);
    }

    /*
    gl.glColor4f(0f, 0f, 0f, 1f);
    for (int i = 0; i < numCols + 1; i++)
    {
    float xpos1 = (float) Utils.getSlotPosition(i, numCols, 0, w);
    gl.glVertex3f(xpos1, 0f, translate.z);
    gl.glVertex3f(xpos1, h, translate.z);
    }
    
    for (int i = 0; i < numRows + 1; i++)
    {
    float ypos1 = (float) Utils.getSlotPosition(i, numRows, 0, h);
    gl.glVertex3f(0f, ypos1, translate.z);
    gl.glVertex3f(w, ypos1, translate.z);
    }
     */
    gl.glEnd();

    gl.glEnable(GL.GL_BLEND);

  }

  public Point3f getGridPoint(int col, int row)
  {
    return new Point3f((xsize * (col)), (ysize * row), 0f);
  }

  public float getColumnSize()
  {
    return xsize;
  }

  public float getRowSize()
  {
    return ysize;
  }

  public float getColPosition(int col)
  {
    return (xsize * col);
  }

  public float getRowPosition(int row)
  {
    return (ysize * row);
  }
  //really only 2D-- wouldn't be hard to make 3d for real... to do

  public Point3f getCellCenter(int col, int row)
  {
    float xc = (xsize * col) + (xsize * .5f);
    float yc = (ysize * row) + (ysize * .5f);
    return new Point3f(xc, yc, 0f);
  }

  public float getColCenter(int col)
  {
    return (xsize * col) + (xsize * .5f);
  }

  public float getRowCenter(int row)
  {
    return (ysize * row) + (ysize * .5f);
  }

  /**
   * returns a GeomRect for this cell of size 1 starting at the specified column and row.
   * @param col
   * @param row
   * @return a GeomRect for the cell indicated by the input parameters.
   */
  public GeomRect getCellGeom(int col, int row)
  {
    Point3f cellPt = getGridPoint(col, row);
    return new GeomRect(cellPt.x, cellPt.y, 0f, xsize, ysize);
  }

  /** 
   * Creates a GeomRect starting at the specified column and row with a specfied number of columns and rows.
   * @param col
   * @param row
   * @param numColCells
   * @param numRowCells
   * @return a GeomRect for the cells indicated by the input parameters.
   */
  public GeomRect getCellGeom(int col, int row, int numColCells, int numRowCells)
  {
    Point3f cellPt = getGridPoint(col, row);
    return new GeomRect(cellPt.x, cellPt.y, 0f, xsize * numColCells, ysize * numRowCells);
  }

  public GeomRect getCellRowGeom(int row)
  {
    return new GeomRect(translate.x, getGridPoint(0, row).y, 0f, w, ysize);
  }

  public GeomRect getCellColGeom(int col)
  {
    return new GeomRect(getGridPoint(col, 0).x, translate.y, 0f, xsize, h);
  }

  public int getColContaining(Point3f pt)
  {
    float test = 0f;
    for (int i = 0; i < xLines; i++)
    {
      test += xsize;
      if (pt.x < test)
      {
        return i;
      }

    }
    return -1; //error
  }

  public int getRowContaining(Point3f pt)
  {
    float test = 0f;
    for (int i = 0; i < yLines; i++)
    {
      test += ysize;
      if (pt.y < test)
      {
        return i;
      }

    }
    return -1; //error
  }

  public int getClosestCol(Point3f unsnappedPt)
  {
    if (unsnappedPt.x < 0f)
    {
      return 0;
    }
//System.out.println("is unsnapped.x > ( " + (xsize * (xLines - 2)) );

    if (unsnappedPt.x > xsize * (xLines - 2))
    {
      //System.out.println("YES too big... returning " + (xLines - 2));
      return xLines - 2;
    }

    float test = 0f;
    for (int i = 0; i <
      xLines; i++)
    {
      test += xsize;
      if (test > unsnappedPt.x)
      {
        //System.out.println("unsnapped x / pivot = " + unsnappedPt.x + "/" + (test - (xsize * .5f)));
        if (unsnappedPt.x > test - (xsize * .5f))
        {
          //System.out.println("bigger than midpoint, returning " +(i+1));
          return i + 1;
        }
        else
        {
          //System.out.println("less than midpoint, returning " +(i));
          return i;
        }

      }
    }

    return -1; //we should never get here
  }

  public int getClosestRow(Point3f unsnappedPt)
  {
    if (unsnappedPt.y < 0f)
    {
      return 0;
    }

    if (unsnappedPt.y > ysize * (yLines - 2))
    {
      return yLines - 2;
    }

    float test = 0f;

    for (int i = 0; i <
      yLines; i++)
    {
      test += ysize;

      if (test > unsnappedPt.y)
      {
        if (unsnappedPt.y > test - (ysize * .5f))
        {
          return i + 1;
        }
        else
        {
          return i;
        }

      }
    }

    return -1; //we should never get here
  }


//same as normal, except wrap around
  public int getClosestRowWrap(Point3f unsnappedPt)
  {
    if (unsnappedPt.y < 0f)
    {
      return yLines - 1;
    }

    if (unsnappedPt.y > ysize * (yLines - 1))
    {
      return 0;
    }

    float test = 0f;

    for (int i = 0; i <
      yLines; i++)
    {
      test += ysize;

      if (test > unsnappedPt.y)
      {
        if (unsnappedPt.y > test - (ysize * .5f))
        {
          return i + 1;
        }
        else
        {
          return i;
        }

      }
    }

    return -1; //we should never get here
  }

  public int getClosestColWrap(Point3f unsnappedPt)
  {
    if (unsnappedPt.x < 0f)
    {
      return xLines - 1;
    }
//System.out.println("is unsnapped.x > ( " + (xsize * (xLines - 2)) );

    if (unsnappedPt.x >= xsize * (xLines - 1))
    {
      //System.out.println("YES too big... returning " + (xLines - 2));
      return 0;
    }

    float test = 0f;
    for (int i = 0; i <
      xLines; i++)
    {
      test += xsize;
      if (test > unsnappedPt.x)
      {
        //System.out.println("unsnapped x / pivot = " + unsnappedPt.x + "/" + (test - (xsize * .5f)));
        if (unsnappedPt.x > test - (xsize * .5f))
        {
          //System.out.println("bigger than midpoint, returning " +(i+1));
          return i + 1;
        }
        else
        {
          //System.out.println("less than midpoint, returning " +(i));
          return i;
        }

      }
    }

    return -1; //we should never get here
  }

  /**
   * Chooses the grid point which is closest to the input point and returns a copy of it.
   * This method does not modify the input point.
   * @param unsnappedPt
   * @return a Point3f representing the input Point3f snapped to the grid.
   */
  public Point3f snapToGrid(
    Point3f unsnappedPt)
  {
    int closestCol = getClosestCol(unsnappedPt);
    int closestRow = getClosestRow(unsnappedPt);

    System.out.println("closestCol / Row " + closestCol + "/" + closestRow);
    return getGridPoint(closestCol, closestRow);
  }
}
