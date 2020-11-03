package com.cbms.source.local;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:h2:mem:cbms_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private Connection conn = null;

    public Database() {
        this.conn = getConnection();
        createTables();
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);
            System.out.println("Connection Created");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void createTables() {
        try {
            Statement stmt = this.conn.createStatement();

            stmt.execute("CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR(20))");
            System.out.println("Table created");
            stmt.executeUpdate("INSERT INTO test VALUES(1, 'one')");

            ResultSet rs = stmt.executeQuery("SELECT * FROM test");
            while (rs.next())
                System.out.println(rs.getString("name"));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertData() {

    }

    public void deleteData() {

    }

}
