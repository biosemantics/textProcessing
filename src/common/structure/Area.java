/*
 * Bean that saves 2D area info
 */
package common.structure;

/**
 *
 * @author iychoi
 */
public class Area {
    private int x;
    private int w;
    private int y;
    private int h;
    
    public Area(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Area(Area area) {
        this.x = area.x;
        this.y = area.y;
        this.w = area.w;
        this.h = area.h;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getLeft() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getTop() {
        return this.y;
    }
    
    public int getW() {
        return this.w;
    }
    
    public int getRight() {
        return this.x + this.w;
    }
    
    public int getH() {
        return this.h;
    }
    
    public int getBottom() {
        return this.y + this.h;
    }
}
