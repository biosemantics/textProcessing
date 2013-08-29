/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy.bean;

import org.apache.commons.lang3.StringEscapeUtils;
import output.xml.XMLWritable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomyDescription extends XMLWritable {
    private String description;

    public enum TaxonomyDescriptionType {
        DESCRIPTION_DEFINITION("Definition"),
        DESCRIPTION_DIAGNOSIS("Diagnosis"),
        DESCRIPTION_TYPESPECIES("type_species");
        
        private String name;
        
        TaxonomyDescriptionType(String name) {
            this.name = name;
        }
        
        public String toString() {
            return this.name;
        }
    }
    
    private TaxonomyDescriptionType type;
    
    public TaxonomyDescription(TaxonomyDescriptionType type, String description) {
        this.type = type;
        this.description = description;
    }
    
    public TaxonomyDescription() {
        
    }
    
    public TaxonomyDescriptionType getType() {
        return this.type;
    }
    
    public void setType(TaxonomyDescriptionType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element description = doc.createElement("description");
        parent.appendChild(description);
        
        Attr attr = doc.createAttribute("type");
        attr.setValue(this.type.toString());
        description.setAttributeNode(attr);
        
        String escapedText = StringEscapeUtils.escapeXml(this.description);
        description.setTextContent(escapedText);
    }
}
