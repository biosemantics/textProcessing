/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djvu.algorithms;

import djvu.DjvuConfiguration;
import djvu.DjvuLine;
import djvu.DjvuLineComparator;
import djvu.DjvuLineOrderAlg;
import djvu.DjvuPage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuLineOrderAlg2Col implements DjvuLineOrderAlg {
    private void classifyLinesByPosition(DjvuPage page, List<DjvuLine> leftLines, List<DjvuLine> rightLines) {
        int pageCenter = page.getWidth() / 2;
        int threshold = page.getWidth() / 8;
        
        List<DjvuLine> lines = page.getLines();
        for(DjvuLine line : lines) {
            // check if the new line belongs to left side or right side or it
            int right = line.getRight();
            int left = line.getLeft();
            
            if(left < pageCenter) {
                leftLines.add(line);
            } else if(right < pageCenter + threshold) {
                leftLines.add(line);
            } else {
                rightLines.add(line);
            }
        }
    }

    @Override
    public List<DjvuLine> orderLines(DjvuPage page, DjvuConfiguration conf) {
        List<DjvuLine> orderedLines = new ArrayList<DjvuLine>();
        
        List<DjvuLine> leftLines = new ArrayList<DjvuLine>();
        List<DjvuLine> rightLines = new ArrayList<DjvuLine>();
        
        classifyLinesByPosition(page, leftLines, rightLines);
        
        // sort them by top
        if (leftLines.size() > 1) {
                Collections.sort(leftLines, new DjvuLineComparator());
        }
        if (rightLines.size() > 1) {
                Collections.sort(rightLines, new DjvuLineComparator());
        }
        
        orderedLines.addAll(leftLines);
        orderedLines.addAll(rightLines);
        
        leftLines.clear();
        rightLines.clear();
        return orderedLines;
    }
}
