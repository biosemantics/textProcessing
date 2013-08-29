/*
 * Bean that stores Page element
 */
package input.djvu;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuPage {
    private int width;
    private int height;
    private int pagenum;
    
    private List<DjvuLine> lines;
    
    public DjvuPage(int width, int height, int pagenum) {
        this.width = width;
        this.height = height;
        this.pagenum = pagenum;
        
        this.lines = new ArrayList<DjvuLine>();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getPagenum() {
        return this.pagenum;
    }
    
    public void addLine(DjvuLine line) {
        this.lines.add(line);
    }
    
    public List<DjvuLine> getLines() {
        return this.lines;
    }
}
