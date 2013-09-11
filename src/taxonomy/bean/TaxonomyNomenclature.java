/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonomy.bean;

import java.util.ArrayList;
import java.util.List;
import common.xml.XMLWritable;
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
    private List<TaxonomyOtherInfo> other_infos;
    
    public TaxonomyNomenclature() {
        this.other_infos = new ArrayList<TaxonomyOtherInfo>();
    }
    
    public TaxonomyNomenclature(String name, String rank, String name_info, String hierarchy) {
        this.name = name;
        this.rank = rank;
        this.name_info = name_info;
        this.hierarchy = hierarchy;
        
        this.other_infos = new ArrayList<TaxonomyOtherInfo>();
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
    
    public void addOtherInfo(TaxonomyOtherInfo otherInfo) {
        this.other_infos.add(otherInfo);
    }
    
    public List<TaxonomyOtherInfo> getOtherInfos() {
        return this.other_infos;
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
        
        if(this.other_infos != null) {
            for(TaxonomyOtherInfo other_info : this.other_infos) {
                other_info.toXML(doc, nomenclature);
            }
        }
    }
}
