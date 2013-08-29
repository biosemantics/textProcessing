/*
 * Correct djvu line text 
 */
package input.djvu;

import input.correct.SpaceCorrector;
import java.io.File;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuLineTextCorrector {
    private SpaceCorrector spaceCorrector;
    
    public DjvuLineTextCorrector(SpaceCorrector spaceCorrector) {
        this.spaceCorrector = spaceCorrector;
    }
    
    public DjvuLineTextCorrector(File file) {
        this.spaceCorrector = new SpaceCorrector(file);
    }
    
    public void correct(DjvuLine line) {
        String correctText = this.spaceCorrector.correct(line.getText());
        line.setText(correctText);
    }
    
    public void correct(DjvuPage page) {
        List<DjvuLine> lines = page.getLines();
        if(lines != null) {
            for(DjvuLine line : lines) {
                correct(line);
            }
        }
    }
    
    public void correct(List<DjvuPage> pages) {
        for(DjvuPage page : pages) {
            correct(page);
        }
    }
}
