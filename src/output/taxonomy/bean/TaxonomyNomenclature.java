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
public class TaxonomyNomenclature extends XMLWritable {
    private String name;
    private String rank;
    private String name_info;
    private TaxonomyOtherInfo other_info;
    
    public TaxonomyNomenclature() {
        
    }
    
    public TaxonomyNomenclature(String name, String rank, String name_info) {
        this.name = name;
        this.rank = rank;
        this.name_info = name_info;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRank() {
        return this.rank;
    }
    
    public void setRank(String rank) {
        this.rank = rank;
    }
    
    public String getNameInfo() {
        return this.name_info;
    }
    
    public void setNameInfo(String name_info) {
        this.name_info = name_info;
    }
    
    public void setOtherInfo(TaxonomyOtherInfo otherInfo) {
        this.other_info = otherInfo;
    }
    
    public TaxonomyOtherInfo getOtherInfo() {
        return this.other_info;
    }
    
    @Override
    public void toXML(Document doc, Element parent) {
        Element nomenclature = doc.createElement("nomenclature");
        parent.appendChild(nomenclature);
        
        Element name = doc.createElement("name");
        nomenclature.appendChild(name);
        
        String escapedName = StringEscapeUtils.escapeXml(this.name);
        name.setTextContent(escapedName);
        
        Element rank = doc.createElement("rank");
        nomenclature.appendChild(rank);
        
        String escapedRank = StringEscapeUtils.escapeXml(this.rank);
        rank.setTextContent(escapedRank);
        
        Element name_info = doc.createElement("name_info");
        nomenclature.appendChild(name_info);
        
        String escapedNameInfo = StringEscapeUtils.escapeXml(this.name_info);
        name_info.setTextContent(escapedNameInfo);
        
        if(this.other_info != null) {
            this.other_info.toXML(doc, nomenclature);
        }
    }
}
