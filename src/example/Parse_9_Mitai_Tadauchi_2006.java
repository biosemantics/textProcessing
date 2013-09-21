/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import common.db.DBUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import paragraph.bean.Document;
import paragraph.bean.Paragraph;
import paragraph.bean.ParagraphType;
import paragraph.db.DocumentTable;
import paragraph.db.ParagraphTable;
import taxonomy.Taxonomy;
import taxonomy.bean.TaxonomyMeta;

/**
 *
 * @author iychoi
 */
public class Parse_9_Mitai_Tadauchi_2006 {
    
    private static Document loadDocument(int documentID) throws IOException {
        Connection conn = DBUtil.getConnection();
        Document document = DocumentTable.getDocument(conn, documentID);
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return document;
    }
    
    private static List<Paragraph> loadParagraphs(Document document) throws IOException {
        Connection conn = DBUtil.getConnection();
        List<Paragraph> paragraphs = ParagraphTable.getParagraphs(conn, document.getDocumentID());

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IOException(ex.getMessage());
        }
        return paragraphs;
    }
    
    public static void main(String[] args) throws Exception {
        int documentID = 9;
        Document document = loadDocument(documentID);
        List<Paragraph> paragraphs = loadParagraphs(document);
        List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
        
        Taxonomy taxonomy = null;
        for(Paragraph paragraph : paragraphs) {
            // prepare taxonomy bean
            if(paragraph.getType().equals(ParagraphType.PARAGRAPH_TAXONNAME)) {
                taxonomy = new Taxonomy();
                // set metadata
                TaxonomyMeta meta = new TaxonomyMeta(document.getFilename());
                taxonomy.setMeta(meta);

                // add to list
                taxonomies.add(taxonomy);
                break;
            }
            
            
            switch(paragraph.getType()) {
                /*
                 * PARAGRAPH_ABSTRACT,
    PARAGRAPH_ABSTRACT_BODY,
    PARAGRAPH_TITLE,
    PARAGRAPH_AUTHOR,
    PARAGRAPH_AUTHOR_DETAILS,
    PARAGRAPH_KEYWORDS,
    
    PARAGRAPH_INTRODUCTION,
    PARAGRAPH_INTRODUCTION_BODY,
    PARAGRAPH_MATERIALS_METHOD,
    PARAGRAPH_MATERIALS_METHOD_BODY,
    PARAGRAPH_DISTRIBUTION,
    PARAGRAPH_DISTRIBUTION_BODY,
    PARAGRAPH_DISTRIBUTION_WITH_BODY,
    PARAGRAPH_KEY,
    PARAGRAPH_KEY_BODY,
    PARAGRAPH_REMARKS,
    PARAGRAPH_REMARKS_BODY,
    PARAGRAPH_REMARKS_WITH_BODY,
    PARAGRAPH_ACKNOWLEDGEMENT,
    PARAGRAPH_ACKNOWLEDGEMENT_BODY,
    PARAGRAPH_TAXONNAME,
    PARAGRAPH_OTHERINFO,
    PARAGRAPH_SYNONYM,
    PARAGRAPH_DIAGNOSIS,
    PARAGRAPH_DIAGNOSIS_BODY,
    PARAGRAPH_DEFINITION,
    PARAGRAPH_DEFINITION_BODY,
    PARAGRAPH_DESCRIPTION,
    PARAGRAPH_DESCRIPTION_BODY,
    PARAGRAPH_TYPESPECIES,
    PARAGRAPH_TYPESPECIES_BODY,
    PARAGRAPH_DISCUSSION_BODY,
    PARAGRAPH_DISCUSSION_SUBTITLE,
    PARAGRAPH_DISCUSSION_SUBBODY,
    PARAGRAPH_SUBTITLE,
    PARAGRAPH_SUBBODY,
                 */
                case PARAGRAPH_TAXONNAME:
                    break;
                default:
                    System.err.println("Skipped (" + paragraph.getTypeString() + ") : " + paragraph.getContent());
                    break;
            }
        }
        
        
        /*
        KeyToParser kparser = new KeyToParser(file);
        List<KeyTo> keytos = kparser.parseKeyTo(dsst, conf);
        
        File parentDir = file.getParentFile();
        
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                /// add missing taxon hierarchy
                if(nomenclature.getHierarchy() == null || nomenclature.getHierarchy().trim().equals("")) {
                    String name = nomenclature.getName();
                    String[] nameSplit = name.split("\\s");
                    
                    String hierarchy = "Genus Nomada (Hymenoptera: Apidae); " + name;
                    nomenclature.setHierarchy(hierarchy.trim());
                }
            }
        }
        
        // to file
        int keyfileIndex = 1;
        for(KeyTo keyto : keytos) {
            File outKeyFile = new File(parentDir, StringUtil.getSafeFileName(keyfileIndex + ". " + keyto.getHeading().getHeading()) + ".xml");
            keyto.toXML(outKeyFile);
            keyfileIndex++;
            
            for(Taxonomy taxonomy : taxonomies) {
                taxonomy.addKeyFile(new TaxonomyKeyFile(outKeyFile));
            }
        }
        
        // to file
        int taxonfileIndex = 1;
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                System.out.println("taxonomy name : " + nomenclature.getNameInfo());
                
                File outTaxonFile = new File(parentDir, StringUtil.getSafeFileName(taxonfileIndex + ". " + nomenclature.getName()) + ".xml");
                taxonomy.toXML(outTaxonFile);
                taxonfileIndex++;
            }
        }
        */
    }
}
