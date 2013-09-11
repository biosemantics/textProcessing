/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djvu.algorithms;

import djvu.DjvuConfiguration;
import djvu.DjvuLine;
import djvu.DjvuParagraphAlg;

/**
 *
 * @author iychoi
 */
public class DjvuLineParagraphAlgGap implements DjvuParagraphAlg {

    private boolean isDistant(DjvuLine prevLine, DjvuLine newLine, DjvuConfiguration conf) {
        if(newLine.getTop() >= prevLine.getBottom() + conf.getParagraphVGapMinThreshold()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isNewParagraph(DjvuLine prevLine, DjvuLine newLine, DjvuConfiguration conf) {
        if(prevLine == null)
            return true;
        
        if(isDistant(prevLine, newLine, conf)) {
            return true;
        }
        
        return false;
    }
}
