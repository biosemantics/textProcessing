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
public class SymbolTable {
    private List<SymbolLineType_Pair> symbols = new ArrayList<SymbolLineType_Pair>();
    
    public SymbolTable(TaxonomyConfiguration conf) {
        initializeSymbols(conf);
    }
    
    public TaxonomyLineCategorizeAlg.TaxonomyLineType findLineType(String text, TaxonomyConfiguration conf) {
        for(SymbolLineType_Pair pair : this.symbols) {
            
            if(text.trim().matches(pair.getSymbol())) {
                return pair.getType();
            }
        }
        return TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_UNKNOWN;
    }

    private void initializeSymbols(TaxonomyConfiguration conf) {
        if (this.symbols.isEmpty()) {
            this.symbols.add(new SymbolLineType_Pair("^Taxonomy$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_NAME));
            this.symbols.add(new SymbolLineType_Pair("^Diagnosis[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DIAGNOSIS));
            this.symbols.add(new SymbolLineType_Pair("^Definition[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DEFINITION));
            this.symbols.add(new SymbolLineType_Pair("^Description[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION));
            this.symbols.add(new SymbolLineType_Pair("^Description (\\(.+\\))?[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION));
            this.symbols.add(new SymbolLineType_Pair("^Description of (fe)?male[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION));
            this.symbols.add(new SymbolLineType_Pair("^Supplementary (D|d)escription[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DESCRIPTION));
            
            // and data pattern
            // type species
            this.symbols.add(new SymbolLineType_Pair("^Type (S|s)pecies\\s?:\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_TYPESPECIES_AND_DATA));
            
            // KEY TO 
            this.symbols.add(new SymbolLineType_Pair("^(KEY|Key) (TO|To|to) .+[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_KEY_TO_FAMILY));
            
            // generic
            this.symbols.add(new SymbolLineType_Pair("^Diagnosis\\.\\s(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DIAGNOSIS_AND_DATA));
            this.symbols.add(new SymbolLineType_Pair("^Included (S|s)pecies\\s?:\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA));
            this.symbols.add(new SymbolLineType_Pair("^Etymology\\s?:\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA));
            this.symbols.add(new SymbolLineType_Pair("^Gender\\s?:\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA));
            this.symbols.add(new SymbolLineType_Pair("^Distribution\\s?:\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA));
            this.symbols.add(new SymbolLineType_Pair("^Distribution.\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA));
            this.symbols.add(new SymbolLineType_Pair("^Specimens (E|e)xamined\\s?:\\s?(.+)", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC_AND_DATA));
            // GENERIC
            this.symbols.add(new SymbolLineType_Pair("^Distribution[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Variation[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Material examined[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Material(s)? and Method(s)?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Preparation of Specimens$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Source of Materials$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Objectives$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Geographic Area$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Collecting Techniques$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Molecular results[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Taxonomic Method(s)?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Ecology$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Morphology$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Classification$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Specimens (E|e)xamined[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Floral association[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_GENERIC));
            // DISCUSSION GENERIC
            this.symbols.add(new SymbolLineType_Pair("^Comments[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC));
            this.symbols.add(new SymbolLineType_Pair("^Remarks[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_DISCUSSION_GENERIC));
            
            // ACK
            this.symbols.add(new SymbolLineType_Pair("^Acknowledgements[\\.]?$", TaxonomyLineCategorizeAlg.TaxonomyLineType.LINE_DIRECTION_TAXONOMY_ACKNOWLEDGEMENT));
        }
    }
}
