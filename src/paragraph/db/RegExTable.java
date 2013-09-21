/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import paragraph.bean.RegEx;

/**
 *
 * @author iychoi
 */
public class RegExTable {
    public static final String REGEX_TABLE_NAME = "tblRegEx";
    
    public static void createRegExTable(Connection conn, boolean bClean) throws IOException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            if (bClean) {
                stmt.execute("drop table if exists " + REGEX_TABLE_NAME);
            }
            stmt.execute("create table if not exists " + REGEX_TABLE_NAME
                    + " (regexID bigint not null primary key auto_increment, "
                    + "documentID bigint not null, "
                    + "regex text(1000) not null, "
                    + "description text(1000) not null, "
                    + "paragraphType text(1000) not null, "
                    + "priority bigint default 0)");
            
            stmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static void insertRegEx(Connection conn, RegEx regex) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("insert into " + REGEX_TABLE_NAME 
                    + " (documentID, regex, description, paragraphType, priority) values (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, regex.getDocumentID());
            pstmt.setString(2, regex.getRegex());
            pstmt.setString(3, regex.getDescription());
            pstmt.setString(4, regex.getParagraphTypeString());
            pstmt.setInt(5, regex.getPriority());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                regex.setRegexID(rs.getInt(1));
            } else {
                throw new IOException("there's no generated id");
            }
            
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static RegEx getRegEx(Connection conn, int regexID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("select * from " + REGEX_TABLE_NAME 
                    + " where regexID = ?");
            pstmt.setInt(1, regexID);
            ResultSet rs = pstmt.executeQuery();
            RegEx regex = null;
            if (rs.next()) {
                regex = new RegEx();
                regex.setRegexID(rs.getInt("regexID"));
                regex.setDocumentID(rs.getInt("documentID"));
                regex.setRegex(rs.getString("regex"));
                regex.setDescription(rs.getString("description"));
                regex.setParagraphType(rs.getString("paragraphType"));
                regex.setPriority(rs.getInt("priority"));
            }
            
            rs.close();
            pstmt.close();
            return regex;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static int getMaxRegExPriority(Connection conn, int documentID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("select max(priority) as priority_max from " + REGEX_TABLE_NAME 
                    + " where documentID = ?");
            pstmt.setInt(1, documentID);
            ResultSet rs = pstmt.executeQuery();
            int max = 0;
            if (rs.next()) {
                max = rs.getInt("priority_max");
            }
            
            rs.close();
            pstmt.close();
            return max;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static List<RegEx> getRegExs(Connection conn) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("select * from " + REGEX_TABLE_NAME 
                    + " order by documentID asc, priority asc");
            ResultSet rs = pstmt.executeQuery();
            
            List<RegEx> regexs = new ArrayList<RegEx>();
            
            while (rs.next()) {
                RegEx regex = new RegEx();
                regex.setRegexID(rs.getInt("regexID"));
                regex.setDocumentID(rs.getInt("documentID"));
                regex.setRegex(rs.getString("regex"));
                regex.setDescription(rs.getString("description"));
                regex.setParagraphType(rs.getString("paragraphType"));
                regex.setPriority(rs.getInt("priority"));
                
                regexs.add(regex);
            }
            
            rs.close();
            pstmt.close();
            return regexs;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static List<RegEx> getRegExs(Connection conn, int documentID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("select * from " + REGEX_TABLE_NAME 
                    + " where documentID = ?"
                    + " order by priority asc");
            pstmt.setInt(1, documentID);
            ResultSet rs = pstmt.executeQuery();
            
            List<RegEx> regexs = new ArrayList<RegEx>();
            
            while (rs.next()) {
                RegEx regex = new RegEx();
                regex.setRegexID(rs.getInt("regexID"));
                regex.setDocumentID(rs.getInt("documentID"));
                regex.setRegex(rs.getString("regex"));
                regex.setDescription(rs.getString("description"));
                regex.setParagraphType(rs.getString("paragraphType"));
                regex.setPriority(rs.getInt("priority"));
                
                regexs.add(regex);
            }
            
            rs.close();
            pstmt.close();
            return regexs;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static void deleteRegExs(Connection conn, int documentID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("delete from " + REGEX_TABLE_NAME 
                    + " where documentID = ?");
            pstmt.setInt(1, documentID);
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static void deleteRegEx(Connection conn, int regexID) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("delete from " + REGEX_TABLE_NAME 
                    + " where regexID = ?");
            pstmt.setInt(1, regexID);
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public static void updateRegEx(Connection conn, RegEx regex) throws IOException {
        try {
            PreparedStatement pstmt = conn.prepareStatement("update " + REGEX_TABLE_NAME 
                    + " set regex = ?, description = ?, paragraphType = ?, priority = ?"
                    + " where regexID = ?");
            pstmt.setString(1, regex.getRegex());
            pstmt.setString(2, regex.getDescription());
            pstmt.setString(3, regex.getParagraphTypeString());
            pstmt.setInt(4, regex.getPriority());
            pstmt.setInt(5, regex.getRegexID());
            pstmt.executeUpdate();
            
            pstmt.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
