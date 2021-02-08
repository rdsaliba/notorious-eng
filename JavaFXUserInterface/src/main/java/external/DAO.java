/*
    Parent object of all DAOs
    this object only gets the active connection and kills it

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package external;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {
    DatabaseConnection databaseConnection;

    public DAO() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    public Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    public ResultSet nonParamQuery(String query){
        ResultSet rs = null;
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
