/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.old;

import common.utils.StringUtil;
import java.io.File;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class Parse_Abebe_FreshwaterNematodes {
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify text file path");
            return;
        }
        
        /*
        TaxonomyConfiguration conf = new TaxonomyConfiguration();
        conf.setRankDefault("Order");
        conf.setUnknownAsDiscussion(true);
        
        File file = new File(args[0]);
        DocumentSpecificSymbolTable dsst = new DocumentSpecificSymbolTable();
        dsst.addSymbols("^Cuticle$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION);
        dsst.addSymbols("^Sensory organs$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION);
        dsst.addSymbols("^Glandular system$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION);
        dsst.addSymbols("^Digestive system$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION);
        dsst.addSymbols("^Reproductive system$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION);
        dsst.addSymbols("^Classification$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC);
        dsst.addSymbols("^Diagnosis.$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_IY1);
        
        dsst.addSymbols("^Mainly marine(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISTRIBUTION);
        //dsst.addSymbols("^Freshwater species(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISTRIBUTION);
        dsst.addSymbols("^An overview of\\s(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        
        dsst.addSymbols("^Only one superfamily\\s(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        dsst.addSymbols("^Type and only (\\w+):(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        dsst.addSymbols("^Type \\(and only\\) genus:(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        dsst.addSymbols("^The family\\s(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        dsst.addSymbols("^Type species\\s(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        dsst.addSymbols("^Type genus\\s(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DISCUSSION);
        dsst.addSymbols("^Species recorded (in|from) (.+):", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC);
        dsst.addSymbols("^Freshwater species:", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC);
        
        dsst.addSymbols("^Suborder\\s[A-Z]+\\s(.+),? \\d+ (.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_NAME);
        dsst.addSymbols("^Superfamily\\s[A-Z]+\\s(.+),? \\d+", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_NAME);
        
        TaxonomyParser parser = new TaxonomyParser(file);
        List<Taxonomy> taxonomies = parser.parseTaxonomy(dsst, conf);
        
        KeyToParser kparser = new KeyToParser(file);
        List<KeyTo> keytos = kparser.parseKeyTo(dsst, conf);
        
        File parentDir = file.getParentFile();
        File parentDirKey = new File(parentDir, "key");
        parentDirKey.mkdir();
        File parentDirTaxon = new File(parentDir, "taxonomy");
        parentDirTaxon.mkdir();
        
        boolean bFirst = true;
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                /// add missing taxon hierarchy
                if(nomenclature.getHierarchy() == null || nomenclature.getHierarchy().trim().equals("")) {
                    String name = nomenclature.getName();
                    
                    if(bFirst) {
                        String hierarchy = name;
                        nomenclature.setHierarchy(hierarchy.trim());
                        
                        for(TaxonomyDescription description : taxonomy.getDescriptions()) {
                            String desc = description.getTitle() + ": " + description.getDescription();
                            description.setDescription(desc);
                        }
                        bFirst = false;
                    } else {
                        String hierarchy = "Order Enoplida; " + name;
                        nomenclature.setHierarchy(hierarchy.trim());
                    }
                }
            }
        }
        
        // to file
        int keyfileIndex = 1;
        int allocKeyToTableNum = 0;
        for(KeyTo keyto : keytos) {
            File outKeyFile = new File(parentDirKey, StringUtil.getSafeFileName(keyfileIndex + ". " + keyto.getHeading().getHeading()) + ".xml");
            keyto.toXML(outKeyFile);
            keyfileIndex++;
            
            int sumKeyTo = 0;
            
            for(int i=0;i<taxonomies.size();i++) {
                Taxonomy taxonomy = taxonomies.get(i);
                int keyToNum =  taxonomy.getKeyToTableNumber();
                sumKeyTo += keyToNum;
                
                if(allocKeyToTableNum < sumKeyTo) {
                    taxonomy.addKeyFile(new TaxonomyKeyFile(outKeyFile));
                    allocKeyToTableNum++;
                    break;
                }
            }
        }
        
        // to file
        int taxonfileIndex = 1;
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                System.out.println("taxonomy name : " + nomenclature.getNameInfo());
                
                File outTaxonFile = new File(parentDirTaxon, StringUtil.getSafeFileName(taxonfileIndex + ". " + nomenclature.getName()) + ".xml");
                taxonomy.toXML(outTaxonFile);
                taxonfileIndex++;
            }
        }
        */
    }
}
