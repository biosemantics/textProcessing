/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.taxonomy;

/**
 *
 * @author iychoi
 */
public class RankRelation {
    private String parentRank;
    private String childRank;
    
    public RankRelation(String parentRank, String childRank) {
        this.parentRank = parentRank;
        this.childRank = childRank;
    }
    
    public String getParentRank() {
        return this.parentRank;
    }
    
    public String getChildRank() {
        return this.childRank;
    }
}
