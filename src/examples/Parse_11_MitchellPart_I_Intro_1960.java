/*
package examples;

import common.db.DBUtil;
import common.utils.StringUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import paragraph.db.DocumentTable;
import paragraph.db.ParagraphTable;
import taxonomy.key.KeyTo;
import taxonomy.key.bean.KeyDiscussion;
import taxonomy.key.bean.KeyHeading;
import taxonomy.key.bean.KeyStatement;

public class Parse_11_MitchellPart_I_Intro_1960 {
    
    private int documentID;
    private Document document;
    private List<Paragraph> paragraphs;
    
    private Document loadDocument(int documentID) throws IOException {
        Connection conn = DBUtil.getConnection();
        Document document = DocumentTable.getDocument(conn, documentID);
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return document;
    }
    
    private List<Paragraph> loadParagraphs(Document document) throws IOException {
        Connection conn = DBUtil.getConnection();
        List<Paragraph> paragraphs = ParagraphTable.getParagraphs(conn, document.getDocumentID());

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return paragraphs;
    }
        
    private String removeTrailingDot(String content) {
        int dotStart = content.length();
        for(int i=0;i<content.length();i++) {
            if(content.charAt(content.length() - 1 - i) == '.') {
                dotStart = content.length() - 1 - i;
            } else {
                break;
            }
        }
        
        return content.substring(0, dotStart).trim();
    }
    
    private String removeStartingDot(String content) {
        int dotEnd = 0;
        for(int i=0;i<content.length();i++) {
            if(content.charAt(i) == '.') {
                dotEnd = i + 1;
            } else {
                break;
            }
        }
        
        return content.substring(dotEnd).trim();
    }
    
    private String[] splitKeyStatement(String content) {
        String[] split1 = content.split("\\.{3,}");
        String first = removeTrailingDot(split1[0]);
        String second = removeStartingDot(split1[1]);
        
        String[] split = new String[3];
        
        Pattern p1 = Pattern.compile("^([-–]|\\d+)(\\.)?\\s(.+)$");
        Matcher mt1 = p1.matcher(first);
        if(mt1.find()) {
            split[0] = mt1.group(1).trim();
            split[1] = mt1.group(3).trim();
            split[2] = second;
        }
        
        return split;
    }
    
    private String getPureStatement(String content) {
        Pattern p1 = Pattern.compile("^\\((\\d+)\\)\\.?\\s(.+)$");
        Matcher mt1 = p1.matcher(content);
        if(mt1.find()) {
            return mt1.group(2).trim();
        }
        
        return content;
    }
    
    private KeyDiscussion genKeyDiscussion(String discussion) {
        KeyDiscussion diss = new KeyDiscussion();
        diss.setText(discussion);
        return diss;
    }
    
    private KeyTo genKeyTo(String content) {
        KeyTo keyto = new KeyTo();
        KeyHeading heading = new KeyHeading();
        heading.setHeading(content);
        keyto.setHeading(heading);
        return keyto;
    }
    
    private KeyStatement genKeyStatement(String content) {
        KeyStatement statement = new KeyStatement();
        String[] split = splitKeyStatement(content);
        statement.setId(split[0]);
        statement.setStatement(getPureStatement(split[1]));
        
        try {
            int nextStatementId = Integer.parseInt(split[2].substring(0, 1));
            statement.setNextStatementId(split[2]);
        } catch(Exception ex) {
            statement.setDetermination(split[2]);
        }
        
        return statement;
    }
    
    public void start(int documentID) throws IOException, Exception {
        this.documentID = documentID;
        this.document = loadDocument(this.documentID);
        this.paragraphs = loadParagraphs(document);
        
        List<KeyTo> keytos = new ArrayList<KeyTo>();
        
        KeyTo keyto = null;
        for(Paragraph paragraph : paragraphs) {
            switch(paragraph.getType()) {
                case PARAGRAPH_KEY:
                {
                    keyto = genKeyTo(paragraph.getContent());
                    keytos.add(keyto);
                    break;
                }
                case PARAGRAPH_KEY_DISCUSSION:
                {
                    if(keyto != null) {
                        keyto.addDiscussion(genKeyDiscussion(paragraph.getContent()));
                    }
                    break;
                }
                case PARAGRAPH_KEY_BODY:
                {
                    if(keyto != null) {
                        keyto.addStatement(genKeyStatement(paragraph.getContent()));
                    }
                    break;
                }
                case PARAGRAPH_IGNORE:
                    break;
                default:
                {
                    System.err.println("Skipped - " + paragraph.getTypeString());
                    System.err.println(paragraph.getContent());
                    break;
                }
            }
        }
        
        // to file
        File keyOutDir = new File("key");
        keyOutDir.mkdir();
        int keyfileIndex = 1;
        for(KeyTo key : keytos) {
            File outKeyFile = new File(keyOutDir, StringUtil.getSafeFileName(keyfileIndex + ". " + key.getHeading().getHeading()) + ".xml");
            key.toXML(outKeyFile);
            keyfileIndex++;
        }
    }
    
    public static void main(String[] args) throws Exception {
        Parse_11_MitchellPart_I_Intro_1960 obj = new Parse_11_MitchellPart_I_Intro_1960();
        obj.start(10);
    }
}
*/