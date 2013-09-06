/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.key.bean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import output.xml.XMLWritable;

/**
 *
 * @author iychoi
 */
public class KeyStatement extends XMLWritable {

    private String id;
    private String statement;
    private String determination;
    
    public KeyStatement() {
        
    }
    
    public KeyStatement(String id, String statement, String determination) {
        this.id = id;
        this.statement = statement;
        this.determination = determination;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setStatement(String statement) {
        this.statement = statement;
    }
    
    public String getStatement() {
        return this.statement;
    }
    
    public void setDetermination(String determination) {
        this.determination = determination;
    }
    
    public String getDetermination() {
        return this.determination;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element key_statement = doc.createElement("key_statement");
        parent.appendChild(key_statement);
        
        Element id = doc.createElement("statement_id");
        key_statement.appendChild(id);
        
        id.setTextContent(this.id);
        
        Element statement = doc.createElement("statement");
        key_statement.appendChild(statement);
        
        statement.setTextContent(this.statement);
        
        Element determination = doc.createElement("determination");
        key_statement.appendChild(determination);
        
        determination.setTextContent(this.determination);
    }
    
}
