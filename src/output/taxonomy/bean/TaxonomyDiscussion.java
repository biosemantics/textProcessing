/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy.bean;

import output.xml.XMLWritable;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author iychoi
 */
public class TaxonomyDiscussion extends XMLWritable {
    private List<TaxonomyGenericElement> innerElements;
    
    public TaxonomyDiscussion() {
        this.innerElements = new ArrayList<TaxonomyGenericElement>();
    }
    
    public void addElement(TaxonomyGenericElement elem) {
        this.innerElements.add(elem);
    }
    
    public List<TaxonomyGenericElement> getInnerElements() {
        return this.innerElements;
    }

    @Override
    public void toXML(Document doc, Element parent) {
        Element discussion = doc.createElement("discussion");
        parent.appendChild(discussion);
        
        if(this.innerElements != null) {
            for(TaxonomyGenericElement elem : this.innerElements) {
                elem.toXML(doc, discussion);
            }
        }
    }
}
