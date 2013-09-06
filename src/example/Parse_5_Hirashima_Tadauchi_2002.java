/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import common.utils.StringUtil;
import java.io.File;
import java.util.List;
import output.key.KeyTo;
import output.key.KeyToParser;
import output.taxonomy.DocumentSpecificSymbolTable;
import output.taxonomy.Taxonomy;
import output.taxonomy.TaxonomyConfiguration;
import output.taxonomy.TaxonomyLineCategorizeAlg;
import output.taxonomy.TaxonomyParser;
import output.taxonomy.bean.TaxonomyKeyFile;
import output.taxonomy.bean.TaxonomyNomenclature;

/**
 *
 * @author iychoi
 */
public class Parse_5_Hirashima_Tadauchi_2002 {
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify text file path");
            return;
        }
        
        TaxonomyConfiguration conf = new TaxonomyConfiguration();
        conf.setRankDefault("Subgenus");
        conf.setUnknownAsDiscussion(true);
        
        File file = new File(args[0]);
        DocumentSpecificSymbolTable dsst = new DocumentSpecificSymbolTable();
        dsst.addSymbols("^Adamon, a new subgenus of Nomada Scopoli$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_NAME);
        
        
        TaxonomyParser parser = new TaxonomyParser(file);
        List<Taxonomy> taxonomies = parser.parseTaxonomy(dsst, conf);
        
        KeyToParser kparser = new KeyToParser(file);
        List<KeyTo> keytos = kparser.parseKeyTo(dsst, conf);
        
        File parentDir = file.getParentFile();
        
        boolean bFirst = true;
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                /// add missing taxon hierarchy
                if(nomenclature.getHierarchy() == null || nomenclature.getHierarchy().trim().equals("")) {
                    String name = nomenclature.getName();
                    String[] nameSplit = name.split("\\s");
                    
                    if(bFirst) {
                        bFirst = false;
                        
                        String hierarchy = "Genus Nomada Scopoli; Subgenus Adamon";

                        nomenclature.setName("Nomada Scopoli Adamon");
                        nomenclature.setHierarchy(hierarchy.trim());
                    } else {
                        String hierarchy = "Genus Nomada Scopoli; Subgenus Adamon; " + name;
                        nomenclature.setHierarchy(hierarchy.trim());    
                        nomenclature.setRank("Species");
                    }
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
    }
}
