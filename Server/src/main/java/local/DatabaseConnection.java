/*
  Creates the connection to the MariaDB database.

  @author Najim, Roy
  @last_edit 02/7/2020
 */
package local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mariadb://127.0.0.1:3306/cbms?";
    //Make sure to set the user and password to the proper values.
    //Credentials should be set to that which you are using on your local DB server.
    private static final String USER = "root"; // todo  use username and password specific to your machine
    private static final String PASSWORD = "";
    private static DatabaseConnection openConnection;
    private static Connection conn;

    private DatabaseConnection() {
    }

    /**
     * Creates an instance of the class. Does not allow for more than one instance of the class.
     *
     * @return An instance of the class.
     * @author Najim
     */
    public static DatabaseConnection getInstance() {
        if (openConnection == null) {
            openConnection = new DatabaseConnection();
        }
        return openConnection;
    }


    /**
     * Getter
     *
     * @return A Connection object
     * @author Najim
     */
    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return conn;
    }
}
