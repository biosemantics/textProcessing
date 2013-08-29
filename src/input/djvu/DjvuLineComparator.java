/*
 * djvu line comparator
 */
package input.djvu;

import java.util.Comparator;

/**
 *
 * @author iychoi
 */
public class DjvuLineComparator implements Comparator<DjvuLine> {

    public DjvuLineComparator() {
        
    }
    
    @Override
    public int compare(DjvuLine line1, DjvuLine line2) {
        int top_diff = line1.getTop() - line2.getTop();
        if (top_diff != 0) {
            return top_diff;
        } else {
            //same line
            return line1.getLeft() - line2.getLeft();
        }
    }
}
