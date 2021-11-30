import java.sql.Connection;
import java.sql.DriverManager;

public class DataConnection {
    public static Connection createConnection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/family", "root", "Prakash1163.");
        }catch (Exception e){
            e.printStackTrace();
        }
        return con;
    }
}
