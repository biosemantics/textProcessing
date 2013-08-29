/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package input.djvu;

import input.correct.SpaceCorrector;
import java.io.File;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class testDjvuParseCorrect {
    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.err.println("specify djvu xml file path and text file path");
            return;
        }
        
        File xmlFile = new File(args[0]);
        DjvuXMLReader reader = new DjvuXMLReader(xmlFile);
        
        List<DjvuPage> pages = reader.parse(new DjvuConfiguration());
        
        File textFile = new File(args[1]);
        DjvuLineTextCorrector corrector = new DjvuLineTextCorrector(textFile);
        corrector.correct(pages);
        
        for(DjvuPage page : pages) {
            System.out.println("page num : " + page.getPagenum());
            
            for(DjvuLine line : page.getLines()) {
                System.out.println("line : " + line.getText() + 
                        " (" + line.getLeft() + ", " + line.getRight() + ", " + 
                        line.getTop() + ", " + line.getBottom() + ")");
            }
        }
    }
}
