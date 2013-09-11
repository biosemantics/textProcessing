/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package taxonomy;

/**
 *
 * @author iychoi
 */
public class TaxonomyConfiguration {
    private int taxonomyNameWordLen = 10;
    private int taxonomySubTitleWordLen = 5;
    private int taxonomyDescriptionSubTitleWordLen = 3;
    private String rankDefault = "Genus";
    private boolean unknownAsDiscussion = false;
    
    public TaxonomyConfiguration() {
        
    }
    
    public void setTaxonomyNameWordLen(int wordLen) {
        this.taxonomyNameWordLen = wordLen;
    }
    
    public int getTaxonomyNameWordLen() {
        return this.taxonomyNameWordLen;
    }
    
    public void setTaxonomySubTitleWordLen(int wordLen) {
        this.taxonomySubTitleWordLen = wordLen;
    }
    
    public int getTaxonomySubTitleWordLen() {
        return this.taxonomySubTitleWordLen;
    }
    
    public void setTaxonomyDescriptionSubTitleWordLen(int wordLen) {
        this.taxonomyDescriptionSubTitleWordLen = wordLen;
    }
    public int getTaxonomyDescriptionSubTitleWordLen() {
        return this.taxonomyDescriptionSubTitleWordLen;
    }

    public void setRankDefault(String rank) {
        this.rankDefault = rank;
    }
    
    public String getRankDefault() {
        return this.rankDefault;
    }

    public void setUnknownAsDiscussion(boolean unknownAsDiscussion) {
        this.unknownAsDiscussion = unknownAsDiscussion;
    }
    
    public boolean getUnknownAsDiscussion() {
        return this.unknownAsDiscussion;
    }
}
