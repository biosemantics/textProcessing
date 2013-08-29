/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu.algorithms;

import common.structure.Area;
import input.djvu.DjvuConfiguration;
import input.djvu.DjvuLine;
import input.djvu.DjvuLineOrderAlg;
import input.djvu.DjvuPage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuLineOrderAlgAuto implements DjvuLineOrderAlg {
    private List<List<DjvuLine>> groupLines(DjvuPage page, int groupMaxThreshold) {
        List<Area> areas = new ArrayList<Area>();
        
        List<DjvuLine> lines = page.getLines();
        for(DjvuLine line : lines) {
            Area area = new Area(line.getLeft(), line.getTop(), line.getRight() - line.getLeft(), line.getBottom() - line.getTop());
            areas.add(area);
        }
        
        int borderIncrease = 1;
        List<Area> groupedArea = groupArea(areas, borderIncrease);
        
        while(groupedArea.size() > groupMaxThreshold) {
            borderIncrease++;
            groupedArea = groupArea(groupedArea, borderIncrease);
        }
        
        if (groupedArea.size() > 1) {
            Collections.sort(groupedArea, new AreaComparator());
        }
        
        // sort groupedArea
        // check neighbor
        List<Area> newGroupedArea = new ArrayList<Area>();
        for(int i=0;i<groupedArea.size();i++) {
            boolean findNeighbor = false;
            boolean inLeft = true;
            for(int j=0;j<groupedArea.size();j++) {
                if(i != j) {
                    if(groupedArea.get(i).getBottom() < groupedArea.get(j).getTop() ||
                            groupedArea.get(i).getTop() > groupedArea.get(j).getBottom()) {
                        // i and j is not a neighbor
                    } else {
                        // neighbor
                        findNeighbor = true;
                        if(groupedArea.get(i).getLeft() > groupedArea.get(j).getLeft()) {
                            inLeft = false;
                        } else {
                            inLeft = true;
                        }
                        break;
                    }
                }
            }
            
            if(findNeighbor) {
                if(inLeft) {
                    newGroupedArea.add(groupedArea.get(i));
                }
            } else {
                newGroupedArea.add(groupedArea.get(i));
            }
        }
        
        for(Area area : groupedArea) {
            if(!newGroupedArea.contains(area)) {
                newGroupedArea.add(area);
            }
        }
        
        List<List<DjvuLine>> groups = new ArrayList<List<DjvuLine>>();
        // alloc empty arrayList
        for(int i=0;i<newGroupedArea.size();i++) {
            groups.add(new ArrayList<DjvuLine>());
        }
        
        // fill array
        for(DjvuLine line : lines) {
            for(int j=0;j<newGroupedArea.size();j++) {
                Area groupArea = newGroupedArea.get(j);
                if(groupArea.getLeft() <= line.getLeft() && groupArea.getTop() <= line.getTop()
                        && groupArea.getRight() >= line.getRight() && groupArea.getBottom() >= line.getBottom()) {
                    // in
                    // this line is contained to this group
                    groups.get(j).add(line);
                    break;
                }
            }
        }
        
        return groups;
    }
    
    private List<Area> groupArea(List<Area> areas, int borderIncrease) {
        List<Area> targetArea = new ArrayList<Area>();
        List<Area> groupedArea = new ArrayList<Area>();
        
        for(Area area : areas) {
            // copy
            targetArea.add(new Area(area));
        }
        
        boolean finished = false;
        while(!finished) {
            boolean merged = false;
            
            for(int i=0;i<targetArea.size();i++) {
                for(int j=0;j<targetArea.size();j++) {
                    if(i != j) {
                        // compare border
                        Area areai = targetArea.get(i);
                        Area areaj = targetArea.get(j);
                        
                        // box check
                        boolean bOverlap = false;
                        int x = 0;
                        int y = 0;
                        
                        x = areaj.getLeft();
                        y = areaj.getTop();
                        if((areai.getLeft()) <= x &&
                                (areai.getRight()) >= x &&
                                (areai.getTop() - borderIncrease) <= y &&
                                (areai.getBottom() + borderIncrease) >= y) {
                            bOverlap = true;
                        }
                        
                        x = areaj.getRight();
                        y = areaj.getTop();
                        if((areai.getLeft()) <= x &&
                                (areai.getRight()) >= x &&
                                (areai.getTop() - borderIncrease) <= y &&
                                (areai.getBottom() + borderIncrease) >= y) {
                            bOverlap = true;
                        }
                        
                        x = areaj.getLeft();
                        y = areaj.getBottom();
                        if((areai.getLeft()) <= x &&
                                (areai.getRight()) >= x &&
                                (areai.getTop() - borderIncrease) <= y &&
                                (areai.getBottom() + borderIncrease) >= y) {
                            bOverlap = true;
                        }
                        
                        x = areaj.getRight();
                        y = areaj.getBottom();
                        if((areai.getLeft()) <= x &&
                                (areai.getRight()) >= x &&
                                (areai.getTop() - borderIncrease) <= y &&
                                (areai.getBottom() + borderIncrease) >= y) {
                            bOverlap = true;
                        }
                        
                        if(bOverlap) {
                            int mergedLeft = Math.min(areai.getLeft(), areaj.getLeft());
                            int mergedRight = Math.max(areai.getRight(), areaj.getRight());
                            int mergedTop = Math.min(areai.getTop(), areaj.getTop());
                            int mergedBottom = Math.max(areai.getBottom(), areaj.getBottom());
                            Area mergedArea = new Area(mergedLeft, mergedTop, mergedRight - mergedLeft, mergedBottom - mergedTop);
                            groupedArea.add(mergedArea);
                            targetArea.remove(Math.max(i, j));
                            targetArea.remove(Math.min(i, j));
                            merged = true;
                        }
                    } 
                    
                    if(merged)
                        break;
                }
                
                if(merged)
                    break;
            }
            
            if(!merged) {
                finished = true;
            } else {
                targetArea.addAll(groupedArea);
                groupedArea.clear();
            }
        }
        
        groupedArea.addAll(targetArea);
        
        return groupedArea;
    }

    @Override
    public List<DjvuLine> orderLines(DjvuPage page, DjvuConfiguration conf) {
        List<DjvuLine> orderedLines = new ArrayList<DjvuLine>();
        List<List<DjvuLine>> groupedLines = groupLines(page, conf.getBlockThreshold());
        
        for(List<DjvuLine> group : groupedLines) {
            orderedLines.addAll(group);
        }
        
        return orderedLines;
    }
}
