/*
 * Bean that stores Line element
 */
package djvu;

/**
 *
 * @author iychoi
 */
public class DjvuLine {
    private int left;
    private int bottom;
    private int right;
    private int top;
    
    private String text;
    
    public DjvuLine(int left, int bottom, int right, int top, String text) {
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
        this.text = text;
    }
    
    public DjvuLine(int left, int bottom, int right, int top) {
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
    }
    
    public int getLeft() {
        return this.left;
    }
    
    public int getBottom() {
        return this.bottom;
    }
    
    public int getRight() {
        return this.right;
    }
    
    public int getTop() {
        return this.top;
    }
    
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
