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

            stmt.execute("CREATE TABLE datasets(" +
                    "DatasetID int," +
                    "Test_or_Train bool," +
                    "Name char(20)," +
                    "Train_nb int," +
                    "Test_nb int," +
                    "PRIMARY KEY (DatasetID, Test_or_Train))");
            System.out.println("Dataset table created");


            stmt.execute("CREATE TABLE operational_conditions(" +
                    "OC_Name char(20)," +
                    "OC_Description varchar(300)," +
                    "PRIMARY KEY (OC_Name))");
            System.out.println("Operational Conditions Table created");


            stmt.execute("CREATE TABLE measured_in(" +
                    "DatasetID int," +
                    "Test_or_Train bool," +
                    "OC_Name char(20)," +
                    "PRIMARY KEY (DatasetID, Test_or_Train, OC_Name)," +
                    "FOREIGN KEY (DatasetID,Test_or_Train) REFERENCES Datasets (DatasetID,Test_or_Train)," +
                    "FOREIGN KEY (OC_Name) REFERENCES Operational_Conditions (OC_Name))");
            System.out.println("Measured In table created");


            stmt.execute("CREATE TABLE systems(" +
                    "DatasetID int," +
                    "Test_or_Train bool," +
                    "Unit_nb int," +
                    "Name char(20)," +
                    "Type char(20)," +
                    "Description varchar(300)," +
                    "SN char(20)," +
                    "Manufacturer char(20)," +
                    "Category char(20)," +
                    "Site char(20)," +
                    "Location char(20)," +
                    "PRIMARY KEY (DatasetID, Test_or_Train, Unit_nb)," +
                    "FOREIGN KEY (DatasetID,Test_or_Train) REFERENCES Datasets (DatasetID,Test_or_Train))");
            System.out.println("Systems table created");


            stmt.execute("CREATE TABLE sensors(" +
                    "DatasetID int," +
                    "Test_or_Train bool," +
                    "Unit_nb int," +
                    "Sensor_nb int," +
                    "SensorID char(20)," +
                    "Type char(20)," +
                    "Location char(20)," +
                    "PRIMARY KEY (Sensor_nb))");
            System.out.println("Sensors table created");


            stmt.execute("CREATE TABLE measures(" +
                    "DatasetID int," +
                    "Test_or_Train bool," +
                    "Unit_nb int," +
                    "Sensor_nb int," +
                    "Time int," +
                    "Sensor_value double," +
                    "PRIMARY KEY (DatasetID, Test_or_Train, Unit_nb, Sensor_nb, Time)," +
                    "FOREIGN KEY (DatasetID,Test_or_Train,Unit_nb ) REFERENCES Systems" +
                    "(DatasetID,Test_or_Train,Unit_nb )," +
                    "FOREIGN KEY (Sensor_nb) REFERENCES Sensors(Sensor_nb))");
            System.out.println("Measure table created");


            stmt.execute("CREATE TABLE models(" +
                    "Name char(20)," +
                    "Description varchar(300)," +
                    "PRIMARY KEY (Name))");
            System.out.println("Models table created");


            stmt.execute("CREATE TABLE calculates_ruls(" +
                    "DatasetID int," +
                    "Test_or_Train bool," +
                    "Unit_nb int," +
                    "Model_name char(20)," +
                    "Timestamp datetime," +
                    "RUL double," +
                    "PRIMARY KEY (DatasetID, Test_or_Train, Unit_nb, Model_name, Timestamp)," +
                    "FOREIGN KEY (DatasetID,Test_or_Train,Unit_nb) REFERENCES Systems (DatasetID,Test_or_Train,Unit_nb)," +
                    "FOREIGN KEY (Model_name) REFERENCES Models (Name))");
            System.out.println("Calculates Ruls table created");


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
