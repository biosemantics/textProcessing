/*
 * Paragraph Extractor
 */
package djvu;

import common.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuParagraphExtractor {

    public DjvuParagraphExtractor() {
    }

    public List<DjvuLine> extractContentLines(DjvuPage page, DjvuLineOrderAlg alg, DjvuConfiguration conf) {
        List<DjvuLine> orderedLines = alg.orderLines(page, conf);

        return orderedLines;
    }

    public List<String> extractParagraphs(List<DjvuPage> pages, DjvuLineFilter filter, DjvuParagraphAlg paraAlg, DjvuLineOrderAlg orderAlg, DjvuConfiguration conf) {
        List<DjvuLineFilter> filters = new ArrayList<DjvuLineFilter>();

        filters.add(filter);
        
        List<DjvuParagraphAlg> algs = new ArrayList<DjvuParagraphAlg>();
        
        algs.add(paraAlg);
        
        return extractParagraphs(pages, filters, algs, orderAlg, conf);
    }
    
    public List<String> extractParagraphs(DjvuPage page, DjvuLineFilter filter, List<DjvuParagraphAlg> paraAlg, DjvuLineOrderAlg orderAlg, DjvuConfiguration conf) {
        List<DjvuLineFilter> filters = new ArrayList<DjvuLineFilter>();

        filters.add(filter);
        
        List<DjvuPage> pages = new ArrayList<DjvuPage>();
        
        pages.add(page);
        
        return extractParagraphs(pages, filters, paraAlg, orderAlg, conf);
    }
    
    public List<String> extractParagraphs(List<DjvuPage> pages, DjvuLineFilter filter, List<DjvuParagraphAlg> paraAlg, DjvuLineOrderAlg orderAlg, DjvuConfiguration conf) {
        List<DjvuLineFilter> filters = new ArrayList<DjvuLineFilter>();

        filters.add(filter);
        return extractParagraphs(pages, filters, paraAlg, orderAlg, conf);
    }

    public List<String> extractParagraphs(List<DjvuPage> pages, List<DjvuLineFilter> filters, List<DjvuParagraphAlg> paraAlg, DjvuLineOrderAlg orderAlg, DjvuConfiguration conf) {
        List<String> paragraphs = new ArrayList<String>();

        for (DjvuPage page : pages) {
            List<DjvuLine> orderedLines = orderAlg.orderLines(page, conf);
            List<DjvuLine> filteredLines = new ArrayList<DjvuLine>();

            if (filters != null) {
                for (DjvuLine line : orderedLines) {
                    boolean filtered = false;
                    for (DjvuLineFilter filter : filters) {
                        if (!filter.accept(line)) {
                            filtered = true;
                            break;
                        }
                    }

                    if (!filtered) {
                        filteredLines.add(line);
                    } else {
                        System.err.println("Line filtered");
                        System.err.println("Page : " + page.getPagenum());
                        System.err.println("Text : " + line.getText());
                    }
                }
            } else {
                filteredLines.addAll(orderedLines);
            }

            //now we got filtered lines
            if(paraAlg != null && paraAlg.size() > 0) {
                DjvuLine prevLine = null;
                String paragraph = "";
            
                for (DjvuLine line : filteredLines) {
                    boolean newPara = false;
                    for(DjvuParagraphAlg alg : paraAlg) {
                        if(alg.isNewParagraph(prevLine, line, conf)) {
                            newPara = true;
                            break;
                        }
                    }

                    if(newPara) {
                        // new para
                        if(!paragraph.trim().equals("")) {
                            paragraphs.add(paragraph);
                        }
                        paragraph = line.getText();
                    } else {
                        // add para
                        paragraph = StringUtil.mergeTwoStringLines(paragraph, line.getText());
                    }

                    prevLine = line;
                }

                if (!paragraph.equals("")) {
                    paragraphs.add(paragraph);
                }
            } else {
                for (DjvuLine line : filteredLines) {
                    paragraphs.add(line.getText().trim());
                }
            }
        }

        return paragraphs;
    }
}
