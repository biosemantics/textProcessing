//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.22 at 03:49:58 PM MST 
//


package xml.taxonomy.beans.treatment;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for treatment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="treatment">
 *   &lt;complexContent>
 *     &lt;extension base="{}treatment">
 *       &lt;redefine>
 *         &lt;complexType name="treatment">
 *           &lt;complexContent>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;sequence>
 *                 &lt;element ref="{}meta"/>
 *                 &lt;element ref="{}taxon_identification" maxOccurs="unbounded"/>
 *               &lt;/sequence>
 *             &lt;/restriction>
 *           &lt;/complexContent>
 *         &lt;/complexType>
 *       &lt;/redefine>
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{}description" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}synonym" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}other_name" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}material" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}discussion" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}taxon_relation_articulation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}habitat_elevation_distribution_or_ecology" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}key_file" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "treatment", propOrder = {
    "descriptionOrTypeOrSynonym"
})
@XmlRootElement
public class Treatment
    extends OriginalTreatment
{

    @XmlElementRefs({
        @XmlElementRef(name = "other_name", type = JAXBElement.class),
        @XmlElementRef(name = "key_file", type = JAXBElement.class),
        @XmlElementRef(name = "synonym", type = JAXBElement.class),
        @XmlElementRef(name = "taxon_relation_articulation", type = TaxonRelationArticulation.class),
        @XmlElementRef(name = "material", type = Material.class),
        @XmlElementRef(name = "habitat_elevation_distribution_or_ecology", type = HabitatElevationDistributionOrEcology.class),
        @XmlElementRef(name = "type", type = Type.class),
        @XmlElementRef(name = "description", type = Description.class),
        @XmlElementRef(name = "discussion", type = Discussion.class)
    })
    protected List<Object> descriptionOrTypeOrSynonym;

    /**
     * Gets the value of the descriptionOrTypeOrSynonym property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descriptionOrTypeOrSynonym property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescriptionOrTypeOrSynonym().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link TaxonRelationArticulation }
     * {@link Material }
     * {@link HabitatElevationDistributionOrEcology }
     * {@link Type }
     * {@link Discussion }
     * {@link Description }
     * 
     * 
     */
    public List<Object> getDescriptionOrTypeOrSynonym() {
        if (descriptionOrTypeOrSynonym == null) {
            descriptionOrTypeOrSynonym = new ArrayList<Object>();
        }
        return this.descriptionOrTypeOrSynonym;
    }

}