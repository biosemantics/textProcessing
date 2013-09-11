/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph.bean;

/**
 *
 * @author iychoi
 */
public class RegEx {
    private int regexID;
    private int documentID;
    private String regex;
    private String description;
    private ParagraphType paragraphType;
    private int priority;
    
    public RegEx() {
        
    }
    
    public void setRegexID(int regexID) {
        this.regexID = regexID;
    }
    
    public int getRegexID() {
        return this.regexID;
    }
    
    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }
    
    public int getDocumentID() {
        return this.documentID;
    }
    
    public void setRegex(String regex) {
        this.regex = regex;
    }
    
    public String getRegex() {
        return this.regex;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setParagraphType(String paragraphType) {
        this.paragraphType = ParagraphType.valueOf(paragraphType);
    }
    
    public void setParagraphType(ParagraphType paragraphType) {
        this.paragraphType = paragraphType;
    }
    
    public String getParagraphTypeString() {
        return this.paragraphType.name();
    }
    
    public ParagraphType getParagraphType() {
        return this.paragraphType;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        return this.priority;
    }
}
