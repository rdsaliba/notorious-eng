package com.cbms.source.local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mariadb://127.0.0.1:3306/cbms?";
    //Make sure to set the user and password to the proper values.
    //Credentials should be set to that which you are using on your local DB server.
    private static final String USER = "root";
    private static final String PASSWORD = "Kratos753951_";
    private Connection conn;

    /**
     * Constructor
     * Creates connection to the database, creates tables and inserts data.
     *
     * @author Najim
     */
    public Database() {
        this.conn = setConnection();
    }

    /**
     * Getter
     *
     * @return (returns Connection object)
     * @author Najim
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Creates connection to the in-memory database by specifying the proper URL string.
     *
     * @return (returns a Connection object)
     * @author Najim
     */
    private Connection setConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD); //Using JDBC's API we connect to the in-memory database
            System.out.println("Connection Created\n");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }
}
