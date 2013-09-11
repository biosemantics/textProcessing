/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djvu.algorithms;

import common.utils.RegExUtil;
import djvu.DjvuConfiguration;
import djvu.DjvuLine;
import djvu.DjvuParagraphAlg;

/**
 *
 * @author iychoi
 */
public class DjvuLineParagraphAlgCaption implements DjvuParagraphAlg {

    @Override
    public boolean isNewParagraph(DjvuLine prevLine, DjvuLine newLine, DjvuConfiguration conf) {
        if(RegExUtil.isFigTblCaption(newLine.getText().trim()))
            return true;
        return false;
    }
}
