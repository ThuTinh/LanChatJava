/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Thu Tinh
 */
public class SqlConnection {
    private Connection conn ;
    private String password ;
    private String user ;

    public SqlConnection() {
        conn = null;
        password = null;
        user = null;
        
    }
    
    public void OpenConnection() throws ClassNotFoundException, SQLException
  {
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
      String url = "jdbc:sqlserver://localhost:1433;databaseName=DBLanChat";
      password = "thutinh1234@";
      user = "sa";
      conn = DriverManager.getConnection(url, user, password);
  }
  public  Connection GetConnection()
  {
      return conn;
  }
  public  void CloseConnection() throws SQLException
  {
      conn.close();
  }
  
    
}
