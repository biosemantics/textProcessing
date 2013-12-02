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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}source"/>
 *         &lt;element name="processed_by" type="{}processed_by" minOccurs="0"/>
 *         &lt;element ref="{}other_info_on_meta" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "source",
    "processedBy",
    "otherInfoOnMeta"
})
@XmlRootElement(name = "meta")
public class Meta {

    @XmlElement(required = true)
    protected String source;
    @XmlElement(name = "processed_by")
    protected ProcessedBy processedBy;
    @XmlElement(name = "other_info_on_meta")
    protected List<String> otherInfoOnMeta;

    /**
     * put the bibligraphic information of the source paper here
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the processedBy property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessedBy }
     *     
     */
    public ProcessedBy getProcessedBy() {
        return processedBy;
    }

    /**
     * Sets the value of the processedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessedBy }
     *     
     */
    public void setProcessedBy(ProcessedBy value) {
        this.processedBy = value;
    }

    /**
     * Gets the value of the otherInfoOnMeta property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherInfoOnMeta property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherInfoOnMeta().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOtherInfoOnMeta() {
        if (otherInfoOnMeta == null) {
            otherInfoOnMeta = new ArrayList<String>();
        }
        return this.otherInfoOnMeta;
    }

}
