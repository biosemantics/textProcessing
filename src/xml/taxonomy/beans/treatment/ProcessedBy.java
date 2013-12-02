//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.22 at 03:49:58 PM MST 
//


package xml.taxonomy.beans.treatment;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for processed_by complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="processed_by">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="processor" type="{}processor"/>
 *         &lt;element name="charaparser" type="{}charaparser"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processed_by", propOrder = {
    "processorOrCharaparser"
})
public class ProcessedBy {

    @XmlElements({
        @XmlElement(name = "processor", type = Processor.class),
        @XmlElement(name = "charaparser", type = Charaparser.class)
    })
    protected List<Object> processorOrCharaparser;

    /**
     * Gets the value of the processorOrCharaparser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processorOrCharaparser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessorOrCharaparser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Processor }
     * {@link Charaparser }
     * 
     * 
     */
    public List<Object> getProcessorOrCharaparser() {
        if (processorOrCharaparser == null) {
            processorOrCharaparser = new ArrayList<Object>();
        }
        return this.processorOrCharaparser;
    }

}
