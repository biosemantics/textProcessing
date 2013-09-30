/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.old;

import common.utils.StringUtil;
import java.io.File;
import java.util.List;
import taxonomy.Taxonomy;
import taxonomy.TaxonomyConfiguration;
import taxonomy.TaxonomyParser;
import taxonomy.bean.TaxonomyNomenclature;

/**
 *
 * @author iychoi
 */
public class Parse_4_Droege_2010 {
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
        List<Taxonomy> taxonomies = parser.parseTaxonomy(conf);
        
        File parentDir = file.getParentFile();
        
        int fileIndex = 1;
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
                taxonomy.toXML(new File(parentDir, StringUtil.getSafeFileName(fileIndex + ". " + nomenclature.getName()) + ".xml"));
                fileIndex++;
            }
        }
    }
}
