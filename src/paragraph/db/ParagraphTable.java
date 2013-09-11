/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph.db;

import paragraph.bean.ParagraphType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import paragraph.bean.Paragraph;

/**
 *
 * @author iychoi
 */
public class ParagraphTable {
    public static final String PARAGRAPH_TABLE_NAME = "tblParagraph";
    
    public static void createParagraphTable(Connection conn, boolean bClean) throws IOException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            if (bClean) {
                stmt.execute("drop table if exists " + PARAGRAPH_TABLE_NAME);
            }
            stmt.execute("create table if not exists " + PARAGRAPH_TABLE_NAME
                    + " (paragraphID bigint not null primary key auto_increment, "
                    + "documentID bigint not null, "
                    + "pageNumber bigint not null, "
                    + "content text(5000) not null, "
                    + "type text(1000) default '" + ParagraphType.PARAGRAPH_UNKNOWN.name() + "')");
                    
            stmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static void insertParagraph(Connection conn, Paragraph paragraph) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("insert into " + PARAGRAPH_TABLE_NAME 
                    + " (documentID, pageNumber, content, type) values (?, ?, ?, ?)");
            pstmt.setInt(1, paragraph.getDocumentID());
            pstmt.setInt(2, paragraph.getPageNumber());
            pstmt.setString(3, paragraph.getContent());
            pstmt.setString(4, paragraph.getType());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                paragraph.setParagraphID(rs.getInt(1));
            } else {
                throw new IOException("there's no generated id");
            }
            
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static Paragraph getParagraph(Connection conn, int paragraphID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("select * from " + PARAGRAPH_TABLE_NAME 
                    + " where paragraphID = ?");
            pstmt.setInt(1, paragraphID);
            ResultSet rs = pstmt.executeQuery();
            Paragraph paragraph = null;
            if (rs.next()) {
                paragraph = new Paragraph();
                paragraph.setParagraphID(rs.getInt("paragraphID"));
                paragraph.setDocumentID(rs.getInt("documentID"));
                paragraph.setPageNumber(rs.getInt("pageNumber"));
                paragraph.setContent(rs.getString("content"));
                paragraph.setType(rs.getString("type"));
            }
            
            rs.close();
            pstmt.close();
            return paragraph;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static List<Paragraph> getParagraphs(Connection conn, int documentID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("select * from " + PARAGRAPH_TABLE_NAME 
                    + " where documentID = ?"
                    + " order by paragraphID asc");
            pstmt.setInt(1, documentID);
            ResultSet rs = pstmt.executeQuery();
            
            List<Paragraph> paragraphs = new ArrayList<Paragraph>();
            
            while (rs.next()) {
                Paragraph paragraph = new Paragraph();
                paragraph.setParagraphID(rs.getInt("paragraphID"));
                paragraph.setDocumentID(rs.getInt("documentID"));
                paragraph.setPageNumber(rs.getInt("pageNumber"));
                paragraph.setContent(rs.getString("content"));
                paragraph.setType(rs.getString("type"));
                
                paragraphs.add(paragraph);
            }
            
            rs.close();
            pstmt.close();
            return paragraphs;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static void deleteParagraphs(Connection conn, int documentID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("delete from " + PARAGRAPH_TABLE_NAME 
                    + " where documentID = ?");
            pstmt.setInt(1, documentID);
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static void deleteParagraph(Connection conn, int paragraphID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("delete from " + PARAGRAPH_TABLE_NAME 
                    + " where paragraphID = ?");
            pstmt.setInt(1, paragraphID);
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static void updateParagraph(Connection conn, Paragraph paragraph) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("update " + PARAGRAPH_TABLE_NAME 
                    + " set content = ?, type = ?"
                    + " where paragraphID = ?");
            pstmt.setString(1, paragraph.getContent());
            pstmt.setString(2, paragraph.getType());
            pstmt.setInt(3, paragraph.getParagraphID());
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
