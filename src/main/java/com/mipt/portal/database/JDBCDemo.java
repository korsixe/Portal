import java.sql.Connection;
import java.sql.DriverManager;

public class JDBCDemo {
  public static void main(String[] args) {
    String upl = "jdbs:mysql://localhost:3306/portal";
    try {
      Class.forName("com.mysql.cj.jdbs.Driver");
      Connection connection = DriverManager.getConnection(url);
    } catch (java.lang.Exception e) {
      System.out.println(e);
    }
  }
}