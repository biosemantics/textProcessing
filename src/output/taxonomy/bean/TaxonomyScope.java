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
public class TaxonomyScope extends XMLWritable {
    private String scope;
    
    public TaxonomyScope() {
        
    }
    
    public TaxonomyScope(String scope) {
        this.scope = scope;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element scope = doc.createElement("scope");
        parent.appendChild(scope);
        
        String escapedText = StringEscapeUtils.escapeXml(this.scope);
        scope.setTextContent(escapedText);
    }
}
