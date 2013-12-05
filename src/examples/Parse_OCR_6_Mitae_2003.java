/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples;

import common.utils.StringUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import xml.taxonomy.Hierarchy;
import xml.taxonomy.HierarchyEntry;
import xml.taxonomy.Rank;
import xml.taxonomy.RankRelation;
import xml.taxonomy.TaxonXMLCommon;
import xml.taxonomy.Taxonomy;
import xml.taxonomy.beans.key.Key;
import xml.taxonomy.beans.treatment.Treatment;

/**
 *
 * @author iychoi
 */
public class Parse_OCR_6_Mitae_2003 {

    private int documentID;
    private Document document;
    private List<Paragraph> paragraphs;
    private String processor_name;

    public Parse_OCR_6_Mitae_2003(String processor_name) {
        this.processor_name = processor_name;
    }

    private String getTaxonName(String nameInfo) {
        String newTaxon = nameInfo;
        int idxBrace = newTaxon.indexOf("[");
        if (idxBrace > 0) {
            newTaxon = newTaxon.substring(0, idxBrace).trim();
        }

        /*
         int idxDot = nameInfo.indexOf(".");
         if(idxDot > 0) {
         newTaxon = newTaxon.substring(idxDot + 1).trim();
         }
         */

        int idxNewSp = newTaxon.indexOf(" NEW SPECIES");
        if (idxNewSp > 0) {
            newTaxon = newTaxon.substring(0, idxNewSp).trim();
        }

        int idxNewSubg = newTaxon.indexOf(" NEW SUBGENUS");
        if (idxNewSubg > 0) {
            newTaxon = newTaxon.substring(0, idxNewSubg).trim();
        }

        int idxNewCb = newTaxon.indexOf(" NEW COMBINATION");
        if (idxNewCb > 0) {
            newTaxon = newTaxon.substring(0, idxNewCb).trim();
        }
        
        int idxSSPNov = newTaxon.indexOf(" ssp. nov.");
        if (idxSSPNov > 0) {
            newTaxon = newTaxon.substring(0, idxSSPNov).trim();
        }

        Pattern p1 = Pattern.compile("^(.+)?, \\d+$");
        Matcher mt1 = p1.matcher(newTaxon);
        if (mt1.find()) {
            return mt1.group(1);
        }

        return newTaxon;
    }

    private String getPureName(String name) {
        String authority = getAuthority(name);
        if (authority == null) {
            return name;
        }
        int idx = name.indexOf(authority);
        if (idx >= 0) {
            return name.substring(0, idx).trim();
        }

        System.out.println("No purename found - " + name);
        return null;
    }

    private String getAuthority(String name) {
        String name2 = name;
        if (name2.endsWith(", sp. nov.")) {
            int lastIdx = name2.indexOf(", sp. nov.");
            if (lastIdx >= 0) {
                name2 = name2.substring(0, lastIdx);
            }
        } else if (name2.endsWith(" n. sp.")) {
            System.out.println("n. sp. found");
            int lastIdx = name2.indexOf(" n. sp.");
            if (lastIdx >= 0) {
                name2 = name2.substring(0, lastIdx);
            }
        } else if (name.endsWith(" n. subsp.")) {
            System.out.println("n. subsp. found");
            int lastIdx = name2.indexOf(" n. subsp.");
            if (lastIdx >= 0) {
                name2 = name2.substring(0, lastIdx);
            }
        } else if (name.endsWith(" var.")) {
            System.out.println("var. found");
            int lastIdx = name2.indexOf(" var.");
            if (lastIdx >= 0) {
                name2 = name2.substring(0, lastIdx);
            }
        }

        int startPos = name2.length() - 1;

        int commaIdx = name2.indexOf(",");
        if (commaIdx >= 0) {
            startPos = Math.min(startPos, commaIdx - 1);
        }

        int etIdx = name2.indexOf(" et ");
        if (etIdx >= 0) {
            startPos = Math.min(startPos, etIdx - 1);
        }
        
        int andIdx = name2.indexOf(" and ");
        if (andIdx >= 0) {
            startPos = Math.min(startPos, andIdx - 1);
        }

        int spacePos = 0;
        for (int i = startPos; i >= 0; i--) {
            if (name2.charAt(i) == ' ') {
                if (i >= 1 && name2.charAt(i - 1) != '.') {
                    spacePos = i;
                    break;
                }
            }
        }

        String authority = name2.substring(spacePos + 1).trim();
        if ((authority.charAt(0) >= 'A' && authority.charAt(0) <= 'Z')
                || authority.charAt(0) == '(') {
            return authority;
        } else {
            System.out.println("No authority found - " + name2);
            return null;
        }
    }

    private String removeBrace(String name) {
        if (name.startsWith("(") && name.endsWith(")")) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }

    private HierarchyEntry getHierarchyEntry(String name, String rank) throws IOException {
        List<String> new_name_parts = new ArrayList<String>();
        List<String> new_rank_parts = new ArrayList<String>();
        List<String> new_auth_parts = new ArrayList<String>();

        List<RankRelation> rank_relations = new ArrayList<RankRelation>();
        System.out.println(name);
        rank_relations.add(new RankRelation("Family", "Subfamily"));
        rank_relations.add(new RankRelation("Subfamily", "Genus"));
        rank_relations.add(new RankRelation("Genus", "Species"));
        rank_relations.add(new RankRelation("Species", "Subspecies"));
        rank_relations.add(new RankRelation("Subgenus", "Variety"));

        String firstPart = null;
        boolean hasSecondPart = false;
        if (name.indexOf(" ssp. ") >= 0 || name.indexOf(" var. ") >= 0) {
            // split into two
            int idx = 0;
            int len = 0;
            String rnk = null;
            if (name.indexOf(" ssp. ") >= 0) {
                idx = name.indexOf(" ssp. ");
                len = 6;
                rnk = "Subspecies";
            } else if (name.indexOf(" var. ") >= 0) {
                idx = name.indexOf(" var. ");
                len = 6;
                rnk = "Variety";
            }

            firstPart = name.substring(0, idx).trim();
            String secondPart = name.substring(idx + 6).trim();

            String secondAuth = getAuthority(secondPart);
            String secondName = getPureName(secondPart);
            System.out.println("secondName : " + secondName);
            if (secondName.split("\\s").length > 1) {
                throw new IOException("second part has more than 2 parts : " + secondName);
            }

            new_name_parts.add(0, secondName);
            new_rank_parts.add(0, rnk);
            new_auth_parts.add(0, secondAuth);
            hasSecondPart = true;
        } else if (name.indexOf(" n. sp.") >= 0 || name.indexOf(" n. subsp.") >= 0 || name.indexOf(" var.") >= 0) {
            int idx = 0;
            if (name.indexOf(" n. sp.") >= 0) {
                idx = name.indexOf(" n. sp.");
            } else if (name.indexOf(" n. subsp.") >= 0) {
                idx = name.indexOf(" n. subsp.");
            } else if (name.indexOf(" var.") >= 0) {
                idx = name.indexOf(" var.");
            }
            firstPart = name.substring(0, idx).trim();
        } else {
            firstPart = name;
        }

        if (firstPart.indexOf(" ") < 0) {
            // not found
            String pure_name = removeBrace(firstPart);
            new_name_parts.add(0, pure_name);
            new_rank_parts.add(0, rank);
            new_auth_parts.add(0, null);
        } else {
            String firstAuth = getAuthority(firstPart);
            String firstName = getPureName(firstPart);

            String[] name_parts = firstName.split("\\s");
            for (int i = name_parts.length - 1; i >= 0; i--) {
                String pure_name = removeBrace(name_parts[i]);
                new_name_parts.add(0, pure_name);
                if (i == name_parts.length - 1) {
                    new_auth_parts.add(0, firstAuth);
                } else {
                    new_auth_parts.add(0, null);
                }
            }

            String prevRank = rank;
            for (int i = 0; i < name_parts.length; i++) {
                int me = name_parts.length - 1 - i;
                if (hasSecondPart) {
                    String newRank = Rank.findParentRank(prevRank, rank_relations);
                    new_rank_parts.add(0, newRank);
                    prevRank = newRank;
                } else {
                    if (me == name_parts.length - 1) {
                        new_rank_parts.add(0, prevRank);
                    } else {
                        String newRank = Rank.findParentRank(prevRank, rank_relations);
                        new_rank_parts.add(0, newRank);
                        prevRank = newRank;
                    }
                }
            }
        }

        String[] entryNames = new String[new_name_parts.size()];
        String[] entryRanks = new String[new_rank_parts.size()];
        String[] entryAuths = new String[new_auth_parts.size()];

        entryNames = new_name_parts.toArray(entryNames);
        entryRanks = new_rank_parts.toArray(entryRanks);
        entryAuths = new_auth_parts.toArray(entryAuths);

        HierarchyEntry entry = new HierarchyEntry(entryNames, entryRanks, entryAuths);
        return entry;
    }

    private HierarchyEntry genHierarchyEntry(String taxonNameInfo) throws IOException {
        System.out.println("taxonNameInfo : " + taxonNameInfo);

        String taxonName = getTaxonName(taxonNameInfo);
        String rankRemoved = TaxonXMLCommon.removeAllRank(taxonName);
        System.out.println("full name : " + rankRemoved);

        String rank = TaxonXMLCommon.getFirstRank(taxonName);
        if (rank == null) {
            rank = "Species";

            if (rankRemoved.endsWith(" n. sp.")) {
                rank = "Species";
            } else if (rankRemoved.endsWith(" n. subsp.")) {
                rank = "Subspecies";
            } else if (rankRemoved.endsWith(" var.")) {
                rank = "Variety";
            } else if (rankRemoved.endsWith(" ssp. nov.")) {
                rank = "Subspecies";
            } else if (rankRemoved.split("\\s").length == 3) {
                char startCh = rankRemoved.split("\\s")[2].charAt(0);
                if (startCh >= 'A' && startCh <= 'Z') {
                    rank = "Species";
                } else {
                    rank = "Subspecies";
                }
            } else if (rankRemoved.split("\\s").length == 2) {
                rank = "Subgenus";
            } else if (rankRemoved.split("\\s").length == 4) {
                rank = "Subspecies";
            }
        }

        HierarchyEntry entry = getHierarchyEntry(rankRemoved, rank);
        return entry;
    }

    public void start(int documentID) throws IOException, Exception {
        this.documentID = documentID;
        this.document = TaxonXMLCommon.loadDocument(this.documentID);
        this.paragraphs = TaxonXMLCommon.loadParagraphs(document);

        List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
        List<Key> keys = new ArrayList<Key>();
        Hierarchy hierarchy = new Hierarchy();

        Taxonomy taxonomy = null;
        Key key = null;
        String prevTitle = null;
        for (Paragraph paragraph : paragraphs) {
            switch (paragraph.getType()) {
                case PARAGRAPH_TITLE: {
                    HierarchyEntry hierarchy_entry = getHierarchyEntry("Nomada", "Genus");
                    hierarchy.addEntry(hierarchy_entry);
                    break;
                }
                case PARAGRAPH_TAXONNAME: {
                    taxonomy = TaxonXMLCommon.createNewTaxonomy();
                    TaxonXMLCommon.addNewMeta(taxonomy, this.processor_name, document.getFilename());
                        
                    if (paragraph.getContent().endsWith("The roberjeotiana Species Group Alexander")) {
                        taxonomy.setTaxonName("Alexander");
                        HierarchyEntry hierarchy_entry = getHierarchyEntry("Alexander", "Species_group");
                        TaxonXMLCommon.addNewIdentification(taxonomy, hierarchy, hierarchy_entry, paragraph.getContent());
                    } else {
                        // set metadata
                        String rankRemovedName = TaxonXMLCommon.removeAllRank(getTaxonName(paragraph.getContent()));
                        taxonomy.setTaxonName(rankRemovedName);

                        HierarchyEntry hierarchy_entry = genHierarchyEntry(paragraph.getContent());
                        TaxonXMLCommon.addNewIdentification(taxonomy, hierarchy, hierarchy_entry, paragraph.getContent());
                    }
                    // add to list
                    taxonomies.add(taxonomy);
                    break;
                }
                case PARAGRAPH_OTHERINFO: {
                    TaxonXMLCommon.addNewOtherInfoOnName(taxonomy, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_SYNONYM: {
                    TaxonXMLCommon.addNewSynonym(taxonomy, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_DISCUSSION_NON_TITLED_BODY: {
                    TaxonXMLCommon.addNewDiscussion(taxonomy, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_DISCUSSION: {
                    prevTitle = paragraph.getContent();
                    break;
                }
                case PARAGRAPH_DISCUSSION_BODY: {
                    TaxonXMLCommon.addNewDiscussion(taxonomy, prevTitle, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_DISCUSSION_WITH_BODY: {
                    String[] split = TaxonXMLCommon.splitTitleBody(paragraph.getContent());
                    TaxonXMLCommon.addNewDiscussion(taxonomy, split[0], split[1]);
                    break;
                }
                case PARAGRAPH_DESCRIPTION: {
                    prevTitle = paragraph.getContent();
                    break;
                }
                case PARAGRAPH_DESCRIPTION_BODY: {
                    TaxonXMLCommon.addNewDescription(taxonomy, prevTitle, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_DESCRIPTION_WITH_BODY: {
                    String[] split = TaxonXMLCommon.splitTitleBody(paragraph.getContent());
                    TaxonXMLCommon.addNewDescription(taxonomy, split[0], split[1]);
                    break;
                }
                case PARAGRAPH_TYPE_WITH_BODY: {
                    String[] split = TaxonXMLCommon.splitTitleBody(paragraph.getContent());
                    TaxonXMLCommon.addNewType(taxonomy, split[0], split[1]);
                    break;
                }
                case PARAGRAPH_MATERIAL: {
                    prevTitle = paragraph.getContent();
                    break;
                }
                case PARAGRAPH_MATERIAL_BODY: {
                    TaxonXMLCommon.addNewMaterial(taxonomy, prevTitle, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_MATERIAL_WITH_BODY: {
                    String[] split = TaxonXMLCommon.splitTitleBody(paragraph.getContent());
                    TaxonXMLCommon.addNewMaterial(taxonomy, split[0], split[1]);
                    break;
                }
                case PARAGRAPH_ARTICULATION: {
                    prevTitle = paragraph.getContent();
                    break;
                }
                case PARAGRAPH_ARTICULATION_BODY: {
                    TaxonXMLCommon.addNewArticulation(taxonomy, prevTitle, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_HABITAT:
                case PARAGRAPH_ELEVATION:
                case PARAGRAPH_ECOLOGY:
                case PARAGRAPH_DISTRIBUTION: {
                    prevTitle = paragraph.getContent();
                    break;
                }
                case PARAGRAPH_HABITAT_BODY:
                case PARAGRAPH_ELEVATION_BODY:
                case PARAGRAPH_ECOLOGY_BODY:
                case PARAGRAPH_DISTRIBUTION_BODY: {
                    TaxonXMLCommon.addNewHabitatElevationDistribution(taxonomy, prevTitle, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_HABITAT_WITH_BODY:
                case PARAGRAPH_ELEVATION_WITH_BODY:
                case PARAGRAPH_ECOLOGY_WITH_BODY:
                case PARAGRAPH_DISTRIBUTION_WITH_BODY: {
                    String[] split = TaxonXMLCommon.splitTitleBody(paragraph.getContent());
                    TaxonXMLCommon.addNewHabitatElevationDistribution(taxonomy, split[0], split[1]);
                    break;
                }
                case PARAGRAPH_KEY: {
                    if (paragraph.getContent().startsWith("Key to")) {
                        taxonomy.increaseKeyToTable();
                        key = TaxonXMLCommon.createNewKey(paragraph.getContent());
                        keys.add(key);
                    }
                    break;
                }
                case PARAGRAPH_KEY_DISCUSSION: {
                    TaxonXMLCommon.addNewDiscussion(key, paragraph.getContent());
                    break;
                }
                case PARAGRAPH_KEY_BODY: {
                    String[] statement = TaxonXMLCommon.splitKeyStatement(paragraph.getContent());
                    TaxonXMLCommon.addKeyStatement(key, statement[0], statement[1], statement[2]);
                    break;
                }
                case PARAGRAPH_IGNORE:
                    break;
                default: {
                    System.err.println("Skipped - " + paragraph.getTypeString());
                    System.err.println(paragraph.getContent());
                    break;
                }
            }
        }

        File taxonOutDir = new File("taxonomy");
        taxonOutDir.mkdir();

        File keyOutDir = new File("key");
        keyOutDir.mkdir();

        int keyfileIndex = 1;
        int allocKeyToTableNum = 0;
        for (Key k : keys) {
            File outKeyFile = new File(keyOutDir, StringUtil.getSafeFileName(keyfileIndex + ". " + k.getKeyHeading() + ".xml"));
            createXML(k, outKeyFile);
            keyfileIndex++;

            int sumKeyTo = 0;

            for (Taxonomy taxon : taxonomies) {
                int keyToNum = taxon.getKeyToTable();
                sumKeyTo += keyToNum;

                if (allocKeyToTableNum < sumKeyTo) {
                    TaxonXMLCommon.addKeyFile(taxon, outKeyFile.getName());
                    allocKeyToTableNum++;
                    break;
                }
            }
        }

        // taxon file
        int taxonfileIndex = 1;
        for (Taxonomy taxon : taxonomies) {
            if (!taxon.getTreatment().getTaxonIdentification().isEmpty()) {
                String taxonName = taxon.getTaxonName();
                File outTaxonFile = new File(taxonOutDir, StringUtil.getSafeFileName(taxonfileIndex + ". " + taxonName + ".xml"));
                createXML(taxon, outTaxonFile);
                taxonfileIndex++;
            }
        }
    }

    private void createXML(Taxonomy taxonomy, File outFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Treatment.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(taxonomy.getTreatment(), outFile);
    }

    private void createXML(Key key, File outFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Key.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(key, outFile);
    }

    public static void main(String[] args) throws Exception {
        Parse_OCR_6_Mitae_2003 obj = new Parse_OCR_6_Mitae_2003("Illyoung Choi");
        obj.start(18);
    }
}
