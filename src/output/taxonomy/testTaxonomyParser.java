/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy;

import common.utils.StringUtil;
import java.io.File;
import java.util.List;
import output.taxonomy.bean.TaxonomyNomenclature;
import output.taxonomy.bean.TaxonomyOtherInfo;

/**
 *
 * @author iychoi
 */
public class testTaxonomyParser {
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify text file path");
            return;
        }
        
        TaxonomyConfiguration conf = new TaxonomyConfiguration();
        conf.setTaxonomyNameWordLen(3);
        
        File file = new File(args[0]);
        TaxonomyParser parser = new TaxonomyParser(file);
        List<Taxonomy> taxonomies = parser.parseTaxonomy(conf);

        File parentDir = file.getParentFile();
        
        for(Taxonomy taxonomy : taxonomies) {
            TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
            if(nomenclature != null) {
                System.out.println("taxonomy name : " + nomenclature.getNameInfo());
                taxonomy.toXML(new File(parentDir, StringUtil.getSafeFileName(nomenclature.getName()) + ".xml"));
                if(taxonomy.getSynonym() != null) {
                    System.out.println("synonym : " + taxonomy.getSynonym().getSynonym());
                }
            }
        }
    }
}
