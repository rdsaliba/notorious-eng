package com.cbms.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class Main   {

   /* @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/SystemInfo.fxml"));
        Scene sample = new Scene(root);
        primaryStage.setTitle("CBMS");
        primaryStage.setScene(sample);
        primaryStage.show();
    }
*/

    public static void main(String[] args) throws Exception {
        
    //    StartupController startupController = new StartupController();
    //    startupController.generateModel();

  //  launch(args);

        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR(20))");
        System.out.println("Table created");
        stmt.executeUpdate("INSERT INTO test VALUES(1, 'one')");

        ResultSet rs = stmt.executeQuery("SELECT * FROM test");
        while(rs.next())
            System.out.println(rs.getString("name"));
    }

    public static Connection getConnection(){
        String url = "jdbc:h2:mem:cbms_db";
        String user = "sa";
        String password = "";
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("Connection Created");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
