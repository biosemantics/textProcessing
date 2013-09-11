/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djvu;

import java.io.File;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class testDjvuParseRegion {
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            System.err.println("specify djvu xml file path and start, end offsets");
            return;
        }
        
        File file = new File(args[0]);
        DjvuXMLReader reader = new DjvuXMLReader(file);
        
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        
        DjvuConfiguration conf = new DjvuConfiguration();
        conf.setPageRegion(start, end);
        
        List<DjvuPage> pages = reader.parse(conf);
        
        for(DjvuPage page : pages) {
            System.out.println("page width : " + page.getWidth());
            System.out.println("page height : " + page.getHeight());
            System.out.println("page num : " + page.getPagenum());
            
            
            for(DjvuLine line : page.getLines()) {
                System.out.println("line : " + line.getText() + 
                        " (" + line.getLeft() + ", " + line.getRight() + ", " + 
                        line.getTop() + ", " + line.getBottom() + ")");
            }
        }
    }
}
