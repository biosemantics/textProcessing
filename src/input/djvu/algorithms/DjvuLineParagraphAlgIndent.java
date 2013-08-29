/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu.algorithms;

import input.djvu.DjvuConfiguration;
import input.djvu.DjvuLine;
import input.djvu.DjvuParagraphAlg;

/**
 *
 * @author iychoi
 */
public class DjvuLineParagraphAlgIndent implements DjvuParagraphAlg {
    private boolean isIndented(DjvuLine prevLine, DjvuLine newLine, DjvuConfiguration conf) {
        if(newLine.getLeft() >= prevLine.getLeft() + conf.getIndentMinThreshold()
                && newLine.getLeft() <= prevLine.getLeft() + conf.getIndentMaxThreshold()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isNewParagraph(DjvuLine prevLine, DjvuLine newLine, DjvuConfiguration conf) {
        if(prevLine == null)
            return true;
        
        if(isIndented(prevLine, newLine, conf)) {
            return true;
        }
        
        return false;
    }
}
