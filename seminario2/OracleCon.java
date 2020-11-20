import java.sql.*;

public class OracleCon {
   public static void main(String args[]) {
      try {
         // load driver class
         //Class.forName("oracle.jdbc.driver.OracleDriver");
         //Class.forName("com.mysql.jdbc.Driver");

         /**
          * Host = oracle0.ugr.es
          * Port = 1521
          * Service Name = practbd.oracle0.ugr.es
          * User/Password = x5943758
          */
         // create the connection object
         String url = "jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
         String usr = "x7475341";
         String pwd = "x7475341";
         Connection con = DriverManager.getConnection(url, usr, pwd);

         
         // create the statement object
         Statement stmt = con.createStatement();

         // execute query
         ResultSet rs = stmt.executeQuery("select * from emp");
         while (rs.next()) {
            System.out.println(
               rs.getInt(1)    + " " +
               rs.getString(2) + " " +
               rs.getInt(3)
            );
         }

         // close the connection object
         con.close();
      } catch (Exception e) {
         System.out.println(e);
      }
   }
}
