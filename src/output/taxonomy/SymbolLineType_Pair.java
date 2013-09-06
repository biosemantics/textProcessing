/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy;

/**
 *
 * @author iychoi
 */
public class SymbolLineType_Pair {
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
