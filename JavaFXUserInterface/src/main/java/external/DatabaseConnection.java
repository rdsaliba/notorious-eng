package external;

import app.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final ConfigProperties properties = new ConfigProperties();
    static Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
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
            logger.error("Exception: ", e);
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
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                logger.error("Exception: ", ex);
            }
        }

        return conn;
    }
}
