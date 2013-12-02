/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.taxonomy;

import common.db.DBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.xml.bind.JAXBElement;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import paragraph.db.DocumentTable;
import paragraph.db.ParagraphTable;
import xml.taxonomy.beans.treatment.Description;
import xml.taxonomy.beans.treatment.Discussion;
import xml.taxonomy.beans.treatment.HabitatElevationDistributionOrEcology;
import xml.taxonomy.beans.treatment.Material;
import xml.taxonomy.beans.treatment.Meta;
import xml.taxonomy.beans.treatment.ProcessedBy;
import xml.taxonomy.beans.treatment.Processor;
import xml.taxonomy.beans.treatment.TaxonIdentification;
import xml.taxonomy.beans.treatment.TaxonRelationArticulation;
import xml.taxonomy.beans.treatment.Treatment;
import xml.taxonomy.beans.treatment.Type;

/**
 *
 * @author iychoi
 */
public class TaxonXMLCommon {
    private static xml.taxonomy.beans.key.ObjectFactory KeyFactory;
    private static xml.taxonomy.beans.treatment.ObjectFactory TreatmentFactory;
    
    static {
        KeyFactory = new xml.taxonomy.beans.key.ObjectFactory();
        TreatmentFactory = new xml.taxonomy.beans.treatment.ObjectFactory();
    }
    
    public static Document loadDocument(int documentID) throws IOException {
        Connection conn = DBUtil.getConnection();
        Document document = DocumentTable.getDocument(conn, documentID);
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return document;
    }
    
    public static List<Paragraph> loadParagraphs(Document document) throws IOException {
        Connection conn = DBUtil.getConnection();
        List<Paragraph> paragraphs = ParagraphTable.getParagraphs(conn, document.getDocumentID());

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return paragraphs;
    }
    
    public static Taxonomy createNewTaxonomy() {
        return new Taxonomy(createNewTreatment());
    }
    
    private static Treatment createNewTreatment() {
        return TreatmentFactory.createTreatment();
    }
    
    public static void addNewMeta(Taxonomy taxonomy, String processor_name, String source) {
        Meta meta = TreatmentFactory.createMeta();
        meta.setSource(source);
        
        ProcessedBy processedby = TreatmentFactory.createProcessedBy();
        Processor processor = TreatmentFactory.createProcessor();
        
        processor.setProcessType("format conversion");
        processor.setValue(processor_name);
        
        processedby.getProcessorOrCharaparser().add(processor);
        meta.setProcessedBy(processedby);
        taxonomy.getTreatment().setMeta(meta);
    }
    
    public static void addNewIdentification(Taxonomy taxonomy, Hierarchy hierarchy, HierarchyEntry hierarchy_entry, String name_info) throws Exception {
        HierarchyEntry completeHierarchy = hierarchy.getCompleteHierarchyNCA(hierarchy_entry);
        String newHierarchy = completeHierarchy.getHierarchyString();
        
        String[] name_parts = completeHierarchy.getNames();
        String[] rank_parts = completeHierarchy.getRanks();
        String[] authority_parts = completeHierarchy.getAuthorities();

        TaxonIdentification identification = TreatmentFactory.createTaxonIdentification();

        for (int i = 0; i < name_parts.length; i++) {
            if (name_parts[i] != null) {
                JAXBElement<String> newName = null;
                JAXBElement<String> newAuthority = null;
                if (rank_parts[i].toLowerCase().equals("order")) {
                    newName = TreatmentFactory.createOrderName(name_parts[i]);
                    newAuthority = TreatmentFactory.createOrderAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("suborder")) {
                    newName = TreatmentFactory.createSuborderName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSuborderAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("superfamily")) {
                    newName = TreatmentFactory.createSuperfamilyName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSuperfamilyAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("family")) {
                    newName = TreatmentFactory.createFamilyName(name_parts[i]);
                    newAuthority = TreatmentFactory.createFamilyAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("subfamily")) {
                    newName = TreatmentFactory.createSubfamilyName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSubfamilyAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("tribe")) {
                    newName = TreatmentFactory.createTribeName(name_parts[i]);
                    newAuthority = TreatmentFactory.createTribeAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("subtribe")) {
                    newName = TreatmentFactory.createSubtribeName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSubtribeAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("genus")) {
                    newName = TreatmentFactory.createGenusName(name_parts[i]);
                    newAuthority = TreatmentFactory.createGenusAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("genus_group")) {
                    newName = TreatmentFactory.createGenusGroupName(name_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("subgenus")) {
                    newName = TreatmentFactory.createSubgenusName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSubgenusAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("section")) {
                    newName = TreatmentFactory.createSectionName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSectionAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("subsection")) {
                    newName = TreatmentFactory.createSubsectionName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSubsectionAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("series")) {
                    newName = TreatmentFactory.createSeriesName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSeriesAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("species")) {
                    newName = TreatmentFactory.createSpeciesName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSpeciesAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("species_group")) {
                    newName = TreatmentFactory.createSpeciesGroupName(name_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("subspecies")) {
                    newName = TreatmentFactory.createSubspeciesName(name_parts[i]);
                    newAuthority = TreatmentFactory.createSubspeciesAuthority(authority_parts[i]);
                } else if (rank_parts[i].toLowerCase().equals("variety")) {
                    newName = TreatmentFactory.createVarietyName(name_parts[i]);
                    newAuthority = TreatmentFactory.createVarietyAuthority(authority_parts[i]);
                } else {
                    throw new Exception("Unknown rank");
                }

                identification.getOrderNameOrOrderAuthorityOrSuborderName().add(newName);

                if (newAuthority != null && newAuthority.getValue() != null) {
                    identification.getOrderNameOrOrderAuthorityOrSuborderName().add(newAuthority);
                }
            }
        }
        
        identification.getTaxonHierarchy().add(newHierarchy);
        
        if (name_info != null) {
            JAXBElement<String> newNameInfo = TreatmentFactory.createOtherInfoOnName(name_info);
            identification.getOrderNameOrOrderAuthorityOrSuborderName().add(newNameInfo);
        }
        
        // add to hierarchy
        hierarchy.addEntry(completeHierarchy);
        
        taxonomy.getTreatment().getTaxonIdentification().add(identification);
    }
    
    public static void addNewOtherInfoOnName(Taxonomy taxonomy, String content) {
        JAXBElement<String> otherInfo = TreatmentFactory.createOtherInfoOnName(content);
        if(!taxonomy.getTreatment().getTaxonIdentification().isEmpty()) {
            taxonomy.getTreatment().getTaxonIdentification().get(0).getOrderNameOrOrderAuthorityOrSuborderName().add(otherInfo);
        }
    }
    
    public static void addNewType(Taxonomy taxonomy, String title, String content) {
        Type type = TreatmentFactory.createType();
        type.setType(convertSubtitle(title));
        type.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(type);
    }
    
    public static void addNewMaterial(Taxonomy taxonomy, String title, String content) {
        Material material = TreatmentFactory.createMaterial();
        material.setType(convertSubtitle(title));
        material.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(material);
    }
    
    public static void addNewArticulation(Taxonomy taxonomy, String title, String content) {
        TaxonRelationArticulation articulation = TreatmentFactory.createTaxonRelationArticulation();
        articulation.setType(convertSubtitle(title));
        articulation.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(articulation);
    }
    
    public static void addNewHabitatElevationDistribution(Taxonomy taxonomy, String title, String content) {
        HabitatElevationDistributionOrEcology habitat = TreatmentFactory.createHabitatElevationDistributionOrEcology();
        habitat.setType(convertSubtitle(title));
        habitat.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(habitat);
    }
    
    public static void addNewDiscussion(Taxonomy taxonomy, String title, String content) {
        Discussion discuss = TreatmentFactory.createDiscussion();
        discuss.setType(convertSubtitle(title));
        discuss.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(discuss);
    }
    
    public static void addNewDiscussion(Taxonomy taxonomy, String content) {
        Discussion discuss = TreatmentFactory.createDiscussion();
        discuss.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(discuss);
    }
    
    public static void addNewSynonym(Taxonomy taxonomy, String content) {
        JAXBElement<String> synonym = TreatmentFactory.createSynonym(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(synonym);
    }
    
    public static void addNewDescription(Taxonomy taxonomy, String title, String content) {
        Description desc = TreatmentFactory.createDescription();
        desc.setType(convertSubtitle(title));
        desc.setValue(content);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(desc);
    }
    
    private static String convertSubtitle(String type) {
        if(type == null) {
            return null;
        }
        
        String newTitle = type.toLowerCase().trim();
        if(newTitle.endsWith(".")) {
            newTitle = newTitle.substring(0, newTitle.length()-1);
        }
        return newTitle;
    }
}
