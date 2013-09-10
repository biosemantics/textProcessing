/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy;

import common.utils.RegExUtil;

/**
 *
 * @author iychoi
 */
public class TaxonomyLineCategorizeAlg {
    
    public enum TaxonomyLineType {
        LINE_UNKNOWN,
        LINE_DIRECTION_TAXONOMY_NAME,
        LINE_TAXONOMY_NAME,
        LINE_TAXONOMY_OTHERINFO,
        LINE_TAXONOMY_SYNONYM,
        LINE_DIRECTION_TAXONOMY_TYPESPECIES_AND_DATA,
        LINE_DIRECTION_TAXONOMY_TYPESPECIES,
        LINE_TAXONOMY_TYPESPECIES,
        LINE_DIRECTION_TAXONOMY_DIAGNOSIS_AND_DATA,
        LINE_DIRECTION_TAXONOMY_DIAGNOSIS,
        LINE_TAXONOMY_DIAGNOSIS,
        LINE_DIRECTION_TAXONOMY_DEFINITION,
        LINE_TAXONOMY_DEFINITION,
        LINE_DIRECTION_TAXONOMY_DESCRIPTION,
        LINE_TAXONOMY_DESCRIPTION,
        LINE_DIRECTION_TAXONOMY_DESCRIPTION_SUBTITLE,
        LINE_DIRECTION_TAXONOMY_DESCRIPTION_AND_DATA,
        LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY,
        LINE_TAXONOMY_KEY_TO_FAMILY_DISCUSSION,
        LINE_TAXONOMY_KEY_TO_FAMILY,
        LINE_TAXONOMY_DISTRIBUTION,
        LINE_TAXONOMY_DISCUSSION,
        LINE_DIRECTION_TAXONOMY_IY1,
        LINE_TAXONOMY_IY1,
        //
        LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA,
        //
        LINE_DIRECTION_TAXONOMY_GENERIC,
        LINE_TAXONOMY_GENERIC,
        LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC,
        LINE_TAXONOMY_DISCUSSION_GENERIC,
        LINE_DIRECTION_TAXONOMY_ACKNOWLEDGEMENT,
        LINE_TAXONOMY_ACKNOWLEDGEMENT,
    }
    
    private SymbolTable symbols;
    private DocumentSpecificSymbolTable ds_symbols;
    private TaxonomyConfiguration conf;
    private String currentLine;
    private TaxonomyLineType previousImportantLineType;
    private TaxonomyLineType previousLineType;
    private TaxonomyLineType currentLineType;
    
    public TaxonomyLineCategorizeAlg(DocumentSpecificSymbolTable symbolTable, TaxonomyConfiguration conf) {
        this.symbols = new SymbolTable(conf);
        this.ds_symbols = symbolTable;
        this.conf = conf;
    }
    
    public void feedLine(String line) {
        this.currentLine = line.trim();
        
        this.previousLineType = this.currentLineType;
        if(this.previousLineType != null) {
            if(!this.previousLineType.equals(TaxonomyLineType.LINE_UNKNOWN)) {
                this.previousImportantLineType = this.previousLineType;
            }
        }

        TaxonomyLineType type = identifyLineType(line.trim());
        this.currentLineType = type;
    }
    
    private TaxonomyLineType identifyLineType(String line) {
        // check document specific table
        TaxonomyLineType lineType1 = this.ds_symbols.findLineType(line, this.conf);
        if(lineType1 != null && !lineType1.equals(TaxonomyLineType.LINE_UNKNOWN)) {
            return lineType1;
        }
        
        // check symbol table
        TaxonomyLineType lineType2 = this.symbols.findLineType(line, this.conf);
        if(lineType2 != null && !lineType2.equals(TaxonomyLineType.LINE_UNKNOWN)) {
            return lineType2;
        }
        
        // name - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_NAME)) {
                return TaxonomyLineType.LINE_TAXONOMY_NAME;
            }
        }
        
        // name - by auto
        if(RegExUtil.isTaxonomyName(line, this.conf.getTaxonomyNameWordLen())) {
            return TaxonomyLineType.LINE_TAXONOMY_NAME;
        }
        
        // other info - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_TAXONOMY_NAME)) {
                return TaxonomyLineType.LINE_TAXONOMY_OTHERINFO;
            }
        }
        
        // synonym - by auto
        if(RegExUtil.isSynonym(line, this.conf.getTaxonomyNameWordLen())) {
            return TaxonomyLineType.LINE_TAXONOMY_SYNONYM;
        }
        
        // diagnosis - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DIAGNOSIS)) {
                return TaxonomyLineType.LINE_TAXONOMY_DIAGNOSIS;
            }
        }
        
        // definition - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DEFINITION)) {
                return TaxonomyLineType.LINE_TAXONOMY_DEFINITION;
            }
        }
        
        // description - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION_SUBTITLE)) {
                return TaxonomyLineType.LINE_TAXONOMY_DESCRIPTION;
            }
        }
        
        // description - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION)) {
                // description subtitle - by auto
                if(RegExUtil.isDescriptionSubTitle(line, this.conf.getTaxonomyDescriptionSubTitleWordLen())) {
                    return TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION_SUBTITLE;
                } else {
                    return TaxonomyLineType.LINE_TAXONOMY_DESCRIPTION;
                }
            }
        }
        
        // key to family - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY)) {
                if(RegExUtil.isKeyToStatement(line)) {
                    return TaxonomyLineType.LINE_TAXONOMY_KEY_TO_FAMILY;
                } else {
                    return TaxonomyLineType.LINE_TAXONOMY_KEY_TO_FAMILY_DISCUSSION;
                }
            }
        }
        
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_TAXONOMY_KEY_TO_FAMILY_DISCUSSION)) {
                if(RegExUtil.isKeyToStatement(line)) {
                    return TaxonomyLineType.LINE_TAXONOMY_KEY_TO_FAMILY;
                } else {
                    return TaxonomyLineType.LINE_TAXONOMY_KEY_TO_FAMILY_DISCUSSION;
                }
            }
        }
        
        // type species - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_TYPESPECIES)) {
                return TaxonomyLineType.LINE_TAXONOMY_TYPESPECIES;
            }
        }
        
        // generic - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC)) {
                return TaxonomyLineType.LINE_TAXONOMY_GENERIC;
            }
        }
        
        // discussion - generic - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC)) {
                return TaxonomyLineType.LINE_TAXONOMY_DISCUSSION_GENERIC;
            }
        }
        
        // ack - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_ACKNOWLEDGEMENT)) {
                return TaxonomyLineType.LINE_TAXONOMY_ACKNOWLEDGEMENT;
            }
        }
        
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_IY1)) {
                return TaxonomyLineType.LINE_TAXONOMY_IY1;
            }
        }
        
        // separated paragraph?
        if(this.previousImportantLineType != null) {
            if(this.previousImportantLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA)) {
                // check sub - by auto
                if(RegExUtil.isSubTitleWithData(line, this.conf.getTaxonomySubTitleWordLen())) {
                    return TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA;
                }
            } else if(this.previousImportantLineType.equals(TaxonomyLineType.LINE_TAXONOMY_SYNONYM)) {
                return TaxonomyLineType.LINE_UNKNOWN;
            } else {
                return this.previousImportantLineType;
            }
        }
        
        return TaxonomyLineType.LINE_UNKNOWN;
    }
    
    public boolean isNewTaxonomy() {
        if(currentLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_NAME)) {
            return true;
        }
        if(currentLineType.equals(TaxonomyLineType.LINE_TAXONOMY_NAME)) {
            return true;
        }
        
        return false;
    }
    
    public boolean isNewKeyTo() {
        if(currentLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY)) {
            return true;
        }
        
        return false;
    }
    
    public TaxonomyLineType getLineType() {
        return this.currentLineType;
    }
}
