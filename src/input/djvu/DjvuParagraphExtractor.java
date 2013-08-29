/*
 * Paragraph Extractor
 */
package input.djvu;

import common.utils.StringUtil;
import input.djvu.algorithms.DjvuLineOrderAlgAuto;
import input.djvu.algorithms.DjvuLineOrderAlgFeng;
import input.djvu.algorithms.DjvuLineParagraphAlgIndent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class DjvuParagraphExtractor {

    List<DjvuLineOrderAlg> orderAlg;

    public DjvuParagraphExtractor() {
        initAlgs();
    }

    private void initAlgs() {
        this.orderAlg = new ArrayList<DjvuLineOrderAlg>();
        this.orderAlg.add(new DjvuLineOrderAlgAuto());
        this.orderAlg.add(new DjvuLineOrderAlgFeng());
    }

    private List<DjvuLine> orderLines(DjvuPage page, DjvuConfiguration conf) {
        List<DjvuLine> prev = null;

        for (DjvuLineOrderAlg alg : this.orderAlg) {
            List<DjvuLine> now = alg.orderLines(page, conf);

            if (prev == null) {
                prev = now;
            } else {
                // compare
                if (prev.size() != now.size()) {
                    System.err.println("Careful! Line Order is not matching between algorithms in size : page " + page.getPagenum());
                    break;
                } else {
                    for (int i = 0; i < prev.size(); i++) {
                        if (!prev.get(i).getText().trim().equals(now.get(i).getText().trim())) {
                            System.err.println("Careful! Line Order is not matching between algorithms : page " + page.getPagenum());
                            break;
                        }
                    }
                }
            }
        }

        return prev;
    }

    public List<DjvuLine> extractContentLines(DjvuPage page, DjvuConfiguration conf) {
        List<DjvuLine> orderedLines = orderLines(page, conf);

        return orderedLines;
    }

    public List<String> extractParagraphs(List<DjvuPage> pages, DjvuLineFilter filter, DjvuParagraphAlg paraAlg, DjvuConfiguration conf) {
        List<DjvuLineFilter> filters = new ArrayList<DjvuLineFilter>();

        filters.add(filter);
        
        List<DjvuParagraphAlg> algs = new ArrayList<DjvuParagraphAlg>();
        
        algs.add(paraAlg);
        
        return extractParagraphs(pages, filters, algs, conf);
    }
    
    public List<String> extractParagraphs(DjvuPage page, DjvuLineFilter filter, List<DjvuParagraphAlg> paraAlg, DjvuConfiguration conf) {
        List<DjvuLineFilter> filters = new ArrayList<DjvuLineFilter>();

        filters.add(filter);
        
        List<DjvuPage> pages = new ArrayList<DjvuPage>();
        
        pages.add(page);
        
        return extractParagraphs(pages, filters, paraAlg, conf);
    }
    
    public List<String> extractParagraphs(List<DjvuPage> pages, DjvuLineFilter filter, List<DjvuParagraphAlg> paraAlg, DjvuConfiguration conf) {
        List<DjvuLineFilter> filters = new ArrayList<DjvuLineFilter>();

        filters.add(filter);
        return extractParagraphs(pages, filters, paraAlg, conf);
    }

    public List<String> extractParagraphs(List<DjvuPage> pages, List<DjvuLineFilter> filters, List<DjvuParagraphAlg> paraAlg, DjvuConfiguration conf) {
        List<String> paragraphs = new ArrayList<String>();

        for (DjvuPage page : pages) {
            List<DjvuLine> orderedLines = orderLines(page, conf);
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
