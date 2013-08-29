/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class SymbolTable {
    private List<SymbolLineType_Pair> symbols = new ArrayList<SymbolLineType_Pair>();
    
    private class SymbolLineType_Pair {
        private String symbol;
        private TaxonomyLineCategorizeAlg.TaxonomyLineType type;
        
        public SymbolLineType_Pair(String symbol, TaxonomyLineCategorizeAlg.TaxonomyLineType type) {
            this.symbol = symbol;
            this.type = type;
        }
        
        public String getSymbol() {
            return this.symbol;
        }
        
        public TaxonomyLineCategorizeAlg.TaxonomyLineType getType() {
            return this.type;
        } 
    }
    
    public SymbolTable() {
        initializeSymbols();
    }
    
    public TaxonomyLineCategorizeAlg.TaxonomyLineType findLineType(String text, TaxonomyConfiguration conf) {
        for(SymbolLineType_Pair pair : this.symbols) {
            if(text.trim().matches(pair.getSymbol())) {
                return pair.getType();
            }
        }
        return TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_UNKNOWN;
    }

    private void initializeSymbols() {
        if (this.symbols.isEmpty()) {
            this.symbols.add(new SymbolLineType_Pair("^Taxonomy$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_NAME));
            this.symbols.add(new SymbolLineType_Pair("^Diagnosis[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DIAGNOSIS));
            this.symbols.add(new SymbolLineType_Pair("^Description[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION));
            this.symbols.add(new SymbolLineType_Pair("^Description of (fe)+male[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION));
            this.symbols.add(new SymbolLineType_Pair("^Distribution[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Variation[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Material examined[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Molecular results[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Comments[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Acknowledgements[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_ACKNOWLEDGEMENT));
        }
    }
}
