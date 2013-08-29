/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy;

import common.utils.RegExUtil;
import common.utils.StreamUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_GENERIC;
import output.taxonomy.bean.TaxonomyDescription;
import output.taxonomy.bean.TaxonomyDiscussion;
import output.taxonomy.bean.TaxonomyGenericElement;
import output.taxonomy.bean.TaxonomyMeta;
import output.taxonomy.bean.TaxonomyNomenclature;
import output.taxonomy.bean.TaxonomyOtherInfo;
import output.taxonomy.bean.TaxonomySynonym;

/**
 *
 * @author iychoi
 */
public class TaxonomyParser {
    private File file;
    private TaxonomyLineCategorizeAlg lineAlg;
    
    private String currentTitle;
    
    public TaxonomyParser(File file) {
        if(file == null || !file.exists() || !file.isFile())
            throw new IllegalArgumentException("Cannot find the file");
        
        this.file = file;
    }
    
    public List<Taxonomy> parseTaxonomy(TaxonomyConfiguration conf) throws IOException {
        this.lineAlg = new TaxonomyLineCategorizeAlg(conf);
        
        List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
        
        String filedata;
        try {
            filedata = StreamUtil.readFileString(this.file);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
        
        String[] paragraphs = filedata.split("\\n[\\r\\t ]*\\n[\\r\\t ]*\\n*");
        
        Taxonomy taxonomy = null;
        for(String paragraph : paragraphs) {
            this.lineAlg.feedLine(paragraph.trim());
            
            if(this.lineAlg.isNewTaxonomy()) {
                taxonomy = new Taxonomy();
                // set metadata
                TaxonomyMeta meta = new TaxonomyMeta(getSource(this.file));
                taxonomy.setMeta(meta);
                
                // add to list
                taxonomies.add(taxonomy);
            }
            
            if(taxonomy != null) {
                processTaxonomy(taxonomy, paragraph.trim(), conf);
            }
        }
        
        return taxonomies;
    }
    
    private String getSource(File inputFile) {
        String filename = inputFile.getName();
        // remove trail extension part
        String source = filename.replaceAll("\\..+$", "");
        return source;
    }
    
    private void processTaxonomy(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        switch(this.lineAlg.getLineType()) {
            case LINE_DIRECTION_TAXONOMY_NAME:
                this.currentTitle = line;
                break;
            case LINE_TAXONOMY_NAME:
                processTaxonomyName(taxonomy, line, conf);
                break;
            case LINE_TAXONOMY_OTHERINFO:
                processTaxonomyOtherInfo(taxonomy, line, conf);
                break;
            case LINE_TAXONOMY_SYNONYM:
                processTaxonomySynonym(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_DIAGNOSIS:
                this.currentTitle = line;
                break;
            case LINE_TAXONOMY_DIAGNOSIS:
                processTaxonomyDiagnosis(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_DESCRIPTION:
                this.currentTitle = line;
                break;
            case LINE_TAXONOMY_DESCRIPTION:
                processTaxonomyDescription(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_GENERIC:
                this.currentTitle = line;
                break;
            case LINE_TAXONOMY_GENERIC:
                processTaxonomyGeneric(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC:
                this.currentTitle = line;
                break;
            case LINE_TAXONOMY_DISCUSSION_GENERIC:
                processTaxonomyDiscussionGeneric(taxonomy, line, conf);
                break;
            case LINE_TAXONOMY_ACKNOWLEDGEMENT:
                // do nothing
                break;
                
        }
    }
    
    private void processTaxonomyName(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyNomenclature nomenclature = new TaxonomyNomenclature();
        String indexRemoved = RegExUtil.removeIndexNumber(line);
        String taxonName = RegExUtil.removeTrailNumber(line);
        
        nomenclature.setName(taxonName);
        nomenclature.setRank(Rank.getRankFromNameInfo(taxonName, conf));
        nomenclature.setNameInfo(indexRemoved);
        
        taxonomy.setNomenclture(nomenclature);
    }
    
    private void processTaxonomyOtherInfo(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
        if(nomenclature != null) {
            TaxonomyOtherInfo old = nomenclature.getOtherInfo();
            if (old != null) {
                String oldParagraph = old.getOtherInfo();
                old.setOtherInfo(oldParagraph + "\n" + line);
            } else {
                TaxonomyOtherInfo otherinfo = new TaxonomyOtherInfo();
                otherinfo.setOtherInfo(line);

                nomenclature.setOtherInfo(otherinfo);
            }
        }
    }
    
    private void processTaxonomySynonym(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomySynonym synonym = new TaxonomySynonym();
        synonym.setSynonym(line);

        taxonomy.addSynonym(synonym);
    }
    
    private void processTaxonomyDiagnosis(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDescription description = new TaxonomyDescription();
        description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_DIAGNOSIS);
        description.setDescription(line);

        taxonomy.addDescription(description);
    }
    
    private void processTaxonomyDescription(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDescription description = new TaxonomyDescription();
        description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_DEFINITION);
        description.setDescription(line);

        taxonomy.addDescription(description);
    }
        
    private void processTaxonomyGeneric(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyGenericElement generic = new TaxonomyGenericElement();
        generic.setName(this.currentTitle);
        generic.setText(line);

        taxonomy.addElement(generic);
    }
    
    private void processTaxonomyDiscussionGeneric(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyGenericElement generic = new TaxonomyGenericElement();
        generic.setName(this.currentTitle);
        generic.setText(line);

        TaxonomyDiscussion discussion = taxonomy.getDiscussion();
        if(discussion == null) {
            discussion = new TaxonomyDiscussion();
            taxonomy.setDiscussion(discussion);
        }
        
        discussion.addElement(generic);
    }
}
