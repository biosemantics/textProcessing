/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import common.utils.StringUtil;
import java.io.File;
import java.util.List;
import taxonomy.key.KeyTo;
import taxonomy.key.KeyToParser;
import taxonomy.DocumentSpecificSymbolTable;
import taxonomy.Taxonomy;
import taxonomy.TaxonomyConfiguration;
import taxonomy.TaxonomyLineCategorizeAlg;
import taxonomy.TaxonomyParser;
import taxonomy.bean.TaxonomyKeyFile;
import taxonomy.bean.TaxonomyNomenclature;

/**
 *
 * @author iychoi
 */
public class Parse_7_Mitai_2004 {
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify text file path");
            return;
        }
        
        TaxonomyConfiguration conf = new TaxonomyConfiguration();
        conf.setRankDefault("Species");
        conf.setUnknownAsDiscussion(true);
        
        File file = new File(args[0]);
        DocumentSpecificSymbolTable dsst = new DocumentSpecificSymbolTable();
        dsst.addSymbols("^Nomada maculifrons var.+", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_SYNONYM);
        dsst.addSymbols("^Nomada comparata:.+", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_SYNONYM);
        dsst.addSymbols("^Nomada ginran Tsuneki,.+", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_SYNONYM);
        dsst.addSymbols("^Nomada yasha Tsuneki,.+", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_SYNONYM);
        
        TaxonomyParser parser = new TaxonomyParser(file);
        List<Taxonomy> taxonomies = parser.parseTaxonomy(dsst, conf);
        
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
    }
}
