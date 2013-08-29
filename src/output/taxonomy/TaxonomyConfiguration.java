/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package output.taxonomy;

/**
 *
 * @author iychoi
 */
public class TaxonomyConfiguration {
    private int taxonomyNameWordLen = 10;
    private String rankDefault = "Genus";
    
    public TaxonomyConfiguration() {
        
    }
    
    public void setTaxonomyNameWordLen(int wordLen) {
        this.taxonomyNameWordLen = wordLen;
    }
    
    public int getTaxonomyNameWordLen() {
        return this.taxonomyNameWordLen;
    }

    public void setRankDefault(String rank) {
        this.rankDefault = rank;
    }
    
    public String getRankDefault() {
        return this.rankDefault;
    }
}
