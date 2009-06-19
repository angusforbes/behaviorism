package parsers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//rename this SqlHandler and move to handlers package! TO DO
abstract public class Connector
{

  protected String url;
  protected String username;
  protected String password;
  protected String driver = "com.mysql.jdbc.Driver"; //default MySQL MM JDBC driver
  protected Connection connection = null;

  protected void initializeConnections()
  {
    try
    {
      if (connection != null && connection.isClosed() == false)
      {
        return; //because already open
      }

      System.out.println("Attempting to open database (" + this.url + ", user:" + this.username + ", pswd:" + this.password + ")...");

      Class.forName(this.driver); //loads driver

      Properties props = new Properties();
      props.setProperty("user", this.username);
      props.setProperty("password", this.password);

      connection = DriverManager.getConnection(this.url, props);

      connection.setAutoCommit(false);
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("ERROR : initializeConnections() : Could not find the database driver!");
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      System.err.println("ERROR : initializeConnections() : Could not open SQL connection to database!");
      e.printStackTrace();
    }

    System.out.println("Succesfully opened database (" + this.url + ", user:" + this.username + ", pswd:" + this.password + ")...");
  }

 
  public List<String> selectColumnFromTable(String column, String table)
  {
    return selectColumnFromTable(column, table, false);
  }

  public List<String> selectColumnFromTable(String column, String table, boolean isDistinct)
  {
    initializeConnections();

    List<String> values = new ArrayList<String>();

    Statement stmt = null;

    ResultSet rs = null;


    try
    {
      stmt = connection.createStatement();

      String q;

      if (isDistinct == true)
      {
        q = "SELECT DISTINCT " + column + " FROM " + table;
      }
      else
      {
        q = "SELECT " + column + " FROM " + table;
      }

      rs = stmt.executeQuery(q);

      while (rs.next())
      {
        values.add(rs.getString(column));
      }

      rs.close();
      rs =
        null;

    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    finally
    {
      handleFinal(rs);
      handleFinal(stmt);
    }

    return values;
  }

  
  public int selectOrInsert(String tableName, String idCol, ColumnData... cds)
  {
    initializeConnections();

    int title_id = -1;


    Statement stmt = null;

    ResultSet rs = null;

    Statement stmt2 = null;

    ResultSet rs2 = null;


    try
    {
      stmt = connection.createStatement();

      String select = "SELECT " + idCol + " FROM " + tableName + " " + makeWhereClauseFromColumnDatas(cds);

      //System.out.println("select = <" + select + ">");
      rs =
        stmt.executeQuery(select);

      if (rs.next())
      {
        title_id = rs.getInt(1);

      }

      rs.close();
      rs =
        null;


      if (title_id == -1) //time1 we need to add it to the table
      {
        //
        // Insert one row that will generate an AUTO INCREMENT
        // key in the 'priKey' field
        //

        stmt2 = connection.createStatement(
          ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

        String insert = "INSERT INTO " + tableName + " " + makeValuesClauseFromColumnDatas(cds);
        //System.out.println("insert = <" + insert + ">");



        stmt2.executeUpdate(insert);
        rs2 =
          stmt.executeQuery("SELECT LAST_INSERT_ID()");

        if (rs2.next())
        {
          title_id = rs2.getInt(1);

        }
        else
        {

          // throw an exception from here
          System.out.println("error... couldn't get last id!");

        }

        rs2.close();
        rs2 =
          null;

      //System.out.println("Key returned from getGeneratedKeys():" + title_id);
      }

    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    finally
    {
      handleFinal(rs, rs2);
      handleFinal(stmt, stmt2);
    }

    return title_id;
  }
  
  public String makeValuesClauseFromColumnDatas(ColumnData[] cds)
  {
    String cols = "(";
    String vals = "(";

    for (int i = 0; i < cds.length; i++)
    {
      ColumnData cd = cds[i];

      cols += cd.column;

      if (cd.data == null)
      {
        vals += "NULL";
      }
      else
      {
        vals += "\"" + cd.data + "\"";
      }
      if (i < cds.length - 1)
      {
        cols += ", ";
        vals += ", ";
      }
    }

    cols += ")";
    vals += ")";

    return cols + " values " + vals;
  }

  public String makeWhereClauseFromColumnDatas(ColumnData[] cds)
  {
    String where = " WHERE ";

    for (int i = 0; i < cds.length; i++)
    {
      ColumnData cd = cds[i];

      if (cd.data == null)
      {
        where += "" + cd.column + " IS NULL ";
      }
      else
      {
        where += "" + cd.column + " = \"" + cd.data + "\"";
      }
      if (i < cds.length - 1)
      {
        where += " AND ";
      }
    }

    return where;
  }

  public void commit()
  {
    try
    {
      connection.commit();
      connection.setAutoCommit(true);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

  }

  public void closeConnection()
  {
    if (connection != null)
    {
      try
      {
        if (connection.isClosed() != true)
        {
          connection.close();
        }
      }
      catch (java.sql.SQLException e)
      {
        e.printStackTrace();
      }
    }
  }

  protected void handleFinal(Statement... stmts)
  {
    for (Statement stmt : stmts)
    {

      if (stmt != null)
      {
        try
        {
          stmt.close();
          stmt = null;
        }
        catch (SQLException ex)
        {
          // ignore
        }
      }
    }
  }

  protected void handleFinal(ResultSet... rss)
  {
    for (ResultSet rs : rss)
    {
      if (rs != null)
      {
        try
        {
          rs.close();
          rs = null;
        }
        catch (java.sql.SQLException e)
        {
          // ignore
        }
      }
    }
  }
}
