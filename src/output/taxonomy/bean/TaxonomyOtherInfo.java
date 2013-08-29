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
public class TaxonomyOtherInfo extends XMLWritable {
    private String otherInfo;
    
    public TaxonomyOtherInfo() {
        
    }
    
    public TaxonomyOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
    
    public String getOtherInfo() {
        return this.otherInfo;
    }
    
    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        Element otherInfo = doc.createElement("other_info");
        parent.appendChild(otherInfo);
        
        String escapedText = StringEscapeUtils.escapeXml(this.otherInfo);
        otherInfo.setTextContent(escapedText);
    }
}
