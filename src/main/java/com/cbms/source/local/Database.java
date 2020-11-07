package com.cbms.source.local;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    /**
     * Constructor
     *
     * @author Najim
     */
    public Database() {

    }

    /**
     * Simple function to test the database.
     *
     * @param conn Connection object used to create statements per JDBC's API
     */
    public void test(Connection conn) {
        try {
            Statement stmt = conn.createStatement();

            ResultSet dataRS = stmt.executeQuery("SELECT * FROM dataset");
            while (dataRS.next())
                System.out.println(dataRS.getString("dataset_id") + "  " + dataRS.getString("test_or_train") + "  " + dataRS.getString("name"));

            dataRS.close();
            System.out.println();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
