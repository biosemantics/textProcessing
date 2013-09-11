/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author iychoi
 */
public class MySQLConnector {
    public static Connection getConnection(String id, String pwd) throws SQLException, ClassNotFoundException {
        // This will load the MySQL driver, each DB has its own driver
        Class.forName("com.mysql.jdbc.Driver");
        // Setup the connection with the DB
        return DriverManager.getConnection("jdbc:mysql://localhost/NCE?useUnicode=true&characterEncoding=utf-8", id, pwd);
    }
}
