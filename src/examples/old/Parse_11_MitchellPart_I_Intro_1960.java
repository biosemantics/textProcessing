/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.old;

import common.utils.StringUtil;
import java.io.File;
import java.util.List;
import taxonomy.DocumentSpecificSymbolTable;
import taxonomy.Taxonomy;
import taxonomy.TaxonomyConfiguration;
import taxonomy.TaxonomyLineCategorizeAlg;
import taxonomy.TaxonomyParser;
import taxonomy.bean.TaxonomyNomenclature;

/**
 *
 * @author iychoi
 */
public class Parse_11_MitchellPart_I_Intro_1960 {
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify text file path");
            return;
        }
        
        TaxonomyConfiguration conf = new TaxonomyConfiguration();
        conf.setTaxonomyNameWordLen(3);
        conf.setRankDefault("Species");
        
        File file = new File(args[0]);
        TaxonomyParser parser = new TaxonomyParser(file);
        DocumentSpecificSymbolTable dsSymbols = new DocumentSpecificSymbolTable();
        dsSymbols.addSymbols("BEES OF THE EASTERN UNITED STATES, I", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_NAME);
        
        List<Taxonomy> taxonomies = parser.parseTaxonomy(dsSymbols, conf);
        
        File parentDir = file.getParentFile();
        
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                /// add missing taxon hierarchy
                if(nomenclature.getHierarchy() == null || nomenclature.getHierarchy().trim().equals("")) {
                    String name = nomenclature.getName();
                    String[] nameSplit = name.split("\\s");
                    if(nameSplit.length != 3) {
                        System.err.println("Error! size is not 3");
                    }
                    String hierarchy = "Genus Nomada; " + name;
                    
                    nomenclature.setHierarchy(hierarchy.trim());
                }
                
                System.out.println("taxonomy name : " + nomenclature.getNameInfo());
                taxonomy.toXML(new File(parentDir, StringUtil.getSafeFileName(nomenclature.getName()) + ".xml"));
            }
        }
    }
}
