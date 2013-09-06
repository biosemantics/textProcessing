/*
 * Taxonomy Bean
 */
package output.taxonomy;

import java.io.File;
import output.taxonomy.bean.TaxonomyDiscussion;
import output.taxonomy.bean.TaxonomyScope;
import output.taxonomy.bean.TaxonomyDescription;
import output.taxonomy.bean.TaxonomyMeta;
import output.taxonomy.bean.TaxonomyKeywords;
import output.taxonomy.bean.TaxonomyGenericElement;
import output.taxonomy.bean.TaxonomySynonym;
import output.taxonomy.bean.TaxonomyNomenclature;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import output.taxonomy.bean.TaxonomyKeyFile;
import output.taxonomy.bean.TaxonomyTypeSpecies;

/**
 *
 * @author iychoi
 */
public class Taxonomy {
    private TaxonomyMeta meta;
    private TaxonomyNomenclature nomenclature;
    private List<TaxonomySynonym> synonyms;
    private TaxonomyScope scope;
    private TaxonomyKeywords keywords;
    private List<TaxonomyTypeSpecies> typeSpecies;
    private List<TaxonomyDescription> descriptions;
    private TaxonomyDiscussion discussion;
    private List<TaxonomyDiscussion> discussionNonTitled;
    private List<TaxonomyGenericElement> innerElements;
    private List<TaxonomyKeyFile> keyFiles;
    
    public Taxonomy() {
        this.synonyms = new ArrayList<TaxonomySynonym>();
        this.typeSpecies = new ArrayList<TaxonomyTypeSpecies>();
        this.descriptions = new ArrayList<TaxonomyDescription>();
        this.discussionNonTitled = new ArrayList<TaxonomyDiscussion>();
        this.innerElements = new ArrayList<TaxonomyGenericElement>();
        this.keyFiles = new ArrayList<TaxonomyKeyFile>();
    }
    
    public void setMeta(TaxonomyMeta meta) {
        this.meta = meta;
    }
    
    public TaxonomyMeta getMeta() {
        return this.meta;
    }
    
    public void setNomenclture(TaxonomyNomenclature nomenclature) {
        this.nomenclature = nomenclature;
    }
    
    public TaxonomyNomenclature getNomenclature() {
        return this.nomenclature;
    }
    
    public void addSynonym(TaxonomySynonym synonym) {
        this.synonyms.add(synonym);
    }
    
    public List<TaxonomySynonym> getSynonyms() {
        return this.synonyms;
    }
    
    public void setScope(TaxonomyScope scope) {
        this.scope = scope;
    }
    
    public TaxonomyScope getScope() {
        return this.scope;
    }
    
    public void setKeywords(TaxonomyKeywords keywords) {
        this.keywords = keywords;
    }
    
    public TaxonomyKeywords getKeywords() {
        return this.keywords;
    }
    
    public void addDescription(TaxonomyDescription description) {
        this.descriptions.add(description);
    }
    
    public List<TaxonomyDescription> getDescriptions() {
        return this.descriptions;
    }
    
    public void addTypeSpecies(TaxonomyTypeSpecies typeSpecies) {
        this.typeSpecies.add(typeSpecies);
    }
    
    public List<TaxonomyTypeSpecies> getTypeSpecies() {
        return this.typeSpecies;
    }
    
    public void setDiscussion(TaxonomyDiscussion discussion) {
        this.discussion = discussion;
    }
    
    public TaxonomyDiscussion getDiscussion() {
        return this.discussion;
    }
    
    public void addDiscussionNonTitled(TaxonomyDiscussion discussion) {
        this.discussionNonTitled.add(discussion);
    }
    
    public List<TaxonomyDiscussion> getDiscussionNonTitled() {
        return this.discussionNonTitled;
    }
    
    public void addElement(TaxonomyGenericElement elem) {
        this.innerElements.add(elem);
    }
    
    public List<TaxonomyGenericElement> getInnerElements() {
        return this.innerElements;
    }
    
    public void addKeyFile(TaxonomyKeyFile keyFile) {
        this.keyFiles.add(keyFile);
    }
    
    public List<TaxonomyKeyFile> getKeyFiles() {
        return this.keyFiles;
    }
    
    public void toXML(File file) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        toXML(doc);
        
        // write xml to file    
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
    
    public void toXML(Document doc) {
        Element treatment = doc.createElement("treatment");
        doc.appendChild(treatment);
        
        if(this.meta != null) {
            this.meta.toXML(doc, treatment);
        }
        
        if(this.nomenclature != null) {
            this.nomenclature.toXML(doc, treatment);
        }
        
        if(this.synonyms != null) {
            for(TaxonomySynonym synonym : this.synonyms) {
                synonym.toXML(doc, treatment);
            }
        }
        
        if(this.typeSpecies != null) {
            for(TaxonomyTypeSpecies typeSpecies : this.typeSpecies) {
                typeSpecies.toXML(doc, treatment);
            }
        }
        
        if(this.scope != null) {
            this.scope.toXML(doc, treatment);
        }
        
        if(this.keywords != null) {
            this.keywords.toXML(doc, treatment);
        }
        
        if(this.descriptions != null) {
            for(TaxonomyDescription description : this.descriptions) {
                description.toXML(doc, treatment);
            }
        }
        
        if(this.innerElements != null) {
            for(TaxonomyGenericElement elem : this.innerElements) {
                elem.toXML(doc, treatment);
            }
        }
        
        if(this.discussionNonTitled != null) {
            for(TaxonomyDiscussion discussion : this.discussionNonTitled) {
                discussion.toXML(doc, treatment);
            }
        }
        
        if(this.discussion != null) {
            this.discussion.toXML(doc, treatment);
        }
        
        if(this.keyFiles != null) {
            for(TaxonomyKeyFile keyfile : this.keyFiles) {
                keyfile.toXML(doc, treatment);
            }
        }
    }
}
