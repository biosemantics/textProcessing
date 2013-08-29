/*
 * Configuration for djvu document
 */
package input.djvu;

import common.structure.Area;

/**
 *
 * @author iychoi
 */
public class DjvuConfiguration {
    // conf for page region
    private int startPage = 0;
    private int endPage = Integer.MAX_VALUE;
    
    // conf for paragraph extract
    private int thresholdMaxBlocks = 15;
    
    // conf for setting margin area
    private int marginLeft = 0;
    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;
    
    private int thresholdMinIndent = 10;
    private int thresholdMaxIndent = 100;
    private int thresholdMinVGap = 20;
    
    public DjvuConfiguration() {
        
    }
    
    public void setPageStart(int start) {
        this.startPage = start;
    }
    
    public int getPageStart() {
        return this.startPage;
    }
    
    public void setPageEnd(int end) {
        this.endPage = end;
    }
    
    public int getPageEnd() {
        return this.endPage;
    }
    
    public void setPageRegion(int start, int end) {
        this.startPage = start;
        this.endPage = end;
    }
    
    public void setBlockThreshold(int thresholdMaxBlocks) {
        this.thresholdMaxBlocks = thresholdMaxBlocks;
    }
    
    public int getBlockThreshold() {
        return this.thresholdMaxBlocks;
    }
    
    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }
    
    public int getMarginLeft() {
        return this.marginLeft;
    }
    
    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }
    
    public int getMarginRight() {
        return this.marginRight;
    }
    
    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }
    
    public int getMarginTop() {
        return this.marginTop;
    }
    
    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }
    
    public int getMarginBottom() {
        return this.marginBottom;
    }
    
    public void setMargin(int left, int right, int top, int bottom) {
        this.marginLeft = left;
        this.marginRight = right;
        this.marginTop = top;
        this.marginBottom = bottom;
    }
    
    public void setMargin(Area area) {
        this.marginLeft = area.getLeft();
        this.marginRight = area.getRight();
        this.marginTop = area.getTop();
        this.marginBottom = area.getBottom();
    }
    
    public Area getContentArea(Area documentArea) {
        Area area = new Area(documentArea.getLeft() + this.marginLeft, 
                documentArea.getTop() + this.marginTop,
                documentArea.getW() - this.marginLeft - this.marginRight,
                documentArea.getH() - this.marginTop - this.marginBottom);
        
        return area;
    }

    public void setIndentMinThreshold(int thresholdMinIndent) {
        this.thresholdMinIndent = thresholdMinIndent;
    }
    
    public int getIndentMinThreshold() {
        return this.thresholdMinIndent;
    }
    
    public void setIndentMaxThreshold(int thresholdMaxIndent) {
        this.thresholdMaxIndent = thresholdMaxIndent;
    }

    public int getIndentMaxThreshold() {
        return this.thresholdMaxIndent;
    }

    public void setParagraphVGapMinThreshold(int thresholdMinVGap) {
        this.thresholdMinVGap = thresholdMinVGap;
    }
    
    public int getParagraphVGapMinThreshold() {
        return this.thresholdMinVGap;
    }
}
