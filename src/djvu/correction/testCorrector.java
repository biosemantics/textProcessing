/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djvu.correction;

import djvu.DjvuConfiguration;
import djvu.DjvuLine;
import djvu.DjvuPage;
import djvu.DjvuXMLReader;
import java.io.File;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class testCorrector {
    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.err.println("specify djvu xml file path and text file path");
            return;
        }
        
        File xmlFile = new File(args[0]);
        DjvuXMLReader reader = new DjvuXMLReader(xmlFile);
        
        List<DjvuPage> pages = reader.parse(new DjvuConfiguration());
        
        File textFile = new File(args[1]);
        SpaceCorrector corrector = new SpaceCorrector(textFile);
        
        for(DjvuPage page : pages) {
            System.out.println("page num : " + page.getPagenum());
            
            for(DjvuLine line : page.getLines()) {
                String correctText = corrector.correct(line.getText());
                
                if(!correctText.equals(line.getText())) {
                    System.out.println("Original ----");
                    System.out.println(line.getText());
                    System.out.println("Correct ----");
                    System.out.println(correctText);
                    System.out.println("------------");
                }
            }
        }
    }
}
