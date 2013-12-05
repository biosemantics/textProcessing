/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.taxonomy;

import common.db.DBUtil;
import common.utils.StringUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBElement;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import paragraph.db.DocumentTable;
import paragraph.db.ParagraphTable;
import xml.taxonomy.beans.key.Determination;
import xml.taxonomy.beans.key.Key;
import xml.taxonomy.beans.key.KeyStatement;
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
    
    public static String[] splitTitleBody(String content) {
        int divPosition = 0;
        boolean braceStart = false;
        for(int i=0;i<content.length();i++) {
            if(content.charAt(i) == '(') {
                braceStart = true;
            } else if(content.charAt(i) == ')') {
                braceStart = false;
            } else if(content.charAt(i) == ':' || content.charAt(i) == '.' || content.charAt(i) == '-') {
                // possible position
                if(!braceStart) {
                    divPosition = i;
                    break;
                }
            }
        }

        String[] split = new String[2];
        split[0] = content.substring(0, divPosition).trim();
        split[1] = content.substring(divPosition+1).trim();
        return split;
    }
    
    public static Key createNewKey(String title) {
        Key key = KeyFactory.createKey();
        key.setKeyHeading(title);
        return key;
    }
    
    public static void addNewMeta(Key key, String processor_name, String source) {
        xml.taxonomy.beans.key.Meta meta = KeyFactory.createMeta();
        meta.setSource(source);
        
        xml.taxonomy.beans.key.ProcessedBy processedby = KeyFactory.createProcessedBy();
        xml.taxonomy.beans.key.Processor processor = KeyFactory.createProcessor();
        
        processor.setProcessType("format conversion");
        processor.setValue(processor_name);
        
        processedby.getProcessorOrCharaparser().add(processor);
        meta.setProcessedBy(processedby);
        key.setMeta(meta);
    }
    
    public static void addNewDiscussion(Key key, String content) {
        JAXBElement<String> discuss = KeyFactory.createDiscussion(content);
        key.getDiscussionOrKeyHeadOrKeyStatement().add(discuss);
    }

    public static void addKeyStatement(Key key, String id, String statementString, String destFilename, String dest) {
        KeyStatement statement = KeyFactory.createKeyStatement();
        statement.setStatementId(id);
        statement.setStatement(statementString);
        try {
            int nextStatementId = Integer.parseInt(dest.substring(0, 1));
            statement.setNextStatementId(dest);
        } catch(Exception ex) {
            Determination determination = KeyFactory.createDetermination();
            determination.setValue(dest);
            determination.setFileName(destFilename);
        }
        
        key.getDiscussionOrKeyHeadOrKeyStatement().add(statement);
    }
    
    public static void addKeyFile(Taxonomy taxonomy, String name) {
        JAXBElement<String> keyfile = TreatmentFactory.createKeyFile(name);
        taxonomy.getTreatment().getDescriptionOrTypeOrSynonym().add(keyfile);
    }
    
    public static void addKeyStatement(Key key, String id, String statementString, String dest) {
        KeyStatement statement = KeyFactory.createKeyStatement();
        statement.setStatementId(id);
        statement.setStatement(statementString);
        try {
            int nextStatementId = Integer.parseInt(dest.substring(0, 1));
            statement.setNextStatementId(dest);
        } catch(Exception ex) {
            Determination determination = KeyFactory.createDetermination();
            determination.setValue(dest);
            statement.setDetermination(determination);
        }
        
        key.getDiscussionOrKeyHeadOrKeyStatement().add(statement);
    }
    
    public static String[] splitKeyStatement(String content) {
        String[] split1 = content.split("\\.{3,}");
        String first = StringUtil.removeTrailingDot(split1[0]);
        String second = StringUtil.removeStartingDot(split1[1]);
        
        String[] split = new String[3];
        
        Pattern p1 = Pattern.compile("^([-–]|\\d+)(\\.)?\\s(.+)$");
        Matcher mt1 = p1.matcher(first);
        if(mt1.find()) {
            split[0] = mt1.group(1).trim();
            split[1] = mt1.group(3).trim();
            split[2] = second;
        }
        
        return split;
    }
    
    public static String getFirstRank(String name) throws IOException {
        String[] name_parts = name.split("\\s");
        if(name_parts.length > 1) {
            if(Rank.checkRank(name_parts[0])) {
                return Rank.findRank(name_parts[0]);
            }
        }
        return null;
    }
    
    public static String removeAllRank(String name) {
        String[] name_parts = name.split("\\s");
        String newName = "";
        for(String name_part : name_parts) {
            if(Rank.checkRank(name_part)) {
                // is rank
            } else {
                if (!newName.equals("")) {
                    newName += " ";
                }
                newName += name_part;
            }
        }
        return newName;
    }
}
