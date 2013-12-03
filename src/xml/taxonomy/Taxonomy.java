/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.taxonomy;

import xml.taxonomy.beans.treatment.Treatment;

/**
 *
 * @author iychoi
 */
public class Taxonomy {
    private Treatment treatment;
    private String name;
    private int keyCount;
    
    public Taxonomy() {
        this.keyCount = 0;
    }
    
    public Taxonomy(Treatment treatment) {
        this.treatment = treatment;
        this.keyCount = 0;
    }
    
    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    
    public Treatment getTreatment() {
        return this.treatment;
    }
    
    public void setTaxonName(String name) {
        this.name = name;
    }
    
    public String getTaxonName() {
        return this.name;
    }

    public void increaseKeyToTable() {
        this.keyCount++;
    }

    public int getKeyToTable() {
        return this.keyCount;
    }
    
}
