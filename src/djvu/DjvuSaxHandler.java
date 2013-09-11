/*
 * SAX handler for djvu xml 
 */
package djvu;

import common.structure.Area;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author iychoi
 */
public class DjvuSaxHandler extends DefaultHandler {

    private static final String DTD_ENTITY_PUBLIC_ID = "-//W3C//DTD DjVuXML 1.1//EN";
    
    private int marginLeft = 0;
    private int marginRight = 0;
    private int marginTop = 0;
    private int marginBottom = 0;
    
    private boolean inBody = false;
    private boolean inObject = false;
    private boolean inLine = false;
    private List<DjvuPage> pages;
    private int startPage = 0;
    private int endPage = Integer.MAX_VALUE;
    private boolean inTargetPage = false;
    // caching
    private DjvuPage lastpage;
    private DjvuLine lastline;
    
    private String text;

    public DjvuSaxHandler(DjvuConfiguration conf) {
        super();
        
        this.startPage = conf.getPageStart();
        this.endPage = conf.getPageEnd();
        
        this.marginLeft = conf.getMarginLeft();
        this.marginRight = conf.getMarginRight();
        this.marginTop = conf.getMarginTop();
        this.marginBottom = conf.getMarginBottom();
        
        this.pages = new ArrayList<DjvuPage>();
    }
    
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        if(publicId.equalsIgnoreCase(DTD_ENTITY_PUBLIC_ID)) {
            return new org.xml.sax.InputSource(new java.io.StringReader(""));
        } else {
            return super.resolveEntity(publicId, systemId);
        }
    }
        
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("BODY")) {
            this.inBody = true;
        } else if (this.inBody && qName.equalsIgnoreCase("OBJECT")) {
            this.inObject = true;

            String sheight = attributes.getValue("height");
            String swidth = attributes.getValue("width");
            String susemap = attributes.getValue("usemap");

            int height = 0;
            int width = 0;
            int pagenum = -1;

            if (sheight != null) {
                height = Integer.parseInt(sheight);
            }
            if (swidth != null) {
                width = Integer.parseInt(swidth);
            }
            if (susemap != null) {
                String pagenumStr = susemap.substring(1, susemap.indexOf(".djvu"));
                pagenum = Integer.parseInt(pagenumStr);
            }

            if (pagenum < this.startPage || pagenum > this.endPage) {
                this.inTargetPage = false;
            } else {
                this.inTargetPage = true;
            }

            if (this.inTargetPage) {
                DjvuPage page = new DjvuPage(width, height, pagenum);
                this.pages.add(page);
                this.lastpage = page;
            }
        } else if (this.inBody && this.inObject && qName.equalsIgnoreCase("LINE")) {
            this.inLine = true;

            if (this.inTargetPage) {
                String scoords = attributes.getValue("coords");
                if (scoords != null) {
                    String[] coords = scoords.split(",");
                    if (coords.length == 4) {
                        int left_coord = Integer.parseInt(coords[0]);
                        int bottom_coord = Integer.parseInt(coords[1]);
                        int right_coord = Integer.parseInt(coords[2]);
                        int top_coord = Integer.parseInt(coords[3]);

                        DjvuLine line = new DjvuLine(left_coord, bottom_coord, right_coord, top_coord);

                        if (this.lastpage != null) {
                            
                            Area contentArea = new Area(marginLeft, marginTop, 
                                this.lastpage.getWidth() - (marginLeft + marginRight),
                                this.lastpage.getHeight() - (marginTop + marginBottom));
                            
                            if (line.getLeft() >= contentArea.getLeft() && line.getTop() >= contentArea.getTop()
                                    && line.getRight() <= contentArea.getRight() && line.getBottom() <= contentArea.getBottom()) {
                                this.lastpage.addLine(line);
                            }
                        }

                        this.lastline = line;
                        //pages.get(pages.size()-1).addLine(line);
                        this.text = "";
                    }
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("BODY")) {
            this.inBody = false;
        } else if (this.inBody && qName.equalsIgnoreCase("OBJECT")) {
            this.inObject = false;
        } else if (this.inBody && this.inObject && qName.equalsIgnoreCase("LINE")) {
            if (this.lastline != null) {
                this.lastline.setText(this.text.trim());
            }
            
            this.inLine = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.inBody && this.inObject && this.inLine && this.inTargetPage) {
            String text = new String(ch, start, length);

            this.text += text;
        }
    }

    public List<DjvuPage> getPages() {
        return this.pages;
    }
}
