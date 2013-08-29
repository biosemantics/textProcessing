/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu;

import java.io.File;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class testDjvuParse {
    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("specify djvu xml file path");
            return;
        }
        
        File file = new File(args[0]);
        DjvuXMLReader reader = new DjvuXMLReader(file);
        
        List<DjvuPage> pages = reader.parse(new DjvuConfiguration());
        
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