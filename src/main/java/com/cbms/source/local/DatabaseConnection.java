package com.cbms.source.local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mariadb://127.0.0.1:3306/cbms?";
    //Make sure to set the user and password to the proper values.
    //Credentials should be set to that which you are using on your local DB server.
    private static final String USER = ""; // todo use username and password specific to your machine
    private static final String PASSWORD = "";
    private static DatabaseConnection openConnection;
    private Connection conn;

    /**
     * Constructor
     * When the constructor is called, a connection to the database is made.
     *
     * @author Najim
     */
    private DatabaseConnection() {
        init();
    }

    /**
     * Creates an instance of the class. Does not allow for more than one instance of the class.
     *
     * @return An instance of the class.
     * @author Najim
     */
    public static DatabaseConnection start() {
        if (openConnection == null) {
            openConnection = new DatabaseConnection();
        }
        return openConnection;
    }

    /**
     * Creates a connection to a specified database (with the URL) using the JDBC API.
     *
     * @author Najim
     */
    private void init() {
        try {
            this.conn = DriverManager.getConnection(URL, USER, PASSWORD); //Using JDBC's API we connect to the in-memory database
            System.out.println("Connection Created\n");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Getter
     *
     * @return A Connection object
     * @author Najim
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Stops the connection to the database.
     *
     */
    public void stop() {
        try {
            this.conn.close();
            System.out.println("Connection stopped.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
