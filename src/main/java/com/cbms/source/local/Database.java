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
            //Make sure the naming convention is up to par variable name lowercase, sql statement uppercase

            //Train is TRUE, Test is FALSE

            stmt.execute("CREATE TABLE dataset(" +
                    "dataset_id INT AUTO_INCREMENT," +
                    "test_or_train BOOL," +
                    "name CHAR(5)," +
                    "PRIMARY KEY (dataset_id))");
            System.out.println("Dataset table created");


            stmt.execute("CREATE TABLE operational_condition(" +
                    "oc_name VARCHAR(50)," +
                    "oc_description VARCHAR(300)," +
                    "PRIMARY KEY (oc_name))");
            System.out.println("Operational Conditions Table created");


            stmt.execute("CREATE TABLE measured_in(" +
                    "dataset_id INT," +
                    "oc_name VARCHAR(50)," +
                    "PRIMARY KEY (dataset_id, oc_name)," +
                    "FOREIGN KEY (dataset_id) REFERENCES dataset (dataset_id)," +
                    "FOREIGN KEY (oc_name) REFERENCES operational_condition (oc_name))");
            System.out.println("Measured In table created");


            stmt.execute("CREATE TABLE systems(" +
                    "dataset_id INT," +
                    "unit_nb INT," +
                    "name VARCHAR(50)," +
                    "type VARCHAR(20)," +
                    "description VARCHAR(300)," +
                    "sn VARCHAR(20)," +
                    "manufacturer VARCHAR(20)," +
                    "category VARCHAR(20)," +
                    "site VARCHAR(20),location VARCHAR(20)," +
                    "PRIMARY KEY (dataset_id, unit_nb)," +
                    "FOREIGN KEY (dataset_id) REFERENCES dataset(dataset_id))");
            System.out.println("Systems table created");


            stmt.execute("CREATE TABLE sensor(" +
                    "sensor_nb INT," +
                    "sensor_id INT," +
                    "type VARCHAR(20)," +
                    "location VARCHAR(20)," +
                    "PRIMARY KEY (sensor_nb))");
            System.out.println("Sensors table created");


            stmt.execute("CREATE TABLE measure(" +
                    "dataset_id INT," +
                    "unit_nb INT," +
                    "sensor_nb INT," +
                    "time INT," +
                    "sensor_value DOUBLE," +
                    "PRIMARY KEY (dataset_id, unit_nb, sensor_nb, time)," +
                    "FOREIGN KEY (dataset_id, unit_nb ) REFERENCES systems" +
                    "(dataset_id,unit_nb )," +
                    "FOREIGN KEY (sensor_nb) REFERENCES sensor(sensor_nb))");
            System.out.println("Measure table created");


            stmt.execute("CREATE TABLE model(" +
                    "name VARCHAR(20)," +
                    "description VARCHAR(300)," +
                    "PRIMARY KEY (name))");
            System.out.println("Models table created");


            stmt.execute("CREATE TABLE calculates_rul(" +
                    "dataset_id INT," +
                    "unit_nb INT," +
                    "model_name VARCHAR(20)," +
                    "timestamp DATETIME," +
                    "rul DOUBLE," +
                    "PRIMARY KEY (dataset_id, unit_nb, model_name, timestamp)," +
                    "FOREIGN KEY (dataset_id,unit_nb) REFERENCES systems(dataset_id,unit_nb)," +
                    "FOREIGN KEY (model_name) REFERENCES model(name))");
            System.out.println("Calculates Ruls table created");



            //EXAMPLE CODE
            stmt.execute("CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR(20))");
            System.out.println("Table created");
            stmt.executeUpdate("INSERT INTO test VALUES(1, 'one')");
            ResultSet rs = stmt.executeQuery("SELECT * FROM Test");
            while (rs.next())
                System.out.println(rs.getString("NAME"));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertData() {

    }

    public void deleteData() {

    }

}
