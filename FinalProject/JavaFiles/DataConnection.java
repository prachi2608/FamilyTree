import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DataConnection {

    //Class to setup Connection to Database
    public static Connection createConnection() {
        Properties pro = new Properties();

        try {
            // name of the property file which contains username and
            pro.load(new FileInputStream("login.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // getting username, password and database name for login into database
        String user = pro.getProperty("username");
        String pass = pro.getProperty("password");
        String data = pro.getProperty("database");

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306/" + data, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
