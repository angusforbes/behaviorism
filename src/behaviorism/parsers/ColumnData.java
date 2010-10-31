package behaviorism. parsers;

//move this to a class inside SqlHandler...
public class ColumnData {

   String column;
   String data;

   public ColumnData(String column, String data) {
      this.column = column;
      this.data = data;
      //System.out.println("inside column data for " + column);
   }
}
