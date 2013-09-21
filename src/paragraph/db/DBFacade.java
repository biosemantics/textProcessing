/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph.db;

import common.db.DBUtil;
import java.io.IOException;
import java.sql.Connection;

/**
 *
 * @author iychoi
 */
public class DBFacade {
    public static void createTables(Connection conn) throws IOException {
        DBUtil.setCharacterSet(conn);
        
        RegExTable.createRegExTable(conn, false);
        DocumentTable.createDocumentTable(conn, false);
        ParagraphTable.createParagraphTable(conn, false);
    }
}
