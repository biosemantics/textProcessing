/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu;

/**
 *
 * @author iychoi
 */
public interface DjvuParagraphAlg {
    public boolean isNewParagraph(DjvuLine prevLine, DjvuLine newLine, DjvuConfiguration conf);
}
