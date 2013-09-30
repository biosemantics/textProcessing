/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

/**
 *
 * @author iychoi
 */
public class DBUtil {
    
    public static Connection getConnection() throws IOException {
        return getConnection("nce", "nce");
    }
    
    public static Connection getConnection(String id, String pwd) throws IOException {
        try {
            return MySQLConnector.getConnection(id, pwd);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public static void setCharacterSet(Connection conn) throws IOException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("SET NAMES 'utf8'");
            stmt.execute("set character_set_server='utf8';");
            // stmt.execute("set character_set_dabatase='utf8';");
            // stmt.execute("set character_set_system = 'utf8';");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
