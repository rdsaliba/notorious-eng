/*
    Parent object of all DAOs
    this object only gets the active connection and kills it

  @author Paul Micu
  @version 1.0
  @last_edit 12/27/2020
 */
package local;

import java.sql.Connection;

public class DAO {
    DatabaseConnection databaseConnection;

    public DAO() {
        databaseConnection = DatabaseConnection.getInstance();
    }

    public Connection getConnection() {
        return DatabaseConnection.getConnection();
    }
}
