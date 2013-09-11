/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djvu;

import common.utils.RegExUtil;
import djvu.algorithms.DjvuLineParagraphAlgCaption;
import djvu.algorithms.DjvuLineParagraphAlgGap;
import djvu.algorithms.DjvuLineParagraphAlgIndent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class testDjvuParagraphExtractor {
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            System.err.println("specify djvu xml file path and start, end offsets");
            return;
        }
        
        File file = new File(args[0]);
        DjvuXMLReader reader = new DjvuXMLReader(file);
        
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        
        int topMargin = 200;
        int sideMargin = 100;
        int bottomMargin = 200;
        
        DjvuConfiguration conf = new DjvuConfiguration();
        conf.setPageRegion(start, end);
        conf.setMarginLeft(sideMargin);
        conf.setMarginRight(sideMargin);
        conf.setMarginTop(topMargin);
        conf.setMarginBottom(bottomMargin);
        
        List<DjvuPage> pages = reader.parse(conf);
        
        DjvuParagraphExtractor paragraphExtractor = new DjvuParagraphExtractor();
        DjvuLineFilter filter = new DjvuLineFilter(){

            @Override
            public boolean accept(DjvuLine line) {
                if(line.getText().trim().equals(""))
                    return false;
                
                return !(RegExUtil.isOneCharOrNum(line.getText()));
            }
        };
        
        List<DjvuParagraphAlg> paraAlgs = new ArrayList<DjvuParagraphAlg>();
        paraAlgs.add(new DjvuLineParagraphAlgIndent());
        paraAlgs.add(new DjvuLineParagraphAlgGap());
        paraAlgs.add(new DjvuLineParagraphAlgCaption());
        
        List<String> paragraphs = paragraphExtractor.extractParagraphs(pages, filter, paraAlgs, conf);
        
        for(String paragraph : paragraphs) {
            System.out.println(paragraph);
            System.out.println("");
        }
        
        /*
        for(DjvuPage page : pages) {
            System.out.println("page num : " + page.getPagenum());
            
            DjvuParagraphExtractor paragraphExtractor = new DjvuParagraphExtractor();
            List<DjvuLine> contentLines = paragraphExtractor.extractContentLines(page, conf);
            
            for(DjvuLine line : contentLines) {
                System.out.println(line.getText());
            }
        }
        */
    }
}
