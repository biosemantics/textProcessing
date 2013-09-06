/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy.bean;

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
    private String hierarchy;
    private String name_info;
    private TaxonomyOtherInfo other_info;
    
    public TaxonomyNomenclature() {
        
    }
    
    public TaxonomyNomenclature(String name, String rank, String name_info, String hierarchy) {
        this.name = name;
        this.rank = rank;
        this.name_info = name_info;
        this.hierarchy = hierarchy;
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
    
    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }
    
    public String getHierarchy() {
        return this.hierarchy;
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
        
        name.setTextContent(this.name);
        
        Element rank = doc.createElement("rank");
        nomenclature.appendChild(rank);
        
        rank.setTextContent(this.rank);
        
        Element hierarchy = doc.createElement("taxon_hierarchy");
        nomenclature.appendChild(hierarchy);
        
        hierarchy.setTextContent(this.hierarchy);
        
        Element name_info = doc.createElement("name_info");
        nomenclature.appendChild(name_info);
        
        name_info.setTextContent(this.name_info);
        
        if(this.other_info != null) {
            this.other_info.toXML(doc, nomenclature);
        }
    }
}
