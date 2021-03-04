package external;

import app.ConfigProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final ConfigProperties properties = new ConfigProperties();

    private static DatabaseConnection openConnection;
    private static Connection conn;

    private static String url = null;
    private static String user = null;
    private static String password = null;

    static {
        try {
            url = properties.getConfigValues("database_url");
            user = properties.getConfigValues("database_user");
            password = properties.getConfigValues("database_password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {}

    public static DatabaseConnection getInstance() {
        if (openConnection == null) {
            openConnection = new DatabaseConnection();
        }
        return openConnection;
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return conn;
    }
}
