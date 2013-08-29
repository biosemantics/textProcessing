/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy.bean;

import org.apache.commons.lang3.StringEscapeUtils;
import output.xml.XMLWritable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomySynonym extends XMLWritable {

    private String synonym;
    
    public TaxonomySynonym(String synonym) {
        this.synonym = synonym;
    }
    
    public TaxonomySynonym() {
        
    }
    
    public String getSynonym() {
        return this.synonym;
    }
    
    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element synonym = doc.createElement("synonym");
        parent.appendChild(synonym);
        
        String escapedText = StringEscapeUtils.escapeXml(this.synonym);
        synonym.setTextContent(escapedText);
    }
}
