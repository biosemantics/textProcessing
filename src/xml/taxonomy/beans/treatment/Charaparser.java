//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.22 at 03:49:58 PM MST 
//


package xml.taxonomy.beans.treatment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for charaparser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="charaparser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="charaparser_version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="charaparser_user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="glossary_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="glossary_version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "charaparser", propOrder = {
    "charaparserVersion",
    "charaparserUser",
    "glossaryName",
    "glossaryVersion"
})
public class Charaparser {

    @XmlElement(name = "charaparser_version", required = true)
    protected String charaparserVersion;
    @XmlElement(name = "charaparser_user")
    protected String charaparserUser;
    @XmlElement(name = "glossary_name", required = true)
    protected String glossaryName;
    @XmlElement(name = "glossary_version", required = true)
    protected String glossaryVersion;

    /**
     * Gets the value of the charaparserVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharaparserVersion() {
        return charaparserVersion;
    }

    /**
     * Sets the value of the charaparserVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharaparserVersion(String value) {
        this.charaparserVersion = value;
    }

    /**
     * Gets the value of the charaparserUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharaparserUser() {
        return charaparserUser;
    }

    /**
     * Sets the value of the charaparserUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharaparserUser(String value) {
        this.charaparserUser = value;
    }

    /**
     * Gets the value of the glossaryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlossaryName() {
        return glossaryName;
    }

    /**
     * Sets the value of the glossaryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlossaryName(String value) {
        this.glossaryName = value;
    }

    /**
     * Gets the value of the glossaryVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlossaryVersion() {
        return glossaryVersion;
    }

    /**
     * Sets the value of the glossaryVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlossaryVersion(String value) {
        this.glossaryVersion = value;
    }

}
