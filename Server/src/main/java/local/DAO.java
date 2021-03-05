/*
    Parent object of all DAOs
    this object only gets the active connection and kills it

  @author Paul Micu
  @last_edit 12/27/2020
 */
package local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {

    Logger logger = LoggerFactory.getLogger(DAO.class);

    DatabaseConnection databaseConnection;

    public DAO() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    public Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    /** This will run the query in the parameter and return its corresponding ResultSet
     *
     * @author Paul
     */
    public ResultSet nonParamQuery(String query) {
        ResultSet rs = null;
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            logger.error("Exception: ", e);
        }
        return rs;
    }
}
