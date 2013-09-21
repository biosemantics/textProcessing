/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph;

import common.utils.RegExUtil;
import common.utils.StreamUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import paragraph.bean.ParagraphType;
import paragraph.db.DocumentTable;
import paragraph.db.ParagraphTable;

/**
 *
 * @author iychoi
 */
public class ParagraphLoader {
    private File file;
    
    public ParagraphLoader(File file) {
        if(file == null || !file.exists() || !file.isFile())
            throw new IllegalArgumentException("Cannot find the file");
        
        this.file = file;
    }
    
    public Document loadToDatabase(Connection conn) throws IOException {
        String filedata;
        try {
            filedata = StreamUtil.readFileString(this.file);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
        
        Document document = new Document();
        document.setFilename(file);
        DocumentTable.insertDocument(conn, document);
        
        String[] paragraphs = RegExUtil.splitWithNewLines(filedata);
        
        for(String paragraphString : paragraphs) {
            if(paragraphString != null && !paragraphString.trim().equals("")) {
                // add rows
                String paragraphContent = RegExUtil.getParagraph(paragraphString.trim());
                int paragraphPage = RegExUtil.getPageOfParagraph(paragraphString.trim());
                
                Paragraph paragraph = new Paragraph();
                paragraph.setDocumentID(document.getDocumentID());
                paragraph.setPageNumber(paragraphPage);
                paragraph.setContent(paragraphContent.trim());
                paragraph.setType(ParagraphType.PARAGRAPH_UNKNOWN);
                paragraph.setModManual(0);
                ParagraphTable.insertParagraph(conn, paragraph);
            }
        }
        
        return document;
    }
}
