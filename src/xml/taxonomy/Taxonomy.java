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
    
    public Taxonomy() {
    }
    
    public Taxonomy(Treatment treatment) {
        this.treatment = treatment;
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
    
}
