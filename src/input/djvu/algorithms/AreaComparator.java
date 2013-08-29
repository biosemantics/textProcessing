/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu.algorithms;

import common.structure.Area;
import java.util.Comparator;

/**
 *
 * @author iychoi
 */
public class AreaComparator implements Comparator<Area> {

    public AreaComparator() {
        
    }
    
    @Override
    public int compare(Area a1, Area a2) {
        int top_diff = a1.getTop() - a2.getTop();
        if (top_diff != 0) {
            return top_diff;
        } else {
            //same line
            return a1.getLeft() - a2.getLeft();
        }
    }
    
}
