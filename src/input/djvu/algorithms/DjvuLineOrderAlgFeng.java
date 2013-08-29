/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu.algorithms;

import input.djvu.DjvuConfiguration;
import input.djvu.DjvuLine;
import input.djvu.DjvuLineComparator;
import input.djvu.DjvuLineOrderAlg;
import input.djvu.DjvuPage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuLineOrderAlgFeng implements DjvuLineOrderAlg {
    private void classifyLinesByPosition(DjvuPage page, List<DjvuLine> leftLines, List<DjvuLine> centerLines, List<DjvuLine> rightLines) {
        int pageCenter = page.getWidth() / 2;
        
        List<DjvuLine> lines = page.getLines();
        for(DjvuLine line : lines) {
            // check if the new line belongs to left side or right side or it
            // crosses the middle point of page
            int right = line.getRight();
            int left = line.getLeft();
            
            if(right < pageCenter) {
                leftLines.add(line);
            } else if(left > pageCenter) {
                rightLines.add(line);
            } else {
                centerLines.add(line);
            }
        }
    }

    @Override
    public List<DjvuLine> orderLines(DjvuPage page, DjvuConfiguration conf) {
        List<DjvuLine> orderedLines = new ArrayList<DjvuLine>();
        
        List<DjvuLine> leftLines = new ArrayList<DjvuLine>();
        List<DjvuLine> centerLines = new ArrayList<DjvuLine>();
        List<DjvuLine> rightLines = new ArrayList<DjvuLine>();
        
        classifyLinesByPosition(page, leftLines, centerLines, rightLines);
        
        // sort them by top
        if (leftLines.size() > 1) {
                Collections.sort(leftLines, new DjvuLineComparator());
        }
        if (centerLines.size() > 1) {
                Collections.sort(centerLines, new DjvuLineComparator());
        }
        if (rightLines.size() > 1) {
                Collections.sort(rightLines, new DjvuLineComparator());
        }
        
        // read from leftside, crossingmiddle, rightside
        /**
         * get middle one by one, get its top, then insert text in order of 1.
         * all left before top 2. all right before top 3. the middle one
         * 
         * After processing all middle text, get all left, then get all right
         */
        for (int i = 0; i < centerLines.size(); i++) {
            DjvuLine centerLine = centerLines.get(i);
            int bottom_center = centerLine.getBottom();

            // get all lines before bottom_center from left_side
            while (leftLines.size() > 0) {
                if (leftLines.get(0).getTop() < bottom_center) {
                    orderedLines.add(leftLines.remove(0));
                } else {
                    break;
                }
            }

            // get all lines before bottom_center from right_side
            while (rightLines.size() > 0) {
                if (rightLines.get(0).getTop() < bottom_center) {
                    orderedLines.add(rightLines.remove(0));
                } else {
                    break;
                }
            }

            // insert the middle line after getting all text above it
            orderedLines.add(centerLine);
        }
        
        // get the rest of lines from left_side
        while (leftLines.size() > 0) {
                orderedLines.add(leftLines.remove(0));
        }

        // get the rest of lines from right_side
        while (rightLines.size() > 0) {
                orderedLines.add(rightLines.remove(0));
        }
        
        leftLines.clear();
        rightLines.clear();
        centerLines.clear();
        
        return orderedLines;
    }
}
