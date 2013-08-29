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
        LINE_DIRECTION_TAXONOMY_DIAGNOSIS,
        LINE_TAXONOMY_DIAGNOSIS,
        LINE_DIRECTION_TAXONOMY_DESCRIPTION,
        LINE_TAXONOMY_DESCRIPTION,
        //
        LINE_DIRECTION_TAXONOMY_GENERIC,
        LINE_TAXONOMY_GENERIC,
        LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC,
        LINE_TAXONOMY_DISCUSSION_GENERIC,
        LINE_DIRECTION_TAXONOMY_ACKNOWLEDGEMENT,
        LINE_TAXONOMY_ACKNOWLEDGEMENT,
    }
    
    private SymbolTable symbols;
    private TaxonomyConfiguration conf;
    private String currentLine;
    private TaxonomyLineType previousImportantLineType;
    private TaxonomyLineType previousLineType;
    private TaxonomyLineType currentLineType;
    
    public TaxonomyLineCategorizeAlg(TaxonomyConfiguration conf) {
        this.symbols = new SymbolTable();
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
        // check symbol table
        TaxonomyLineType lineType = this.symbols.findLineType(line, this.conf);
        if(lineType != null && !lineType.equals(TaxonomyLineType.LINE_UNKNOWN)) {
            return lineType;
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
        
        // description - by direction
        if(this.previousLineType != null) {
            if(this.previousLineType.equals(TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION)) {
                return TaxonomyLineType.LINE_TAXONOMY_DESCRIPTION;
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
        
        // separated paragraph?
        if(this.previousImportantLineType != null) {
            return this.previousImportantLineType;
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
    
    public TaxonomyLineType getLineType() {
        return this.currentLineType;
    }
}
