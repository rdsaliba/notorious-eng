package com.cbms.source.local;

import java.sql.Connection;

public class DAO {
    DatabaseConnection databaseConnection;

    public DAO() {
        databaseConnection = DatabaseConnection.start();
    }

    public Connection getConnection() {
        return DatabaseConnection.start().getConnection();
    }

    public void closeConnection() {
        databaseConnection.stop();
    }
}
