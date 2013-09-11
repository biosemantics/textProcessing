/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonomy;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DocumentSpecificSymbolTable {
    private List<SymbolLineType_Pair> symbols = new ArrayList<SymbolLineType_Pair>();
    
    public DocumentSpecificSymbolTable() {
    }
    
    public TaxonomyLineCategorizeAlg.TaxonomyLineType findLineType(String text, TaxonomyConfiguration conf) {
        for(SymbolLineType_Pair pair : this.symbols) {
            if(text.trim().matches(pair.getSymbol())) {
                return pair.getType();
            }
        }
        return TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_UNKNOWN;
    }
    
    public void addSymbols(String expression, TaxonomyLineCategorizeAlg.TaxonomyLineType lineType) {
        this.symbols.add(new SymbolLineType_Pair(expression, lineType));
    }
}
