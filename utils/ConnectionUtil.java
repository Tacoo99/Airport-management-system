package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A class to connect to the SQL database
 */
public class ConnectionUtil {

    /**
     * Declaration of the library that supports our type of database and providing the address with access data
     * @return Returning the connection object to the SQL database
     */

    public static Connection conDB()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/projekt", "root", "");
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Baza SQL : "+ex.getMessage());
           return null;
        }
    }

}
