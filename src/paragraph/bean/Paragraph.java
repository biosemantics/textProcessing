/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph.bean;

/**
 *
 * @author iychoi
 */
public class Paragraph {
    private int paragraphID;
    private int documentID;
    private int pageNumber;
    private String content;
    private ParagraphType type;
    private int confirmed;
    
    public Paragraph() {
        
    }
    
    public void setParagraphID(int paragraphID) {
        this.paragraphID = paragraphID;
    }
    
    public int getParagraphID() {
        return this.paragraphID;
    }
    
    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }
    
    public int getDocumentID() {
        return this.documentID;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public int getPageNumber() {
        return this.pageNumber;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setType(String type) {
        this.type = ParagraphType.valueOf(type);
    }
    
    public void setType(ParagraphType type) {
        this.type = type;
    }
    
    public String getTypeString() {
        return this.type.name();
    }
    
    public ParagraphType getType() {
        return this.type;
    }

    public int getConfirmed() {
        return this.confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }
}
