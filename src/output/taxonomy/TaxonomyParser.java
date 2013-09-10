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
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DEFINITION;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_DEFINITION;
import static output.taxonomy.TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_TAXONOMY_GENERIC;
import output.taxonomy.bean.TaxonomyDescription;
import output.taxonomy.bean.TaxonomyDiscussion;
import output.taxonomy.bean.TaxonomyGenericElement;
import output.taxonomy.bean.TaxonomyMeta;
import output.taxonomy.bean.TaxonomyNomenclature;
import output.taxonomy.bean.TaxonomyOtherInfo;
import output.taxonomy.bean.TaxonomySynonym;
import output.taxonomy.bean.TaxonomyTypeSpecies;

/**
 *
 * @author iychoi
 */
public class TaxonomyParser {
    private File file;
    private TaxonomyLineCategorizeAlg lineAlg;
    
    private String currentTitle;
    private String currentSubTitle;
    
    public TaxonomyParser(File file) {
        if(file == null || !file.exists() || !file.isFile())
            throw new IllegalArgumentException("Cannot find the file");
        
        this.file = file;
    }
    
    public List<Taxonomy> parseTaxonomy(TaxonomyConfiguration conf) throws IOException {
        return parseTaxonomy(new DocumentSpecificSymbolTable(), conf);
    }
    
    public List<Taxonomy> parseTaxonomy(DocumentSpecificSymbolTable dsSymbolTable, TaxonomyConfiguration conf) throws IOException {
        this.lineAlg = new TaxonomyLineCategorizeAlg(dsSymbolTable, conf);
        
        List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
        
        String filedata;
        try {
            filedata = StreamUtil.readFileString(this.file);
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
        
        String[] paragraphs = filedata.split("\\n[\\r\\t ]*\\n[\\r\\t ]*\\n*");
        
        Taxonomy taxonomy = null;
        boolean bDescription = false;
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
        return filename;
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
            case LINE_DIRECTION_TAXONOMY_DIAGNOSIS_AND_DATA:
                processTaxnomyDiagnosisWithData(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_DEFINITION:
                this.currentTitle = line;
                break;
            case LINE_TAXONOMY_DEFINITION:
                processTaxonomyDefinition(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_DESCRIPTION:
                this.currentTitle = line;
                break;
            case LINE_DIRECTION_TAXONOMY_DESCRIPTION_SUBTITLE:
                this.currentSubTitle = line;
                break;
            case LINE_TAXONOMY_DESCRIPTION:
                processTaxonomyDescription(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_DESCRIPTION_AND_DATA:
                processTaxonomyDescriptionWithData(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_GENERIC:
                this.currentTitle = line;
                break;
            case LINE_DIRECTION_TAXONOMY_TYPESPECIES:
                this.currentTitle = line;
                break;
            case LINE_DIRECTION_TAXONOMY_TYPESPECIES_AND_DATA:
                processTaxonomyTypeSpeciesWithData(taxonomy, line, conf);
                break;
            case LINE_TAXONOMY_TYPESPECIES:
                processTaxonomyTypeSpecies(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA:
                processTaxonomyGenericWithData(taxonomy, line, conf);
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
            case LINE_UNKNOWN:
                processTaxonomyUnknown(taxonomy, line, conf);
                break;
            case LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY:
                taxonomy.increaseKeyToTable();
                break;
            case LINE_TAXONOMY_KEY_TO_FAMILY:
                break;
            case LINE_TAXONOMY_DISTRIBUTION:
                processTaxonomyDistribution(taxonomy, line, conf);
                break;
            case LINE_TAXONOMY_DISCUSSION:
                processTaxonomyDiscussion(taxonomy, line, conf);
                break;
            case LINE_TAXONOMY_IY1:
                processTaxonomyIy1(taxonomy, line, conf);
                break;
        }
    }
    
    private void processTaxonomyName(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyNomenclature nomenclature = new TaxonomyNomenclature();
        String indexRemoved = RegExUtil.removeIndexNumber(line);
        String taxonName = RegExUtil.removeTrailNumberAndBrace(indexRemoved);
        
        
        nomenclature.setName(taxonName);
        nomenclature.setRank(Rank.getRankFromNameInfo(taxonName, conf));
        nomenclature.setNameInfo(indexRemoved);
        
        taxonomy.setNomenclture(nomenclature);
    }
    
    private void processTaxonomyOtherInfo(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyNomenclature nomenclature = taxonomy.getNomenclature();
        if(nomenclature != null) {
            TaxonomyOtherInfo otherinfo = new TaxonomyOtherInfo();
            otherinfo.setOtherInfo(line);

            nomenclature.addOtherInfo(otherinfo);
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
    
    private void processTaxnomyDiagnosisWithData(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDescription description = new TaxonomyDescription();
        String titleRemoved = RegExUtil.removeSubTitle(line);
        description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_DIAGNOSIS);
        description.setDescription(titleRemoved);

        taxonomy.addDescription(description);
    }
    
    private void processTaxonomyDefinition(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDescription description = new TaxonomyDescription();
        description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_DEFINITION);
        description.setDescription(line);

        taxonomy.addDescription(description);
    }
    
    private void processTaxonomyDescription(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDescription description = new TaxonomyDescription();
        description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_GENERIC);
        if(this.currentTitle.trim().equalsIgnoreCase("description") || this.currentTitle.trim().equalsIgnoreCase("description.")) {
            // skip
        } else {
            description.setTitle(this.currentTitle);
        }
        
        // overwrite
        if(this.currentSubTitle != null && !this.currentSubTitle.trim().equals("")) {
            String text = RegExUtil.removeTrailDot(this.currentSubTitle);
            description.setTitle(text);
        }
        
        description.setDescription(line);

        taxonomy.addDescription(description);
    }
    
    private void processTaxonomyDescriptionWithData(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDescription description = new TaxonomyDescription();
        String titleRemoved = RegExUtil.removeSubTitle(line);
        description.setType(TaxonomyDescription.TaxonomyDescriptionType.DESCRIPTION_GENERIC);
        description.setDescription(titleRemoved);

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
    
    private void processTaxonomyDiscussion(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyDiscussion discussion = new TaxonomyDiscussion();        
        discussion.setText(line);
        
        taxonomy.addDiscussionNonTitled(discussion);
    }
    
    private void processTaxonomyTypeSpecies(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyTypeSpecies typeSpecies = new TaxonomyTypeSpecies();
        typeSpecies.setTypeSpecies(line);

        taxonomy.addTypeSpecies(typeSpecies);
    }
    
    private void processTaxonomyTypeSpeciesWithData(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyTypeSpecies typeSpecies = new TaxonomyTypeSpecies();
        String titleRemoved = RegExUtil.removeSubTitle(line);
        typeSpecies.setTypeSpecies(titleRemoved);

        taxonomy.addTypeSpecies(typeSpecies);
    }

    private void processTaxonomyGenericWithData(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyGenericElement generic = new TaxonomyGenericElement();
        String[] strs = RegExUtil.splitSubTitleAndBody(line);
        if(strs != null && strs.length >= 2) {
            generic.setName(strs[0]);
            generic.setText(strs[1]);
            taxonomy.addElement(generic);
        }
    }

    private void processTaxonomyUnknown(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        if(conf.getUnknownAsDiscussion()) {
            processTaxonomyDiscussion(taxonomy, line, conf);
        } else {
            TaxonomyGenericElement generic = new TaxonomyGenericElement();
            generic.setName("UNKNOWN");
            generic.setText(line);

            taxonomy.addElement(generic);
        }
    }

    private void processTaxonomyKeyTo(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        
    }
    
    private void processTaxonomyDistribution(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyGenericElement generic = new TaxonomyGenericElement();
        generic.setName("distribution");
        generic.setText(line);

        taxonomy.addElement(generic);
    }

    private void processTaxonomyIy1(Taxonomy taxonomy, String line, TaxonomyConfiguration conf) {
        TaxonomyGenericElement generic = new TaxonomyGenericElement();
        generic.setName("description");
        generic.setText(line);

        taxonomy.addElement(generic);
    }
}
