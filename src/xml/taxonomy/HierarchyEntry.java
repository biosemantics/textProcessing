/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.taxonomy;

/**
 *
 * @author iychoi
 */
public class HierarchyEntry {
    private String[] names;
    private String[] ranks;
    private String[] authorities;
    
    public HierarchyEntry(String[] names, String[] ranks, String[] authorities) {
        this.names = names;
        this.ranks = ranks;
        this.authorities = authorities;
    }
    
    public String[] getNames() {
        return this.names;
    }
    
    public String[] getRanks() {
        return this.ranks;
    }
    
    public String[] getAuthorities() {
        return this.authorities;
    }
    
    public String toString() {
        String newHierarchy = "";
        for(int i=0;i<names.length;i++) {
            if (!newHierarchy.equals("")) {
                newHierarchy += "; ";
            }
            newHierarchy += ranks[i] + " " + names[i];
        }
        return newHierarchy;
    }
    
    public String getHierarchyString() {
        String newHierarchy = "";
        for(int i=0;i<names.length;i++) {
            if (!newHierarchy.equals("")) {
                newHierarchy += "; ";
            }
            newHierarchy += ranks[i] + " " + names[i];
        }
        return newHierarchy;
    }
}
