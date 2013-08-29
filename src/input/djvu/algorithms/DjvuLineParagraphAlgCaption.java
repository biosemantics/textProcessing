/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu.algorithms;

import common.utils.RegExUtil;
import input.djvu.DjvuConfiguration;
import input.djvu.DjvuLine;
import input.djvu.DjvuParagraphAlg;

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
