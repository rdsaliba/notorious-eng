/*
  Creates the connection to the MariaDB database.

  @author Najim, Roy
  @last_edit 02/7/2020
 */
package local;

import app.ConfigProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final ConfigProperties properties = new ConfigProperties();

    private static DatabaseConnection openConnection;
    private static Connection conn;

    private static String URL = null;
    private static String USER = null;
    private static String PASSWORD = null;

    static {
        try {
            URL = properties.getConfigValues("database_url");
            USER = properties.getConfigValues("database_user");
            PASSWORD = properties.getConfigValues("database_password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() {
        if (openConnection == null) {
            openConnection = new DatabaseConnection();
        }
        return openConnection;
    }

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
