/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paragraph;

import java.util.Comparator;
import paragraph.bean.RegEx;

/**
 *
 * @author iychoi
 */
public class RegExPriorityComparator implements Comparator<RegEx> {

    public RegExPriorityComparator() {
        
    }
    
    @Override
    public int compare(RegEx ex1, RegEx ex2) {
        int priority_diff = ex1.getPriority() - ex2.getPriority();
        if (priority_diff != 0) {
            return priority_diff;
        } else {
            //priority
            return ex1.getRegexID() - ex2.getRegexID();
        }
    }
    
}
